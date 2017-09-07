package osv.util.motivote;

public class Vote extends Incentive {
	private final int siteID;

	@SuppressWarnings("rawtypes")
	public Vote(Motivote motivote, int voteID, int siteID, String username, String ip) {
		super(motivote, voteID, username, ip);
		this.siteID = siteID;
	}

	public int siteID() {
		return siteID;
	}
}
