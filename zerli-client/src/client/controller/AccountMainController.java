package client.controller;

import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.AnalyseFileContainer;
import common.request_data.Complaint;
import common.request_data.ComplaintList;
import common.request_data.IncomeReport;
import common.request_data.IncomeReportList;
import common.request_data.Order;
import common.request_data.OrderList;
import common.request_data.OrderReport;
import common.request_data.OrderStatus;
import common.request_data.OrderTable;
import common.request_data.OrderType;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.Shop;
import common.request_data.Survey;
import common.request_data.User;
import common.request_data.UsersList;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import client.model.ProductView;
import common.Role;

import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * AccountMainController represented the left part of the client gui This class
 * builds the main panel on the customer screen, according to the situation and
 * the option that the customer chose
 * 
 * @Katya,Yarden,Jessica,Yohan
 *
 */
public class AccountMainController extends BaseController {
	@FXML
	Text mainHeader;
	@FXML
	GridPane content;
	@FXML
	ScrollPane scrollPane;

	private static int NUM_COLUMNS = 3;

	Text emailHeader = new Text();
	Text passwordHeader = new Text();
	Text nicknameHeader = new Text();
	Set<String> ordersSet = new HashSet<String>();
	ObservableList<String> ordersList = FXCollections.observableArrayList();
	IncomeReportList tempIncomeReport = new IncomeReportList();

	String path = null;
	File selectedFile = null;
	String surveyIdFlag = null;
	String shopTypeCB = "";
	OrderList allOrdersForUser = new OrderList();

	private static String CORRECT_EMAIL = "Email:";
	private static String EMAIL_NO_MATCH = "Email does not match:";
	private static String EMAIL_USED = "Email already registered:";
	private static String CORRECT_PASSWORD = "Password:";
	private static String PASSWORD_NO_MATCH = "Password does not match:";
	private static String PASSWORD_WEAK = "Password is too weak: ";
	private static String CORRECT_NICKNAME = "Nickname:";
	private static String EMPTY_NICKNAME = "Nickname cannot be empty:";
	private Role SELECTED_ACC_ROLE = null;
	private Shop SHOP_NAME_FOR_ACCOUNT = null;
	private String date = null;

	/**
	 * this function update the top of the screen its set the right user name
	 * details-user name,email and account settings
	 * 
	 * @Katya
	 *
	 */
	@Override
	public void update() {
		emailHeader.setText(CORRECT_EMAIL);
		passwordHeader.setText(CORRECT_PASSWORD);
		nicknameHeader.setText(CORRECT_NICKNAME);
	}

	private int getNumColumns() {
		return NUM_COLUMNS;
	}

	/**
	 * this function update the user main screen appearance
	 *
	 * in switch case we get the client's request to change screen and add to
	 * content- the main screen controller the screen data
	 *
	 *
	 * @param view the view name we want to insert
	 *
	 */
	@Override
	public void setView(String view) {
		scrollPane.setContent(content);
		mainHeader.setText(view + ":");
		content.getChildren().clear();
		content.getColumnConstraints().clear();
		content.getRowConstraints().clear();
		switch (view) {
		case ("Orders"):
			onOrders();
			break;
		case ("Orders report"):
			if (model.getUserManager().getCurrentUser().userrole.toString().equals("Manager"))
				handleManagerOrderReport();
			else if (model.getUserManager().getCurrentUser().userrole.toString().equals("Owner"))
				handleOwnerOrderReport();
			break;
		case ("Income report"):
			if (model.getUserManager().getCurrentUser().userrole.toString().equals("Manager"))
				handleManagerReport();
			else if (model.getUserManager().getCurrentUser().userrole.toString().equals("Owner"))
				content.getChildren().add(handleOwnerReport());
			break;
		case ("Account settings"):
			handleAccountSetting();
			break;
		case ("Compare reports"):
			handleCompareReport();
			break;
		case ("New account"):
			handleNewAccount();
			break;
		case ("Complaints"): {
			content.getChildren().add(complaints());
			break;
		}
		case ("Complaints handler"): {
			content.getChildren().add(ComplaintHandling());
			break;
		}
		// Supplier
		case ("Discounts"):
			mainHeader.setText(view + ":");
			onChangeDiscount();
			break;
		case ("Surveys"): {
			content.getChildren().add(insertSurveys());
			break;
		}
		case ("Surveys handler"): {
			content.getChildren().add(SurveysHandling());
			break;
		}
		case ("Change Catalogue"): {
			mainHeader.setText(view + ":");
			onChangeCatalogue();
			break;
		}
		case ("Get order"): {
			content.getChildren().add(deliveryGetOrderHandler());
			break;
		}
		case "Wallet Balance":
			onWalletBalance();
			break;
		case ("Complaints report"):
			handleOwnerComplaintsReport();
			break;
		}
	}

