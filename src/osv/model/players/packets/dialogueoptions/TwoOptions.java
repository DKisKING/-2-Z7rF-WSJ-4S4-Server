package osv.model.players.packets.dialogueoptions;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.text.WordUtils;

import osv.Config;
import osv.Server;
import osv.model.content.LootValue;
import osv.model.content.SkillcapePerks;
import osv.model.content.achievement_diary.varrock.VarrockDiaryEntry;
import osv.model.content.achievement_diary.western_provinces.WesternDiaryEntry;
import osv.model.content.achievement_diary.wilderness.WildernessDiaryEntry;
import osv.model.content.barrows.RoomLocation;
import osv.model.items.GameItem;
import osv.model.items.ItemAssistant;
import osv.model.items.ItemCombination;
import osv.model.minigames.bounty_hunter.BountyHunterEmblem;
import osv.model.players.Player;
import osv.model.players.PlayerAssistant.PointExchange;
import osv.model.players.combat.Degrade;
import osv.model.players.combat.pkdistrict.District;
import osv.model.players.Right;
import osv.model.players.skills.crafting.SpinMaterial;
import osv.model.players.skills.herblore.UnfCreator;
import osv.model.players.skills.slayer.SlayerMaster;
import osv.util.Misc;

/*
 * @author Matt
 * Two Option Dialogue actions
 */

public class TwoOptions {
	/*
	 * Handles all first options on 'Two option' dialogues.
	 */
	public static void handleOption1(Player c) {

		switch (c.dialogueAction) {
		case 811:
			GameItem item = new GameItem(c.unfPotHerb);
			UnfCreator.makeUnfinishedPotion(c, item);
			break;
			
		case 813:
			District.stage(c, c.inClanWars() || c.inClanWarsSafe() ? "end" : "start");
			c.getPA().closeAllWindows();
			break;
			
		case 819:
			c.getDH().sendDialogues(820, 822);
			break;
		
		case 703:
			c.getDH().sendDialogues(704, 822);
			break;
		case 705:
			c.getDH().sendDialogues(706, 822);
			break;
		case 550:
			c.getDH().sendDialogues(539, c.npcType);
			break;
		case 64: //Buy a kittem
			if (c.getDiaryManager().getVarrockDiary().hasDone(VarrockDiaryEntry.PURCHASE_KITTEN)) {
				c.sendMessage("You cannot purchase another kitten.");
				return;
			}
			int[] kittens = { 1555, 1556, 1557, 1558, 1559, 1560 };
			int kitten = Misc.random(kittens.length - 1);
			if (c.getItems().playerHasItem(995, 15000)) {
				c.getItems().deleteItem(995, 15000);
				c.getItems().addItem(kittens[kitten], 1);
				c.sendMessage("You've successfully purchased a kitten!");
				c.getPA().removeAllWindows();
				c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.PURCHASE_KITTEN);
			}
			break;
			
		case 65:
			c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.GRAND_TREE_TELEPORT);
			switch (c.getDH().tree) {
			case "village":
				c.getPA().startTeleport(2461, 3444, 0, "modern"); //Stronghold
				c.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.SPIRIT_TREE_WEST);
				break;
				
			case "stronghold":
				c.getPA().startTeleport(2542, 3169, 0, "modern"); //Village
				break;
				
