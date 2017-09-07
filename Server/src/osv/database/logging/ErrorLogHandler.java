package osv.database.logging;

import java.sql.Timestamp;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.exception.ExceptionUtils;

import osv.Server;
import osv.database.BatchQuery;

public class ErrorLogHandler extends Log {

	private ConcurrentLinkedQueue<LoggedException> errorQueue = new ConcurrentLinkedQueue<>();

	private static final int MAX_MESSAGE_LENGTH = 255;
	private final String QUERY = "INSERT INTO errors (DATE, MESSAGE) VALUES (?, ?)";

	private BatchQuery batch;

	public ErrorLogHandler() {
		super();
		resetBatch();
	}

	public void logError(String line) {
		errorQueue.add(new LoggedException(line, System.currentTimeMillis()));
	}

	private void processQueue() {
		while (errorQueue.size() > 0) {
			LoggedException exception = errorQueue.poll();
			String line = exception.getLine();
			try {
				batch.getStatement().setTimestamp(1, new Timestamp(exception.getTimestamp()));
				if (line.length() > MAX_MESSAGE_LENGTH) {
					line = line.substring(0, MAX_MESSAGE_LENGTH);
				}
				batch.getStatement().setString(2, line);
				batch.getStatement().addBatch();
			} catch (Exception e) {
				batch.execute();
				resetBatch();
				System.out.println(ExceptionUtils.getStackTrace(e));
			}
		}
		batch.execute();
		resetBatch();
	}

	private void resetBatch() {
		batch = new BatchQuery(QUERY, Server.getPunishConnections());
	}

	@Override
	public synchronized void execute() {
		processQueue();
	}
}