	/**
	 * set the content so that the delivery man can search the information about the
	 * orders
	 *
	 * @return VBox-that represent the content the delivery man
	 *
	 */
	private VBox deliveryGetOrderHandler() {
		VBox vbox = new VBox();
		VBox orderInformation = new VBox();
		orderInformation.setPadding(new Insets(5, 5, 5, 5));
		Text orderNumber = new Text("Order number:   ");
		Text phoneNumberOfTheRecipient = new Text("Phone to call:   ");
		Text dateOfOrder = new Text("Date to deliver:   ");
		Text timeOfOrder = new Text("Time to deliver:   ");
		Text nameOfTheRecipient = new Text("Name of the Recipient:   ");
		Text city = new Text("City:   ");
		Text street = new Text("Street:   ");
		Text price = new Text("Price:   ");
		orderInformation.getChildren().addAll(orderNumber, phoneNumberOfTheRecipient, dateOfOrder, timeOfOrder,
				nameOfTheRecipient, city, street, price);
		HBox idHbox = new HBox();
		HBox orderHbox = new HBox();
		HBox complaintHbox = new HBox();
		Label infoLabel = new Label();
		Label errorLabel = new Label();
		Label idLabel = new Label("Enter customer id ");
		Label ordersLabel = new Label("Choose order");
		TextField idTextField = new TextField("");
		ChoiceBox<String> ordersChoiceBox = new ChoiceBox<String>();
		Button searchIdBtn = new Button("search");

		Button sendDeliveryStatus = new Button("send");
		OrderList orderList = new OrderList();
		orderList.orders = new ArrayList<Order>();
		// Initialize => Disable all button except the name search
		ordersChoiceBox.setDisable(true);

		sendDeliveryStatus.setDisable(true);
		searchIdBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String username = idTextField.getText();
				ordersChoiceBox.getItems().clear();
				if (username == null || username.equals(""))
					errorLabel.setText("You must enter customer id!"); // NEED TO CHECK IF THIS USER REALLY EXSISTED
				else {
					ordersChoiceBox.setDisable(false);

					allOrdersForUser.username = username;
					allOrdersForUser = model.getOrderManager().getOrders(allOrdersForUser.username);
					if (allOrdersForUser == null || allOrdersForUser.orders.size() == 0)
						errorLabel.setText("No orders for " + username + "\r\nor user doesn't exists");
					else
						errorLabel.setText("");
					for (Order o : allOrdersForUser.orders) {
						if (o.status.equals(OrderStatus.ON_DELIVERY)) {
							ordersChoiceBox.getItems().add(o.orderNumber);
							orderList.orders.add(o);
						}
					}
				}
			}
		});
		//// Choice box onAction
		ordersChoiceBox.setOnAction((event) -> {
			String orderId = ordersChoiceBox.getSelectionModel().getSelectedItem().toString();
			for (Order o : allOrdersForUser.orders)
				if (o.orderNumber.equals(orderId)) {
					orderNumber.setText(orderNumber.getText() + o.orderNumber);
					phoneNumberOfTheRecipient.setText(phoneNumberOfTheRecipient.getText() + o.phone);
					dateOfOrder.setText(dateOfOrder.getText() + o.date);
					timeOfOrder.setText(timeOfOrder.getText() + o.hour);
					nameOfTheRecipient.setText(nameOfTheRecipient.getText() + o.recipient);
					city.setText(city.getText() + o.city);
					street.setText(street.getText() + o.address);
					price.setText(price.getText() + o.totalPrice);
				}
			// allOrdersForUser
			sendDeliveryStatus.setDisable(false);
		});
		// Send onAction
		orderHbox.getChildren().addAll(ordersLabel, ordersChoiceBox);
		idHbox.getChildren().addAll(idLabel, idTextField, searchIdBtn);
		sendDeliveryStatus.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String orderId = ordersChoiceBox.getSelectionModel().getSelectedItem().toString();
				for (Order o : allOrdersForUser.orders)
					if (o.orderNumber.equals(orderId)) {
						checkOntime(o);
					}
				infoLabel.setText("Order was handeld!!");
			}
		});
		complaintsCss(idHbox, vbox, complaintHbox, orderHbox);
		// ordersList.removeAll(ordersList);
		vbox.getChildren().addAll(errorLabel, idHbox, orderHbox, complaintHbox, orderInformation, sendDeliveryStatus,
				infoLabel);
		return vbox;
	}

	/**
	 * Builds the add new user screen
	 *
	 * create the sign up screen in which the user details need to be fill screen
	 * data also check if the user can be sign up and he insert correct proper
	 * details - strong password,not existing user name..
	 *
	 * @param view the view name due to the client request
	 *
	 */
	public void handleNewAccount() {
		HBox twoCol = new HBox();
		HBox roleCol = new HBox();
		VBox leftMenu = new VBox();
		VBox rightMenu = new VBox();

		ChoiceBox<String> shopBox = new ChoiceBox<String>();

		shopBox.setDisable(true);

		// CreditCard SetUP
		Text cardNumber = new Text("Card number:");
		TextField cardNumberText = new TextField();
		cardNumberText.setPromptText("Must have 12 numbers");
		Text expirationText = new Text("Expiration date:");
		DatePicker expirationDateChoice = new DatePicker();
		Text cvvText = new Text("CVV:");
		TextField cvvNumberField = new TextField();
		cvvNumberField.setPromptText("Must have 3 numbers");
		TextField email = new TextField();
		email.setPromptText("email");
		TextField reemail = new TextField();
		reemail.setPromptText("Retype email");

		// Worker SetUP
		Text workerText = new Text("Give the worker the option to change the catalogue?");
		ChoiceBox<String> yesNoBox = new ChoiceBox<String>();
		ObservableList<String> ynlist = FXCollections.observableArrayList("Yes", "No");
		yesNoBox.setItems(ynlist);

		// Password setUp

		TextField password = new TextField();
		password.setPromptText("Password");
		TextField repassword = new TextField();
		repassword.setPromptText("Retype password");

		// Nickname setUp
		TextField nickname = new TextField();
		nickname.setPromptText("Nickname");

		// Register successful Text
		Text regText = new Text("");

		// Type setUp
		Text typeText = new Text("Type of account and the shop he gets:");
		ChoiceBox<String> roleBox = new ChoiceBox<String>();
		ObservableList<String> list = FXCollections.observableArrayList("Customer", "Worker");
		roleBox.setItems(list);
		roleBox.setOnAction((event) -> {
			SHOP_NAME_FOR_ACCOUNT = null;
			cardNumberText.clear();
			expirationDateChoice.setValue(null);
			cvvNumberField.clear();
			rightMenu.getChildren().clear();
			regText.setText("");
			Role[] allRoles = Role.values();
			for (Role r : allRoles) {
				// Comparing
				if (r.toString().equals(roleBox.getSelectionModel().getSelectedItem().toString()))
					SELECTED_ACC_ROLE = r;
			}
			if (SELECTED_ACC_ROLE.equals(Role.CUSTOMER)) {
				rightMenu.getChildren().add(cardNumber);
				rightMenu.getChildren().add(cardNumberText);
				rightMenu.getChildren().add(expirationText);
				rightMenu.getChildren().add(expirationDateChoice);
				rightMenu.getChildren().add(cvvText);
				rightMenu.getChildren().add(cvvNumberField);
			} else {
				rightMenu.getChildren().add(workerText);
				rightMenu.getChildren().add(yesNoBox);
			}
		});
		shopBox.setDisable(false);
		ObservableList<String> list2 = FXCollections.observableArrayList("Haifa", "Nahariya");
		shopBox.setItems(list2);
		shopBox.setOnAction((event) -> {
			Shop[] allShops = Shop.values();
			for (Shop s : allShops) {
				// Comparing
				if (s.toString().equals(shopBox.getSelectionModel().getSelectedItem().toString()))
					SHOP_NAME_FOR_ACCOUNT = s;
			}
		});

		// Button setUp
		Button btn = new Button("Register");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				boolean isBad = false;

				if (!email.getText().equals(reemail.getText()) || email.getText().equals("")) {
					emailHeader.setText(EMAIL_NO_MATCH);
					regText.setText("Wrong inputs");
					isBad = true;
				} else {
					emailHeader.setText(CORRECT_EMAIL);
				}
				if (!password.getText().equals(repassword.getText()) || password.getText().equals("")) {
					passwordHeader.setText(PASSWORD_NO_MATCH);
					regText.setText("Wrong inputs");
					isBad = true;
				} else {
					passwordHeader.setText(CORRECT_PASSWORD);
				}
				if (nickname.getText().equals("")) {
					nicknameHeader.setText(EMPTY_NICKNAME);
					regText.setText("Wrong inputs");
					isBad = true;
				} else {
					nicknameHeader.setText(CORRECT_NICKNAME);
				}
				if (SELECTED_ACC_ROLE == null || SHOP_NAME_FOR_ACCOUNT == null) {
					regText.setText("You must choose a Role and a Shop option");
					return;
				}

				if (SELECTED_ACC_ROLE.equals(Role.CUSTOMER)) {
					if (cardNumberText.getText().equals("") || expirationDateChoice.getValue() == null
							|| cvvNumberField.getText().equals("")) {
						regText.setText("Please fill all credit card details");
						isBad = true;
					} else {
						try {
							String tmpCard = cardNumberText.getText();
							String tmpCVV = cvvNumberField.getText();

							if (tmpCard.length() != 12 || tmpCVV.length() != 3) {

								tmpCard = tmpCard + "A";
								tmpCVV = tmpCVV + "A";
							}

							Double x = Double.parseDouble(tmpCard);
							x = Double.parseDouble(tmpCVV);
						} catch (NumberFormatException e2) {
							regText.setText("Wrong card informatin, please enter again!");
							cardNumberText.clear();
							cvvNumberField.clear();
							isBad = true;
						}
					}

				}

				else if (SELECTED_ACC_ROLE.equals(Role.WORKER)) {
					if (yesNoBox.getSelectionModel().getSelectedItem() == null) {
						regText.setText("Please choose if the worker can change the catalogue");
						isBad = true;
					}
				}

				if (isBad) {

					return;
				}
				try {
					if (SELECTED_ACC_ROLE.equals(Role.CUSTOMER)) {
						date = expirationDateChoice.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

					} else if (SELECTED_ACC_ROLE.equals(Role.WORKER)) {
						if (yesNoBox.getSelectionModel().getSelectedItem().equals("Yes"))
							cardNumberText.setText("1");
						else
							cardNumberText.setText("0");
						System.out.println("387 accountMainController " + cardNumberText.getText());
					}

					if (!model.getUserManager().addNewUser(email.getText().trim(), password.getText().trim(),
							nickname.getText().trim(), SHOP_NAME_FOR_ACCOUNT, SELECTED_ACC_ROLE, true,
							cardNumberText.getText().trim(), date, cvvNumberField.getText().trim(), false, "0.0")) {
						regText.setText("User name not in Database");
						return;
					}
					regText.setText("Registered successfully!!!");

				} catch (WeakPassword e1) {
					passwordHeader.setText(PASSWORD_WEAK + e1.getMessage());
					regText.setText("Didnt Register new account!");
					return;
				} catch (PermissionDenied e2) {

					return;
				}

			}
		});

		// Set the content
		leftMenu.getChildren().add(emailHeader);
		leftMenu.getChildren().add(email);
		leftMenu.getChildren().add(reemail);
		leftMenu.getChildren().add(passwordHeader);
		leftMenu.getChildren().add(password);
		leftMenu.getChildren().add(repassword);
		leftMenu.getChildren().add(nicknameHeader);
		leftMenu.getChildren().add(nickname);
		leftMenu.getChildren().add(typeText);
		roleCol.getChildren().add(roleBox);
		roleCol.getChildren().add(shopBox);
		leftMenu.getChildren().add(roleCol);
		leftMenu.getChildren().add(btn);
		leftMenu.getChildren().add(regText);
		twoCol.getChildren().add(leftMenu);
		twoCol.getChildren().add(rightMenu);
		newAccountCSS(leftMenu, rightMenu, roleCol, twoCol);
		content.getChildren().add(twoCol);
	}

	public void newAccountCSS(VBox menuLeft, VBox menuRight, HBox roleShop, HBox main) {
		roleShop.setSpacing(5);
		menuLeft.setSpacing(5);
		menuRight.setSpacing(5);
		main.setSpacing(30);
	}

	// Income reports for manager
	private void handleManagerReport() {
		HBox mainContantCol = new HBox();
		// BC initialize

		final CategoryAxis xAxis = new CategoryAxis(); // tzir x
		final NumberAxis yAxis = new NumberAxis(); // tzir y
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);

		// rightMenu initialize
		VBox rightMenu = new VBox();
		rightMenu.setVisible(false);
		Text myShopIncome = new Text();
		Text topSellingItemText = new Text("TOP SOLD ITEM OF THE MONTH:");
		topSellingItemText.setFill(Color.RED);
		topSellingItemText.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		Text topSellingItem = new Text("");
		topSellingItem.setFill(Color.GREEN);
		Text numberOfOrdersText = new Text("TOTAL NUMBER OF ORDERS:");
		numberOfOrdersText.setFill(Color.RED);
		numberOfOrdersText.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		Text numberOfOrders = new Text("");
		numberOfOrders.setFill(Color.GREEN);
		Text topSellingShopText = new Text("TOP SELLING SHOP:");
		topSellingShopText.setFill(Color.RED);
		topSellingShopText.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		Text topSellingShop = new Text("");
		topSellingShop.setFill(Color.GREEN);

		// leftMenu initialize

		HBox twoCol = new HBox();
		VBox leftMenu = new VBox();
		Text topTxt = new Text("Choose a Year and month of income:");
		ChoiceBox<String> yearBox = new ChoiceBox<String>();
		ChoiceBox<String> monthBox = new ChoiceBox<String>();
		List<XYChart.Series> allSeries = new ArrayList<XYChart.Series>();
		monthBox.setDisable(true);

		// ChoiceBox setup
		ObservableList<String> yearList = FXCollections.observableArrayList("2020", "2021", "2022");
		yearBox.setItems(yearList);
		ObservableList<String> monthList = FXCollections.observableArrayList("January", "February", "March", "April",
				"May", "June", "July", "August", "September", "October", "November", "December");
		monthBox.setItems(monthList);

		// ChoiceBox functions
		yearBox.setOnAction((event) -> {

			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.setTitle(null);
			monthBox.setDisable(false);
			rightMenu.setVisible(false);
		});

		monthBox.setOnAction((event) -> {
			rightMenu.setVisible(true);
			bc.getData().clear();
			bc.setTitle(null);
			String shopName, year, month;
			shopName = model.getUserManager().getCurrentUser().shopname.toString();
			year = yearBox.getSelectionModel().getSelectedItem().toString();
			month = monthBox.getSelectionModel().getSelectedItem().toString();
			bc.setTitle(shopName + " " + year + " " + month + " Income Compared to other stores");
			xAxis.setLabel("Month");
			yAxis.setLabel("Income");
			try {
				IncomeReport myReport = model.getUserManager()
						.getIncomeReport(model.getUserManager().getCurrentUser().shopname, year, month);
				if (myReport.income == null) {
					bc.setTitle("NO REPORT FOR THAT YEAR AND MONTH!!!");
					myShopIncome.setText("");
					topSellingItem.setText("");
					numberOfOrders.setText("");
					topSellingShop.setText("");
				} else {
					topSellingItem.setText(myReport.bestSellingProduct);
					numberOfOrders.setText(myReport.totalNumberOfOrders);
					myShopIncome.setText(shopName + " income is: " + myReport.income);
					XYChart.Series series1 = new XYChart.Series();
					series1.setName(shopName);
					series1.getData().add(new XYChart.Data(month, Integer.parseInt(myReport.income)));
					bc.getData().add(series1);

					// Set all other shops income
					tempIncomeReport = model.getUserManager().getAllIncomeReports();
					int maxIncomeShop = 0;
					String topSellingShopName = "";
					for (IncomeReport r : tempIncomeReport.Reports) {
						if (maxIncomeShop < Integer.parseInt(r.income)) {
							topSellingShopName = r.shop.name();
							maxIncomeShop = Integer.parseInt(r.income);
						}
						if (!r.shop.toString().equals(shopName)) {
							XYChart.Series series2 = new XYChart.Series();

							series2.setName(r.shop.name().toString());// Change to tempIncomeReports
							series2.getData().add(new XYChart.Data(month, Integer.parseInt(r.income)));
							bc.getData().add(series2);
						}
					}
					topSellingShop.setText(
							topSellingShopName + " With total income value of --> " + String.valueOf(maxIncomeShop));
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		// Content setUP
		leftMenu.getChildren().add(topTxt);
		leftMenu.getChildren().add(yearBox);
		leftMenu.getChildren().add(monthBox);
		leftMenu.getChildren().add(bc);
		rightMenu.getChildren().add(myShopIncome);
		rightMenu.getChildren().add(topSellingItemText);
		rightMenu.getChildren().add(topSellingItem);
		rightMenu.getChildren().add(numberOfOrdersText);
		rightMenu.getChildren().add(numberOfOrders);
		rightMenu.getChildren().add(topSellingShopText);
		rightMenu.getChildren().add(topSellingShop);
		mainContantCol.getChildren().add(leftMenu);
		mainContantCol.getChildren().add(rightMenu);
		managerIncomeReportCSS(leftMenu, rightMenu, mainContantCol);
		content.getChildren().add(mainContantCol);

	}

	public void managerIncomeReportCSS(VBox left, VBox right, HBox main) {
		left.setSpacing(5);
		right.setSpacing(5);
		main.setSpacing(30);

	}

	private void handleOwnerComplaintsReport() {
		final CategoryAxis xAxis = new CategoryAxis(); // tzir x
		final NumberAxis yAxis = new NumberAxis(); // tzir y
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);

		// Menu initialize
		HBox twoCol = new HBox();
		VBox menu = new VBox();
		Text shopTxt = new Text("Choose a Shop:");
		Text yearTxt = new Text("Choose a Year:");
		Text quarterTxt = new Text("Choose a quarter:");
		ChoiceBox<String> shopBox = new ChoiceBox<String>();
		ChoiceBox<String> yearBox = new ChoiceBox<String>();
		ChoiceBox<String> monthBox = new ChoiceBox<String>();
		Text firstMonthComplaints = new Text();
		Text secondMonthComplaints = new Text();
		Text thirdMonthComplaints = new Text();
		Text totalSum = new Text();

		yearBox.setDisable(true);
		monthBox.setDisable(true);

		// ChoiceBox setup
		ObservableList<String> shopList = FXCollections.observableArrayList(Shop.HAIFA.toString(),
				Shop.NAHARIYA.toString());
		shopBox.setItems(shopList);
		ObservableList<String> yearList = FXCollections.observableArrayList("2020", "2021", "2022");
		yearBox.setItems(yearList);
		ObservableList<String> monthList = FXCollections.observableArrayList("First", "Second", "Third", "Fourth");
		monthBox.setItems(monthList);

		// ChoiceBox functions
		shopBox.setOnAction((event) -> {
			yearBox.valueProperty().set(null);
			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.setTitle(null);
			yearBox.setDisable(false);
			monthBox.setDisable(true);
		});
		yearBox.setOnAction((event) -> {
			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.setTitle(null);
			monthBox.setDisable(false);
		});

		monthBox.setOnAction((event) -> {
			bc.getData().clear();
			bc.setTitle(null);
			String shopName, year, quarter;

			// Set shop to send to sql
			Shop shopToSQL;
			Shop[] allShops = Shop.values();
			for (Shop s : allShops) {
				// Comparing
				if (s.toString().equals(shopBox.getSelectionModel().getSelectedItem().toString()))
					SHOP_NAME_FOR_ACCOUNT = s;
			}

			shopName = shopBox.getSelectionModel().getSelectedItem().toString();
			year = yearBox.getSelectionModel().getSelectedItem().toString();
			quarter = monthBox.getSelectionModel().getSelectedItem().toString();
			bc.setTitle(shopName + " " + year + " " + quarter + " quarter Complaints");
			xAxis.setLabel(quarter + " quarter");
			yAxis.setLabel("Complaints");
			ComplaintList listAllComplaints = model.getUserManager().getAllComplaints();

			try {
				IncomeReport myReport, myReport2, myReport3;
				XYChart.Series series1, series2, series3;
				int monthlyComplaints = 0;

				switch (quarter) {

				case "First":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "January");

					firstMonthComplaints.setText("Total Complaints for January: " + getMonthlyComplaints(
							listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "JANUARY"));
					series1 = new XYChart.Series();
					series1.setName("January");
					series1.getData().add(new XYChart.Data(quarter, getMonthlyComplaints(listAllComplaints.complaints,
							SHOP_NAME_FOR_ACCOUNT, year, "JANUARY")));
					bc.getData().add(series1);

					myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "February");

					secondMonthComplaints.setText("Total complaints for February: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "02"));
					series2 = new XYChart.Series();
					series2.setName("February");
					series2.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "02")));
					bc.getData().add(series2);

					myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "March");

					thirdMonthComplaints.setText("Total complaints for March: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "MARCH"));
					series3 = new XYChart.Series();
					series3.setName("March");
					series3.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "03")));
					bc.getData().add(series3);

					totalSum.setText("Total Compliants for this quarter is: " + (getMonthlyComplaints(
							listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "01")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "02")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "03")));
					break;

				case "Second":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "April");

					firstMonthComplaints.setText("Total income for April: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "04"));
					series1 = new XYChart.Series();
					series1.setName("April");
					series1.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "04")));
					bc.getData().add(series1);

					myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "May");

					secondMonthComplaints.setText("Total income for May: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "05"));
					series2 = new XYChart.Series();
					series2.setName("May");
					series2.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "05")));
					bc.getData().add(series2);

					myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "June");

					thirdMonthComplaints.setText("Total income for June: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "06"));
					series3 = new XYChart.Series();
					series3.setName("June");
					series3.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "06")));
					bc.getData().add(series3);

					totalSum.setText("Total Compliants for this quarter is: " + (getMonthlyComplaints(
							listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "04")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "05")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "06")));
					break;

				case "Third":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "July");

					firstMonthComplaints.setText("Total income for July: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "07"));
					series1 = new XYChart.Series();
					series1.setName("July");
					series1.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "07")));
					bc.getData().add(series1);

					myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "August");

					secondMonthComplaints.setText("Total income for August: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "08"));
					series2 = new XYChart.Series();
					series2.setName("August");
					series2.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "08")));
					bc.getData().add(series2);

					myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "September");

					thirdMonthComplaints.setText("Total income for September: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "09"));
					series3 = new XYChart.Series();
					series3.setName("September");
					series3.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "09")));
					bc.getData().add(series3);

					totalSum.setText("Total Compliants for this quarter is: " + (getMonthlyComplaints(
							listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "07")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "08")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "09")));
					break;

				case "Fourth":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "October");

					firstMonthComplaints.setText("Total income for October: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "10"));
					series1 = new XYChart.Series();
					series1.setName("October");
					series1.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "10")));
					bc.getData().add(series1);

					myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "November");

					secondMonthComplaints.setText("Total income for November: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "11"));
					series2 = new XYChart.Series();
					series2.setName("November");
					series2.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "11")));
					bc.getData().add(series2);

					myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "December");

					thirdMonthComplaints.setText("Total income for December: "
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "12"));
					series3 = new XYChart.Series();
					series3.setName("December");
					series3.getData().add(new XYChart.Data(quarter,
							getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "12")));
					bc.getData().add(series3);

					totalSum.setText("Total Compliants for this quarter is: " + (getMonthlyComplaints(
							listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "10")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "11")
							+ getMonthlyComplaints(listAllComplaints.complaints, SHOP_NAME_FOR_ACCOUNT, year, "12")));
					break;

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		VBox rightMenu = new VBox();
		rightMenu.getChildren().addAll(firstMonthComplaints, secondMonthComplaints, thirdMonthComplaints, totalSum);
		// Content setUP
		menu.getChildren().add(shopTxt);
		menu.getChildren().add(shopBox);
		menu.getChildren().add(yearTxt);
		menu.getChildren().add(yearBox);
		menu.getChildren().add(quarterTxt);
		menu.getChildren().add(monthBox);
		menu.getChildren().add(bc);
		twoCol.getChildren().addAll(menu, rightMenu);
		content.getChildren().add(twoCol);

	}

	/**
	 *
	 * return the amount of the complaints of a shop at a specific year and month
	 * 
	 * @param complaint - complaints array
	 * @author Yohan and Yarden
	 */
	private int getMonthlyComplaints(ArrayList<Complaint> complaints, Shop shop, String year, String month) {
		int count = 0;
		for (Complaint c : complaints) {
			LocalDate date = LOCAL_DATE(c.date);
			if ((date.getMonth().toString().equals(month)) && (date.getYear() == Integer.parseInt(year)))
				count++;
		}
		return count;
	}

	/**
	 *
	 * Sets the content to the income report from the sql
	 * 
	 * @return HBox hbox
	 * @author Yohan, Niv
	 */
	private HBox handleOwnerReport() {
		// BC initialize
		final CategoryAxis xAxis = new CategoryAxis(); // tzir x
		final NumberAxis yAxis = new NumberAxis(); // tzir y
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
		// Menu initialize
		HBox twoCol = new HBox();
		VBox menu = new VBox();
		Text shopTxt = new Text("Choose a Shop:");
		Text yearTxt = new Text("Choose a Year:");
		Text quarterTxt = new Text("Choose a quarter:");
		ChoiceBox<String> shopBox = new ChoiceBox<String>();
		ChoiceBox<String> yearBox = new ChoiceBox<String>();
		ChoiceBox<String> monthBox = new ChoiceBox<String>();
		Text firstMonthIncome = new Text();
		Text secondMonthIncome = new Text();
		Text thirdMonthIncome = new Text();
		Text totalSum = new Text();
		yearBox.setDisable(true);
		monthBox.setDisable(true);
		// ChoiceBox setup
		ObservableList<String> shopList = FXCollections.observableArrayList(Shop.HAIFA.toString(),
				Shop.NAHARIYA.toString());
		shopBox.setItems(shopList);
		ObservableList<String> yearList = FXCollections.observableArrayList("2020", "2021", "2022");
		yearBox.setItems(yearList);
		ObservableList<String> monthList = FXCollections.observableArrayList("First", "Second", "Third", "Fourth");
		monthBox.setItems(monthList);
		// ChoiceBox functions
		shopBox.setOnAction((event) -> {
			yearBox.valueProperty().set(null);
			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.setTitle(null);
			yearBox.setDisable(false);
			monthBox.setDisable(true);
		});
		yearBox.setOnAction((event) -> {
			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.setTitle(null);
			firstMonthIncome.setText("");
			secondMonthIncome.setText("");
			thirdMonthIncome.setText("");
			totalSum.setText("");
			monthBox.setDisable(false);
		});
		monthBox.setOnAction((event) -> {
			bc.getData().clear();
			bc.setTitle(null);
			String shopName, year, quarter;
			// Set shop to send to sql
			Shop shopToSQL;
			Shop[] allShops = Shop.values();
			for (Shop s : allShops) {
				// Comparing
				if (s.toString().equals(shopBox.getSelectionModel().getSelectedItem().toString()))
					SHOP_NAME_FOR_ACCOUNT = s;
			}
			shopName = shopBox.getSelectionModel().getSelectedItem().toString();
			year = yearBox.getSelectionModel().getSelectedItem().toString();
			quarter = monthBox.getSelectionModel().getSelectedItem().toString();
			bc.setTitle(shopName + " " + year + " " + quarter + " quarter Income");
			xAxis.setLabel(quarter + " quarter");
			yAxis.setLabel("Income");
			try {
				IncomeReport myReport, myReport2, myReport3;
				XYChart.Series series1, series2, series3;

				switch (quarter) {

				case "First":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "January");
					if (myReport.income == null) {
						bc.setTitle("NO REPORTS FOR THAT QUARTER!!");
						firstMonthIncome.setText("");
						secondMonthIncome.setText("");
						thirdMonthIncome.setText("");
						totalSum.setText("");
					} else {
						firstMonthIncome.setText("Total income for January: " + myReport.income);
						series1 = new XYChart.Series();
						series1.setName("January");
						series1.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport.income)));
						bc.getData().add(series1);

						myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "February");
						secondMonthIncome.setText("Total income for February: " + myReport2.income);
						series2 = new XYChart.Series();
						series2.setName("February");
						series2.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport2.income)));
						bc.getData().add(series2);

						myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "March");
						thirdMonthIncome.setText("Total income for March: " + myReport3.income);
						series3 = new XYChart.Series();
						series3.setName("March");
						series3.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport3.income)));
						bc.getData().add(series3);

						totalSum.setText("Total income for this quarter is: " + (Integer.parseInt(myReport.income)
								+ Integer.parseInt(myReport2.income) + Integer.parseInt(myReport3.income)));
						break;
					}
				case "Second":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "April");
					if (myReport.income == null) {
						bc.setTitle("NO REPORTS FOR THAT QUARTER!!");
						firstMonthIncome.setText("");
						secondMonthIncome.setText("");
						thirdMonthIncome.setText("");
						totalSum.setText("");
					} else {
						firstMonthIncome.setText("Total income for April: " + myReport.income);
						series1 = new XYChart.Series();
						series1.setName("April");
						series1.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport.income)));
						bc.getData().add(series1);

						myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "May");
						secondMonthIncome.setText("Total income for May: " + myReport2.income);
						series2 = new XYChart.Series();
						series2.setName("May");
						series2.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport2.income)));
						bc.getData().add(series2);

						myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "June");
						thirdMonthIncome.setText("Total income for June: " + myReport3.income);
						series3 = new XYChart.Series();
						series3.setName("June");
						series3.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport3.income)));
						bc.getData().add(series3);

						totalSum.setText("Total income for this quarter is: " + (Integer.parseInt(myReport.income)
								+ Integer.parseInt(myReport2.income) + Integer.parseInt(myReport3.income)));
						break;
					}
				case "Third":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "July");
					if (myReport.income == null) {
						bc.setTitle("NO REPORTS FOR THAT QUARTER!!");
						firstMonthIncome.setText("");
						secondMonthIncome.setText("");
						thirdMonthIncome.setText("");
						totalSum.setText("");
					} else {
						firstMonthIncome.setText("Total income for July: " + myReport.income);
						series1 = new XYChart.Series();
						series1.setName("July");
						series1.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport.income)));
						bc.getData().add(series1);

						myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "August");
						secondMonthIncome.setText("Total income for August: " + myReport2.income);
						series2 = new XYChart.Series();
						series2.setName("August");
						series2.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport2.income)));
						bc.getData().add(series2);

						myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "September");
						thirdMonthIncome.setText("Total income for September: " + myReport3.income);
						series3 = new XYChart.Series();
						series3.setName("September");
						series3.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport3.income)));
						bc.getData().add(series3);

						totalSum.setText("Total income for this quarter is: " + (Integer.parseInt(myReport.income)
								+ Integer.parseInt(myReport2.income) + Integer.parseInt(myReport3.income)));
						break;
					}
				case "Fourth":
					myReport = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "October");
					if (myReport.income == null) {
						bc.setTitle("NO REPORTS FOR THAT QUARTER!!");
						firstMonthIncome.setText("");
						secondMonthIncome.setText("");
						thirdMonthIncome.setText("");
						totalSum.setText("");
					} else {
						firstMonthIncome.setText("Total income for October: " + myReport.income);
						series1 = new XYChart.Series();
						series1.setName("October");
						series1.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport.income)));
						bc.getData().add(series1);

						myReport2 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "November");
						secondMonthIncome.setText("Total income for November: " + myReport2.income);
						series2 = new XYChart.Series();
						series2.setName("November");
						series2.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport2.income)));
						bc.getData().add(series2);

						myReport3 = model.getUserManager().getIncomeReport(SHOP_NAME_FOR_ACCOUNT, year, "December");
						thirdMonthIncome.setText("Total income for December: " + myReport3.income);
						series3 = new XYChart.Series();
						series3.setName("December");
						series3.getData().add(new XYChart.Data(quarter, Integer.parseInt(myReport3.income)));
						bc.getData().add(series3);

						totalSum.setText("Total income for this quarter is: " + (Integer.parseInt(myReport.income)
								+ Integer.parseInt(myReport2.income) + Integer.parseInt(myReport3.income)));
						break;
					}
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		VBox rightMenu = new VBox();
		rightMenu.getChildren().addAll(firstMonthIncome, secondMonthIncome, thirdMonthIncome, totalSum);
		// Content setUP
		menu.getChildren().add(shopTxt);
		menu.getChildren().add(shopBox);
		menu.getChildren().add(yearTxt);
		menu.getChildren().add(yearBox);
		menu.getChildren().add(quarterTxt);
		menu.getChildren().add(monthBox);
		menu.getChildren().add(bc);
		twoCol.getChildren().addAll(menu, rightMenu);
		return twoCol;
	}

	private void handleCompareReport() {
		HBox h = new HBox();
		HBox left = handleOwnerReport();
		HBox right = handleOwnerReport();
		h.getChildren().addAll(left, right);
		h.setPrefHeight(400);
		h.setPrefWidth(200);
		content.getChildren().add(h);

	}

	/**
	 *
	 * Sets the content to the income report from the sql with two chartes
	 * 
	 * @return HBox hbox
	 * @author Yohan, Niv
	 */
	private void handleOwnerOrderReport() {
		VBox menu = new VBox();
		Text topTxt = new Text("Choose a year and a month of orders:");
		ChoiceBox<String> shopBox = new ChoiceBox<String>();
		ChoiceBox<String> yearBox = new ChoiceBox<String>();
		ChoiceBox<String> monthBox = new ChoiceBox<String>();
		yearBox.setDisable(true);
		monthBox.setDisable(true);
		final CategoryAxis xAxis = new CategoryAxis(); // tzir x
		final NumberAxis yAxis = new NumberAxis(); // tzir y
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
		ObservableList<String> shopList = FXCollections.observableArrayList(Shop.HAIFA.name(), Shop.NAHARIYA.name());
		shopBox.setItems(shopList);
		shopBox.setOnAction((event) -> {
			yearBox.valueProperty().set(null);
			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.layout();
			bc.getData().clear();
			bc.setTitle(null);
			yearBox.setDisable(false);
			monthBox.setDisable(true);
		});
		ObservableList<String> yearList = FXCollections.observableArrayList("2020", "2021", "2022");
		yearBox.setItems(yearList);
		yearBox.setOnAction((event) -> {
			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.layout();
			bc.getData().clear();
			bc.setTitle(null);
			monthBox.setDisable(false);
		});
		ObservableList<String> list = FXCollections.observableArrayList("January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November", "December");
		monthBox.setItems(list);
		monthBox.setOnAction((event) -> {
			String shopName, year, month;
			bc.getData().clear();
			bc.layout();
			shopName = shopBox.getSelectionModel().getSelectedItem().toString();
			year = yearBox.getSelectionModel().getSelectedItem().toString();
			month = monthBox.getSelectionModel().getSelectedItem().toString();

			xAxis.setLabel("Products");
			yAxis.setLabel("Items sold");
			OrderReport myOrderReport;
			if (shopName.equals(Shop.NAHARIYA.name()))
				myOrderReport = model.getUserManager().getOrderReport(Shop.NAHARIYA, year, month);
			else
				myOrderReport = model.getUserManager().getOrderReport(Shop.HAIFA, year, month);

			if (myOrderReport.shop == null) {
				bc.setTitle("NO ORDER REPORT FOR THAT YEAR AND MONTH");

			} else {
				bc.setTitle("Total number of order are: " + myOrderReport.TON);

				XYChart.Series series1 = new XYChart.Series();
				series1.getData().add(new XYChart.Data("Field_Beauty", Integer.parseInt(myOrderReport.Field_Beauty)));
				series1.setName("Field_Beauty " + myOrderReport.Field_Beauty);

				XYChart.Series series2 = new XYChart.Series();
				series2.getData().add(new XYChart.Data("Warm_White", Integer.parseInt(myOrderReport.Warm_White)));
				series2.setName("Warm_White " + myOrderReport.Warm_White);

				XYChart.Series series3 = new XYChart.Series();
				series3.getData().add(new XYChart.Data("Pink_Spring", Integer.parseInt(myOrderReport.Pink_Spring)));
				series3.setName("Pink_Spring " + myOrderReport.Pink_Spring);

				XYChart.Series series4 = new XYChart.Series();
				series4.getData().add(new XYChart.Data("Cute_Ball", Integer.parseInt(myOrderReport.Cute_Ball)));
				series4.setName("Cute_Ball " + myOrderReport.Cute_Ball);

				XYChart.Series series5 = new XYChart.Series();
				series5.getData().add(new XYChart.Data("High_Ground", Integer.parseInt(myOrderReport.High_Ground)));
				series5.setName("High_Ground " + myOrderReport.High_Ground);

				XYChart.Series series6 = new XYChart.Series();
				series6.getData().add(new XYChart.Data("With_Love", Integer.parseInt(myOrderReport.With_Love)));
				series6.setName("With_Love " + myOrderReport.With_Love);

				XYChart.Series series7 = new XYChart.Series();
				series7.getData().add(new XYChart.Data("Happy_moments", Integer.parseInt(myOrderReport.Happy_moments)));
				series7.setName("Happy_moments " + myOrderReport.Happy_moments);

				XYChart.Series series8 = new XYChart.Series();
				series8.getData().add(new XYChart.Data("Memories", Integer.parseInt(myOrderReport.Memories)));
				series8.setName("Memories " + myOrderReport.Memories);

				XYChart.Series series9 = new XYChart.Series();
				series9.getData().add(new XYChart.Data("Pink_Orchid", Integer.parseInt(myOrderReport.Pink_Orchid)));
				series9.setName("Pink_Orchid " + myOrderReport.Pink_Orchid);

				XYChart.Series series10 = new XYChart.Series();
				series10.getData().add(new XYChart.Data("White_Rose", Integer.parseInt(myOrderReport.White_Rose)));
				series10.setName("White_Rose " + myOrderReport.White_Rose);

				XYChart.Series series11 = new XYChart.Series();
				series11.getData().add(new XYChart.Data("Red_Rose", Integer.parseInt(myOrderReport.Red_Rose)));
				series11.setName("Red_Rose " + myOrderReport.Red_Rose);

				bc.getData().addAll(series1, series2, series3, series4, series5, series6, series7, series8, series9,
						series10, series11);
			}
		});

		menu.getChildren().add(topTxt);
		menu.getChildren().add(shopBox);
		menu.getChildren().add(yearBox);
		menu.getChildren().add(monthBox);
		menu.getChildren().add(bc);
		content.getChildren().add(menu);
	}

	/**
	 *
	 * Sets the content to the order report from the sql
	 * 
	 * @return HBox hbox
	 * @author Yohan, Niv
	 */
	private void handleManagerOrderReport() {
		VBox menu = new VBox();
		Text topTxt = new Text("Choose a year and a month of orders:");

		ChoiceBox<String> yearBox = new ChoiceBox<String>();
		ChoiceBox<String> monthBox = new ChoiceBox<String>();
		monthBox.setDisable(true);
		final CategoryAxis xAxis = new CategoryAxis(); // tzir x
		final NumberAxis yAxis = new NumberAxis(); // tzir y
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
		ObservableList<String> yearList = FXCollections.observableArrayList("2020", "2021", "2022");

		yearBox.setItems(yearList);
		yearBox.setOnAction((event) -> {
			monthBox.valueProperty().set(null);
			bc.getData().clear();
			bc.layout();
			bc.getData().clear();
			bc.setTitle(null);
			monthBox.setDisable(false);
		});

		ObservableList<String> list = FXCollections.observableArrayList("January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November", "December");
		monthBox.setItems(list);

		monthBox.setOnAction((event) -> {
			String shopName, year, month;
			bc.getData().clear();
			bc.layout();
			shopName = model.getUserManager().getCurrentUser().shopname.toString();
			year = yearBox.getSelectionModel().getSelectedItem().toString();
			month = monthBox.getSelectionModel().getSelectedItem().toString();

			xAxis.setLabel("Products");
			yAxis.setLabel("Items sold");

			OrderReport myOrderReport = model.getUserManager()
					.getOrderReport(model.getUserManager().getCurrentUser().shopname, year, month);
			if (myOrderReport.shop == null) {
				bc.setTitle("NO ORDER REPORT FOR THAT YEAR AND MONTH");

			} else {
				bc.setTitle("Total number of order are: " + myOrderReport.TON);

				XYChart.Series series1 = new XYChart.Series();
				series1.getData().add(new XYChart.Data("Field_Beauty", Integer.parseInt(myOrderReport.Field_Beauty)));
				series1.setName("Field_Beauty " + myOrderReport.Field_Beauty);

				XYChart.Series series2 = new XYChart.Series();
				series2.getData().add(new XYChart.Data("Warm_White", Integer.parseInt(myOrderReport.Warm_White)));
				series2.setName("Warm_White " + myOrderReport.Warm_White);

				XYChart.Series series3 = new XYChart.Series();
				series3.getData().add(new XYChart.Data("Pink_Spring", Integer.parseInt(myOrderReport.Pink_Spring)));
				series3.setName("Pink_Spring " + myOrderReport.Pink_Spring);

				XYChart.Series series4 = new XYChart.Series();
				series4.getData().add(new XYChart.Data("Cute_Ball", Integer.parseInt(myOrderReport.Cute_Ball)));
				series4.setName("Cute_Ball " + myOrderReport.Cute_Ball);

				XYChart.Series series5 = new XYChart.Series();
				series5.getData().add(new XYChart.Data("High_Ground", Integer.parseInt(myOrderReport.High_Ground)));
				series5.setName("High_Ground " + myOrderReport.High_Ground);

				XYChart.Series series6 = new XYChart.Series();
				series6.getData().add(new XYChart.Data("With_Love", Integer.parseInt(myOrderReport.With_Love)));
				series6.setName("With_Love " + myOrderReport.With_Love);

				XYChart.Series series7 = new XYChart.Series();
				series7.getData().add(new XYChart.Data("Happy_moments", Integer.parseInt(myOrderReport.Happy_moments)));
				series7.setName("Happy_moments " + myOrderReport.Happy_moments);

				XYChart.Series series8 = new XYChart.Series();
				series8.getData().add(new XYChart.Data("Memories", Integer.parseInt(myOrderReport.Memories)));
				series8.setName("Memories " + myOrderReport.Memories);

				XYChart.Series series9 = new XYChart.Series();
				series9.getData().add(new XYChart.Data("Pink_Orchid", Integer.parseInt(myOrderReport.Pink_Orchid)));
				series9.setName("Pink_Orchid " + myOrderReport.Pink_Orchid);

				XYChart.Series series10 = new XYChart.Series();
				series10.getData().add(new XYChart.Data("White_Rose", Integer.parseInt(myOrderReport.White_Rose)));
				series10.setName("White_Rose " + myOrderReport.White_Rose);

				XYChart.Series series11 = new XYChart.Series();
				series11.getData().add(new XYChart.Data("Red_Rose", Integer.parseInt(myOrderReport.Red_Rose)));
				series11.setName("Red_Rose " + myOrderReport.Red_Rose);

				bc.getData().addAll(series1, series2, series3, series4, series5, series6, series7, series8, series9,
						series10, series11);
			}
		});

		menu.getChildren().add(topTxt);
		menu.getChildren().add(yearBox);
		menu.getChildren().add(monthBox);
		menu.getChildren().add(bc);
		content.getChildren().add(menu);

	}

	/**
	 *
	 * Sets the content to the register page for the manager
	 * 
	 * @author Yohan, Niv
	 */
	private void handleAccountSetting() {

		// setUp
		HBox twoCol = new HBox();
		VBox leftMenu = new VBox();
		VBox rightMenu = new VBox();
		HBox buttonBoxCustomer = new HBox();
		HBox buttonBoxWorker = new HBox();
		TableView customersView = new TableView();
		TableView workersView = new TableView();
		Button approveBtn = new Button("Approve");
		Button blockBtn = new Button("Block");
		Button freezeBtn = new Button("Freeze");
		Button yesBtn = new Button("YES");
		Button noBtn = new Button("NO");
		Text errorMsgUsers = new Text("");
		Text errorMsgWorkers = new Text("");
		ObservableList<User> customerList = FXCollections.observableArrayList();
		ObservableList<User> workerList = FXCollections.observableArrayList();

		// Button onActions
		yesBtn.setOnAction(new EventHandler() {

			@Override
			public void handle(Event arg0) {
				if (workersView.getSelectionModel().getSelectedItem() == null)
					errorMsgWorkers.setText("YOU MUST PICK A ROW!!");
				else {
					User user = (User) workersView.getSelectionModel().getSelectedItem();
					if (user.cardNumber.equals("1"))
						errorMsgWorkers.setText("WORKER ALREADY APPROVED!!");
					else {

						model.getUserManager().changeStatus(user);
						setTableViewForAccuntSettings2(workersView, workerList);

						errorMsgWorkers.setText("USER STATUS CHANGED");
					}
				}
			}

		});

		noBtn.setOnAction(new EventHandler() {

			@Override
			public void handle(Event arg0) {
				if (workersView.getSelectionModel().getSelectedItem() == null)
					errorMsgWorkers.setText("YOU MUST PICK A ROW!!");
				else {
					User user = (User) workersView.getSelectionModel().getSelectedItem();
					if (user.cardNumber.equals("0"))
						errorMsgWorkers.setText("WORKER ALREADY APPROVED!!");
					else {

						model.getUserManager().changeStatus(user);
						setTableViewForAccuntSettings2(workersView, workerList);
						errorMsgWorkers.setText("USER STATUS CHANGED");
					}
				}
			}

		});

		approveBtn.setOnAction(new EventHandler() {

			@Override
			public void handle(Event arg0) {
				if (customersView.getSelectionModel().getSelectedItem() == null)
					errorMsgUsers.setText("YOU MUST PICK A ROW!!");
				else {
					User user = (User) customersView.getSelectionModel().getSelectedItem();
					if (user.accountStatus.equals("Approved"))
						errorMsgUsers.setText("USER ALREADY APPROVED!!");
					else {
						user.accountStatus = "Approved";// In ServerUserManager he will change the account to the
														// account status
						user.userrole = Role.CUSTOMER;
						model.getUserManager().changeStatus(user);
						setTableViewForAccuntSettings(customersView, customerList);
						errorMsgUsers.setText("USER STATUS CHANGED");
					}
				}
			}

		});

		blockBtn.setOnAction(new EventHandler() {

			@Override
			public void handle(Event arg0) {
				if (customersView.getSelectionModel().getSelectedItem() == null)
					errorMsgUsers.setText("YOU MUST PICK A ROW!!");
				else {
					User user = (User) customersView.getSelectionModel().getSelectedItem();
					if (user.accountStatus.equals("Blocked"))
						errorMsgUsers.setText("USER ALREADY BLOCKED!!");
					else {
						user.accountStatus = "Blocked";// In ServerUserManager he will change the account to the account
														// status
						user.userrole = Role.CUSTOMER;
						model.getUserManager().changeStatus(user);
						setTableViewForAccuntSettings(customersView, customerList);
						errorMsgUsers.setText("USER STATUS CHANGED");
					}
				}
			}

		});

		freezeBtn.setOnAction(new EventHandler() {

			@Override
			public void handle(Event arg0) {
				if (customersView.getSelectionModel().getSelectedItem() == null)
					errorMsgUsers.setText("YOU MUST PICK A ROW!!");
				else {
					User user = (User) customersView.getSelectionModel().getSelectedItem();
					if (user.accountStatus.equals("Frozen"))
						errorMsgUsers.setText("USER ALREADY FROZEN!!");
					else {
						user.accountStatus = "Frozen";// In ServerUserManager he will change the account to the account
														// status
						user.userrole = Role.CUSTOMER;
						model.getUserManager().changeStatus(user);
						setTableViewForAccuntSettings(customersView, customerList);
						errorMsgUsers.setText("USER STATUS CHANGED");
					}
				}
			}

		});

		// TableView
		setTableViewForAccuntSettings(customersView, customerList);
		setTableViewForAccuntSettings2(workersView, workerList);

		buttonBoxCustomer.getChildren().addAll(approveBtn, blockBtn, freezeBtn);
		buttonBoxWorker.getChildren().addAll(yesBtn, noBtn);
		leftMenu.getChildren().addAll(customersView, errorMsgUsers, buttonBoxCustomer);
		rightMenu.getChildren().addAll(workersView, errorMsgWorkers, buttonBoxWorker);
		accountSettingsCSS(buttonBoxCustomer, leftMenu);
		accountSettingsCSS(buttonBoxWorker, rightMenu);
		twoCol.getChildren().addAll(leftMenu, rightMenu);
		content.getChildren().addAll(twoCol);

	}

	public void accountSettingsCSS(HBox buttons, VBox menu) {
		buttons.setPadding(new Insets(20, 10, 20, 10));
		buttons.setSpacing(12);
		menu.setPadding(new Insets(10, 20, 20, 10));
		menu.setAlignment(Pos.CENTER);
	}

	/**
	 *
	 * Sets the table view of the customers
	 * 
	 * @param TableView            usersView
	 * @param ObservableList<User> customerList
	 * @author Yohan, Niv
	 */
	private void setTableViewForAccuntSettings(TableView usersView, ObservableList<User> customerList) {
		usersView.getItems().clear();
		usersView.getColumns().clear();
		UsersList allUsersList = model.getUserManager().getUsersForManager();

		TableColumn userNameColumn = new TableColumn("UserName");
		userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		TableColumn nickNameColumn = new TableColumn("NickName");
		nickNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nickname"));
		TableColumn StatusColumn = new TableColumn("Status");
		StatusColumn.setCellValueFactory(new PropertyValueFactory<User, String>("accountStatus"));
		usersView.getColumns().addAll(userNameColumn, nickNameColumn, StatusColumn);
		for (User u : allUsersList.Users) {
			if ((u.userrole.equals(Role.CUSTOMER) || u.userrole.equals(Role.GUEST)) && (u.cardNumber.length() == 12)
					&& u.shopname.equals(model.getUserManager().getCurrentUser().shopname))
				customerList.add(u);
		}
		usersView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		usersView.setItems(customerList);

	}

	/**
	 *
	 * Sets the table view of the workers
	 * 
	 * @param TableView            usersView
	 * @param ObservableList<User> customerList
	 * @author Yohan, Niv
	 */
	private void setTableViewForAccuntSettings2(TableView usersView, ObservableList<User> customerList) {
		usersView.getItems().clear();
		usersView.getColumns().clear();
		UsersList allUsersList = model.getUserManager().getUsersForManager();

		TableColumn userNameColumn = new TableColumn("UserName");
		userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		TableColumn nickNameColumn = new TableColumn("NickName");
		nickNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nickname"));
		TableColumn StatusColumn = new TableColumn("Can change catalogue");
		StatusColumn.setCellValueFactory(new PropertyValueFactory<User, String>("accountStatus"));
		usersView.getColumns().addAll(userNameColumn, nickNameColumn, StatusColumn);
		for (User u : allUsersList.Users) {
			if (u.userrole.equals(Role.WORKER) && !u.cardNumber.equals("null")
					&& u.shopname.equals(model.getUserManager().getCurrentUser().shopname)) {
				if (u.cardNumber.equals("1")) {
					u.accountStatus = "YES";
				} else {
					u.accountStatus = "NO";
				}
				System.out.println("1083 AccountMain controller " + u.cardNumber);
				customerList.add(u);
			}
		}
		usersView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		usersView.setItems(customerList);

	}

	/**
	 * builds the " add new complaint" user screen
	 *
	 * Selects an order to complain about and must fill out a complaint up to 255
	 * Characters..
	 *
	 * 
	 * @return Vbox that represents the "add new complaint screen"
	 * @author Jessica and Yarden
	 */
	public VBox complaints() {
		VBox vbox = new VBox();
		HBox idHbox = new HBox();
		HBox orderHbox = new HBox();
		HBox complaintHbox = new HBox();
		Label infoLabel = new Label();
		Label errorLabel = new Label();
		Label idLabel = new Label("Enter customer id ");
		Label ordersLabel = new Label("Choose order");
		TextField idTextField = new TextField("");
		ChoiceBox<String> ordersChoiceBox = new ChoiceBox<String>();
		Button searchIdBtn = new Button("search");
		TextArea complaintAreaText = new TextArea("");
		complaintAreaText.setPromptText("Fill customer complaint - up to 255 chars!");
		Button sendComplaintToDBBtn = new Button("send");
		OrderList orderList = new OrderList();
		orderList.orders = new ArrayList<Order>();
		// Initialize => Disable all button except the name search
		ordersChoiceBox.setDisable(true);
		complaintAreaText.setDisable(true);
		sendComplaintToDBBtn.setDisable(true);
		searchIdBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String username = idTextField.getText();
				ordersChoiceBox.getItems().clear();
				if (username == null || username.equals(""))
					errorLabel.setText("You must enter customer id!"); // NEED TO CHECK IF THIS USER REALLY EXSISTED
				else {
					ordersChoiceBox.setDisable(false);
					OrderList list = new OrderList();
					list.username = username;
					list = model.getOrderManager().getOrders(list.username);
					if (list == null || list.orders.size() == 0)
						errorLabel.setText("No orders for " + username + "\r\nor user doesn't exists");
					else
						errorLabel.setText("");
					for (Order o : list.orders) {
						ordersChoiceBox.getItems().add(o.orderNumber);
						orderList.orders.add(o);
					}
				}
			}
		});
		ordersChoiceBox.setOnAction((event) -> {
			complaintAreaText.setDisable(false);
			sendComplaintToDBBtn.setDisable(false);
		});
		orderHbox.getChildren().addAll(ordersLabel, ordersChoiceBox);
		idHbox.getChildren().addAll(idLabel, idTextField, searchIdBtn);
		sendComplaintToDBBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				String username = idTextField.getText();
				Double price = 0.0;
				String orderId = "";
				for (Order o : orderList.orders) {
					if (o.orderNumber.equals(ordersChoiceBox.getValue())) {
						price = o.totalPrice;
						orderId = o.orderNumber;
						break;
					}
				}

				if (complaintAreaText.getText().equals("")) {
					infoLabel.setText("You must fill the complaint!");
					return;
				} else if ((complaintAreaText.getText().length() > 255)) {
					infoLabel.setText("You can write only 255 chars");
					return;
				}
				ComplaintList complaintList = model.getUserManager().getAllComplaints();
				boolean alreadyExistsComplaint = false;
				for (Complaint c : complaintList.complaints) {
					if (c.orderId.equals(ordersChoiceBox.getValue())) {
						infoLabel.setText("There already a complaint to this order, try with another order");
						alreadyExistsComplaint = true;
						break;
					}
				}
				if (!alreadyExistsComplaint) {
					if (model.getUserManager().addNewCompliant(username, orderId, complaintAreaText.getText(),
							dtf.format(now), price.toString(), "Awaiting response", "0",
							model.getUserManager().getCurrentUser().shopname,
							model.getUserManager().getCurrentUser().username))
						infoLabel.setText(username + " complaint was successfully added");
				}
			}
		});
		complaintsCss(idHbox, vbox, complaintHbox, orderHbox);
		// ordersList.removeAll(ordersList);
		vbox.getChildren().addAll(errorLabel, idHbox, orderHbox, complaintHbox, complaintAreaText, sendComplaintToDBBtn,
				infoLabel);
		return vbox;
	}

	/**
	 * design the "add new complaint" window
	 *
	 * Set high and width Preferences
	 *
	 * @author Jessica
	 */
	public void complaintsCss(HBox idHbox, VBox menu, HBox complaintHbox, HBox orderHbox) {
		idHbox.setPadding(new Insets(20, 10, 20, 10));
		// ordersChoiceBox.setPadding(new Insets(3, 2, 0, 60));
		// complaintHbox.getChildren().add(ordersChoiceBox);
		menu.setPadding(new Insets(2, 10, 20, 10));
		menu.setAlignment(Pos.TOP_CENTER);
		idHbox.setAlignment(Pos.TOP_CENTER);
		complaintHbox.setPadding(new Insets(1, 10, 10, 10));
		complaintHbox.setAlignment(Pos.TOP_CENTER);
	}

	/**
	 * builds the " handling complaint" user screen
	 *
	 * the user Receives notification of complaints that have not been handled for
	 * more than 24 hours The user chooses which and how to handle the complaint and
	 * which refund is needed A table is obtained with all the complaints that the
	 * user has opened
	 *
	 * 
	 * @return Vbox that represents the "handling complaint screen"
	 * @author Jessica and Yarden
	 */
	public VBox ComplaintHandling() {
		TableView complaintView = new TableView();
		Label infoLabel = new Label("");
		List<String> tmpList = new ArrayList<>();
		tmpList = setTableView(complaintView); // setting table view
		if (tmpList.isEmpty())
			infoLabel.setText("The list is empty!");

		else
			JOptionPane.showMessageDialog(null, "please take care of this complaints:" + tmpList.toString(),
					"complaint Notification", JOptionPane.PLAIN_MESSAGE);
		VBox vbox = new VBox();
		VBox refundVbox = new VBox();
		HBox hbox = new HBox();
		HBox refundHBox = new HBox();
		HBox orderHBox = new HBox();
		Label refundLabel = new Label("Refund: ");
		Label orderLabel = new Label("Order id: ");
		Button sendBtn = new Button("send to DB");
		sendBtn.setDisable(true);
		ObservableList<String> list = FXCollections.observableArrayList("0", "30%", "50%", "100%");
		ChoiceBox<String> refundBox = new ChoiceBox<String>();
		refundBox.setItems(list);
		refundBox.setValue("chose your % refund");
		Label orderIdLabel = new Label("Please, press order id");
		Label refundStatusLabel = new Label("");
		Label complaintStatusLabel = new Label("");
		refundBox.setDisable(true);

		complaintView.setOnMousePressed((new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				Complaint complaint = (Complaint) (complaintView.getSelectionModel().getSelectedItem());
				if (complaint == null)
					orderIdLabel.setText("You must press the order id column");
				else {
					orderIdLabel.setText(complaint.getOrderId());
					complaintStatusLabel.setText(complaint.getUserName() + " complaint was chosen");
				}
				refundBox.setDisable(false);
			}
		}));
		refundBox.setOnAction((event) -> {
			sendBtn.setDisable(false);
		});
		sendBtn.setOnAction((event) -> {
			double priceAfterRefund = 0;
			Complaint complaint = (Complaint) (complaintView.getSelectionModel().getSelectedItem());
			if (complaint.getComplaintStatus().equals("Awaiting response")) {
				String refundChoice = refundBox.getValue();
				switch (refundChoice) {
				case "0":
					priceAfterRefund = 0;
					break;
				case "30%":
					priceAfterRefund = 0.3 * Double.parseDouble(complaint.getPrice());
					break;
				case "50%":
					priceAfterRefund = 0.5 * Double.parseDouble(complaint.getPrice());
					break;
				case "100%":
					priceAfterRefund = Double.parseDouble(complaint.getPrice());
					break;
				default:
					break;
				}
				complaintStatusLabel.setText("The complaint was handled!");
				model.getUserManager().setRefundAmount(complaint.getOrderId(), String.valueOf(priceAfterRefund));
				model.getUserManager().setRefundAmount(complaint.userName, String.valueOf(priceAfterRefund));
				setTableView(complaintView);
			}
		});
		refundHBox.getChildren().addAll(refundLabel, refundBox);
		orderHBox.getChildren().addAll(orderLabel, orderIdLabel);
		refundVbox.getChildren().addAll(orderHBox, refundHBox, sendBtn);
		refundVbox.setPadding(new Insets(20, 10, 20, 20));
		refundHBox.setAlignment(Pos.TOP_CENTER);
		orderHBox.setAlignment(Pos.TOP_CENTER);
		refundVbox.setAlignment(Pos.TOP_CENTER);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().addAll(complaintView, complaintStatusLabel, hbox, refundVbox, refundStatusLabel);
		return vbox;
	}

	/**
	 *
	 * Sets the table view of the complaints
	 * 
	 * @param TableView complaintView
	 * @author Jessica, Yarden
	 */
	public List<String> setTableView(TableView complaintView) {
		ObservableList<Complaint> complaintsList = FXCollections.observableArrayList();
		complaintView.getItems().clear();
		complaintView.getColumns().clear();
		ComplaintList listAllComplaints = model.getUserManager().getAllComplaints();
		complaintView.setEditable(true);
		TableColumn userNameColumn = new TableColumn("User name");
		userNameColumn.setCellValueFactory(new PropertyValueFactory<Complaint, String>("userName"));
		TableColumn orderIdColumn = new TableColumn("Order id");
		orderIdColumn.setCellValueFactory(new PropertyValueFactory<Complaint, String>("orderId"));
		TableColumn complaintColumn = new TableColumn("Complaint");
		complaintColumn.setCellValueFactory(new PropertyValueFactory<Complaint, String>("complaint"));
		TableColumn dateColumn = new TableColumn("Date");
		dateColumn.setCellValueFactory(new PropertyValueFactory<Complaint, String>("date"));
		TableColumn priceColumn = new TableColumn("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<Complaint, String>("price"));
		TableColumn complaintStatusColumn = new TableColumn("complaintStauts");
		complaintStatusColumn.setCellValueFactory(new PropertyValueFactory<Complaint, String>("complaintStatus"));
		TableColumn refundAmountColumn = new TableColumn("refund");
		refundAmountColumn.setCellValueFactory(new PropertyValueFactory<Complaint, String>("refund"));
		complaintView.getColumns().addAll(userNameColumn, orderIdColumn, complaintColumn, dateColumn, priceColumn,
				complaintStatusColumn, refundAmountColumn);
		List<String> complaintNotification = new ArrayList<>();
		for (Complaint c : listAllComplaints.complaints) {
			{
				if (c.supportName.equals(model.getUserManager().getCurrentUser().username)
						&& c.getComplaintStatus().equals("Awaiting response")) {
					complaintsList.add(c);
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String saveTime = c.getDate();
					Calendar cal = Calendar.getInstance();
					try {
						cal.setTime(df.parse(saveTime));
						cal.add(Calendar.DAY_OF_MONTH, 1);
						if (System.currentTimeMillis() >= cal.getTimeInMillis()) {
							complaintNotification.add(c.getUserName() + " " + c.getOrderId());
							complaintNotification.add(System.getProperty("line.separator"));
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		complaintView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		complaintView.setItems(complaintsList);
		complaintNotification.add(" need to be handled less then 24 hours ago! ");
		return (complaintNotification);
	}

	/**
	 *
	 * Creates new buffer to send file to sql
	 * 
	 * @author Jessica, Yarden
	 */
	public boolean sendFile() {
		AnalyseFileContainer analyseFileContainer = new AnalyseFileContainer();
		analyseFileContainer.analyseFile.fileName = path;
		try {
			File pdf = new File(path);
			byte[] pdfByte = new byte[(int) pdf.length()];
			FileInputStream fis = new FileInputStream(pdf);
			BufferedInputStream bis = new BufferedInputStream(fis);
			analyseFileContainer.initArray(pdfByte.length);
			analyseFileContainer.analyseFile.size = pdfByte.length;
			analyseFileContainer.analyseFile.surveyAnalyseId = surveyIdFlag;
			bis.read(analyseFileContainer.analyseFile.byteArray, 0, pdfByte.length);
			model.getUserManager().analyseTypeSurvey(analyseFileContainer.analyseFile);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * builds the "insert survey" user screen
	 *
	 * the user have to answer to 6 questions and choose a survey to answer
	 * about,only then the survey will be saved to the DB
	 * 
	 * @return Vbox that represents the "insert survey screen screen"
	 * @author Jessica and Yarden
	 */
	private VBox insertSurveys() {
		VBox choiceboxVbox = new VBox();
		VBox questionsVBox = new VBox();
		HBox questionsHBox = new HBox();
		Button saveBtn = new Button("send");
		Label surveyLabel = new Label("Insert surveys answers");
		Label errorLabel = new Label("");
		Label infoLabel = new Label("Choose your survey type");
		Label numberq1 = new Label("1.");
		Label numberq2 = new Label("2.");
		Label numberq3 = new Label("3.");
		Label numberq4 = new Label("4.");
		Label numberq5 = new Label("5.");
		Label numberq6 = new Label("6.");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM");
		LocalDateTime now = LocalDateTime.now();
		// set choices box
		ComboBox<String> surveyTypesCB = new ComboBox<String>();
		ComboBox<String> generalComboBox = new ComboBox<String>();
		ChoiceBox<Integer> q1ChoiceBox = new ChoiceBox<Integer>();
		ChoiceBox<Integer> q2ChoiceBox = new ChoiceBox<Integer>();
		ChoiceBox<Integer> q3ChoiceBox = new ChoiceBox<Integer>();
		ChoiceBox<Integer> q4ChoiceBox = new ChoiceBox<Integer>();
		ChoiceBox<Integer> q5ChoiceBox = new ChoiceBox<Integer>();
		ChoiceBox<Integer> q6ChoiceBox = new ChoiceBox<Integer>();
		surveyTypesCB.getItems().addAll("Monthly survey", "Sales survey", "Shop survey");
		q1ChoiceBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		q2ChoiceBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		q3ChoiceBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		q4ChoiceBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		q5ChoiceBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		q6ChoiceBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		// set the choices box
		generalComboBox.setDisable(true);
		q1ChoiceBox.setDisable(true);
		q2ChoiceBox.setDisable(true);
		q3ChoiceBox.setDisable(true);
		q4ChoiceBox.setDisable(true);
		q5ChoiceBox.setDisable(true);
		q6ChoiceBox.setDisable(true);
		surveyTypesCB.setOnAction((event) -> {
			generalComboBox.getItems().clear();
			switch (surveyTypesCB.getValue()) {
			case "Monthly survey": {
				errorLabel.setText("");
				q1ChoiceBox.valueProperty().set(null);
				q2ChoiceBox.valueProperty().set(null);
				q3ChoiceBox.valueProperty().set(null);
				q4ChoiceBox.valueProperty().set(null);
				q5ChoiceBox.valueProperty().set(null);
				q6ChoiceBox.valueProperty().set(null);
				generalComboBox.setDisable(false);
				generalComboBox.getItems().addAll("06 (Juny)");
				generalComboBox.valueProperty().set("06 (Juny)");
				surveyIdFlag = "1";
				// generalComboBox.valueProperty().set(null);
				errorLabel.setText("");
				break;
			}
			case "Sales survey": {
				errorLabel.setText("");
				q1ChoiceBox.valueProperty().set(null);
				q2ChoiceBox.valueProperty().set(null);
				q3ChoiceBox.valueProperty().set(null);
				q4ChoiceBox.valueProperty().set(null);
				q5ChoiceBox.valueProperty().set(null);
				q6ChoiceBox.valueProperty().set(null);
				generalComboBox.setDisable(false);
				generalComboBox.getItems().addAll("20%");
				generalComboBox.valueProperty().set("20%");
				surveyIdFlag = "2";
				// generalComboBox.valueProperty().set(null);
				errorLabel.setText("");
				break;
			}
			case "Shop survey": {
				errorLabel.setText("");
				// generalComboBox.getItems().clear();
				q1ChoiceBox.valueProperty().set(null);
				q2ChoiceBox.valueProperty().set(null);
				q3ChoiceBox.valueProperty().set(null);
				q4ChoiceBox.valueProperty().set(null);
				q5ChoiceBox.valueProperty().set(null);
				q6ChoiceBox.valueProperty().set(null);
				generalComboBox.setDisable(false);
				generalComboBox.getItems().addAll("HAIFA", "NAHARIYA");
				generalComboBox.valueProperty().set("HAIFA");
				errorLabel.setText("");
			}
			default:
				break;
			}
		});
		generalComboBox.setOnAction((event) -> {
			shopTypeCB = generalComboBox.getValue();
			errorLabel.setText("");
			q1ChoiceBox.setDisable(false);
			q2ChoiceBox.setDisable(false);
			q3ChoiceBox.setDisable(false);
			q4ChoiceBox.setDisable(false);
			q5ChoiceBox.setDisable(false);
			q6ChoiceBox.setDisable(false);
			saveBtn.setDisable(false);
		});
		questionsVBox.setAlignment(Pos.TOP_CENTER);
		questionsHBox.getChildren().addAll(numberq1, q1ChoiceBox, numberq2, q2ChoiceBox, numberq3, q3ChoiceBox,
				numberq4, q4ChoiceBox, numberq5, q5ChoiceBox, numberq6, q6ChoiceBox);
		questionsVBox.getChildren().addAll(surveyLabel, questionsHBox, errorLabel, saveBtn);
		choiceboxVbox.getChildren().addAll(infoLabel, surveyTypesCB, generalComboBox, questionsVBox);
		saveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				errorLabel.setText("");
				if (surveyTypesCB.getValue() == null) {
					errorLabel.setText("You must fill all choise boxs!");
					return;
				}
				if (q1ChoiceBox.getValue() == null || q2ChoiceBox.getValue() == null || q3ChoiceBox.getValue() == null
						|| q4ChoiceBox.getValue() == null || q5ChoiceBox.getValue() == null
						|| q6ChoiceBox.getValue() == null) {
					errorLabel.setText("You must answer all the questions!");
					return;
				}
				if (surveyTypesCB.getValue().equals("Shop survey") && shopTypeCB.equals("HAIFA"))
					surveyIdFlag = "3";
				else if (surveyTypesCB.getValue().equals("Shop survey") && shopTypeCB.equals("NAHARIYA"))
					surveyIdFlag = "4";
				if (model.getUserManager().setSurveyAnswers(q1ChoiceBox.getValue(), q2ChoiceBox.getValue(),
						q3ChoiceBox.getValue(), q4ChoiceBox.getValue(), q5ChoiceBox.getValue(), q6ChoiceBox.getValue(),
						surveyTypesCB.getValue(), generalComboBox.getValue(), dtf.format(now), surveyIdFlag))
					errorLabel.setText("The survey was successfully completed");
				q1ChoiceBox.valueProperty().set(null);
				q2ChoiceBox.valueProperty().set(null);
				q3ChoiceBox.valueProperty().set(null);
				q4ChoiceBox.valueProperty().set(null);
				q5ChoiceBox.valueProperty().set(null);
				q6ChoiceBox.valueProperty().set(null);
			}
		});
		errorLabel.setText("");
		infoLabel.setAlignment(Pos.TOP_CENTER);
		questionsVBox.setAlignment(Pos.TOP_CENTER);
		return choiceboxVbox;
	}

	/**
	 * builds the "insert survey" user screen
	 *
	 * the user have to answer to 6 questions and choose a survey to answer
	 * about,only then the survey will be saved to the DB
	 *
	 * 
	 * @return Vbox that represents the "insert survey screen screen"
	 * @author Jessica and Yarden
	 */
	public VBox SurveysHandling() {
		VBox surveyVbox = new VBox();
		HBox syurveyHbox = new HBox();
		Button uploadeAnalyse = new Button("Chose pdf file to uploade");
		Label surveyMessageLabel = new Label("");
		// System.out.println(dtf.format(now));
		ObservableList<String> surveyList = FXCollections.observableArrayList("Monthly survey", "Sales survey",
				"Shop survey");
		ChoiceBox<String> surveyTypeCB = new ChoiceBox<>();
		ComboBox<String> generalComboBox = new ComboBox<String>();
		surveyTypeCB.setItems(surveyList);
		surveyTypeCB.setValue("Choose survey type to analyse");
		Button analyseBtn = new Button("analyse");
		surveyVbox.getChildren().addAll(surveyTypeCB, generalComboBox, uploadeAnalyse, analyseBtn);
		generalComboBox.setDisable(true);
		analyseBtn.setDisable(true);
		surveyTypeCB.setOnAction((event) -> {
			switch (surveyTypeCB.getValue()) {
			case "Monthly survey": {
				generalComboBox.setDisable(false);
				generalComboBox.getItems().addAll("06 (Juny)");
				surveyIdFlag = "1";
				generalComboBox.valueProperty().set(null);
				surveyMessageLabel.setText("");
				break;
			}
			case "Sales survey": {
				generalComboBox.setDisable(false);
				generalComboBox.getItems().addAll("20%");
				surveyIdFlag = "2";
				generalComboBox.valueProperty().set(null);
				surveyMessageLabel.setText("");
				break;
			}
			case "Shop survey": {
				generalComboBox.setDisable(false);
				generalComboBox.getItems().addAll("HAIFA", "NAHARIYA");
				generalComboBox.valueProperty().set(null);
				surveyMessageLabel.setText("");
			}
			default:
				generalComboBox.setDisable(true);
				break;
			}
		});
		generalComboBox.setOnAction((event) -> {
			if (surveyTypeCB.getValue().equals("Shop survey") && generalComboBox.getValue().equals("HAIFA"))
				surveyIdFlag = "3";
			else if (surveyTypeCB.getValue().equals("Shop survey") && generalComboBox.getValue().equals("NAHARIYA"))
				surveyIdFlag = "4";
		});
		uploadeAnalyse.setOnAction((event) -> {
			FileChooser fc = new FileChooser();
			selectedFile = fc.showOpenDialog(null);
			if (selectedFile == null) {
				surveyMessageLabel.setText("Please choose a file. try again");
				return;
			}
			path = selectedFile.getAbsolutePath();
			String fileName = selectedFile.getName();
			String splitWithDot[] = selectedFile.getName().split("\\.");
			if (!(splitWithDot[1].equals("pdf"))) {
				surveyMessageLabel.setText("You chose not pdf file. try again");
				return;
			}
			analyseBtn.setDisable(false);
		});
		analyseBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!(sendFile()))
					surveyMessageLabel.setText("Cannot open the file");
			}
		});
		return surveyVbox;
	}

	/**
	 *
	 * Sets update date
	 * 
	 * @param String dateString
	 * @author Yohan, Niv
	 */
	public static final LocalDate LOCAL_DATE(String dateString) {
		String[] date = dateString.split("\\s+");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(date[0], formatter);
		// localDate.getMonth();
		return localDate;
	}

	private void onOrders() {
		VBox vbox = new VBox();
		OrderList orders;

		if (model.getUserManager().getCurrentUser().userrole.equals(Role.CUSTOMER)) {
			orders = model.getOrderManager().getOrders(model.getUsername());

		} else {
			orders = model.getOrderManager().getOrdersM(model.getUserManager().getCurrentUser().shopname.name());

		}
		TableView<OrderTable> table = createNewTable(content);
		vbox.getChildren().addAll(table, getButtons(null));
		vbox.setSpacing(10);
		content.getChildren().add(vbox);

	}

	/**
	 *
	 * Sets the table view of the customers
	 * 
	 * @param GridPane content
	 * @author Yohan, Niv
	 */
	private TableView<OrderTable> createNewTable(GridPane content) {
		TableView<OrderTable> tableView = new TableView<OrderTable>();
		tableView.setMaxHeight(200);
		TableColumn<OrderTable, String> orderNumCol = new TableColumn<OrderTable, String>("Order#");
		orderNumCol.setCellValueFactory(new PropertyValueFactory<OrderTable, String>("orderNumber"));

		TableColumn<OrderTable, Double> priceNumCol = new TableColumn<OrderTable, Double>("Total Price");
		priceNumCol.setCellValueFactory(new PropertyValueFactory<OrderTable, Double>("totalPrice"));

		TableColumn<OrderTable, Double> walletCol = new TableColumn<OrderTable, Double>("Paid by wallet");
		walletCol.setCellValueFactory(new PropertyValueFactory<OrderTable, Double>("paidByWallet"));

		TableColumn<OrderTable, OrderStatus> statusCol = new TableColumn<OrderTable, OrderStatus>("Status");
		statusCol.setCellValueFactory(new PropertyValueFactory<OrderTable, OrderStatus>("status"));

		TableColumn<OrderTable, String> productsCol = new TableColumn<OrderTable, String>("Products");
		productsCol.setCellValueFactory(new PropertyValueFactory<OrderTable, String>("Products"));

		TableColumn<OrderTable, String> greetingCol = new TableColumn<OrderTable, String>("Greeting");
		greetingCol.setCellValueFactory(new PropertyValueFactory<OrderTable, String>("greetingMessage"));

		TableColumn<OrderTable, Shop> shopCol = new TableColumn<OrderTable, Shop>("Shop");
		shopCol.setCellValueFactory(new PropertyValueFactory<OrderTable, Shop>("shop"));

		TableColumn<OrderTable, OrderType> orderTypeCol = new TableColumn<OrderTable, OrderType>("Type");
		orderTypeCol.setCellValueFactory(new PropertyValueFactory<OrderTable, OrderType>("orderType"));

		TableColumn<OrderTable, String> cityCol = new TableColumn<OrderTable, String>("City");
		cityCol.setCellValueFactory(new PropertyValueFactory<OrderTable, String>("city"));

		TableColumn<OrderTable, String> addressCol = new TableColumn<OrderTable, String>("Address");
		addressCol.setCellValueFactory(new PropertyValueFactory<OrderTable, String>("address"));

		TableColumn<OrderTable, String> deliveryPhoneCol = new TableColumn<OrderTable, String>("Phone");
		deliveryPhoneCol.setCellValueFactory(new PropertyValueFactory<OrderTable, String>("phone"));

		tableView.getColumns().addAll(orderNumCol, priceNumCol, walletCol, statusCol, productsCol, greetingCol, shopCol,
				orderTypeCol, cityCol, addressCol, deliveryPhoneCol);

		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {

				VBox vbox = (VBox) content.getChildren().get(0);
				openButtons(newSelection.order, (HBox) vbox.getChildren().get(1), tableView);
			}
		});
		OrderList list;
		if (model.getUserManager().getCurrentUser().userrole.equals(Role.CUSTOMER)) {
			list = model.getOrderManager().getOrders(model.getUsername());
		} else {
			list = model.getOrderManager().getOrdersM(model.getUserManager().getCurrentUser().shopname.name());
		}

		for (Order order : list.orders) {
			OrderTable ord = new OrderTable(order);
			tableView.getItems().add(ord);

		}

		return tableView;

	}

	/**
	 *
	 * Sets the buttons for the order table for customer/manager
	 * 
	 * @param Order     order
	 * @param HBox      hbox
	 * @param TableView table
	 * @author Yohan, Niv
	 */
	private void openButtons(Order order, HBox hbox, TableView table) {
		// Customer table view of buttons
		if (model.getUserManager().getCurrentUser().userrole.equals(Role.CUSTOMER)) {
			Button cancelOrderBtn = (Button) hbox.getChildren().get(0);
			Button changeGreeting = (Button) hbox.getChildren().get(1);
			Button changeShop = (Button) hbox.getChildren().get(2);
			Button changePhone = (Button) hbox.getChildren().get(3);
			if (order != null) {
				cancelOrderBtn.setDisable(false);
				changeGreeting.setDisable(false);
				changeShop.setDisable(false);
				changePhone.setDisable(false);
				/*-------On action for cancel button-------*/
				cancelOrderBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						Stage stage = new Stage();
						dialog.initOwner(stage);
						VBox dialogVbox = new VBox(20);

						dialogVbox.getChildren().add(new Text("To cancel the order press cancel order"));
						Button cancelOrd = new Button("Cancel Order");
						cancelOrd.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						cancelOrd.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								order.status = OrderStatus.WAITING_FOR_CANCEL;
								table.refresh();

							}
						});
						Button back = new Button("Back");
						back.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						back.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								dialog.close();
							}
						});
						HBox hbox = new HBox(cancelOrd, back);
						hbox.setAlignment(Pos.CENTER);
						hbox.setSpacing(40);
						dialogVbox.getChildren().add(hbox);
						dialogVbox.setAlignment(Pos.CENTER);

						Scene dialogScene = new Scene(dialogVbox, 300, 200);
						dialog.setScene(dialogScene);
						dialog.show();

					}
				});

				/*-------On action for change shop button-------*/
				changeShop.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						Stage stage = new Stage();
						dialog.initOwner(stage);
						VBox dialogVbox = new VBox(20);
						dialogVbox.getChildren().add(new Text("Choose shop and press save"));
						ChoiceBox<Shop> choiceBox = new ChoiceBox<>();
						choiceBox.setValue(order.shop);
						choiceBox.getItems().setAll(Shop.values());
						Button saveShop = new Button("Save");
						saveShop.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");

						saveShop.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								order.shop = choiceBox.getSelectionModel().getSelectedItem();
								table.refresh();

							}
						});
						Button back = new Button("Back");
						back.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						back.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								dialog.close();
							}
						});
						HBox hbox = new HBox(saveShop, back);
						hbox.setAlignment(Pos.CENTER);
						hbox.setSpacing(40);
						dialogVbox.getChildren().addAll(choiceBox, hbox);
						dialogVbox.setAlignment(Pos.CENTER);

						Scene dialogScene = new Scene(dialogVbox, 300, 200);
						dialog.setScene(dialogScene);
						dialog.show();
					}
				});

				/*-------On action for change phone button-------*/
				changePhone.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						Stage stage = new Stage();
						dialog.initOwner(stage);
						VBox dialogVbox = new VBox(20);
						dialogVbox.getChildren().add(new Text("Enter telephone and press save"));
						TextField phone = new TextField();
						phone.setText(order.phone);
						Button save = new Button("Save");
						save.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						save.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								order.phone = phone.getText();
								table.refresh();

							}
						});
						Button back = new Button("Back");
						back.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						back.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								dialog.close();
							}
						});
						HBox hbox = new HBox(save, back);
						hbox.setAlignment(Pos.CENTER);
						hbox.setSpacing(40);
						dialogVbox.getChildren().addAll(phone, hbox);
						dialogVbox.setAlignment(Pos.CENTER);
						Scene dialogScene = new Scene(dialogVbox, 300, 200);
						dialog.setScene(dialogScene);
						dialog.show();
					}
				});

				/*-------On action for change greeting button-------*/

				changeGreeting.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						Stage stage = new Stage();
						dialog.initOwner(stage);
						VBox dialogVbox = new VBox(20);
						dialogVbox.getChildren().add(new Text("Enter new greeting and press save"));
						TextArea greeting = new TextArea();
						greeting.setText(order.greetingMessage);
						Button save = new Button("Save");
						save.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						save.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								order.greetingMessage = greeting.getText();
								table.refresh();

							}
						});
						Button back = new Button("Back");
						back.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						back.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								dialog.close();
							}
						});
						HBox hbox = new HBox(save, back);
						hbox.setAlignment(Pos.CENTER);
						hbox.setSpacing(30);
						dialogVbox.getChildren().addAll(greeting, hbox);
						dialogVbox.setAlignment(Pos.CENTER);

						Scene dialogScene = new Scene(dialogVbox, 300, 200);
						dialog.setScene(dialogScene);
						dialog.show();
					}
				});
			}

			////// table orders for manager!
		} else {
			Button cancelOrderBtn = (Button) hbox.getChildren().get(0);
			Button approveOrderBtn = (Button) hbox.getChildren().get(1);
			approveOrderBtn.setText("Approve Order");
			Button deleteOrder = (Button) hbox.getChildren().get(2);
			deleteOrder.setText("Delete Order");
			Button changePhone = (Button) hbox.getChildren().get(3);
			changePhone.setVisible(false);
			if (order != null) {

				deleteOrder.setDisable(false);
				cancelOrderBtn.setDisable(false);
				approveOrderBtn.setDisable(false);
				changePhone.setDisable(false);

				/*-------On action for cancel button-------*/
				cancelOrderBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						Stage stage = new Stage();
						dialog.initOwner(stage);
						VBox dialogVbox = new VBox(20);
						Text msg = new Text("Please add a reason for cancellation");
						TextField reasonText = new TextField();
						dialogVbox.getChildren().addAll(msg, reasonText);
						Button cancelOrd = new Button("Cancel Order");
						cancelOrd.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						cancelOrd.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (reasonText.getText().equals("")) {
									msg.setText("You nust add a reson to cancel the order");
								} else {
									order.status = OrderStatus.CANCELED;
									model.getOrderManager().updateOrder(order);
									msg.setText("Order canceled!\r\n user got " + checkRefoundAmount(order)
											+ "\r\n email sent to the customer");
									cancelOrd.setDisable(true);
									table.refresh();

								}
							}

						});
						Button back = new Button("Back");
						back.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						back.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								dialog.close();
							}
						});
						HBox hbox = new HBox(cancelOrd, back);
						hbox.setAlignment(Pos.CENTER);
						hbox.setSpacing(40);
						dialogVbox.getChildren().add(hbox);
						dialogVbox.setAlignment(Pos.CENTER);

						Scene dialogScene = new Scene(dialogVbox, 300, 200);
						dialog.setScene(dialogScene);
						dialog.show();

					}
				});

				/*-------On action for delete Order button-------*/
				deleteOrder.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						Stage stage = new Stage();
						dialog.initOwner(stage);
						VBox dialogVbox = new VBox(20);
						Text msg = new Text("Are you sure you want to delete this order?");
						dialogVbox.getChildren().add(msg);
						Button saveShop = new Button("Yes");
						saveShop.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");

						saveShop.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (order.status.equals(OrderStatus.APPROVED)
										|| order.status.equals(OrderStatus.CANCELED)) {
									model.getOrderManager().deleteOrder(order);
									msg.setText("Order removed from the DataBase");
									table.getItems().remove(table.getSelectionModel().getSelectedItem());
									table.refresh();
									saveShop.setDisable(true);
								} else {
									msg.setText(
											"You can only delete an order when:\r\nIt had beed approved or canceled");
								}

							}
						});
						Button back = new Button("Back");
						back.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						back.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								dialog.close();
							}
						});
						HBox hbox = new HBox(saveShop, back);
						hbox.setAlignment(Pos.CENTER);
						hbox.setSpacing(40);
						dialogVbox.getChildren().add(hbox);
						dialogVbox.setAlignment(Pos.CENTER);

						Scene dialogScene = new Scene(dialogVbox, 300, 200);
						dialog.setScene(dialogScene);
						dialog.show();

					}
				});

				/*-------On action for change greeting button-------*/

				approveOrderBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						Stage stage = new Stage();
						dialog.initOwner(stage);
						VBox dialogVbox = new VBox(20);
						Text msg = new Text("Dobule check if you have all the items in the store");
						dialogVbox.getChildren().add(msg);
						Button save = new Button("Save");
						save.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						save.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (order.status.equals(OrderStatus.WAITING_FOR_APPROVE)) {
									if (order.orderType.equals(OrderType.TAKE_AWAY)) {
										order.status = OrderStatus.APPROVED;
										model.getOrderManager().updateOrder(order);
										msg.setText("Order approved\r\n email sent to the customer");
										table.refresh();
										save.setDisable(true);
									} else if (order.orderType.equals(OrderType.DELIVERY)) {
										order.status = OrderStatus.ON_DELIVERY;
										model.getOrderManager().updateOrder(order);
										msg.setText("Order on delivery\r\n email sent to the customer");
										table.refresh();
										save.setDisable(true);
									}
								} else if (order.status.equals(OrderStatus.DELIVERD_ON_TIME)) {
									order.status = OrderStatus.APPROVED;
									model.getOrderManager().updateOrder(order);
									msg.setText("Order approved\r\n email sent to the customer");
									table.refresh();
									save.setDisable(true);
								} else if (order.status.equals(OrderStatus.DID_NOT_DELIVERD_ON_TIME)) {
									order.status = OrderStatus.APPROVED;
									order.totalPrice = 20;// Set refund order amount
									model.getUserManager().updateWalletR(order);
									model.getOrderManager().updateOrder(order);
									msg.setText(
											"Order approved\r\n Refunded delivery fee \r\n email sent to the customer");
									table.refresh();
									save.setDisable(true);
								} else {
									msg.setText("Order already been handeld!!!");
								}

							}

						});
						Button back = new Button("Back");
						back.setStyle("-fx-background-color: #215732;-fx-text-fill: white;");
						back.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								dialog.close();
							}
						});
						HBox hbox = new HBox(save, back);
						hbox.setAlignment(Pos.CENTER);
						hbox.setSpacing(30);
						dialogVbox.getChildren().add(hbox);
						dialogVbox.setAlignment(Pos.CENTER);

						Scene dialogScene = new Scene(dialogVbox, 300, 200);
						dialog.setScene(dialogScene);
						dialog.show();
					}
				});
			}

		}
	}

	/**
	 *
	 * Gets the button on the orders
	 * 
	 * @param Order order
	 * @author Yohan, Niv
	 */
	private HBox getButtons(Order order) {
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		Button cancelOrderBtn = new Button("Cancel order");
		Button changeGreeting = new Button("Change greeting");
		Button changeShop = new Button("Change shop");
		Button changePhone = new Button("Change phone");
		hbox.getChildren().addAll(cancelOrderBtn, changeGreeting, changeShop, changePhone);
		cancelOrderBtn.setDisable(true);
		changeGreeting.setDisable(true);
		changeShop.setDisable(true);
		changePhone.setDisable(true);

		/*-------On action for change phone button-------*/
		return hbox;
	}

	/**
	 *
	 * Calculate the refund amount due to the worker choice
	 * 
	 * @param Order order
	 * @author Yarden, Jessica
	 */
	private String checkRefoundAmount(Order order) {
		String date = order.date;// the date to be deliverd
		String sleshDate = date.replace('-', '/');
		String time = order.hour + ":00"; // the time to be deliverd
		String second = order.timeOfOrder; // time the user made the cancel
		String first = sleshDate + " " + time;
		long difference_In_Hours = 0;
		long difference_In_Days = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date d1 = sdf.parse(second);
			Date d2 = sdf.parse(first);
			long difference_In_Time = d2.getTime() - d1.getTime();
			difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
			difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ((difference_In_Days != 0) || ((difference_In_Days == 0) && (difference_In_Hours >= 3))) { // full refund
			model.getUserManager().updateWalletR(order);
			return "FULL REFUND";
		} else if (difference_In_Hours < 3 && difference_In_Hours >= 1) {// 50% refund
			order.totalPrice = order.totalPrice / 0.5;
			model.getUserManager().updateWalletR(order);
			return "50% REFUND";

		} else
			order.totalPrice = 0.0;
		model.getUserManager().updateWalletR(order);
		return "NO REFUND";

	}

	/**
	 *
	 * Check if the delivery do it time
	 * 
	 * @param Order order
	 * @author Yohan, Niv
	 */
	private void checkOntime(Order order) {
		String date = order.date;// the date to be deliverd
		String sleshDate = date.replace('-', '/');
		String time = order.hour + ":00"; // the time to be deliverd
		String second = order.timeOfOrder; // time the user made the cancel
		String first = sleshDate + " " + time;
		long difference_In_Hours = 0;
		long difference_In_Days = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date d1 = sdf.parse(second);
			Date d2 = sdf.parse(first);
			long difference_In_Time = d2.getTime() - d1.getTime();
			difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
			difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (difference_In_Hours < 0) {
			model.getOrderManager().deleteOrder(order);
			order.status = OrderStatus.DID_NOT_DELIVERD_ON_TIME;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			order.timeOfOrder = dtf.format(now);
			model.getCartManager().submitOrder(order);
		} else {
			model.getOrderManager().deleteOrder(order);
			order.status = OrderStatus.DELIVERD_ON_TIME;
			model.getCartManager().submitOrder(order);
		}

	}

	/*----------------------WORKER CASES--------------------------*/

	private void onChangeCatalogue() {
		if (model.getUserManager().getCurrentUser().cardNumber.equals("0")) { // 0- Workers has no permission to change
																				// cataloge
			Text test = new Text("You are not autorized to change the catalogue.");
			return;
		} else {
			Text test = new Text("");
			setViewForAuthorizedUser();

		}

	}

	/**
	 *
	 * Sets the Authoriztion users
	 * 
	 * @param Order order
	 * @author Yohan, Niv
	 */
	private void setViewForAuthorizedUser() {
		scrollPane.setContent(content);
		ProductList products = model.getProductManager().getProducts("All");
		int numColumns = getNumColumns();
		int currentColumn = 0;
		int currentRow = 0;
		content.getChildren().clear();
		content.getColumnConstraints().clear();
		content.getRowConstraints().clear();
		for (Product p : products.items) {
			// System.out.println(p.imageString);
			ProductView productView = new ProductView(p);
			VBox pr = productView.getViewObjectForWorker();
			Button add = (Button) pr.getChildren().get(3);
			Button remove = (Button) pr.getChildren().get(4);
			if (p.inCatalogue) {
				add.setDisable(true);
				remove.setDisable(false);
			} else {
				add.setDisable(false);
				remove.setDisable(true);
			}
			add.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					model.getProductManager().productToFromCatalogue(p.name, true);
					add.setDisable(true);
					remove.setDisable(false);

				}
			});
			remove.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					model.getProductManager().productToFromCatalogue(p.name, false);
					add.setDisable(false);
					remove.setDisable(true);

				}
			});
			content.add(pr, currentColumn, currentRow);
			currentColumn = (currentColumn + 1) % numColumns;
			if (currentColumn == 0) {
				currentRow += 1;
			}
		}
	}

	/*----------------------SUPPORT CASES--------------------------*/
	/**
	 *
	 * Sets new discounts for product
	 * 
	 * @author Yohan, Niv
	 */
	private void onChangeDiscount() {
		scrollPane.setContent(content);
		ProductList products = model.getProductManager().getProducts("All");
		int numColumns = getNumColumns();
		int currentColumn = 0;
		int currentRow = 0;
		content.getChildren().clear();
		content.getColumnConstraints().clear();
		content.getRowConstraints().clear();
		for (Product p : products.items) {
			// System.out.println(p.imageString);
			ProductView productView = new ProductView(p);
			VBox pr = productView.getViewObjectForSupplier();
			Text price = (Text) pr.getChildren().get(2);
			ChoiceBox<Integer> discountBox = (ChoiceBox<Integer>) pr.getChildren().get(3);
			Button save = (Button) pr.getChildren().get(4);
			// onAction for discountBox box
			ObservableList<Integer> discountList = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40,
					45, 50);
			discountBox.setItems(discountList);
			discountBox.setValue(model.getProductManager().getProduct(p.name).discount);
			save.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					model.getProductManager().setDiscount(p.name, discountBox.getValue());
					if (model.getProductManager().getProduct(p.name).discount > 0) {
						price.setText("Price: " + (model.getProductManager().getProduct(p.name).price
								- (model.getProductManager().getProduct(p.name).price / 100)
										* model.getProductManager().getProduct(p.name).discount));
					} else
						price.setText("Price: " + model.getProductManager().getProduct(p.name).price);

				}
			});

			content.add(pr, currentColumn, currentRow);
			currentColumn = (currentColumn + 1) % numColumns;
			if (currentColumn == 0) {
				currentRow += 1;
			}
		}
	}

	/**
	 *
	 * Change the user credit card with the refund sum
	 * 
	 * @author Jessica, Yarden
	 */
	private void onWalletBalance() {
		VBox vbox = new VBox();
		Text wallet = new Text("You wallet balance is :");
		Text walletBalance = new Text(
				"" + model.getUserManager().getUserWallet(model.getUserManager().getCurrentUser().username));
		vbox.getChildren().addAll(wallet, walletBalance);
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(20);
		content.getChildren().add(vbox);
		content.setAlignment(Pos.CENTER);
	}

}