package osv.model.players.packets.commands.moderator;

import java.util.Optional;

import osv.Server;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.packets.commands.Command;

/**
 * Market Unmute a given player.
 * 
 * @author Emiel
 */
public class Marketunmute extends Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c2.marketMuteEnd = 0;
			c.sendMessage(c2.playerName + " has been unmuted on the market channel.");
			c2.sendMessage("@red@You have been unmuted by " + c.playerName + " on the market channel.");
			Server.getPunishmentLogHandler().logPunishment(c2, c, "Market Unmute", "");
		}
	}
}
