package osv.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import osv.Server;

public class Query implements Runnable {

	/**
	 * The connection with the database.
	 */
	protected Connection con;

	/**
	 * The Java statement.
	 */
	protected Statement stmt;

	/**
	 * The query which is to be executed after initialization.
	 */
	protected String query;

	/**
	 * @param query The query which is to be executed.
	 */
	public Query(String query) {
		this.query = query;
	}

	/**
	 * Create a new Thread and execute the Query.
	 */
	public void execute() {
		new Thread(this).start();
	}

	/**
	 * Execute the Query without creating a new Thread.
	 */
	public void executeWaitOnResponse() {
		if (query == null) {
			return;
		}
		setupConnection();
		executeQuery();
		terminateConnection();
	}

	/**
	 * Executes the query on a new thread.
	 */
	@Override
	public void run() {
		executeWaitOnResponse();
	}

	/**
	 * Creates a connection with the database.
	 */
	protected final void setupConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = Server.getPunishConnections().getConnection();
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute the specified query and return the result if the query selects a portion of the database.
	 * 
	 * @param query The query which is to be executed.
	 * @return The return set of the query, if any.
	 */
	protected final ResultSet executeQuery() {
		try {
			if (query.toLowerCase().startsWith("select")) {
				return stmt.executeQuery(query);
			}
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			terminateConnection();
		}
		return null;
	}

	/**
	 * Terminates the existing connection with the database if existent.
	 */
	protected final void terminateConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt = null;
		}
	}
}
