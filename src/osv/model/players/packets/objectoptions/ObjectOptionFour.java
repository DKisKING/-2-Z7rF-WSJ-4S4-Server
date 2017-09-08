package osv.model.players.packets.objectoptions;

import osv.Server;
import osv.model.players.Player;

public class ObjectOptionFour {
	
	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		
		switch (objectType) {
		
		}
	}

}
