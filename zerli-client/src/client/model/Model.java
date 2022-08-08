package client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import client.controller.BaseController;
import common.Role;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Holds all information about the current client
 * 
 * @author Jessica and Yarden
 */
public class Model {
	private static String APP_NAME = "Zerli";
	/*
	 * Window names should match FXML file names, add new strings as soon as they
	 * are ready:
	 */
	private static String[] WINDOW_NAMES = { "home", "signin", "shop", "order", "account", "empty" };
	private static String DEFAULT_WINDOW = WINDOW_NAMES[0];

	private Stage primaryStage;
	private Map<String, BaseController> controllers;
	private String username;
	private Stack<String> history;
	private ClientUserManager userManager;
	private ClientProductManager productManager;
	private ClientOrderManager orderManager;
	private AccountManager accountManager;
	private ClientCartManager cartManager;
	private EchoClient echoClient;

	public Model(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.controllers = new HashMap<String, BaseController>();
		username = null;
		history = new Stack<String>();
		this.primaryStage.setTitle(APP_NAME);
		echoClient = null;

		/* Will be set when EchoClient is provided. */
		userManager = null;
		productManager = null;
		orderManager = null;
		accountManager = null;
		cartManager = null;
	}

	public void addWindowController(String name, BaseController controller, Scene scene) {
		controller.setState(this, scene);
		this.controllers.put(name, controller);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setScene() throws IllegalArgumentException {
		setScene(WINDOW_NAMES[0]);
	}

	public void setScene(String sceneName) throws IllegalArgumentException {
		if (!controllers.containsKey(sceneName)) {
			throw new IllegalArgumentException("Incorrect scene name.");
		}
		if (sceneName == DEFAULT_WINDOW) {
			/* Default window does not have "back" button - we can clear a history here. */
			history.clear();
		}
		history.push(sceneName);
		BaseController controller = controllers.get(sceneName);
		controller.update();
		primaryStage.setScene(controller.getScene());
		primaryStage.show();
	}

	public void setSceneBack() {
		String sceneName = history.pop();
		if (history.isEmpty()) {
			// History is never empty as soon as setScene was called at least once.
			history.push(sceneName);
		}
		sceneName = history.pop();
		if (userManager.getCurrentUser().userrole == Role.MANAGER || userManager.getCurrentUser().userrole == Role.OWNER
				|| userManager.getCurrentUser().userrole == Role.SUPPORT) {
			/* Only Guest, Customer and Worker can view Shop. */
			if (sceneName == "shop") {
				sceneName = "account";
			}
		}
		setScene(sceneName);
	}

	public String[] getWindowNames() {
		return WINDOW_NAMES;
	}

	public ClientUserManager getUserManager() {
		return userManager;
	}

	public ClientProductManager getProductManager() {
		return productManager;
	}

	public ClientOrderManager getOrderManager() {
		return orderManager;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public ClientCartManager getCartManager() {
		return cartManager;
	}

	public void setEchoClient(String host, int port) {
		echoClient = new EchoClient(host, port);
		userManager = new ClientUserManager(echoClient);
		productManager = new ClientProductManager(echoClient);
		orderManager = new ClientOrderManager(echoClient);
		cartManager = new ClientCartManager(echoClient);
		accountManager = new AccountManager(this);

	}

	
	public void disconnectEchoClient() {
		echoClient.close();
		echoClient = null;
		userManager = null;
		productManager = null;
		orderManager = null;
		cartManager = null;
		accountManager = null;
	}

	public EchoClient getEchoClient() {
		return echoClient;
	}

}
