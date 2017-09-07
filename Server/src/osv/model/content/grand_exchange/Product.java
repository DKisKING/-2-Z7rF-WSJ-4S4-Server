package osv.model.content.grand_exchange;

import java.lang.reflect.Field;

public class Product {

	/**
	 * A string of information that separates this product from every other.
	 */
	private final String identifier;

	/**
	 * The name of the player that is the owner of this product.
	 */
	private final String owner;

	/**
	 * The item identification value that we are buying or selling.
	 */
	private final int itemId;

	/**
	 * The price of the product.
	 */
	private final int price;

	/**
	 * The type of product whether it's a buy or sell offer.
	 */
	private final ProductType type;

	/**
	 * The state of the product whether it's cancelled, pending, or completed.
	 */
	private ProductState state;

	/**
	 * The slot on the grand exchange interface that this product exists
	 */
	private final byte slot;

	/**
	 * The absolute maximum quantity that is being bought or sold
	 */
	private final int maximumQuantity;

	/**
	 * The amount of the offer that can be collected
	 */
	private int collectibleQuantity;

	/**
	 * The remaining amount of quantity that is still up for offer
	 */
	private int remainingQuantity;

	/**
	 * The amount of coins that are collectible from sold items or a cancelled product
	 */
	private int collectibleCoins;

	/**
	 * The amount of coins that are not used up by the buy order
	 */
	private int remainingCoins;

	/**
	 * A time stating when the product was created
	 */
	private final long timestamp;

	public Product(String identifier, String owner, ProductType type, ProductState state, byte slot, int itemId, int maximumQuantity, int collectibleQuantity,
			int remainingQuantity, int collectibleCoins, int remainingCoins, int price, long timestamp) {
		this.identifier = identifier;
		this.owner = owner;
		this.type = type;
		this.state = state;
		this.slot = slot;
		this.itemId = itemId;
		this.maximumQuantity = maximumQuantity;
		this.collectibleQuantity = collectibleQuantity;
		this.remainingQuantity = remainingQuantity;
		this.collectibleCoins = collectibleCoins;
		this.remainingCoins = remainingCoins;
		this.price = price;
		this.timestamp = timestamp;
	}

	public final int getRemainingQuantity() {
		return remainingQuantity;
	}

	public void subtractRemainingQuantity(int amount) {
		remainingQuantity -= amount;
	}

	public final int getCollectibleQuantity() {
		return collectibleQuantity;
	}

	public final void addCollectibleQuantity(int amount) {
		collectibleQuantity += amount;
	}

	public final int getRemainingCoins() {
		return remainingCoins;
	}

	public final void subtractRemainingCoins(int amount) {
		remainingCoins -= amount;
	}

	public final int getCollectibleCoins() {
		return collectibleCoins;
	}

	public final void addCollectibleCoins(int amount) {
		collectibleCoins += amount;
	}

	public final int getItemId() {
		return itemId;
	}

	public final int getMaximumQuantity() {
		return maximumQuantity;
	}

	public final int getPrice() {
		return price;
	}

	public final ProductState getState() {
		return state;
	}

	public void setState(ProductState state) {
		this.state = state;
	}

	public final String getIdentifier() {
		return identifier;
	}

	public final String getOwner() {
		return owner;
	}

	public final long getTimestamp() {
		return timestamp;
	}

	public final byte getSlot() {
		return slot;
	}

	public ProductType getType() {
		return type;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();

		Field[] fields = getClass().getDeclaredFields();

		try {
			string.append("{\n");
			for (Field field : fields) {
				string.append("\t" + field.getName() + " = " + field.get(this) + "\n");
			}
			string.append("}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string.toString();
	}

}
