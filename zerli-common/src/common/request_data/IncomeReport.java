package common.request_data;

import common.JSONObject;

/**
 * 
 * Default constructor is required for JSONObject if any other constructor
 * defined.
 * 
 * @author Yohan, Niv
 */
public class IncomeReport extends JSONObject {
	public Shop shop;
	public String year;
	public String month;
	public String income;
	public String bestSellingProduct;
	public String totalNumberOfOrders;

	public IncomeReport() {

	}

	public IncomeReport(Shop shop, String year, String month, String Income, String BSI, String TNO) {
		this.shop = shop;
		this.year = year;
		this.month = month;
		this.income = Income;
		this.bestSellingProduct = BSI;
		this.totalNumberOfOrders = TNO;

	}

	public static IncomeReport fromJson(String s) {
		/* Add such function to each subclass! */
		return (IncomeReport) fromJson(s, IncomeReport.class);
	}
}