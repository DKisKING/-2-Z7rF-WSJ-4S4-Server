package osv.model.content;

import osv.model.players.Player;

/**
 * Opening resource packs
 * @author Matt
 */
public enum Packs {
		/**
		 * Packs data
		 * id, itemId, amount
		 */
		AIR_PACK(12728, 556, 100), 
		WATER_PACK(12730, 555, 100), 
		EARTH_PACK(12732, 557, 100), 
		FIRE_PACK(12734, 554, 100), 
		CHAOS_PACK(12738, 562, 100), 
		FEATHER_PACK(11881, 314, 100), 
		VIAL_OF_WATER_PACK(11879, 228, 100), 
		EMPTY_VIAL_PACK(11877, 230, 100), 
		BAIT_PACK(11883, 313, 100), 
		SOFT_CLAY_PACK(12009, 1762, 50), 
		BIRD_SNARE_PACK(12740, 10007, 50), 
		BOX_TRAP_PACK(12742, 10009, 50), 
		MAGIC_IMP_PACK(12744, -1, -1),
		AMYLASE_PACK(12641, 12640, 100),
		EASY_CLUE_BOTTLE(13648, 2677, 1),
		MEDIUM_CLUE_BOTTLE(13649, 2801, 1),
		HARD_CLUE_BOTTLE(13650, 2722, 1),
		EASY_CLUE_GEODE(20358, 2677, 1),
		MEDIUM_CLUE_GEODE(20360, 2801, 1),
		HARD_CLUE_GEODE(20362, 2722, 1),
		EASY_CLUE_NEST(19712, 2677, 1),
		MEDIUM_CLUE_NEST(19714, 2801, 1),
		HARD_CLUE_NEST(19716, 2722, 1);

		private int packId;
		private int itemId;
		private int itemAmount;

		public int getPackId() {
			return packId;
		}

		public int getItemId() {
			return itemId;
		}

		public int getItemAmount() {
			return itemAmount;
		}

		Packs(int packId, int itemId, int itemAmount) {
			this.packId = packId;
			this.itemId = itemId;
			this.itemAmount = itemAmount;
		}
	
	public static void openPack(final Player player, int item) {
		for (Packs pack : Packs.values()) {
			String name = pack.name().toLowerCase().replaceAll("_", " ");
			if (pack.getPackId() == item) {
				if (player.getItems().playerHasItem(item)) {
					player.getItems().deleteItem(pack.getPackId(), 1);
					player.getItems().addItem(pack.getItemId(), pack.getItemAmount());
					player.sendMessage("You opened the " + name + ".");
				}
			}
		}
	}

}