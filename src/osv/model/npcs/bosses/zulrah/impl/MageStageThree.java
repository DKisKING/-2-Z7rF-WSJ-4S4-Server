package osv.model.npcs.bosses.zulrah.impl;

import osv.event.CycleEventContainer;
import osv.model.npcs.bosses.zulrah.Zulrah;
import osv.model.npcs.bosses.zulrah.ZulrahLocation;
import osv.model.npcs.bosses.zulrah.ZulrahStage;
import osv.model.players.Player;
import osv.model.players.combat.CombatType;

public class MageStageThree extends ZulrahStage {

	public MageStageThree(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		zulrah.getNpc().setFacePlayer(true);
		if (zulrah.getNpc().totalAttacks > 5) {
			player.getZulrahEvent().changeStage(4, CombatType.RANGE, ZulrahLocation.WEST);
			zulrah.getNpc().totalAttacks = 0;
			container.stop();
			return;
		}
	}

}
