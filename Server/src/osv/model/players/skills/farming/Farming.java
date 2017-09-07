package osv.model.players.skills.farming;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import osv.Config;
import osv.Server;
import osv.event.CycleEvent;
import osv.event.CycleEventContainer;
import osv.event.CycleEventHandler;
import osv.model.content.SkillcapePerks;
import osv.model.content.achievement.AchievementType;
import osv.model.content.achievement.Achievements;
import osv.model.content.achievement_diary.falador.FaladorDiaryEntry;
import osv.model.entity.HealthStatus;
import osv.model.players.Boundary;
import osv.model.players.ClientGameTimer;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.mode.ModeType;
import osv.model.players.skills.Skill;
import osv.util.Misc;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 27, 2013
 */

public class Farming {
	
	public static int[] farmersOutfit = { 13640, 13642, 13644, 13646 };

	public static final int MAX_PATCHES = 1;

	private Player player;

	private int weeds;

	private long lastPoisonBerryFarm;

	public Farming(Player player) {
		this.player = player;
	}
	
	private boolean hasMagicSecateurs() {
		return player.getItems().playerHasItem(7409) || player.getItems().isWearingItem(7409, 3) || SkillcapePerks.FARMING.isWearing(player) || SkillcapePerks.isWearingMaxCape(player);
	}

	public void fillCompostBucket() {
		player.getSkilling().stop();
		player.getSkilling().setSkill(Skill.FARMING);
		if (!player.getItems().playerHasItem(995, 250)) {
			player.getDH().sendDialogues(662, 3257);
			return;
		}
		if (!player.getItems().playerHasItem(1925)) {
			player.getDH().sendDialogues(663, 3257);
			return;
		}
		Server.getEventHandler().submit(new FarmingCompostEvent(player, 3));
	}

