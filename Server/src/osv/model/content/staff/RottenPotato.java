package osv.model.content.staff;

import org.apache.commons.lang3.Range;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.Commands;
import osv.util.Misc;

/**
 * Class handling loading and actions of the rotten potato
 * @author Matt
 *
 */
public class RottenPotato {
	
	/**
	 * Points to the username options are being used towards
	 */
	public static String username = "";
	
	/**
	 * Loads various possible options to perform on a player
	 * @param player
	 * @param option		The options we are going to load
	 */
	private static void loadOnPlayerOptions(Player player, String option) {
		
		switch (option) {
		case "choose_option_player":
			player.rottenPotatoOption = "choose_option_player";
			player.getDH().sendOption5("Punish", "Monitor", "Move", "", "");
			break;
		case "punish_player":
			player.rottenPotatoOption = "punish_player";
			player.getDH().sendOption5("Kick "+username+"", "Quick jail "+username+"", "Quick ban "+username+"", "Quick mute "+username+"", "");
			break;
			
		case "monitor_player":
			player.rottenPotatoOption = "monitor_player";
			player.getDH().sendOption5("Check bank of "+username+" "+(!player.getRights().isOrInherits(Right.ADMINISTRATOR) ? "@red@Not available" : "")+"", "Check inventory of "+username+"", "Teleport to "+username+"", "Check if "+username+" have a Bank-PIN", "");
			break;
			
		case "move_player":
			player.rottenPotatoOption = "move_player";
			player.getDH().sendOption5("Teleport "+username+" to you", "Move "+username+" home", "Move "+username+" to questioning", "", "");
			break;
		}
	}
	
	/**
	 * Loads various possible options to perform towards ourself
	 * @param player
	 * @param option		The options we are going to load
	 */
	private static void loadPlayerOptions(Player player, String option) {
		
		switch (option) {
		case "choose_option_me":
			player.getDH().sendOption5("Interface/Debug", "Quick spawn", "Transform", "", "Empty Inventory");
			player.rottenPotatoOption = "choose_option_me";
			break;
			
		case "interface_me":
			player.getDH().sendOption5("Open Bank", "Debug", "", "", "");
			player.rottenPotatoOption = "interface_me";
			break;
			
		case "debug_me":
			player.getDH().sendOption5("Graphics Debug", "Animations Debug", "", "", "");
			player.rottenPotatoOption = "debug_me";
			break;
			
		case "debug_graphics":
			player.getDH().sendOption5("Show Graphics +1", "Show Graphics +5", "Show Graphics -1", "Document Graphic", "");
			player.rottenPotatoOption = "debug_graphics";
			break;
			
		case "debug_animations":
			player.getDH().sendOption5("Show Animations +1", "Show Animations +5", "Show Animations -1", "Document Animation", "");
			player.rottenPotatoOption = "debug_animations";
			break;
			
		case "quick_spawn":
			player.getDH().sendOption5("All Runes", "Money", "", "", "");
			player.rottenPotatoOption = "quick_spawn";
			break;
			
		case "transform":
			player.getDH().sendOption5("Santa/Anti-Santa Junior", "Holiday Item", "Bush", "Hilt", "Un-Transform");
			player.rottenPotatoOption = "transform";
			break;
		}
	}

	/**
	 * Loading the loadPlayerOptions method with the selected option when clicking the rotten potato
	 * @param player
	 */
	public static void clickItem(Player player) {
		loadPlayerOptions(player, "choose_option_me");
	}
	
	/**
	 * Loading the loadOnPlayerOptions method with the selected option when using the rotten potato on a player
	 * @param other		The player that the rotten potato was used on
	 */
	public static void useItem(Player player, Player other) {
		player.stopMovement();
		player.getCombat().resetPlayerAttack();
		loadOnPlayerOptions(player, "choose_option_player");
	}
	
	/**
	 * Closing all the windows currently open
	 * @param player
	 */
	private static void close(Player player) {
		player.getPA().removeAllWindows();
	}


