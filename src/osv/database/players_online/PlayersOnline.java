package osv.database.players_online;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import osv.Config;
import osv.Server;
import osv.ServerState;
import osv.database.BatchQuery;
import osv.database.Query;
import osv.model.players.PlayerHandler;
import osv.model.players.RightComparator;

public class PlayersOnline implements Runnable {

	private List<PlayerOnlineEntry> cache = new ArrayList<>();

	/**
	 * Executes the query on a new thread.
	 */
	@Override
	public void run() {
		if (Config.SERVER_STATE != ServerState.PUBLIC_PRIMARY) {
			return;
		}
		List<PlayerOnlineEntry> playersOnline = fromPlayerToEntry();

		// No players online, no need to update list.
		if (playersOnline.isEmpty()) {
			return;
		}

		// No update required, cache is up to date with online list.
		if (!isUpdateRequired(playersOnline)) {
			return;
		}

		cache.clear();
		playersOnline.sort(RightComparator.PLAYER_ONLINE_COMPARATOR);
		new Query("TRUNCATE players").executeWaitOnResponse();
		BatchQuery batch = new BatchQuery("INSERT INTO players (name, rights, title) VALUES (?, ?, ?)", Server.getPunishConnections());

		playersOnline.stream().forEachOrdered(player -> {
			try {
				batch.getStatement().setString(1, player.getName());
				batch.getStatement().setInt(2, player.getRight().getValue());
				batch.getStatement().setString(3, player.getTitle());
				batch.getStatement().addBatch();
				cache.add(player);
			} catch (SQLException e) {
			}
		});
		batch.execute();
		System.out.println("Executed online player query");
	}

	private final boolean isUpdateRequired(List<PlayerOnlineEntry> online) {
		for (PlayerOnlineEntry poe : online) {
			if (cache.stream().noneMatch(entry -> poe.getName().equalsIgnoreCase(entry.getName()))) {
				return true;
			}
		}
		return false;
	}

	private final List<PlayerOnlineEntry> fromPlayerToEntry() {
		List<PlayerOnlineEntry> list = new ArrayList<>();

		PlayerHandler.nonNullStream().forEach(player -> list.add(new PlayerOnlineEntry(player.playerName, player.getRights().getPrimary(), player.getTitles().getCurrentTitle())));
		return list;
	}

}
