package osv.model.content.grand_exchange;

public final class ProductData {

	/**
	 * The last price of the product
	 */
	private final int price;

	/**
	 * The amount of the product that has been sold
	 */
	private final long sold;

	/**
	 * An object with information about a particular grand exchange item
	 * 
	 * @param price the final price of that item
	 * @param sold the amount that were sold in that day
	 */
	public ProductData(int price, long sold) {
		this.price = price;
		this.sold = sold;
	}

	public final int getPrice() {
		return price;
	}

	public final long getSold() {
		return sold;
	}

}
