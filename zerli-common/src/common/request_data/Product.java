package common.request_data;

import common.JSONObject;

/**
 * JSON class that saves information about one product and can be transferred
 * between the client and the server for each product saves -
 * names,price,discount,category,image name
 * 
 * @author Katya,Ahron
 */
public class Product extends JSONObject {
	public String name;
	public double price;
	public int discount; // Percent
	public String category;
	public String imageString;
	public boolean inCatalogue;

	public Product() {
		name = null;
		price = 0.0;
		discount = 0;
		category = null;
		imageString = null;
		inCatalogue = false;
	}

	public Product(String name, double price, int discount, String category, String imageString, boolean inCatalogue) {
		this.name = name;
		this.price = price;
		this.discount = discount;
		this.category = category;
		this.imageString = imageString;
		this.inCatalogue = inCatalogue;
	}

	public static Product fromJson(String s) {
		/* Add such function to each subclass! */
		return (Product) fromJson(s, Product.class);
	}
}