package common.request_data;

import java.util.ArrayList;

import common.JSONObject;

/**
 * this is an json class that represents the complaints list by this class we
 * can transfer the complaints list data between user->client->sql we can save
 * here all the complaint for the supporter that is stored in DB
 * 
 * @author Jessica, Yarden
 */
public class ComplaintList extends JSONObject {
	public int start;
	public int amount;
	public String supportName;
	// public String category;
	public ArrayList<Complaint> complaints; // Not List because it should be initialized by default during JSON //
											// creation.

	public static ComplaintList fromJson(String s) {
		/* Add such function to each subclass! */
		return (ComplaintList) fromJson(s, ComplaintList.class);
	}
}