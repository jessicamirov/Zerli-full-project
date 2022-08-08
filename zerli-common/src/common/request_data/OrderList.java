package common.request_data;

import java.util.ArrayList;

import common.JSONObject;

/**
 * JSON class that saves information about one order and can be transferred
 * between the client and the server for each order saves -
 * number,username,time,orderstatus,order time,is canceled the phone and the
 * city of the user.
 * 
 * @author Jessica, Yarden
 */
public class OrderList extends JSONObject {
	public int start;
	public int amount;
	public String username;
	public ArrayList<Order> orders; // Not List because it should be initialized by default during JSON creation.

	public static OrderList fromJson(String s) {
		/* Add such function to each subclass! */
		return (OrderList) fromJson(s, OrderList.class);
	}

}