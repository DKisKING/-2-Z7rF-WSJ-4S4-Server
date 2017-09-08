package osv.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.java.swing.plaf.motif.MotifBorders;

import osv.Config;
import osv.Server;
import osv.model.players.Player;
import osv.model.players.PlayerHandler;
import osv.model.players.Right;

//public class MotivoteExecute implements Runnable {
	
/*	private static final MotifBorders motivote = new MotifBorders();

	static int authAmount = 0;
	private static final ExecutorService VOTE = Executors.newCachedThreadPool();
	Player player;

	public static void run(Player player, String auth) {
		if (System.currentTimeMillis() - player.lastAuthClaim < 600 * 6) {
			player.sendMessage("@cr10@Not so quick, wait a moment and try again.");
			return;
		}
		player.lastAuthClaim = System.currentTimeMillis();
		VOTE.execute(() -> {
			try {
				boolean success = motivote.redeemVote(auth);
				if (success) {
					authAmount++;
					if (!player.getItems().addItem(1464, player.getRights().isOrInherits(Right.MEGA_VIP) ? 2 : Config.DOUBLE_VOTE_INCENTIVES ? 2 : 1)) {
						Server.itemHandler.createGroundItem(player, 1464, player.getX(), player.getY(), player.heightLevel, player.getRights().isOrInherits(Right.MEGA_VIP) ? 2 : Config.DOUBLE_VOTE_INCENTIVES ? 2 : 1);
					}
					player.setLastIncentive(System.currentTimeMillis());
					player.sendMessage("@cr10@Auth redeemed, thanks for voting!");
					
					if (authAmount == 10) {
						PlayerHandler.executeGlobalMessage("<img=10></img><col=255>Another 10 auths have been claimed, get yours too! ::vote");
						authAmount = 0;
					}
				} else {
					player.sendMessage("@cr10@Invalid auth supplied, please try again later.");
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				player.sendMessage("@cr10@Unable to check auth, please try again later.");
			}
		});
	}

	@Override
	public void run() {

	}

}*/
