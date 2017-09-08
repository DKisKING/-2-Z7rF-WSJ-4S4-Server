package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;
import osv.model.players.skills.Skill;

public class SaradominGodsword extends Special {

	public SaradominGodsword() {
		super(5.0, 1.35, 1.1, new int[] { 11806 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7058);
		player.gfx0(1209);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() < 1) {
			return;
		}
		if (damage.getAmount() < 21) {
			player.getHealth().increase(10);
			player.replenishSkill(Skill.PRAYER.getId(), 5);
		}
		if (damage.getAmount() > 20) {
			player.getHealth().increase(damage.getAmount() / 2);
			player.replenishSkill(Skill.PRAYER.getId(), damage.getAmount() / 4);
		}
	}

}
