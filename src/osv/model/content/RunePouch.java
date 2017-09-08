package osv.model.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import osv.Server;
import osv.model.content.LootingBag.LootingBagItem;
import osv.model.entity.Entity;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.Player;
import osv.model.players.PlayerSave;

public class RunePouch {
	
	/**
	 * Checks wether or not a player is allowed to configure the looting bag
	 * @return
	 */
	public boolean configurationPermitted() {
		if (/*player.underAttackBy > 0 || player.underAttackBy2 > 0 ||*/ player.inDuelArena() || player.inPcGame()
				|| player.inPcBoat() || player.isInJail() || player.getInterfaceEvent().isActive()
				|| player.getPA().viewingOtherBank || player.isDead || player.viewingLootBag || player.addingItemsToLootBag) {
			return false;
		}

		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return false;
		}
		if (player.getTutorial().isActive()) {
			player.getTutorial().refresh();
			return false;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			player.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}

		if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
			player.sendMessage("You must decline the trade to start walking.");
			return false;
		}

		if (player.isStuck) {
			player.isStuck = false;
			player.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
			return false;
		}

		return true;
	}
	
	public Player player;
	public List<LootingBagItem> items;

	public static final int RUNE_POUCH_ID = 12791;
	public static final boolean CHECK_FOR_POUCH = true;

	final int START_ITEM_INTERFACE = 29908;
	final int START_INVENTORY_INTERFACE = 29880;

	public int selectedItem = -1;
	public int selectedSlot = -1;
	public int interfaceId = -1;

	public RunePouch(Player player) {
		this.player = player;
		items = new ArrayList<>();
	}

	// not used
	public int[] runes = new int[] { 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, };

	
	public void onDeath(Player o, String entity) {
		Entity killer = player.getKiller();
		
		if (o == null) {
			return;
		}
		for (Iterator<LootingBagItem> iterator = items.iterator(); iterator.hasNext();) {
			LootingBagItem item = iterator.next();

			if (item == null) {
				continue;
			}
			if (item.getId() <= 0 || item.getAmount() <= 0) {
				continue;
			}
			if (entity == "PVP") {
				if (killer != null && killer instanceof Player) {
					Player playerKiller = (Player) killer;
					if (playerKiller.getMode().isItemScavengingPermitted()) {
						Server.itemHandler.createGroundItem(playerKiller, item.getId(), player.getX(), player.getY(), player.heightLevel, item.getAmount(), player.killerId);
					} else {
						Server.itemHandler.createUnownedGroundItem(item.getId(), player.getX(), player.getY(), player.heightLevel, item.getAmount());
					}
				}
			} else {
				Server.itemHandler.createGroundItem(player, item.getId(), player.getX(), player.getY(),
						player.heightLevel, item.getAmount(), player.getIndex());
			}
			iterator.remove();
		}
		sendItems();
		PlayerSave.saveGame(player);
	}
	 

	public static boolean isRunePouch(Player player, int itemId) {
		return itemId == RUNE_POUCH_ID;
	}

	public boolean handleButton(int buttonId) {
		if (buttonId == 116181) {
			closeLootbag();
			return true;
		}
		return false;
	}

	public void openRunePouch() {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return;
		}
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		onClose();
		Player c =  player;
		sendItems();
		sendInventoryItems();
		c.getPA().showInterface(29875);
		player.viewingRunePouch = true;
	}

	public void withdrawAll() {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		for (Iterator<LootingBagItem> iterator = items.iterator(); iterator.hasNext();) {
			LootingBagItem item = iterator.next();
			if (!player.getItems().addItem(item.getId(), item.getAmount())) {
				break;
			}
			iterator.remove();
		}
		sendItems();
		sendInventoryItems();
	}
	public void removeMultipleItemsFromBag(int id, int amount) {
		if (amount >= Integer.MAX_VALUE) {
			amount = countItems(id);
		}
		int count = 0;
		while (containsItem(id)) {
			if (!removeItemFromRunePouch(id, amount)) {
				return;
			}
			count+=amount;
			if (count >= amount) {
				return;
			}
		}
	}	
	
	public boolean containsItem(int id) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}
	public boolean handleClickItem(int id, int amount, int interfaceId) {
		if (!player.viewingRunePouch) {
			return false;
		}
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return false;
		}
	
		if (interfaceId >= START_ITEM_INTERFACE) {
			//removeItemFromRunePouch(id, amount);
			removeMultipleItemsFromBag(id, amount);
			return true;
		} else {
			addItemToRunePouch(id, amount);
			return true;
		}
	}

	public int findIndexInLootBag(int id) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				return items.indexOf(item);
			}
		}
		return -1;
	}

	public boolean removeItemFromRunePouch(int id, int amount) {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return false;
		}
		if (items.size() <= 0) {
			return false;
		}
		int index = findIndexInLootBag(id);
		if (index == -1) {
			return false;
		}
		LootingBagItem item = items.get(index);
		if (item == null) {
			return false;
		}
		if (item.getId() <= 0 || item.getAmount() <= 0) {
			return false;
		}
		if (player.getItems().freeSlots() <= 0) {
			if (!(player.getItems().playerHasItem(id) && player.getItems().isStackable(id))) {
				return false;
			}
		}

		if (player.getItems().getItemCount(id) + amount >= Integer.MAX_VALUE ||player.getItems().getItemCount(id) + amount <= 0) {
			return false;
		}
		
		int amountToAdd = 0;
		if ((items.get(items.indexOf(item)).getAmount()) > amount) {
			amountToAdd = amount;
			items.get(items.indexOf(item)).incrementAmount(-amount);
		} else {
			amountToAdd = item.getAmount();
			items.remove(index);
		}

		player.getItems().addItem(item.getId(), amountToAdd);
		sendItems();
		sendInventoryItems();
		return true;
	}

	public void deleteItemFromRunePouch(int id, int amount) {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (items.size() <= 0) {
			return;
		}
		int index = findIndexInLootBag(id);
		if (index == -1) {
			return;
		}
		LootingBagItem item = items.get(index);
		if (item == null) {
			return;
		}
		if (item.getId() <= 0 || item.getAmount() <= 0) {
			return;
		}
		if ((items.get(items.indexOf(item)).getAmount()) > amount) {
			items.get(items.indexOf(item)).incrementAmount(-amount);
		} else {
			items.remove(index);
		}
		sendItems();
	}

	public boolean pouchContainsItem(int id) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean pouchContainsItem(int id, int amount) {
		for (LootingBagItem item : items) {
			if (item.getId() == id && item.getAmount() >= amount) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRunes(int runes, int amount) {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		if (runes <= 0 || amount <= 0) {
			return true;
		}
		if (!pouchContainsItem(runes, amount)) {
			return false;
		}
		return true;
	}

	public boolean hasRunes(int[] runes, int[] runeAmounts) {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		for (int i = 0; i < runes.length; i++) {
			if (!pouchContainsItem(runes[i], runeAmounts[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean deleteRunesOnCast(int runes, int runeAmounts) {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		if (!hasRunes(runes, runeAmounts)) {
			return false;
		}
		deleteItemFromRunePouch(runes, runeAmounts);
		return true;
	}

	public boolean deleteRunesOnCast(int[] runes, int[] runeAmounts) {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return false;
		}
		if (!hasRunes(runes, runeAmounts)) {
			return false;
		}
		for (int i = 0; i < runes.length; i++) {
			deleteItemFromRunePouch(runes[i], runeAmounts[i]);
		}
		return true;
	}

	public void addItemToRunePouch(int id, int amount) {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (amount >= Integer.MAX_VALUE) {
			amount = player.getItems().getItemCount(id);
		}
		if (id == RUNE_POUCH_ID) {
			player.sendMessage("Don't be silly.");
			return;
		}
		if (!(id >= 554 && id <= 566) && id != 9075) {
			player.sendMessage("You can only store runes in a rune pouch.");
			return;
		}
		if (items.size() >= 3 && !(pouchContainsItem(id) && player.getItems().isStackable(id))) {
			player.sendMessage("Pouch cannot hold more than 3 different runes.");
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		if (countItems(id) + amount >= Integer.MAX_VALUE || countItems(id) + amount <= 0) {
			return;
		}
		// int amt = player.getItems().deleteItemAndReturnAmount(id, amount);
		// addItemToList(id, amt);

		List<Integer> amounts = player.getItems().deleteItemAndReturnAmount(id, amount);
		/*for (int amt : amounts) {
			addItemToList(id, amt);
		}*/
		int count = 0;
		for (int amt : amounts) {
			if (!addItemToList(id, amt)) {
				break;
			}
			count++;
			if (count >= amount) {
				break;
			}
		}

		resetItems();
		sendItems();
		sendInventoryItems();
	}
	public int countItems(int id) {
		int count = 0;
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				count += item.getAmount();
			}
		}
		return count;
	}
	public boolean addItemToList(int id, int amount) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				if (item.getAmount() + amount >= Integer.MAX_VALUE) {
					return false;
				}
				if (player.getItems().isStackable(id)) {
					item.incrementAmount(amount);
					return false;
				}
			}
		}
		items.add(new LootingBagItem(id, amount));
		return true;
	}

	public void closeLootbag() {
		onClose();
	}

	public void withdraw() {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return;
		}
		openRunePouch();
	}

	public void onClose() {
		player.viewingRunePouch = false;
		player.getPA().closeAllWindows();
	}

	public void onLogin() {
		sendItems();
	}

	public void sendItems() {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return;
		}

		String sendSpells = "#";

		for (int i = 0; i < 3; i++) {
			int id = 0;
			int amt = 0;

			if (i < items.size()) {
				LootingBagItem item = items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
			}

			if (id <= 0) {
				id = -1;
			}
			player.getPA().sendFrame34a(START_ITEM_INTERFACE + i, id, 0 , amt);
			//PlayerFunction.itemOnInterface(c, START_ITEM_INTERFACE + i, 0, id, amt);
			if (id == -1)
				id = 0;
			if (i == 2) {
				sendSpells += id + ":" + amt;
			} else {
				sendSpells += id + ":" + amt + "-";
			}
		}
		sendSpells += "$";
		player.getPA().sendFrame126(sendSpells, 49999);
	}

	public void sendInventoryItems() {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) {
			return;
		}
		for (int i = 0; i < 28; i++) {
			int id = 0;
			int amt = 0;

			if (i < player.playerItems.length) {
				id = player.playerItems[i];
				amt = player.playerItemsN[i];
			}
			player.getPA().sendFrame34a(START_INVENTORY_INTERFACE + i, id - 1, 0 , amt);
		}
	}

	private void resetItems() {
		player.getItems().resetItems(3214);
		player.getPA().requestUpdates();
	}
}
