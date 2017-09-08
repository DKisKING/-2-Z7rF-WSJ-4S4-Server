package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;

public class SaradominSwordBlessed extends Special {

	public SaradominSwordBlessed() {
		super(6.5, 2.0, 1.43, new int[] { 12809 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(1133);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
