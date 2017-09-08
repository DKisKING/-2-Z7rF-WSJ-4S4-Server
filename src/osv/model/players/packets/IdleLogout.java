package osv.model.players.packets;

import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.util.Misc;

public class IdleLogout implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
//		if (c.underAttackBy > 0) {
//			return;
//		}
//		try {
//			c.isIdle = true;
//			c.logout();
//			c.disconnected = true;
//			Misc.println(c.playerName + " is idle, kicked.");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}