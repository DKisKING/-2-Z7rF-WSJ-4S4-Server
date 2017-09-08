package osv.model.players.packets.commands.helper;

import osv.model.content.help.HelpDatabase;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Opens an interface containing all help tickets.
 * 
 * @author Emiel
 */
public class Helpdb extends Command {

	@Override
	public void execute(Player c, String input) {
		HelpDatabase.getDatabase().openDatabase(c);
	}
}
