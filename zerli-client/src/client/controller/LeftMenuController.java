package client.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 *
 * Gets all the tabs for the current user
 * 
 * @author Katya, Ahron
 */
public class LeftMenuController extends BaseController {
	private class ButtonHandler implements EventHandler<ActionEvent> {
		private String viewName;

		public ButtonHandler(String viewName) {
			this.viewName = viewName;
		}

		@Override
		public void handle(ActionEvent event) {
			onCategoryChange(viewName);
		}
	}

	@FXML
	private VBox menu;

	private BaseController mainController;

	private String currentViewName;
	private String[] views;

	@Override
	public void update() {
		menu.getChildren().clear();
		for (String viewName : views) {
			Button b = new Button();
			b.setText(viewName);
			b.setOnAction(new ButtonHandler(viewName));
			b.setDisable(viewName.equals(currentViewName));
			menu.getChildren().add(b);
		}
	}

	/**
	 *
	 * Sets main window of the screen
	 * 
	 * @param BaseController mainController
	 * @param String[]       views
	 * @author Katya, Ahron
	 */
	public void setMainWindow(BaseController mainController, String[] views) {
		this.mainController = mainController;
		this.views = views;
		currentViewName = views[0];
		mainController.setView(currentViewName);
	}

	/**
	 *
	 * Change the header for correct tabs
	 * 
	 * @param String category
	 * @author Katya, Ahron
	 */
	private void onCategoryChange(String category) {
		currentViewName = category;
		for (Node b : menu.getChildren()) {
			Button bb = (Button) b;
			bb.setDisable(category.equals(bb.getText()));
		}
		mainController.setView(category);
	}
}
