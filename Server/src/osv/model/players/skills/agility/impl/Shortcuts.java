package osv.model.players.skills.agility.impl;

import osv.model.content.achievement_diary.ardougne.ArdougneDiaryEntry;
import osv.model.content.achievement_diary.falador.FaladorDiaryEntry;
import osv.model.content.achievement_diary.kandarin.KandarinDiaryEntry;
import osv.model.content.achievement_diary.morytania.MorytaniaDiaryEntry;
import osv.model.content.achievement_diary.wilderness.WildernessDiaryEntry;
import osv.model.npcs.NPC;
import osv.model.npcs.NPCHandler;
import osv.model.players.Player;
import osv.model.players.skills.agility.AgilityHandler;
import osv.util.Misc;

/**
 * Agility Shortcuts
 * 
 * @author Matt
 */

public class Shortcuts {

	public static final int SLAYER_TOWER_CHAIN_UP = 16537, 
							SLAYER_TOWER_CHAIN_DOWN = 16538, 
							RELLEKKA_STRANGE_FLOOR = 16544, 
							RELLEKKA_CREVICE = 16539, 
							STEPPING_STONE = 16466,
							ARDOUGNE_LOG = 16548;

	public boolean agilityShortcuts(final Player c, final int objectId) {
		int x = c.absX;
		int y = c.absY;
		int z = c.heightLevel;
		
		switch (objectId) {
		
		case 23271:
			int NORTH = 3523;
			int SOUTH = 3520;
			if (y == NORTH) {
				c.getAgilityHandler().move(c, 0, -3, 6132, -1);
			} else if (y == SOUTH) {
				c.getAgilityHandler().move(c, 0, 3, 6132, -1);
			}
			return true;
			
		case 23274:
			if (c.playerLevel[c.playerAgility] < 20) {
				c.sendMessage("You must have an agility level of at least 20 to cross here.");
				return false;
			}
			c.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.CROSS_BALANCE);
			if (c.getAgilityHandler().hotSpot(c, 2603, 3477))
				c.getAgilityHandler().move(c, -5, 0, 762, -1);
			if (c.getAgilityHandler().hotSpot(c, 2598, 3477))
				c.getAgilityHandler().move(c, 5, 0, 762, -1);
			return true;
			
		case 20843:
			AgilityHandler.delayFade(c, "NONE", 3102, 3482, 0, "You enter the portal..", "and end up by the rowboat.", 3);
			return true;
			
		case 29082:
			NPC npc = NPCHandler.getNpc(7439);
			npc.facePlayer(c.getIndex());
			npc.startAnimation(7328);
			npc.forceChat("HAH! Off you go " + Misc.formatPlayerName(c.playerName) + "!");
			c.startAnimation(2304);
			AgilityHandler.delayFade(c, "NONE", 2610, 4776, 0, "Abigail hits you while entering the boat..", "and you wake up in a random place.", 2);
			return true;
		
		case 26762:
			AgilityHandler.delayFade(c, "CRAWL", 3232, 10351, 0, "You crawl into the cavern..", "and end up at scorpia's cave.", 3);
			return true;
		case 26763:
			AgilityHandler.delayFade(c, "CRAWL", 3233, 3950, 0, "You crawl out of scorpia's cave..", "and end up outside.", 3);
			return true;
			
		case 678: //Cave to corp
			AgilityHandler.delayFade(c, "CRAWL", 2945, 4384, 2, "You crawl into the cave.", "and end up at corporeal beast lair.", 3);
			break;
			
		case 679: //Cave out from corp
			AgilityHandler.delayFade(c, "CRAWL", 3206, 3681, 0, "You crawl into the cave.", "and end up outside corporeal beast lair.", 3);
			break;
			
		case 26567:
		case 26568: //Cerberus cave
		case 26569:
			if (!c.getSlayer().isCerberusRoute()) {
				c.sendMessage("You have no clue how to navigate in here, you should find a slayer master to learn.");
				return false;
			}
			AgilityHandler.delayFade(c, "CRAWL", 1310, 1237, 0, "You crawl into the cave", "and end up in a dark place.", 3);
			return true;
			
		case 26564:
		case 26565:
		case 26566:
			AgilityHandler.delayFade(c, "CRAWL", 2873, 9847, 0, "You crawl into the cave", "and end up on the outside.", 3);
			return true;
			
		case 26711:
			AgilityHandler.delayFade(c, "CRAWL", 3748, 5849, 0, "You crawl into the crevice", "and end up in the kalphite hive.", 3);
			return true;
		case 26712:
			AgilityHandler.delayFade(c, "CRAWL", 2436, 9824, 0, "You crawl into the crevice", "and end up back on the outside.", 3);
			return true;
			
//		case 26645:
//			AgilityHandler.delayFade(c, "JUMP", 3328, 4751, 0, "You jump into the portal..", "and end up at the free-for-all area.", 3);
//			return true;
//		case 26646:
//			AgilityHandler.delayFade(c, "JUMP", 3101, 3492, 0, "You jump into the portal..", "and end up in edgeville.", 3);
//			return true;
			
		case 537: // Kraken Cave
			if (!c.getSlayer().getTask().isPresent()) {
				c.sendMessage("You must have an active kraken task to enter this crevice...");
				return false;	
			}
			if (!c.getSlayer().getTask().get().getPrimaryName().contains("kraken")) {
				c.sendMessage("You must have an active kraken task to enter this crevice.");
				return false;	
			}
			AgilityHandler.delayFade(c, "CRAWL", 3696, 5798, 0, "You crawl into the crevice",
					"and end up in a wet cave.", 3);
			return true;
			
		case 8729: // Wyvern top floor
			if (!c.getSlayer().getTask().isPresent()) {
				c.sendMessage("You must have an active skeletal wyvern task to go up here.");
				return false;	
			}
			if (!c.getSlayer().getTask().get().getPrimaryName().equals("skeletal wyvern")) {
				c.sendMessage("You must have an active skeletar wyvern task to enter this crevice.");
				return false;	
			}
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3060, 9558, 0, 2);
			return true;
			
