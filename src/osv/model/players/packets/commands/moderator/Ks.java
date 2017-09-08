package osv.model.players.packets.commands.moderator;

import java.util.Optional;

import osv.model.content.kill_streaks.Killstreak;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.packets.commands.Command;

/**
 * Shows the killstreaks of a given player.
 * 
 * @author Emiel
 */
public class Ks extends Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c.sendMessage("Hunter killstreak of " + c2.playerName + " : " + c2.getKillstreak().getAmount(Killstreak.Type.HUNTER));
			c.sendMessage("Rogue killstreak of " + c2.playerName + " : " + c2.getKillstreak().getAmount(Killstreak.Type.ROGUE));
		}
	}
}
