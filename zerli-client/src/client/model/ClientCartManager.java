package client.model;

import java.util.HashMap;

import common.Request;
import common.RequestType;
import common.interfaces.CartManager;
import common.request_data.CartItem;
import common.request_data.Order;
import common.request_data.OrderType;
import common.request_data.ServerError;
import common.request_data.Shop;
import common.request_data.User;

/**
 *
 * update the cart left menu buttons
 *
 * @author Katya, Aharon
 */
public class ClientCartManager implements CartManager {
	private static String[] steps = { "Cart", "Greeting", "Delivery", "Payment", "Summary" };

	private EchoClient client;
	public User currentUser;
	private CartItem cart;
	ClientCartManager clientcartmanager;
	public ClientCartManager(EchoClient client) {
		this.client = client;
		this.currentUser = null;
		cart = new CartItem();
		cart.items = new HashMap<String, Integer>();
		clientcartmanager=this;
	}

	public ClientCartManager(EchoClient client, ClientCartManager clientcartmanager) {
		this.client = client;
		this.currentUser = null;
		cart = new CartItem();
		cart.items = new HashMap<String, Integer>();
		this.clientcartmanager=clientcartmanager;
	}

	public void setCurrentUser(User user) {
		currentUser = user;
	}

	public String[] getSteps() {
		return steps;
	}

	public void addToCart(String name) {
		if (!cart.items.containsKey(name) || cart.items == null) {
			cart.items.put(name, 1);
		} else {
			cart.items.put(name, (cart.items.get(name)) + 1);
		}

	}

	public CartItem getCart() {
		return cart;
	}

	public Order submitOrder(Order order) {

		Request request = client.requestServer(new Request(RequestType.ADD_ORDER, currentUser, order));
		if (request.requestType != RequestType.ADD_ORDER) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}
		return Order.fromJson(request.data);
	}

	public void cleanCart() {
		cart.address = null;
		cart.city = null;
		cart.date = null;
		cart.greetingMessage = null;
		cart.items = new HashMap<String, Integer>();
		cart.orderType = null;
		cart.paymentID = null;
		cart.paymentPhone = null;
		cart.phone = null;
		cart.price = 0.0;
		cart.recipient = null;
		cart.shop = null;
		cart.signature = null;
		cart.time = null;
		cart.submit = false;
		cart.paidByWallet = 0.0;

	}

}