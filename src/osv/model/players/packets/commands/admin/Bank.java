package osv.model.players.packets.commands.admin;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Open the banking interface.
 * 
 * @author Emiel
 */
public class Bank extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().openUpBank();
	}
}
