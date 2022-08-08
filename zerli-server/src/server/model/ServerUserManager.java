package server.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.Role;

import common.interfaces.UserManager;
import common.request_data.AnalyseFile;
import common.request_data.Complaint;
import common.request_data.ComplaintList;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;
import common.request_data.Order;
import common.request_data.OrderReport;
import common.request_data.Shop;
import common.request_data.User;
import common.request_data.UsersList;

/**
 * Handles all the user permission to the db
 * 
 * @author Katya and Ahron
 */
public class ServerUserManager extends BaseSQL implements UserManager {
	/*
	 * SQL queries for Users manipulation. See UserManager interface for explanation
	 * on each function.
	 */

	/* SQL SCHEMA: */
	private static String TABLE_NAME = "users";
	private static String USERNAME = "username"; // key
	private static String PASSWORD = "password";
	private static String NICKNAME = "nickname";
	private static String USERROLE = "userrole"; // varchar
	private static String SHOP_NAME = "ShopName";
	private static String APPROVED = "approved"; // boolean
	private static String CARD_NUMBER = "cardNumber";
	private static String EXPIRATION_DATE = "expirationDate";
	private static String CVV = "CVV";
	private static String LOG_INFO = "logInfo"; // boolean
	private static String INT = " int";

	private static String VARCHAR = " varchar(255)";
	private static String BOOLEAN = " boolean";
	public static String  statusIncomeReport ;

	
	private static IncomeReportList incomeReportList = new IncomeReportList();
	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;
	private boolean isManager; // Same check is made multiple times. Easier to store variable.

