package osv.model.players.packets.commands.donator;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Teleports the player to the donator zone.
 * 
 * @author Emiel
 */
public class Dz extends Command {

	@Override
	public void execute(Player c, String input) {
		if (c.inTrade || c.inDuel || c.inWild()) {
			return;
		}
		if (c.inClanWars() || c.inClanWarsSafe()) {
			c.sendMessage("@cr10@This player is currently at the pk district.");
			return;
		}
		c.getPA().startTeleport(2852, 2956, 0, "modern");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Same as @blu@::donatorzone@blu@");
	}

}
