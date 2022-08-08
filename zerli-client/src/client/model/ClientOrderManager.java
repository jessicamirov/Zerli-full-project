package client.model;

import java.util.List;

import common.Request;
import common.RequestType;
import common.interfaces.OrderManager;
import common.request_data.CartItem;
import common.request_data.Order;
import common.request_data.OrderList;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.ServerError;
import common.request_data.User;

/**
 * represent order screen
 *
 * @author Katya, Aharon
 */
public class ClientOrderManager implements OrderManager {

	private EchoClient client;
	public User currentUser;
	ClientOrderManager clientordermanager;

	public ClientOrderManager(EchoClient client) {
		this.client = client;
		this.currentUser = null;
		clientordermanager=this; //In normal case
	}

	public ClientOrderManager(EchoClient echoClient, ClientOrderManager clientordermanager) {
		this.clientordermanager=clientordermanager;
	}

	public void setCurrentUser(User user) {
		currentUser = user;
	}

	public void clear() {
		/* Removes all order information, e.g. on user change or on order complete. */
	}

	/**
	 * insert orderNum primaryKey and get the order
	 * 
	 * @param orderNum - primary Key
	 * @author Katya and Aharon
	 * @return Order x return 1 order by orderNum
	 */
	public Order getOrder(String orderNum) {
		Order order = new Order();
		order.orderNumber = orderNum;
		Request request = client.requestServer(new Request(RequestType.GET_ORDER, currentUser, order));
		if (request.requestType != RequestType.GET_ORDER) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}

		return Order.fromJson(request.data);
	}

	public OrderList getOrders(String username) {
		OrderList orderList = new OrderList();
		orderList.username = username;
		Request request = client.requestServer(new Request(RequestType.GET_ORDERS, currentUser, orderList));
		if (request.requestType != RequestType.GET_ORDERS) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}

		return OrderList.fromJson(request.data);
	}

	/**
	 * insert orderNum primaryKey and get the order
	 * 
	 * @param orderNum - primary Key
	 * @author Katya and Aharon
	 * @return OrderLiset return all the user orders
	 */
	@Override
	public OrderList getOrdersM(String shopName) {
		OrderList orderList = new OrderList();
		orderList.username = shopName;
		Request request = client.requestServer(new Request(RequestType.GET_ORDERS, currentUser, orderList));
		if (request.requestType != RequestType.GET_ORDERS) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}

		return OrderList.fromJson(request.data);
	}

	@Override
	public void updateOrder(Order order) {
		Request request = client.requestServer(new Request(RequestType.UPDATE_ORDER, currentUser, order));
	}

	@Override
	public void deleteOrder(Order order) {
		Request request = client.requestServer(new Request(RequestType.DELETE_ORDER, currentUser, order));

	}

}
