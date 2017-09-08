package osv.model.players.packets.commands.moderator;

import java.util.Optional;

import osv.Server;
import osv.model.multiplayer_session.MultiplayerSession;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;
import osv.punishments.Punishment;
import osv.punishments.PunishmentType;
import osv.punishments.Punishments;

/**
 * Ban a given player.
 * 
 * @author Emiel
 */
public class Ban extends Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String[] args = input.split("-");
			if (args.length != 3) {
				throw new IllegalArgumentException();
			}
			String name = args[0];
			int duration = Integer.parseInt(args[1]);
			String reason = args[2];
			long banEnd;
			if (duration == 0) {
				banEnd = Long.MAX_VALUE;
			} else {
				banEnd = System.currentTimeMillis() + duration * 1000 * 60;
			}
			Punishments punishments = Server.getPunishments();
			if (punishments.contains(PunishmentType.BAN, name)) {
				c.sendMessage("This player is already banned.");
				return;
			}
			Server.getPunishments().add(new Punishment(PunishmentType.BAN, banEnd, name));
			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(name);
			if (optionalPlayer.isPresent()) {
				Player c2 = optionalPlayer.get();
				if (!c.getRights().isOrInherits(Right.OWNER) && c2.getRights().isOrInherits(Right.ADMINISTRATOR)) {
					c.sendMessage("You cannot ban this player.");
					return;
				}
				if (Server.getMultiplayerSessionListener().inAnySession(c2)) {
					MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c2);
					session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				}
				c2.properLogout = true;
				c2.disconnected = true;
				if (duration == 0) {
					c.sendMessage(name + " has been permanently banned.");
					Server.getPunishmentLogHandler().logPunishment(c2, c, "Ban (Permanent)", reason);
				} else {
					c.sendMessage(name + " has been banned for " + duration + " minute(s).");
					Server.getPunishmentLogHandler().logPunishment(c2, c, "Ban (" + duration + ")", reason);
				}
				return;
			}
			if (duration == 0) {
				c.sendMessage(name + " has been permanently banned.");
				Server.getPunishmentLogHandler().logPunishment(name, c, "Ban (Permanent)", reason);
			} else {
				c.sendMessage(name + " has been banned for " + duration + " minute(s).");
				Server.getPunishmentLogHandler().logPunishment(name, c, "Ban (" + duration + ")", reason);
			}
		} catch (Exception e) {
			c.sendMessage("Correct usage: ::ban-player-duration-reason (0 as duration for permanent)");
		}
	}
}
