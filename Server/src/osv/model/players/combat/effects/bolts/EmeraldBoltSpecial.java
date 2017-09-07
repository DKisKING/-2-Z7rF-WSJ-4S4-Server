package osv.model.players.combat.effects.bolts;

import java.util.Optional;

import osv.model.entity.HealthStatus;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.DamageEffect;
import osv.model.players.combat.range.RangeExtras;
import osv.util.Misc;

public class EmeraldBoltSpecial implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int change = Misc.random((int) (damage.getAmount()));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 752, true);
		defender.getHealth().proposeStatus(HealthStatus.POISON, 5, Optional.empty());
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getNpcName() == null) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount()));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 752, true);
	}

	@Override
	public boolean isExecutable(Player operator) {
		return RangeExtras.boltSpecialAvailable(operator, 9241);
	}

}
