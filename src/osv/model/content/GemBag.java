package osv.model.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import osv.Server;
import osv.model.content.LootingBag.LootingBagItem;
import osv.model.entity.Entity;
import osv.model.items.ItemAssistant;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.Player;
import osv.model.players.PlayerSave;

public class GemBag {
	/**
	 * Checks whether a player is allowed to configure the gem bag or not
	 * @return
	 */
	public boolean configurationPermitted() {
		if (player.inDuelArena() || player.inPcGame() || player.inPcBoat() || player.isInJail() || player.getInterfaceEvent().isActive() || player.getPA().viewingOtherBank
				|| player.isDead || player.viewingLootBag || player.addingItemsToLootBag) {
			return false;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return false;
		}
		if (player.getTutorial().isActive()) {
			player.getTutorial().refresh();
			return false;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			player.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
			player.sendMessage("You must decline the trade to start walking.");
			return false;
		}
		if (player.isStuck) {
			player.isStuck = false;
			player.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
			return false;
		}
		return true;
	}
	
	public Player player;
	public List<LootingBagItem> items;

	/**
	 * The gem bag id and boolean to set if we want to check if a player has one
	 */
	public static final int GEM_BAG_ID = 12020;
	public static final boolean CHECK_FOR_BAG = true;

	/**
	 * The gem bag class
	 * @param player
	 */
	public GemBag(Player player) {
		this.player = player;
		items = new ArrayList<>();
	}

	/**
	 * Handles players death with a gem bag in their inventory
	 * @param o
	 * @param entity
	 */
	public void onDeath(Player o, String entity) {
		Entity killer = player.getKiller();
		
		if (o == null) {
			return;
		}
		for (Iterator<LootingBagItem> iterator = items.iterator(); iterator.hasNext();) {
			LootingBagItem item = iterator.next();

			if (item == null) {
				continue;
			}
			if (item.getId() <= 0 || item.getAmount() <= 0) {
				continue;
			}
			if (entity == "PVP") {
				if (killer != null && killer instanceof Player) {
					Player playerKiller = (Player) killer;
					if (playerKiller.getMode().isItemScavengingPermitted()) {
						Server.itemHandler.createGroundItem(playerKiller, item.getId(), player.getX(), player.getY(), player.heightLevel, item.getAmount(), player.killerId);
					} else {
						Server.itemHandler.createUnownedGroundItem(item.getId(), player.getX(), player.getY(), player.heightLevel, item.getAmount());
					}
				}
			} else {
				Server.itemHandler.createGroundItem(player, item.getId(), player.getX(), player.getY(),
						player.heightLevel, item.getAmount(), player.getIndex());
			}
			iterator.remove();
		}
		PlayerSave.saveGame(player);
	}
	
	/**
	 * Attempts to withdraw all gems from the gem bag
	 */
	public void withdrawAll() {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		for (Iterator<LootingBagItem> iterator = items.iterator(); iterator.hasNext();) {
			LootingBagItem item = iterator.next();
			if (!player.getItems().addItem(item.getId(), item.getAmount())) {
				break;
			}
			iterator.remove();
		}
	}

	/**
	 * The id's of the gems you are allowed to store in the gem bag
	 */
	public int[] uncutGems = new int[] { 1617, 1619, 1621, 1623, 1625, 1627, 1629, 1631, 6571, 19496 };
	
	/**
	 * Attempts to fill the bag with the gems a player has in their inventory
	 */
	public void fillBag() {
		for (int i = 0; i < uncutGems.length; i++) {
			if (player.getItems().playerHasItem(uncutGems[i], 1)) {
				addItemToGemBag(uncutGems[i], player.getItems().getItemAmount(uncutGems[i]));
			}
		}
	}

	/**
	 * Attempts  to add the gems chosen to the gem bag
	 * @param id
	 * @param amount
	 */
	public void addItemToGemBag(int id, int amount) {
		boolean isUncut = IntStream.of(uncutGems).anyMatch(identification -> identification == id);
		boolean haveUncut = IntStream.of(uncutGems).anyMatch(identification -> player.getItems().playerHasItem(identification));
		if (!haveUncut) {
			player.sendMessage("You currently do not have any uncut gems to store.");
			return;
		}
		if (!isUncut) {
			player.sendMessage("You can only store uncut gems in the gem bag.");
			return;
		}
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (player.getItems().isStackable(id)) {
			return;
		}
		if (amount >= 28) {
			amount = player.getItems().itemAmount(id);
		}
		if (id == GEM_BAG_ID) {
			player.sendMessage("Don't be silly.");
			return;
		}
		if (items.size() >= 61 && !(sackContainsItem(id) && player.getItems().isStackable(id))) {
			System.out.println("This");
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		if (countItems(id) + amount >= 61 || countItems(id) + amount <= 0) {
			player.sendMessage("You cannot store this many of this gem.");
			return;
		}
		player.sendMessage("Filled the gem bag with x" + amount + " " + ItemAssistant.getItemName(id));
		for (int amt = 0; amt < amount; amount--) {
			player.getItems().deleteItem(id, 1);
			addItemToList(id + 1, 1);
		}
	}
	
	/**
	 * Checks the amount and of what gem you have stored in the sack
	 */
	public void check() {
		int frame = 8149;
		int totalAmount = 0;
		for (int i2 = 8144; i2 < 8195; i2++) {
			player.getPA().sendFrame126("", i2);
		}
		player.getPA().sendFrame126("@dre@                   Gem Bag", 8144);
		player.getPA().sendFrame126("", 8145);
		player.getPA().sendFrame126("", 8148);
		if (totalAmount == 0) {
			player.getPA().sendFrame126("@red@EMPTY", 8147);
		}
		for (int i = 0; i < 14; i++) {
			int id = 0;
			int amt = 0;

			if (i < items.size()) {
				LootingBagItem item = items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
				totalAmount += amt;
				player.getPA().sendFrame126("@red@Total Amount: "+totalAmount+"/600", 8147);
				player.getPA().sendFrame126("@blu@x" + amt + " " + ItemAssistant.getItemName(id) + "", frame);
				frame++;
			}
			player.getPA().showInterface(8134);
		}
	}
	
	/**
	 * Checks if the sack contains a certain gem
	 * @param id
	 * @return
	 */
	public boolean sackContainsItem(int id) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Counts the amount of a certain gem
	 * @param id
	 * @return
	 */
	public int countItems(int id) {
		int count = 0;
		for (LootingBagItem item : items) {
			if (item.getId() == id + 1) {
				count += item.getAmount();
			}
		}
		return count;
	}
	
	/**
	 * Adds the gems to a list
	 * @param id
	 * @param amount
	 * @return
	 */
	public boolean addItemToList(int id, int amount) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				if (item.getAmount() + amount >= 61) {
					return false;
				}
				if (player.getItems().isStackable(id)) {
					item.incrementAmount(amount);
					return false;
				}
			}
		}
		items.add(new LootingBagItem(id, amount));
		return true;
	}
}
