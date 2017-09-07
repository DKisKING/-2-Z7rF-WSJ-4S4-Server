package osv.database.logging;

import java.sql.SQLException;
import java.sql.Timestamp;

import osv.Server;
import osv.database.BatchQuery;
import osv.model.players.Player;

public class BarrowsLogHandler extends Log {

	private static final int BATCH_SIZE = 10;
	private static final String QUERY = "INSERT INTO barrows (DATE, PLAYER, REWARDS, BARROWS, KILLCOUNT, BROTHERS_KILLED) VALUES (?, ?, ?, ?, ?, ?)";

	private BatchQuery batch;
	private int batchCounter;

	public BarrowsLogHandler() {
		super();
		resetBatch();
	}

	public synchronized void logReward(Player c, String rewards, int gotBarrows, int killCount, int brothersKilled) {
		try {
			batch.getStatement().setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			batch.getStatement().setString(2, c.playerName);
			batch.getStatement().setString(3, rewards);
			batch.getStatement().setInt(4, gotBarrows);
			batch.getStatement().setInt(5, killCount);
			batch.getStatement().setInt(6, brothersKilled);
			batch.getStatement().addBatch();
			batchCounter++;
			if (batchCounter >= BATCH_SIZE) {
				batch.execute();
				resetBatch();
			}
		} catch (SQLException e) {
			batch.execute();
			resetBatch();
			e.printStackTrace();
		}
	}

	private void resetBatch() {
		batch = new BatchQuery(QUERY, Server.getPunishConnections());
		batchCounter = 0;
	}

	@Override
	public void execute() {
		System.out.println("Executing chat log Query");
		batch.execute();
		resetBatch();
	}

}
