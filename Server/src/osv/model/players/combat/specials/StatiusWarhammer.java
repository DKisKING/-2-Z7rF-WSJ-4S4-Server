package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;

public class StatiusWarhammer extends Special {

	public StatiusWarhammer() {
		super(3.5, 1.25, 1.25, new int[] { 13902 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx0(1241);
		player.startAnimation(7296);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
