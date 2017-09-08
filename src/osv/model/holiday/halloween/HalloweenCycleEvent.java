package osv.model.holiday.halloween;

import osv.event.CycleEvent;
import osv.event.CycleEventContainer;
import osv.model.holiday.HolidayController;
import osv.model.players.PlayerHandler;

public class HalloweenCycleEvent extends CycleEvent {

	@Override
	public void execute(CycleEventContainer container) {
		Halloween halloween = (Halloween) container.getOwner();
		if (halloween == null) {
			container.stop();
			return;
		}
		if (!HolidayController.HALLOWEEN.isActive()) {
			PlayerHandler.executeGlobalMessage("@red@The Halloween event is officially over. Enjoy the rest of your Holidays.");
			halloween.finalizeHoliday();
			container.stop();
			return;
		}
		halloween.getSearchGame().update();
	}

}
