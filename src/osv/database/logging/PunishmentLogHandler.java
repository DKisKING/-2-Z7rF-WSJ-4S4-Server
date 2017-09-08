package osv.database.logging;

import java.sql.SQLException;
import java.sql.Timestamp;

import osv.Server;
import osv.database.BatchQuery;
import osv.model.players.Player;
import osv.util.Misc;

public class PunishmentLogHandler extends Log {

	private static final int BATCH_SIZE = 1;
	private static final String QUERY = "INSERT into punishments (TYPE, DATE, PLAYER, PLAYER_IP, STAFF, REASON) VALUES(?, ?, ?, ?, ?, ?)";

	private BatchQuery batch;
	private int batchCounter;

	public PunishmentLogHandler() {
		super();
		resetBatch();
	}

	public synchronized void logPunishment(Player player, Player staff, String type, String reason) {
		try {
			batch.getStatement().setString(1, type);
			batch.getStatement().setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			batch.getStatement().setString(3, getName(player));
			batch.getStatement().setString(4, Misc.getIP(player));
			batch.getStatement().setString(5, getName(staff));
			batch.getStatement().setString(6, reason);
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

	public synchronized void logPunishment(String player, Player staff, String type, String reason) {
		try {
			batch.getStatement().setString(1, type);
			batch.getStatement().setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			batch.getStatement().setString(3, player);
			batch.getStatement().setString(4, "Unknown");
			batch.getStatement().setString(5, getName(staff));
			batch.getStatement().setString(6, reason);
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

	private String getName(Player player) {
		return player == null ? "Unknown" : player.playerName;
	}

	@Override
	public void execute() {
		System.out.println("Executing punishment log Query");
		batch.execute();
		resetBatch();
	}

}
