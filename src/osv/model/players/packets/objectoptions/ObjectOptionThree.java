package osv.model.players.packets.objectoptions;

import osv.Server;
import osv.model.players.Player;

/*
 * @author Matt
 * Handles all 3rd options for objects.
 */

public class ObjectOptionThree {

	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		// c.sendMessage("Object type: " + objectType);
		if (Server.getHolidayController().clickObject(c, 3, objectType, obX, obY)) {
			return;
		}
		
		switch (objectType) {
		case 7811:
			if (!c.inClanWarsSafe()) {
				return;
			}
			c.getDH().sendDialogues(818, 6773);
			break;
		}
	}

}
