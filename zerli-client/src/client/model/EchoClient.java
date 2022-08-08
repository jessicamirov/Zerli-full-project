package client.model;

import java.io.IOException;

import common.Request;
import common.RequestType;
import common.request_data.ServerError;
import ocsf.client.AbstractClient;

/**
 * holding the client part until server message
 * 
 * @param Order order
 * @author Yohan and Niv
 */
public class EchoClient extends AbstractClient {
	/*
	 * Simple client that sends "Request" object to server and gets same "Request"
	 * object back from server with modified data fields In case of errors
	 * Request.requestType is changed to REQUEST_FAILED or FORBIDDEN. If operation
	 * is correct requestType is preserved.
	 */
	/* Result will be stored here: */
	private String responseMessage;

	/*
	 * When listening for server response lets make sure we are not listening
	 * forever.
	 */
	private static int MAX_ATTEMPTS = 1000;
	private static int WAIT_DELAY = 100; // milliseconds

	public static int DEFAULT_PORT = 5555;
	public static String DEFAULT_HOST = "localhost";

	public EchoClient(String host, int port) {
		super(host, port);
	}

	public boolean isServerAvailable() {
		/* Send a TEST request to server and check that it is returned unchanged. */
		Request request = requestServer(new Request(RequestType.PING, null, null));
		/*
		 * For any problem, requestType will change to REQUEST_FAILED. If requestType is
		 * still PING - server is behaving correctly.
		 */
		System.out.println(request.data);
		return (request.requestType == RequestType.PING);
	}

	public void close() {
		/* Close connection to server. */
		try {
			closeConnection();
		} catch (IOException e) {
		}
	}

	private void connectIfNotConnected() throws IOException {
		/* Do not open a new connection if already connected. */
		if (isConnected()) {
			return;
		}
		openConnection();
	}

	@Override
	protected void handleMessageFromServer(Object message) {
		System.out.println((String) message);
		responseMessage = (String) message;
	}

	public Request requestServer(Request request) {
		responseMessage = null;
		try {
			connectIfNotConnected();
			sendToServer(request.toJson());
		} catch (IOException e) {
			e.printStackTrace();
			request.requestType = RequestType.REQUEST_FAILED;
			request.data = new ServerError("Cannot connect to server.").toJson();
			return request;
		}
		int attempt = 0;
		while (responseMessage == null) {
			attempt++;
			if (attempt > MAX_ATTEMPTS) {
				request.requestType = RequestType.REQUEST_FAILED;
				request.data = new ServerError("Timeout reached.").toJson();
				/*
				 * Probably connection issue. Lets close and reopen a new connection on next
				 * request.
				 */
				close();
				return request;
			}
			try {
				Thread.sleep(WAIT_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
				request.requestType = RequestType.REQUEST_FAILED;
				request.data = new ServerError("Thread was interrupted.").toJson();
				return request;
			}
		}
		return Request.fromJson(responseMessage);
	}
}
