package server.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

import common.Request;
import common.RequestType;
import common.Role;
import common.interfaces.CartManager;
import common.interfaces.ProductManager;
import common.interfaces.UserManager;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.AnalyseFile;
import common.request_data.CategoriesList;
import common.request_data.Complaint;
import common.request_data.ComplaintList;
import common.request_data.Order;
import common.request_data.OrderList;
import common.request_data.OrderReport;
import common.request_data.Product;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;
import common.request_data.ProductList;
import common.request_data.Refund;
import common.request_data.ServerError;
import common.request_data.Shop;
import common.request_data.Survey;
import common.request_data.User;
import common.request_data.UsersList;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * main connection between echo client
 * 
 * @author Yohan and Niv and Katya and Jessica and Yarden and Ahron
 */
public class EchoServer extends AbstractServer {
	/*
	 * Server listener for Client requests. Translates "echo" strings into objects
	 * and handles all the main logic. Uses DBManager to access SQL database. Does
	 * not execute any SQL queries directly, but asks DBManager to do so.
	 */
	private static int DEFAULT_PORT = 5555;
	private DBManager manager;

	public EchoServer(int port) {
		super(port);
	}

	public void setDBManager(DBManager manager) {
		this.manager = manager;
	}

	public static int getDefaultPort() {
		return DEFAULT_PORT;
	}

