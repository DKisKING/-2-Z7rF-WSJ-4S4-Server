package test;

import osv.model.content.grand_exchange.GrandExchange;

public class GrandExchangeTesting {

	public static void main(String... args) {
		// GrandExchange ge = new GrandExchange();
		//
		// ProductHistory history = new ProductHistory(4151);
		//
		// int amount = 5;
		//
		// while (amount-- > 0) {
		// ProductData data = new ProductData(100, 50);
		// history.addProductData(Calendar.AUGUST, 17 + amount, data);
		// }
		//
		// try {
		// ge.write(history);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		GrandExchange ge = new GrandExchange();

		ge.readLibrary();
	}

}
