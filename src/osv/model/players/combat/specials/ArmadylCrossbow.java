package osv.model.players.combat.specials;

import osv.model.entity.Entity;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;
import osv.model.players.combat.range.RangeData;

/**
 * 
 * @author Jason MacKeigan
 * @date Apr 4, 2015, 2015, 11:44:39 PM
 */
public class ArmadylCrossbow extends Special {

	public ArmadylCrossbow() {
		super(4.0, 2.0, 1.0, new int[] { 11785 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.usingBow = true;
		player.rangeItemUsed = player.playerEquipment[player.playerArrows];
		player.startAnimation(4230);
		if (player.playerIndex > 0 && target instanceof Player) {
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 301, 43, 31, 37, 10);
		} else if (player.npcIndex > 0 && target instanceof NPC) {
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 301, 43, 31, 37, 10);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
