package osv.model.players.combat.effects.bolts;

import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.DamageEffect;
import osv.model.players.combat.range.RangeExtras;
import osv.util.Misc;

public class DragonBoltSpecial implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		if (defender.antifireDelay > 0 || defender.getItems().isWearingAnyItem(11283, 11284, 1540)) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount() * 1.45));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 756, false);
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getNpcName() != null && defender.getDefinition().getNpcName().toLowerCase().contains("dragon")) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount() * 1.45));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 756, false);
	}

	@Override
	public boolean isExecutable(Player operator) {
		return RangeExtras.boltSpecialAvailable(operator, 9244);
	}

}
