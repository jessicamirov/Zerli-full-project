package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import common.interfaces.OrderManager;
import common.request_data.Order;
import common.request_data.OrderList;
import common.request_data.OrderStatus;
import common.request_data.OrderType;
import common.request_data.ProductList;
import common.request_data.ProductListCart;
import common.request_data.Shop;
import common.request_data.User;

/**
 * handles all order information at db
 * 
 * @author Katya and Ahron
 */
public class ServerOrderManager extends BaseSQL implements OrderManager {
	/* SQL SCHEMA: */
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
	private static String DATEOFORDER = "dateOfOrder";
	private static String PAYBYWALLET = "paidByWallet";

	/* TODO: Define fields */

	private static String VARCHAR = " varchar(255)";
	private static String MEDIUMTEXT = " MEDIUMTEXT";
	private static String SMALLINT = " smallint";
	private static String DOUBLE = " double";
	private static String LONGTEXT = " LONGTEXT";

	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;

	public ServerOrderManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;

	}

	/**
	 * reset all orders in db
	 * 
	 * @param Connection connection
	 * @author Katya and Ahron
	 */
	public static void resetOrders(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + TABLE_NAME + " (" + ORDER_NUMBER + VARCHAR + ", " + USERNAME + VARCHAR + ", " + DATE
				+ VARCHAR + ", " + HOUR + VARCHAR + ", " + PRODUCTS + LONGTEXT + ", " + STATUS + VARCHAR + ", " + PRICE
				+ DOUBLE + ", " + RECIPIENT + VARCHAR + ", " + GREETING + MEDIUMTEXT + ", " + SIGNATURE + VARCHAR + ", "
				+ SHOP + VARCHAR + ", " + ADDRESS + VARCHAR + ", " + CITY + VARCHAR + ", " + DELIVERYPHONE + VARCHAR
				+ ", " + PAYMENTPHONE + VARCHAR + ", " + ORDERTYPE + VARCHAR + ", " + DATEOFORDER + VARCHAR + ", "
				+ PAYBYWALLET + DOUBLE + ", PRIMARY KEY (" + ORDER_NUMBER + "));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO: Create table with schema - same as UserManager
	}

	/**
	 * gets an order from db
	 * 
	 * @param String orderNum
	 * @return Order
	 * @author Katya and Ahron
	 */
	@Override
	public Order getOrder(String orderNum) {
		Order order = new Order();
		String query = "SELECT user FROM " + TABLE_NAME + " WHERE " + ORDER_NUMBER + "='" + orderNum + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* name is a key, so there can be 0 or 1 objects only. */

			while (rs.next()) {
				order.username = rs.getString("user");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return order;
	}

	/**
	 * gets all orders from db
	 * 
	 * @param String username
	 * @return OrderList
	 * @author Katya and Ahron
	 */
	@Override
	public OrderList getOrders(String username) {

		OrderList orderList = new OrderList();
		orderList.orders = new ArrayList<Order>();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* name is a key, so there can be 0 or 1 objects only. */

			while (rs.next()) {
				Order order = new Order();
				order.totalPrice = rs.getDouble(PRICE);
				order.status = OrderStatus.fromString(rs.getString(STATUS));
				order.signature = rs.getString(SIGNATURE);
				order.shop = Shop.fromString(rs.getString(SHOP));
				order.recipient = rs.getString(RECIPIENT);
				String productsJson = rs.getString(PRODUCTS);
				order.products = ProductListCart.fromJson(productsJson);
				order.greetingMessage = rs.getString(GREETING);
				order.phone = rs.getString(DELIVERYPHONE);
				order.paymentPhone = rs.getString(PAYMENTPHONE);
				order.orderType = OrderType.fromString(rs.getString(ORDERTYPE));
				order.orderNumber = rs.getString(ORDER_NUMBER);
				order.hour = rs.getString(HOUR);
				order.date = rs.getString(DATE);
				order.city = rs.getString(CITY);
				order.address = rs.getString(ADDRESS);
				order.timeOfOrder = rs.getString(DATEOFORDER);
				order.paidByWallet = rs.getDouble(PAYBYWALLET);
				orderList.orders.add(order);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}
	/**
	 * gets all orders from db to the manager
	 * 
	 * @param String shopName
	 * @return OrderList
	 * @author Katya and Ahron
	 */
	@Override
	public OrderList getOrdersM(String shopName) {
		OrderList orderList = new OrderList();
		orderList.orders = new ArrayList<Order>();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + SHOP + "='" + shopName + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* name is a key, so there can be 0 or 1 objects only. */

			while (rs.next()) {
				Order order = new Order();
				order.totalPrice = rs.getDouble(PRICE);
				order.status = OrderStatus.fromString(rs.getString(STATUS));
				order.signature = rs.getString(SIGNATURE);
				order.shop = Shop.fromString(rs.getString(SHOP));
				order.recipient = rs.getString(RECIPIENT);
				String productsJson = rs.getString(PRODUCTS);
				order.products = ProductListCart.fromJson(productsJson);
				order.greetingMessage = rs.getString(GREETING);
				order.phone = rs.getString(DELIVERYPHONE);
				order.paymentPhone = rs.getString(PAYMENTPHONE);
				order.orderType = OrderType.fromString(rs.getString(ORDERTYPE));
				order.orderNumber = rs.getString(ORDER_NUMBER);
				order.hour = rs.getString(HOUR);
				order.date = rs.getString(DATE);
				order.city = rs.getString(CITY);
				order.address = rs.getString(ADDRESS);
				order.timeOfOrder = rs.getString(DATEOFORDER);
				orderList.orders.add(order);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}
	/**
	 * updates an order 
	 * 
	 * @param Order order
	 * @author Katya and Ahron
	 */
	@Override
	public void updateOrder(Order order) {
		String query = "UPDATE " + TABLE_NAME + " SET " + STATUS + "='" + order.status + "' WHERE " + ORDER_NUMBER
				+ "='" + order.orderNumber + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * delete order from db
	 * 
	 * @param Order order
	 * @author Katya and Ahron
	 */
	@Override
	public void deleteOrder(Order order) {
		String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ORDER_NUMBER + "='" + order.orderNumber + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}