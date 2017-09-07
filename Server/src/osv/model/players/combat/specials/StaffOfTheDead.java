package osv.model.players.combat.specials;

import osv.Server;
import osv.event.impl.StaffOfTheDeadEvent;
import osv.model.entity.Entity;
import osv.model.players.Player;
import osv.model.players.combat.Damage;
import osv.model.players.combat.Special;

public class StaffOfTheDead extends Special {

	public StaffOfTheDead() {
		super(10.0, 1.0, 1.0, new int[] { 11791, 12904 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx(1228, 255);
		Server.getEventHandler().stop(player, "staff_of_the_dead");
		Server.getEventHandler().submit(new StaffOfTheDeadEvent(player));
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() > 1) {
			player.gfx(1229, 355);
			damage.setAmount(damage.getAmount() / 2);
		}
	}

}
