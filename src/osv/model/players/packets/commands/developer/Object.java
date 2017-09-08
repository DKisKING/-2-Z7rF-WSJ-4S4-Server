package osv.model.players.packets.commands.developer;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Spawn a specific Object.
 * 
 * @author Emiel
 *
 */
public class Object extends Command {

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split(" ");
		if (args.length < 2) {
			c.getPA().object(Integer.parseInt(args[0]), c.absX, c.absY, 0, 10);
			c.sendMessage("Object: " + Integer.parseInt(args[0]) + ", Type: 10");
		} else {
			c.getPA().object(Integer.parseInt(args[0]), c.absX, c.absY, 0, Integer.parseInt(args[1]));
			c.sendMessage("Object: " + Integer.parseInt(args[0]) + ", Type: " + Integer.parseInt(args[1]));
		}
	}
}
