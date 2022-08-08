package common.request_data;

import java.util.ArrayList;

import common.JSONObject;

/**
 * 
 * Default constructor is required for JSONObject if any other constructor
 * defined.
 * 
 * @author Yohan, Niv
 */
public class IncomeReportList extends JSONObject {

	public ArrayList<IncomeReport> Reports;

	public static IncomeReportList fromJson(String s) {
		/* Add such function to each subclass! */
		return (IncomeReportList) fromJson(s, IncomeReportList.class);
	}

}
