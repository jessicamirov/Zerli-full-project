package client.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import client.model.ProductView;
import common.request_data.CartItem;
import common.request_data.Order;
import common.request_data.OrderStatus;
import common.request_data.OrderType;
import common.request_data.Product;
import common.request_data.ProductListCart;
import common.request_data.Shop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * Represents the items hold by the customer while in order
 * 
 * @author Katya, Ahron
 */
public class CartMainController extends BaseController {
	private static int NUM_COLUMNS = 3;

	@FXML
	Text mainHeader;
	@FXML
	GridPane content;
	@FXML
	ScrollPane scrollPane;
	String uncorrectText = "Must be filled";

	@Override
	public void update() {
	}

	private int getNumColumns() {
		return NUM_COLUMNS;
	}

	/**
	 *
	 * From String to Data type for the Data Picker
	 * 
	 * @param String dateString
	 * @author Katya, Ahron
	 */
	public static final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}

	/**
	 *
	 * Sets the left menu button for the cart
	 * 
	 * @param String view
	 * @author Katya, Ahron
	 */
	@Override
	public void setView(String view) {
		scrollPane.setContent(content);
		mainHeader.setText(view + ":");
		content.getChildren().clear();
		content.getColumnConstraints().clear();
		content.getRowConstraints().clear();
		content.setAlignment(Pos.TOP_LEFT);

		CartItem cart = model.getCartManager().getCart();

		switch (view) {

		case "Cart":
			onCart(cart);
			break;

		case "Greeting":
			onGreeting(cart);
			break;

		case "Delivery":
			onDelivery(cart);
			break;

		case "Payment":
			onPayment(cart);
			break;
		case "Summary":
			onSummary(cart);
			break;

		default:
			break;
		}

	}

	/**
	 *
	 * Show all the items in cart and gives an opportunity to add or remove items
	 * 
	 * @param CartItem cart
	 * @author Katya, Ahron
	 */
	private void onCart(CartItem cart) {
		int numColumns = getNumColumns();
		int currentColumn = 0;
		int currentRow = 0;
		if (cart.items != null) {
			for (String name : cart.items.keySet()) {

				Product p = model.getProductManager().getProduct(name);
				ProductView product = new ProductView(p);
				VBox productBox = product.getViewObjectForCart();
				Text price = (Text) productBox.getChildren().get(2);
				double priceToShow = p.price - (p.price / 100 * p.discount);
				price.setText("" + priceToShow + " nis");
				HBox buttons = (HBox) productBox.getChildren().get(3);
				Button plus = (Button) buttons.getChildren().get(0);
				Text amountText = (Text) buttons.getChildren().get(1);
				Button minus = (Button) buttons.getChildren().get(2);
				amountText.setText(cart.items.get(name).toString());

				// ----------------------ON ACTIONS----------------//

				// onAction for Plus button
				plus.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						Integer amount = cart.items.get(name);
						amount++;
						cart.items.put(name, amount);
						amountText.setText(cart.items.get(name).toString());

					}
				});
				// onAction for minus button
				minus.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						Integer amount = cart.items.get(name);
						if (amount == 0) {
							return;
						}
						amount--;
						cart.items.put(name, amount);

						amountText.setText(cart.items.get(name).toString());
					}
				});

				content.add(productBox, currentColumn, currentRow);
				currentColumn = (currentColumn + 1) % numColumns;
				if (currentColumn == 0) {
					currentRow += 1;
				}
			}
		}

	}

	/**
	 *
	 * Adding the greeting cart if it needs
	 * 
	 * @param CartItem cart
	 * @author Katya, Ahron
	 */
	private void onGreeting(CartItem cart) { // it works, logical job is finished
		VBox vbox = new VBox();
		Text recipient = new Text("Recipient:");
		TextField recipientText = new TextField();
		if (cart.recipient != null) {
			recipientText.setText(cart.recipient);
		}

		Text message = new Text("Message:");
		TextArea messageText = new TextArea();
		if (cart.recipient != null) {
			messageText.setText(cart.greetingMessage);
		}

		Text signature = new Text("Signature");
		TextField signatureText = new TextField();
		if (cart.recipient != null) {
			signatureText.setText(cart.signature);
		}

		vbox.getChildren().addAll(recipient, recipientText, message, messageText, signature, signatureText);
		content.getChildren().add(vbox);

		// ----------------------ON ACTIONS----------------//

		// onAction for recipient
		recipientText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.recipient = newValue;

		});
		// onAction for greeting message
		messageText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.greetingMessage = newValue;
		});
		// onAction for signature
		signatureText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.signature = newValue;
		});

	}

	/**
	 *
	 * Add delivery information
	 * 
	 * @param CartItem cart
	 * @author Katya, Ahron
	 */
	private void onDelivery(CartItem cart) {
		VBox vbox = new VBox();
		Text shop = new Text("Shop:");

		/* Choose shop */

		ChoiceBox<Shop> shopChoice = new ChoiceBox<>();
		shopChoice.getItems().addAll(Shop.HAIFA, Shop.NAHARIYA);
		if (cart.shop != null) {
			shopChoice.setValue(cart.shop);
		}

		/* Choose delivery or take away */

		Text orderType = new Text("Order type:");

		ChoiceBox<OrderType> orderTypeChoice = new ChoiceBox<>();
		orderTypeChoice.getItems().setAll(OrderType.values());
		if (cart.orderType != null) {
			orderTypeChoice.setValue(cart.orderType);
		}

		/* Choose date and time */

		Text date = new Text("Delivery or take away date:");
		DatePicker dateChoice = new DatePicker();
		if (cart.date != null) {
			try {
				dateChoice.setValue(LOCAL_DATE(cart.date));
			} catch (NullPointerException e) {
				System.out.println("Wrong date");
			}

		}

		Text time = new Text("Delivery or take away time:");
		ChoiceBox<String> timeChoice = new ChoiceBox<String>();
		if (cart.time != null) {
			timeChoice.setValue(cart.time);
		}

		/* Enter city and address */

		Text city = new Text("City:");
		TextField cityText = new TextField();

		if (cart.city != null) {
			cityText.setText(cart.city);
		}

		Text address = new Text("Address:");
		TextField addressText = new TextField();
		if (cart.address != null) {
			addressText.setText(cart.address);
		}
		if (cart.orderType == OrderType.TAKE_AWAY) {
			cityText.setDisable(true);
			addressText.setDisable(true);
		} else {
			cityText.setDisable(false);
			addressText.setDisable(false);

		}

		/* Add all nodes to VBOX */

		vbox.getChildren().addAll(shop, shopChoice, orderType, orderTypeChoice, date, dateChoice, time, timeChoice,
				city, cityText, address, addressText);
		content.getChildren().add(vbox);

		// ----------------------ON ACTIONS----------------//

		// On action for shopChoice box
		shopChoice.setOnAction((event) -> {
			cart.shop = shopChoice.getSelectionModel().getSelectedItem();

		});

		// onAction for orderTypeChoice box
		orderTypeChoice.setOnAction((event) -> {
			cart.orderType = orderTypeChoice.getSelectionModel().getSelectedItem();
			if (cart.orderType == OrderType.TAKE_AWAY) {
				cityText.setDisable(true);
				addressText.setDisable(true);
			}

		});

		// onAction for dateChoice box
		dateChoice.valueProperty().addListener((ov, oldValue, newValue) -> {
			cart.date = newValue.toString();
		});

		// onAction for timeChoice box
		ObservableList<String> hourList = FXCollections.observableArrayList("10:00", "10.30", "11:00", "11:30", "12:00",
				"12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "18:00",
				"18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00");
		timeChoice.setItems(hourList);
		timeChoice.setOnAction((event) -> {
			cart.time = timeChoice.getSelectionModel().getSelectedItem().toString();
		});

		// onAction for city choice
		cityText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.city = newValue;
		});

		// onAction for address
		addressText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.address = newValue;
		});

	}

	/**
	 *
	 * Add payment information
	 * 
	 * @param CartItem cart
	 * @author Katya, Ahron
	 */
	private void onPayment(CartItem cart) {
		VBox vbox = new VBox();

		Text id = new Text("ID:");
		TextField idText = new TextField();
		if (cart.paymentID != null) {
			idText.setText(cart.paymentID);
		}

		Text phone = new Text("Buyer Phone:");
		TextField phoneText = new TextField();
		if (cart.paymentPhone != null) {
			phoneText.setText(cart.paymentPhone);
		}

		Text callNumber = new Text("Call number:");
		TextField callNumberText = new TextField();
		if (cart.phone != null) {
			callNumberText.setText(cart.phone);
		}
		Text wallet = new Text("Wallet balance: "
				+ model.getUserManager().getUserWallet(model.getUserManager().getCurrentUser().username)
				+ "\nDo you want to pay by wallet?");
		Button payByWallet = new Button("Pay by wallet");

		vbox.getChildren().addAll(id, idText, phone, phoneText, callNumber, callNumberText, wallet, payByWallet);
		vbox.setSpacing(5);

		content.getChildren().add(vbox);

		// ----------------------ON ACTIONS----------------//

		// onAction for ID
		idText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.paymentID = newValue;
		});

		// onAction for phone of payment person
		phoneText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.paymentPhone = newValue;
		});
