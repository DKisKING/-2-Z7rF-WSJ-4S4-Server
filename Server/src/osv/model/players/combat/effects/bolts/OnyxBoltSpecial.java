package osv.model.players.combat.effects.bolts;

import osv.Config;
import osv.model.npcs.NPC;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.DamageEffect;
import osv.model.players.combat.range.RangeExtras;
import osv.util.Misc;

public class OnyxBoltSpecial implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int change = Misc.random((int) (damage.getAmount() * 1.25));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 753, false);
		attacker.getHealth().increase(change / 4);
		attacker.getPA().refreshSkill(3);
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getNpcName() == null) {
			return;
		}
		if (Misc.linearSearch(Config.UNDEAD_IDS, defender.npcType) != -1) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount() * 1.25));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 753, false);
		attacker.getHealth().increase(change / 4);
		attacker.getPA().refreshSkill(3);
	}

	@Override
	public boolean isExecutable(Player operator) {
		return RangeExtras.boltSpecialAvailable(operator, 9245);
	}

}
