package osv.model.players.packets.commands.admin;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

public class Requestupdates extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("" + c.playerLevel[5] + "/" + c.getLevelForXP(c.playerXP[5]) + "", 687);// Prayer
	}

}
