package osv.model.content.staff;

import java.util.Optional;

import osv.model.content.LootingBag.LootingBagItem;
import osv.model.items.GameItem;
import osv.model.items.bank.BankItem;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.packets.Commands;

public class StaffInterface {
	
	public static int bankTabId = 0;
	
	public static void checkInfo(Player player) {
		Optional<Player> other = PlayerHandler.getOptionalPlayer(player.wrenchUsername);
		if (!other.isPresent()) { 
			player.sendMessage("This player is currently unavailable.");
			return;
		}
		player.stopMovement();
		player.getPA().sendFrame126("Checking user: " + player.wrenchUsername + " | General Info", 36002);
		player.getPA().sendFrame126("Check Bank", 36008);
		player.getPA().sendFrame126("Check SafeBox", 36009);
		player.getPA().sendFrame126("Check Zulrah", 36010);
		player.getPA().sendFrame126("Check Cerberus", 36011);
		player.getPA().sendFrame126("Check Loot Bag", 36012);
		player.getPA().sendFrame126("Check Rune Pouch", 36013);
		player.getPA().sendFrame126("Kick", 36014);
		player.getPA().sendFrame126("", 36015);
		for (int i = 0; i < other.get().playerEquipment.length; i++) {
			player.getPA().itemOnInterface(-1, -1, 36081, i);
			if (player.playerEquipment[i] == -1) {
				player.getPA().itemOnInterface(-1, -1, 36081, i);
			}
			player.getPA().itemOnInterface(other.get().playerEquipment[i], other.get().playerEquipmentN[i], 36081, i);
		}
		for (int i = 0; i < 28; i++) {
			player.getPA().itemOnInterface(-1, -1, 36083, i);
			player.getPA().itemOnInterface(other.get().playerItems[i] - 1, other.get().playerItemsN[i], 36083, i);
		}
		for (int i = 0; i < other.get().playerLevel.length; i++) {
			player.getPA().sendFrame126("" + other.get().playerLevel[i], 36049 + i);
		}
		player.getPA().showInterface(36000);
	}
	
	public static void checkBank(Player player, int tabId) {
		bankTabId = tabId;
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(player.wrenchUsername);
		player.getPA().sendFrame126("Checking user: " + player.wrenchUsername + " | Bank Tab: " + tabId, 36102);
		player.getPA().sendFrame126("Back", 36106);
		player.getPA().sendFrame126("Kick", 36107);
		player.getPA().sendFrame126("", 36108);
		player.getPA().sendFrame126("", 36109);
		player.getPA().sendFrame126("", 36110);
		player.getPA().sendFrame126("", 36111);
		player.getPA().sendFrame126("Next Tab", 36112);
		player.getPA().sendFrame126("Previous Tab", 36113);
		int id = 0;
		if (!optionalPlayer.isPresent()) { 
			player.sendMessage("This player is currently unavailable.");
			return;
		}
		for (int i = 0; i < 125; i++) {
			player.getPA().itemOnInterface(-1, -1, 36183, i);
		}
		for (BankItem item : optionalPlayer.get().getBank().getBankTab(tabId).getItems()) {
			if (item.getId() == -1) {
				continue;
			}
			if (item.getAmount() == 0) {
				continue;
			}
			player.getPA().itemOnInterface(item.getId() - 1, item.getAmount(), 36183, id);
			id++;
			if (id >= 125) {
				return;
			}
		}
		player.getPA().showInterface(36100);
	}
	
