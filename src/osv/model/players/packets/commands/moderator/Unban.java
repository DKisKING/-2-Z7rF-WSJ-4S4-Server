package osv.model.players.packets.commands.moderator;

import osv.Server;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;
import osv.punishments.Punishment;
import osv.punishments.PunishmentType;
import osv.punishments.Punishments;

/**
 * Unbans a given player.
 * 
 * @author Emiel
 */
public class Unban extends Command {

	@Override
	public void execute(Player c, String input) {
		Punishments punishments = Server.getPunishments();
		Punishment punishment = punishments.getPunishment(PunishmentType.BAN, input);

		if (!punishments.contains(PunishmentType.BAN, input) || punishment == null) {
			c.sendMessage("A punishment could not be found for: " + input);
			return;
		}

		punishments.remove(punishment);
		c.sendMessage("You have successfully removed " + input + " from the ban list.");
		Server.getPunishmentLogHandler().logPunishment(input, c, "Unban", "");
	}
}
