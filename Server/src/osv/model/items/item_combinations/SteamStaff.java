package osv.model.items.item_combinations;

import java.util.List;
import java.util.Optional;

import osv.model.items.GameItem;
import osv.model.items.ItemCombination;
import osv.model.players.Player;

public class SteamStaff extends ItemCombination {

	public SteamStaff(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(super.outcome.getId(), super.outcome.getAmount());
		player.getDH().sendItemStatement("You combined the items and created the Mystic Steam Staff (or).", 12796);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("The Mystic Steam Staff (or) is untradeable.", "You can revert this but you will lose the upgrade kit.");
	}

}
