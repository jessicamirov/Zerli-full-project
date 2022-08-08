package common.request_data;

import java.util.HashMap;

import common.JSONObject;

/**
 * JSON that gives saves all the products in the user cart can be transferred
 * between the client and the server the product saves in hash map and not in
 * aList because it should be initialized by default during JSON creation.
 * 
 * @author Katya
 */
public class ProductListCart extends JSONObject {
	public HashMap<String, Integer> items; // Not List because it should be initialized by default during JSON creation.

	public static ProductListCart fromJson(String s) {
		/* Add such function to each subclass! */
		return (ProductListCart) fromJson(s, ProductListCart.class);
	}

	@Override
	public String toString() {

		return items.toString();

	}
}