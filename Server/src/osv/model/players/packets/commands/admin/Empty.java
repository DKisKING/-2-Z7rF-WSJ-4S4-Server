package osv.model.players.packets.commands.admin;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Empty the inventory of the player.
 * 
 * @author Emiel
 */
public class Empty extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().removeAllItems();
		c.sendMessage("You empty your inventory.");
	}
}
