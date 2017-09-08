package osv.model.players.packets.commands.moderator;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;

/**
 * Shows the IP and Mac address of a given player.
 * 
 * @author Emiel
 */
public class Info extends Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (c2.getRights().contains(Right.MODERATOR)) {
				c.sendMessage("You cannot do this to a member of staff.");
				return;
			}
			c.sendMessage("<col=CC0000>IP of " + c2.playerName + " : " + c2.connectedFrom);
			c.sendMessage("<col=CC0000>Mac Address of " + c2.playerName + " : " + c2.getMacAddress());
			c.sendMessage("<col=CC0000>Connected from:");
			for (String connected : c2.lastConnectedFrom) {
				c.sendMessage("<col=CD1000> > " + connected);
			}
		} else {
			c.sendMessage(input + " is not online. You can request the info of online players.");
		}
	}
}
