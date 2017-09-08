package osv.model.players.packets;

import osv.model.players.PacketType;
import osv.model.players.Player;

public class ChangeRegion implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.getPA().removeObjects();
		// Server.objectManager.loadObjects(c);
	}

}
