package osv.model.players.packets.commands.developer;

import java.util.Optional;

import osv.model.items.ItemAssistant;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.model.players.packets.commands.Command;
import osv.util.Misc;

/**
 * Give a certain amount of an item to a player.
 * 
 * @author Emiel
 */
public class Giveitem extends Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String args[] = input.split("-");
			if (args.length != 3) {
				throw new IllegalArgumentException();
			}
			String playerName = args[0];
			int itemID = Integer.parseInt(args[1]);
			int amount = Misc.stringToInt(args[2]);

			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(playerName);

			if (optionalPlayer.isPresent()) {
				Player c2 = optionalPlayer.get();

/*				if (c2.getMode().isIronman() || c2.getMode().isUltimateIronman()) {
					if (!c.getRights().isOrInherits(Right.OWNER)) {
						c.sendMessage("You cannot give items to these players because of their respective game modes.");
						return;
					}
				}*/

				if (c2.getItems().freeSlots() > 1) {
					c2.getItems().addItem(itemID, amount);
					c2.sendMessage("You have just been given " + amount + " of item: " + ItemAssistant.getItemName(itemID) + " by: " + Misc.optimizeText(c.playerName));
				} else {
					c2.getItems().addItemToBank(itemID, amount);
					c2.sendMessage("You have just been given " + amount + " of item: " + ItemAssistant.getItemName(itemID) + " by: " + Misc.optimizeText(c.playerName));
					c2.sendMessage("It is in your bank because you didn't have enough space in your inventory.");
				}
				c.sendMessage("You have just given " + amount + " of item number: " + ItemAssistant.getItemName(itemID) + ".");

			} else {
				c.sendMessage(playerName + " is not online.");
			}
		} catch (Exception e) {
			c.sendMessage("Error. Correct syntax: ::giveitem-player-itemid-amount");
		}
	}
}