//		// onAction for phone
		callNumberText.textProperty().addListener((observable, oldValue, newValue) -> {
			cart.phone = newValue;
		});
		// onAction to pay by wallet
		payByWallet.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				double priceFromServer = 0.0;
				if (cart.items != null) {
					for (String name : cart.items.keySet()) {

						Product p = model.getProductManager().getProduct(name);
						double price = (p.price - (p.price / 100 * p.discount)) * cart.items.get(name); // real price
																										// with
																										// discount
						priceFromServer += price; // The real price * count of products = real price

					}

					if (cart.paidByWallet == 0) {
						if (Double.parseDouble(model.getUserManager()
								.getUserWallet(model.getUserManager().getCurrentUser().username)) >= priceFromServer) {
							cart.paidByWallet = priceFromServer;

							wallet.setText("Wallet balance: " + (Double
									.parseDouble(model.getUserManager()
											.getUserWallet(model.getUserManager().getCurrentUser().username))
									- cart.paidByWallet));
						} else {
							cart.paidByWallet = Double.parseDouble(model.getUserManager()
									.getUserWallet(model.getUserManager().getCurrentUser().username)); // CHANGES
							wallet.setText("Wallet balance: " + 0);

						}
					}

				}
			}
		});
	}

	/**
	 *
	 * Gets all information before submit
	 * 
	 * @param CartItem cart
	 * @author Katya, Ahron
	 */
	private void onSummary(CartItem cart) {
		// To check that the total price is right according to prices in database
		if (cart.items != null) {
			double priceFromServer = 0.0;
			for (String name : cart.items.keySet()) {

				Product p = model.getProductManager().getProduct(name);
				double price = (p.price - (p.price / 100 * p.discount)) * cart.items.get(name); // real price with
																								// discount
				priceFromServer += price; // The real price * count of products = real price

			}
			cart.price = priceFromServer;

		}
		VBox right = new VBox();
		VBox left = new VBox();

		Text recipient = new Text("Recipient:");
		Text recipientValue = new Text(cart.recipient == null ? uncorrectText : cart.recipient);

		Text recipientPhone = new Text("Recipient Phone:");
		Text recipientPhoneValue = new Text(cart.phone == null ? uncorrectText : cart.phone);

		Text buyer = new Text("Buyer:");
		Text buyerValue = new Text(model.getUserManager().getUsername(null));

		Text buyerPhone = new Text("Buyer Phone:");
		Text buyerPhoneValue = new Text(cart.paymentPhone == null ? uncorrectText : cart.paymentPhone);

		Text greeting = new Text("Greeting cart:");
		Text greetingValue = new Text(cart.greetingMessage == null ? "No" : "Yes");

		Text dateTime = new Text();
		Text dateTimeValue = new Text();
		if (cart.orderType == OrderType.TAKE_AWAY) {
			dateTime.setText("Take away date and time:");
		} else {
			dateTime.setText("Delivery Date and Time:");
		}
		if (cart.date == null || cart.time == null) {
			dateTimeValue.setText(uncorrectText);
		} else {
			dateTimeValue.setText(cart.date + " " + cart.time);
		}

		Text address = new Text("");
		Text addressValue = new Text();
		if (cart.orderType == OrderType.TAKE_AWAY) {
			address.setText("Order Type:");
			addressValue.setText("Take away");
		} else {
			address.setText("Delivery Address:");
			if (cart.address == null) {
				addressValue.setText(uncorrectText);
			} else {
				addressValue.setText(cart.city + ", " + cart.address);
			}
		}

		Text price = new Text("Price:");

		if (cart.paidByWallet > 0) {
			price.setText("Price after wallet paying: ");
		}
		double priceWithWallet = cart.price - cart.paidByWallet;
		Text priceValue = new Text("" + priceWithWallet);

		Text shipping = new Text("Shipping:");

		Text shippingValue = new Text("20.00");
		if (cart.orderType == OrderType.TAKE_AWAY) {
			shippingValue.setText("0.0");
		}

		Text totalPrice = new Text("Total:");

		Double total = priceWithWallet;
		if (cart.orderType == OrderType.DELIVERY) {
			total += 20;
		}
		Text totalPriceValue = new Text(total.toString());

		Button submit = new Button("Submit");
		submit.setId("buttonSubmitOrder");

		Text readyForSubmit = new Text("");

		if ((cart.orderType == OrderType.DELIVERY
				&& (cart.address == null || cart.address == "" || cart.city == null || cart.city == ""))
				|| cart.date == null || cart.date == "" || cart.orderType == null || cart.paymentID == null
				|| cart.paymentID == "" || cart.paymentPhone == null || cart.paymentPhone == "" || cart.phone == null
				|| cart.phone == "" || cart.shop == null || cart.time == null || cart.time == "") {

			submit.setDisable(true);
			readyForSubmit.setText("Please fill all the information");
			readyForSubmit.setFill(Color.RED);
		} else {
			submit.setDisable(false);
			readyForSubmit.setText("");
		}
		submit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Order order = CreateOrderFromCart(cart);
				order = model.getCartManager().submitOrder(order);
				if (!cart.submit) {
					submit.setDisable(true);
					final Stage dialog = new Stage();
					dialog.initModality(Modality.APPLICATION_MODAL);
					Stage stage = new Stage();
					dialog.initOwner(stage);
					VBox dialogVbox = new VBox(20);
					dialogVbox.getChildren().add(new Text("Order was added succesfully.\nCheck your e-mail."));
					Button close = new Button("Close");
					close.setId("button");
					close.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							model.getCartManager().cleanCart();
							dialog.close();
							content.getChildren().clear();
							askCustomer();
						}
					});

					dialogVbox.getChildren().add(close);
					dialogVbox.setAlignment(Pos.CENTER);
					Scene dialogScene = new Scene(dialogVbox, 300, 200);

					dialog.setScene(dialogScene);
					dialog.show();

				}

			}
		});
		submit.setAlignment(Pos.CENTER);
		left.getChildren().addAll(recipient, recipientPhone, buyer, buyerPhone, greeting, dateTime, address, price,
				shipping, totalPrice);
		right.getChildren().addAll(recipientValue, recipientPhoneValue, buyerValue, buyerPhoneValue, greetingValue,
				dateTimeValue, addressValue, priceValue, shippingValue, totalPriceValue);

		left.setSpacing(6);
		right.setSpacing(5);

		content.setHgap(30);
		content.setVgap(10);
		content.add(left, 0, 0);
		content.add(right, 1, 0);
		content.add(readyForSubmit, 0, 1);
		content.add(submit, 1, 1);

	}

	/**
	 *
	 * Creates new order to send to sql
	 * 
	 * @param CartItem cart
	 * @author Katya, Ahron
	 */
	private Order CreateOrderFromCart(CartItem cart) {
		Order order = new Order();
		order.address = cart.address;
		order.city = cart.city;
		order.date = cart.date;
		order.greetingMessage = cart.greetingMessage;
		order.hour = cart.time;
		order.orderNumber = "";
		order.orderType = cart.orderType;
		order.paymentPhone = cart.paymentPhone;
		order.phone = cart.phone;
		order.products = new ProductListCart();
		order.products.items = new HashMap<String, Integer>();
		for (String name : cart.items.keySet()) {
			Product p = model.getProductManager().getProduct(name);
			order.products.items.put(p.name, cart.items.get(name));

		}
		order.recipient = cart.recipient;
		order.shop = cart.shop;
		order.signature = cart.signature;
		order.status = OrderStatus.WAITING_FOR_APPROVE;
		order.totalPrice = cart.price;
		order.username = model.getUsername();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		order.timeOfOrder = dtf.format(now);
		order.paidByWallet = cart.paidByWallet;
		return order;

	}

	/**
	 *
	 * Shows the content page after the submit is done
	 * 
	 * @author Katya, Ahron
	 */
	private void askCustomer() {
		Button continueShopping = new Button("Continue shopping");
		continueShopping.setId("Button");
		Button seeOrders = new Button("Show my orders");
		seeOrders.setId("Button");
		content.setAlignment(Pos.CENTER);
		content.add(continueShopping, 0, 0);
		content.add(seeOrders, 1, 0);
		continueShopping.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				model.setScene("shop");

			}
		});
		seeOrders.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				model.setScene("account");

			}
		});

	}

}