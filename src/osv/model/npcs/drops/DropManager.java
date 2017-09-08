package osv.model.npcs.drops;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import osv.Server;
import osv.model.items.GameItem;
import osv.model.items.Item;
import osv.model.items.ItemAssistant;
import osv.model.npcs.NPC;
import osv.model.npcs.NPCDefinitions;
import osv.model.players.Boundary;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.util.Location3D;
import osv.util.Misc;

public class DropManager {

	private static final DecimalFormat PERCENTILE_FORMAT = new DecimalFormat("#.###");

	public static int AMOUNT_OF_TABLES = 0;

	private static final Comparator<Integer> COMPARE_NAMES = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			String name1 = NPCDefinitions.get(o1).getNpcName(); 
			String name2 = NPCDefinitions.get(o2).getNpcName();
			return name1.compareToIgnoreCase(name2);
		}
	};

	private Map<List<Integer>, TableGroup> groups = new HashMap<>();

	private List<Integer> ordered = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void read() {
		JSONParser parser = new JSONParser();
		try {
			fileReader = new FileReader("./Data/json/npc_droptable.json");
			JSONArray data = (JSONArray) parser.parse(fileReader);
			Iterator<?> drops = data.iterator();

			while (drops.hasNext()) {
				JSONObject drop = (JSONObject) drops.next();

				List<Integer> npcIds = new ArrayList<>();

				if (drop.get("npc_id") instanceof JSONArray) {
					JSONArray idArray = (JSONArray) drop.get("npc_id");
					idArray.forEach(id -> npcIds.add(((Long) id).intValue()));
				} else {
					npcIds.add(((Long) drop.get("npc_id")).intValue());
				}

				TableGroup group = new TableGroup(npcIds);

				for (TablePolicy policy : TablePolicy.POLICIES) {
					if (!drop.containsKey(policy.name().toLowerCase())) {
						continue;
					}
					JSONObject dropTable = (JSONObject) drop.get(policy.name().toLowerCase());
					Table table = new Table(policy, ((Long) dropTable.get("accessibility")).intValue());
					JSONArray tableItems = (JSONArray) dropTable.get("items");
					Iterator<?> items = tableItems.iterator();

					while (items.hasNext()) {
						JSONObject item = (JSONObject) items.next();
						int id = ((Long) item.get("item")).intValue();
						int minimumAmount = ((Long) item.get("minimum")).intValue();
						int maximumAmount = ((Long) item.get("maximum")).intValue();
						table.add(new Drop(npcIds, id, minimumAmount, maximumAmount));
					}
					group.add(table);
				}
				groups.put(npcIds, group);
			}
			ordered.clear();

			for (TableGroup group : groups.values()) {
				if (group.getNpcIds().size() == 1) {
					ordered.add(group.getNpcIds().get(0));
					continue;
				}
				for (int id : group.getNpcIds()) {
					String name = NPCDefinitions.get(id).getNpcName();
					if (ordered.stream().noneMatch(i -> NPCDefinitions.get(i).getNpcName().equals(name))) {
						ordered.add(id);
					}
				}
			}

			ordered.sort(COMPARE_NAMES);
			Misc.println("Loaded " + ordered.size() + " drop tables.");
			AMOUNT_OF_TABLES = ordered.size();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to create a drop for a player after killing a non-playable character
	 * 
	 * @param player the player receiving a possible drop
	 * @param npc the npc dropping the items
	 */
	static boolean test = false;
	
	static int[] bosses = { 
			/* Misc bosses */
			6619, 6618, 6615, 6766, 963, 965, 5890, 6609, 319, 6610, 6611, 5779, 6342, 2205, 2215, 3129, 3162, 2054, 2265, 2266, 2267,
			/* Godwars minions */ 
			2206, 2207, 2208, 3130, 3131, 3132, 2216, 2217, 2218, 3163, 3164, 3165
	};

	public void create(Player player, NPC npc, Location3D location, int repeats) {
		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npc.npcType)).findFirst();
		
		group.ifPresent(g -> {
			double modifier = getModifier(player);
			List<GameItem> drops = g.access(player, modifier, repeats);

			for (GameItem item : drops) {
				if (item.getId() == 536) {
					if (player.getRechargeItems().hasItem(13111) && player.inWild()) {
						item.changeDrop(537, item.getAmount());
					}
				}
				if (item.getId() == 6529) {
					if (player.getRechargeItems().hasItem(11136)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.20));
					}
					if (player.getRechargeItems().hasItem(11138)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.50));
					}
					if (player.getRechargeItems().hasItem(11140)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.70));
					}
					if (player.getRechargeItems().hasItem(13103)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.90));
					}
				}
				if (item.getId() == 6729 && player.getRechargeItems().hasItem(13132)) {
					item.changeDrop(6730, item.getAmount());
				}
				if (item.getId() == 13233 && !Boundary.isIn(player, Boundary.CERBERUS_BOSSROOMS)) {
					player.sendMessage("@red@Something hot drops from the body of your vanquished foe");
				}
				
				if (IntStream.of(bosses).anyMatch(id -> id == npc.npcType)) {
					PlayerHandler.nonNullStream()
					.filter(p -> p.distanceToPoint(player.absX, player.absY) < 10 && p.heightLevel == player.heightLevel)
					.forEach(p -> {
						if (item.getAmount() > 1)
							p.sendMessage("@dre@" + Misc.formatPlayerName(player.playerName) + " received a drop: " + Misc.format(item.getAmount()) + " x " + Item.getItemName(item.getId()) + ".");
						else
							p.sendMessage("@dre@" + Misc.formatPlayerName(player.playerName) + " received a drop: " + Item.getItemName(item.getId()) + ".");
					});
				}

				Server.itemHandler.createGroundItem(player, item.getId(), location.getX(), location.getY(),
						location.getZ(), item.getAmount(), player.getIndex());
			}

			/**
			 * Looting bag and rune pouch
			 */
			if (npc.inWild()) {
				switch (Misc.random(60)) {
				case 2:
					if (player.getItems().getItemCount(11941, true) < 1) {
						Server.itemHandler.createGroundItem(player, 11941, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
					}
					break;
					
				case 8:
					if (player.getItems().getItemCount(12791, true) < 1) {
						Server.itemHandler.createGroundItem(player, 12791, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
					}
					break;
				}
			}
			/**
			 * Clue scrolls
			 */
			int chance = player.getRechargeItems().hasItem(13118) ? 142 : player.getRechargeItems().hasItem(13119) ? 135 : player.getRechargeItems().hasItem(13120) ? 120 : 150;
			if (Misc.random(chance) == 1) {
				player.sendMessage("@pur@You sense a clue scroll being dropped to the ground.");
				if (npc.getDefinition().getNpcCombat() > 0 && npc.getDefinition().getNpcCombat() <= 80) {
					Server.itemHandler.createGroundItem(player, 2677, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
				} 
				if (npc.getDefinition().getNpcCombat() > 80 && npc.getDefinition().getNpcCombat() <= 140) {
					Server.itemHandler.createGroundItem(player, 2801, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
				} 
				if (npc.getDefinition().getNpcCombat() > 140) {
					Server.itemHandler.createGroundItem(player, 2722, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
				}
			}

			/**
			 * Runecrafting pouches
			 */
			if (Misc.random(200) == 10) {
				if (npc.getDefinition().getNpcCombat() >= 70 && npc.getDefinition().getNpcCombat() <= 100 && player.getItems().getItemCount(5509, true) == 1 && player.getItems().getItemCount(5510, true) != 1) {
					Server.itemHandler.createGroundItem(player, 5510, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
				} else if (npc.getDefinition().getNpcCombat() > 100 && player.getItems().getItemCount(5510, true) == 1 && player.getItems().getItemCount(5512, true) != 1) {
					Server.itemHandler.createGroundItem(player, 5512, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
				}
			}

			/**
			 * Crystal keys
			 */
			if (Misc.random(115) == 1) {
				player.sendMessage("@pur@You sense a crystal key being dropped to the ground.");
				Server.itemHandler.createGroundItem(player, 989, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
			}
		});
	}

	private double getModifier(Player player) {
		double modifier = 1.0;
		if (player.getItems().isWearingItem(2572)) {
			modifier -= .03;
		} else if (player.getItems().isWearingItem(12785)) {
			modifier -= .05;
		}
		if (player.getRights().contains(Right.LEGENDARY)) {
			modifier -= 0.150;
		} else if (player.getRights().contains(Right.MEGA_VIP)) {
			modifier -= 0.120;
		} else if (player.getRights().contains(Right.SUPER_VIP)) {
			modifier -= 0.100;
		} else if (player.getRights().contains(Right.VIP)) {
			modifier -= 0.070;
		} else if (player.getRights().contains(Right.SUPPORTER)) {
			modifier -= 0.050;
		} else if (player.getRights().contains(Right.SPONSOR)) {
			modifier -= 0.035;
		} else if (player.getRights().contains(Right.CONTRIBUTOR)) {
			modifier -= 0.020;
		}
		return modifier;
	}

	public void clear(Player player) {
		for(int i = 0; i < 150; i++) {
			player.getPA().sendFrame126("", 42531 + i);
		}
		player.searchList.clear();
	}

	public void open(Player player) {
		clear(player);

		for (int index = 0; index < ordered.size(); index++) {
			player.getPA().sendFrame126(StringUtils.capitalize(NPCDefinitions.get(ordered.get(index)).getNpcName().toLowerCase().replaceAll("_", " ")), 42531 + index);
		}

		player.getPA().showInterface(42500);
	}

	public void search(Player player, String name) {
		if(name.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+$")) {
			player.sendMessage("You may not search for alphabetical and numerical combinations.");
			return;
		}
		if (System.currentTimeMillis() - player.lastDropTableSearch < TimeUnit.SECONDS.toMillis(5)) {
			player.sendMessage("You can only do this once every 5 seconds.");
			return;
		}
		player.lastDropTableSearch = System.currentTimeMillis();
		
		clear(player);

		List<Integer> definitions = ordered.stream().filter(Objects::nonNull).filter(def -> NPCDefinitions.get(def).getNpcName() != null).filter(def -> NPCDefinitions.get(def).getNpcName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());

		if(definitions.isEmpty()) {
			definitions = ordered.stream().filter(Objects::nonNull).collect(Collectors.toList());
			List<Integer> npcs = new ArrayList<>();
			int count = 0;
			for(Integer index : definitions) {
				Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(NPCDefinitions.get(index).getNpcId())).findFirst();
				if(group.isPresent()) {
					TableGroup g = group.get();
					
					for(TablePolicy policy : TablePolicy.values()) {
						Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
						if(table.isPresent()) {
							for(Drop drop : table.get()) {
								if(drop == null) {
									continue;
								}
								
								if(ItemAssistant.getItemName(drop.getItemId()).toLowerCase().contains(name.toLowerCase())) {
									npcs.add(index);
									player.getPA().sendFrame126(StringUtils.capitalize(NPCDefinitions.get(NPCDefinitions.get(index).getNpcId()).getNpcName().toLowerCase().replaceAll("_", " ")), 42531 + count);
									count++;
								}
							}
							
							
						}
					}
				};

			}
			
			player.searchList = npcs;
			return;
			
		}
		
		for(int index = 0; index < definitions.size(); index++) {
			if(index >= 150) {
				break;
			}
			player.getPA().sendFrame126(StringUtils.capitalize(NPCDefinitions.get(definitions.get(index)).getNpcName().toLowerCase().replaceAll("_", " ")), 42531 + index);
		}

		player.searchList = definitions;
	}

	public void select(Player player, int button) {
		int listIndex = button - 166035;
		if (listIndex < 0 || listIndex > ordered.size() - 1) {
			return;
		}

		int npcId = player.searchList.isEmpty() ? ordered.get(listIndex) : player.searchList.get(listIndex);

		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();

		group.ifPresent(g -> {
			if (System.currentTimeMillis() - player.lastDropTableSelected < TimeUnit.SECONDS.toMillis(5)) {
				player.sendMessage("You can only do this once every 5 seconds.");
				return;
			}

			player.lastDropTableSelected = System.currentTimeMillis();
			String name = StringUtils.capitalize(NPCDefinitions.get(npcId).getNpcName().toLowerCase().replaceAll("_", " "));
			player.getPA().sendFrame126(name + " (" + npcId + ")", 42502);
			double modifier = getModifier(player);
			for (TablePolicy policy : TablePolicy.POLICIES) {
				Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
				if (table.isPresent()) {
					double chance = (1.0 / (double) (table.get().getAccessibility() * modifier)) * 100D;
					int in_kills = (int) (100 / chance);
					if (chance > 100.0) {
						chance = 100.0;
					}
					if (in_kills == 0) {
						in_kills = 1;
					}
					if (player.dropRateInKills) {
						player.getPA().sendString("1/"+in_kills+"" + "", 42514 + policy.ordinal());
					} else {
						player.getPA().sendString(PERCENTILE_FORMAT.format(chance) + "%", 42514 + policy.ordinal());
					}
					updateAmounts(player, policy, table.get());
					updateTable(player, table.get());
				} else {
					player.getPA().sendString("-", 42514 + policy.ordinal());
					updateAmounts(player, policy, new ArrayList<Drop>());
					updateTable(player, new Table(policy, -1));
				}
			}
		});
	}

	public static void updateTable(Player player, Table table) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(42733 + table.getPolicy().ordinal());
		int length = table.size();
		int current = 0;

		player.getOutStream().writeWord(length);
		for (Drop drop : table) {
			if (drop.getMaximumAmount() > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(drop.getMaximumAmount());
			} else {
				player.getOutStream().writeByte(drop.getMaximumAmount());
			}
			player.getOutStream().writeWordBigEndianA(drop.getItemId() + 1);
			current++;
		}

		for (; current < 50; current++) {
			player.getOutStream().writeByte(1);
			player.getOutStream().writeWordBigEndianA(-1);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	private void updateAmounts(Player player, TablePolicy policy, List<Drop> drops) {
		int collumnOffset = policy.ordinal() * 100;

		for (int index = 0; index < drops.size(); index++) {
			Drop drop = drops.get(index);
			int minimum = drop.getMinimumAmount();
			int maximum = drop.getMaximumAmount();
			int frame = 42739 + collumnOffset + (index * 2);
			if (minimum == maximum) {
				player.getPA().sendString("", frame);
			} else {
				player.getPA().sendString(Misc.getValueWithoutRepresentation(drop.getMinimumAmount()) + " - " + Misc.getValueWithoutRepresentation(drop.getMaximumAmount()), frame);
			}
		}

		for (int index = drops.size(); index < 50; index++) {
			player.getPA().sendString("", 42739 + collumnOffset + (index * 2));
		}
	}

	static int amountt = 0;

	private FileReader fileReader;

	/**
	 * Testing droptables of chosen npcId
	 * @param player		The player who is testing the droptable
	 * @param npcId			The npc who of which the player is testing the droptable from
	 * @param amount		The amount of times the player want to grab a drop from the npc droptable
	 */
	public void test(Player player, int npcId, int amount) {
		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();

		amountt = amount;

		while (amount-- > 0) {
			group.ifPresent(g -> {
				List<GameItem> drops = g.access(player, 1.0, 1);

				for (GameItem item : drops) {
					player.getItems().addItemToBank(item.getId(), item.getAmount());
				}
			});
		}
		player.sendMessage("Completed @blu@" + amountt + "@bla@ drops from @blu@" + Server.npcHandler.getNpcName(npcId) + "@bla@.");
	}


}
