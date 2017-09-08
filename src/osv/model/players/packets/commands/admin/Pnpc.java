package osv.model.players.packets.commands.admin;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.packets.commands.Command;

/**
 * Transform a given player into an npc.
 * 
 * @author Emiel
 *
 */
public class Pnpc extends Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String args[] = input.split("-");
			if (args.length != 2) {
				throw new IllegalArgumentException();
			}
			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(args[0]);
			if (optionalPlayer.isPresent()) {
				Player c2 = optionalPlayer.get();
				int npc = Integer.parseInt(args[1]);
				if (npc > 7616) {
					c.sendMessage("Max npc id is: 7616");
					return;
				}
				c2.npcId2 = npc;
				c2.isNpc = true;
				c2.updateRequired = true;
				c2.appearanceUpdateRequired = true;
			} else {
				throw new IllegalStateException();
			}
		} catch (IllegalArgumentException e) {
			c.sendMessage("Error. Correct syntax: ::pnpc-player-npcid");
		} catch (IllegalStateException e) {
			c.sendMessage("You can only use the command on online players.");
		}
	}
}
