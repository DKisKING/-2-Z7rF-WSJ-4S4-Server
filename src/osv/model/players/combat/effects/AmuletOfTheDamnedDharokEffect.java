package osv.model.players.combat.effects;

import osv.model.items.EquipmentSet;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.CombatType;
import osv.model.players.combat.Damage;
import osv.model.players.combat.DamageEffect;
import osv.model.players.combat.Hitmark;
import osv.util.Misc;

/**
 * The Amulet of the damned has an effect when worn with the entire Dharok set. There is a 1/4 chance that during the damage step the attacker will receive 15% of the damage they
 * dealt as a recoil.
 * 
 * @author Jason MacKeigan
 * @date Nov 25, 2014, 2:53:48 AM
 */
public class AmuletOfTheDamnedDharokEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int damageDealt = (int) Math.floor(damage.getAmount() * .15);
		if (damageDealt < 1) {
			return;
		}
		defender.getDamageQueue().add(new Damage(attacker, damageDealt, 1, attacker.playerEquipment, Hitmark.HIT, CombatType.MELEE));
	}

	@Override
	public boolean isExecutable(Player operator) {
		return EquipmentSet.DHAROK.isWearingBarrows(operator) && operator.getItems().isWearingItem(12853) && Misc.random(100) < 25;
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {

	}

}
