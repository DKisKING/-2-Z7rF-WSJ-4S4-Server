package osv.model.players.packets.commands.moderator;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.packets.commands.Command;

public class Maxslayer extends Command {
	
	@Override
	public void execute(Player c, String input) {
		Optional<Player> op = PlayerHandler.nonNullStream().filter(Objects::nonNull).max(Comparator.comparing(client -> client.getSlayer().getPoints()));
		if (op.isPresent()) {
			c.sendMessage("Highest slayer points: " + op.get().playerName + " - " + op.get().getSlayer().getPoints());
		}
	}

}
