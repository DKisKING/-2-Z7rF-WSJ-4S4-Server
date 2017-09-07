package osv.model.npcs.bosses.zulrah.impl;

import osv.event.CycleEventContainer;
import osv.model.npcs.bosses.zulrah.Zulrah;
import osv.model.npcs.bosses.zulrah.ZulrahLocation;
import osv.model.npcs.bosses.zulrah.ZulrahStage;
import osv.model.players.Player;
import osv.model.players.combat.CombatType;

public class MeleeStageTen extends ZulrahStage {

	public MeleeStageTen(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (zulrah.getNpc().totalAttacks > 1 && zulrah.getNpc().attackTimer == 9) {
			player.getZulrahEvent().changeStage(11, CombatType.RANGE, ZulrahLocation.NORTH);
			zulrah.getNpc().totalAttacks = 0;
			zulrah.getNpc().setFacePlayer(true);
			container.stop();
			return;
		}
	}
}
