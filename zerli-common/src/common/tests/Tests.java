package common.tests;

import common.Request;
import common.RequestType;
import common.request_data.User;

public class Tests {
	public static void testRequest() {
		System.out.println("Testing Request");
		Request client = new Request(RequestType.GET_USER, null, new User("K", "pass"));
		String clientJson = client.toJson();

		Request server = Request.fromJson(clientJson);
		String serverJson = server.toJson();

		if (!serverJson.equals(clientJson)) {
			System.out.println("ERROR: Requests");
		} else {
			System.out.println("Request Passed");
		}
	}

	public static void testUser() {
		System.out.println("Testing User");
		User client = new User("K", "pass");
		String clientJson = client.toJson();

		User server = User.fromJson(clientJson);
		String serverJson = server.toJson();
		if (!serverJson.equals(clientJson)) {
			System.out.println("ERROR: User");
		} else {
			System.out.println("User Passed");
		}
	}

	public static void main(String[] args) {
		testUser();
		testRequest();
	}
}
