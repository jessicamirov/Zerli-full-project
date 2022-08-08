package client.model;

/**
 * tabs for the left menu orders
 * 
 * @author Katya and Aharon
 */
public class OrderManager {
	private static String[] steps = { "Cart", "Greeting", "Delivery", "Payment", "Summary" };

	public String[] getSteps() {
		return steps;
	}

	public void clear() {
		/* Removes all order information, e.g. on user change or on order complete. */
	}
}