		case 10596:
			AgilityHandler.delayEmote(c, "CRAWL", 3056, 9555, 0, 2);
			return true;
			
		case 10595:
			AgilityHandler.delayEmote(c, "CRAWL", 3056, 9562, 0, 2);
			return true;
			
		case ARDOUGNE_LOG:
			if (c.playerLevel[c.playerAgility] < 32) {
				c.sendMessage("You must have an agility level of at least 32 to cross here.");
				return false;
			}
			c.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.CROSS_THE_LOG);
			if (c.getAgilityHandler().hotSpot(c, 2602, 3336))
				c.getAgilityHandler().move(c, -4, 0, 762, -1);
			if (c.getAgilityHandler().hotSpot(c, 2598, 3336))
				c.getAgilityHandler().move(c, 4, 0, 762, -1);
			break;
			
		case RELLEKKA_STRANGE_FLOOR:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (x == 2768)
				c.getAgilityHandler().move(c, 2, 0, 756, -1);
			if (x == 2770)
				c.getAgilityHandler().move(c, -2, 0, 756, -1);
			if (x == 2773)
				c.getAgilityHandler().move(c, 2, 0, 756, -1);
			if (x == 2775)
				c.getAgilityHandler().move(c, -2, 0, 756, -1);
			return true;
		case RELLEKKA_CREVICE:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (x <= 2730) {
				c.getAgilityHandler().move(c, 5, 0, 756, -1);
			} else {
				c.getAgilityHandler().move(c, -5, 0, 756, -1);
			}
			return true;
		case SLAYER_TOWER_CHAIN_UP:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (z == 0 && y <= 3552) {
				if (c.playerLevel[c.playerAgility] < 60) {
					c.sendMessage("You need to have a slayer level of at least 61 to climb this chain.");
					return false;
				}
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3422, 3549, 1, 2);
			} else if (z == 1 && y >= 3574) {
				if (c.playerLevel[c.playerAgility] < 70) {
					c.sendMessage("You need to have a slayer level of at least 71 to climb this chain.");
					return false;
				}
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3447, 3575, 2, 2);
				c.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.CLIMB_CHAIN);
			}
			return true;
		case SLAYER_TOWER_CHAIN_DOWN:
			if (z == 1 && y <= 3552) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3422, 3549, 0, 2);
			} else if (z == 2 && y >= 3574) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3447, 3575, 1, 2);
			}
			return true;
			
		case STEPPING_STONE:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (y < 2972) {
				AgilityHandler.delayEmote(c, "JUMP", 2863, 2976, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "JUMP", 2863, 2971, 0, 2);
			}
			return true;
			
		case 28857:
			AgilityHandler.delayEmote(c, "CLIMB_UP", c.absX > 1573 ? 1574 : 1567, c.absY > 3490 ? 3493 : 3482, 2, 2);
			return true;
			
		case 28858:
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.absX > 1573 ? 1576 : 1565, c.absY > 3490 ? 3493 : 3482, 0, 2);
			return true;
			
		case 17050:
		case 17049:
			if (c.playerLevel[c.playerAgility] < 11 || c.playerLevel[c.playerStrength] < 37 || c.playerLevel[c.playerRanged] < 19) {
				c.sendMessage("You must have a ranged level of 11, strength level of 37 and agility level of 11 to do this.");
				return false;
			}
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3032, c.absY == 3388 ? 3390 : 3388, 0, 2);
			c.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.GRAPPLE_NORTH_WALL);
			return true;
			
		case 26766:
			AgilityHandler.delayFade(c, "CRAWL", 3062, 10130, 0, "You crawl into the entrance", "And end up in the wilderness god wars dungeon..", 3);
			c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.WILDERNESS_GODWARS);
			return true;
			
		case 26769:
			AgilityHandler.delayFade(c, "CRAWL", 3017, 3740, 0, "You crawl into the crevice", "And end up outside the entrace..", 3);
			return true;
		}
		return false;
	}

}
