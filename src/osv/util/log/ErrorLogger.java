package osv.util.log;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import osv.Config;
import osv.Server;
import osv.ServerState;

/**
 * 
 * @author nick
 */
public class ErrorLogger extends PrintStream {

	private DateFormat dateFormat = new SimpleDateFormat();

	public ErrorLogger(PrintStream out) {
		super(out);
	}

	@Override
	public void print(String str) {
		if (str == null || str.length() <= 0) {
			return;
		}
		if (Config.SERVER_STATE == ServerState.PUBLIC_PRIMARY) {
			Server.getErrorLogHandler().logError(str);
		}
		if (str.startsWith("debug:"))
			super.print("[" + getPrefix() + "] DEBUG: " + str.substring(6));
		else
			super.print("[" + getPrefix() + "]: " + str);
	}

	private String getPrefix() {
		return dateFormat.format(new Date());
	}
}
