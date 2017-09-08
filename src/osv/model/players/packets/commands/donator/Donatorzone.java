package osv.model.players.packets.commands.donator;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Teleports the player to the donator zone.
 * 
 * @author Emiel
 */
public class Donatorzone extends Command {

	@Override
	public void execute(Player c, String input) {
		if (c.inTrade || c.inDuel || c.inWild()) {
			return;
		}
		c.getPA().startTeleport(2852, 2956, 0, "modern");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to the donator zone");
	}

}
