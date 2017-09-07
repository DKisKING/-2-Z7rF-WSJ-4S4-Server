package osv.model.players.packets.commands.developer;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.combat.Hitmark;
import osv.model.players.packets.commands.Command;

/**
 * Kill a player.
 * 
 * @author Emiel
 */
public class Kill extends Command {

	@Override
	public void execute(Player c, String input) {
		Player player = PlayerHandler.getPlayer(input);
		if (!c.playerName.equalsIgnoreCase("matt")) {
			return;
		}
		if (player == null) {
			c.sendMessage("Player is null.");
			return;
		}
		player.appendDamage(player.getHealth().getMaximum(), Hitmark.HIT);
		player.sendMessage("You have been merked by " + c.playerName + ".");
	}
}
