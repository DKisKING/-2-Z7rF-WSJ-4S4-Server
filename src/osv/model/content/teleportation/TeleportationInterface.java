package osv.model.content.teleportation;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import osv.model.content.achievement_diary.western_provinces.WesternDiaryEntry;
import osv.model.players.Player;
import osv.util.Misc;

public final class TeleportationInterface {

	/**
	 * Executes every function that has to do with the teleportation interface.
	 * @param player		the player to handle the functionality for.
	 * @param buttonId		the button id that was clicked.
	 * @return {@code true} if any functionality was executed, {@code false} otherwise.
	 */
	public static boolean actions(Player player, int buttonId) {
		if(TeleportationInterface.selectMenu(player, buttonId)) {
			return true;
		}
		if(TeleportationInterface.selectTeleport(player, buttonId)) {
			return true;
		}	
		if(TeleportationInterface.teleport(player, buttonId)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Attempts to teleport the player to the previously specified location.
	 * @param player		the player to teleport.
	 * @param buttonId		the button id that was clicked.
	 * @return {@code true} if the player was teleported, {@code false} otherwise.
	 */
	private static boolean teleport(Player player, int buttonId) {
		if(buttonId != 89115) {
			return false;
		}
		if (System.currentTimeMillis() - player.lastTeleport < 3500) {
			return false;	
		}
		if(player.teleportType == null || player.teleportData == null) {
			player.sendMessage("You haven't specified where you want to teleport yet.");
			return true;
		}
		if (player.inClanWars() || player.inClanWarsSafe()) {
			player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return false;
		}
		TeleportData data = player.teleportData;
		
		player.getPA().spellTeleport(data.x, data.y, data.height);
		player.lastTeleport = System.currentTimeMillis();
		
		if (data.x == 2662 && data.y == 2652) {
			player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.PEST_CONTROL_TELEPORT);
		}
		
		player.teleportData = null;
		player.teleportType = null;
		return true;
	}
	
	/**
	 * Attemmpts to select a teleport for the specified {@code player}.
	 * @param player		the player selecting a teleport.
	 * @param buttonId		the button that was clicked.
	 * @return {@code true} if the player successfully selected a teleport, {@code false] otherwise.
	 */
	public static boolean selectTeleport(Player player, int buttonId) {
		if(!LINE_BUTTON_IDS.contains(buttonId)) {
			return false;
		}
		
		if(player.teleportType == null) {
			player.sendMessage("Please select a menu before selecting a teleport.");
			return true;
		}
		
		int index = LINE_BUTTON_IDS.indexOf(buttonId);
		
		if(player.teleportType.data.size() - 1 < index || player.teleportType.data.get(index) == null) {
			player.sendMessage("There is no teleport on this spot.");
			return true;
		}
		
		TeleportData data = player.teleportType.data.get(index);
		
		player.getPA().sendFrame126(data.information, 31007);
		
		IntStream.range(31011, 31049).forEach(id -> player.getPA().sendChangeSprite(id, (byte) 0));
		player.getPA().sendChangeSprite(LINE_BUTTON_IDS_FOR_LINE_IDS.get(buttonId) - 1, (byte) 1);
		player.teleportData = data;
		return true;
	}

	/**
	 * Attempts to open the teleportation interface.
	 * @param player	the player to open this for.
	 * @return {@code true} if the interface was opened, {@code false} otherwise.
	 */
	public static boolean open(Player player) {
		if (System.currentTimeMillis() - player.lastTeleport < 2500) {
			return false;
		}
		player.lastTeleport = System.currentTimeMillis();
		IntStream.range(0, 12).forEach(id -> player.getPA().sendFrame126("", LINE_IDS.get(id)));
		IntStream.range(31011, 31069).forEach(id -> player.getPA().sendChangeSprite(id, (byte) 0));

		player.getPA().sendFrame126(
				"By clicking teleport below, you are aware that you might \\n have chosen a location which may be placed within \\n the wilderness. And is aware of the risks that follows.",
				31007);

		selectMenu(player, player.lastMenuChosen == 0 ? 121076 : player.lastMenuChosen);

		IntStream.rangeClosed(0, 6).forEach(id -> {
			if (TeleportType.valueOf(id).isPresent())
				player.getPA().sendFrame126(TeleportType.valueOf(id).get().toString(), MENU_LINE_IDS.get(id));
		});
		player.getPA().showInterface(31000);
		return true;
	}

	/**
	 * Attempts to execute the functionality when a player selects a menu.
	 * @param player		the player whom selected the menu.
	 * @param buttonId		the id of the button selected.
	 * @return {@code true} if the menu was opened, {@code false} otherwise.
	 */
	public static boolean selectMenu(Player player, int buttonId) {
		TeleportType type = TeleportType.getTeleportMenu(buttonId).orElse(null);

		if(type == null) {
			return false;
		}
		
		IntStream.range(0, 12).forEach(id -> player.getPA().sendFrame126("", LINE_IDS.get(id)));
		IntStream.range(31011, 31069).forEach(id -> player.getPA().sendChangeSprite(id, (byte) 0));
		
		player.getPA().sendFrame126("By clicking teleport below, you are aware that you might \\n have chosen a location which may be placed within \\n the wilderness. And is aware of the risks that follows.", 31007);
		
		player.teleportData = null;
		player.teleportType = type;
		player.lastMenuChosen = buttonId;
		
		IntStream.range(31011, 31069).forEach(id -> player.getPA().sendChangeSprite(id, (byte) 0));
		player.getPA().sendChangeSprite(MENU_BUTTON_IDS_FOR_MENU_LINE_IDS.get(buttonId) - 1, (byte) 1);
		
		IntStream.rangeClosed(0, 12).forEach(id -> {
			if(TeleportType.valueOf(type, id).isPresent()) {
				player.getPA().sendFrame126(TeleportType.valueOf(type, id).get().toString(), LINE_IDS.get(id));
			}
		});
		return true;
	}

	private static final ImmutableList<Integer> MENU_LINE_IDS = ImmutableList.of(31053, 31056, 31059, 31062, 31065, 31068);//u sure its - 1 for that aswel?

	private static final ImmutableList<Integer> LINE_IDS = ImmutableList.of(31013, 31016, 31019, 31022, 31025, 31028, 31033, 31036, 31039, 31042, 31045, 31048);
	
	private static final ImmutableList<Integer> LINE_BUTTON_IDS = ImmutableList.of(121036, 121039, 121042, 121045, 121048, 121051, 121056, 121059, 121062, 121065, 121068, 121071); 
	
	private static final ImmutableMap<Integer, Integer> LINE_BUTTON_IDS_FOR_LINE_IDS = ImmutableMap.copyOf(Misc.map(LINE_BUTTON_IDS, LINE_IDS));
	
	private static final ImmutableMap<Integer, Integer> MENU_BUTTON_IDS_FOR_MENU_LINE_IDS = ImmutableMap.copyOf(Misc.map(ImmutableList.of(121076, 121079, 121082, 121085, 121088, 121091), MENU_LINE_IDS));
	
	public enum TeleportType {
		TRAINING(121076, TeleportData.CHICKENS, TeleportData.ROCK_CRABS, TeleportData.COWS),
		MONSTERS(121079, TeleportData.SLAYER_TOWER, TeleportData.EDGEVILLE_DUNGEON, TeleportData.TAVERLY_DUNGEON, TeleportData.BRIMHAVEN_DUNGEON, TeleportData.RELLEKKA_DUNGEON, TeleportData.STRONGHOLD_CAVE, TeleportData.MITHRIL_DRAGONS, TeleportData.LLETYA, TeleportData.DEMONIC_GORILLAS, TeleportData.ICE_DUNGEON, TeleportData.DAGANNOTH_CAVE),
		BOSSES(121082, TeleportData.KING_BLACK_DRAGON, TeleportData.CHAOS_ELEMENTAL, TeleportData.GODWARS, TeleportData.BARRELCHEST, TeleportData.KRAKEN, TeleportData.VENENATIS, TeleportData.VETION, TeleportData.CALLISTO, TeleportData.GIANT_MOLE, TeleportData.LIZARDMAN_CANYON, TeleportData.ABYSSAL_SIRE, TeleportData.RAIDS),
		MINIGAMES(121085, TeleportData.PEST_CONTROL, TeleportData.DUEL_ARENA, TeleportData.FIGHT_CAVES, TeleportData.BARROWS, TeleportData.WARRIORS_GUILD, TeleportData.MAGE_ARENA, TeleportData.LIGHTHOUSE, TeleportData.RECIPE_FOR_DISASTER),
		SKILLING(121088, TeleportData.SKILLING_AREA, TeleportData.HUNTING_GROUNDS, TeleportData.WOODCUTTING_GUILD),
		PLAYER_KILLING(121091, TeleportData.WILDERNESS_PORTALS, TeleportData.WEST_DRAGONS, TeleportData.EAST_DRAGONS, TeleportData.HILL_GIANTS);
		
		/**
		 * Caches our enum values.
		 */
		private static final ImmutableSet<TeleportType> VALUES = Sets.immutableEnumSet(EnumSet.allOf(TeleportType.class));

		/**
		 * The button identification to open this teleport type.
		 */
		private final int buttonId;

		/**
		 * The teleports that can be teleported to on this teleport type.
		 */
		private final ImmutableList<TeleportData> data;

		/**
		 * Constructs a new {@link TeleportType}.
		 * @param buttonId	{@link #buttonId}.
		 * @param data		{@link #data}.
		 */
		private TeleportType(int buttonId, TeleportData... data) {
			this.buttonId = buttonId;
			this.data = ImmutableList.copyOf(Arrays.asList(data));
		}

		public static Optional<TeleportType> valueOf(int ordinal) {
			return VALUES.stream().filter($it -> $it.ordinal() == ordinal).findAny();
		}
		
		public static Optional<TeleportType> valueOf(TeleportData data) {
			return VALUES.stream().filter($it -> $it.data.contains(data)).findAny();
		}
		
		public static Optional<TeleportData> valueOf(TeleportType type, int ordinal) {
			return Optional.ofNullable(type.data.size() > ordinal ? type.data.get(ordinal) : null);
		}
		
		public static Optional<TeleportType> getTeleportMenu(int button) {
			return VALUES.stream().filter($it -> $it.buttonId == button).findAny();
		}

		@Override
		public String toString() {
			return StringUtils.capitalize(name().replaceAll("_", " ").toLowerCase());
		}
	}


	public enum TeleportData {
		/**
		 * Player killing teleports
		 */
		WILDERNESS_PORTALS(89192, 2980, 3871, 0, "Find yourself at one of the wilderness portals \\n @red@Be aware, this is within the wilderness"),
		WEST_DRAGONS(89225, 2979, 3597, 0, "Find yourself to the west dragons area \\n @red@Be aware, this is within the wilderness"),
		EAST_DRAGONS(90002, 3341, 3685, 0, "Find yourself to the east dragons area \\n @red@Be aware, this is within the wilderness"),
		HILL_GIANTS(90035, 3288, 3631, 0, "Find yourself to the hill giants area \\n @red@Be aware, this is within the wilderness"),

		/**
		 * Minigame teleports
		 */
		PEST_CONTROL(89192, 2662, 2652, 0, "Make your way to the pest control \\n Conquer the island and get yourself a set of void gear"),
		DUEL_ARENA(89225, 3365, 3266, 0, "Want to test your luck? \\n Then the duel arena is the place you want to visit"),
		FIGHT_CAVES(90002, 2439, 5169, 0, "Find the red devil within the fight caves and defeat him \\n and you might be lucky to get a hot reward"),
		BARROWS(90035, 3565, 3308, 0, "Visit the sleeping brothers \\n they might just have something you want"),
		WARRIORS_GUILD(90068, 2847, 3543, 0, "Find the cyclops and try your luck on a defender"),
		MAGE_ARENA(90101, 3106, 3959, 0, "Fight your way to obtain mage arena points which \\n may be used for goodies in the mage points store \\n @red@Be aware, this is within the wilderness"),
		LIGHTHOUSE(90156, 2514, 3621, 0, "Finish the minigame and purchase yourself \\n a colored book of your choice"),
		RECIPE_FOR_DISASTER(90189, 3218, 9622, 0, "Finish the minigame and purchase yourself \\n a pair of gloves of your choice"),

		/**
		 * Boss teleports
		 */
		KING_BLACK_DRAGON(89192, 3005, 3850, 0, "Fight the vicious king black dragon \\n @red@Be aware, this is within the wilderness"),
		CHAOS_ELEMENTAL(89225, 3262, 3929, 0, "Fight the vicious chaos elemental \\n @red@Be aware, this is within the wilderness"),
		GODWARS(90002, 2880, 5310, 2, "Enter the dungeon of godwars \\n And test how long lived you really are"),
		BARRELCHEST(90035, 3331, 3706, 0, "Fight the vicious yet clumsy barrelchest \\n @red@Be aware, this is within the wilderness"),
		KRAKEN(90101, 3292, 3648, 0, "Fight the vicious multi tentacled kraken \\n @red@Be aware, this is within the wilderness"),
		VENENATIS(90156, 3343, 3741, 0, "Fight the vicious spider, venenatis \\n @red@Be aware, this is within the wilderness"),
		VETION(90189, 3179, 3774, 0, "Fight the vicious undead, vetion \\n @red@Be aware, this is within the wilderness"),
		CALLISTO(90222, 3313, 3826, 0, "Fight the vicious bear, callisto \\n @red@Be aware, this is within the wilderness"),
		GIANT_MOLE(90255, 3077, 3910, 0, "Fight the giant mole \\n @red@Be aware, this is within the wilderness"),
		LIZARDMAN_CANYON(91032, 1476, 3687, 0, "Visit the lizardman canyon \\n And see what you may find"),
		ABYSSAL_SIRE(91032, 3037, 4763, 0, "Visit the sire of the abyssal\\n Be ready, this might get dangerous!"),
		RAIDS(91032, 1643, 3674, 0, "Visit the raids dungeons\\n And combat mighty foes!"),

		/**
		 * Monster teleports
		 */
		SLAYER_TOWER(89192, 3428, 3538, 0, "Contains multiple slayer creatures you might be looking for"),
		EDGEVILLE_DUNGEON(89225, 3096, 9867, 0, "Filled with creatures and is part wilderness"),
		TAVERLY_DUNGEON(90002, 2884, 9798, 0, "Filled with creatures like dragons and hellhounds, and also a vicious boss"),
		BRIMHAVEN_DUNGEON(90035, 2679, 9565, 0, "Filled with creatures like dragons and giants"),
		RELLEKKA_DUNGEON(90068, 2808, 10002, 0, "Filled with creatures like..."),
		STRONGHOLD_CAVE(90101, 2444, 9825, 0, "Filled with various creatures"),
		MITHRIL_DRAGONS(90156, 1746, 5323, 0, "Here you will find high leveled mithril dragons"),
		LLETYA(90189, 2352, 3160, 0, "Here you will find elves of different sorts"),
		DEMONIC_GORILLAS(90222, 2128, 5647, 0, "Visit the Crash Site Cavern \\n And combat some evil gorillas"),
		ICE_DUNGEON(90222, 3035, 9581, 0, "Visit the Asgarnian Ice Dungeon \\n And combat some cold creatures"),
		DAGANNOTH_CAVE(90068, 1923, 4367, 0, "Fight the vicious trio of dagannoths \\n They sure know how to work together"),

		/**
		 * Training
		 */
		CHICKENS(89192, 3236, 3295, 0, "Suitable training ground for beginners"),
		ROCK_CRABS(89225, 2679, 3717, 0, "Suitable training ground for beginners"),
		COWS(90002, 3253, 3267, 0, "Suitable training ground for beginners"),

		/**
		 * Skilling teleports
		 */
		SKILLING_AREA(89192, 3027, 3379, 0, "Find yourself at the skilling area, located \\n at the falador park"),
		HUNTING_GROUNDS(89225, 2525, 2916, 0, "Find yourself in the hunting grounds jungle"),
		WOODCUTTING_GUILD(90002, 1656, 3505, 0, "Find yourself at the woodcutting guild");

		/**
		 * Caches our enum values.
		 */
		public static final ImmutableSet<TeleportData> VALUES = Sets.immutableEnumSet(EnumSet.allOf(TeleportData.class));

		/**
		 * The button identification.
		 */
		private final int button;

		/**
		 * The x-axis for this position.
		 */
		private final int x;

		/**
		 * The y-axis for this position.
		 */
		private final int y;

		/**
		 * The height-axis for this position.
		 */
		private final int height;

		/**
		 * The information for this position.
		 */
		private final String information;

		/**
		 * Constructs a new {@link TeleportData}.
		 * @param button			{@link #button}.
		 * @param x					{@link #x}.
		 * @param y					{@link #y}.
		 * @param height			{@link #height}.
		 * @param information		{@link #information}.
		 */
		TeleportData(int button, int x, int y, int height, String information) {
			this.button = button;
			this.x = x;
			this.y = y;
			this.height = height;
			this.information = information;
		}

		public static Optional<TeleportData> valueOf(int ordinal) {
			return VALUES.stream().filter($it -> $it.ordinal() == ordinal).findAny();
		}
		
		public static Optional<TeleportData> getTeleport(int button) {
			return VALUES.stream().filter($it -> $it.button == button).findAny();
		}

		@Override
		public String toString() {
			return StringUtils.capitalize(name().replaceAll("_", " ").toLowerCase());
		}
	}
}
