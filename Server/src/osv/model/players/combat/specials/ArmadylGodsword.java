package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;

public class ArmadylGodsword extends Special {

	public ArmadylGodsword() {
		super(5.0, 1.75, 1.375, new int[] { 11802 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7061);
		player.gfx0(1211);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
