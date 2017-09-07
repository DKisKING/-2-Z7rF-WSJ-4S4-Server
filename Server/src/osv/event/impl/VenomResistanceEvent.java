package osv.event.impl;

import osv.event.Event;
import osv.model.entity.Entity;
import osv.model.entity.Health;
import osv.model.entity.HealthStatus;

public class VenomResistanceEvent extends Event<Entity> {

	public VenomResistanceEvent(Entity attachment, int ticks) {
		super("venom_resistance_event", attachment, ticks);
	}

	@Override
	public void execute() {
		super.stop();
		if (attachment == null) {
			return;
		}
		Health health = attachment.getHealth();
		health.removeNonsusceptible(HealthStatus.VENOM);
	}

}
