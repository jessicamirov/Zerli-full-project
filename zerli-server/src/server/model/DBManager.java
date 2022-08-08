package server.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.interfaces.CartManager;
import common.interfaces.OrderManager;
import common.Role;

import common.interfaces.ProductManager;
import common.interfaces.UserManager;
import common.request_data.ComplaintList;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;
import common.request_data.Order;
import common.request_data.OrderReport;
import common.request_data.Shop;
import common.request_data.User;
import common.request_data.UsersList;

/**
 * Holds all the parameters to the server that connects to sql workbench
 * 
 * @author Katya and Ahron
 */
public class DBManager {
	/*
	 * Handles all SQL requests via sql_queries. Provides high level API for
	 * EchoServer for data manipulation.
	 */

	private static String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
	/* We can probably reuse the format for a more flexible connection: */
	private static String PREFIX = "jdbc:mysql://";
	private static String DEFAULT_SERVER = "localhost";
	private static String DELIMITER = "/";
	private static String DEFAULT_DATABASE = "zerli_database";
	private static String IMPORTED_DATABASE = "import_users_database.users";
	private static String POSTFIX = "?serverTimezone=IST";

	private static String DEFAULT_USERNAME = "root";
	private static String DEFAULT_PASSWORD = "Aa123456";

	private String serverURL;
	private String username;
	private String password;
	private User guestUser;
	private List<String> loggedInUsers = new ArrayList<String>();

	private boolean isConnected;

	public DBManager() {
		this(getDefaultURL(), DEFAULT_USERNAME, DEFAULT_PASSWORD);
	}

	public DBManager(String serverURL, String username, String password) {
		this.serverURL = serverURL;
		this.username = username;
		this.password = password;
		isConnected = false;
		guestUser = new User(Role.GUEST.toString(), null);
		guestUser.nickname = Role.GUEST.toString();
		guestUser.userrole = Role.GUEST;
		guestUser.shopname = Shop.NONE;
		guestUser.approved = true;
	}

	public static String getDefaultURL() {
		return PREFIX + DEFAULT_SERVER + DELIMITER + DEFAULT_DATABASE + POSTFIX;
	}

	public static String getImportedDatabse() {
		return IMPORTED_DATABASE;
	}

	public static String getDefaultUsername() {
		return DEFAULT_USERNAME;
	}

	public static String getDefaultPassword() {
		return DEFAULT_PASSWORD;
	}

	public Connection getConnection()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		/*
		 * Can be private, but kept public for easier debug and testing. Creates
		 * connection object to execute statements via
		 * getConnection().prepareStatement(String statement).execute()
		 */
		Class.forName(DRIVER_CLASS).newInstance();
		return DriverManager.getConnection(serverURL, username, password);
	}

	public void createDatabase() {
		String query = "CREATE DATABASE IF NOT EXISTS " + DEFAULT_DATABASE + ";";
		String oldUrl = serverURL;
		serverURL = PREFIX + DEFAULT_SERVER + POSTFIX;
		try {
			BaseSQL.runUpdate(getConnection(), query);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverURL = oldUrl;
	}

	public UserManager getUserManager(User requestedBy) {
		try {

			return new ServerUserManager(requestedBy, getConnection());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {

			e.printStackTrace();
		}
		return null;
	}

	public User validateUser(User user) {

		/*
		 * Return null if user is incorrect or does not match the password or role.
		 * Returning null will result in "forbidden" response.
		 */

		if (user != null && loggedInUsers.contains(user.username)) {

			return getUserManager(user).getLoggedInUser(user.username, user.password);
		}

		if (user == null || user.username == null || user.userrole == Role.GUEST) {
			/* No user information at all is fine and defined as Guest access. */
			return guestUser;
		}

		return getUserManager(null).getUser(user.username, user.password);
	}

	public boolean isConnected() {
		if (isConnected) {
			/* Checked once already. No need to repeat unless something fails. */
			return true;
		}
		try {
			ServerUserManager.test(getConnection());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}
		isConnected = true;
		return true;
	}

	public ProductManager getProductManager(User requestedBy) {
		try {
			return new ServerProductManager(requestedBy, getConnection());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IncomeReport getIncomeReport(IncomeReport r) throws SQLException {
		return getUserManager(null).getIncomeReport(r.shop, r.year, r.month);
	}

	public IncomeReportList getIncomeReportBC() {

		return getUserManager(null).getAllIncomeReports();
	}

	public OrderManager getOrderManager(User requestedBy) {
		try {
			return new ServerOrderManager(requestedBy, getConnection());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CartManager getCartManager(User requestedBy) {
		try {
			return new ServerCartManager(requestedBy, getConnection());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public UsersList getUsers() {
		return getUserManager(null).getUsers();

	}

	public boolean changeStatus(User user) {
		return getUserManager(null).changeStatus(user);
	}

	public boolean logOffUser(User user) {

		getUserManager(null).logOffUser(user);
		loggedInUsers.remove(user.username);
		System.out.println("User removed from loggedIn users list");

		return true;

	}

	public boolean logInUser(User user) {
		if (loggedInUsers.contains(user.username))
			return false;
		if (getUserManager(null).logInUser(user)) {
			System.out.println("User added to loggedIn users list");
			loggedInUsers.add(user.username);
			return true;
		}

		return false;
	}

	public boolean userLoggedIn(User user) {
		if (user == null || user.userrole == null) {
			return false;
		}

		if (loggedInUsers.contains(user.username)) {
			return true;
		}

		return false;
	}

	public void importUsersFromDifferentDataBase() {
		getUserManager(null).importUsersFromDifferentDataBase(getImportedDatabse());

	}

	public ComplaintList getAllComplaints() {
		// TODO Auto-generated method stub
		return getUserManager(null).getAllComplaints();
	}

	public OrderReport getOrderReport(OrderReport orderReport) {
		// TODO Auto-generated method stub
		return getUserManager(null).getOrderReport(orderReport.shop, orderReport.year, orderReport.month);
	}

	public void updateOrder(Order order) {

		getOrderManager(null).updateOrder(order);

	}

	public void deleteOrder(Order order) {

		getOrderManager(null).deleteOrder(order);
	}

	public boolean addNewCompliant(String userName, String orderId, String complaint, String date, String price,
			String complaintStatus, String refund, Shop shop, String supportName) {

		return getUserManager(null).addNewCompliant(userName, orderId, complaint, date, price, complaintStatus, refund,
				shop, supportName);
	}

	public void logOffAllUsers() {
		
		getUserManager(null).logOffAllUsers();
		
	}

}
