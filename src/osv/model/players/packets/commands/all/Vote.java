package osv.model.players.packets.commands.all;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Opens the vote page in the default web browser.
 * 
 * @author Emiel
 */
public class Vote extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendString("http://osv.motivoters.com/motivote/", 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens a web page where you can vote for rewards");
	}

}
