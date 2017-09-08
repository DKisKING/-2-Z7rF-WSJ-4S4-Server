package osv.model.players.packets.commands.owner;

import osv.Server;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

public class Serverstatus extends Command {

	@Override
	public void execute(Player c, String input) {
		System.out.println(Server.getStatus());
		c.sendMessage("See console for server status information.");
	}

}
