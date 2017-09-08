package osv.database;

import java.sql.ResultSet;

public interface ResultQueryOperation {

	/**
	 * Performs some operation on a {@link ResultSet} object.
	 * 
	 * @param result the result set being operated on
	 */
	public void operate(ResultSet result);

}
