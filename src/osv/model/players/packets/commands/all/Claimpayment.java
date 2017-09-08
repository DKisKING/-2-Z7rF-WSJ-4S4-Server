package osv.model.players.packets.commands.all;

import java.util.Optional;

import osv.database.DonationQuery;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Checks if the player has unclaimed donations.
 * 
 * @author Emiel
 *
 */
public class Claimpayment extends Command {

	@Override
	public void execute(Player player, String input) {
		player.sendMessage("Checking the database for unclaimed donations...");
		new DonationQuery(player);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Checks if you have unclaimed donations");
	}

}
