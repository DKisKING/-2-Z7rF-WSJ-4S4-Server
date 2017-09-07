package osv.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPool {

	private static ComboPooledDataSource cpds;

	/**
	 * Create a new connection pool.
	 */
	public ConnectionPool(String host, String user, String pass, String schema) {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("com.mysql.jdbc.Driver");
			cpds.setJdbcUrl("jdbc:mysql://" + host + "/" + schema);
			cpds.setUser(user);
			cpds.setPassword(pass);
			cpds.setMinPoolSize(30);
			cpds.setAcquireIncrement(10);
			cpds.setMaxPoolSize(200);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

}
