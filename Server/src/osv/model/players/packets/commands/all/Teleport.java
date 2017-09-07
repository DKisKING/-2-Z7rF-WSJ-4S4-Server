package osv.model.players.packets.commands.all;

import osv.model.content.teleportation.TeleportationInterface;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

public class Teleport extends Command {

	@Override
	public void execute(Player player, String input) {
		if (player.inClanWars() || player.inClanWarsSafe()) {
			player.sendMessage("@cr10@You can not do this here.");
			return;
		}
		TeleportationInterface.open(player);
	}

}
