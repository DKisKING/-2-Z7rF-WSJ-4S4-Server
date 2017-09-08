package osv.model.content;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import osv.Server;
import osv.model.content.achievement.AchievementType;
import osv.model.content.achievement.Achievements;
import osv.model.items.GameItem;
import osv.model.players.Boundary;
import osv.model.players.Player;
import osv.model.players.Right;
import osv.util.Misc;

public class CrystalChest {

	public static int KEY = 989;
	public static int DRAGONSTONE = 1631;
	public static int KEY_HALVE1 = 985;
	public static int KEY_HALVE2 = 987;
	public static final int ANIMATION = 881;

	private static Map<Rarity, List<GameItem>> items = new HashMap<>();

	static {
		items.put(Rarity.COMMON, Arrays.asList(
				new GameItem(140, 10), 
				new GameItem(374, 50), 
				new GameItem(380, 100), 
				new GameItem(995, 100000), 
				new GameItem(1127, 1),
				new GameItem(1163, 1), 
				new GameItem(1201, 1), 
				new GameItem(1303, 1), 
				new GameItem(1319, 1), 
				new GameItem(1331, 1), 
				new GameItem(1373, 1), 
				new GameItem(1712, 1),
				new GameItem(4131, 1), 
				new GameItem(5935, 1)));
		
		items.put(Rarity.UNCOMMON, Arrays.asList(
				new GameItem(386, 20), 
				new GameItem(990, 3), 
				new GameItem(995, 500000), 
				new GameItem(1305, 1), 
				new GameItem(1377, 1),
				new GameItem(2368, 1), 
				new GameItem(2677, 1), 
				new GameItem(2801, 1), 
				new GameItem(3027, 10), 
				new GameItem(3145, 15), 
				new GameItem(4587, 1), 
				new GameItem(5935, 5),
				new GameItem(6688, 10), 
				new GameItem(11840, 1)));
	}

	public static GameItem randomChestRewards(int chance) {
		int random = Misc.random(chance);
		List<GameItem> itemList = random < chance ? items.get(Rarity.COMMON) : items.get(Rarity.UNCOMMON);
		return Misc.getRandomItem(itemList);
	}

	public static void makeKey(Player c) {
		if (c.getItems().playerHasItem(KEY_HALVE1, 1) && c.getItems().playerHasItem(KEY_HALVE2, 1)) {
			c.getItems().deleteItem(KEY_HALVE1, 1);
			c.getItems().deleteItem(KEY_HALVE2, 1);
			c.getItems().addItem(KEY, 1);
		}
	}

	public static void searchChest(Player c) {
		if (c.getItems().playerHasItem(KEY)) {
			c.getItems().deleteItem(KEY, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItem(DRAGONSTONE, 1);
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.VIP) ? randomChestRewards(2) : randomChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.heightLevel, reward.getAmount());
			}
			Achievements.increase(c, AchievementType.LOOT_CRYSTAL_CHEST, 1);
			c.sendMessage("@blu@You stick your hand in the chest and pull an item out of the chest.");
		} else {
			c.sendMessage("@blu@The chest is locked, it won't budge!");
			return;
		}
	}

	enum Rarity {
		UNCOMMON, COMMON, RARE
	}

}