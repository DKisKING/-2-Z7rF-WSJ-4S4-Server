package osv.model.content.barrows;

import osv.model.items.GameItem;
import osv.util.Misc;

public class RewardItem extends GameItem {

	private int minAmount;
	private int maxAmount;
	private RewardLevel rarity;

	public RewardItem(int id, int minAmount, int maxAmount, RewardLevel rarity) {
		super(id);
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.rarity = rarity;
	}

	@Override
	public int getAmount() {
		return Misc.random(maxAmount - minAmount) + minAmount;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(int maxAmount) {
		this.maxAmount = maxAmount;
	}

	public RewardLevel getRarityLevel() {
		return rarity;
	}

}
