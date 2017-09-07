package osv.model.players.packets.commands.developer;

import java.util.Optional;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;
import osv.model.players.PlayerHandler;

/**
 * Change the level of a given players skill.
 * 
 * @author DK
 */
public class Sol extends Command {

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split("-");
		
		//Check correct number of input arguments to stop java errors
		if (args.length != 3) {
			c.sendMessage("Error. Correct syntax: ::sol-Player-skillid-level");
			return;
		}		

		//Defines target player, skill ID and desired level
		Player c2 = PlayerHandler.getPlayer(args[0]);
		String P2 = args[0];
		int SkillID = Integer.parseInt(args[1]);
		int SkillLevel = Integer.parseInt(args[2]);
		
		if (SkillID > 21){
			c.sendMessage("Error. Skill ID is greater than number of skills");
			return;
		}
		
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(args[0]);
		if (optionalPlayer.isPresent()) {
		
		//set's and updates target players skill
		c2.playerLevel[SkillID] = SkillLevel;
		c2.playerXP[SkillID] = c2.getPA().getXPForLevel(SkillLevel) + 1;
		c2.getPA().setSkillLevel(SkillID, c2.playerLevel[SkillID], c2.playerXP[SkillID]);
		c2.getPA().refreshSkill(SkillID);

		//displays message to both people
		c.sendMessage("You have set " + P2 + "'s skill id " + SkillID + " to level " + SkillLevel + ".");
		c2.sendMessage(c + " has set one of your skills to level " + SkillLevel + ".");
		
		} else {
			c.sendMessage(args[0] + " is not online. You can only change the stats of online players.");
		}
	}
}
