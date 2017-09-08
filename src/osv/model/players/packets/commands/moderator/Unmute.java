package osv.model.players.packets.commands.moderator;

import java.util.Optional;

import osv.Server;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.packets.commands.Command;
import osv.punishments.Punishment;
import osv.punishments.PunishmentType;

/**
 * Unmute a given player.
 * 
 * @author Emiel
 */
public class Unmute extends Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();

			Punishment punishment = Server.getPunishments().getPunishment(PunishmentType.MUTE, c2.playerName);

			if (punishment == null) {
				c.sendMessage("This player is not muted.");
				return;
			}

			Server.getPunishments().remove(punishment);
			c2.muteEnd = 0;
			c.sendMessage(c2.playerName + " has been unmuted.");
			c2.sendMessage("@red@You have been unmuted by " + c.playerName + ".");
			Server.getPunishmentLogHandler().logPunishment(c2, c, "Unmute", "");
		}
	}
}
