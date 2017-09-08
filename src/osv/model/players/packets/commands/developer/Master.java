package osv.model.players.packets.commands.developer;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Set's all player skills to 99.
 * 
 * @author DK
 */
public class Master extends Command {

	@Override
	public void execute(Player c, String input) {

		//Loops through all skills and sets them to 99
		for (int i = 0; i < 22; i++) {
			c.playerLevel[i] = 99;
			c.playerXP[i] = c.getPA().getXPForLevel(99) + 1;
			c.getPA().setSkillLevel(i, c.playerLevel[i], c.playerXP[i]);
			c.getPA().refreshSkill(i);
		}
		//displays message to player
		c.sendMessage("Successfully set all skills to 99.");
		
	}
}
