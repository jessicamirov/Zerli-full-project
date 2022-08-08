package client.model;

import java.sql.SQLException;
import common.Request;
import common.RequestType;
import common.Role;

import common.interfaces.UserManager;
import common.request_data.AnalyseFile;
import common.request_data.Complaint;
import common.request_data.ComplaintList;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;
import common.request_data.Order;
import common.request_data.OrderReport;
import common.request_data.Refund;
import common.request_data.ServerError;
import common.request_data.Shop;
import common.request_data.Survey;
import common.request_data.User;
import common.request_data.UsersList;

/**
 * represent the user details and functions due to a request MAX_ATTEMPS-saves
 * the user Attempts to log in
 * 
 * @author Katya
 */
public class ClientUserManager implements UserManager {
	public EchoClient client;
	public User currentUser;
	public int attemptCount;
	public ClientUserManager clientusermanager;
	private static int MAX_ATTEMPTS = 10;

	public ClientUserManager(EchoClient client) {
		this.client = client;
		attemptCount = 0;
		clientusermanager=this;
	}
	public ClientUserManager(EchoClient client,ClientUserManager clientusermanager) {
		this.client = client;
		attemptCount = 0;
		this.clientusermanager=clientusermanager;
	}

	/**
	 * saves the user and reset the attemps to log in
	 * 
	 * @author Katya
	 */
	public IncomeReport getIncomeReportBC() {
		Request request = client.requestServer(new Request(RequestType.GET_INCOME_REPORT_BC, currentUser, null));
		return null;

	}

	/**
	 * @param User the user class
	 * @return String the user nickname
	 * @author Katya
	 */
	public String getUsername(User user) {
		/* The way we want to display a username in the GUI. */
		if (user == null) {
			user = getCurrentUser();
		}
		if (user == null || user.userrole == Role.GUEST) {
			return Role.GUEST.toString();
		}
		String nickname = user.nickname;
		Role role = user.userrole;
		if (role == Role.CUSTOMER) {
			return nickname;
		}
		return nickname + " (" + role.toString() + ")";
	}

	/**
	 * when user try to log in saves the attempts
	 * 
	 * @author Katya
	 */
	public User getCurrentUser() {
		if (currentUser == null) {
			/*
			 * When first time requested, we should ask server, which GUEST user is our
			 * default.
			 */
			try {
				attemptCount = 0;
				setCurrentUser(null, null);
			} catch (TooManyAttempts | PermissionDenied e) {
				/* Impossible */
			}
		}
		return clientusermanager.currentUser;
	}

	/**
	 * set the current user by asking password and user name if the user passed the
	 * max attempts of wrong log in throw TooManyAttemps EXCEPTION
	 * 
	 * @author Katya
	 */
	public boolean setCurrentUser(String username, String password) throws TooManyAttempts, PermissionDenied {
		if (clientusermanager.attemptCount > MAX_ATTEMPTS) {
			throw new TooManyAttempts();
		}
		User user = clientusermanager.getUser(username,password);
		if (user == null) {
			attemptCount++;
			throw new PermissionDenied();
		}
		if (!user.approved) {
			attemptCount = 0;
			return false;
		}
		if (!clientusermanager.logInUser(user))
			throw new PermissionDenied();
		attemptCount = 0;
		clientusermanager.currentUser = user;
		return true;
	}


