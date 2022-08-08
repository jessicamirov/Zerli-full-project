package common.request_data;

import java.util.ArrayList;

import common.JSONObject;

/**
 * User-a json class that this class help us to transform the user details
 * between client and server
 *
 * @author Katya ,Ahron
 */
public class UsersList extends JSONObject {

	public ArrayList<User> Users;

	public static UsersList fromJson(String s) {
		/* Add such function to each subclass! */
		return (UsersList) fromJson(s, UsersList.class);
	}

}
