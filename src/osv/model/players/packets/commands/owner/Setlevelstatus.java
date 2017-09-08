package osv.model.players.packets.commands.owner;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Change the level of a given skill.
 * 
 * @author Emiel
 */
public class Setlevelstatus extends Command {

	@Override
	public void execute(Player c, String input) {
		int skillId = -1;
		int skillLevel = -1;
		String[] args = input.split(" ");
		if (args.length < 2) {
			return;
		}
		try {
			skillLevel = Integer.parseInt(args[1]);
			try {
				skillId = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				int sID = getSkillID(args[0]);
				if (sID >= 0) {
					skillId = sID;
				} else {
					c.getDH().sendStatement("You must enter a skill name or skill id.");
				}
			}
			if (skillId < 0 || skillId > c.playerLevel.length - 1) {
				c.sendMessage("Unable to set level, skill id cannot exceed the range of 0 -> " + (c.playerLevel.length - 1) + ".");
				return;
			}
			if (skillLevel < 1) {
				skillLevel = 0;
			}
			if (skillId == 3) {
				c.getHealth().setAmount(skillLevel);
			} else {
				c.playerLevel[skillId] = skillLevel;
			}
			c.getPA().refreshSkill(skillId);
			c.sendMessage("You have set the status of skill id '" + skillId + "' to " + skillLevel + ".");
		} catch (Exception e) {
			c.sendMessage("Error. Correct syntax: ::setlevelstatus skillid level");
		}
	}

	private int getSkillID(String type) {
		if (type.equals("attack"))
			return 0;
		else if (type.equals("defence"))
			return 1;
		else if (type.equals("strength"))
			return 2;
		else if (type.equals("hitpoints"))
			return 3;
		else if (type.equals("ranged"))
			return 4;
		else if (type.equals("prayer"))
			return 5;
		else if (type.equals("magic"))
			return 6;
		else if (type.equals("cooking"))
			return 7;
		else if (type.equals("woodcutting"))
			return 8;
		else if (type.equals("fletching"))
			return 9;
		else if (type.equals("fishing"))
			return 10;
		else if (type.equals("firemaking"))
			return 11;
		else if (type.equals("crafting"))
			return 12;
		else if (type.equals("smithing"))
			return 13;
		else if (type.equals("mining"))
			return 14;
		else if (type.equals("herblore"))
			return 15;
		else if (type.equals("agility"))
			return 16;
		else if (type.equals("thieving"))
			return 17;
		else if (type.equals("slayer"))
			return 18;
		else if (type.equals("farming"))
			return 19;
		else if (type.equals("runecrafting"))
			return 20;
		else
			return -1;
	}
}
