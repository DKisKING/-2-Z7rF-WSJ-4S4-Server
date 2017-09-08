package osv.database.logging;

public class LoggedException {

	private String line;
	private long timestamp;

	public LoggedException(String line, long timestamp) {
		this.line = line;
		this.timestamp = timestamp;
	}

	public String getLine() {
		return line;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
