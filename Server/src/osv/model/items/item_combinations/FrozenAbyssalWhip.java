package osv.model.items.item_combinations;

import java.util.List;
import java.util.Optional;

import osv.model.items.GameItem;
import osv.model.items.ItemCombination;
import osv.model.players.Player;

public class FrozenAbyssalWhip extends ItemCombination {

	public FrozenAbyssalWhip(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(outcome.getId(), outcome.getAmount());
		player.getDH().sendItemStatement("You combined the items and created a frozen abyssal whip.", outcome.getId());
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Combining these are final.", "You cannot revert this.");
	}

}
