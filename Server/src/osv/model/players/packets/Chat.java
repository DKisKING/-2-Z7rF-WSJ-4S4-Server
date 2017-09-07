package osv.model.players.packets;

import osv.Config;
import osv.Server;
import osv.ServerState;
import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.punishments.PunishmentType;
import osv.util.Misc;

/**
 * Chat
 **/
public class Chat implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.setChatTextEffects(c.getInStream().readUnsignedByteS());
		c.setChatTextColor(c.getInStream().readUnsignedByteS());
		c.setChatTextSize((byte) (c.packetSize - 2));
		c.inStream.readBytes_reverseA(c.getChatText(), c.getChatTextSize(), 0);

		if (Server.getPunishments().contains(PunishmentType.NET_MUTE, c.connectedFrom)) {
			c.sendMessage("Your entire network has been muted. Other players cannot see your message.");
			return;
		}

		if (Server.getPunishments().contains(PunishmentType.MUTE, c.playerName)) {
			c.sendMessage("You are currently muted. Other players cannot see your message.");
			return;
		}

		if (System.currentTimeMillis() < c.muteEnd) {
			c.sendMessage("You are currently muted. Other players cannot see your message.");
			return;
		}
		String message = Misc.decodeMessage(c.getChatText(), c.getChatTextSize());
		
		if (Config.SERVER_STATE == ServerState.PUBLIC_PRIMARY) {
			Server.getChatLogHandler().logMessage(c, "Chat", "", message);
		}
		c.setChatTextUpdateRequired(true);
	}
}
