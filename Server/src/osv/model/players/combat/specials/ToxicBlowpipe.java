package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;
import osv.model.players.combat.range.RangeData;

public class ToxicBlowpipe extends Special {

	public ToxicBlowpipe() {
		super(5.0, 1.35, 1.5, new int[] { 12926 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(5061);
		player.projectileStage = 1;
		if (player.playerIndex > 0 && !player.usingSpecial) {
			player.getCombat().fireProjectilePlayer(0);
		} else if (player.npcIndex > 0 && !player.usingSpecial) {
			player.getCombat().fireProjectileNpc(0);
		}
		if (player.fightMode == 2) {
			player.attackTimer--;
		}
		if (target instanceof NPC && player.npcIndex > 0) {
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 1043, 28, 28, 37, 10);
		} else if (target instanceof Player && player.playerIndex > 0) {
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 1043, 28, 31, 37, 10);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() > 0) {
			player.getHealth().increase(damage.getAmount() / 2);
		}
	}

}
