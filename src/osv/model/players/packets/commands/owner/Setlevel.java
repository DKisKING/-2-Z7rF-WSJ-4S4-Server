package osv.model.players.packets.commands.owner;

import java.util.stream.IntStream;

import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

/**
 * Change the level of a given skill.
 * 
 * @author Emiel
 */
public class Setlevel extends Command {

	@Override
	public void execute(Player c, String input) {
		int skillId = -1;
		int skillLevel = -1;
		String[] args = input.split(" ");
		if (args.length < 2) {
			return;
		}
		if (args.length == 3) {
			IntStream.range(Integer.parseInt(args[0]), Integer.parseInt(args[1])).forEach(level -> {
				c.playerLevel[level] = Integer.parseInt(args[2]);
				c.playerXP[level] = c.getPA().getXPForLevel(Integer.parseInt(args[2])) + 1;
				c.getPA().refreshSkill(level);
				c.getPA().setSkillLevel(level, c.playerLevel[level], c.playerXP[level]);
			});
			c.sendMessage("You have set the skill id '" + Integer.parseInt(args[0]) + "' to '"
					+ Integer.parseInt(args[1]) + "' to level " + Integer.parseInt(args[2]) + ".");
		} else {
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
					c.sendMessage("Unable to set level, skill id cannot exceed the range of 0 -> "
							+ (c.playerLevel.length - 1) + ".");
					return;
				}
				if (skillLevel < 1) {
					skillLevel = 1;
					// } else if (skillLevel > 99) {
					// skillLevel = 99;
				}
				c.playerLevel[skillId] = skillLevel;
				c.playerXP[skillId] = c.getPA().getXPForLevel(skillLevel) + 1;
				c.getPA().refreshSkill(skillId);
				c.sendMessage("You have set the skill id '" + skillId + "' to level " + skillLevel + ".");
				c.getPA().setSkillLevel(skillId, c.playerLevel[skillId], c.playerXP[skillId]);
				c.getPA().levelUp(skillId);
			} catch (Exception e) {
				c.sendMessage("Error. Correct syntax: ::setlevel skillid level");
			}
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
