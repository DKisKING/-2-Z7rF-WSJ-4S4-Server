package osv.model.content.barrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class RewardList extends ArrayList<RewardItem> {

	public RewardList() {
		addAll(Reward.asList());
	}

	public void reset() {
		clear();
		addAll(Reward.asList());
	}

	public int getTotalWeight(int killCount) {
		int total = 0;
		for (RewardItem item : this) {
			int rarity = item.getRarityLevel().getRarity();
			if (item.getRarityLevel() == RewardLevel.COMMON) {
				rarity = firstTierRarity(killCount);
			}
			total += rarity;
		}
		return total;
	}

	public int firstTierRarity(int killCount) {
		int size = (int) Reward.VALUES.stream().filter(reward -> reward.rarity == RewardLevel.COMMON).count();
		return RewardLevel.COMMON.getRarity() - (RewardLevel.KC_MULTIPLIER * killCount / size);
	}
	private enum Reward {

		MIND_RUNE(558, 1, 400, RewardLevel.COMMON), 
		CHAOS_RUNE(562, 1, 150, RewardLevel.COMMON), 
		DEATH_RUNE(560, 1, 100, RewardLevel.COMMON), 
		BLOOD_RUNE(565, 1, 80, RewardLevel.COMMON), 
		COINS(995, 1, 5306, RewardLevel.COMMON), 
		BOLT_RACKS(4740, 1, 191, RewardLevel.COMMON), 
		ASTRAL_RUNE(9075, 1, 150, RewardLevel.COMMON),

		CRYSTAL_KEY(989, 1, 1, RewardLevel.UNCOMMON), 
		STRENGTH_POTION(114, 1, 10, RewardLevel.UNCOMMON), 
		ATTACK_POTION(122, 1, 10, RewardLevel.UNCOMMON), 
		DEFENCE_POTION(134, 1, 10, RewardLevel.UNCOMMON), 
		PRAYER_POT(140, 1, 10, RewardLevel.UNCOMMON), 
		RANGE_POTION(170, 1, 10, RewardLevel.UNCOMMON), 
		CLUE_MEDIUM(2801, 1, 1, RewardLevel.UNCOMMON);

		private static List<Reward> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

		private int itemId, minAmount, maxAmount;
		private RewardLevel rarity;

		Reward(int itemId, int minAmount, int maxAmount, RewardLevel rarity) {
			this.itemId = itemId;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
			this.rarity = rarity;
		}

		public static List<RewardItem> asList() {
			List<RewardItem> list = new ArrayList<RewardItem>();
			for (Reward reward : VALUES) {
				list.add(new RewardItem(reward.itemId, reward.minAmount, reward.maxAmount, reward.rarity));
			}
			return list;
		}

	}

}
