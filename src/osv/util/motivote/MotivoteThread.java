package osv.util.motivote;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public final class MotivoteThread implements Runnable {

	private final Motivote<?> motivote;

	private boolean reward = false;

	public MotivoteThread(Motivote<?> motivote) {
		this.motivote = motivote;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			synchronized (motivote.finalized) {
				if (!motivote.finalized.isEmpty()) {
					String ids = "";

					for (Integer id : motivote.finalized) {
						ids += id + ",";
					}

					ids = ids.substring(0, ids.length() - 1);
					try (Scanner s = new Scanner(
							new URL(motivote.pageURL() + "?do=finalize&type=" + (reward ? "rewards" : "votes") + "&key=" + motivote.securityKey() + "&ids=" + ids).openStream(),
							"UTF-8")) {
						String out = s.useDelimiter("\\A").next();

						if (out.equalsIgnoreCase("success")) {
							motivote.finalized.clear();
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("MotivoteThread - Check for error");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (Scanner s = new Scanner(new URL(motivote.pageURL() + "?do=pending&key=" + motivote.securityKey()).openStream(), "UTF-8")) {
			String out = s.useDelimiter("\\A").next();

			if (out.startsWith("{")) {
				JSONParser parser = new JSONParser();
				Object dat = parser.parse(out);

				if (dat instanceof JSONObject) {
					JSONObject obj = (JSONObject) dat;

					if (!obj.containsKey("error")) {
						reward = (boolean) obj.get("reward");

						synchronized (motivote.pending) {
							synchronized (motivote.finalized) {
								JSONArray dataArray = null;

								if (reward && obj.containsKey("rewards")) {
									dataArray = (JSONArray) obj.get("rewards");
								} else if (obj.containsKey("votes")) {
									dataArray = (JSONArray) obj.get("votes");
								}

								if (dataArray != null) {
									JSONObject[] datas = (JSONObject[]) dataArray.toArray(new JSONObject[0]);

									for (JSONObject v : datas) {
										int internalID = Integer.parseInt((String) v.get("id"));

										if (!motivote.finalized.contains(internalID) && !motivote.pending.contains(internalID)) {
											motivote.pending.add(internalID);
											String user = (String) v.get("user");
											String ip = (String) v.get("ip");

											if (reward) {
												Reward re = new Reward(motivote, internalID, Integer.parseInt((String) v.get("incentive")), user, ip, (String) v.get("name"),
														Integer.parseInt((String) v.get("amount")));
												((Motivote<Reward>) motivote).handler().onCompletion(re);
											} else {
												Vote vo = new Vote(motivote, internalID, Integer.parseInt((String) v.get("site")), user, ip);
												((Motivote<Vote>) motivote).handler().onCompletion(vo);
											}

										}
									}
								}

								motivote.pending.clear();
							}
						}
					}
				}
			}
		} catch (IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
