package common.request_data;

import common.JSONObject;

/**
 * 
 * Default constructor is required for JSONObject if any other constructor
 * defined.
 * 
 * @author Yohan, Niv
 */
public class Order extends JSONObject {
	public String orderNumber;
	public String username;
	public String date;
	public String hour;
	public ProductListCart products;
	public OrderStatus status;
	public double totalPrice = 0.0;
	public String recipient;
	public String greetingMessage;
	public String signature;
	public Shop shop = null;
	public String address;
	public String city;
	public String phone; // Recipient of order
	public String paymentPhone; // Payment phone
	public OrderType orderType;
	public String timeOfOrder;
	public boolean gotDelivery;
	public double paidByWallet;

	public Order() {
		this.orderNumber = null;
		this.username = null;
		this.date = null;
		this.hour = null;
		this.products = null;
		this.status = null;
		this.totalPrice = 0.0;
		this.recipient = null;
		this.greetingMessage = null;
		this.signature = null;
		this.shop = null;
		this.address = null;
		this.city = null;
		this.phone = null;
		this.paymentPhone = null;
		this.orderType = null;
		this.gotDelivery = false;
		this.paidByWallet = 0.0;

	}

	public Order(String orderNumber, String username, String date, String hour, ProductListCart products,
			OrderStatus status, double totalPrice, String recipient, String greetingMessage, String signature,
			Shop shop, String address, String city, String phone, String paymentName, String paymentPhone,
			OrderType orderType, boolean gotDelivery, double paidByWallet) {
		super();
		this.orderNumber = orderNumber;
		this.username = username;
		this.date = date;
		this.hour = hour;
		this.products = products;
		this.status = status;
		this.totalPrice = totalPrice;
		this.recipient = recipient;
		this.greetingMessage = greetingMessage;
		this.signature = signature;
		this.shop = shop;
		this.address = address;
		this.city = city;
		this.phone = phone;
		this.paymentPhone = paymentPhone;
		this.orderType = orderType;
		this.gotDelivery = gotDelivery;
		this.paidByWallet = paidByWallet;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public static Order fromJson(String s) {
		/* Add such function to each subclass! */
		return (Order) fromJson(s, Order.class);
	}

}