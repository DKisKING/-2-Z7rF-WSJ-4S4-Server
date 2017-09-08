package osv.model.players.skills.woodcutting;

import java.util.Optional;

import osv.Config;
import osv.Server;
import osv.clip.Region;
import osv.clip.WorldObject;
import osv.event.Event;
import osv.model.content.SkillcapePerks;
import osv.model.content.achievement.AchievementType;
import osv.model.content.achievement.Achievements;
import osv.model.content.achievement_diary.desert.DesertDiaryEntry;
import osv.model.content.achievement_diary.falador.FaladorDiaryEntry;
import osv.model.content.achievement_diary.fremennik.FremennikDiaryEntry;
import osv.model.content.achievement_diary.kandarin.KandarinDiaryEntry;
import osv.model.content.achievement_diary.lumbridge_draynor.LumbridgeDraynorDiaryEntry;
import osv.model.content.achievement_diary.varrock.VarrockDiaryEntry;
import osv.model.content.achievement_diary.wilderness.WildernessDiaryEntry;
import osv.model.players.Boundary;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.skills.Skill;
import osv.model.players.skills.firemake.Firemaking;
import osv.util.Misc;
import osv.world.objects.GlobalObject;

public class WoodcuttingEvent extends Event<Player> {
	private Tree tree;
	private Hatchet hatchet;
	private int objectId, x, y, chops;
	
	private int[] lumberjackOutfit = { 10933, 10939, 10940, 10941 };

