package common.request_data;

import java.util.ArrayList;

import common.JSONObject;

/**
 * this is an json class that represents the Catagories list in the menu by this
 * class we can transfer the category list data between user->client->sql
 * 
 * @author Katya, Ahron
 */
public class CategoriesList extends JSONObject {
	public ArrayList<String> items; // Not List because it should be initialized by default during JSON creation.

	public static CategoriesList fromJson(String s) {
		/* Add such function to each subclass! */
		return (CategoriesList) fromJson(s, CategoriesList.class);
	}
}
