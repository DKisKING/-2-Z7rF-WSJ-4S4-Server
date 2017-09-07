package osv.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import osv.Config;
import osv.Server;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.RightGroup;

public class DonationQuery extends Thread {

	/**
	 * The connection with the database.
	 */
	private Connection con;

	/**
	 * The Java statement.
	 */
	private Statement stmt;

	/**
	 * The query which is to be executed after initialization.
	 */
	private String query;

	/**
	 * The player this is created for
	 */
	private Player player;

	private ResultSet result;

	private PreparedStatement ps;

	/**
	 * Creates a new donation query. In doing so we check for the players name in the list to see if they obtained their item.
	 * 
	 * @param player the player
	 */
	public DonationQuery(Player player) {
		this.player = player;
		this.run();
	}

	/**
	 * Executes the query on a new thread.
	 */
	@Override
	public void run() {
		if (!player.getMode().isDonatingPermitted()) {
			player.sendMessage("Your game mode does not permit you to accept donations.");
			return;
		}
		query = "SELECT * FROM donations";
		makeConnection();
		result = executeQuery(query);
		int identificationValue = -1;
		try {
			while (result.next()) {
				String name = result.getString("name");
				if (name.equalsIgnoreCase(player.playerName)) {
					int claimed = result.getInt("claimed");
					if (claimed != 0) {
						continue;
					}
					String status = result.getString("status");
					if (!status.equalsIgnoreCase("completed")) {
						continue;
					}
					String reward = result.getString("item");
					double price = result.getDouble("amount");
					Optional<DonatablePackage> dpOp = DonatablePackage.forName(reward);
					if (dpOp.isPresent()) {
						DonatablePackage dp = dpOp.get();

						if (player.getRights().getPrimary() == Right.IRONMAN) {
							switch (dp.packageName) {
							case "1x contributor scroll":
							case "1x sponsor scroll":
							case "1x supporter scroll":
							case "1x VIP scroll":
								player.sendMessage("Your gamemode does not allow you to accept this donation.");
								return;
							}
						}
						if (dp.price == price) {
							dp.reward.append(player);
							if (dp.increasesTotal) {
								int newPrice = (int) (price + price / 4);
								if (Config.CYBER_MONDAY) {
									player.amDonated += newPrice;
								} else {
									player.amDonated += price;
								}
								if (player.getRights().getPrimary() == Right.IRONMAN) {
									upgradeIronmanRank();
								} else {
									upgradeRank();
								}
							}

							/**
							 * Halloween Exclusive Pet
							 */
//							if (Misc.random(20) == 2 && player.getItems().getItemCount(12840, true) == 0 && player.summonId != 12840) {
//								PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + player.playerName + "</col> hit the jackpot and got a <col=CC0000>Death Jr</col> pet!");
//								player.getItems().addItemUnderAnyCircumstance(12840, 1);
//							}
							PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.playerName + "</col> donated $" + Double.toString(price)
									+ " and received <col=CC0000>" + dp.packageName + "</col>.");
							identificationValue = result.getInt("id");
						}
						break;
					}
				}
			}
			if (identificationValue != -1) {
				ps = con.prepareStatement("UPDATE donations SET claimed = '1' WHERE id = ? ");
				ps.setInt(1, identificationValue);
				ps.executeUpdate();
			} else {
				player.sendMessage("You don't have any available donations to claim.");
			}
			terminateConnection();
		} catch (SQLException e) {
			terminateConnection();
			e.printStackTrace();
		}
	}
	
	private void upgradeIronmanRank() {
//		if (player.getRights().getPrimary() == Right.IRONMAN && !player.getRights().getPrimary().isOrInherits(Right.CONTRIBUTOR)) {
//			player.getRights().add(Right.CONTRIBUTOR);
//			player.sendMessage("Congratulations, your rank has been upgraded to Contributor!");
//			player.sendMessage("This rank is hidden, but you will have all it's perks.");
//		} else {
//			return;
//		}
		
		ArrayList<RankUpgrade> orderedList = new ArrayList<>(Arrays.asList(RankUpgrade.values()));
		orderedList.sort((one, two) -> Integer.compare(two.amount, one.amount));
		orderedList.stream().filter(r -> player.amDonated >= r.amount).findFirst().ifPresent(rank -> {
			RightGroup rights = player.getRights();
			Right right = rank.rights;
			if (!rights.contains(right)) {
				player.sendMessage("Congratulations, your rank has been upgraded to " + right.toString() + ".");
				player.sendMessage("This rank is hidden, but you will have all it's perks.");
				rights.add(right);
			}
		});
	}

	private void upgradeRank() {
		ArrayList<RankUpgrade> orderedList = new ArrayList<>(Arrays.asList(RankUpgrade.values()));
		orderedList.sort((one, two) -> Integer.compare(two.amount, one.amount));
		orderedList.stream().filter(r -> player.amDonated >= r.amount).findFirst().ifPresent(rank -> {
			RightGroup rights = player.getRights();
			Right right = rank.rights;
			if (!rights.contains(right)) {
				player.sendMessage("Congratulations, your rank has been upgraded to " + right.toString() + ".");
				if (rights.getPrimary().isOrInherits(Right.CONTRIBUTOR) || rights.getPrimary().isOrInherits(Right.PLAYER)) {
					rights.setPrimary(right);
				} else {
					player.sendMessage("You now have the ability to change to this rank if you desire.");
					rights.add(right);
				}
			}
		});
	}

	/**
	 * Creates a connection with the database.
	 */
	private void makeConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = Server.getPunishConnections().getConnection();
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute the specified query and return the result if the query selects a portion of the database.
	 * 
	 * @param query The query which is to be executed.
	 * @return The return set of the query, if any.
	 */
	public ResultSet executeQuery(String query) {
		try {
			if (query.toLowerCase().startsWith("select")) {
				return stmt.executeQuery(query);
			}
			stmt.executeUpdate(query);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Terminates the existing connection with the database if existent.
	 */
	private void terminateConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt = null;
		}
	}

	private enum RankUpgrade {
		CONTRIBUTOR(Right.CONTRIBUTOR, 10), 
		SPONSOR(Right.SPONSOR, 30), 
		SUPPORTER(Right.SUPPORTER, 75), 
		VIP(Right.VIP, 150), 
		SUPER_VIP(Right.SUPER_VIP, 300), 
		MEGA_VIP(Right.MEGA_VIP, 500), 
		LEGENDARY(Right.LEGENDARY, 1000);

		/**
		 * The rights that will be appended if upgraded
		 */
		private final Right rights;

		/**
		 * The amount required for the upgrade
		 */
		private final int amount;

		private RankUpgrade(Right rights, int amount) {
			this.rights = rights;
			this.amount = amount;
		}
	}
	
	private enum DonatablePackage {
		DONATOR_POINTS_1("20x donator points", 10.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 20;
				player.sendMessage("You have received 20 donator points for donating, thank you!");
			}

		}), DONATOR_POINTS_2("45x donator points", 20.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 45;
				player.sendMessage("You have received 45 donator points for donating, thank you!");
			}

		}), DONATOR_POINTS_3("70x donator points", 30.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 70;
				player.sendMessage("You have received 70 donator points for donating, thank you!");
			}

		}), DONATOR_POINTS_4("125x donator points", 50.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 125;
				player.sendMessage("You have received 125 donator points for donating, thank you!");
			}

		}), DONATOR_POINTS_5("200x donator points", 75.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 200;
				player.sendMessage("You have received 200 donator points for donating, thank you!");
			}

		}), DONATOR_POINTS_6("275x donator points", 100.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 275;
				player.sendMessage("You have received 275 donator points for donating, thank you!");
			}

		}), DONATOR_POINTS_7("430x donator points", 150.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 430;
				player.sendMessage("You have received 430 donator points for donating, thank you!");
			}

		}), DONATOR_POINTS_8("750x donator points", 250.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 750;
				player.sendMessage("You have received 750 donator points for donating, thank you!");
			}

		}), MYSTER_BOX_1("1x mystery box", 10.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6199, 1);
				player.sendMessage("You have received 1x mystery box for donating, thank you!");
			}

		}), MYSTER_BOX_3("3x mystery box", 25.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6199, 3);
				player.sendMessage("You have received 3x mystery box for donating, thank you!");
			}

		}), MYSTER_BOX_5("5x mystery box", 40.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6199, 5);
				player.sendMessage("You have received 5x mystery box for donating, thank you!");
			}

		}), GAMBLER_SCROLL("1x gambler scroll", 40.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2701, 1);
				player.sendMessage("You have received 1x gambler scroll for donating, thank you!");
			}

		}), CONTRIBUTION_SCROLL("1x contributor scroll", 10.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2697, 1);
				player.sendMessage("You have received 1x contributor scroll for donating, thank you!");
			}

		}), SPONSOR_SCROLL("1x sponsor scroll", 25.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2698, 1);
				player.sendMessage("You have received 1x sponsor scroll for donating, thank you!");
			}

		}), SUPPORTER_SCROLL("1x supporter scroll", 65.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2699, 1);
				player.sendMessage("You have received 1x sponsor scroll for donating, thank you!");
			}

		}), VIP_SCROLL("1x VIP scroll", 130.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2700, 1);
				player.sendMessage("You have received 1x VIP scroll for donating, thank you!");
			}

		}), COW_OUTFIT("1x cow outfit", 15.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(11919, 1);
				player.getItems().addItemToBank(12956, 1);
				player.getItems().addItemToBank(12957, 1);
				player.getItems().addItemToBank(12958, 1);
				player.getItems().addItemToBank(12959, 1);
				player.sendMessage("You have received 1x cow outfit for donating, thank you!");
			}

		}), SANTA_OUTFIT("1x santa outfit", 20.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(12887, 1);
				player.getItems().addItemToBank(12888, 1);
				player.getItems().addItemToBank(12889, 1);
				player.getItems().addItemToBank(12890, 1);
				player.getItems().addItemToBank(12891, 1);
				player.sendMessage("You have received 1x santa outfit for donating, thank you!");
			}
			
		}), ANTISANTA_OUTFIT("1x anti-santa outfit", 20.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(12892, 1);
				player.getItems().addItemToBank(12893, 1);
				player.getItems().addItemToBank(12894, 1);
				player.getItems().addItemToBank(12895, 1);
				player.getItems().addItemToBank(12896, 1);
				player.sendMessage("You have received 1x anti-santa outfit for donating, thank you!");
			}
			
		}), PRESENT_1("1x present", 10.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6542, 1);
				player.sendMessage("You have received 1x present for donating, thank you!");
			}
			
		}), PRESENT_3("3x present", 25.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6542, 3);
				player.sendMessage("You have received 3x present for donating, thank you!");
			}
			
		}), PRESENT_5("5x present", 40.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6542, 5);
				player.sendMessage("You have received 5x present for donating, thank you!");
			}

		});

		/**
		 * The name of the package
		 */
		private final String packageName;

		/**
		 * The price of the package
		 */
		private final double price;

		/**
		 * Determines if this package increases your total donated amount
		 */
		private final boolean increasesTotal;

		/**
		 * The reward received from this package
		 */
		private final Reward reward;

		/**
		 * Creates a new donatable package with a set name and price
		 * 
		 * @param packageName the name of the package
		 * @param price the cost of the package
		 * @param reward the reward received
		 */
		private DonatablePackage(String packageName, double price, boolean increasesTotal, Reward reward) {
			this.packageName = packageName;
			this.price = price;
			this.increasesTotal = increasesTotal;
			this.reward = reward;
		}

		/**
		 * Returns an Optional of type {@code DonatablePackage} that is generated by comparing the name of the package provided to the array of available packages.
		 * 
		 * @param name the name of the package
		 * @return the package
		 */
		static Optional<DonatablePackage> forName(String name) {
			return Arrays.asList(values()).stream().filter(pack -> pack.packageName.equals(name)).findFirst();
		}
	}

	private interface Reward {

		/**
		 * Appends the reward to the player
		 * 
		 * @param player the player receiving the reward
		 */
		void append(Player player);
	}

}
