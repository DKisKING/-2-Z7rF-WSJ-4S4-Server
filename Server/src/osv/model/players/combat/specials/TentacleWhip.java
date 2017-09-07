package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;

public class TentacleWhip extends Special {

	public TentacleWhip() {
		super(5.0, 1.25, 1.1, new int[] { 12006 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(1658);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (target instanceof NPC) {
			((NPC) target).gfx100(341);
			((NPC) target).freezeTimer = 5;
		} else if (target instanceof Player) {
			Player t = ((Player) target);
			t.gfx100(341);
			t.freezeTimer = 5;
		}
	}

}
