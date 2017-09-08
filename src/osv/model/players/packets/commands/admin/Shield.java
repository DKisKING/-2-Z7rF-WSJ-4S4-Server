package osv.model.players.packets.commands.admin;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * LOOK MOM! I'M A SHIELD!
 * 
 * @author Emiel
 */
public class Shield extends Command {

	@Override
	public void execute(Player c, String input) {
		if (c.isNpc && c.npcId2 == 336) {
			c.isNpc = false;
		} else {
			c.npcId2 = 336;
			c.isNpc = true;
		}
		c.updateRequired = true;
		c.appearanceUpdateRequired = true;
	}
}
