package client.controller;

import client.model.ClientUserManager;
import common.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * control the top screen
 *
 * saves and show the user details at head of the screen
 * 
 * @author Katya and Aharon
 */
public class HeaderController extends BaseController {
	@FXML
	Text welcome;

	@FXML
	Button login;

	@FXML
	Button account;

	@FXML
	Button checkout;

	@FXML
	Button search;

	@FXML
	TextField search_text;

	private static String WELCOME = "Welcome";
	private static String HELLO = "Hello, ";
	private static String LOGIN = "Login";
	private static String LOGOUT = "Logout";

	/**
	 * update the current user details on the head of the screen
	 *
	 * @author Katya and Aharon
	 */
	@Override
	public void update() {
		ClientUserManager userManager = model.getUserManager();
		login.setDisable(false);

		if (model.getUsername() == null) {
			// Really guest user
			welcome.setText(WELCOME);
			login.setText(LOGIN);

			account.setDisable(true);
			checkout.setDisable(true);
			changeCeckout(false);
			disableSearch(false);
			return;

		} // frozen user gets same permission as guest

		else if (userManager.getCurrentUser().accountStatus.equals("Frozen")) {
			if (userManager.getCurrentUser().userrole.equals(Role.GUEST))
				System.out.println("56 headerController " + model.getUsername());
			welcome.setText("Please visit the store manager");
			login.setText("Frozen");
			login.setDisable(true);
			account.setDisable(true);
			checkout.setDisable(true);
			changeCeckout(false);
			disableSearch(false);
			return;
		}

		welcome.setText(HELLO + model.getUserManager().getUsername(null));
		login.setText(LOGOUT);
		account.setDisable(false);

		if (userManager.getCurrentUser().userrole == Role.MANAGER || userManager.getCurrentUser().userrole == Role.OWNER
				|| userManager.getCurrentUser().userrole == Role.SUPPORT) {
			checkout.setDisable(true);
			disableSearch(true);
			changeCeckout(false);
		} else if (userManager.getCurrentUser().userrole == Role.WORKER) {
			disableSearch(true);
			checkout.setDisable(true);
			changeCeckout(true);
		} else {
			checkout.setDisable(false);
			changeCeckout(false);
			disableSearch(false);
		}
	}

	/**
	 * let the user search in the catalog
	 *
	 * @author Katya and Aharon
	 */
	private void disableSearch(boolean disable) {
		search.setDisable(disable);
		search_text.setDisable(disable);
	}

	/**
	 * update the checkout part
	 *
	 * @author Katya and Aharon
	 */
	private void changeCeckout(boolean modify) {
		if (checkout.getStyleClass().contains("modify-button")) {
			if (!modify) {
				checkout.getStyleClass().remove("modify-button");
			}
		} else {
			if (modify) {
				checkout.getStyleClass().add("modify-button");
			}
		}
		if (checkout.getStyleClass().contains("checkout-button")) {
			if (modify) {
				checkout.getStyleClass().remove("checkout-button");
			}
		} else {
			if (!modify) {
				checkout.getStyleClass().add("checkout-button");
			}
		}

	}

	/**
	 * if the user is logout show the sign in option
	 *
	 * @author Katya and Aharon
	 */
	public void onLogin(ActionEvent event) throws Exception {
		if (model.getUsername() == null) {
			model.setScene("signin");
			return;
		}
		model.getUserManager().logOffUser(model.getUserManager().getCurrentUser());
		model.setUsername(null);
		model.setScene();
	}

	public void onAccount(ActionEvent event) throws Exception {
		model.setScene("account");
	}

	public void onCheckOut(ActionEvent event) throws Exception {
		model.setScene("order");
	}

	public void onSearch(ActionEvent event) throws Exception {
		/* TODO: apply search filtering */
		model.setScene("shop");
	}
}
