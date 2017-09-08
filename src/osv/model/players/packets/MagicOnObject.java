package osv.model.players.packets;

import java.util.Objects;

import osv.Server;
import osv.model.multiplayer_session.MultiplayerSessionFinalizeType;
import osv.model.multiplayer_session.MultiplayerSessionStage;
import osv.model.multiplayer_session.MultiplayerSessionType;
import osv.model.multiplayer_session.duel.DuelSession;
import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.model.players.skills.crafting.OrbCharging;

public class MagicOnObject implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
//		int x = c.getInStream().readSignedWordBigEndian();
//		int y = c.getInStream().readSignedWordBigEndianA();
//		int magicId = c.getInStream().readUnsignedWord();
		
//		c.objectX = c.getInStream().readSignedWordBigEndianA();
//		c.objectId = c.getInStream().readUnsignedWord();
//		c.objectY = c.getInStream().readUnsignedWordA();
		
		int x = c.getInStream().readSignedWordBigEndian();
		int magicId = c.getInStream().readUnsignedWord();
		int y = c.getInStream().readUnsignedWordA();
		int objectId = c.getInStream().readSignedWordBigEndian();
		

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
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		
		c.turnPlayerTo(x, y);

		System.out.println("Spell ID: " + magicId);
		System.out.println("Object used on: X: " + x + ", Y: " + y + ", ID: " + objectId);
		
		c.usingMagic = true;
		
		switch (objectId) {
		case 2153:
		case 2152:
		case 2151:
		case 2150:
			OrbCharging.chargeOrbs(c, magicId, objectId);
			break;
		}

	}

}
