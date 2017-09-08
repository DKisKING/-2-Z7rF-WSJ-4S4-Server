package osv.database;

import java.sql.ResultSet;

public class ResultQuery extends Query {

	private final ResultQueryOperation operation;
	private ResultSet results;

	public ResultQuery(String query, ResultQueryOperation operation) {
		super(query);
		if (!query.toLowerCase().startsWith("select")) {
			throw new IllegalArgumentException("Query must start with 'select' to return ResultSet object.");
		}
		this.operation = operation;
	}

	@Override
	public void execute() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		setupConnection();
		results = executeQuery();
		if (results == null) {
			terminateConnection();
			throw new NullPointerException("The returned ResultSet object is null.");
		}
		operation.operate(results);
		terminateConnection();
	}

	@Override
	public void executeWaitOnResponse() {
		throw new UnsupportedOperationException();
	}

}
