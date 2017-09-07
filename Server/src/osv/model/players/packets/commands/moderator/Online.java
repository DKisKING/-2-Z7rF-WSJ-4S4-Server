package osv.model.players.packets.commands.moderator;

import java.util.List;
import java.util.stream.Collectors;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;

public class Online extends Command {

	@Override
	public void execute(Player c, String input) {
		List<Player> matches = PlayerHandler.nonNullStream().filter(player -> player.connectedFrom.equalsIgnoreCase(input) || player.getMacAddress().equalsIgnoreCase(input))
				.collect(Collectors.toList());
		if (matches.size() <= 0) {
			c.sendMessage("There are no players online with the ip: " + input);
			return;
		}
		if (matches.stream().anyMatch(player -> player.getRights().isOrInherits(Right.MODERATOR))) {
			c.sendMessage("One of the players that match the credentials is a staff member.");
			return;
		}
		c.sendMessage("Matches found: " + matches.size());
		matches.forEach(player -> c.sendMessage("> " + player.playerName + ": " + player.getMacAddress() + "."));
	}

}
