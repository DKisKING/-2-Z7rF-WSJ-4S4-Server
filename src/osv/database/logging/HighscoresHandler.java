package osv.database.logging;

import java.sql.SQLException;

import osv.Server;
import osv.database.BatchQuery;
import osv.model.players.Player;
import osv.model.players.Right;

public class HighscoresHandler extends Log {

	private static final int BATCH_SIZE = 20;
	private static final String QUERY = "REPLACE INTO hs (username, lvl_1, xp_1, lvl_2, xp_2, lvl_3, xp_3, lvl_4, xp_4, lvl_5, xp_5, lvl_6, xp_6, lvl_7, xp_7, lvl_8, xp_8, lvl_9, xp_9, lvl_10, xp_10, lvl_11, xp_11, lvl_12, xp_12, lvl_13, xp_13, lvl_14, xp_14, lvl_15, xp_15, lvl_16, xp_16, lvl_17, xp_17, lvl_18, xp_18, lvl_19, xp_19, lvl_20, xp_20, lvl_21, xp_21, lvl_22, xp_22, lvl_23, xp_23, lvl_24, xp_24, lvl_25, xp_25, rank, total_exp, total_lvl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private BatchQuery batch;
	private int batchCounter;

	public HighscoresHandler() {
		super();
		resetBatch();
	}

	public synchronized void updateHighscores(Player player) {
		if (player == null) {
			return;
		}
		int counter = 0;
		try {
			batch.getStatement().setString(++counter, player.playerName);
			for (int i = 0; i <= 20; i++) {
				int level = player.getLevelForXP(player.playerXP[i]);
				if (level > 99) {
					level = 99;
				}
				batch.getStatement().setInt(++counter, level);
				batch.getStatement().setInt(++counter, player.playerXP[i]);
			}
			batch.getStatement().setInt(++counter, 0);
			batch.getStatement().setInt(++counter, player.getBH().getTotalRogueKills());
			batch.getStatement().setInt(++counter, 0);
			batch.getStatement().setInt(++counter, player.getBH().getTotalHunterKills());
			batch.getStatement().setInt(++counter, player.deathcount);
			batch.getStatement().setInt(++counter, player.killcount);
			batch.getStatement().setInt(++counter, player.playerLevel[21]);
			batch.getStatement().setInt(++counter, player.playerXP[21]);
			batch.getStatement().setInt(++counter, player.getRights().isOrInherits(Right.HELPER) && player.getRights().isOrInherits(Right.IRONMAN) ? 13 : player.getRights().isOrInherits(Right.HELPER) && player.getRights().isOrInherits(Right.OSRS) ? 23 : player.getRights().getPrimary().getValue());
			batch.getStatement().setLong(++counter, player.getPA().getTotalXP());
			batch.getStatement().setInt(++counter, player.getPA().totalLevel());
			batch.getStatement().addBatch();
			batchCounter++;
			if (batchCounter >= BATCH_SIZE) {
				execute();
			}
		} catch (SQLException e) {
			execute();
			e.printStackTrace();
		}
	}

	private void resetBatch() {
		batch = new BatchQuery(QUERY, Server.getPunishConnections());
		batchCounter = 0;
	}

	@Override
	public void execute() {
		System.out.println("Executing highscores query");
		batch.execute();
		resetBatch();
	}
}
