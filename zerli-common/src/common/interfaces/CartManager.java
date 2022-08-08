package common.interfaces;


import common.request_data.Order;

public interface CartManager {

	public Order submitOrder(Order order);
}