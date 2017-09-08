package osv.model.players.packets.commands.moderator;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;

/**
 * Teleport to a given player.
 * 
 * @author Emiel
 */
public class Xteleto extends Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (!c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
				if (c2.inClanWars() || c2.inClanWarsSafe()) {
					c.sendMessage("@cr10@This player is currently at the pk district.");
					return;
				}
			}
			c.getPA().movePlayer(c2.getX(), c2.getY(), c2.heightLevel);
		} else {
			c.sendMessage(input + " is not line. You can only teleport to online players.");
		}
	}
}
