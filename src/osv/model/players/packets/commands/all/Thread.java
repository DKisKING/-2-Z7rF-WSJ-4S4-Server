package osv.model.players.packets.commands.all;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

public class Thread extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("www.os-v.org/forums/showthread.php?" + input, 12000);
		c.sendMessage("Attempting to open thread: " + input);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens up a thread by its ID");
	}

}
