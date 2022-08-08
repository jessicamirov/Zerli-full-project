package common.request_data;

/**
 * JSON class that saves information about one the order type and can be
 * transferred between the client and the server for each order type saves if it
 * is a delivery or a take away
 * 
 * @author Katya
 */
public enum OrderType {

	// NONE("None"),
	DELIVERY("Delivery"), TAKE_AWAY("Take away");

	private String s;

	OrderType(final String s) {
		this.s = s;
	}

	public String getValue() {
		return s;
	}

	public String toString() {
		return this.getValue();
	}

	public static OrderType fromString(String text) {
		for (OrderType b : OrderType.values()) {
			if (b.s.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}

}