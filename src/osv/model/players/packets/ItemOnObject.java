package osv.model.players.packets;

/**
 * @author Ryan / Lmctruck30
 */

import java.util.Objects;

import osv.Server;
import osv.event.CycleEvent;
import osv.event.CycleEventContainer;
import osv.event.CycleEventHandler;
import osv.model.items.UseItem;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.PacketType;
import osv.model.players.Player;

public class ItemOnObject implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		/*
		 * a = ? b = ?
		 */

		@SuppressWarnings("unused")
		int a = c.getInStream().readUnsignedWord();
		int objectId = c.getInStream().readSignedWordBigEndian();
		int objectY = c.getInStream().readSignedWordBigEndianA();
		@SuppressWarnings("unused")
		int b = c.getInStream().readUnsignedWord();
		int objectX = c.getInStream().readSignedWordBigEndianA();
		int itemId = c.getInStream().readUnsignedWord();
		if (!c.getItems().playerHasItem(itemId, 1)) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getTutorial().isActive()) {
			c.getTutorial().refresh();
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
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
		// UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);

		switch (c.objectId) {
		
		case 18818:
		case 409:
			c.objectDistance = 3;
			break;
			
		case 884:
			c.objectDistance = 5;
			c.objectXOffset = 3;
			c.objectYOffset = 3;
			break;
			
		default:
			c.objectDistance = 1;
			c.objectXOffset = 0;
			c.objectYOffset = 0;
			break;

		}
		if (c.goodDistance(objectX + c.objectXOffset, objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
			c.turnPlayerTo(objectX, objectY);
			UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
		} else {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.goodDistance(objectX + c.objectXOffset, objectY + c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
						c.turnPlayerTo(objectX, objectY);
						UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
						container.stop();
					}
				}

				@Override
				public void stop() {
					c.clickObjectType = 0;
				}
			}, 1);
		}

	}

}
