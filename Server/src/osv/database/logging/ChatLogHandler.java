package osv.database.logging;

import java.sql.SQLException;
import java.sql.Timestamp;

import osv.Server;
import osv.database.BatchQuery;
import osv.model.players.Player;
import osv.model.players.Right;

public class ChatLogHandler extends Log {

	private static final int BATCH_SIZE = 100;
	private static final String QUERY = "INSERT INTO chat (DATE, TYPE, PLAYER, IP, MESSAGE, RECIPIENT) VALUES (?, ?, ?, ?, ?, ?)";

	private BatchQuery batch;
	private int batchCounter;

	public ChatLogHandler() {
		super();
		resetBatch();
	}

	public synchronized void logMessage(Player c, String type, String recipient, String message) {
		try {
			batch.getStatement().setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			batch.getStatement().setString(2, type);
			batch.getStatement().setString(3, c.playerName);
			batch.getStatement().setString(4, getIP(c));
			batch.getStatement().setString(5, message);
			batch.getStatement().setString(6, recipient);
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

	private String getIP(Player c) {
		if (c.getRights().isOrInherits(Right.MODERATOR)) {
			return "Private";
		}
		return c.connectedFrom;
	}

	@Override
	public void execute() {
		System.out.println("Executing chat log Query");
		batch.execute();
		resetBatch();
	}

}
