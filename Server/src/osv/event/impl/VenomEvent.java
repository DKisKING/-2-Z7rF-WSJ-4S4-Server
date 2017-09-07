package osv.event.impl;

import java.util.Optional;

import osv.event.Event;
import osv.model.entity.Entity;
import osv.model.entity.Health;
import osv.model.entity.HealthStatus;
import osv.model.players.combat.Hitmark;

public class VenomEvent extends Event<Entity> {

	private int damage;

	private Optional<Entity> inflictor;

	public VenomEvent(Entity attachment, int damage, Optional<Entity> inflictor) {
		super("health_status", attachment, 33);
		this.damage = damage;
		this.inflictor = inflictor;
	}

	@Override
	public void execute() {
		if (attachment == null) {
			super.stop();
			return;
		}

		Health health = attachment.getHealth();

		if (health.isNotSusceptibleTo(HealthStatus.VENOM)) {
			super.stop();
			return;
		}

		if (attachment.getHealth().getAmount() <= 0) {
			super.stop();
			return;
		}

		attachment.appendDamage(damage, Hitmark.VENOM);
		inflictor.ifPresent(inf -> attachment.addDamageTaken(inf, damage));

		damage += 2;

		if (damage > 20) {
			damage = 20;
		}
	}

}
