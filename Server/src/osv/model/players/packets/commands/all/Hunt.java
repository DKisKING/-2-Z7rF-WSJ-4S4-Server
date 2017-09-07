package osv.model.players.packets.commands.all;

import java.util.Optional;

import osv.Server;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Teleport the player to the Hunting Grounds.
 * 
 * @author DK
 */
public class Hunt extends Command {

	@Override
	public void execute(Player c, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.inClanWars() || c.inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		if (c.inWild()) {
			return;
		}
		c.getPA().spellTeleport(2525, 2916, 0);
		
	}	
	
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Welcome Home Of Tyras");
	}

}
