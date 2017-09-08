package osv.model.players.packets.commands.all;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Opens the store page in the default web browser.
 * 
 * @author Emiel
 */
public class Donate extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("www.os-v.org/store", 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens a web page with our donation store");
	}

}