	/* Interface: */
	@Override
	public User getUser(String username, String password) {
		Request request = client
				.requestServer(new Request(RequestType.GET_USER, currentUser, new User(username, password)));
		if (request.requestType != RequestType.GET_USER) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}
		return User.fromJson(request.data);
	}

	/**
	 * add a new user to system saves,
	 * ame,password,nickname,shopname,role,cardNumber... if password is weak throws
	 * WeakPassword EXCEPTION if not a manager want to approve a new user throws
	 * PermissionDenied EXCEPTION
	 * 
	 * @author Katya
	 */
	@Override
	public boolean addNewUser(String username, String password, String nickname, Shop shopname, Role role,
			boolean approved, String cardNumber, String expirationDate, String cvv, boolean logInfo, String userWallet)
			throws WeakPassword, PermissionDenied {

		User new_user = new User(username, password);
		new_user.nickname = nickname;
		new_user.shopname = shopname;
		new_user.userrole = role;
		new_user.approved = approved;
		new_user.cardNumber = cardNumber;
		new_user.exDate = expirationDate;
		new_user.cvv = cvv;
		new_user.userWallet = Double.parseDouble(userWallet);

		Request request = client.requestServer(new Request(RequestType.ADD_USER, currentUser, new_user));
		if (request.requestType != RequestType.ADD_USER) {
			ServerError error = ServerError.fromJson(request.data);
			return false;
		}
		/* Not an error means user was added successfully. */
		return true;
	}

	/**
	 * approve a system by nickname if not a manager want to approve a new user
	 * throws PermissionDenied EXCEPTION
	 * 
	 * @author Katya
	 */
	@Override
	public boolean approveUser(String username) throws PermissionDenied {
		Request request = client
				.requestServer(new Request(RequestType.APPROVE_USER, currentUser, new User(username, null)));
		if (request.requestType != RequestType.APPROVE_USER) {
			ServerError error = ServerError.fromJson(request.data);
			if (request.requestType == RequestType.FORBIDDEN) {
				/*
				 * Current user is not allowed to approve users - probably not a manager or user
				 * is already approved.
				 */
				throw new PermissionDenied();
			}

			return false;
		}
		/* Not an error means user was added successfully. */
		return true;
	}

	/* TODO: Relevant for Manager view only. No need to implement now. */
	@Override
	public boolean removeUser(String username) throws PermissionDenied {
		Request request = client
				.requestServer(new Request(RequestType.APPROVE_USER, currentUser, new User(username, null)));
		if (request.requestType != RequestType.APPROVE_USER) {
			ServerError error = ServerError.fromJson(request.data);
			if (request.requestType == RequestType.FORBIDDEN) {
				/*
				 * Current user is not allowed to approve users - probably not a manager or user
				 * is already approved.
				 */
				throw new PermissionDenied();
			}
			System.out.println(error.message);
			return false;
		}
		/* Not an error means user was added successfully. */
		return true;
	}

	@Override
	public UsersList getUsers() {
		Request request = client.requestServer(new Request(RequestType.GET_USERS, currentUser, null));
		UsersList usersList = UsersList.fromJson(request.data);

		/* Not an error means user was added successfully. */
		return usersList;
	}

	@Override
	public boolean addNewOrder(String username, String orderID, Shop shopname, String approved)
			throws WeakPassword, PermissionDenied {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addNewIncomeReport(Shop shop, String year, String month, String Income, String BSI, String TNO)
			throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IncomeReport getIncomeReport(Shop shop, String year, String month) throws SQLException {
		Request request = client.requestServer(new Request(RequestType.GET_INCOME_REPORT, currentUser,
				new IncomeReport(shop, year, month, null, null, null)));
		if (request.requestType != RequestType.GET_INCOME_REPORT) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}
		return IncomeReport.fromJson(request.data);
	}

	@Override
	public IncomeReportList getAllIncomeReports() {
		Request request = client.requestServer(new Request(RequestType.GET_INCOME_REPORT_BC, currentUser, null));
		if (request.requestType != RequestType.GET_INCOME_REPORT_BC) {

			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}

		return IncomeReportList.fromJson(request.data);
	}

	@Override
	public boolean changeStatus(User user) {
		Request request = client.requestServer(new Request(RequestType.CHANGE_STATUS, currentUser, user));
		if (request.requestType != RequestType.CHANGE_STATUS) {

			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}

		return true;
	}

	/**
	 * logging off a user
	 * 
	 * @return true if Succeeded
	 * @author Katya
	 */
	@Override
	public boolean logOffUser(User user) {
		Request request = client.requestServer(new Request(RequestType.LOG_OFF_USER, currentUser, user));

		if (request.requestType != RequestType.LOG_OFF_USER) {
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}

		return true;
	}

	@Override
	public boolean logInUser(User user) {

		Request request = client.requestServer(new Request(RequestType.LOGIN, currentUser, user));

		if (request.requestType != RequestType.LOGIN) {
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}

		return true;
	}

	@Override
	public User getLoggedInUser(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	public UsersList getUsersForManager() {
		Request request = client.requestServer(new Request(RequestType.GET_USERS, currentUser, null));
		UsersList usersList = UsersList.fromJson(request.data);

		/* Not an error means user was added successfully. */
		return usersList;
	}

	/**
	 * saves a new complaint to specific ordernId
	 * 
	 * @param userName, orderId,complaint,date, price,complaintStatus, refund,
	 *                  supportName)
	 * @return true if Succeeded
	 * @author Jessica and Yarden
	 */
	@Override
	public boolean addNewCompliant(String userName, String orderId, String complaint, String date, String price,
			String complaintStatus, String refund, Shop shop, String supportName) {
		Complaint compliantJson = new Complaint(userName, orderId, complaint, date, price, complaintStatus, refund,
				shop, supportName);
		Request request = client.requestServer(new Request(RequestType.SET_COMPLAINT, currentUser, compliantJson));
		/* Not an error means user was added successfully. */
		if (request.requestType != RequestType.SET_COMPLAINT) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return false;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}
		return true;
	}

	/**
	 * return all the complaints the supporter insert to system
	 * 
	 * @param supportName
	 * @return ComplaintList
	 * @author Jessica and Yarden
	 */
	@Override
	public ComplaintList getAllComplaints() {
		ComplaintList complaintList = new ComplaintList();
		// complaintList.supportName = supportName;
		Request request = client.requestServer(new Request(RequestType.GET_ALL_COMPLAINTS, currentUser, complaintList));
		/* Not an error means user was added successfully. */
		if (request.requestType != RequestType.GET_ALL_COMPLAINTS) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}
		return ComplaintList.fromJson(request.data);
	}

	/**
	 * set a requested refund to a specific Order
	 * 
	 * @param ordetID,refund
	 * @return true if Succeeded
	 * @author Jessica and Yarden
	 */
	@Override
	public boolean setRefundAmount(String orderId, String refund) {
		Refund refundJson = new Refund(orderId, refund);
		Request request = client.requestServer(new Request(RequestType.GET_REFUND_AMOUNT, currentUser, refundJson));
		/* Not an error means user was added successfully. */
		if (request.requestType != RequestType.GET_REFUND_AMOUNT) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return false;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}
		return true;
	}

	/**
	 * saves answers to specific survey
	 * 
	 * @param q1,q2,q4,q5,q6,type,shop name.date
	 * @return true if Succeeded
	 * @author Jessica and Yarden
	 */
	@Override
	public boolean setSurveyAnswers(double q1, double q2, double q3, double q4, double q5, double q6, String type,
			String shopName, String Date, String surveyAnalyseId) {
		Survey surveyJson = new Survey(q1, q2, q3, q4, q5, q6, type, shopName, Date, surveyAnalyseId);
		Request request = client.requestServer(new Request(RequestType.GET_ANSWERS_SURVEY, currentUser, surveyJson));
		/* Not an error means user was added successfully. */
		if (request.requestType != RequestType.GET_ANSWERS_SURVEY) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return false;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}
		return true;
	}

	@Override
	public boolean getMonthAvarge() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void importUsersFromDifferentDataBase(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public OrderReport getOrderReport(Shop shop, String year, String Month) {

		Request request = client
				.requestServer(new Request(RequestType.GET_ORDER_REPORT, currentUser, new OrderReport(shop, year, Month,
						null, null, null, null, null, null, null, null, null, null, null, null)));
		if (request.requestType != RequestType.GET_ORDER_REPORT) {

			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}
		return OrderReport.fromJson(request.data);
	}

	@Override
	public boolean updateWallet(double wallet) {
		User u = new User();
		u.userWallet = wallet;
		Request request = client.requestServer(new Request(RequestType.UPDATE_WALLET, currentUser, u));
		if (request.requestType != RequestType.UPDATE_WALLET) {
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}
		/* Not an error means user was added successfully. */
		return true;
	}

	/**
	 * sets the customer wallet
	 * 
	 * @return String
	 * @param String username
	 * @author Yohan and Niv
	 */
	@Override
	public String getUserWallet(String username) {
		User u = new User(username, null);

		Request request = client.requestServer(new Request(RequestType.GET_USER_WALLET, currentUser, u));
		if (request.requestType != RequestType.GET_USER_WALLET) {
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}
		/* Not an error means user was added successfully. */
		return User.fromJson(request.data).userWallet.toString();
	}

	/**
	 * gets the customer wallet and adds to it refund amount
	 * 
	 * @param Order order
	 * @author Yohan and Niv
	 */
	@Override
	public void updateWalletR(Order order) {

		User u = new User(order.username, order.orderNumber);
		u.userWallet = order.totalPrice;
		Request request = client.requestServer(new Request(RequestType.CANEL_REFUND, currentUser, u));
	}

	/**
	 * return an analyze to a specific survey
	 * 
	 * @param surveyType,shopName,date
	 * @return Survey - the analyze of the survey
	 * @author Jessica and Yarden
	 */
	@Override
	public boolean analyseTypeSurvey(AnalyseFile myFile) {
		Request request = client.requestServer(new Request(RequestType.GET_ANALYSE_SURVEY, currentUser, myFile));
		/* Not an error means user was added successfully. */
		if (request.requestType != RequestType.GET_ANALYSE_SURVEY) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return false;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}
		return true;
	}

	@Override
	public void logOffAllUsers() {
		// TODO Auto-generated method stub
		
	}

}
