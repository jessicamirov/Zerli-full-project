package common;

import common.request_data.User;

/*
Flow steps:
1. "data", for example, User object is created by Client.
2. Request object created by Client with: Request Type, "User" object or null, "data" object or null.
3. When "data" object is added to request - it is converted to JSON string via "joJson()".
4. Request object converted to JSON string all together via "toJson()".
5. Request string is send to server including "data" string inside.
6. Server receives Request as a string
7. Server converts request to Request object view "fromJson"
8. Server identifies from Request that RequestType is, for example, "GET_USER".
9. Server gets data from request and converts data to User object.
10. Server check user name or does any other work on data. Server validates User versus database and adds "Full Name" and "Role" fields.
11. At the end of the work above we have new request object with updated data inside.
12. Server sends back Request object with updated "data".
13. Client receives Request string, converts it to object and checks that RequestType is same, for example, "GET_USER".
14. If RequestType changed to FORBIDDEN or UNKNOWN_REQUEST - reports error to user.
15. Otherwise get data from request and convert it correct "data" type, for example, to User object via new User().fromJson(data)

Note: it is important to send User information on each Request to validate that user has required permission for an action.
*/
public class Request extends JSONObject {
	public RequestType requestType;
	public User user;
	public String data;

	public Request() {
		this.requestType = null;
		this.user = null;
		this.data = null;
	}

	public Request(RequestType requestType, User authorizationInformation, JSONObject data) {
		this.requestType = requestType;
		this.user = authorizationInformation;
		if (data == null) {
			this.data = null;
		} else {
			this.data = data.toJson();
		}
	}

	public static Request fromJson(String s) {
		/* Add such function to each subclass! */
		return (Request) fromJson(s, Request.class);
	}
}
