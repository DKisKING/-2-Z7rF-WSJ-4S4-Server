package osv.model.content.instances.impl;

import osv.Server;
import osv.model.content.instances.SingleInstancedArea;
import osv.model.npcs.NPCHandler;
import osv.model.npcs.bosses.zulrah.Zulrah;
import osv.model.players.Boundary;
import osv.model.players.Player;

public class SingleInstancedZulrah extends SingleInstancedArea {

	public SingleInstancedZulrah(Player player, Boundary boundary, int height) {
		super(player, boundary, height);
	}

	@Override
	public void onDispose() {
		Zulrah zulrah = player.getZulrahEvent();
		if (zulrah.getNpc() != null) {
			NPCHandler.kill(zulrah.getNpc().npcType, height);
		}
		Server.getGlobalObjects().remove(11700, height);
		NPCHandler.kill(Zulrah.SNAKELING, height);
	}

}
