package osv.model.content.LootingBag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import osv.Server;
import osv.model.entity.Entity;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.Player;
import osv.model.players.PlayerSave;

/**
 * Looting bag functionality.
 * 
 * @author Sky
 */
public class LootingBag {
	
	public Player player;
	public List<LootingBagItem> items;

	public int selectedItem = -1;
	public int selectedSlot = -1;

	public LootingBag(Player player) {
		this.player = player;
		items = new ArrayList<>();
	}

	/**
	 * Checks wether or not a player is allowed to configure the looting bag
	 * @return
	 */
	public boolean configurationPermitted() {
		if (/*player.underAttackBy > 0 || player.underAttackBy2 > 0 ||*/ player.inDuelArena() || player.inPcGame()
				|| player.inPcBoat() || player.isInJail() || player.getInterfaceEvent().isActive()
				|| player.getPA().viewingOtherBank || player.isDead || player.viewingRunePouch) {
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

	/**
	 * Configuring the inventory of the looting bag on death
	 * @param player
	 */
	public void onDeath(Player player, String entity) {
		Entity killer = player.getKiller();

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

	/**
	 * The looting bag id
	 */
	public static final int LOOTING_BAG = 11941;
	
	public static boolean isLootingBag(Player player, int itemId) {
		return itemId == LOOTING_BAG;
	}

	public boolean handleButton(int buttonId) {
		if (buttonId == 142131) {
			closeLootbag();
			return true;
		}
		return false;
	}

	/**
	 * Opens the check looting bag interface
	 */
	public void openLootbag() {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		onClose();
		sendItems();
		player.setSidebarInterface(3, 37342);
		player.viewingLootBag = true;
	}

	/**
	 * Opens the deposit looting bag interface
	 */
	public void openLootbagAdd() {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (player.inClanWars() || player.inClanWarsSafe()) {
			return;
		}
		if (!player.inWild()) {
			player.sendMessage("You can only do this in the wilderness.");
			return;
		}
		onClose();
		sendInventoryItems();
		player.setSidebarInterface(3, 37343);
		player.addingItemsToLootBag = true;
	}

	/**
	 * Handles deposit and withdrawal of items
	 * @param id		The item being configured
	 * @param amount	The amount of the item being configured
	 * @return
	 */
	public boolean handleClickItem(int id, int amount) {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return false;
		}
	
		if (player.viewingLootBag) {
			removeMultipleItemsFromBag(id, amount);
			//withdraw(id, amount);
			return true;
		}
		if (player.addingItemsToLootBag) {
			deposit(id, amount);
			return true;
		}
		return false;
	}
	
	public void removeMultipleItemsFromBag(int id, int amount) {
		if (amount >= Integer.MAX_VALUE) {
			amount = countItems(id);
		}
		int count = 0;
		while (containsItem(id)) {
			if (!withdraw(id, amount)) {
				return;
			}
			if (player.getItems().isStackable(id)) {
				count += amount;
			} else {
				count++;
			}
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

	public int findIndexInLootBag(int id) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				return items.indexOf(item);
			}
		}
		return -1;
	}

	/**
	 * Handles withdrawal from the lootingbag
	 * @param id		The id of the item being withdrawn
	 * @param amount	The amount of the item being withdrawn
	 */
	public boolean withdraw(int id, int amount) {
		int index = findIndexInLootBag(id);
		int amountToAdd = 0;
		if (items.size() <= 0) {
			return false;
		}
		if (index == -1) {
			return false;
		}
		LootingBagItem item = items.get(index);
		if (item == null) {
			return false;
		}
		if (item == null || item.getId() <= 0 || item.getAmount() <= 0 || player.getItems().freeSlots() <= 0) {
			return false;
		}
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return false;
		}
		if (!player.inBank()) {
			player.sendMessage("You are only able to withdraw while in a bank.");
			return false;
		}
		if (player.getItems().getItemCount(id, false) + amount >= Integer.MAX_VALUE || player.getItems().getItemCount(id, false) + amount <= 0) {
			return false;
		}
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

	/**
	 * Handles depositing of items into the looting bag
	 * @param id		The id of the item being deposited
	 * @param amount	The amount of the item being deposited
	 */
	public void deposit(int id, int amount) {
		if (amount >= Integer.MAX_VALUE) {
			amount = player.getItems().getItemCount(id, false);
		}
		if (player.inClanWars() || player.inClanWarsSafe()) {
			return;
		}
        int bagSpotsLeft = 28 - items.size();
        boolean stackable = player.getItems().isStackable(id);
        boolean bagContainsItem = containsItem(id);
        if (amount > bagSpotsLeft) {
            if (!(stackable && bagContainsItem)) {
            amount = bagSpotsLeft;
            }
        }
		if (!player.getItems().playerHasItem(id)) {
			return;
		}
		if (!player.inWild()) {
			player.sendMessage("You can only do this in the wilderness.");
			return;
		}
		if (items.size() >= 28) {
			player.sendMessage("The bag cannot hold anymore items.");
			return;
		}
		if (id == 11942 || id == LOOTING_BAG) {
			player.sendMessage("You may be surprised to learn that bagception is not permitted.");
			return;
		}
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (!player.getItems().isTradable(id)) {
			player.sendMessage("This item is deemed untradable and cannot be put into the bag.");
			return;
		}
		if (countItems(id) + amount >= Integer.MAX_VALUE || countItems(id) + amount <= 0) {
			return;
		}
		List<Integer> amounts = player.getItems().deleteItemAndReturnAmount(id, amount);
		
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
					return true;
				}
			}
		}
		items.add(new LootingBagItem(id, amount));
		return true;
	}

	/**
	 * Closing the looting bag and resetting
	 */
	public void closeLootbag() {
		player.setSidebarInterface(3, 3213);
		player.viewingLootBag = false;
		player.addingItemsToLootBag = false;
		onClose();
	}

	/**
	 * Opens withdrawal mode
	 */
	public void openWithdrawalMode() {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (!player.getItems().playerHasItem(LOOTING_BAG)) {
			return;
		}
		openLootbag();
	}

	public void onClose() {
		player.viewingLootBag = false;
		player.addingItemsToLootBag = false;
	}

	/**
	 * Opens deposit mode
	 */
	public void openDepositMode() {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (!player.getItems().playerHasItem(LOOTING_BAG)) {
			return;
		}
		openLootbagAdd();
	}

	public void sendItems() {
		if (!player.getItems().playerHasItem(LOOTING_BAG)) {
			return;
		}
		final int START_ITEM_INTERFACE = 47342;
		for (int i = 0; i < 28; i++) {
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
			player.getPA().sendFrame34a(START_ITEM_INTERFACE + i, id, 0, amt);
		}
	}

	public void sendInventoryItems() {
		if (!player.getItems().playerHasItem(LOOTING_BAG)) {
			return;
		}
		final int START_ITEM_INTERFACE = 27342;
		for (int i = 0; i < 28; i++) {
			int id = 0;
			int amt = 0;

			if (i < player.playerItems.length) {
				id = player.playerItems[i];
				amt = player.playerItemsN[i];
			}

			player.getPA().sendFrame34a(START_ITEM_INTERFACE + i, id - 1, 0, amt);
		}
	}

	@SuppressWarnings("unused")
	private String getShortAmount(int amount) {
		if (amount <= 1) {
			return "";
		}
		String amountToString = "" + amount;
		if (amount > 1000000000) {
			amountToString = "@gre@" + (amount / 1000000000) + "B";
		} else if (amount > 1000000) {
			amountToString = "@gre@" + (amount / 1000000) + "M";
		} else if (amount > 1000) {
			amountToString = "@whi@" + (amount / 1000) + "K";
		}
		return amountToString;
	}

	private void resetItems() {
		player.getItems().resetItems(3214);
		player.getPA().requestUpdates();
	}
}
