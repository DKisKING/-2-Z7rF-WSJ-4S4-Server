package osv.model.players.packets;

import java.util.Objects;

import osv.Server;
import osv.model.content.staff.RottenPotato;
import osv.model.items.ItemAssistant;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;
import osv.util.Misc;

public class ItemOnPlayer implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		@SuppressWarnings("unused")
		int interfaceId = c.getInStream().readUnsignedWordBigEndianA();
		int playerIndex = c.getInStream().readUnsignedWord();
		int itemId = c.getInStream().readUnsignedWord();
		int slotId = c.getInStream().readUnsignedWordBigEndian();
		c.setItemOnPlayer(null);
		if (c.teleTimer > 0)
			return;
		if (playerIndex > PlayerHandler.players.length) {
			return;
		}
		if (slotId > c.playerItems.length) {
			return;
		}
		if (PlayerHandler.players[playerIndex] == null) {
			return;
		}
		if (!c.getItems().playerHasItem(itemId, 1, slotId)) {
			return;
		}
		Player other = PlayerHandler.players[playerIndex];
		if (other == null) {
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (other.getBankPin().requiresUnlock()) {
			return;
		}
		if (c.getTutorial().isActive()) {
			c.getTutorial().refresh();
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (other.getInterfaceEvent().isActive()) {
			c.sendMessage("This player is busy.");
			return;
		}
		if (Misc.distanceBetween(c, other) > 15) {
			c.sendMessage("You need to move closer to do this.");
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		c.setItemOnPlayer(other);
		if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && itemId != 5733 && itemId != 6713) {
			c.sendMessage("You gave " + other.playerName + " some " + ItemAssistant.getItemName(itemId) + ".");
			other.sendMessage("You were given some " + ItemAssistant.getItemName(itemId) + " from " + c.playerName + ".");
			other.getItems().addItem(itemId, c.getItems().isStackable(itemId) ? c.getItems().getItemAmount(itemId) : 1);
			c.getItems().deleteItem(itemId, c.getItems().isStackable(itemId) ? c.getItems().getItemAmount(itemId) : 1);
		}
		switch (itemId) {
		case 962:
			if (other.connectedFrom.equalsIgnoreCase(c.connectedFrom)) {
				c.sendMessage("You cannot use this on another player that is on the same host as you.");
				return;
			}
			c.turnPlayerTo(other.getX(), other.getY());
			c.getDH().sendDialogues(612, -1);
			break;
			
		case 13345:
			if (other.connectedFrom.equalsIgnoreCase(c.connectedFrom)) {
				c.sendMessage("You cannot use this on another player that is on the same host as you.");
				return;
			}
			if (!c.getItems().playerHasItem(13345)) {
				return;
			}
			if (other.getItems().freeSlots() == 0) {
				c.sendMessage("This player does not have any space.");
				return;
			}
			c.turnPlayerTo(other.getX(), other.getY());
			c.getItems().deleteItem(13345, 1);
			int random = Misc.random(4);
			switch (random) {
			case 0:
			case 1:
			case 2:
				c.sendMessage("How unlucky, this present was empty.");
				other.sendMessage(""+c.playerName+" used a present on you and it was empty!");
				break;
				
			case 3:
				other.getItems().addItem(13343, 1);
				c.sendMessage("How unlucky, "+other.playerName+" got the Black Santa Hat!");
				other.sendMessage(""+c.playerName+" used a present on you and you got a Black Santa Hat!");
				break;
				
			case 4:
				c.getItems().addItem(13343, 1);
				c.sendMessage("You were lucky enough to find a Black Santa Hat in the present!");
				other.sendMessage(""+c.playerName+" used a present on you and got a Black Santa Hat!");
				break;
		}
			break;
			
		case 13346:
			if (other.connectedFrom.equalsIgnoreCase(c.connectedFrom)) {
				c.sendMessage("You cannot use this on another player that is on the same host as you.");
				return;
			}
			if (!c.getItems().playerHasItem(13346)) {
				return;
			}
			if (other.getItems().freeSlots() == 0) {
				c.sendMessage("This player does not have any space.");
				return;
			}
			c.turnPlayerTo(other.getX(), other.getY());
			c.getItems().deleteItem(13346, 1);
			int random_hat = Misc.random(4);
			switch (random_hat) {
				case 0:
				case 1:
				case 2:
					c.sendMessage("How unlucky, this present was empty.");
					other.sendMessage(""+c.playerName+" used a present on you and it was empty!");
					break;
					
				case 3:
					other.getItems().addItem(13344, 1);
					c.sendMessage("How unlucky, "+other.playerName+" got the Inverted Santa Hat!");
					other.sendMessage(""+c.playerName+" used a present on you and you got a Inverted Santa Hat!");
					break;
					
				case 4:
					c.getItems().addItem(13344, 1);
					c.sendMessage("You were lucky enough to find a Inverted Santa Hat in the present!");
					other.sendMessage(""+c.playerName+" used a present on you and got a Inverted Santa Hat!");
					break;
			}
			break;
			
		case 5733:
			if (!c.getRights().isOrInherits(Right.MODERATOR)) {
				c.getItems().deleteItem(5733, 10);
				return;
			}
			RottenPotato.useItem(c, other);
			RottenPotato.username = other.playerName;
			c.turnPlayerTo(other.getX(), other.getY());
			break;
			
		case 6713:
			if (!c.getRights().isOrInherits(Right.HELPER)) {
				c.getItems().deleteItem(6713, 10);
				return;
			}
			c.stopMovement();
			c.wrenchUsername = other.playerName;
			c.getPA().sendFrame126("Checking user: " + other.playerName + " | General Info", 36002);
			c.getPA().sendFrame126("Check Bank", 36008);
			c.getPA().sendFrame126("Check SafeBox", 36009);
			c.getPA().sendFrame126("Check Zulrah", 36010);
			c.getPA().sendFrame126("Check Cerberus", 36011);
			c.getPA().sendFrame126("Check Loot Bag", 36012);
			c.getPA().sendFrame126("Check Rune Pouch", 36013);
			c.getPA().sendFrame126("Kick", 36014);
			c.getPA().sendFrame126("", 36015);
			for (int i = 0; i < other.playerEquipment.length; i++) {
				c.getPA().itemOnInterface(-1, -1, 36081, i);
				if (c.playerEquipment[i] == -1) {
					c.getPA().itemOnInterface(-1, -1, 36081, i);
				}
				c.getPA().itemOnInterface(other.playerEquipment[i], other.playerEquipmentN[i], 36081, i);
			}
			for (int i = 0; i < 28; i++) {
				c.getPA().itemOnInterface(-1, -1, 36083, i);
				c.getPA().itemOnInterface(other.playerItems[i] - 1, other.playerItemsN[i], 36083, i);
			}
			for (int i = 0; i < other.playerLevel.length; i++) {
				c.getPA().sendFrame126("" + other.playerLevel[i], 36049 + i);
			}
			c.getPA().showInterface(36000);
			break;
		}
	}
}