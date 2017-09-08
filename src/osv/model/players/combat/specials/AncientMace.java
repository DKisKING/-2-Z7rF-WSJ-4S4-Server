package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;

public class AncientMace extends Special {

	public AncientMace() {
		super(10.0, 1.60, 1.3, new int[] { 11061 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx0(1052);
		player.startAnimation(6147);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
