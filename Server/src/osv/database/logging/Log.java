package osv.database.logging;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason MacKeigan
 * @date Jun 2, 2015
 */
public abstract class Log {

	/**
	 * A {@link List} of all the classes that subclass this class
	 */
	private static List<Log> logs = new ArrayList<>();

	/**
	 * A constructor that when overridden must be referenced to ensure that the instance of the subclass be added to the {@link #logs} list.
	 */
	public Log() {
		logs.add(this);
	}

	/**
	 * Used to send a query of the log data on-demand
	 */
	public abstract void execute();

	/**
	 * A convenience function used to reference the {@link Log#execute()} function that has been overridden in all subclasses.
	 */
	public static void executeAll() {
		logs.forEach(log -> log.execute());
	}

}
