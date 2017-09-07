package osv.util.motivote;

import java.util.ArrayList;

public final class Motivote<T extends Incentive> {
	public static final float VERSION = 1.3f;

	private final MotivoteHandler<T> handler;
	private MotivoteThread worker;
	private final String securityKey;
	private final String pageURL;

	protected final ArrayList<Integer> finalized = new ArrayList<Integer>();

	protected final ArrayList<Integer> pending = new ArrayList<Integer>();

	public Motivote(MotivoteHandler<T> handler, String webDir, String securityKey) {
		this.handler = handler;
		this.pageURL = webDir + "databack.php";
		this.securityKey = securityKey;
	}

	private void start() {
		if (worker == null) {
			worker = new MotivoteThread(this);
		}
	}

	public void process() {
		start();
		worker.run();
	}

	public String pageURL() {
		return pageURL;
	}

	public String securityKey() {
		return securityKey;
	}

	public MotivoteHandler<T> handler() {
		return handler;
	}

	public void fail(T incentive) {
		synchronized (pending) {
			synchronized (finalized) {
				pending.remove((Integer) incentive.internalID());
			}
		}
	}

	public void complete(T incentive) {
		synchronized (pending) {
			synchronized (finalized) {
				pending.remove((Integer) incentive.internalID());
				finalized.add(incentive.internalID());
			}
		}
	}
}