	public static void checkSafeBox(Player player) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(player.wrenchUsername);
		player.getPA().sendFrame126("Checking user: " + player.wrenchUsername + " | Safe Box", 36102);
		player.getPA().sendFrame126("Back", 36106);
		player.getPA().sendFrame126("Kick", 36107);
		player.getPA().sendFrame126("", 36108);
		player.getPA().sendFrame126("", 36109);
		player.getPA().sendFrame126("", 36110);
		player.getPA().sendFrame126("", 36111);
		player.getPA().sendFrame126("", 36112);
		player.getPA().sendFrame126("", 36113);
		if (!optionalPlayer.isPresent()) { 
			player.sendMessage("This player is currently unavailable.");
			return;
		}
		for (int i = 0; i < 100; i++) {
			player.getPA().itemOnInterface(-1, -1, 36183, i);
		}
		for (int i = 0; i < optionalPlayer.get().safeBoxSlots; i++) {
			int id = 0;
			int amt = 0;

			if (i < optionalPlayer.get().getSafeBox().items.size()) {
				LootingBagItem item = optionalPlayer.get().getSafeBox().items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
			}

			if (id <= 0) {
				id = -1;
			}
			player.getPA().itemOnInterface(id, amt, 36183, i);
		}
		player.getPA().showInterface(36100);
	}
	
	public static void checkLootingBag(Player player) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(player.wrenchUsername);
		player.getPA().sendFrame126("Checking user: " + player.wrenchUsername + " | Looting Bag", 36102);
		player.getPA().sendFrame126("Back", 36106);
		player.getPA().sendFrame126("Kick", 36107);
		player.getPA().sendFrame126("", 36108);
		player.getPA().sendFrame126("", 36109);
		player.getPA().sendFrame126("", 36110);
		player.getPA().sendFrame126("", 36111);
		player.getPA().sendFrame126("", 36112);
		player.getPA().sendFrame126("", 36113);
		if (!optionalPlayer.isPresent()) { 
			player.sendMessage("This player is currently unavailable.");
			return;
		}
		for (int i = 0; i < 100; i++) {
			player.getPA().itemOnInterface(-1, -1, 36183, i);
		}
		for (int i = 0; i < 28; i++) {
			int id = 0;
			int amt = 0;

			if (i < optionalPlayer.get().getLootingBag().items.size()) {
				LootingBagItem item = optionalPlayer.get().getLootingBag().items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
			}

			if (id <= 0) {
				id = -1;
			}
			player.getPA().itemOnInterface(id, amt, 36183, i);
		}
		player.getPA().showInterface(36100);
	}
	
	public static void checkRunePouch(Player player) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(player.wrenchUsername);
		player.getPA().sendFrame126("Checking user: " + player.wrenchUsername + " | Rune Pouch", 36102);
		player.getPA().sendFrame126("Back", 36106);
		player.getPA().sendFrame126("Kick", 36107);
		player.getPA().sendFrame126("", 36108);
		player.getPA().sendFrame126("", 36109);
		player.getPA().sendFrame126("", 36110);
		player.getPA().sendFrame126("", 36111);
		player.getPA().sendFrame126("", 36112);
		player.getPA().sendFrame126("", 36113);
		if (!optionalPlayer.isPresent()) { 
			player.sendMessage("This player is currently unavailable.");
			return;
		}
		for (int i = 0; i < 100; i++) {
			player.getPA().itemOnInterface(-1, -1, 36183, i);
		}
		for (int i = 0; i < 3; i++) {
			int id = 0;
			int amt = 0;

			if (i < optionalPlayer.get().getRunePouch().items.size()) {
				LootingBagItem item = optionalPlayer.get().getRunePouch().items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
			}

			if (id <= 0) {
				id = -1;
			}
			player.getPA().itemOnInterface(id, amt, 36183, i);
		}
		player.getPA().showInterface(36100);
	}
	
	public static void checkZulrah(Player player) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(player.wrenchUsername);
		player.getPA().sendFrame126("Checking user: " + player.wrenchUsername + " | Zulrah Tab", 36102);
		player.getPA().sendFrame126("Back", 36106);
		player.getPA().sendFrame126("Kick", 36107);
		player.getPA().sendFrame126("", 36108);
		player.getPA().sendFrame126("", 36109);
		player.getPA().sendFrame126("", 36110);
		player.getPA().sendFrame126("", 36111);
		player.getPA().sendFrame126("", 36112);
		player.getPA().sendFrame126("", 36113);
		if (!optionalPlayer.isPresent()) { 
			player.sendMessage("This player is currently unavailable.");
			return;
		}
		int id = 0;
		for (int i = 0; i < 100; i++) {
			player.getPA().itemOnInterface(-1, -1, 36183, i);
		}
		for (GameItem item : player.getZulrahLostItems()) {
			player.getPA().itemOnInterface(item.getId(), item.getAmount(), 36183, id);
			id++;
		}
		player.getPA().showInterface(36100);
	}
	
	public static void checkCerberus(Player player) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(player.wrenchUsername);
		player.getPA().sendFrame126("Checking user: " + player.wrenchUsername + " | Cerberus Tab", 36102);
		player.getPA().sendFrame126("Back", 36106);
		player.getPA().sendFrame126("Kick", 36107);
		player.getPA().sendFrame126("", 36108);
		player.getPA().sendFrame126("", 36109);
		player.getPA().sendFrame126("", 36110);
		player.getPA().sendFrame126("", 36111);
		player.getPA().sendFrame126("", 36112);
		player.getPA().sendFrame126("", 36113);
		if (!optionalPlayer.isPresent()) { 
			player.sendMessage("This player is currently unavailable.");
			return;
		}
		int id = 0;
		for (int i = 0; i < 100; i++) {
			player.getPA().itemOnInterface(-1, -1, 36183, i);
		}
		for (GameItem item : player.getCerberusLostItems()) {
			player.getPA().itemOnInterface(item.getId(), item.getAmount(), 36183, id);
			id++;
		}
		player.getPA().showInterface(36100);
	}
	
	public static void kickPlayer(Player player) {
		Commands.executeCommand(player, "kick" + " " + player.wrenchUsername, "helper");
	}
	
}
