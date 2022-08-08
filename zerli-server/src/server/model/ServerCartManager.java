package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.interfaces.CartManager;
import common.interfaces.ProductManager;
import common.interfaces.UserManager;
import common.request_data.Order;
import common.request_data.OrderType;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.ProductListCart;
import common.request_data.User;

/**
 * handles all cart item information
 * 
 * @author Katya and Ahron
 */
public class ServerCartManager extends BaseSQL implements CartManager {

	private static int orderNumber = 1;

	private static String TABLE_NAME = "orders";
	private static String ORDER_NUMBER = "number";
	private static String USERNAME = "user";
	private static String DATE = "date";
	private static String HOUR = "hour";
	private static String PRODUCTS = "products";
	private static String STATUS = "status";
	private static String PRICE = "price";
	private static String RECIPIENT = "recipient";
	private static String GREETING = "greeting";
	private static String SIGNATURE = "signature";
	private static String SHOP = "shop";
	private static String ADDRESS = "address";
	private static String CITY = "city";
	private static String DELIVERYPHONE = "deliveryPhone";
	private static String PAYMENTPHONE = "paymentPhone";
	private static String ORDERTYPE = "orderType";

	/* TODO: Define fields */

	private static String VARCHAR = " varchar(255)";
	private static String MEDIUMTEXT = " MEDIUMTEXT";
	private static String SMALLINT = " smallint";
	private static String DOUBLE = " double";
	private static String LONGTEXT = " LONGTEXT";

	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;

	public ServerCartManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;

	}

	/**
	 * sets orders in db
	 * 
	 * @param Order order
	 * @return Order
	 * @author Katya and Ahron
	 */
	@Override
	public Order submitOrder(Order order) {

		try {
			Double priceFromServer = checkTotalPrice(order.products);
			order.totalPrice = priceFromServer;
			if (order.totalPrice > 0 && order.orderType == OrderType.DELIVERY) { // to add delivery price
				order.totalPrice += 20.0;
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Wallet checking
		if (order.paidByWallet <= requestedBy.userWallet) {
			UserManager userManager = new ServerUserManager(requestedBy, connection);
			userManager.updateWallet(requestedBy.userWallet - order.paidByWallet);
		} else {
			UserManager userManager = new ServerUserManager(requestedBy, connection);
			userManager.updateWallet(0);
		}

		order.orderNumber = getNextOrderNumber();

		try {
			String query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + order.orderNumber + "', " + "'"
					+ order.username + "', " + "'" + order.date + "', " + "'" + order.hour + "', " + "'"
					+ order.products.toJson() + "', " + "'" + order.status + "', " + "'" + order.totalPrice + "', "
					+ "'" + order.recipient + "', " + "'" + order.greetingMessage + "', " + "'" + order.signature
					+ "', " + "'" + order.shop.toString() + "', " + "'" + order.address + "', " + "'" + order.city
					+ "', " + "'" + order.phone + "', " + "'" + order.paymentPhone + "', " + "'" + order.orderType
					+ "', " + "'" + order.timeOfOrder + "', " + "'" + order.paidByWallet + "'" + ");";

			runUpdate(connection, query);

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
		return order;

	}

	/**
	 * Checking if the price is right according to database product's prices
	 * 
	 * @param ProductListCart products
	 * @return double
	 * @author Katya and Ahron
	 */

	private double checkTotalPrice(ProductListCart products) throws SQLException {
		Double priceToCheck = 0.0;
		for (String p : products.items.keySet()) {
			ProductManager productManager = new ServerProductManager(requestedBy, connection);
			Product toCheck = new Product();
			toCheck = productManager.getProduct(p);
			int discount = toCheck.discount;
			double priceOfProduct = toCheck.price;
			if (discount > 0) {
				priceOfProduct = priceOfProduct - (priceOfProduct / 100 * discount);
			}

			priceToCheck += priceOfProduct * products.items.get(p);
		}

		return priceToCheck;
	}

	private String getNextOrderNumber() {
		StringBuilder str = new StringBuilder();
		str.append("" + (orderNumber++));
		return str.toString();
	}

}