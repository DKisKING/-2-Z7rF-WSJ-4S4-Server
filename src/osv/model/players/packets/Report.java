package osv.model.players.packets;

import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.util.Misc;

public class Report implements PacketType {

	@SuppressWarnings("unused")
	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		String player = Misc.longToReportPlayerName(c.inStream.readQWord2()).replaceAll("_", " ");
		byte rule = (byte) c.inStream.readUnsignedByte();
	}

}