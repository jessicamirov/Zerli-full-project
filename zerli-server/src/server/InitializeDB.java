package server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import common.Role;

import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.ImageFile;
import common.request_data.Order;
import common.request_data.OrderStatus;
import common.request_data.OrderType;
import common.request_data.Product;
import common.request_data.ProductListCart;
import common.request_data.Shop;
import common.request_data.User;
import server.model.DBManager;
import server.model.ServerCartManager;
import server.model.ServerOrderManager;
import server.model.ServerProductManager;
import server.model.ServerUserManager;

public class InitializeDB {
	/* Add a default set of data that is enough to play with the application. */
	public void f(DBManager model) throws PermissionDenied, SQLIntegrityConstraintViolationException {
		createDatabase(model);

		Connection connection = null;
		try {
			connection = model.getConnection();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return;
		}
		
		addOrdersReports(connection);
		addOrderTable(connection);
		addIncomeReports(connection);
		addProducts(connection);
		addOrders(connection);
		addSurveys(connection);
		addSurveyAnalysis(connection);
		addComplaints(connection);
		addUsers(connection);
		
	}
	
	private void addSurveyAnalysis(Connection connection) {
		User worker = new User();
		worker.userrole = Role.WORKER;
		try {
			ServerUserManager.resetSurveyAnalysis(connection);
			ServerUserManager userManager = new ServerUserManager(worker, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	private void addOrders(Connection connection) {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		manager.userWallet = 0.0;
		try {
			ServerOrderManager.resetOrders(connection);
			ServerCartManager cartManager = new ServerCartManager(manager, connection);

			Order order = new Order();
			order.address = "Levi eshkol";
			order.city = "Haifa";
			order.date = "2022-06-08";
			order.greetingMessage = "I LOVE THIS PROJECT!";
			order.hour = "19:00";
			order.orderNumber = "";
			order.orderType = OrderType.DELIVERY;
			order.paymentPhone = "0525381648";
			order.phone = "0528211166";
			order.products = new ProductListCart();
			order.products.items = new HashMap<String,Integer>();
			order.products.items.put("Field Beauty", 4);
			order.recipient = "Adam";
			order.shop = Shop.HAIFA;
			order.signature = "From Yarden gabay";
			order.status = OrderStatus.WAITING_FOR_APPROVE;
			order.totalPrice = 0;
			order.username = "u3";
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			order.timeOfOrder = dtf.format(now);
			cartManager.submitOrder(order);
			
			order = new Order();
			order.address = "Hazit";
			order.city = "Haifa";
			order.date = "2022-06-09";
			order.greetingMessage = "I LOVE U";
			order.hour = "18:00";
			order.orderNumber = "";
			order.orderType = OrderType.TAKE_AWAY;
			order.paymentPhone = "0525381648";
			order.phone = "";
			order.products = new ProductListCart();
			order.products.items = new HashMap<String,Integer>();
			order.products.items.put("Pink Spring", 2);
			order.recipient = "Eve";
			order.shop = Shop.HAIFA;
			order.signature = "From Snake";
			order.status = OrderStatus.WAITING_FOR_APPROVE;
			order.totalPrice = 0;
			order.username = "u3";
			dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			now = LocalDateTime.now();
			order.timeOfOrder = dtf.format(now);
			cartManager.submitOrder(order);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void createDatabase(DBManager model) {
		model.createDatabase();
	}
	
	private void addOrdersReports(Connection connection) {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetOrdersReports(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			addOrdersReports(userManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	


	private void addIncomeReports(Connection connection) {
		
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetIncomeReports(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);

			addIncomeReports(userManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	

	private void addUsers(Connection connection) throws SQLIntegrityConstraintViolationException {
		User manager = new User();
		manager.userrole = Role.MANAGER;
		try {
			ServerUserManager.resetUsers(connection);
			ServerUserManager userManager = new ServerUserManager(manager, connection);
			
			//userManager.addNewUser("u2", "u2", "Katya",Shop.NONE, Role.CUSTOMER, true,"1111222233334444","18-7-2023","132",false,"0");
			userManager.addNewUser("o", "o", "Yarden",Shop.ALL, Role.OWNER, true,null,null,null,false,"100");
			userManager.addNewUser("m", "m", "Niv",Shop.HAIFA, Role.MANAGER, true,null,null,null,false,"100");
			userManager.addNewUser("e", "e", "Expert",Shop.ALL, Role.SERVICE_EXPERT, true,null,null,null,false,"100");
			//userManager.addNewUser("w1", "w1", "Good one",Shop.HAIFA, Role.WORKER, true,"1",null,null,false,"100");
			//userManager.addNewUser("w2", "w2", "Bad one",Shop.HAIFA, Role.WORKER, true,"0",null,null,false,null);
			userManager.addNewUser("s", "s", "Aaron",Shop.ALL, Role.SUPPORT, true,null,null,null,false,"100");
			userManager.addNewUser("d", "d", "Yagan",Shop.ALL, Role.DELIVERY, true,null,null,null,false,"100");
		} catch (WeakPassword | PermissionDenied e) {
			e.printStackTrace();
		}
	}
	
	private void addComplaints(Connection connection) {
		User worker = new User();
		worker.userrole = Role.WORKER;
		try {
			ServerUserManager.resetComplaints(connection);
			ServerUserManager userManager = new ServerUserManager(worker, connection);
			userManager.addNewCompliant("u3", "123", "ugly flowers", "07/01/2021 12:34:45\r\n"
					+ "", "100", "Awaiting response",
					"0", Shop.HAIFA,"s");
			userManager.addNewCompliant("u3", "234", "dry boquet", "07/03/2022 12:34:45\r\n"
					+ "", "50", "Awaiting response","0" ,
					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "12", "ugly flowers", "07/03/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "23", "dry boquet", "07/04/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "115", "ugly flowers", "07/05/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "217", "dry boquet", "07/06/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "1209", "ugly flowers", "07/07/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "2283", "dry boquet", "07/08/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "13", "ugly flowers", "07/09/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "25", "dry boquet", "07/10/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "18", "ugly flowers", "07/11/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "29", "dry boquet", "07/12/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "36", "ugly flowers", "07/01/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "78", "dry boquet", "07/03/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "178", "ugly flowers", "07/02/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "92", "dry boquet", "07/04/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "90", "ugly flowers", "07/07/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "66", "dry boquet", "07/05/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			userManager.addNewCompliant("u3", "64", "ugly flowers", "07/11/2021 12:34:45\r\n"
//					+ "", "100", "Awaiting response",
//					"0", Shop.HAIFA,"s");
//			userManager.addNewCompliant("u3", "872", "dry boquet", "07/9/2022 12:34:45\r\n"
//					+ "", "50", "Awaiting response","0" ,
//					Shop.NAHARIYA,"s");
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addSurveys(Connection connection) {
		User worker = new User();
		worker.userrole = Role.WORKER;
		try {
			ServerUserManager.resetSurvey(connection);
			ServerUserManager userManager = new ServerUserManager(worker, connection);
			// userManager.setSurveyAnswers(1, 2, 1, 4, 3, 7, "Sales survey", "HAIFA",
			// "2022/04");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
//			userManager.setSurveyAnswers(0, 0, 0, 0, 0, 0, "1", "2", "3");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addProducts(Connection connection) throws PermissionDenied {
		User support = new User();
		support.userrole = Role.SUPPORT;

		ServerProductManager.resetProducts(connection);
		ServerProductManager productManager = new ServerProductManager(support, connection);

		productManager.addProduct(new Product("Field Beauty", 40.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b1.jpg"),true));// /src/server/gallery/b1.jpg
	
		productManager.addProduct(new Product("Warm White", 60.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b2.jpg"),true));
		
		productManager.addProduct(new Product("Pink Spring", 55.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b3.jpg"),false));
		
		productManager.addProduct(
				new Product("Cute Ball", 70.0, 0, "Bouquet", ImageFile.asEncodedString("./src/server/gallery/b4.jpg"),false));
		
		productManager.addProduct(new Product("High Ground", 85.0, 0, "Bouquet",
				ImageFile.asEncodedString("./src/server/gallery/b5.jpg"),true));
		
		productManager.addProduct(
				new Product("With Love", 65.0, 0, "Bouquet", ImageFile.asEncodedString("./src/server/gallery/b6.jpg"),true));
		
		productManager.addProduct(new Product("Happy moments", 200.0, 0, "Wedding",
				ImageFile.asEncodedString("./src/server/gallery/w1.jpg"),true));
		
		productManager.addProduct(
				new Product("Memories", 150.0, 0, "Funeral", ImageFile.asEncodedString("./src/server/gallery/f1.jpg"),true));
		
		productManager.addProduct(new Product("Pink Orchid", 120.0, 0, "Flowerpot",
				ImageFile.asEncodedString("./src/server/gallery/p1.jpg"),true));
		
		productManager.addProduct(new Product("1m White Rose", 25.0, 0, "Retail",
				ImageFile.asEncodedString("./src/server/gallery/r1.jpg"),true));
		
		productManager.addProduct(new Product("0.6m Red Rose", 10.0, 0, "Retail",
				ImageFile.asEncodedString("./src/server/gallery/r2.jpg"),true));
		

	

	}

	private void addOrderTable(Connection connection) {

		ServerOrderManager.resetOrders(connection);

	}
	
	
	private void addIncomeReports(ServerUserManager userManager) throws SQLException {
		// add reports to HAIFA SHOP
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "January", "5000", "Flowers", "40");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "February", "4760", "Flowers", "19");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "March", "6020", "Flowers", "35");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "April", "3000", "Flowers", "20");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "May", "1500", "Flowers", "11");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "June", "730", "Flowers", "6");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "July", "680", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "August", "1356", "Flowers", "12");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "September", "2377", "Flowers", "15");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "October", "2985", "Flowers", "21");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "November", "5505", "Flowers", "45");
		userManager.addNewIncomeReport(Shop.HAIFA, "2020", "December", "7300", "Flowers", "63");

		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "January", "3450", "Flowers", "22");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "February", "4530", "Flowers", "32");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "March", "8000", "Flowers", "60");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "April", "9210", "Flowers", "85");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "May", "5760", "Flowers", "46");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "June", "350", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "July", "210", "Flowers", "2");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "August", "860", "Flowers", "7");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "September", "9780", "Flowers", "100");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "October", "1750", "Flowers", "9");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "November", "2800", "Flowers", "27");
		userManager.addNewIncomeReport(Shop.HAIFA, "2021", "December", "1600", "Flowers", "13");
		
		// add reports NAHARIYA SHOP
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "January", "2700", "Flowers", "40");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "February", "4200", "Flowers", "19");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "March", "8960", "Flowers", "35");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "April", "1600", "Flowers", "20");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "May", "720", "Flowers", "11");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "June", "1600", "Flowers", "6");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "July", "500", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "August", "2800", "Flowers", "12");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "September", "1450", "Flowers", "15");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "October", "3270", "Flowers", "21");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "November", "6000", "Flowers", "45");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2020", "December", "7000", "Flowers", "63");

		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "January", "4530", "Flowers", "22");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "February", "3200", "Flowers", "32");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "March", "6000", "Flowers", "60");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "April", "8200", "Flowers", "85");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "May", "4000", "Flowers", "46");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "June", "720", "Flowers", "4");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "July", "900", "Flowers", "2");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "August", "570", "Flowers", "7");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "September", "10000", "Flowers", "100");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "October", "2500", "Flowers", "9");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "November", "1900", "Flowers", "27");
		userManager.addNewIncomeReport(Shop.NAHARIYA, "2021", "December", "560", "Flowers", "13");
		
	}
	
	private void addOrdersReports(ServerUserManager userManager) {
		
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "January", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","40");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "February", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","19");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "March", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","35");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","20");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "May", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","11");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "June", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0","6");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "July", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","4");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "August", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","12");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "September", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12","15");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "October", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","21");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "November", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","45");
		userManager.addNewOrderReport(Shop.HAIFA, "2020", "December", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","63");
		
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "January", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "22");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "February", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "32");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "March", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "60");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "85");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "May", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "46");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "June", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "4");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "July", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "2");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "August", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "7");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "September", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "9");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "October", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20", "9");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "November", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "27");
		userManager.addNewOrderReport(Shop.HAIFA, "2021", "December", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "13");
		
		
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "January", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "32");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "February", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "85");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "March", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","19");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "April", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","21");
		userManager.addNewOrderReport(Shop.HAIFA, "2022", "May", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "31");
		
		
		
		
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "January", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","40");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "February", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","19");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "March", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","35");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","20");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "May", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","11");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "June", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0","6");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "July", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","4");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "August", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","12");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "September", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12","15");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "October", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1","21");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "November", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","45");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2021", "December", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2","63");
		
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "January", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "12", "22");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "February", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "32");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "March", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "60");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "April", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "85");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "May", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "46");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "June", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "4");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "July", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "2");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "August", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "7");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "September", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "9");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "October", "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20", "9");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "November", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15", "27");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2020", "December", "9", "8", "7", "6", "15", "4", "3", "2", "1", "0", "0", "13");
		
		
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "January", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "31");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "February",  "2", "6", "12", "13", "17", "0", "5", "7", "13", "9", "20","21");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "March", "3", "2", "4", "3", "7", "8", "14", "10", "15", "1", "15","19");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "April", "5", "8", "7", "16", "11", "21", "16", "17", "2", "31", "1", "85");
		userManager.addNewOrderReport(Shop.NAHARIYA, "2022", "May", "6", "4", "9", "2", "10", "17", "5", "13", "9", "13", "2", "32");
		
		
		
	}


}
