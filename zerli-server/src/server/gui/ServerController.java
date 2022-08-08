package server.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import server.model.DBManager;
import server.model.EchoServer;
import server.model.ServerManager;

/**
 * holds all the button and textfiels to connect to sql
 * 
 * @author Katya and Ahron
 */
public class ServerController {
	@FXML
	private Button buttonConnect = null;
	
	@FXML
	private Button buttonImport = null;

	@FXML
	private TextField portText = null;

	@FXML
	private TextField urlText = null;

	@FXML
	private TextField usernameText = null;

	@FXML
	private PasswordField passwordText = null;

	@FXML
	private Text serverMessage = null;

	@FXML
	private Text sqlMessage = null;

	private static String SERVER_CONNECTED = "Listening on: ";
	private static String SERVER_DISCONNECTED = "Server Offline";
	private static String SQL_CONNECTED = "SQL Online";
	private static String SQL_DISCONNECTED = "SQL Offline";
	private ServerManager model;
	private boolean connected;

	public void setServerModel(ServerManager model) {
		this.model = model;
		this.connected = false;
		
	}

	public void onDefault(ActionEvent event) throws Exception {
		portText.setText("" + EchoServer.getDefaultPort());
		urlText.setText(DBManager.getDefaultURL());
		usernameText.setText(DBManager.getDefaultUsername());
		passwordText.setText(DBManager.getDefaultPassword());
	}
	
	public void onImport(ActionEvent event) {
		if(!connected) {
			serverMessage.setText("YOU MUST CONNECT FIRST");
			return;
		}
		else {
		this.model.getDBManager().importUsersFromDifferentDataBase();
		//buttonImport.setVisible(false);
		}
	}

	public void onConnect(ActionEvent event) throws Exception {
		if (connected) {
			connected = false;
			buttonConnect.setText("Connect");
			this.model.close();
			serverMessage.setText(SERVER_DISCONNECTED);
			sqlMessage.setText(SQL_DISCONNECTED);
			return;
		}
		int port;
		try {
			port = Integer.parseInt(portText.getText().trim());
		} catch (NumberFormatException e) {
			serverMessage.setText("Invalid port");
			return;
		}
		String url = urlText.getText().trim();
		String username = usernameText.getText().trim();
		String password = passwordText.getText().trim();
		/* TODO: Check that arguments above make sense. */
		
		this.model.setServer(port);
		this.model.setDBManager(url, username, password);

		connected = true;
		if (this.model.isServerRunning()) {
			serverMessage.setText(SERVER_CONNECTED + port);
		} else {
			connected = false;
			serverMessage.setText(SERVER_DISCONNECTED);
		}
		if (this.model.isSQLCnnected()) {
			sqlMessage.setText(SQL_CONNECTED);
		} else {
			connected = false;
			sqlMessage.setText(SQL_DISCONNECTED);
		}
		if (connected) {
			buttonConnect.setText("Disconnect");
		} else {
			buttonConnect.setText("Connect");
		}
	}
}
