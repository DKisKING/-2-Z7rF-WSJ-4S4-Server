package osv.model.npcs.bosses.zulrah;

import osv.event.CycleEvent;
import osv.model.players.Player;

public abstract class ZulrahStage extends CycleEvent {

	protected Zulrah zulrah;

	protected Player player;

	public ZulrahStage(Zulrah zulrah, Player player) {
		this.zulrah = zulrah;
		this.player = player;
	}

}
