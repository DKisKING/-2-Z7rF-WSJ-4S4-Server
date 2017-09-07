package osv.model.players.packets.dialogueoptions;

import osv.event.impl.WheatPortalEvent;
import osv.model.content.DiceHandler;
import osv.model.content.LootValue;
import osv.model.content.QuickSets;
import osv.model.content.RecolourGraceful;
import osv.model.content.StatReset;
import osv.model.content.teleportation.TeleportationDevice;
import osv.model.npcs.NPC;
import osv.model.npcs.NPCHandler;
import osv.model.players.Boundary;
import osv.model.players.Player;
import osv.model.players.PlayerAssistant;
import osv.model.players.combat.Degrade;
import osv.model.players.combat.Degrade.DegradableItem;
import osv.util.Misc;

/*
 * @author Matt
 * Five Option Dialogue actions
 */

public class FiveOptions {
	/*
	 * Handles all first options on 'Five option' dialogues.
	 */

	static NPC HISTORIAN = NPCHandler.getNpc(7597);
	public static void handleOption1(Player c) {

			switch (c.dialogueAction) {
			case 851:
				c.getDH().sendDialogues(854, 7597);
				break;
			case 855: //Tekton
				HISTORIAN.facePlayer(c.getIndex());
				HISTORIAN.startAnimation(1818);
				HISTORIAN.gfx0(343);
				HISTORIAN.forceChat("Off to Tekton you go " + Misc.formatPlayerName(c.playerName) + "!");
				c.getPlayerAssistant().movePlayer(3309, 5277, 1);
				break;
			
			case 820:
				QuickSets.gearUp(c, 0);
				break;
			case 821:
				QuickSets.gearUp(c, 3);
				break;
		
		case 801:
			c.getDH().sendDialogues(802, 6773);
			break;
			
		case 77:
			c.getPA().closeAllWindows();
			if (!c.getRingOfLifeEffect()) {
				c.setRingOfLifeEffect(true);
				c.sendMessage("You have toggled on the ROL effect.");
			} else {
				c.setRingOfLifeEffect(false);
				c.sendMessage("You have toggled off the ROL effect.");
			}
			break;
		case 14400:
			c.getPA().startTeleport(2474, 3438, 0, "modern");
			c.sendMessage("You will gain XP after each lap");
			c.getPA().closeAllWindows();
			break;

		case 14402:
			c.getPA().startTeleport(3223, 3415, 0, "modern");
			c.sendMessage("You will gain XP after each lap");
			c.getPA().closeAllWindows();
			break;

		case 1401:
			StatReset.execute(c, 0);
			break;

		case 1404:
			StatReset.execute(c, 5);
			break;

		case 2002:
			c.getPA().spellTeleport(3565, 3308, 0);
			break;
		case 2003:
			c.getPA().spellTeleport(3218, 9622, 0);
			break;

		case 61: // Resource Area
			TeleportationDevice.startTeleport(c, 0);
			break;
			
		case 76: //Max cape
				c.getPA().closeAllWindows();
				if (c.inWild()) {
					return;
				}
				if (c.playerMagicBook == 0) {
					c.playerMagicBook = 1;
					c.setSidebarInterface(6, 28062);
					c.autocasting = false;
					c.sendMessage("An ancient wisdomin fills your mind.");
					c.getPA().resetAutocast();
				} else if (c.playerMagicBook == 1) {
					c.sendMessage("You switch to the lunar spellbook.");
					c.setSidebarInterface(6, 28064);
					c.playerMagicBook = 2;
					c.autocasting = false;
					c.autocastId = -1;
					c.getPA().resetAutocast();
				} else if (c.playerMagicBook == 2) {
					c.setSidebarInterface(6, 28060);
					c.playerMagicBook = 0;
					c.autocasting = false;
					c.sendMessage("You feel a drain on your memory.");
					c.autocastId = -1;
					c.getPA().resetAutocast();
				}
			break;
			
		case 1393:
			LootValue.configureValue(c, "setvalue", 100000);
			break;
			
		case 55:
			RecolourGraceful.COLOR = "PURPLE";
			RecolourGraceful.recolor(c, RecolourGraceful.ITEM_TO_RECOLOUR);
			break;
			
		case 56:
			RecolourGraceful.COLOR = "GREEN";
			RecolourGraceful.recolor(c, RecolourGraceful.ITEM_TO_RECOLOUR);
			break;
			
		case 711:
			c.getDH().sendDialogues(712, 306);
			break;
			
		case 78: //Max cape crafting guild teleport
			c.getPA().startTeleport(2936, 3283, 0, "modern");
			c.getPA().closeAllWindows();
			break;
		}

		if (c.dialogueAction == 114) {
			if (c.getItems().playerHasItem(6737, 1) && c.pkp >= 250) {
				c.getItems().deleteItem(6737, 1);
				c.pkp -= 250;
				c.getItems().addItem(11773, 1);
				c.sendMessage("You imbue your berserker ring for the cost of @blu@250 PKP@bla@.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			} else {
				c.sendMessage("You need 250 PKP and a Berserker ring to do this.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			return;
		}
		if (c.dialogueAction == 135) {
			if (c.getItems().playerHasItem(12605, 1) && c.pkp >= 250) {
				c.getItems().deleteItem(12605, 1);
				c.pkp -= 250;
				c.getItems().addItem(12692, 1);
				c.sendMessage("You imbue your treasonous ring for the cost of @blu@250 PKP@bla@.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			} else {
				c.sendMessage("You need 250 PKP and a Treasonous ring to do this.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			return;
		}
		if (c.dialogueAction == 131) {
			c.getDH().sendDialogues(659, 311);
			return;
		}
		if (c.teleAction == 16) {
			c.getPA().startTeleport(3313, 3826, 0, "modern");
			return;
		}
		if (c.teleAction == 15) {
			c.getPA().startTeleport(3236, 3295, 0, "modern");
			return;
		}
		if (c.dialogueAction == 128) {
			c.getFightCave().create(1);
			return;
		}
		if (c.teleAction == 17) {
			c.getPA().spellTeleport(2808, 10002, 0);
			return;
		}
		if (c.dialogueAction == 123) {
			DegradableItem[] claimable = Degrade.getClaimedItems(c);
			if (claimable.length == 0) {
				return;
			}
			c.getPA().removeAllWindows();
			Degrade.claim(c, claimable[0].getItemId());
			return;
		}
		if (c.dialogueAction == 121) {
			c.getDH().sendDialogues(614, -1);
			c.teleAction = -1;
			c.dialogueAction = -1;
			return;
		}
		if (c.teleAction == 14) {
			c.getPA().spellTeleport(1923, 4367, 0);
			c.teleAction = -1;
			c.dialogueAction = -1;
			return;
		}
		if (c.teleAction == 12) {
			c.getPA().spellTeleport(3302, 9361, 0);
		}
		if (c.teleAction == 11) {
			c.getPA().spellTeleport(3228, 9392, 0);
		}
		if (c.teleAction == 10) {
			c.getPA().spellTeleport(2705, 9487, 0);
		}
		if (c.teleAction == 9) {
			c.getPA().spellTeleport(3226, 3263, 0);
		}
		if (c.teleAction == 8) {
			c.getPA().spellTeleport(3293, 3178, 0);
		}
		if (c.teleAction == 7) {
			c.getPA().spellTeleport(3118, 9851, 0);
		}
		if (c.teleAction == 1) {
			// 44 Portals
			c.getPA().spellTeleport(2980, 3871, 0);
		} else if (c.teleAction == 2) {
			c.getPA().spellTeleport(3428, 3538, 0);
		} else if (c.teleAction == 200) {
			// barrows
			// c.getPA().spellTeleport(3565, 3314, 0);
			// c.getItems().addItem(952, 1);
		} else if (c.teleAction == 3) {
			c.getPA().spellTeleport(3005, 3850, 0);
		} else if (c.teleAction == 4) {
			// varrock wildy
			c.getPA().spellTeleport(3025, 3379, 0);
		} else if (c.teleAction == 5) {
			c.getPA().spellTeleport(3046, 9779, 0);
		} else if (c.teleAction == 2000) {
			// lum
			c.getPA().spellTeleport(3222, 3218, 0);// 3222 3218
		} else {
			int actionButtonId = 0;
			DiceHandler.handleClick(c, actionButtonId);
		}
		if (c.dialogueAction == 10) {
			c.getPA().spellTeleport(2845, 4832, 0);
			c.dialogueAction = -1;

		} else if (c.dialogueAction == 11) {
			c.getPA().spellTeleport(2786, 4839, 0);
			c.dialogueAction = -1;
		} else if (c.dialogueAction == 12) {
			c.getPA().spellTeleport(2398, 4841, 0);
			c.dialogueAction = -1;
		}
	}

	/*
	 * Handles all 2nd options on 'Five option' dialogues.
	 */
	public static void handleOption2(Player c) {

		switch (c.dialogueAction) {
		
		case 851:
			c.getShops().openShop(118);
			//c.sendMessage("A raids shop will be available shortly!");
			//c.getPA().closeAllWindows();
			break;
		
		case 855: //Skeletal mystics
			HISTORIAN.facePlayer(c.getIndex());
			HISTORIAN.startAnimation(1818);
			HISTORIAN.gfx0(343);
			HISTORIAN.forceChat("Off to Skeletal Mystics you go " + Misc.formatPlayerName(c.playerName) + "!");
			c.getPlayerAssistant().movePlayer(3343, 5248, 1);
			break;

		case 820:
			QuickSets.gearUp(c, 1);
			break;
			

		case 821:
			QuickSets.gearUp(c, 4);
			break;
	
		case 801:
			c.getDH().sendDialogues(805, 6773);
			break;
		
		case 77:
			c.getPA().closeAllWindows();
			if (!c.getFishingEffect()) {
				c.setFishingEffect(true);
				c.sendMessage("You have toggled on the Fishing effect.");
			} else {
				c.setFishingEffect(false);
				c.sendMessage("You have toggled off the Fishing effect.");
			}
			break;
		
	case 711:
		c.getDH().sendDialogues(713, 306);
		break;
		
		case 14400:
			c.getPA().startTeleport(2551, 3555, 0, "modern");
			c.sendMessage("You will gain XP after each lap");
			c.getPA().closeAllWindows();
			break;

		case 14402:
			c.getPA().startTeleport(2729, 3488, 0, "modern");
			c.sendMessage("You will gain XP after each lap");
			c.getPA().closeAllWindows();
			break;

		case 1401:
			StatReset.execute(c, 2);
			break;

		case 1404:
			StatReset.execute(c, 4);
			break;
		case 2002:
			c.getPA().spellTeleport(2847, 3543, 0);
			c.sendMessage("@blu@Use the animators to gain tokens, then head upstairs to the cyclops.");
			break;
		case 2003:

			break;

		case 61: // Kbd Lair
			TeleportationDevice.startTeleport(c, 1);
			break;
			
		case 76: //Max cape
			c.getDH().sendDialogues(78, 311);
			break;
			
		case 78: //Max cape teleport
			PlayerAssistant.ringOfCharosTeleport(c);
			c.getPA().closeAllWindows();
			break;
			
		case 1393:
			LootValue.configureValue(c, "setvalue", 500000);
			break;
			
		case 55:
			RecolourGraceful.COLOR = "BLUE";
			RecolourGraceful.recolor(c, RecolourGraceful.ITEM_TO_RECOLOUR);
			break;
			
		case 56:
			RecolourGraceful.COLOR = "WHITE";
			RecolourGraceful.recolor(c, RecolourGraceful.ITEM_TO_RECOLOUR);
			break;
		}

		if (c.teleAction == 17) {
			c.getPA().spellTeleport(2444, 9825, 0);
			return;
		}
		if (c.dialogueAction == 114) {
			if (c.getItems().playerHasItem(6733, 1) && c.pkp >= 250) {
				c.getItems().deleteItem(6733, 1);
				c.pkp -= 250;
				c.getItems().addItem(11771, 1);
				c.sendMessage("You imbue your archer ring for the cost of @blu@250 PKP@bla@.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			} else {
				c.sendMessage("You need 250 PKP and an Archer ring to do this.");
				c.dialogueAction = 0;
				c.getPA().removeAllWindows();
			}
			return;
		}
		if (c.dialogueAction == 135) {
			if (c.getItems().playerHasItem(12603, 1) && c.pkp >= 250) {
				c.getItems().deleteItem(12603, 1);
				c.pkp -= 250;
				c.getItems().addItem(12691, 1);
				c.sendMessage("You imbue your treasonous ring for the cost of @blu@250 PKP@bla@.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			} else {
				c.sendMessage("You need 250 PKP and a Treasonous ring to do this.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			return;
		}
		if (c.dialogueAction == 131) {
			c.getDH().sendDialogues(652, 311);
			return;
		}
		if (c.teleAction == 16) {
			c.getPA().startTeleport(3077, 3910, 0, "modern");
			return;
		}
		if (c.teleAction == 15) {
			c.getPA().startTeleport(3253, 3267, 0, "modern");
			return;
		}
		if (c.teleAction == 2) {
			c.getPA().spellTeleport(3096, 9867, 0);
		}
		if (c.dialogueAction == 128) {
			c.getFightCave().create(2);
			return;
		}
		if (c.dialogueAction == 123) {
			DegradableItem[] claimable = Degrade.getClaimedItems(c);
			if (claimable.length < 2) {
				return;
			}
			c.getPA().removeAllWindows();
			Degrade.claim(c, claimable[1].getItemId());
			return;
		}
		if (c.dialogueAction == 121) {
			c.getDH().sendDialogues(616, -1);
			c.teleAction = -1;
			c.dialogueAction = -1;
			return;
		}
		if (c.teleAction == 14) {
			c.getPA().spellTeleport(3292, 3648, 0);
			c.sendMessage("@blu@The kraken is roughly 20 steps south-west of this location.");
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		if (c.teleAction == 12) {
			c.getPA().spellTeleport(2908, 9694, 0);
		}
		if (c.teleAction == 11) {
			c.getPA().spellTeleport(3237, 9384, 0);
		}
		if (c.teleAction == 10) {
			c.getPA().spellTeleport(3219, 9366, 0);
		}
		if (c.teleAction == 9) {
			c.getPA().spellTeleport(2916, 9800, 0);
		}
		if (c.teleAction == 8) {
			c.getPA().spellTeleport(2903, 9849, 0);
		}
		if (c.teleAction == 7) {
			c.getPA().spellTeleport(2859, 9843, 0);
		}
		if (c.teleAction == 3) {
			// kbd
			// c.sendMessage("King Black Dragon has been disabled.");
			c.getPA().spellTeleport(3262, 3929, 0);
		}
		// c.getPA().closeAllWindows();
		/*
		 * if (c.teleAction == 1) { //rock crabs c.getPA().spellTeleport(2676, 3715, 0); } else if (c.teleAction == 2) { //taverly dungeon c.getPA().spellTeleport(2884, 9798, 0); }
		 * else if (c.teleAction == 3) { //kbd c.getPA().spellTeleport(3007, 3849, 0); } else if (c.teleAction == 4) { //west lv 10 wild c.getPA().spellTeleport(2979, 3597, 0); }
		 * else if (c.teleAction == 5) { c.getPA().spellTeleport(3079,9502,0); }
		 */
		if (c.teleAction == 1) {
			// West drags
			c.getPA().spellTeleport(2979, 3597, 0);
		} else if (c.teleAction == 200) {
			// pest control
			// c.getPA().spellTeleport(3252, 3894, 0);
		} else if (c.teleAction == 4) {
			// graveyard
			c.getPA().spellTeleport(3043, 9779, 0);
		} else if (c.teleAction == 5) {
			c.getPA().spellTeleport(3079, 9502, 0);

		} else if (c.teleAction == 2000) {
			c.getPA().spellTeleport(3210, 3424, 0);// 3210 3424
		} else if (c.dialogueAction == 10) {
			c.getPA().spellTeleport(2796, 4818, 0);
			c.dialogueAction = -1;
		} else if (c.dialogueAction == 11) {
			c.getPA().spellTeleport(2527, 4833, 0);
			c.dialogueAction = -1;
		} else if (c.dialogueAction == 12) {
			c.getPA().spellTeleport(2464, 4834, 0);
			c.dialogueAction = -1;
		}
	}

	/*
	 * Handles all 3rd options on 'Five option' dialogues.
	 */
	public static void handleOption3(Player c) {

		switch (c.dialogueAction) {
		case 851:
			c.getDH().sendDialogues(852, 7597);
			break;
			
		case 78:
			if (WheatPortalEvent.xLocation > 0 && WheatPortalEvent.yLocation > 0) {
				c.getPA().spellTeleport(WheatPortalEvent.xLocation + 1, WheatPortalEvent.yLocation + 1, 0);
			} else {
				c.sendMessage("There is currently no portal available, wait 5 minutes.");
				return;
			}
			break;
		
		case 820:
			QuickSets.gearUp(c, 2);
			break;

		case 821:
			QuickSets.gearUp(c, 5);
			break;
		case 801:
			c.getDH().sendDialogues(806, 6773);
			break;
		case 77:
			c.getPA().closeAllWindows();
			if (!c.getMiningEffect()) {
				c.setMiningEffect(true);
				c.sendMessage("You have toggled on the Mining effect.");
			} else {
				c.setMiningEffect(false);
				c.sendMessage("You have toggled off the Mining effect.");
			}
			break;
		
	case 711:
		c.getDH().sendDialogues(714, 306);
		break;
		
		case 14400:
			c.getPA().startTeleport(3004, 3935, 0, "modern");
			c.sendMessage("You will gain XP after each lap");
			c.getPA().closeAllWindows();
			break;

		case 14402:
			c.getPA().spellTeleport(2673, 3297, 0);
			break;

		case 1401:
			StatReset.execute(c, 1);
			break;

		case 1404:
			StatReset.execute(c, 6);
			break;

		case 2002:
			c.getPA().spellTeleport(3106, 3959, 0);
			break;
		case 2003:

			break;

		case 61: // GWD + 10 kc
			TeleportationDevice.startTeleport(c, 2);
			break;
			
		case 76: //Max cape
			c.getDH().sendDialogues(77, 311);
			break;
			
		case 1393:
			LootValue.configureValue(c, "setvalue", 1000000);
			break;
			
		case 55:
			RecolourGraceful.COLOR = "GOLD";
			RecolourGraceful.recolor(c, RecolourGraceful.ITEM_TO_RECOLOUR);
			break;
			
		case 56: //recolor graceful
			
			break;

		}

		if (c.dialogueAction == 114) {
			if (c.getItems().playerHasItem(6731, 1) && c.pkp >= 250) {
				c.getItems().deleteItem(6731, 1);
				c.pkp -= 250;
				c.getItems().addItem(11770, 1);
				c.sendMessage("You imbue your seers ring for the cost of @blu@250 PKP@bla@.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			} else {
				c.sendMessage("You need 250 PKP and n Seers ring to do this.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			return;
		}
		if (c.dialogueAction == 131) {
			c.getDH().sendDialogues(661, 311);
			return;
		}
		if (c.dialogueAction == 128) {
			c.getFightCave().create(3);
			return;
		}
		if (c.teleAction == 17) {
			c.getPA().spellTeleport(1746, 5323, 0);
			return;
		}
		if (c.dialogueAction == 123) {
			DegradableItem[] claimable = Degrade.getClaimedItems(c);
			if (claimable.length < 3) {
				return;
			}
			c.getPA().removeAllWindows();
			Degrade.claim(c, claimable[2].getItemId());
			return;
		}
		if (c.dialogueAction == 121) {
			c.getDH().sendDialogues(617, -1);
			c.teleAction = -1;
			c.dialogueAction = -1;
			return;
		}
		if (c.teleAction == 15) {
			c.getPA().startTeleport(2679, 3717, 0, "modern");
			return;
		}
		if (c.teleAction == 16) {
			c.getPA().spellTeleport(1476, 3687, 0);
		}
		if (c.teleAction == 14) {
			c.getPA().spellTeleport(3343, 3735, 0);
		}
		if (c.teleAction == 12) {
			c.getPA().spellTeleport(2739, 5088, 0);
		}
		if (c.teleAction == 11) {
			c.getPA().spellTeleport(3280, 9372, 0);
		}
		if (c.teleAction == 10) {
			c.getPA().spellTeleport(3241, 9364, 0);
		}
		if (c.teleAction == 9) {
			c.getPA().spellTeleport(3159, 9895, 0);
		}
		if (c.teleAction == 8) {
			c.getPA().spellTeleport(2912, 9831, 0);
		}
		if (c.teleAction == 7) {
			c.getPA().spellTeleport(2843, 9555, 0);
		}
		if (c.teleAction == 3) {
			c.getPA().spellTeleport(2880, 5311, 2);
		}
		/*
		 * if (c.teleAction == 1) { //experiments c.getPA().spellTeleport(3555, 9947, 0); } else if (c.teleAction == 2) { //brimhavem dung c.getPA().spellTeleport(2709, 9564, 0); }
		 * else if (c.teleAction == 3) { //dag kings c.getPA().spellTeleport(2479, 10147, 0); } else if (c.teleAction == 4) { //easts lv 18 c.getPA().spellTeleport(3351, 3659, 0);
		 * } else if (c.teleAction == 5) { c.getPA().spellTeleport(2813,3436,0); }
		 */
		if (c.teleAction == 1) {
			// Hill giants
			c.getPA().spellTeleport(3288, 3631, 0);
		} else if (c.teleAction == 2) {
			c.getPA().spellTeleport(2884, 9798, 0);
		} else if (c.teleAction == 200) {

		} else if (c.teleAction == 4) {
			// Hillz
			c.getPA().spellTeleport(2726, 3487, 0);
		} else if (c.teleAction == 5) {
			c.getPA().spellTeleport(2813, 3436, 0);
		} else if (c.teleAction == 2000) {
			c.getPA().spellTeleport(3222, 3219, 0);
		}
		if (c.dialogueAction == 10) {
			c.getPA().spellTeleport(2713, 4836, 0);
			c.dialogueAction = -1;
		} else if (c.dialogueAction == 11) {
			c.getPA().spellTeleport(2162, 4833, 0);
			c.dialogueAction = -1;
		} else if (c.dialogueAction == 12) {
			c.getPA().spellTeleport(2207, 4836, 0);
			c.dialogueAction = -1;
		}
	}

	/*
	 * Handles all 4th options on 'Five option' dialogues.
	 */
	public static void handleOption4(Player c) {

		switch (c.dialogueAction) {		
		case 851:
			c.getDH().sendDialogues(853, 7597);
			break;
		case 801: //Safe-box
			c.getSafeBox().openSafeBox();
			break;
		case 77:
			c.getPA().closeAllWindows();
			if (!c.getWoodcuttingEffect()) {
				c.setWoodcuttingEffect(true);
				c.sendMessage("You have toggled on the Woodcutting effect.");
			} else {
				c.setWoodcuttingEffect(false);
				c.sendMessage("You have toggled off the Woodcutting effect.");
			}
			break;
		
		case 711:
			c.getDH().sendDialogues(715, 306);
			break;
		case 14400:
			// c.getPA().startTeleport(3004, 3935, 0, "modern");
			// c.sendMessage("You will gain XP after each lap");
			// c.getPA().closeAllWindows();
			break;

		case 14402:

			break;

		case 1401:
			c.getDH().sendDialogues(1404, c.npcType);
			break;

		case 1404:
			StatReset.execute(c, 3);
			break;

		case 2002:
			c.getPA().spellTeleport(2520, 3591, 0);
			break;
		case 2003:

			break;

		case 61: // Corporeal beast
			TeleportationDevice.startTeleport(c, 3);
			break;
			
		case 76: //Max cape
			c.getPA().closeAllWindows();
			break;
			
		case 1393: //Set value manually
			c.getOutStream().createFrame(27);
			c.settingLootValue = true;
			break;
			
		case 55:
			RecolourGraceful.COLOR = "RED";
			RecolourGraceful.recolor(c, RecolourGraceful.ITEM_TO_RECOLOUR);
			break;
			
		case 56: //recolor graceful
			
			break;
		}

		if (c.dialogueAction == 135) {
			c.getDH().sendDialogues(2400, -1);
			return;
		}
		if (c.dialogueAction == 114) {
			if (c.getItems().playerHasItem(6735, 1) && c.pkp >= 250) {
				c.getItems().deleteItem(6735, 1);
				c.pkp -= 250;
				c.getItems().addItem(11772, 1);
				c.sendMessage("You imbue your warrior ring for the cost of @blu@250 PKP@bla@.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			} else {
				c.sendMessage("You need 250 PKP and n Warrior ring to do this.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			return;
		}
		if (c.dialogueAction == 128) {
			c.getDH().sendDialogues(634, -1);
			return;
		}
		if (c.teleAction == 17) {
			c.getPA().spellTeleport(2352, 3160, 0);
			return;
		}
		switch (c.teleAction) {
		case 2:
			c.getPA().spellTeleport(2678 + Misc.random(3), 9563 + Misc.random(2), 0);
			break;
		}
		if (c.teleAction == 14) {
			c.getPA().startTeleport(3179, 3774, 0, "modern");
			c.teleAction = -1;
			c.dialogueAction = -1;
			return;
		}
		if (c.dialogueAction == 123) {
			DegradableItem[] claimable = Degrade.getClaimedItems(c);
			if (claimable.length < 4) {
				return;
			}
			c.getPA().removeAllWindows();
			Degrade.claim(c, claimable[3].getItemId());
			return;
		}
		if (c.dialogueAction == 121) {
			c.getDH().sendDialogues(618, -1);
			c.teleAction = -1;
			c.dialogueAction = -1;
			return;
		}
		if (c.teleAction == 12) {
			c.getDH().sendOption5("GarGoyle", "Bloodveld", "Banshee", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 11;
			return;
		}
		if (c.teleAction == 11) {
			c.getDH().sendOption5("Black Demon", "Dust Devils", "Nechryael", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 10;
			return;
		}
		if (c.teleAction == 10) {
			c.getDH().sendOption5("Goblins", "Baby blue dragon", "Moss Giants", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 9;
			return;
		}
		if (c.teleAction == 9) {
			c.getDH().sendOption5("Al-kharid warrior", "Ghosts", "Giant Bats", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 8;
			return;
		}
		if (c.teleAction == 8) {
			c.getDH().sendOption5("Hill Giants", "Hellhounds", "Lesser Demons", "Chaos Dwarf", "-- Next Page --");
			c.teleAction = 7;
			return;
		}
		if (c.teleAction == 7) {
			c.getPA().spellTeleport(2923, 9759, 0);
		}
		if (c.teleAction == 1) {
			// East drags
			c.getPA().spellTeleport(3341, 3685, 0);
		} else if (c.teleAction == 200) {
			// duel arena
			c.getPA().spellTeleport(3366, 3266, 0);
		} else if (c.teleAction == 3) {
			c.getPA().spellTeleport(3331, 3706, 0);

		} else if (c.teleAction == 4) {
			// Fala
			/*
			 * c.getPA().removeAllWindows(); c.teleAction = 0;
			 */
			c.getPA().spellTeleport(2815, 3461, 0);
			c.getDH().sendStatement("You need a Rake, Watering can, Seed Dibber and a seed.");
		} else if (c.teleAction == 5) {
			c.getPA().spellTeleport(2724, 3484, 0);
			c.sendMessage("For magic logs, try north of the duel arena.");
		}
		if (c.dialogueAction == 10) {
			c.getPA().spellTeleport(2660, 4839, 0);
			c.dialogueAction = -1;
		} else if (c.dialogueAction == 11) {
			// c.getPA().spellTeleport(2527, 4833, 0); astrals here
			// c.getRunecrafting().craftRunes(2489);
			c.dialogueAction = -1;
		} else if (c.dialogueAction == 12) {
			// c.getPA().spellTeleport(2464, 4834, 0); bloods here
			// c.getRunecrafting().craftRunes(2489);
			c.dialogueAction = -1;
		}
	}

	/*
	 * Handles all 5th options on 'Five option' dialogues.
	 */
	public static void handleOption5(Player c) {

		switch (c.dialogueAction) {
		case 711:
		case 77:
		case 801:
		case 851:
			c.getPA().removeAllWindows();
			break;
		case 14400:
			c.getDH().sendDialogues(14402, -1);
			break;

		case 14402:
			c.getDH().sendDialogues(14401, -1);
			break;

		case 1401:
			c.getPA().removeAllWindows();
			break;

		case 1404:
			c.getDH().sendDialogues(1401, -1);
			break;

		case 2002:
			c.getDH().sendDialogues(2003, -1);
			break;

		case 2003:

			break;

		case 61: // Teleportation device 5

			break;
			
		case 76: //Max cape
			c.getPA().closeAllWindows();
			break;
			
		case 1393:
			c.getDH().sendDialogues(1394, c.npcType);
			break;
			
		case 55:
			c.getDH().sendDialogues(61, c.npcType);
			break;
			
		case 56:
			c.getDH().sendDialogues(55, c.npcType);
			break;
		}

		if (c.dialogueAction == 114) {
			c.getDH().sendDialogues(2402, -1);
			return;
		}
		if (c.dialogueAction == 128) {
			c.getDH().sendDialogues(636, -1);
			return;
		}
		switch (c.teleAction) {
		case 17:
			c.getDH().sendDialogues(3324, -1);
			return;
		case 2:
			c.getDH().sendDialogues(3333, -1);
			return;
		case 14:
			c.getDH().sendOption5("Callisto @red@(39 Wild)", "Giant mole @red@(49 wild)", "Lizardman Canyon", "", "Close");
			c.teleAction = 16;
			return;
		}
		if (c.dialogueAction == 121 || c.dialogueAction == 123 || c.teleAction == 16 || c.dialogueAction == 131 || c.dialogueAction == 135) {
			c.getPA().removeAllWindows();
			c.teleAction = -1;
			c.dialogueAction = -1;
			return;
		}
		if (c.teleAction == 8) {
			c.getDH().sendOption5("Goblins", "Baby blue dragon", "Moss Giants", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 9;
			return;
		}
		if (c.teleAction == 9) {
			c.getDH().sendOption5("Black Demon", "Dust Devils", "Nechryael", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 10;
			return;
		}
		if (c.teleAction == 11) {
			c.getDH().sendOption5("Infernal Mage", "Dark Beasts", "Abyssal Demon", "-- Previous Page --", "");
			c.teleAction = 12;
			return;
		}
		if (c.teleAction == 10) {
			c.getDH().sendOption5("GarGoyle", "Bloodveld", "Banshee", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 11;
			return;
		}
		if (c.teleAction == 7) {
			c.getDH().sendOption5("Al-kharid warrior", "Ghosts", "Giant Bats", "-- Previous Page --", "-- Next Page --");
			c.teleAction = 8;
			return;
		}
		if (c.teleAction == 1) {
			// Empty teleport
			return;
		} else if (c.teleAction == 200) {
			// last minigame spot
			// c.sendMessage("Suggest something for this spot on the
			// forums!");
			// c.getPA().closeAllWindows();
			// c.getPA().spellTeleport(2876, 3546, 0);
		} else if (c.teleAction == 3) {
			c.getDH().sendOption5("Dagannoth Cave", "Kraken @red@(Level 17 & Multi)", "Venenatis @red@(Level 29 & Multi)", "Vet'ion @red@(Level 34 & Multi)", "@blu@Next");
			c.teleAction = 14;
		} else if (c.teleAction == 4) {
			// ardy lever
			/*
			 * c.getPA().removeAllWindows(); c.teleAction = 0;
			 */
			c.getPA().spellTeleport(3039, 4836, 0);
		} else if (c.teleAction == 5) {
			c.getPA().spellTeleport(2812, 3463, 0);
		}
		if (c.dialogueAction == 10 || c.dialogueAction == 11) {
			c.dialogueId++;
			c.getDH().sendDialogues(c.dialogueId, 0);
		} else if (c.dialogueAction == 12) {
			c.dialogueId = 17;
			c.getDH().sendDialogues(c.dialogueId, 0);
		}
	}
}
