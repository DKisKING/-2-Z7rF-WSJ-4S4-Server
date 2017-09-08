package osv.model.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import osv.Server;
import osv.model.content.LootingBag.LootingBagItem;
import osv.model.entity.Entity;
import osv.model.items.ItemAssistant;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.Player;
import osv.model.players.PlayerSave;

public class HerbSack {
	/**
	 * Checks wether or not a player is allowed to configure the herb sack
	 * @return
	 */
	public boolean configurationPermitted() {
		if (player.inDuelArena() || player.inPcGame() || player.inPcBoat() || player.isInJail() || player.getInterfaceEvent().isActive() || player.getPA().viewingOtherBank
				|| player.isDead || player.viewingLootBag || player.addingItemsToLootBag) {
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
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
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

	/**
	 * The herb sack id and boolean to set if we want to check if a player has a herb sack
	 */
	public static final int HERB_SACK_ID = 13226;
	public static final boolean CHECK_FOR_SACK = true;

	/**
	 * The herb sack class
	 * @param player
	 */
	public HerbSack(Player player) {
		this.player = player;
		items = new ArrayList<>();
	}

	/**
	 * Handles players death with a herb sack in their inventory
	 * @param o
	 * @param entity
	 */
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
		PlayerSave.saveGame(player);
	}
	
	/**
	 * Attempts to withdraw all herbs from the herb sack
	 */
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
	}

	/**
	 * The id's of the herbs you are allowed to store in the herb sack
	 */
	public int[] cleanHerbs = new int[] { 249, 251, 253, 255, 257, 2998, 259, 261, 263, 3000, 265, 2481, 267, 269 };
	
	/**
	 * Attempts to fill the sack with the herbs a player has in their inventory
	 */
	public void fillSack() {
		for (int i = 0; i < cleanHerbs.length; i++) {
			if (player.getItems().playerHasItem(cleanHerbs[i], 1)) {
				addItemToHerbSack(cleanHerbs[i], player.getItems().getItemAmount(cleanHerbs[i]));
			}
		}
	}

	/**
	 * Attempts  to add the herbs chosen to the herb sack
	 * @param id
	 * @param amount
	 */
	public void addItemToHerbSack(int id, int amount) {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (player.getItems().isStackable(id)) {
			return;
		}
		if (amount >= 28) {
			amount = player.getItems().getItemCount(id, false);
		}
		if (id == HERB_SACK_ID) {
			player.sendMessage("Don't be silly.");
			return;
		}
		if (!(id >= 249 && id <= 269) && id != 2481 && id != 3000 && id != 2998) {
			player.sendMessage("You can only store clean herbs in the herb sack.");
			return;
		}
		if (items.size() >= 14 && !(sackContainsItem(id) && player.getItems().isStackable(id))) {
			player.sendMessage("Herb sack cannot hold more than 14 different herbs.");
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		if (countItems(id) + amount >= 51 || countItems(id) + amount <= 0) {
			return;
		}
		player.sendMessage("Filled the sack with x" + amount + " " + ItemAssistant.getItemName(id));
		for (int amt = 0; amt < amount; amount--) {
			player.getItems().deleteItem(id, 1);
			addItemToList(id + 1, 1);
		}
	}
	
	/**
	 * Checks the amount and of what herb you have stored in the sack
	 */
	public void check() {
		int frame = 8149;
		int totalAmount = 0;
		for (int i2 = 8144; i2 < 8195; i2++) {
			player.getPA().sendFrame126("", i2);
		}
		player.getPA().sendFrame126("@dre@                   Herb Sack", 8144);
		player.getPA().sendFrame126("", 8145);
		player.getPA().sendFrame126("", 8148);
		for (int i = 0; i < 14; i++) {
			int id = 0;
			int amt = 0;

			if (i < items.size()) {
				LootingBagItem item = items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
				totalAmount += amt;
				player.getPA().sendFrame126("@red@Total Amount: "+totalAmount+"/700", 8147);
				player.getPA().sendFrame126("@blu@x" + amt + " " + ItemAssistant.getItemName(id) + "", frame);
				frame++;
			}
			player.getPA().showInterface(8134);
		}
	}
	
	/**
	 * Checks if the sack contains a certain herb
	 * @param id
	 * @return
	 */
	public boolean sackContainsItem(int id) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Counts the amount of a certain herb
	 * @param id
	 * @return
	 */
	public int countItems(int id) {
		int count = 0;
		for (LootingBagItem item : items) {
			if (item.getId() == id + 1) {
				count += item.getAmount();
			}
		}
		return count;
	}
	
	/**
	 * Adds the herbs to a list
	 * @param id
	 * @param amount
	 * @return
	 */
	public boolean addItemToList(int id, int amount) {
		for (LootingBagItem item : items) {
			if (item.getId() == id) {
				if (item.getAmount() + amount >= 51) {
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
}
