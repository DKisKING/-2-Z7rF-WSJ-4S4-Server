package osv.model.players.combat.effects;

import java.util.Objects;

import osv.model.items.EquipmentSet;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.CombatType;
import osv.model.players.combat.Damage;
import osv.model.players.combat.DamageEffect;
import osv.model.players.combat.Hitmark;
import osv.util.Misc;

/**
 * The Amulet of the Damned has a special effect when wearing full kerils and combating another player. The effect has a 1/4 chance of executing which deals 1/2 of the damage dealt
 * to the player in the last damage step.
 * 
 * @author Jason MacKeigan
 * @date Nov 25, 2014, 2:52:54 AM
 */
public class AmuletOfTheDamnedKarilEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		if (Objects.isNull(attacker) || Objects.isNull(defender) || Objects.isNull(damage)) {
			return;
		}
		if (damage.getAmount() < 2) {
			return;
		}
		int appendedDamage = damage.getAmount() / 2;
		attacker.getDamageQueue().add(new Damage(defender, appendedDamage, 1, attacker.playerEquipment, Hitmark.HIT, CombatType.RANGE));
	}

	@Override
	public boolean isExecutable(Player operator) {
		return EquipmentSet.KARIL.isWearingBarrows(operator) && Misc.random(100) < 25;
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {

	}

}