	public void patchObjectInteraction(final int objectId, final int itemId, final int x, final int y) {
		
		/**
		 * Skilling outfit pieces
		 */
		int pieces = 0;
		for (int i = 0; i < farmersOutfit.length; i++) {
			if (player.getItems().isWearingItem(farmersOutfit[i])) {
				pieces++;
			}
		}
		
		Patch patch = Patch.get(x, y);
		if (patch == null)
			return;
		final int id = patch.getId();
		player.turnPlayerTo(x, y);
		if (objectId == FarmingConstants.GRASS_OBJECT || objectId == FarmingConstants.HERB_PATCH_DEPLETED) {
			if (player.getFarmingState(id) < State.RAKED.getId()) {
				if (!player.getItems().playerHasItem(FarmingConstants.RAKE, 1))
					player.sendMessage("You need to rake this patch to remove all the weeds.");
				else if (itemId == FarmingConstants.RAKE || player.getItems().playerHasItem(FarmingConstants.RAKE)) {
					player.startAnimation(FarmingConstants.RAKING_ANIM);
					player.turnPlayerTo(x, y);
					if (weeds <= 0)
						weeds = 3;
					CycleEventHandler.getSingleton().stopEvents(this);
					CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player == null) {
								container.stop();
								return;
							}
							if (weeds > 0) {
								weeds--;
								player.turnPlayerTo(x, y);
								player.getItems().addItem(6055, 1);
								player.startAnimation(FarmingConstants.RAKING_ANIM);
							} else if (weeds == 0) {
								player.setFarmingState(id, State.RAKED.getId());
								player.sendMessage("You raked the patch of all it's weeds, now the patch is ready for compost.", 255);
								player.startAnimation(65535);
								updateObjects();
								container.stop();
							}
						}

						@Override
						public void stop() {

						}

					}, 3);
				}
			} else if (player.getFarmingState(id) >= State.RAKED.getId() && player.getFarmingState(id) < State.COMPOST.getId()) {
				if (!player.getItems().playerHasItem(FarmingConstants.COMPOST, 1))
					player.sendMessage("You need to put compost on this to enrich the soil.");
				else if (itemId == FarmingConstants.COMPOST || player.getItems().playerHasItem(FarmingConstants.COMPOST) && itemId == -1) {
					player.turnPlayerTo(x, y);
					player.startAnimation(FarmingConstants.PUTTING_COMPOST);
					player.getItems().deleteItem2(FarmingConstants.COMPOST, 1);
					player.getItems().addItem(1925, 1);
					player.setFarmingState(id, State.COMPOST.getId());
					player.sendMessage("You put compost on the soil, it is now time to seed it.");
				}
			} else if (player.getFarmingState(id) >= State.COMPOST.getId() && player.getFarmingState(id) < State.SEEDED.getId()) {
				if (!player.getItems().playerHasItem(FarmingConstants.SEED_DIBBER, 1)) {
					player.sendMessage("You need to use a seed dibber with a seed on this patch.");
					return;
				}
				final FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(itemId);
				if (herb == null) {
					player.sendMessage("You must use an appropriate seed on the patch at this stage.");
					return;
				}
				if (player.getLevelForXP(player.playerXP[19]) < herb.getLevelRequired()) {
					player.sendMessage("You need a farming level of " + herb.getLevelRequired() + " to grow " + herb.getSeedName().replaceAll(" seed", "") + ".");
					return;
				}
				if (itemId == herb.getSeedId() && player.getItems().playerHasItem(FarmingConstants.SEED_DIBBER)) {
					player.turnPlayerTo(x, y);
					player.startAnimation(FarmingConstants.SEED_DIBBING);
					CycleEventHandler.getSingleton().stopEvents(this);
					/**
					 * Calculate experience
					 */
					double osrsExperience = herb.getPlantingXp() + herb.getPlantingXp() / 20 * pieces;
					double regExperience = herb.getPlantingXp() * (Config.FARMING_EXPERIENCE + (hasMagicSecateurs() ? 1 : 0)) + herb.getPlantingXp() * Config.FARMING_EXPERIENCE / 20 * pieces;
					System.out.println("Plant xp: " + herb.getPlantingXp() * (Config.FARMING_EXPERIENCE + (hasMagicSecateurs() ? 5 : 0)) + herb.getPlantingXp() + ", Pieces: " + pieces);
					CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {

						@Override
						public void execute(CycleEventContainer container) {
							if (player == null || player.disconnected) {
								container.stop();
								return;
							}
							if (!player.getItems().playerHasItem(herb.getSeedId()))
								return;
							player.getItems().deleteItem2(herb.getSeedId(), 1);
							player.setFarmingState(id, State.SEEDED.getId());
							player.setFarmingSeedId(id, herb.getSeedId());
							player.setFarmingTime(id, hasMagicSecateurs() ? herb.getGrowthTime() / 2 : herb.getGrowthTime());
							player.setFarmingHarvest(id, 3 + Misc.random(hasMagicSecateurs() ? 7 : 4));
							player.getPA().addSkillXP((int) (player.getMode().getType().equals(ModeType.OSRS) ? osrsExperience : regExperience), 19, true);
							player.sendMessage("You dib a seed into the soil, it is now time to water it.");
							updateObjects();
							container.stop();
						}

						@Override
						public void stop() {
						}

					}, 3);
				}
			}
		} else if (objectId == FarmingConstants.HERB_OBJECT) {
			boolean wateringCans = IntStream.of(FarmingConstants.WATERING_CAN).anyMatch(identification -> identification == itemId);
			boolean hasWateringCan = IntStream.of(FarmingConstants.WATERING_CAN).anyMatch(identification -> player.getItems().playerHasItem(identification));
			if (player.getFarmingState(id) >= State.SEEDED.getId() && player.getFarmingState(id) < State.GROWTH.getId()) {
				if (!hasWateringCan)
					player.sendMessage("You need to water the herb before you can harvest it.");
				else if (wateringCans || hasWateringCan && itemId == -1) {
					int time = (int) Math.round(player.getFarmingTime(id) * .6);
					player.turnPlayerTo(x, y);
					player.startAnimation(FarmingConstants.WATERING_CAN_ANIM);
					player.setFarmingState(id, State.GROWTH.getId());
					player.getItems().replaceItem(player, itemId, itemId == 5333 ? 5331 : itemId - 1);
					player.sendMessage("You water the herb, wait " + Math.round(player.getFarmingTime(id) * .6) + " seconds for the herb to mature.");
					player.getPA().sendGameTimer(ClientGameTimer.FARMING, TimeUnit.SECONDS, time);
					return;
				}
			}
			if (player.getFarmingState(id) == State.GROWTH.getId()) {
				if (player.getFarmingTime(id) > 0) {
					player.sendMessage("You need to wait another " + Math.round(player.getFarmingTime(id) * .6) + " seconds until the herb is mature.");
					return;
				}
			}
			if (player.getFarmingState(id) == State.HARVEST.getId()) {
				if (player.getItems().freeSlots() < 1) {
					player.getDH().sendStatement("You need atleast 1 free space to harvest some herbs.");
					player.nextChat = -1;
					return;
				}
				if (player.getFarmingHarvest(id) == 0 || player.getFarmingState(id) != State.HARVEST.getId()) {
					resetValues(id);
					updateObjects();
					return;
				}
				final FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(player.getFarmingSeedId(id));
				
				/**
				 * Experience calculation
				 */
				double osrsHarvestExperience = herb.getHarvestingXp() + herb.getHarvestingXp() / 5 * pieces;
				double regHarvestExperience = herb.getHarvestingXp() * Config.FARMING_EXPERIENCE + herb.getHarvestingXp() * Config.FARMING_EXPERIENCE / 5 * pieces;
				System.out.println("Harvest xp: " + herb.getHarvestingXp() * Config.FARMING_EXPERIENCE + herb.getHarvestingXp() + ", Pieces: " + pieces);
				if (herb != null) {
					CycleEventHandler.getSingleton().stopEvents(this);
					CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {

						@Override
						public void execute(CycleEventContainer container) {
							if (player == null || player.disconnected) {
								container.stop();
								return;
							}
							if (player.getItems().freeSlots() < 1) {
								player.getDH().sendStatement("You need atleast 1 free space to harvest some herbs.");
								player.nextChat = -1;
								player.startAnimation(65535);
								container.stop();
								return;
							}
							if (player.getFarmingHarvest(id) <= 0) {
								player.sendMessage("The herb patch has completely depleted...", 600000);
								Achievements.increase(player, AchievementType.FARM, 1);
								player.startAnimation(65535);
								resetValues(id);
								updateObjects();
								container.stop();
								return;
							}
							switch (herb) {
							case AVANTOE:
								break;
							case CADANTINE:
								break;
							case DRAWF_WEED:
								break;
							case GUAM:
								break;
							case HARRALANDER:
								break;
							case IRIT:
								break;
							case KWUARM:
								break;
							case LANTADYME:
								break;
							case MARRENTIL:
								break;
							case RANARR:
								break;
							case SNAP_DRAGON:
								break;
							case TARROMIN:
								break;
							case TOADFLAX:
								break;
							case TORSTOL:
								if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
									player.getDiaryManager().getFaladorDiary()
											.progress(FaladorDiaryEntry.HARVEST_TORSTOL);
								}
								break;
							default:
								break;
							
							}
							 if (Misc.random(herb.getPetChance()) == 20 && player.getItems().getItemCount(20661, false) == 0 && player.summonId != 20661) {
								 PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + player.playerName + "</col> harvested some crops and found <col=CC0000>Tangleroot</col> pet!");
								 player.getItems().addItemUnderAnyCircumstance(20661, 1);
							 }
							player.startAnimation(FarmingConstants.PICKING_HERB_ANIM);
							player.setFarmingHarvest(id, player.getFarmingHarvest(id) - 1);
							player.getItems().addItem(herb.getGrimyId(), 1);
							player.getPA().addSkillXP((int) (player.getMode().getType().equals(ModeType.OSRS) ? osrsHarvestExperience : regHarvestExperience), 19, true);
						}

						@Override
						public void stop() {
						}

					}, 3);
				}
			}
		}
	}

	public void farmPoisonBerry() {
		if (System.currentTimeMillis() - lastPoisonBerryFarm < TimeUnit.MINUTES.toMillis(5)) {
			player.sendMessage("You can only pick berries from this bush every 5 minutes.");
			return;
		}
		int level = player.playerLevel[Skill.FARMING.getId()];
		if (level < 70) {
			player.sendMessage("You need a farming level of 70 to get this.");
			return;
		}
		if (player.getItems().freeSlots() < (hasMagicSecateurs() ? 2 : 1)) {
			player.sendMessage("You need at least " + (hasMagicSecateurs() ? 2 : 1) + " free slot " + (hasMagicSecateurs() ? "s" : "") + " to do this.");
			return;
		}
		int maximum = player.getLevelForXP(player.playerXP[Skill.FARMING.getId()]);
		if (Misc.random(100) < (10 + (maximum - level))) {
			player.getHealth().proposeStatus(HealthStatus.POISON, 6, Optional.empty());
		}
		if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
			player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICK_POSION_BERRY);
		}
		player.startAnimation(881);
		lastPoisonBerryFarm = System.currentTimeMillis();
		player.getItems().addItem(6018, hasMagicSecateurs() ? 2 : 1);
		player.getPA().addSkillXP(player.getMode().getType().equals(ModeType.OSRS) ? 50 : 2_000, Skill.FARMING.getId(), true);

	}

	public void farmingProcess() {
		for (int i = 0; i < Farming.MAX_PATCHES; i++) {
			if (player.getFarmingTime(i) > 0 && player.getFarmingState(i) == Farming.State.GROWTH.getId()) {
				player.setFarmingTime(i, player.getFarmingTime(i) - 1);
				if (player.getFarmingTime(i) == 0) {
					FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(player.getFarmingSeedId(i));
					if (herb != null)
						player.sendMessage("Your farming patch of " + herb.getSeedName().replaceAll(" seed", "") + " is ready to be harvested.", 255);
					player.setFarmingState(i, Farming.State.HARVEST.getId());
				}
			}
		}
	}

	public void resetValues(int id) {
		player.setFarmingHarvest(id, 0);
		player.setFarmingSeedId(id, 0);
		player.setFarmingState(id, 0);
		player.setFarmingTime(id, 0);
	}

	public void updateObjects() {
		for (int i = 0; i < Farming.MAX_PATCHES; i++) {
			Patch patch = Patch.get(i);
			if (patch == null)
				continue;
			if (player.distanceToPoint(patch.getX(), patch.getY()) > 60)
				continue;
			if (player.getFarmingState(i) < State.RAKED.getId()) {
				player.getPA().checkObjectSpawn(FarmingConstants.GRASS_OBJECT, patch.getX(), patch.getY(), 0, 10);
			} else if (player.getFarmingState(i) >= State.RAKED.getId() && player.getFarmingState(i) < State.SEEDED.getId()) {
				player.getPA().checkObjectSpawn(FarmingConstants.HERB_PATCH_DEPLETED, patch.getX(), patch.getY(), 0, 10);
			} else if (player.getFarmingState(i) >= State.SEEDED.getId()) {
				player.getPA().checkObjectSpawn(FarmingConstants.HERB_OBJECT, patch.getX(), patch.getY(), 0, 10);
			}
		}
	}

	public boolean isHarvestable(int id) {
		return player.getFarmingState(id) == State.HARVEST.getId();
	}

	public long getLastBerryFarm() {
		return lastPoisonBerryFarm;
	}

	public void setLastBerryFarm(long millis) {
		this.lastPoisonBerryFarm = millis;
	}

	public enum State {
		NONE(0), RAKED(1), COMPOST(2), SEEDED(3), WATERED(4), GROWTH(5), HARVEST(6);

		private int id;

		State(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	static enum Patch {
		FALADOR_PARK(0, 3003, 3372);

		int id, x, y;

		Patch(int id, int x, int y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}

		public int getId() {
			return this.id;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		static List<Patch> patches = new ArrayList<>();

		static {
			for (Patch patch : Patch.values())
				patches.add(patch);
		}

		public static Patch get(int x, int y) {
			for (Patch patch : patches)
				if (patch.getX() == x && patch.getY() == y)
					return patch;
			return null;
		}

		public static Patch get(int id) {
			for (Patch patch : patches)
				if (patch.getId() == id)
					return patch;
			return null;
		}
	}
}
