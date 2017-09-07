package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;
import osv.model.players.combat.range.RangeData;

public class Ballista extends Special {

	public Ballista() {
		super(6.5, 2.0, 1.5, new int[] { 19478, 19481 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.usingBow = true;
		player.rangeItemUsed = player.playerEquipment[player.playerArrows];
		player.startAnimation(7222);
		if (player.playerIndex > 0 && target instanceof Player) {
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, player.getCombat().getRangeProjectileGFX(), 43, 31, 37, 10);
		} else if (player.npcIndex > 0 && target instanceof NPC) {
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, player.getCombat().getRangeProjectileGFX(), 43, 31, 37, 10);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}
}