	public ServerUserManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;
		this.isManager = this.requestedBy != null && this.requestedBy.userrole == Role.MANAGER;
	}
	public ServerUserManager(User requestedBy) {
		this.requestedBy = requestedBy;
		this.isManager = this.requestedBy != null && this.requestedBy.userrole == Role.MANAGER;
	}

	/* Not interface function: */

	public static boolean test(Connection connection) {
		/*
		 * Tests connection to a table, just to validate that table is available. Can be
		 * any other query.
		 */
		try {
			connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " LIMIT 1;").executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * create a new table of surveys in db
	 * 
	 * @param Connection connection
	 * @author Jessica and Yarden
	 */
	public static void resetSurvey(Connection connection) {
		String query = "DROP TABLE IF EXISTS surveys;";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		} // TO-CHECK
		query = "CREATE TABLE surveys ( surveyId int NOT NULL AUTO_INCREMENT ,q1 VARCHAR(255) ,q2 VARCHAR(255) ,q3 VARCHAR(255) ,q4 VARCHAR(255) ,q5 VARCHAR(255) ,q6 VARCHAR(255) ,type VARCHAR(255) ,shopName VARCHAR(255) ,date VARCHAR(255) ,surveyAnalyseId VARCHAR(255), PRIMARY KEY (surveyId));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create a new table of surveysanalysis in db
	 * 
	 * @param Connection connection
	 * @author Jessica and Yarden
	 */
	public static void resetSurveyAnalysis(Connection connection) {
		String query = "DROP TABLE IF EXISTS surveyanalysis;";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		} // TO-CHECK
		query = "CREATE TABLE surveyanalysis ( surveyAnalyseId" + VARCHAR + ", pdfAnalayse LONGBLOB);";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create a new table of complaints in db
	 * 
	 * @param Connection connection
	 * @author Jessica and Yarden
	 */
	public static void resetComplaints(Connection connection) {
		String query = "DROP TABLE IF EXISTS complaints;";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE complaints (" + USERNAME + VARCHAR + ", orderId" + VARCHAR + ", " + "complaint" + VARCHAR
				+ ", " + "date" + VARCHAR + ", " + "price" + VARCHAR + ", complaintStatus" + VARCHAR + ", supportName"
				+ VARCHAR + ", refund" + VARCHAR + ", " + SHOP_NAME + VARCHAR + ", PRIMARY KEY ( orderId));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create a new table of users in db
	 * 
	 * @param Connection connection
	 * @author Yohan and Niv
	 */
	public static void resetUsers(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + TABLE_NAME + " (" + USERNAME + VARCHAR + ", " + PASSWORD + VARCHAR + ", " + NICKNAME
				+ VARCHAR + ", " + SHOP_NAME + VARCHAR + ", " + USERROLE + VARCHAR + ", " + APPROVED + BOOLEAN + ", "
				+ CARD_NUMBER + VARCHAR + ", " + EXPIRATION_DATE + VARCHAR + ", " + CVV + VARCHAR + ", " + LOG_INFO
				+ BOOLEAN + ", " + "userWallet" + VARCHAR + ", PRIMARY KEY (" + USERNAME + "));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * insert info into table users from atoher db
	 * 
	 * @param String imported_database_name
	 * @author Yohan and Niv
	 */
	@Override
	public void importUsersFromDifferentDataBase(String imported_database_name) {
		String query = "INSERT INTO zerli_database.users  (username , password , nickname , ShopName , userrole , approved , cardNumber , expirationDate , CVV , logInfo , userWallet )  \r\n"
				+ "SELECT username , password , nickname , ShopName , userrole , approved , cardNumber , expirationDate , CVV , logInfo , userWallet  FROM "
				+ imported_database_name + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * select all login users from db
	 * 
	 * @param String username
	 * @param String password
	 * @author Yohan and Niv
	 */
	@Override
	public User getLoggedInUser(String username, String password) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* username is a key, so there can be 0 or 1 objects only. */
			while (rs.next()) {
				User user = new User();
				user.username = rs.getString(USERNAME);
				user.password = rs.getString(PASSWORD);
				user.nickname = rs.getString(NICKNAME);
				user.shopname = Shop.valueOf(rs.getString(SHOP_NAME));
				user.approved = (rs.getInt(APPROVED) != 0 ? true : false);
				user.userrole = Role.valueOf(rs.getString(USERROLE));
				user.cardNumber = rs.getString(CARD_NUMBER);
				user.exDate = rs.getString(EXPIRATION_DATE);
				user.cvv = rs.getString(CVV);
				user.logInfo = (rs.getInt(LOG_INFO) != 0 ? true : false);
				user.setAccountStatus();

				return user;

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * create a new table of order reports in db
	 * 
	 * @param Connection connection
	 * @author Yohan and Niv
	 */
	public static void resetOrdersReports(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + "orders_reports" + ";"; //
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + "orders_reports" + " (" + SHOP_NAME + VARCHAR + ", " + "Year" + VARCHAR + ", "
				+ "Month" + VARCHAR + ", " + "Field_Beauty" + VARCHAR + ", " + "Warm_White" + VARCHAR + ", "
				+ "Pink_Spring" + VARCHAR + ", " + "Cute_Ball" + VARCHAR + ", " + "High_Ground" + VARCHAR + ", "
				+ "With_Love" + VARCHAR + ", " + "Happy_moments" + VARCHAR + ", " + "Memories" + VARCHAR + ", "
				+ "Pink_Orchid" + VARCHAR + ", " + "White_Rose" + VARCHAR + ", " + "Red_Rose" + VARCHAR + ", "
				+ "TotalNumberOfOrders" + VARCHAR + ");";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds new order reports to db
	 * 
	 * @param Shop shop, String year, String month, String Field_Beauty, String
	 *             Warm_White, String Pink_Spring, String Cute_Ball, String
	 *             High_Ground, String With_Love, String Happy_moments, String
	 *             Memories, String Pink_Orchid, String White_Rose, String Red_Rose,
	 *             String TON
	 * @author Yohan and Niv
	 */
	public boolean addNewOrderReport(Shop shop, String year, String month, String Field_Beauty, String Warm_White,
			String Pink_Spring, String Cute_Ball, String High_Ground, String With_Love, String Happy_moments,
			String Memories, String Pink_Orchid, String White_Rose, String Red_Rose, String TON) {
		String query = "INSERT INTO " + "orders_reports" + " VALUES (" + "'" + shop.name() + "', " + "'" + year + "', "
				+ "'" + month + "', " + "'" + Field_Beauty + "', " + "'" + Warm_White + "', " + "'" + Pink_Spring
				+ "', " + "'" + Cute_Ball + "', " + "'" + High_Ground + "', " + "'" + With_Love + "', " + "'"
				+ Happy_moments + "', " + "'" + Memories + "', " + "'" + Pink_Orchid + "', " + "'" + White_Rose + "', "
				+ "'" + Red_Rose + "', " + "'" + TON + "');";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * create a new table of income report in db
	 * 
	 * @param Connection connection
	 * @author Yohan and Niv
	 */
	public static void resetIncomeReports(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + "income_reports" + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + "income_reports" + " (" + SHOP_NAME + VARCHAR + ", " + "Year" + VARCHAR + ", "
				+ "Month" + VARCHAR + ", " + "Income" + VARCHAR + ", " + "BestSellingProduct" + VARCHAR + ", "
				+ "TotalNumberOfOrders" + VARCHAR + ");";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Interface functions: */
	@Override
	public IncomeReportList getAllIncomeReports() {

		return incomeReportList;
	}

	/**
	 * select all of users from db
	 * 
	 * @param String username, String password
	 * @author Yohan and Niv
	 */
	@Override
	public User getUser(String username, String password) {
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* username is a key, so there can be 0 or 1 objects only. */
			while (rs.next()) {
				User user = new User();
				user.username = rs.getString(USERNAME);
				user.password = rs.getString(PASSWORD);
				user.nickname = rs.getString(NICKNAME);
				user.shopname = Shop.valueOf(rs.getString(SHOP_NAME));
				user.approved = (rs.getInt(APPROVED) != 0 ? true : false);
				user.userrole = Role.valueOf(rs.getString(USERROLE));
				user.cardNumber = rs.getString(CARD_NUMBER);
				user.exDate = rs.getString(EXPIRATION_DATE);
				user.cvv = rs.getString(CVV);
				user.logInfo = (rs.getInt(LOG_INFO) != 0 ? true : false);

				user.userWallet = Double.parseDouble(rs.getString("userWallet"));
				System.out.println("248 ServerUserManager " + user.userWallet);
				user.setAccountStatus();
				if (!user.password.equals(password) || user.logInfo) {

					return null;
				}

				return user;

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * inser info into a table of users in db
	 * 
	 * @param String username, String password, String nickname, Shop shopname, Role
	 *               role, boolean approved, String cardNumber, String
	 *               expirationDate, String cvv, boolean logInfo, String userWallet
	 * @author Yohan and Niv
	 */
	@Override
	public boolean addNewUser(String username, String password, String nickname, Shop shopname, Role role,
			boolean approved, String cardNumber, String expirationDate, String cvv, boolean logInfo, String userWallet)
			throws WeakPassword, PermissionDenied {
		String query;

		if (cardNumber != null) {
			query = "UPDATE " + TABLE_NAME + " SET " + NICKNAME + "='" + nickname + "', " + SHOP_NAME + "='"
					+ shopname.name() + "', " + USERROLE + "='" + role.name() + "', " + APPROVED + "='"
					+ (approved ? 1 : 0) + "', " + CARD_NUMBER + "='" + cardNumber + "', " + EXPIRATION_DATE + "='"
					+ expirationDate + "', " + CVV + "='" + cvv + "', " + LOG_INFO + "='" + (logInfo ? 1 : 0) + "', "
					+ "userWallet" + "='" + userWallet + "' WHERE " + USERNAME + "='" + username + "';";
		} else
			query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + username + "', " + "'" + password + "', " + "'"
					+ nickname + "', " + "'" + shopname.name() + "', " + "'" + role.name() + "', " + (approved ? 1 : 0)
					+ ", " + "'" + cardNumber + "', " + "'" + expirationDate + "', " + "'" + cvv + "', " + "'"
					+ (logInfo ? 1 : 0) + "', " + "'" + userWallet + "');";

		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * insert new info about new orders
	 * 
	 * @param String username, String orderID, Shop shopname, String approved
	 * @author Yohan and Niv
	 */
	@Override
	public boolean addNewOrder(String username, String orderID, Shop shopname, String approved)
			throws WeakPassword, PermissionDenied {
		// TODO Auto-generated method stub
		String query = "INSERT INTO " + "orders" + " VALUES (" + "'" + username + "', " + "'" + orderID + "', " + "'"
				+ shopname.name() + "', " + "'" + approved + "');";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * update new username user
	 * 
	 * @param String username
	 * @author Yohan and Niv
	 */
	@Override
	public boolean approveUser(String username) throws PermissionDenied {
		if (!isManager) {
			throw new PermissionDenied();
		}
		String query = "UPDATE " + TABLE_NAME + " SET " + APPROVED + "=1 WHERE " + USERNAME + "='" + username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: probably incorrect. Will not fail if result is empty. */
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * remove the user from db
	 * 
	 * @param String username
	 * @author Yohan and Niv
	 */
	@Override
	public boolean removeUser(String username) throws PermissionDenied {
		if (!isManager) {
			throw new PermissionDenied();
		}
		String query = "DELETE FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: Check that when user does not exists - there is an error. */
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * remove the user from db
	 * 
	 * @return UsersList
	 * @author Yohan and Niv
	 */
	@Override
	public UsersList getUsers() {

		String query = "SELECT * FROM " + TABLE_NAME + ";";
		UsersList usersList = new UsersList();
		usersList.Users = new ArrayList<User>();

		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) {
				User user = new User();
				user.username = rs.getString(USERNAME);
				user.password = rs.getString(PASSWORD);
				user.nickname = rs.getString(NICKNAME);
				user.shopname = Shop.valueOf(rs.getString(SHOP_NAME));
				user.approved = (rs.getInt(APPROVED) != 0 ? true : false);
				user.userrole = Role.valueOf(rs.getString(USERROLE));
				user.cardNumber = rs.getString(CARD_NUMBER);
				user.exDate = rs.getString(EXPIRATION_DATE);
				user.cvv = rs.getString(CVV);
				user.logInfo = (rs.getInt(LOG_INFO) != 0 ? true : false);
				user.userWallet = Double.parseDouble(rs.getString("userWallet"));
				user.setAccountStatus();
				usersList.Users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usersList;
	}

	/**
	 * add new info about income report
	 * 
	 * @param Shop shop, String year, String month, String Income, String BSI,
	 *             String TNO
	 * @author Yohan and Niv
	 */
	@Override
	public boolean addNewIncomeReport(Shop shop, String year, String month, String Income, String BSI, String TNO) {
		 
		    //check shop
			if(shop!=Shop.HAIFA || shop!=Shop.NAHARIYA ||shop!=Shop.ALL) {
				statusIncomeReport="Error in the input - 'Shop'";
				return false;
			}
			//check year
			@SuppressWarnings("deprecation")
			int CurrentYear = (new Date().getYear()+1900);
			if (Integer.parseInt(year)>CurrentYear ) {
				statusIncomeReport="The input 'Year' is bigger than current year!";
				return false;
			}
			//check month
			Month[] m=Month.values();
			boolean flag=false;
			for(Month n : m) {
				if (n.name().equals(year.toUpperCase())) {
					flag=true;
					break;}
			}
			if(!flag) {
				statusIncomeReport="The input 'Month' is wrong!";
				return false;}
			//check income
			if(Integer.parseInt(Income)<0) {
				statusIncomeReport = "The input 'Income' is negative!";
				return false;	
			}
			if(Integer.parseInt(TNO)<0) {
				statusIncomeReport = "The input 'TNO' is negetive";
			}
			
			

		String query = "INSERT INTO " + "income_reports" + " VALUES (" + "'" + shop.name() + "', " + "'" + year + "', "
				+ "'" + month + "', " + "'" + Income + "', " + "'" + BSI + "', " + TNO + ");";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * gets income report from db
	 * 
	 * @param Shop shop, String year, String month
	 * @return IncomeReport
	 * @author Yohan and Niv
	 */
	@Override
	public IncomeReport getIncomeReport(Shop shop, String year, String month) throws SQLException {
		IncomeReport incomeReport = new IncomeReport();
		incomeReportList.Reports = new ArrayList<IncomeReport>();

		// Get the report the user asked for
		String query = "SELECT * FROM income_reports WHERE ShopName = '" + shop.name() + "' AND Year = '" + year
				+ "' AND Month = '" + month + "';";
		try {
			ResultSet rs = runQuery(connection, query);

			while (rs.next()) {

				incomeReport.shop = shop;
				incomeReport.year = year;
				incomeReport.month = month;
				incomeReport.income = rs.getString("Income");
				incomeReport.bestSellingProduct = rs.getString("BestSellingProduct");
				incomeReport.totalNumberOfOrders = rs.getString("TotalNumberOfOrders");
				incomeReportList.Reports.add(incomeReport);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Get the income of other shops for the same year and months
		Shop[] allShops = Shop.values();
		for (Shop s : allShops) {
			if (!(s.toString().equals(shop.toString())) && !(s.toString().equals(shop.ALL.toString()))
					&& !(s.toString().equals(shop.NONE.toString()))) {
				query = "SELECT * FROM income_reports WHERE ShopName = '" + s.name() + "' AND Year = '" + year
						+ "' AND Month = '" + month + "';";

				try {
					ResultSet rs = runQuery(connection, query);

					while (rs.next()) {
						IncomeReport incomeReportM = new IncomeReport();
						incomeReportM.shop = s;
						incomeReportM.year = year;
						incomeReportM.month = month;
						incomeReportM.income = rs.getString("Income");
						incomeReportM.bestSellingProduct = rs.getString("BestSellingProduct");
						incomeReportM.totalNumberOfOrders = rs.getString("TotalNumberOfOrders");

						incomeReportList.Reports.add(incomeReportM);

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return incomeReport;
	}

	/**
	 * change the customer status from approved/blocked/freeze
	 * 
	 * @param User user
	 * @author Yohan and Niv
	 */
	@Override
	public boolean changeStatus(User user) {
		String query = null;
		String switchCase = user.accountStatus;
		if (user.userrole.equals(Role.CUSTOMER)) {
			switch (switchCase) {
			case "Frozen":// FREEZE OPTION
				query = "UPDATE users SET approved = 1, userrole = 'GUEST' WHERE username = '" + user.username + "';";
				break;
			case "Blocked":
				query = "UPDATE users SET approved = 0 WHERE username = '" + user.username + "';";
				break;
			case "Approved":
				query = "UPDATE users SET approved = 1, userrole = 'CUSTOMER' WHERE username = '" + user.username
						+ "';";
				break;
			}
			try {
				runUpdate(connection, query);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		else {
			if (user.cardNumber.equals("1"))
				query = "UPDATE users SET cardNumber = '0' WHERE username = '" + user.username + "';";
			else
				query = "UPDATE users SET cardNumber = '1' WHERE username = '" + user.username + "';";
			try {
				runUpdate(connection, query);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

	}

	/**
	 * log in user
	 * 
	 * @param User user
	 * @author Yohan and Niv
	 */
	@Override
	public boolean logInUser(User user) {
		String query = "UPDATE " + TABLE_NAME + " SET " + LOG_INFO + "=1 WHERE " + USERNAME + "='" + user.username
				+ "';";
		try {
			runUpdate(connection, query);

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * log off user
	 * 
	 * @param User user
	 * @author Yohan and Niv
	 */
	@Override
	public boolean logOffUser(User user) {
		String query = "UPDATE " + TABLE_NAME + " SET " + LOG_INFO + "=0 WHERE " + USERNAME + "='" + user.username
				+ "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * add new complaint to db, insert the info into complaints table
	 * 
	 * @param String userName, String orderId, String complaint, String date, String
	 *               price, String complaintStatus, String refund, Shop shop, String
	 *               supportName
	 * @return boolean
	 * @author Jessica and Yarden
	 */
	@Override
	public boolean addNewCompliant(String userName, String orderId, String complaint, String date, String price,
			String complaintStatus, String refund, Shop shop, String supportName) {
		String query = "INSERT INTO complaints VALUES (" + "'" + userName + "', " + "'" + orderId + "', " + "'"
				+ complaint + "', " + "'" + date + "', " + "'" + price + "', '" + complaintStatus + "', '" + supportName
				+ "', " + "'" + refund + "', '" + shop.name() + "');";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * gets all complaints according to username
	 * 
	 * @return ComplaintList
	 * @author Jessica and Yarden
	 */
	@Override
	public ComplaintList getAllComplaints() {
		ComplaintList complaintList = new ComplaintList();
		complaintList.complaints = new ArrayList<Complaint>();
		String query = "SELECT * FROM complaints;";
		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) { // for lines
				Complaint complaint = new Complaint();
				complaint.userName = rs.getString("username");
				complaint.orderId = rs.getString("orderId");
				complaint.complaint = rs.getString("complaint");
				complaint.date = rs.getString("date");
				complaint.price = rs.getString("price");
				complaint.complaintStatus = rs.getString("complaintStatus");
				complaint.refund = rs.getString("refund");
				complaint.shop = Shop.valueOf(rs.getString(SHOP_NAME));
				complaint.supportName = rs.getString("supportName");
				complaintList.complaints.add(complaint);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return complaintList;
	}

	/**
	 * insert refund coulmn the refund of customer gets
	 * 
	 * @param String orderId, String refund
	 * @return boolean
	 * @author Jessica and Yarden
	 */
	@Override
	public boolean setRefundAmount(String orderId, String refund) {
		try {
			Double x = Double.parseDouble(orderId);
			String query = "UPDATE complaints SET refund ='" + refund
					+ "' , complaintStatus = 'Approved' WHERE orderId ='" + orderId + "';";
			try {
				runUpdate(connection, query);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} catch (NumberFormatException e2) {
			String query = "SELECT userWallet FROM users WHERE username = '" + orderId + "';";
			String tmp = null;
			ResultSet rs;
			try {
				rs = runQuery(connection, query);
				while (rs.next())
					tmp = rs.getString("userWallet");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			Double val1 = Double.parseDouble(tmp);
			Double val2 = Double.parseDouble(refund);
			Double refundTmp = val1 + val2;
			refund = refundTmp + "";
			String query1 = "UPDATE users SET userWallet ='" + refund + "' WHERE username ='" + orderId + "';";
			try {
				runUpdate(connection, query1);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	/**
	 * insert survey answers into surveys table
	 * 
	 * @param double q1, double q2, double q3, double q4, double q5, double q6,
	 *               String type, String shopName, String date, String
	 *               surveyAnalyseId
	 * @return boolean
	 * @author Jessica and Yarden
	 */
	@Override
	public boolean setSurveyAnswers(double q1, double q2, double q3, double q4, double q5, double q6, String type,
			String shopName, String date, String surveyAnalyseId) {
		int zero = 0;
		String query = "INSERT INTO surveys VALUES (" + "'" + zero + "', " + "'" + q1 + "', " + "'" + q2 + "', " + "'"
				+ q3 + "', " + "'" + q4 + "', " + "'" + q5 + "', '" + q6 + "', '" + type + "', " + "'" + shopName
				+ "', " + "'" + date + "', '" + surveyAnalyseId + "');";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean getMonthAvarge() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * get all the orders report from the DB
	 * 
	 * @param Shop shop, String year, String Month
	 * @return OrderReport
	 * @author Yohan and Niv
	 */
	@Override
	public OrderReport getOrderReport(Shop shop, String year, String Month) {
		OrderReport orderReport = new OrderReport();

		// Get the report the user asked for

		String query = "SELECT * FROM orders_reports WHERE ShopName = '" + shop.name() + "' AND Year = '" + year
				+ "' AND Month = '" + Month + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) {

				orderReport.shop = shop;
				orderReport.year = year;
				orderReport.month = Month;
				orderReport.Cute_Ball = rs.getString("Cute_Ball");
				orderReport.Field_Beauty = rs.getString("Field_Beauty");
				orderReport.Warm_White = rs.getString("Warm_White");
				orderReport.Happy_moments = rs.getString("Happy_moments");
				orderReport.High_Ground = rs.getString("High_Ground");
				orderReport.With_Love = rs.getString("With_Love");
				orderReport.Memories = rs.getString("Memories");
				orderReport.Pink_Orchid = rs.getString("Pink_Orchid");
				orderReport.Pink_Spring = rs.getString("Pink_Spring");
				orderReport.White_Rose = rs.getString("White_Rose");
				orderReport.Red_Rose = rs.getString("Red_Rose");
				orderReport.TON = rs.getString("TotalNumberOfOrders");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderReport;
	}
	/**
	 * sets the userWallet of a user
	 * 
	 * @param double wallet
	 * @return boolean
	 * @author Yohan and Niv
	 */
	@Override
	public boolean updateWallet(double wallet) {
		String s = "" + wallet;
		String query = "UPDATE " + TABLE_NAME + " SET " + "userWallet" + "='" + s + "' WHERE " + USERNAME + "='"
				+ requestedBy.username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: probably incorrect. Will not fail if result is empty. */
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * get a userWallet
	 * 
	 * @param String username
	 * @return String
	 * @author Yohan and Niv
	 */
	@Override
	public String getUserWallet(String username) {

		String query = "SELECT userWallet FROM users WHERE username = '" + username + "';";
		String wallet = null;
		try {
			ResultSet rs = runQuery(connection, query);

			while (rs.next()) {
				wallet = rs.getString("userWallet");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wallet;

	}
	/**
	 * add to the current userWallet
	 * 
	 * @param Order order
	 * @author Yohan and Niv
	 */
	@Override
	public void updateWalletR(Order order) {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		ServerOrderManager som = new ServerOrderManager(manager, connection);

		Order o = som.getOrder(order.orderNumber);
		Double oldWallet = Double.parseDouble(getUserWallet(o.username));
		order.totalPrice = order.totalPrice + oldWallet;
		String query = "UPDATE " + TABLE_NAME + " SET " + "userWallet" + "='" + order.totalPrice + "' WHERE " + USERNAME
				+ "='" + o.username + "';";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: probably incorrect. Will not fail if result is empty. */
			e.printStackTrace();

		}

	}
	/**
	 * saves the files os surveyAnalye to the DB
	 * 
	 * @param AnalyseFile myFile
	 * @return boolean
	 * @author Yohan and Niv
	 */
	@Override
	public boolean analyseTypeSurvey(AnalyseFile myFile) throws FileNotFoundException {
		int fileSize = myFile.size;
		ArrayList<String> analyzeId = new ArrayList<>();
		File newFile = null;
		FileOutputStream fileOut;
		try {
			newFile = new File(myFile.fileName);
			fileOut = new FileOutputStream(newFile);
			BufferedOutputStream bufferOut = new BufferedOutputStream(fileOut);
			try {
				bufferOut.write(myFile.byteArray, 0, myFile.size);
				fileOut.flush();
				bufferOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			PreparedStatement ps = connection
					.prepareStatement("UPDATE zerli_database.surveyanalysis SET pdfAnalayse = ? WHERE surveyAnalyseId= "
							+ myFile.surveyAnalyseId + " ");
			ps.setBinaryStream(1, new FileInputStream(newFile));
			ps.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("PDF FAILED");
			return false;
		}
		return true;
	}

	@Override
	public void logOffAllUsers() {
		String query = "UPDATE users SET logInfo = '0';";
		System.out.println("LOGGED OFF ALL USERS!");
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			/* TODO: probably incorrect. Will not fail if result is empty. */
			e.printStackTrace();

		}
	}

}