			case "grand_exchange":
				c.getPA().startTeleport(2542, 3169, 0, "modern"); //Village
				break;
			}
			break;
		
		case 72: //Attempt for pet
			c.getFightCave().gamble();
			break;
			
		case 75: //Remove bigger boss tasks
			c.getSlayer().setBiggerBossTasks(false);
			c.sendMessage("You will no longer get extended boss tasks.");
			c.getPA().removeAllWindows();
			break;
			
		case 10:
			c.getOutStream().createFrame(27);
			c.tablet = 1;
			break;

		case 40:
			c.sendMessage("Bowstring");
			c.getPA().removeAllWindows();
			SpinMaterial.getInstance().spin(c, SpinMaterial.Material.FLAX.getRequiredItem());
			break;

		case 80:
			if (!c.getItems().playerHasItem(8839))
				return;
			if (c.pcPoints < 200) {
				c.sendMessage("You do not have the pest control points to complete this upgrade.");
				return;
			}
			if (c.getItems().freeSlots() == 0) {
				c.sendMessage("You need at least one free slot to purchase this item reward.");
				return;
			}
			c.pcPoints -= 200;
			c.getItems().replaceItem(c, 8839, 13072);
			c.getDH().sendDialogues(81, -1);
			c.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.UPGRADE_VOID);
			c.sendMessage("You have received a Elite Void Top in exchange for 200 pc points.");
			break;

		case 81:
			if (!c.getItems().playerHasItem(8840))
				return;
			if (c.pcPoints < 200) {
				c.sendMessage("You do not have the pest control points to complete this upgrade.");
				return;
			}
			if (c.getItems().freeSlots() == 0) {
				c.sendMessage("You need at least one free slot to purchase this item reward.");
				return;
			}
			c.pcPoints -= 200;
			c.getItems().replaceItem(c, 8840, 13073);
			c.getDH().sendDialogues(82, -1);
			c.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.UPGRADE_VOID);
			c.sendMessage("You have received a Elite Void Robe in exchange for 200 pc points.");
			break;
			
		case 11824:
			if (!c.getItems().playerHasItem(11824))
				return;
			if (!c.getItems().playerHasItem(995, 10_000_000)) {
				c.sendMessage("You do not have 10M Coins to do this.");
				return;
			}
			c.getItems().deleteItem(11824, 1);
			c.getItems().deleteItem(995, 10_000_000);
			c.getItems().addItem(11889, 1);
			c.getDH().sendItemStatement("The Guildmaster created a Zamorakian Hasta.", 11889);
			c.nextChat = -1;
			break;
			
		case 11889:
			if (!c.getItems().playerHasItem(11889))
				return;
			if (!c.getItems().playerHasItem(995, 5_000_000)) {
				c.sendMessage("You do not have 5M Coins to do this.");
				return;
			}
			c.getItems().deleteItem(11889, 1);
			c.getItems().deleteItem(995, 5_000_000);
			c.getItems().addItem(11824, 1);
			c.getDH().sendItemStatement("The Guildmaster created a Zamorakian Speark.", 11824);
			c.nextChat = -1;
			break;
			
		case 94:
			c.getDH().sendDialogues(97, c.npcType);
			break;
			
		case 103:
			SkillcapePerks.purchaseMaxCape(c);
			break;
			
		case 1391:
			c.getDH().sendDialogues(1400, c.npcType);
			break;
			
		case 1392:
			c.getDH().sendDialogues(1393, c.npcType);
			break;
			
		case 66:
			c.getPA().closeAllWindows();
			Degrade.repairCrystalBow(c, 4207);
			break;
			
		case 68:
			c.dropRateInKills = true;
			Server.getDropManager().open(c);
			c.sendMessage("Now viewing drop-rates in 1/kills form");
			break;
		}
		if (c.dialogueAction == 29) {
			c.dialogueAction = -1;
			c.getPA().movePlayer(RoomLocation.getRandomSpawn());
			c.getPA().removeAllWindows();
			return;
		}
		if (c.dialogueAction == 132) {
			c.getDH().sendDialogues(655, 311);
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 133) {
			c.getDH().sendDialogues(656, 311);
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 130) {
			if (c.talkingNpc == 5870 && c.getCerberusLostItems().size() > 0) {
				c.getCerberusLostItems().retain();
			} else {
				if (c.getZulrahLostItems().size() > 0) {
					c.getZulrahLostItems().retain();
				}
			}
			return;
		}
		if (c.dialogueAction == 127) {
			int price = c.getRechargeItems().hasItem(13109) ? 40_000 : c.getRechargeItems().hasItem(13110) ? 25_000 : c.getRechargeItems().hasItem(13111) ? -1 : 50_000;
			if (c.absX == 3184 && c.absY == 3945) {
				if (c.getItems().playerHasItem(995, price)) {
					c.getPA().movePlayer(3184, 3944, 0);
					c.getItems().deleteItem2(995, price);
					c.getPA().removeAllWindows();
				} else {
					c.getDH().sendStatement("You need at least 50,000 gp to enter this area.");
				}
			}
			c.dialogueAction = -1;
			c.nextChat = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 947) {
			c.getShops().openShop(113);
			c.dialogueAction = -1;
		}
		if (c.dialogueAction == 125) {
			if (c.getItems().playerHasItem(8851, 200)) {
				c.getPA().movePlayer(2847, 3540, 2);
				c.getPA().removeAllWindows();
				c.getWarriorsGuild().cycle();
			} else {
				c.getDH().sendNpcChat2("You need atleast 200 warrior guild tokens.", "You can get some by operating the armour animator.", 4289, "Kamfreena");
				c.nextChat = 0;
			}
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 124) {
			if (!c.getPA().canTeleport("")) {
				c.getPA().removeAllWindows();
				return;
			}
			c.getZulrahEvent().initialize();
			return;
		}
		if (c.dialogueAction == 120) {
			if (c.getItemOnPlayer() == null) {
				return;
			}
			if (!c.getItems().playerHasItem(962)) {
				return;
			}
			if (c.getItemOnPlayer().getItems().freeSlots() < 1) {
				c.sendMessage("The other player must have at least 1 free slot.");
				return;
			}
			int[] partyHats = { 1038, 1040, 1042, 1044, 1046, 1048 };
			int hat = partyHats[Misc.random(partyHats.length - 1)];
			Player winner = Misc.random(1) == 0 ? c : c.getItemOnPlayer();
			Player loser = winner == c ? c.getItemOnPlayer() : c;
			if (Objects.equals(winner, loser)) {
				return;
			}
			c.getPA().closeAllWindows();
			loser.turnPlayerTo(winner.getX(), winner.getY());
			winner.turnPlayerTo(loser.getX(), loser.getY());
			winner.startAnimation(881);
			loser.startAnimation(881);
			c.getItems().deleteItem(962, 1);
			winner.getItems().addItem(hat, 1);
			loser.getItems().addItem(2996, 200);
			winner.sendMessage("You have received a " + ItemAssistant.getItemName(hat) + " from the christmas cracker.");
			loser.sendMessage("Awee you didn't get the partyhat. You received 200 pk tickets as consolation.");
		}
		if (c.dialogueAction == -1 && c.getCurrentCombination().isPresent()) {
			ItemCombination combination = c.getCurrentCombination().get();
			if (combination.isCombinable(c)) {
				combination.combine(c);
			} else {
				c.getDH().sendStatement("You don't have all the items you need for this combination.");
				c.nextChat = -1;
				c.setCurrentCombination(Optional.empty());
			}
			return;
		}
		if (c.dialogueAction == 555) {
			return;
		}
		if (c.dialogueAction == 3308) {
			Optional<SlayerMaster> master_npc = SlayerMaster.get(c.talkingNpc);
			if (c.getSlayer().getMaster() != master_npc.get().getId() && master_npc.get().getId() != 401) {
				c.getDH().sendNpcChat("You already seem to have an active task with someone else.");
				return;
			}
			c.getSlayer().createNewTask(c.talkingNpc);
		}
		if (c.dialogueAction == 100) {
			c.getPoints().giveReward();
		}
		if (c.dialogueAction == 115) {
			if (c.getItems().playerHasItem(12526) && c.getItems().playerHasItem(6585)) {
				c.getItems().deleteItem2(12526, 1);
				c.getItems().deleteItem2(6585, 1);
				c.getItems().addItem(12436, 1);
				c.getDH().sendDialogues(582, -1);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 114) {
			c.getDH().sendDialogues(579, -1);
			return;
		}
		if (c.dialogueAction == 110) {
			if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12757)) {
				c.getItems().deleteItem2(11235, 1);
				c.getItems().deleteItem2(12757, 1);
				c.getItems().addItem(12766, 1);
				c.getDH().sendDialogues(568, 315);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 111) {
			if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12759)) {
				c.getItems().deleteItem2(11235, 1);
				c.getItems().deleteItem2(12759, 1);
				c.getItems().addItem(12765, 1);
				c.getDH().sendDialogues(571, 315);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 112) {
			if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12761)) {
				c.getItems().deleteItem2(11235, 1);
				c.getItems().deleteItem2(12761, 1);
				c.getItems().addItem(12767, 1);
				c.getDH().sendDialogues(574, 315);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 113) {
			if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12763)) {
				c.getItems().deleteItem2(11235, 1);
				c.getItems().deleteItem2(12763, 1);
				c.getItems().addItem(12768, 1);
				c.getDH().sendDialogues(577, 315);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 109) {
			if (c.getItems().playerHasItem(4153) && c.getItems().playerHasItem(12849)) {
				c.getItems().deleteItem2(4153, 1);
				c.getItems().deleteItem2(12849, 1);
				c.getItems().addItem(12848, 1);
				c.getDH().sendDialogues(565, 315);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 108) {
			if (c.getItems().playerHasItem(11924) && c.getItems().playerHasItem(12802)) {
				c.getItems().deleteItem2(11924, 1);
				c.getItems().deleteItem2(12802, 1);
				c.getItems().addItem(12806, 1);
				c.getDH().sendDialogues(560, 315);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 107) {
			if (c.getItems().playerHasItem(11926) && c.getItems().playerHasItem(12802)) {
				c.getItems().deleteItem2(11926, 1);
				c.getItems().deleteItem2(12802, 1);
				c.getItems().addItem(12807, 1);
				c.getDH().sendDialogues(560, 315);
			} else {
				c.getPA().removeAllWindows();
			}
		}
		if (c.dialogueAction == 106) {
			int worth = c.getBH().getNetworthForEmblems();
			long total = (long) worth + c.getBH().getBounties();
			if (total > Integer.MAX_VALUE) {
				c.sendMessage("You have to spend some bounties before obtaining any more.");
				c.getPA().removeAllWindows();
				c.nextChat = -1;
				return;
			}
			if (worth > 0) {
				BountyHunterEmblem.EMBLEMS.forEach(emblem -> c.getItems().deleteItem2(emblem.getItemId(), c.getItems().getItemAmount(emblem.getItemId())));
				c.getBH().setBounties(c.getBH().getBounties() + worth);
				c.sendMessage("You sold all of the emblems in your inventory for " + Misc.insertCommas(Integer.toString(worth)) + " bounties.");
				c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MYSTERIOUS_EMBLEM);
				c.getDH().sendDialogues(557, 315);
			} else {
				c.nextChat = -1;
				c.getPA().closeAllWindows();
			}
			return;
		}
		if (c.dialogueAction == 105) {
			if (c.getItems().playerHasItem(12804) && c.getItems().playerHasItem(11838)) {
				c.getItems().deleteItem2(12804, 1);
				c.getItems().deleteItem2(11838, 1);
				c.getItems().addItem(12809, 1);
				c.getDH().sendDialogues(552, -1);
			} else {
				c.getPA().removeAllWindows();
			}
			c.dialogueAction = -1;
			c.nextChat = -1;
			return;
		}
		if (c.dialogueAction == 104) {
			c.getDH().sendDialogues(549, 315);
			c.dialogueAction = -1;
			return;
		}
		if (c.dialogueAction == 101) {
			c.getDH().sendDialogues(546, 315);
			c.dialogueAction = -1;
			return;
		}
		if (c.dialogueAction == 102) {
			c.getDH().sendDialogues(547, 315);
			c.dialogueAction = -1;
			return;
		}
		if (c.dialogueAction == 200) {
			c.getPA().exchangeItems(PointExchange.PK_POINTS, 2996, 1);
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		} else if (c.dialogueAction == 201) {
			c.getDH().sendDialogues(503, -1);
			return;
		} else if (c.dialogueAction == 202) {
			c.getPA().exchangeItems(PointExchange.VOTE_POINTS, 1464, 1);
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 2258) {
			c.getPA().startTeleport(3039, 4834, 0, "modern"); // first click
			// teleports
			// second
			// click
			// open
			// shops
		}
//		if (c.dialogueAction == 12000) {
//			for (int i = 8144; i < 8195; i++) {
//				c.getPA().sendFrame126("", i);
//			}
//			long milliseconds = (long) c.playTime * 600;
//			long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
//			long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
//			long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours));
//			long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
//			String time = days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds.";
//
//			c.getPA().sendFrame126("@dre@Account Information for @blu@" + c.playerName + "", 8144);
//			c.getPA().sendFrame126("", 8145);
//			c.getPA().sendFrame126("@blu@Donator Points@bla@ - " + c.donatorPoints + "", 8150);
//			c.getPA().sendFrame126("@blu@Vote Points@bla@ - " + c.votePoints + "", 8149);
//			c.getPA().sendFrame126("@blu@Amount Donated@bla@ - " + c.amDonated + "", 8151);
//			c.getPA().sendFrame126("@blu@PC Points@bla@ - " + c.pcPoints + "", 8152);
//			c.getPA().sendFrame126("@blu@Time Played: @bla@" + time, 8153);
//			c.getPA().sendFrame126("@blu@Slayer points: @bla@" + c.getSlayer().getPoints(), 8154);
//			c.getPA().sendFrame126("@blu@Mage arena points: @bla@" + c.getArenaPoints(), 8155);
//			c.getPA().sendFrame126("@blu@Consecutive tasks: @bla@" + c.getSlayer().getConsecutiveTasks(), 8156);
//			c.getPA().showInterface(8134);
//		}
		if (c.dialogueAction == 4000) {
			if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2697, 1)) {
				if (c.getRights().isOrInherits(Right.CONTRIBUTOR)) {
					c.getDH().sendStatement("You cannot read this scroll as you are already a contributor or higher.");
					return;
				}
				c.getItems().deleteItem(2697, 1);
				c.gfx100(263);
				c.getRights().add(Right.CONTRIBUTOR);
				if (c.getRights().getPrimary().isOrInherits(Right.PLAYER)) {
					c.getRights().setPrimary(Right.CONTRIBUTOR);
				}
				c.sendMessage("You are now a contributor. You must relog for changes to take effect!");
				c.getPA().closeAllWindows();
			}
		}
		if (c.dialogueAction == 4001) {
			if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2698, 1)) {
				if (c.getRights().isOrInherits(Right.SPONSOR)) {
					c.getDH().sendStatement("You cannot read this scroll as you are already a Sponsor or higher.");
					return;
				}
				c.getItems().deleteItem(2698, 1);
				c.gfx100(263);
				c.getRights().add(Right.SPONSOR);
				if (c.getRights().getPrimary().isOrInherits(Right.PLAYER) || c.getRights().getPrimary().isOrInherits(Right.CONTRIBUTOR)) {
					c.getRights().setPrimary(Right.SPONSOR);
				}
				c.getRights().updatePrimary();
				c.sendMessage("You are now a Sponsor. You must relog for changes to take effect!");
				c.getPA().closeAllWindows();
			}
		}
		if (c.dialogueAction == 4002) {
			if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2699, 1)) {
				if (c.getRights().isOrInherits(Right.SUPPORTER)) {
					c.getDH().sendStatement("You cannot read this scroll as you are already a Supporter.");
					return;
				}
				c.getItems().deleteItem(2699, 1);
				c.gfx100(263);
				c.getRights().add(Right.SUPPORTER);
				if (c.getRights().getPrimary().isOrInherits(Right.PLAYER) || c.getRights().getPrimary().isOrInherits(Right.CONTRIBUTOR)) {
					c.getRights().setPrimary(Right.SUPPORTER);
				}
				c.sendMessage("You are now a Supporter. You must relog for changes to take effect!");
				c.getPA().closeAllWindows();
			}
		}
		if (c.dialogueAction == 4003) {
			if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2700, 1)) {
				if (c.getRights().isOrInherits(Right.VIP)) {
					c.getDH().sendStatement("You cannot read this scroll as you are already a VIP or higher.");
					return;
				}
				c.getItems().deleteItem(2700, 1);
				c.gfx100(263);
				c.getRights().add(Right.VIP);
				if (c.getRights().getPrimary().isOrInherits(Right.PLAYER) || c.getRights().getPrimary().isOrInherits(Right.CONTRIBUTOR)) {
					c.getRights().setPrimary(Right.VIP);
				}
				c.sendMessage("You are now a VIP. You must relog for changes to take effect!");
				c.getPA().closeAllWindows();
			}
		}
		if (c.dialogueAction == 4004) {
			if (c.inWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getMode().isIronman() || c.getMode().isUltimateIronman()) {
				c.sendMessage("You are not allowed to do this on your game mode.");
				return;
			}
			if (c.getItems().playerHasItem(2701, 1)) {
				c.getItems().deleteItem(2701, 1);
				c.gfx100(263);
				c.playerTitle = "Gambler";
				c.getItems().addItemUnderAnyCircumstance(15098, 1);
				c.sendMessage("You are now a Gambler. A dice has been added to your bank!");
				c.getPA().closeAllWindows();
			}
		}
		if (c.dialogueAction == 206) {
			c.getItems().resetItems(3214);
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 2109) {
			if (c.absX >= 2438 && c.absX <= 2439 && c.absY >= 5168 && c.absY <= 5169) {
				c.getFightCave().create(1);
			}
		}
		if (c.dialogueAction == 113239) {
			if (c.inDuelArena()) {
				return;
			}
			c.getItems().addItem(557, 1000);
			c.getItems().addItem(560, 1000);
			c.getItems().addItem(9075, 1000);
			c.getPA().removeAllWindows();
			c.dialogueAction = 0;
		}
		if (c.newPlayerAct == 1) {
			// c.isNewPlayer = false;
			c.newPlayerAct = 0;
			c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, "modern");
			c.getPA().removeAllWindows();
		}
		if (c.dialogueAction == 6) {
			c.sendMessage("Slayer will be enabled in some minutes.");
			// c.getSlayer().generateTask();
			// c.getPA().sendFrame126("@whi@Task:
			// @gre@"+Server.npcHandler.getNpcListName(c.slayerTask)+
			// " ", 7383);
			// c.getPA().closeAllWindows();
		}
		// if (c.dialogueAction == 29) {
		// if (c.isInBarrows() || c.isInBarrows2()) {
		// c.getBarrows().checkCoffins();
		// c.getPA().removeAllWindows();
		// return;
		// } else {
		// c.getPA().removeAllWindows();
		// c.sendMessage("@blu@You can only do this while you're at barrows,
		// fool.");
		// }
		// } else if (c.dialogueAction == 27) {
		// c.getBarrows().cantWalk = false;
		// c.getPA().removeAllWindows();
		// // c.getBarrowsChallenge().start();
		// return;
		if (c.dialogueAction == 25) {
			c.getDH().sendDialogues(26, 0);
			return;
		}
		if (c.dialogueAction == 162) {
			c.sendMessage("You successfully emptied your inventory.");
			c.getPA().removeAllItems();
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 508) {
			c.getDH().sendDialogues(1030, 925);
			return;
		}

		if (c.caOption2) {
			c.getDH().sendDialogues(106, c.npcType);
			c.caOption2 = false;
		}
		if (c.caOption2a) {
			c.getDH().sendDialogues(102, c.npcType);
			c.caOption2a = false;
		}

		if (c.dialogueAction == 1) {
			c.getDH().sendDialogues(38, -1);
		}
	}

	/*
	 * Handles all the 2nd options on 'Two option' dialogues.
	 */
	public static void handleOption2(Player c) {

		switch (c.dialogueAction) {
		case 813:
			c.getSafeBox().openSafeBox();
			break;
		case 819:
			c.getDH().sendDialogues(821, 822);
			break;
		case 550:
			c.getDH().sendDialogues(551, c.npcType);
			break;
		case 703:
			c.getPA().closeAllWindows();
			break;
		case 705:
			c.getPA().closeAllWindows();
			break;
		case 65:
			c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.GRAND_TREE_TELEPORT);
			switch (c.getDH().tree) {
			case "village":
				c.getPA().startTeleport(3183, 3508, 0, "modern"); //Grand exchange
				break;
				
			case "stronghold":
				c.getPA().startTeleport(3183, 3508, 0, "modern"); //Grand exchange
				break;
				
			case "grand_exchange":
				c.getPA().startTeleport(2461, 3444, 0, "modern"); //Stronghold
				c.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.SPIRIT_TREE_WEST);
				break;
			}
			break;
			
		case 72: //Attempt for pet
			c.getPA().closeAllWindows();
			break;
			
		case 40:
			c.getPA().removeAllWindows();
			SpinMaterial.getInstance().spin(c, SpinMaterial.Material.CROSSBOW.getRequiredItem());
			break;

		case 10:
			// TabletCreation.createTablet(c, 1, 1);
			c.getOutStream().createFrame(27);
			c.tablet = 2;
			break;
			
		case 94:
			c.getDH().sendDialogues(95, c.npcType);
			break;
			
		case 103:
			c.getPA().closeAllWindows();
			break;
			
		case 1391:
			c.getDH().sendDialogues(1392, c.npcType);
			break;
			
		case 1392:
			LootValue.configureValue(c, "resetvalue", -1);
			break;
			
		case 66:
			c.getPA().closeAllWindows();
			if (c.getRechargeItems().hasItem(13144)) {
				if (!c.getItems().playerHasItem(995, 25_000_000) || !c.getItems().playerHasItem(4207)) {
					c.sendMessage("You need at least 25m coins and a crystal to do this.");
					return;
				}
				c.getItems().deleteItem(995, 25_000_000);
				c.getItems().deleteItem(4207, 1);
				c.getItems().addItem(13092, 1);
				c.sendMessage("The weird old man successfully created a crystal halberd for you.");
			}
			break;
			
		case 68:
			c.dropRateInKills = false;
			Server.getDropManager().open(c);
			c.sendMessage("Now viewing drop-rates in percent form");
			break;
		}

		if (c.dialogueAction == 132 || c.dialogueAction == 134) {
			c.getPA().closeAllWindows();
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 132 || c.dialogueAction == 134) {
			c.getPA().closeAllWindows();
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 133) {
			c.getPA().closeAllWindows();
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 149) {
			c.getShops().openShop(9);
			c.dialogueAction = -1;
			return;
		}
		if (c.dialogueAction == 126 || c.dialogueAction == 130) {
			c.getPA().removeAllWindows();
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.dialogueAction == 947) {
			c.getShops().openShop(111);
			c.dialogueAction = -1;
		}
		if (c.dialogueAction == -1 && c.getCurrentCombination().isPresent()) {
			c.setCurrentCombination(Optional.empty());
			c.getPA().removeAllWindows();
			return;
		}
		if (c.dialogueAction == 29) {
			c.dialogueAction = -1;
			c.getPA().removeAllWindows();
			return;
		}
		if (c.dialogueAction == 3308) {
			c.getPA().removeAllWindows();
		}
		if (c.dialogueAction == 100 || c.dialogueAction == 120) {
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 200 || c.dialogueAction == 202 || c.dialogueAction >= 101 && c.dialogueAction <= 103 || c.dialogueAction == 106
				|| c.dialogueAction >= 109 && c.dialogueAction <= 114) {
			c.getPA().removeAllWindows();
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		} else if (c.dialogueAction == 201) {
			c.getDH().sendDialogues(501, -1);
			return;
		}
		if (c.dialogueAction == 162) {
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 12001) {
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 12000) {
			for (int i = 8144; i < 8195; i++) {
				c.getPA().sendFrame126("", i);
			}
			int[] frames = { 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159, 8160, 8161, 8162, 8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173,
					8174, 8175 };
			c.getPA().sendFrame126("@dre@Kill Tracker for @blu@" + c.playerName + "", 8144);
			c.getPA().sendFrame126("", 8145);
			c.getPA().sendFrame126("@blu@Total kills@bla@ - " + c.getNpcDeathTracker().getTotal() + "", 8147);
			c.getPA().sendFrame126("", 8148);
			int index = 0;
			for (Entry<String, Integer> entry : c.getNpcDeathTracker().getTracker().entrySet()) {
				if (entry == null) {
					continue;
				}
				if (index > frames.length - 1) {
					break;
				}
				if (entry.getValue() > 0) {
					c.getPA().sendFrame126("@blu@" + WordUtils.capitalize(entry.getKey().toLowerCase()) + ": @red@" + entry.getValue(), frames[index]);
					index++;
				}
			}
			c.getPA().showInterface(8134);
		}
		if (c.dialogueAction == 109) {
			c.getPA().removeAllWindows();
			c.dialogueAction = 0;
		}
		if (c.dialogueAction == 113239) {
			if (c.inDuelArena()) {
				return;
			}
			c.getItems().addItem(555, 1000);
			c.getItems().addItem(560, 1000);
			c.getItems().addItem(565, 1000);
			c.getPA().removeAllWindows();
			c.dialogueAction = 0;
		}
		if (c.dialogueAction == 2301) {
			c.getPA().removeAllWindows();
			c.dialogueAction = 0;
		}
		if (c.newPlayerAct == 1) {
			c.newPlayerAct = 0;
			c.getPA().removeAllWindows();
		}
		if (c.doricOption2) {
			c.getDH().sendDialogues(309, 284);
			c.doricOption2 = false;
		}
		/*
		 * if (c.dialogueAction == 8) { c.getPA().fixAllBarrows(); } else { c.dialogueAction = 0; c.getPA().removeAllWindows(); }
		 */
		if (c.dialogueAction == 27) {
			c.getPA().removeAllWindows();
		}
		if (c.caOption2a) {
			c.getDH().sendDialogues(136, c.npcType);
			c.caOption2a = false;
		}
	}

}
