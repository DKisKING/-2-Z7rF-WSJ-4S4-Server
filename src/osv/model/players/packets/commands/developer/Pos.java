package osv.model.players.packets.commands.developer;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Show the current position.
 * 
 * @author Emiel
 *
 */
public class Pos extends Command {

	@Override
	public void execute(Player player, String input) {
		player.sendMessage("Current coordinates x: " + player.absX + " y:" + player.absY + " h:" + player.heightLevel);
	}
}
