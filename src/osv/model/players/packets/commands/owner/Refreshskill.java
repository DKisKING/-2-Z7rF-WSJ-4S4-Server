package osv.model.players.packets.commands.owner;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

public class Refreshskill extends Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().refreshSkill(Integer.parseInt(input));
	}

}
