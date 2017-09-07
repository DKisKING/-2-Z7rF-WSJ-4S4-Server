package osv.model.players.packets.commands.owner;

import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;

public class Addrights extends Command {

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split("-");
		if (args.length != 2) {
			c.sendMessage("The correct format is '::addrights-name-rights'.");
			return;
		}
		Player player = PlayerHandler.getPlayer(args[0]);
		if (player == null) {
			c.sendMessage("The player '" + args[0] + "' could not be found, try again.");
			return;
		}
		int rightValue;
		try {
			rightValue = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			c.sendMessage("The level of rights must be a whole number.");
			return;
		}
		Right right = Right.get(rightValue);
		if (right == null) {
			c.sendMessage("The level of rights you've requested is unknown.");
			return;
		}
		if (player.getRights().isOrInherits(right)) {
			c.sendMessage("That player already has this level of rights.");
			return;
		}
		player.getRights().add(right);
		player.getRights().updatePrimary();
		c.sendMessage("You have promoted " + args[0] + " to " + right.name() + ".");
	}

}
