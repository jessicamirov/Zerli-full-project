package common;

public enum Role {
	/* User role identified by server with string representation for printing. */
	CUSTOMER("Customer"),
	MANAGER("Manager"),
	OWNER("Owner"),
	WORKER("Worker"),
	SUPPORT("Support"),
	GUEST("Guest"),
	DELIVERY("DeliveryPerson"),
	SERVICE_EXPERT("Expert");

	private String s;

	Role(String s) {
		this.s = s;
	}

	public String toString() {
		return s;
	}
}