	/**
	 * gets the request for echo client and send it to the right handled with switch
	 * case
	 * 
	 * @param Object             message
	 * @param ConnectionToClient client
	 * @author ALL
	 */
	@Override
	protected void handleMessageFromClient(Object message, ConnectionToClient client) {
		Request request = Request.fromJson((String) message);
		if (manager == null) {
			/* Should not happen. */
			System.out.println("Warning! DBManager is null, but server runs.");
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Database is offline").toJson();
			respond(client, request);
			return;
		}

		if (!manager.userLoggedIn(request.user))// If user is loggedIn he doesnt need to be validated
			request.user = manager.validateUser(request.user);

		if (request.requestType.equals(RequestType.LOG_OFF_USER)) {

			request = handleLogOff(request);

			respond(client, request);
			return;
		}
		if (request.requestType.equals(RequestType.LOGIN)) {
			request = handleLogIn(request);
			respond(client, request);
			return;
		}
		if (request.user == null) {
			/*
			 * If user is null after the validation, meaning some user data is invalid.
			 * Usually should not happen, but can be a result of incorrect client.
			 */
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("User or password is invalid").toJson();
			respond(client, request);
			return;
		}
		if (!request.user.approved) {
			/*
			 * If user is null after the validation, meaning some user data is invalid.
			 * Usually should not happen, but can be a result of incorrect client.
			 */
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("User is not yet approved.").toJson();
			respond(client, request);
			return;
		}

		System.out.println("Request: " + request.requestType.name() + ", Role: " + request.user.userrole.name()
				+ ", User: " + request.user.nickname);
		switch (request.requestType) {
		case PING:
			/*
			 * Ping does not change the request at all. Keeps all the data as is. Required
			 * to prove that server behaves correctly. Requires being a GUEST or correct
			 * user. Incorrect user will fail before this switch.
			 */
			break;

		/* UserManager */
		case GET_USER:
			request = handleGetUser(request);
			break;

		case GET_USERS:
			request = handleGetUsers(request);
			break;

		case ADD_USER:
			try {
				request = handleAddUser(request);
			} catch (SQLIntegrityConstraintViolationException e1) {
				e1.printStackTrace();

			}
			break;

		case GET_ORDERS:
			request = handleGetOrders(request);
			break;

		case GET_USER_WALLET:
			try {
				request = handleGetUserWallet(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			break;

		case APPROVE_USER:
			request = handleApproveUser(request);
			break;
		/* TODO: Missing REMOVE_USER, GET_USERS */
		/* ProductManager */
		case GET_CATEGORIES:
			request = handleGetCategories(request);
			break;
		case GET_PRODUCTS:
			request = handleGetProducts(request);
			break;
		case GET_INCOME_REPORT:
			try {
				request = handleGetIncomeReports(request);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case GET_INCOME_REPORT_BC:
			try {
				request = handleGetIncomeReportsBC(request);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case UPDATE_WALLET:
			try {
				request = handleUpdateWallet(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;

		case ADD_ORDER:
			try {
				request = handleAddOrder(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case CANEL_REFUND:
			try {
				request = handleCancelRefund(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;

		case UPDATE_ORDER:
			request = handleUpdateOrder(request);
			break;

		case DELETE_ORDER:
			request = handleDeleteOrder(request);
			break;

		case GET_ALL_COMPLAINTS:
			try {
				request = handleGetComplaints(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;

		case GET_REFUND_AMOUNT:
			try {
				request = handleGetRefund(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;

		case GET_USER_OREDERS:
			try {
				request = handleGetUserOrder(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;

		case GET_ANSWERS_SURVEY:
			try {
				request = handleSurveyAnswers(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;

		case SET_DISCOUNT:
			try {
				request = handleSetDiscount(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | PermissionDenied
					| SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case CHANGE_STATUS:
			request = handleChangeStatus(request);
			break;

		case TOFROM_CATALOGUE:
			try {
				request = handleToFromCatalogue(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case GET_ORDER_REPORT:
			request = handleGetOrderReport(request);
			break;

		case GET_PRODUCT:
			request = handleGetProduct(request);
			break;

		case SET_COMPLAINT:

			try {
				request = handleNewComplaint(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case GET_ANALYSE_SURVEY:

			try {
				request = handleSurveyAnalyse(request);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | FileNotFoundException
					| SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		default:
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Unsupporter reuqest.").toJson();
			break;
		}

		respond(client, request);
	}

	/**
	 * change the status of and order to cancled
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleCancelRefund(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		User u = User.fromJson(request.data);
		Order o = new Order();
		o.totalPrice = u.userWallet;
		o.username = u.username;
		o.orderNumber = u.password; // on this func password was set to ordernumber from clientUserManager
		ServerUserManager s = new ServerUserManager(request.user, manager.getConnection());
		s.updateWalletR(o);
		request.data = u.toJson();
		return request;
	}

	/**
	 * get the user wallet
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleGetUserWallet(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		User u = User.fromJson(request.data);
		String username = u.username;
		ServerUserManager s = new ServerUserManager(request.user, manager.getConnection());
		String w = s.getUserWallet(username);
		u.userWallet = Double.parseDouble(w);
		request.data = u.toJson();
		return request;
	}

	/**
	 * updates the user wallet
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleUpdateWallet(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		UserManager userManager = new ServerUserManager(request.user, manager.getConnection());
		User walletToUpdate = User.fromJson(request.data);
		if (!userManager.updateWallet(walletToUpdate.userWallet)) {
			/* Product does not exists. */
			request.requestType = RequestType.REQUEST_FAILED;
		}
		request.data = null;
		return request;
	}

	/**
	 * sets items if they should be in the catalog or not
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private Request handleToFromCatalogue(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		ProductManager productManager = new ServerProductManager(request.user, manager.getConnection());
		Product toFromCatalogue = Product.fromJson(request.data);
		try {
			if (!productManager.productToFromCatalogue(toFromCatalogue.name, toFromCatalogue.inCatalogue)) {
				/* Product does not exists. */
				request.requestType = RequestType.REQUEST_FAILED;
			}
			request.data = null;
		} catch (PermissionDenied e) {
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("Can not change to/from catalogue field.").toJson();
		}
		return request;
	}

	/**
	 * sets the discount for product
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv and Jessica and Yarden
	 */
	private Request handleSetDiscount(Request request) throws PermissionDenied, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		ProductManager productManager = new ServerProductManager(request.user, manager.getConnection());
		Product product = Product.fromJson(request.data);
		if (!productManager.setDiscount(product.name, product.discount)) {
			/* Product does not exists. */
			request.requestType = RequestType.REQUEST_FAILED;
		}
		request.data = null;
		return request;
	}

	/**
	 * adds new complaint to the db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Jessica and Yarden
	 */
	private Request handleNewComplaint(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Complaint complaintRequest = Complaint.fromJson(request.data);
		if (manager.addNewCompliant(complaintRequest.userName, complaintRequest.orderId, complaintRequest.complaint,
				complaintRequest.date, complaintRequest.price, complaintRequest.complaintStatus,
				complaintRequest.refund, complaintRequest.shop, request.user.username))
			request.data = null;
		else {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Request failed in DB.").toJson();
		}
		return request;
	}

	/**
	 * delete orders for db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleDeleteOrder(Request request) {
		Order order = Order.fromJson(request.data);
		manager.deleteOrder(order);
		return request;
	}

	/**
	 * updates orders in db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleUpdateOrder(Request request) {
		Order order = Order.fromJson(request.data);
		manager.updateOrder(order);
		return request;
	}

	/**
	 * sets answers of surveys which customer fills
	 * 
	 * @param Request request
	 * @return Request
	 * @author Jessica and Yarden
	 */
	private Request handleSurveyAnswers(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Survey surveyRequest = Survey.fromJson(request.data);
		ServerUserManager serverUserManager = new ServerUserManager(request.user, manager.getConnection());
		if (serverUserManager.setSurveyAnswers(surveyRequest.getQuestion1(), surveyRequest.getQuestion2(),
				surveyRequest.getQuestion3(), surveyRequest.getQuestion4(), surveyRequest.getQuestion5(),
				surveyRequest.getQuestion6(), surveyRequest.getType(), surveyRequest.getShopName(),
				surveyRequest.getDate(), surveyRequest.getSurveyAnalyseId()))
			request.data = null;
		else {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Request failed in DB.").toJson();
		}
		return request;
	}

	/**
	 * gets all complaints of current user which handled them
	 * 
	 * @param Request request
	 * @return Request
	 * @author Jessica and Yarden
	 */
	private Request handleGetComplaints(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		ComplaintList complaintList = ComplaintList.fromJson(request.data);
		ServerUserManager serverUserManager = new ServerUserManager(request.user, manager.getConnection());
		complaintList = serverUserManager.getAllComplaints();
		request.data = complaintList.toJson();
		return request;
	}

	/**
	 * gets all user orders
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleGetUserOrder(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		OrderList orderList = OrderList.fromJson(request.data);
		ServerOrderManager serverOrderManager = new ServerOrderManager(request.user, manager.getConnection());
		orderList = serverOrderManager.getOrders(orderList.username);
		request.data = orderList.toJson();
		return request;
	}

	/**
	 * handled the refund amount per client
	 * 
	 * @param Request request
	 * @return Request
	 * @author Jessica and Yarden
	 */
	private Request handleGetRefund(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Refund refundRequest = Refund.fromJson(request.data);
		ServerUserManager serverUserManager = new ServerUserManager(request.user, manager.getConnection());
		if (serverUserManager.setRefundAmount(refundRequest.orderId, refundRequest.refund))
			request.data = null;
		else {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Request failed in DB.").toJson();
		}
		return request;
	}

	/**
	 * Logs in users to the db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleLogIn(Request request) {
		System.out.println("Correct user requested.");
		User toCheck = User.fromJson(request.data);
		if (!manager.logInUser(toCheck)) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = toCheck.toJson();
		}
		return request;
	}

	/**
	 * Logs off users to the db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleLogOff(Request request) {
		User user = User.fromJson(request.data);

		if (manager.logOffUser(user)) {
			return request;
		}

		request.requestType = RequestType.REQUEST_FAILED;
		return request;
	}

	/**
	 * change status of permission user
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleChangeStatus(Request request) {
		User user = User.fromJson(request.data);
		if (manager.changeStatus(user))
			return request;
		request.requestType = RequestType.REQUEST_FAILED;
		return request;
	}

	/**
	 * gets all the user from db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleGetUsers(Request request) {
		UsersList tmp = manager.getUsers();
		request.data = tmp.toJson();
		return request;
	}

	/**
	 * gets all the income reports from bar chart
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleGetIncomeReportsBC(Request request) throws SQLException {
		IncomeReportList incomeReportList = new IncomeReportList();

		incomeReportList = manager.getIncomeReportBC();

		if (incomeReportList == null) {
			System.out.println("Incorrect request.");
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = null;
		} else {
			request.data = incomeReportList.toJson();
		}
		return request;

	}

	/**
	 * gets all the income reports information
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleGetIncomeReports(Request request) throws SQLException {
		/*
		 * requestType.GET_INCOME_REPORT
		 */
		IncomeReport incomeReport = IncomeReport.fromJson(request.data);
		incomeReport = manager.getIncomeReport(incomeReport);
		if (incomeReport == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = incomeReport.toJson();
		}
		return request;

	}

	/**
	 * gets all the order reports
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleGetOrderReport(Request request) {
		OrderReport orderReport = OrderReport.fromJson(request.data);
		orderReport = manager.getOrderReport(orderReport);
		if (orderReport == null) {
			System.out.println("Incorrect request.");
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = null;
		} else {
			request.data = orderReport.toJson();
		}
		return request;
	}

	/**
	 * reponse sends back the data to the echo client
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private void respond(ConnectionToClient client, Request request) {
		try {
			client.sendToClient(request.toJson());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Handler for requestType: */
	/**
	 * gets user informations
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private Request handleGetUser(Request request) {
		/*
		 * requestType.GET_USER
		 */
		System.out.println("Correct user requested.");
		User toCheck = User.fromJson(request.data);

		toCheck.userrole = Role.WORKER;
		if (!manager.userLoggedIn(toCheck)) {
			System.out.println("User to be checkd is: " + toCheck.username);
			toCheck = manager.validateUser(toCheck);
		}

		else
			toCheck = null;

		if (toCheck == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = toCheck.toJson();
		}
		return request;
	}

	/**
	 * add new user to db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleAddUser(Request request) throws SQLIntegrityConstraintViolationException {
		/*
		 * requestType.ADD_USER
		 */
		User toAdd = User.fromJson(request.data);
		User checkUser = manager.validateUser(toAdd);

		if (checkUser == null || checkUser.cardNumber == null) {
			request.requestType = RequestType.REQUEST_FAILED;
			;
			request.data = new ServerError("User name allready in dataBase").toJson();
			return request;
		}

		try {
			if (manager.getUserManager(request.user).addNewUser(toAdd.username, toAdd.password, toAdd.nickname,
					toAdd.shopname, toAdd.userrole, toAdd.approved, toAdd.cardNumber, toAdd.exDate, toAdd.cvv,
					toAdd.logInfo, toAdd.userWallet.toString())) {
				/* User already exists. */

			} else
				request.requestType = RequestType.REQUEST_FAILED;
			request.data = null;
		} catch (WeakPassword e) {
			/* Bad password. */
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError(e.getMessage()).toJson();
		} catch (PermissionDenied e) {
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("Only manager can approve new users.").toJson();
		} catch (SQLIntegrityConstraintViolationException e1) {
			e1.printStackTrace();
			request.requestType = RequestType.REQUEST_FAILED;
			;
			request.data = new ServerError("User name not in Database").toJson();
		}
		return request;
	}

	/**
	 * sets user to approved
	 * 
	 * @param Request request
	 * @return Request
	 * @author Yohan and Niv
	 */
	private Request handleApproveUser(Request request) {
		/*
		 * requestType.APPROVE_USER
		 */
		User toApprove = User.fromJson(request.data);
		try {
			if (!manager.getUserManager(request.user).approveUser(toApprove.username)) {
				/* User does not exists. */
				request.requestType = RequestType.REQUEST_FAILED;
			}
			request.data = null;
		} catch (PermissionDenied e) {
			request.requestType = RequestType.FORBIDDEN;
			request.data = new ServerError("Only manager can approve new users.").toJson();
		}
		return request;
	}

	/**
	 * Gets all the product to the catalog
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private Request handleGetCategories(Request request) {
		/*
		 * requestType.GET_CATEGORIES
		 */
		CategoriesList categoriesList = new CategoriesList();
		categoriesList.items = new ArrayList<String>();
		categoriesList.items.addAll(manager.getProductManager(request.user).getCategories());
		request.data = categoriesList.toJson();
		return request;
	}

	/**
	 * gets all the product from db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private Request handleGetProducts(Request request) {
		/*
		 * requestType.GET_PRODUCTS
		 */
		ProductList productList = ProductList.fromJson(request.data);
		productList = manager.getProductManager(request.user).getProducts(productList.category);
		request.data = productList.toJson();
		return request;
	}

	/**
	 * add new order to db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private Request handleAddOrder(Request request)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		CartManager cartManager;
		cartManager = new ServerCartManager(request.user, manager.getConnection());
		Order order = Order.fromJson(request.data);
		order = cartManager.submitOrder(order);
		if (order == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = order.toJson();
		}

		return request;
	}

	/**
	 * gets all the orders from db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private Request handleGetOrders(Request request) {
		OrderList orders = OrderList.fromJson(request.data);
		if (orders.username.equals(Shop.HAIFA.name()) || orders.username.equals(Shop.NAHARIYA.name()))
			orders = manager.getOrderManager(request.user).getOrdersM(orders.username);
		else
			orders = manager.getOrderManager(request.user).getOrders(orders.username);
		if (orders == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = orders.toJson();
		}

		return request;
	}

	/**
	 * gets the singular product from db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Katya and Ahron
	 */
	private Request handleGetProduct(Request request) {
		/*
		 * requestType.GET_PRODUCT
		 */

		Product product = Product.fromJson(request.data);
		product = manager.getProductManager(request.user).getProduct(product.name);
		if (product == null) {
			System.out.println("Incorrect request.");
			request.data = null;
		} else {
			request.data = product.toJson();
		}

		return request;
	}

	/**
	 * analyse surveys answers and push to db
	 * 
	 * @param Request request
	 * @return Request
	 * @author Jessica and Yarden
	 */
	private Request handleSurveyAnalyse(Request request) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, FileNotFoundException {
		AnalyseFile analyseSurvey = AnalyseFile.fromJson(request.data);
		System.out.println("echo server first: " + analyseSurvey.toString()); // jessica
		ServerUserManager serverUserManager = new ServerUserManager(request.user, manager.getConnection());
		if (!(serverUserManager.analyseTypeSurvey(analyseSurvey))) {
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Request failed in DB.").toJson();
		}
		System.out.println("echo server : " + request.data); // jessica
		return request;
	}

}
