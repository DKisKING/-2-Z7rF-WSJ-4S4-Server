package osv.database.logging;

import java.sql.SQLException;
import java.sql.Timestamp;

import osv.Server;
import osv.database.BatchQuery;
import osv.model.players.Player;
import osv.model.players.Right;

public class LoginLogHandler extends Log {

	private static final int BATCH_SIZE = 50;
	private static final String QUERY = "INSERT INTO logins (DATE, TYPE, PLAYER, IP, MAC) VALUES (?, ?, ?, ?, ?)";

	private BatchQuery batch;
	private int batchCounter;

	public LoginLogHandler() {
		super();
		resetBatch();
	}

	public synchronized void logLogin(Player player, String type) {
		try {
			batch.getStatement().setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			batch.getStatement().setString(2, type);
			batch.getStatement().setString(3, player.playerName);
			batch.getStatement().setString(4, getIP(player));
			batch.getStatement().setString(5, getMac(player));
			batch.getStatement().addBatch();
			batchCounter++;
			if (batchCounter >= BATCH_SIZE) {
				batch.execute();
				resetBatch();
			}
		} catch (SQLException e) {
			resetBatch();
			e.printStackTrace();
		}
	}

	private void resetBatch() {
		batch = new BatchQuery(QUERY, Server.getPunishConnections());
		batchCounter = 0;
	}

	private String getIP(Player c) {
		if (c.getRights().isOrInherits(Right.MODERATOR)) {
			return "Private";
		}
		return c.connectedFrom;
	}

	private String getMac(Player c) {
		if (c.getRights().isOrInherits(Right.MODERATOR)) {
			return "Private";
		}
		return c.getMacAddress();
	}

	@Override
	public void execute() {
		System.out.println("Executing login log Query");
		batch.execute();
		resetBatch();
	}

}
