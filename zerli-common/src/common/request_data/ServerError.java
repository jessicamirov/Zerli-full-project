package common.request_data;

import common.JSONObject;

/**
 * JSON class that saves messages about errors and allows them to be transferred
 * between the client and the server
 * 
 * @author Katya
 */
public class ServerError extends JSONObject {
	public String message;

	public ServerError() {
		/*
		 * Default constructor is required for JSONObject if any other constructor
		 * defined.
		 */
	}

	public ServerError(String message) {
		this.message = message;
	}

	public static ServerError fromJson(String s) {
		/* Add such function to each subclass! */
		return (ServerError) fromJson(s, ServerError.class);
	}
}
