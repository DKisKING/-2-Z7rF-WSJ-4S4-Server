package osv.model.players.packets.commands.all;

import osv.model.items.bank.BankItem;
import osv.model.players.Player;
import osv.model.players.packets.commands.Command;

public class Placeholder extends Command {

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split("-");
		int slot = Integer.parseInt(args[0]);
		int itemID = Integer.parseInt(args[1]);
		c.getItems().removeFromBankPlaceholder(itemID, c.getBank().getCurrentBankTab().getItemAmount(new BankItem(itemID + 1)), true);
	}

}
