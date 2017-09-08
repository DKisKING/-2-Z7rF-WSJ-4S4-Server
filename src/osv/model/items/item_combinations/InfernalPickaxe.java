package osv.model.items.item_combinations;

import java.util.List;
import java.util.Optional;

import osv.model.items.GameItem;
import osv.model.items.ItemCombination;
import osv.model.players.Player;

public class InfernalPickaxe extends ItemCombination {

	public InfernalPickaxe(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		if (player.playerLevel[13] < 85) {
			player.sendMessage("You must have a smithing level of at least 85 to do this.");
			return;
		}
		items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(outcome.getId(), outcome.getAmount());
		//emote 4514
		player.startAnimation(4514);
		player.getDH().sendItemStatement("You combined the items and created an infernal axe.", outcome.getId());
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Combining these are final.", "You cannot revert this.");
	}

}
