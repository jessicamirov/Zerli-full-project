package client.controller;

import java.util.List;

import client.model.Model;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * represents the top right screen
 * 
 * @author Katya and Aharon
 */
public class CartController extends BaseController {
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
	private CartMainController mainController;

	/**
	 * extends the base controller
	 * 
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
	 * extends the base controller
	 * 
	 * @author Katya and Aharon
	 */
	@Override
	public void update() {
		headerController.update();
		leftMenuController.setMainWindow(mainController, model.getCartManager().getSteps());
		leftMenuController.update();
		mainController.update();
	}

}