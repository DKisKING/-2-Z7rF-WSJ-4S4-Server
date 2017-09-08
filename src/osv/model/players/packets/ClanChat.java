package osv.model.players.packets;

import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.util.Misc;

/**
 * Chat
 **/
public class ClanChat implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		String textSent = Misc.longToPlayerName2(c.getInStream().readLong());
		textSent = textSent.replaceAll("_", " ");
	}
}