	public WoodcuttingEvent(Player player, Tree tree, Hatchet hatchet, int objectId, int x, int y) {
		super("skilling", player, 1);
		this.tree = tree;
		this.hatchet = hatchet;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute() {
		double osrsExperience = 0;
		double experience = 0;
		int pieces = 0;
		for (int i = 0; i < lumberjackOutfit.length; i++) {
			if (attachment.getItems().isWearingItem(lumberjackOutfit[i])) {
				pieces+= 2;
			}
		}
		osrsExperience = tree.getExperience() + tree.getExperience() / 10 * pieces;
		experience = tree.getExperience() * Config.WOODCUTTING_EXPERIENCE + tree.getExperience() * Config.WOODCUTTING_EXPERIENCE / 10 * pieces;
		
		if (attachment == null || attachment.disconnected || attachment.getSession() == null) {
			super.stop();
			return;
		}
		if (!attachment.getItems().playerHasItem(hatchet.getItemId()) && !attachment.getItems().isWearingItem(hatchet.getItemId())) {
			attachment.sendMessage("Your axe has dissapeared.");
			super.stop();
			return;
		}
		if (attachment.playerLevel[attachment.playerWoodcutting] < hatchet.getLevelRequired()) {
			attachment.sendMessage("You no longer have the level required to operate this hatchet.");
			super.stop();
			return;
		}
		if (attachment.getItems().freeSlots() == 0) {
			attachment.sendMessage("You have run out of free inventory space.");
			super.stop();
			return;
		}
		if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
			attachment.getInterfaceEvent().execute();
			super.stop();
			return;
		}
		chops++;
		int chopChance = 1 + (int) (tree.getChopsRequired() * hatchet.getChopSpeed());
		if (Misc.random(tree.getChopdownChance()) == 0 || tree.equals(Tree.NORMAL) && Misc.random(chopChance) == 0) {
			int face = 0;
			Optional<WorldObject> worldObject = Region.getWorldObject(objectId, x, y, 0);
			if (worldObject.isPresent()) {
				face = worldObject.get().getFace();
			}
			if (tree.equals(Tree.REDWOOD)) {
				face = attachment.absX < 1569 ? 1 : 3;
			}
			Server.getGlobalObjects().add(new GlobalObject(tree.getStumpId(), x, y, attachment.heightLevel, face, 10, tree.getRespawnTime(), objectId));
			
			attachment.getItems().addItem(tree.getWood(), 1);
			attachment.getPA().addSkillXP((int) (attachment.getRights().isOrInherits(Right.OSRS) ? osrsExperience : experience) , Skill.WOODCUTTING.getId(), true);
			Achievements.increase(attachment, AchievementType.WOODCUT, 1);
			if (Misc.random(tree.getPetChance() / 40) == 10) {
				switch (Misc.random(1)) {
				case 0:
					attachment.getItems().addItem(19712, 1);
					break;
					
				case 1:
					attachment.getItems().addItem(19714, 1);
					break;
				}
				attachment.sendMessage("@blu@You appear to see a clue nest fall from the tree, and pick it up.");
			}
			if (Misc.random(12000) == 5555) {
				attachment.getItems().addItemUnderAnyCircumstance(lumberjackOutfit[Misc.random(lumberjackOutfit.length - 1)], 1);
				attachment.sendMessage("You notice a lumberjack piece falling from the tree and pick it up.");
			}
			if (Misc.random(tree.getPetChance()) / 2 == 10) {
				attachment.getItems().addItemUnderAnyCircumstance(19716, 1);
				attachment.sendMessage("@blu@You appear to see a clue nest fall from the tree, and pick it up.");
			}
			 if (Misc.random(tree.getPetChance()) == 2 && attachment.getItems().getItemCount(13322, false) == 0 && attachment.summonId != 13322) {
				 PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + attachment.playerName + "</col> chopped down the nest for <col=CC0000>Beaver</col> pet!");
				 attachment.getItems().addItemUnderAnyCircumstance(13322, 1);
			 }
			super.stop();
			return;
		}
		if (!tree.equals(Tree.NORMAL)) {
			if (Misc.random(chopChance) == 0 || chops >= tree.getChopsRequired()) {
				chops = 0;
				int random = Misc.random(4);
				attachment.getPA().addSkillXP((int) (attachment.getRights().isOrInherits(Right.OSRS) ? osrsExperience : experience) , Skill.WOODCUTTING.getId(), true);
				Achievements.increase(attachment, AchievementType.WOODCUT, 1);
				if ((attachment.getItems().isWearingItem(13241) || attachment.getItems().playerHasItem(13241)) && random == 2) {
					Firemaking.lightFire(attachment, tree.getWood(), "infernal_axe");
					return;
				}
				switch (tree) {
				case MAGIC:
					if (Boundary.isIn(attachment, Boundary.AL_KHARID_BOUNDARY)) {
						attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_MAGIC_AL);
					}
					if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA_BOUNDARY)) {
						attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MAGIC_LOG_WILD);
					}
					if (Boundary.isIn(attachment, Boundary.SEERS_BOUNDARY)) {
						attachment.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.CUT_MAGIC_SEERS);
					}
					break;
				case MAPLE:
					break;
				case NORMAL:
					break;
				case OAK:
					if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
						attachment.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.CHOP_OAK_FREM);
					}
					break;
				case WILLOW:
					if (Boundary.isIn(attachment, Boundary.FALADOR_BOUNDARY)) {
						attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.CHOP_WILLOW);
					}
					if (Boundary.isIn(attachment, Boundary.DRAYNOR_BOUNDARY)) {
						attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_WILLOW_DRAY);
					}
					break;
				case YEW:
					if (Boundary.isIn(attachment, Boundary.FALADOR_BOUNDARY)) {
						attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.CHOP_YEW);
					}
					if (Boundary.isIn(attachment, Boundary.VARROCK_BOUNDARY)) {
						attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.YEWS_AND_BURN);
					}
					break;
				case TEAK:
					if (Boundary.isIn(attachment, Boundary.DESERT_BOUNDARY)) {
						attachment.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.CHOP_TEAK);
					}
					break;
				default:
					break;
				
				}
				if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA)) {
					if (Misc.random(22) == 5) {
						int randomAmount = Misc.random(5) + 1;
						attachment.sendMessage("You received x" + randomAmount + " blood money while fishing!");
						attachment.getItems().addItem(13307, randomAmount);
					}
				}
				attachment.getItems().addItem(tree.getWood(), SkillcapePerks.WOODCUTTING.isWearing(attachment) || (SkillcapePerks.isWearingMaxCape(attachment) && attachment.getWoodcuttingEffect()) && Misc.random(2) == 1 ? 2 : 1);
			}
		}
		if (super.getElapsedTicks() % 4 == 0) {
			attachment.startAnimation(hatchet.getAnimation());
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment != null) {
			attachment.startAnimation(65535);
		}
	}

}
