package common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONObject {
	public String toJson() {
		try {
			// Converts "this" to string in JSON format using all public fields of "this".
			return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	protected static JSONObject fromJson(String s, Class<? extends JSONObject> c) {
		if (s == null || s.isEmpty()) {
			return null;
		}
		try {
			// Converts string in JSON format to an object of provided class.
			return new ObjectMapper().readValue(s, c);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject fromJson(String s) {
		/* Add such function to each subclass! */
		return (JSONObject) fromJson(s, JSONObject.class);
	}
}
