package osv.model.players.packets;

import osv.model.players.PacketType;
import osv.model.players.Player;

public class BankModifiableX implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		@SuppressWarnings("unused")
		int slot = player.getInStream().readUnsignedWordA();
		int component = player.getInStream().readUnsignedWord();
		int item = player.getInStream().readUnsignedWordA();
		int amount = player.getInStream().readInteger();
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}
		if (player.getTutorial().isActive()) {
			player.getTutorial().refresh();
			return;
		}
		if (amount <= 0)
			return;
		switch (component) {
		case 5382:
			if (player.getBank().getBankSearch().isSearching()) {
				player.getBank().getBankSearch().removeItem(item, amount);
				return;
			}
			player.getItems().removeFromBank(item, amount, true);
			break;
		}
	}

}
