package osv.model.players.packets.commands.all;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Open the forums in the default web browser.
 * 
 * @author Emiel
 */
public class Forums extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("www.os-v.org/forums", 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens a web page with our forums");
	}

}