	/**
	 * Handling the options which are loaded upon the methods above (loadPlayerOptions and loadOnPlayerOptions)
	 * @param player
	 * @param actionButtonId		The action id we are clicking
	 */
	public static void optionActions(Player player, int actionButtonId) {
		String split = "-", space = " ";
		
		switch (actionButtonId) {
		
		/**
		 * Option one of five
		 */
		case 9190:
			switch (player.rottenPotatoOption) {
			case "choose_option_player":
				loadOnPlayerOptions(player, "punish_player");
				break;
				
			case "choose_option_me":
				if (player.getRights().isOrInherits(Right.OWNER)) {
					loadPlayerOptions(player, "interface_me");
				} else {
					loadPlayerOptions(player, "debug_me");
				}
				break;
				
			case "punish_player":
				Commands.executeCommand(player, "kick" + space + username, "helper");
				close(player);
				break;
				
			case "monitor_player":
				if (!player.getRights().isOrInherits(Right.OWNER)) {
					return;
				}
				Commands.executeCommand(player, "checkbank" + space + username, "admin");
				break;
				
			case "move_player":
				Commands.executeCommand(player, "teletome" + space + username, "moderator");
				close(player);
				break;
				
			case "transform":
				final Range<Integer> SANTA = Range.between(1047, 1048);
				
				if (Misc.random(SANTA) == 1047) {
					Commands.executeCommand(player, "pnpc" + split + player.playerName + split + "1047", "admin");
					Commands.executeCommand(player, "setanim" + space + player.playerName + split + "7161" + split + "1378" + split + "1378", "admin");
				} else {
					Commands.executeCommand(player, "pnpc" + split + player.playerName + split + "1048", "admin");
					Commands.executeCommand(player, "setanim" + space + player.playerName + split + "808" + split + "819" + split + "819", "admin");
				}
				break;
				
			case "interface_me":
				Commands.executeCommand(player, "bank", "admin");
				break;
				
			case "quick_spawn":
				for (int runes = 554; runes < 567; runes++) {
					Commands.executeCommand(player, "spawn" + space + runes + space + "50k", "owner");
				}
					Commands.executeCommand(player, "spawn" + space + "9075" + space + "50k", "owner");
				break;
				
			case "debug_me":
				loadPlayerOptions(player, "debug_graphics");
				break;
				
			case "debug_graphics":
				player.getDH().sendOption5("Show Graphics +1", "Show Graphics +5", "Show Graphics -1", "", "Graphical ID: " + player.gfx);
				Commands.executeCommand(player, "gfx" + space + player.gfx + space + "plus" + space + "1", "owner");
				break;
				
			case "debug_animations":
				player.getDH().sendOption5("Show Animations +1", "Show Animations +5", "Show Animations -1", "", "Animation ID: " + player.emote);
				Commands.executeCommand(player, "e" + space + player.emote + space + "plus" + space + "1", "admin");
				break;
			}
		break;

		/**
		 * Option two of five
		 */
		case 9191:
			switch (player.rottenPotatoOption) {
			case "choose_option_player":
				loadOnPlayerOptions(player, "monitor_player");
				break;
				
			case "choose_option_me":
				if (!player.getRights().isOrInherits(Right.OWNER)) {
					return;
				}
				loadPlayerOptions(player, "quick_spawn");
				break;
				
			case "punish_player":
				Commands.executeCommand(player, "jail" + split + username + split + "1440" + split + "Quick jail 'rotten potato'", "moderator");
				close(player);
				break;
				
			case "monitor_player":
				Commands.executeCommand(player, "checkinventory" + space + username, "moderator");
				break;
				
			case "move_player":
				Commands.executeCommand(player, "unjail" + space + username, "moderator");
				close(player);
				break;
				
			case "transform":
				final Range<Integer> HOLIDAY_ITEMS = Range.between(2706, 2707);
				Commands.executeCommand(player, "pnpc" + split + player.playerName + split + Misc.random(HOLIDAY_ITEMS), "admin");
				break;
				
			case "quick_spawn":
					Commands.executeCommand(player, "spawn" + space + "995" + space + "50m", "owner");
				break;
				
			case "interface_me":
				loadPlayerOptions(player, "debug_me");
				break;
				
			case "debug_me":
				loadPlayerOptions(player, "debug_animations");
				break;
			
			case "debug_graphics":
				player.getDH().sendOption5("Show Graphics +1", "Show Graphics +5", "Show Graphics -1", "", "Graphical ID: " + player.gfx);
				Commands.executeCommand(player, "gfx" + space + player.gfx + space + "plus" + space + "5", "owner");
				break;
				
			case "debug_animations":
				player.getDH().sendOption5("Show Animations +1", "Show Animations +5", "Show Animations -1", "", "Animation ID: " + player.emote);
				Commands.executeCommand(player, "e" + space + player.emote + space + "plus" + space + "5", "admin");
				break;
			}
		break;

		/**
		 * Option three of five
		 */
		case 9192:
			switch (player.rottenPotatoOption) {
			case "choose_option_player":
				loadOnPlayerOptions(player, "move_player");
				break;
				
			case "choose_option_me":
				loadPlayerOptions(player, "transform");
				break;
				
			case "punish_player":
				Commands.executeCommand(player, "ban" + split + username + split + "1440" + split + "Quick ban 'rotten potato'", "moderator");
				close(player);
				break;
				
			case "monitor_player":
				Commands.executeCommand(player, "xteleto" + space + username, "moderator");
				close(player);
				break;
				
			case "move_player":
				Commands.executeCommand(player, "questioning" + space + username, "moderator");
				close(player);
				break;
				
			case "transform":
				Commands.executeCommand(player, "pnpc" + split + player.playerName + split + "2704", "admin");
				break;
				
			case "interface_me":
//				Commands.executeCommand(player, "e", "admin");
//				player.getDH().sendOption5("Open Bank", "Show Graphic +1", "Show Animation +1", "", "Animation ID: " + player.emote);
			break;
			
			case "debug_graphics":
				player.getDH().sendOption5("Show Graphics +1", "Show Graphics +5", "Show Graphics -1", "", "Graphical ID: " + player.gfx);
				Commands.executeCommand(player, "gfx" + space + player.gfx + space + "minus" + space + "1", "owner");
				break;
				
			case "debug_animations":
				player.getDH().sendOption5("Show Animations +1", "Show Animations +5", "Show Animations -1", "", "Animation ID: " + player.emote);
				Commands.executeCommand(player, "e" + space + player.emote + space + "minus" + space + "1", "admin");
				break;
			}
		break;

		/**
		 * Option four of five
		 */
		case 9193:
			switch (player.rottenPotatoOption) {
			case "choose_option_player":
				//TODO:
				break;
				
			case "choose_option_me":
				//TODO:
				break;
				
			case "punish_player":
				Commands.executeCommand(player, "mute" + split + username + split + "1440" + split + "Quick mute 'rotten potato'", "moderator");
				close(player);
				break;
				
			case "monitor_player":
				Player chosen_player = PlayerHandler.getPlayer(username);
				player.getDH().sendStatement(username + " "+ (chosen_player.getBankPin().getPin().length() > 0 ? "@gre@does@bla@" : "@red@does not@bla@") +" have a bank-pin set");
				break;
				
			case "move_player":
				//TODO:
				break;
				
			case "transform":
				final Range<Integer> HILTS = Range.between(2709, 2712);
				Commands.executeCommand(player, "pnpc" + split + player.playerName + split + Misc.random(HILTS), "admin");
				break;
				
			case "debug_graphics":
				break;
				
			case "debug_animations":
				break;
			}
		break;

		/**
		 * Option five of five
		 */
		case 9194:
			switch (player.rottenPotatoOption) {
			case "choose_option_player":
				//TODO:
				break;
				
			case "choose_option_me":
				Commands.executeCommand(player, "empty", "admin");
				break;
				
			case "punish_player":
				//TODO:
				break;
				
			case "monitor_player":
				//TODO: 
				break;
				
			case "move_player":
				//TODO:
				break;
				
			case "transform":
				Commands.executeCommand(player, "unpc", "owner");
				break;
			}
		break;
		}
	}
}