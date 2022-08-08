package client.controller;

import client.model.EchoClient;
import common.Role;
import common.request_data.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Represent the first user screen
 *
 * shows log in option and connect to server
 *
 * @author Jessica and Yarden
 */
public class HomeController extends BaseController {
	private static String LOGIN = "Login";
	private static String LOGOUT = "Logout";
	int countAttemps = 0;

	@FXML
	private Button buttonUserChange = null;

	@FXML
	private Button buttonView = null;

	@FXML
	private Button buttonConnect = null;

	@FXML
	private TextField hostName = null;

	@FXML
	private TextField portNumber = null;

	@FXML
	private Text errorMessage = null;

	/**
	 * if the user logged in the button changed to logout
	 *
	 * @author Katya, Aharon
	 */
	@Override
	public void update() {
		if (countAttemps == 0) {
			buttonUserChange.setVisible(false);
			buttonView.setVisible(false);
		}
		countAttemps++;
		if (this.model.getUsername() != null) {
			buttonUserChange.setText(LOGOUT);
		} else {
			buttonUserChange.setText(LOGIN);
		}
	}

	/**
	 * log out the user and take him back to home screen
	 *
	 * @author Katya, Aharon
	 */
	public void onUserChange(ActionEvent event) throws Exception {
		if (this.model.getUsername() != null) {
			// User already logged in -> need to logout:
			this.model.getUserManager().logOffUser(new User(this.model.getUsername(), null));
			this.model.setUsername(null);
			this.model.setScene("home");

			return;
		}
		// User not logged in -> need to login:
		this.model.setScene("signin");
	}

	/**
	 * check if the connection to server passed and show the right message
	 *
	 * @author Katya, Aharon
	 */
	public void onConnect(ActionEvent event) throws Exception {
		if (this.model.getEchoClient() != null && this.model.getEchoClient().isServerAvailable()) {
			this.model.disconnectEchoClient();
			errorMessage.setText("Not connected to server");
			buttonConnect.setText("Connect");
			buttonUserChange.setVisible(false);
			buttonView.setVisible(false);

		} else {
			buttonUserChange.setVisible(true);
			buttonView.setVisible(true);
			buttonConnect.setVisible(false);
			hostName.setVisible(false);
			portNumber.setVisible(false);
			String portText = portNumber.getText().trim();
			int port;
			if (portText.isEmpty()) {
				port = EchoClient.DEFAULT_PORT;
			} else {
				try {
					port = Integer.parseInt(portText);
				} catch (NumberFormatException e) {
					errorMessage.setText("Invalid port");
					return;
				}
			}
			String host = hostName.getText().trim();
			if (host.isEmpty()) {
				host = EchoClient.DEFAULT_HOST;
			}

			this.model.setEchoClient(host, port);
			if (!this.model.getEchoClient().isServerAvailable()) {
				this.model.disconnectEchoClient();
				errorMessage.setText("Unable to connect to " + host + ":" + port);
				buttonConnect.setText("Connect");
				buttonUserChange.setDisable(true);
				buttonView.setDisable(true);
				return;
			}
			errorMessage.setText("Connected to " + host + ":" + port);
			buttonConnect.setText("Disconnect");
			buttonUserChange.setDisable(false);
			buttonView.setDisable(false);
		}
	}

	/**
	 * show the scene due to the user role
	 *
	 * @author Katya, Aharon
	 */
	public void onView(ActionEvent event) throws Exception {
		/* TODO: Change bellow to correct windows based on user type: */
		String username = this.model.getUsername();

		if (username != null) {
			if (this.model.getUserManager().getCurrentUser().userrole == Role.MANAGER) {
				this.model.setScene("account");
				return;
			}
			if (this.model.getUserManager().getCurrentUser().userrole == Role.OWNER) {
				this.model.setScene("account");
				return;
			}
			if (this.model.getUserManager().getCurrentUser().userrole == Role.WORKER) {
				this.model.setScene("account");
				return;
			}
			if (this.model.getUserManager().getCurrentUser().userrole == Role.SUPPORT) {
				this.model.setScene("account");
				return;
			}
			if (this.model.getUserManager().getCurrentUser().userrole == Role.DELIVERY) {
				this.model.setScene("account");
				return;
			}
			if (this.model.getUserManager().getCurrentUser().userrole == Role.SERVICE_EXPERT) {
				this.model.setScene("account");
				return;
			}
		}
		this.model.setScene("shop");
	}
}
