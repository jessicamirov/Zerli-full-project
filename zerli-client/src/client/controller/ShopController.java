package client.controller;

import java.util.List;

import client.model.Model;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Represents the catalog shop controller
 *
 * extends the Basecontroller and is functionality
 *
 * @author Katya and Aharon
 */
public class ShopController extends BaseController {
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
	private ShopMainController mainController;

	@Override
	public void setState(Model state, Scene scene) {
		super.setState(state, scene);
		headerController.setState(state, scene);
		leftMenuController.setState(state, scene);
		mainController.setState(state, scene);

	}

	/**
	 * Update all product the was in the cart
	 *
	 * extends the Basecontroller and is functionality
	 *
	 * @author Katya and Aharon
	 */
	@Override
	public void update() {
		headerController.update();
		List<String> categoryList = model.getProductManager().getCategories();
		String[] categories = new String[categoryList.size()];
		categoryList.toArray(categories);
		leftMenuController.setMainWindow(mainController, categories);
		leftMenuController.update();
		mainController.update();
	}
}
