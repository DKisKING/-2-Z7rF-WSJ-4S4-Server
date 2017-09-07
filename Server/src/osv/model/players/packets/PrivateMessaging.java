package osv.model.players.packets;

import java.util.Objects;

import osv.Config;
import osv.Server;
import osv.ServerState;
import osv.model.players.PacketType;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.PlayerSave;
import osv.punishments.PunishmentType;
import osv.util.Misc;

/**
 * Private messaging, friends etc
 **/
public class PrivateMessaging implements PacketType {

	public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215, CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 74,
			ADD_IGNORE = 133;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		switch (packetType) {

		case ADD_FRIEND:
			c.getFriends().add(c.getInStream().readLong());
			break;

		case SEND_PM:
			if (System.currentTimeMillis() < c.muteEnd) {
				c.sendMessage("You are muted for breaking a rule.");
				return;
			}
			if (Server.getPunishments().contains(PunishmentType.NET_MUTE, c.connectedFrom)) {
				c.sendMessage("Your entire network has been muted. Other players cannot see your message.");
				return;
			}
			c.muteEnd = 0;
			final long recipient = c.getInStream().readLong();
			int pm_message_size = packetSize - 8;
			final byte pm_chat_message[] = new byte[pm_message_size];
			c.getInStream().readBytes(pm_chat_message, pm_message_size, 0);
			c.getFriends().sendPrivateMessage(recipient, pm_chat_message);
			if (Objects.nonNull(PlayerHandler.getPlayerByLongName(recipient)) && Objects.nonNull(c)) {
				System.out.println(c.playerName + " PM: " + Misc.decodeMessage(pm_chat_message, pm_chat_message.length));
				if (Config.SERVER_STATE == ServerState.PUBLIC_PRIMARY) {
					Server.getChatLogHandler().logMessage(c, "Private Message",
							PlayerHandler.getPlayerByLongName(recipient).playerName,
							Misc.decodeMessage(pm_chat_message, pm_chat_message.length));
				}
			}
			break;

		case REMOVE_FRIEND:
			c.getFriends().remove(c.getInStream().readLong());
			PlayerSave.saveGame(c);
			break;

		case REMOVE_IGNORE:
			c.getIgnores().remove(c.getInStream().readLong());
			break;

		case CHANGE_PM_STATUS:
			c.getInStream().readUnsignedByte();
			c.setPrivateChat(c.getInStream().readUnsignedByte());
			c.getInStream().readUnsignedByte();
			c.getFriends().notifyFriendsOfUpdate();
			break;

		case ADD_IGNORE:
			c.getIgnores().add(c.getInStream().readLong());
			break;

		}

	}
}
