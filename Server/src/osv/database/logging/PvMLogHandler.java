package osv.database.logging;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import osv.Server;
import osv.database.BatchQuery;
import osv.model.items.GameItem;
import osv.model.items.ItemAssistant;
import osv.model.npcs.NPC;
import osv.model.npcs.NPCDefinitions;
import osv.model.players.Player;
import osv.model.players.Right;

public class PvMLogHandler extends Log {

	private static final int BATCH_SIZE = 5;
	private static final String QUERY = "INSERT INTO pvmlogs (DATE, MONSTER, PLAYER, IP, DROPPED) VALUES (?, ?, ?, ?, ?)";

	private BatchQuery batch;
	private int batchCounter;

	public PvMLogHandler() {
		super();
		resetBatch();
	}

	public synchronized void logKill(NPC monster, Player player, List<GameItem> droppedItems) {
		try {
			String dropString = createDropString(droppedItems);
			batch.getStatement().setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			if (Objects.isNull(monster) || Objects.isNull(NPCDefinitions.get(monster.npcType).getNpcName())) {
				batch.getStatement().setString(2, "Unknown");
			} else {
				batch.getStatement().setString(2, NPCDefinitions.get(monster.npcType).getNpcName());
			}
			if (Objects.isNull(player)) {
				batch.getStatement().setString(3, "Unknown");
				batch.getStatement().setString(4, "Unknown");
			} else {
				batch.getStatement().setString(3, player.playerName);
				batch.getStatement().setString(4, getIP(player));
			}
			batch.getStatement().setString(5, dropString);
			batch.getStatement().addBatch();
			batchCounter++;
			if (batchCounter >= BATCH_SIZE) {
				execute();
			}
		} catch (SQLException e) {
			batch.execute();
			resetBatch();
			e.printStackTrace();
		}
	}

	private void resetBatch() {
		batch = new BatchQuery(QUERY, Server.getPunishConnections());
		batchCounter = 0;
	}

	private String createDropString(List<GameItem> droppedItems) {
		if (Objects.isNull(droppedItems) || droppedItems.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (GameItem item : droppedItems) {
			sb.append(ItemAssistant.getItemName(item.getId()));
			if (item.getAmount() > 1) {
				sb.append(" x" + item.getAmount() + "");
			}
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}

	private String getIP(Player c) {
		if (c.getRights().isOrInherits(Right.MODERATOR)) {
			return "Private";
		}
		return c.connectedFrom;
	}

	@Override
	public void execute() {
		System.out.println("Executing pvm log Query");
		batch.execute();
		resetBatch();
	}

}
