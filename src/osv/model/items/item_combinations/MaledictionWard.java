package osv.model.items.item_combinations;

import java.util.List;
import java.util.Optional;

import osv.model.items.GameItem;
import osv.model.items.ItemCombination;
import osv.model.players.Player;

public class MaledictionWard extends ItemCombination {

	public MaledictionWard(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(super.outcome.getId(), super.outcome.getAmount());
		player.getDH().sendItemStatement("You combined the items and created the Malediction ward (or).", 12806);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("A Malediction ward (or) is untradeable.", "You can revert this but you will lose the upgrade kit.");
	}

}
