package osv.model.content.grand_exchange;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ProductHistory {

	/**
	 * The item that contains information regarding it's history in the grand exchange
	 */
	private final int itemId;

	/**
	 * A mapping of of previous prices where the first key to the underlying map is the month value as indicated in {@link java.util.Calendar#MONTH}. The underlying mapping key is
	 * the day of that month and the data for that day.
	 */
	private final Map<Integer, Map<Integer, ProductData>> history = new HashMap<>();

	/**
	 * Creates a new collection of history for a particular item
	 * 
	 * @param itemId the identification value of the item
	 */
	public ProductHistory(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * The identification value of the item
	 * 
	 * @return the item id
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Adds a piece of {@link ProductData} to the mapping of data for this product.
	 * 
	 * @param month the month that the data is recorded for.
	 * @param day the day that the data is recorded for.
	 * @param data the data for that day.
	 */
	public void addProductData(int month, int day, ProductData data) {
		Map<Integer, ProductData> dataForMonth = history.getOrDefault(month, new HashMap<>());
		dataForMonth.put(day, data);
		history.put(month, dataForMonth);
	}

	/**
	 * Retains a mapping of history about the item for each given month.
	 * 
	 * @return a mapping for every month, or null if no mapping exists.
	 * @throws NullPointerException thrown if there is no mapping for this data yet.
	 */
	public Map<Integer, Map<Integer, ProductData>> getHistory() throws NullPointerException {
		return history;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ItemId: " + itemId).append("\n");
		for (Entry<Integer, Map<Integer, ProductData>> data : history.entrySet()) {
			int month = data.getKey();
			Map<Integer, ProductData> subdata = data.getValue();
			sb.append("{").append("\n");
			sb.append("\tMonth = " + month).append("\n");
			for (Entry<Integer, ProductData> productData : subdata.entrySet()) {
				int day = productData.getKey();
				ProductData pd = productData.getValue();
				sb.append("\tDay: " + day).append("\n");
				sb.append("\tSold: " + pd.getSold()).append("\n");
				sb.append("\tPrice: " + pd.getPrice()).append("\n");
			}
			sb.append("}").append("\n");
		}
		return sb.toString();
	}

}
