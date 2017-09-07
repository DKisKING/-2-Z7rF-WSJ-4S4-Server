package osv.social_media.hitbox;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.simple.parser.ParseException;

import osv.Config;
import osv.ServerState;
import osv.model.players.PlayerHandler;

public class Hitbox {

	/**
	 * A list of all {@link HitboxUser} objects
	 */
	List<HitboxUser> users = new ArrayList<>(Arrays.asList(
					new HitboxUser("MrMatt", "Matt")));

	/**
	 * Updates each of the {@link HitboxUser} objects by calling the read function of each user.
	 */
	public void update() {
		Iterator<HitboxUser> iterator = users.iterator();
		while (iterator.hasNext()) {
			HitboxUser user = iterator.next();
			try {
				user.read();
				checkLiveStatus(user);
			} catch (UnknownHostException | ConnectException e) {
			} catch (IllegalStateException | ParseException | IOException e) {
				// iterator.remove();
				e.printStackTrace();
				System.out.println("Hitbox - Check for error");
			}
		}
	}

	/**
	 * Checks the status of the live stream and will announce to all players that there is an active live stream if one is live.
	 * 
	 * @param user the {@link HitboxUser}
	 */
	private void checkLiveStatus(HitboxUser user) {
		if (Config.SERVER_STATE == ServerState.PRIVATE) {
			return;
		}
		if (!PlayerHandler.isPlayerOn(user.getPlayerNames().toString())) {
			return;
		}
		if (!user.isLive() && !user.isLiveAnnounced()) {
			return;
		}
		if (user.isLive() && user.isLiveAnnounced()) {
			return;
		}
		if (!user.isLive() && user.isLiveAnnounced()) {
			PlayerHandler.executeGlobalMessage("@cr11@The livestream '" + user.getName() + "' is now offline. Please tune in next time.");
			user.setLiveAnnounced(false);
			return;
		}
		if (user.isLive() && !user.isLiveAnnounced()) {
			PlayerHandler.executeGlobalMessage("@cr11@" + "The livestream '" + user.getName() + "' is online at hitbox.tv/" + user.getName());
			user.setLiveAnnounced(true);
		}
	}

}
