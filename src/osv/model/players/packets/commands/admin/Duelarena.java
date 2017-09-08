package osv.model.players.packets.commands.admin;

import osv.Config;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Toggles whether the Duel Arena is enabled or not.
 * 
 * @author Emiel
 */
public class Duelarena extends Command {

	@Override
	public void execute(Player c, String input) {
		Config.NEW_DUEL_ARENA_ACTIVE = Config.NEW_DUEL_ARENA_ACTIVE ? false : true;
		c.sendMessage("The duel arena is currently " + (Config.NEW_DUEL_ARENA_ACTIVE ? "Enabled" : "Disabled") + ".");
	}
}
