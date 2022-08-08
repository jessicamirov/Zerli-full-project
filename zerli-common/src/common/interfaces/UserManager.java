package common.interfaces;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import common.Role;
import common.interfaces.UserManager.PermissionDenied;
import common.request_data.AnalyseFile;
import common.request_data.ComplaintList;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;
import common.request_data.Order;
import common.request_data.OrderReport;
import common.request_data.Shop;
import common.request_data.User;
import common.request_data.UsersList;

public interface UserManager {
	/*
	 * This interface should be implemented in both Client and Server sides:
	 * 
	 * 1. ClientUserManager implement UserManager
	 * 
	 * 2. ServerUserManager implements UserManager
	 * 
	 * This function will allow direct access from client to server data without
	 * taking into account the real process.
	 * 
	 * Client will call functions from ClientUserManager. ClientUserManager will
	 * create a Request with required data inside and send it via EchoClient to
	 * EchoServer. EchoServer will unpack the data from Request and call exactly
	 * same function from ServerUserManager. ServerUserManager will get the data
	 * from SQL and return it in correct data format. EchoServer will pack the data
	 * to same Request it came with. EchoServer will return same Request back to
	 * EchoClient. EchoClient will return data to UserClientManager.
	 * UserClientManager will return data to Client.
	 * 
	 * For each function below there is a RequestType that will tell EchoServer
	 * which function to call.
	 */

	/* Possible error are send via ServerError data class: */
	public class TooManyAttempts extends Exception {
		private static final long serialVersionUID = 4180122998486458438L;
	}

	public class WeakPassword extends Exception {
		/*
		 * Password requirements can be set by server. So password strength should be
		 * checked twice. Client will check once to make it faster. But if client have
		 * not checked for some reason - server can also decline weak or empty
		 * passwords.
		 */
		private static final long serialVersionUID = -4115270837154574029L;

		public WeakPassword(String message) {
			/* Explanation on what is wrong with the password. */
			super(message);
		}
	}

	public class PermissionDenied extends Exception {
		private static final long serialVersionUID = -2211480302172449980L;
	}
	

	/* Required functions for RequestType: */
	/*
	 * GET_USER
	 * 
	 * Returns an object User which corresponds to provided username and password.
	 * If password does not match or user does not exist return null.
	 */
	public User getUser(String username, String password);

	/*
	 * ADD_USER
	 * 
	 * Adds a new user. Not approved user can be added by anyone. Approved user can
	 * be added only by Manager, otherwise PermissionDenied will be thrown. Returns
	 * true if user was added.
	 */
	public boolean addNewUser(String username, String password, String nickname,Shop shopname, Role role,
			boolean approved,String cardNumber,String expirationDate,String cvv,boolean logInfo,String userWallet)
			throws WeakPassword, PermissionDenied, SQLIntegrityConstraintViolationException;

	/*
	 * APPROVE_USER
	 * 
	 * Approves already existing user. Can be done only by Manager, otherwise
	 * PermissionDenied thrown. If user does not exist or already approved - returns
	 * false.
	 */
	public boolean addNewOrder(String username,String orderID,Shop shopname,String approved)
			throws WeakPassword, PermissionDenied;
	
	/*
	 * WILL BE REMOVED!!!!!!
	 */
	
	public boolean addNewIncomeReport(Shop shop,String year,String month,String Income,String BSI,String TNO) throws SQLException;
	
	/*
	 * ADD_INCOME_REPORT
	 * Adds a new Income report to DB 
	 */
	
	public IncomeReport getIncomeReport(Shop shop,String year,String month) throws SQLException;
	/*
	 * GET_INCOME_REPORT
	 * Gets an income report from the DB to show to the Manager/Owner 
	 */
	public boolean approveUser(String user) throws PermissionDenied;

	/*
	 * REMOVE_USER
	 * 
	 * Removes existing user. Can be done only by Manager, otherwise
	 * PermissionDenied is thrown. Returns false if no user was removed.
	 */
	public boolean removeUser(String user) throws PermissionDenied;

	/*
	 * GET_USERS
	 * 
	 * Returns a list of "amount" existing users starting from user "start" by type
	 * approved/not approved. Can be done only by Manager.
	 */
	public UsersList getUsers();
	
	/*
	 * GET_INCOME_REPORT_BC
	 * 
	 * will gets all other reports for BarCharts
	 */
	public IncomeReportList getAllIncomeReports();
	
	/*
	 * CHANGE_STATUS
	 * 
	 * Changes the Status of an account , BLOCKED/APPROVED
	 */
	public boolean changeStatus (User user);
	
	/*
	 * LOG_OFF_USER
	 * 
	 * Changes the logInfo to LOGED_OFF
	 */
	public boolean logOffUser(User user);
	
	/*
	 * LOG_IN
	 * 
	 * Changes the logInfo to LOGED_In
	 */
	public boolean logInUser(User user);
	
	
	/*
	 * No requestType, function is only used in serverusermanager
	 */
	public User getLoggedInUser(String username, String password);
	
	/*
	 * GET_ALL_COMPLAINTS
	 * 
	 * Return list of all complaints that specific support user has asked for.
	 */
	public ComplaintList getAllComplaints();

	/*
	 * SET_REFUND_AMOUNT
	 * 
	 * set refund to this user according his orderId.
	 */
	public boolean setRefundAmount(String orderId, String refund);


	/*
	 * GET_MONTH_AVARGE
	 * 
	 * Display monthly survey analysis.
	 */
	public boolean getMonthAvarge();
	
	/*
	 * THE ONE TIME FUNCTION THAT IMPORTS USERS FROM DIFFERENT DATABASE
	 * 
	 * */
	public void importUsersFromDifferentDataBase(String string);
	
	/*
	 * GET_ORDER_REPORT
	 * 
	 * Gets order report for manager and owner
	 */
	public OrderReport getOrderReport(Shop shop,String year,String Month);
	
	public boolean updateWallet(double wallet);
	
	/*
	 * GET_USER_WALLET
	 * 
	 * Gets the user current wallet
	 */
	public String getUserWallet(String username);

	
	/*
	 * CANEL_REFUND
	 * 
	 * Refund the proper amount to the userWallet
	 */
	void updateWalletR(Order order);
	

	/*
	 * ADD_NEW_COMPLIANT
	 * 
	 * Return boolean if the new complaint were added to sql
	 */
	public boolean addNewCompliant(String userName, String orderId, String complaint, String date, String price,
			String complaintStatus, String refund, Shop shop, String supportName);

	/*
	 * SET_SURVEY_ANSWERS
	 * 
	 * fill answers to DB.
	 */
	public boolean setSurveyAnswers(double q1, double q2, double q3, double q4, double q5, double q6, String type,
			String shopName, String Date, String surveyAnalyseId);
	
	/*
	 * ANALYSE_TYPE_SURVEY
	 * 
	 * Display survey analysis.
	 */
	public boolean analyseTypeSurvey(AnalyseFile myFile) throws FileNotFoundException;

	public void logOffAllUsers();
	
	
}


