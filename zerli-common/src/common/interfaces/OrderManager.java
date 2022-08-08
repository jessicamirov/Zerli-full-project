package common.interfaces;

import common.request_data.Order;
import common.request_data.OrderList;

public interface OrderManager {
	public Order getOrder(String username);
	public OrderList getOrders(String username);
	public OrderList getOrdersM(String shopName);
	public void updateOrder(Order order);
	void deleteOrder(Order order);
}