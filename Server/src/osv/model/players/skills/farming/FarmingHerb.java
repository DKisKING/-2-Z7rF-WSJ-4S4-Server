package osv.model.players.skills.farming;

import osv.model.items.ItemAssistant;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 27, 2013
 */
public class FarmingHerb {

	public enum Herb {

		GUAM(5291, 199, 100, 30, 1, 25, 8000), 
		MARRENTIL(5292, 201, 200, 50, 7, 25, 7800), 
		TARROMIN(5293, 203, 300, 100, 19, 25, 7600), 
		HARRALANDER(5294, 205, 500, 150, 26, 25, 7400), 
		RANARR(5295, 207, 600, 200, 32, 50, 7200), 
		TOADFLAX(5296, 3049, 1000, 250, 38, 50, 7000), 
		IRIT(5297, 209, 1250, 300, 44, 50, 6800), 
		AVANTOE(5298, 211, 1500, 350, 50, 50, 6600), 
		KWUARM(5299, 213, 1750, 375, 56, 50, 6400), 
		SNAP_DRAGON(5300, 3051, 2000, 400, 62, 100, 6200), 
		CADANTINE(5301, 215, 2250, 425, 67, 100, 6000), 
		LANTADYME(5302, 2485, 2300, 450, 73, 100, 5800), 
		DRAWF_WEED(5303, 217, 2400, 475, 79, 100, 5600), 
		TORSTOL(5304, 219, 2500, 500, 85, 100, 5000);

		int seedId, grimyId, levelRequired, time, petChance;
		double plantXp, harvestXp;

		Herb(int seedId, int grimyId, double plantXp, double harvestXp, int levelRequired, int time, int petChance) {
			this.seedId = seedId;
			this.grimyId = grimyId;
			this.plantXp = plantXp;
			this.harvestXp = harvestXp;
			this.levelRequired = levelRequired;
			this.time = time;
			this.petChance = petChance;
		}
		
		public int getPetChance() {
			return petChance;
		}

		public int getSeedId() {
			return seedId;
		}

		public int getGrimyId() {
			return grimyId;
		}

		public double getPlantingXp() {
			return plantXp;
		}

		public double getHarvestingXp() {
			return harvestXp;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getGrowthTime() {
			return time;
		}

		public String getSeedName() {
			return ItemAssistant.getItemName(seedId);
		}

		public String getGrimyName() {
			return ItemAssistant.getItemName(grimyId);
		}
	}

	public static Herb getHerbForSeed(int seedId) {
		for (Herb h : Herb.values())
			if (h.getSeedId() == seedId)
				return h;
		return null;
	}

	public static Herb getHerbForGrimy(int grimyId) {
		for (Herb h : Herb.values())
			if (h.getGrimyId() == grimyId)
				return h;
		return null;
	}

}
