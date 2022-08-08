package client.controller;

import client.model.Model;
import javafx.event.ActionEvent;
import javafx.scene.Scene;

/**
 * this is the base controller,that all the controller extends
 *
 * @author Katya Ahron
 */
public abstract class BaseController {
	protected Model model;
	protected Scene scene;

	/**
	 * saves state and scene in the controller
	 *
	 * @author Katya Ahron
	 */
	public void setState(Model model, Scene scene) {
		this.model = model;
		this.scene = scene;
	}

	/**
	 * return the current scene
	 *
	 * @author Katya Ahron
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * saves the last scene so the user can go back to the last scene
	 *
	 * @author Katya Ahron
	 */
	public void onBack(ActionEvent event) throws Exception {
		this.model.setSceneBack();
	}

	/**
	 * saves the default scene so the user can go back to the last scene
	 *
	 * @author Katya Ahron
	 */
	public void onHome(ActionEvent event) throws Exception {
		this.model.setScene();
	}

	public void setView(String view) {
	};

	public abstract void update();
}
