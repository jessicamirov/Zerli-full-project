package client.controller;

import client.model.Model;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * represents all the controllers of the screen partition
 *
 * Each part of the screen has a controller that is responsible for it
 * headerController-control the top of the user screen-the user details
 * LeftMenuController -control the left part of the user screen-the menu
 * AccountMainController - control the User functionality Katya Aharon
 */
public class AccountController extends BaseController {
	/* These 2 should come in pair, though first one is not used: */
	@FXML
	private Parent header;
	@FXML
	private HeaderController headerController;

	/* These 2 should come in pair, though first one is not used: */
	@FXML
	private Parent category;
	@FXML
	private LeftMenuController leftMenuController;

	/* These 2 should come in pair, though first one is not used: */
	@FXML
	private Parent main;
	@FXML
	private AccountMainController mainController;

	/**
	 * this function get model and scene and save it to the controllers
	 *
	 *
	 * @param scene x is scene we want to change to
	 * @param state state is the model we want to save
	 * @author Katya and Aharon
	 */
	@Override
	public void setState(Model state, Scene scene) {
		super.setState(state, scene);
		headerController.setState(state, scene);
		leftMenuController.setState(state, scene);
		mainController.setState(state, scene);
	}

	/**
	 * this function updates the controllers, and due it the user screens
	 *
	 *
	 * @author Katya and Aharon
	 */
	@Override
	public void update() {
		headerController.update();
		leftMenuController.setMainWindow(mainController, model.getAccountManager().getReports());
		leftMenuController.update();
		mainController.update();
	}
}
