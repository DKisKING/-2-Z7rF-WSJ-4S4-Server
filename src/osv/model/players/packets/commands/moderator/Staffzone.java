package osv.model.players.packets.commands.moderator;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Teleport the player to the staffzone.
 * 
 * @author Emiel
 */
public class Staffzone extends Command {

	@Override
	public void execute(Player c, String input) {
		if (c.inClanWars() || c.inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		c.getPA().startTeleport(3164, 3489, 2, "modern");
	}
}
