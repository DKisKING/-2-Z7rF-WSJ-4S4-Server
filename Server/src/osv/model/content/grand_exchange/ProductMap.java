package osv.model.content.grand_exchange;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ProductMap {

	private Map<String, Product[]> playerMap;
	private Map<ProductType, Map<ProductState, Map<Integer, Set<Product>>>> typeMap;

	/**
	 * Comparator which sorts a Set of Products by price (desc) -> time (asc) -> index (asc).
	 */
	private final Comparator<Product> buyComparator = new Comparator<Product>() {
		@Override
		public int compare(Product o1, Product o2) {
			if (o1.getPrice() < o2.getPrice()) {
				return 1;
			} else if (o1.getPrice() == o2.getPrice()) {
				int timeComparison = Long.compare(o1.getTimestamp(), o2.getTimestamp());
				if (timeComparison < 0) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return -1;
			}
		}
	};

	/**
	 * Comparator which sorts a Set of Products by price (asc) -> time (asc) -> index (asc).
	 */
	private final Comparator<Product> sellComparator = new Comparator<Product>() {
		@Override
		public int compare(Product o1, Product o2) {
			if (o1.getPrice() < o2.getPrice()) {
				return -1;
			} else if (o1.getPrice() == o2.getPrice()) {
				int timeComparison = Long.compare(o1.getTimestamp(), o2.getTimestamp());
				if (timeComparison > 0) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		}
	};

	/**
	 * Initialize the Object.
	 */
	public ProductMap() {
		initialize();
	}

	/**
	 * Initializes the playerMap and the first 3 levels of the typeMap.
	 */
	private void initialize() {
		playerMap = new HashMap<>();
		typeMap = new HashMap<>();
		for (ProductType type : ProductType.values()) {
			Map<ProductState, Map<Integer, Set<Product>>> stateMap = new HashMap<>();
			for (ProductState state : ProductState.values()) {
				Map<Integer, Set<Product>> map = new HashMap<>();
				stateMap.put(state, map);
			}
			typeMap.put(type, stateMap);
		}
	}

	/**
	 * Adds a product to both mappings.
	 * 
	 * @param product The product which is to be added.
	 */
	public synchronized void add(Product product) {
		Product[] playerProducts = playerMap.getOrDefault(product.getOwner(), new Product[8]);
		playerProducts[product.getSlot()] = product;
		playerMap.put(product.getOwner(), playerProducts);

		Set<Product> productSet = getProductSet(product);
		productSet.add(product);
		typeMap.get(product.getType()).get(product.getState()).put(product.getItemId(), productSet);
	}

	/**
	 * Find the Set which a Product satisfying given conditions belongs in, or initialises one if there isn't any.
	 * 
	 * @param type The type of the Product.
	 * @param state The state of the Product.
	 * @param itemId The item ID of the Product.
	 * @return A Set which the given Product belongs in.
	 */
	private Set<Product> getProductSet(ProductType type, ProductState state, int itemId) {
		if (typeMap.get(type).get(state).containsKey(itemId)) {
			return typeMap.get(type).get(state).get(itemId);
		} else {
			if (state == ProductState.PENDING) {
				switch (type) {
				case BUY:
					return new TreeSet<>(buyComparator);
				case SELL:
					return new TreeSet<>(sellComparator);
				default:
					throw new IllegalStateException();
				}
			} else {
				return new TreeSet<>();
			}
		}
	}

	/**
	 * Find the Set which a given Product belongs in, or initialises one if there isn't any.
	 * 
	 * @param product The product of which we want to find the Set it belongs in.
	 * @return A Set which the given Product belongs in.
	 */
	private Set<Product> getProductSet(Product product) {
		return get(product.getType(), product.getState(), product.getItemId());
	}

	/**
	 * Get all Products associated with a given player name.
	 * 
	 * @param player The name of the player.
	 * @return An array of all products associated with the given player name.
	 * @throws NullPointerException Thrown in case the player has no Products listed.
	 */
	public Product[] get(String player) throws NullPointerException {
		if (playerMap.containsKey(player)) {
			return playerMap.get(player);
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * Find the Product which belongs to a given player name and is in a given Grand Exchange slot.
	 * 
	 * @param player The name of the player.
	 * @param slot The slot of the Product.
	 * @return The product which is associated with the given player name and is in the given slot.
	 * @throws NullPointerException Thrown in case the player has no Products listed.
	 */
	public Product get(String player, int slot) throws NullPointerException {
		if (playerMap.containsKey(player)) {
			return playerMap.get(player)[slot];
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * Find the Set of Products by given ProductType, ProductState and item ID.
	 * 
	 * @param type The type of the Product.
	 * @param state The state of the Product.
	 * @param itemId The item ID of the Product.
	 * @return A Set of Products satisfying given conditions.
	 */
	public Set<Product> get(ProductType type, ProductState state, int itemId) {
		if (typeMap.get(type).get(state).containsKey(itemId)) {
			return typeMap.get(type).get(state).get(itemId);
		} else {
			Set<Product> set = getProductSet(type, state, itemId);
			typeMap.get(type).get(state).put(itemId, set);
			return set;
		}
	}

	/**
	 * Find the Set of Products in which given Product belongs.
	 * 
	 * @param product The Product of which the ProductType, ProductState and item ID must match.
	 * @return A Set of Products satisfying given conditions.
	 */
	public Set<Product> get(Product product) {
		return get(product.getType(), product.getState(), product.getItemId());
	}

	/**
	 * Deletes a given Product from both mappings.
	 * 
	 * @param product The product which should be deleted.
	 */
	public void delete(Product product) {
		if (playerMap.containsKey(product.getOwner())) {
			playerMap.get(product.getOwner())[product.getSlot()] = null;
		}
		Map<Integer, Set<Product>> itemMap = typeMap.get(product.getType()).get(product.getState());
		if (itemMap.containsKey(product.getItemId())) {
			itemMap.get(product.getItemId()).remove(product);
		}
	}

	public void changeState(Product product, ProductState newState) {
		if (typeMap.get(product.getType()).get(product.getState()).get(product.getItemId()).contains(product)) {
			typeMap.get(product.getType()).get(product.getState()).get(product.getItemId()).remove(product);
			product.setState(newState);
			typeMap.get(product.getType()).get(product.getState()).get(product.getItemId()).add(product);
		}
	}
}
