package client;

import client.controller.BaseController;
import client.model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientUI extends Application {

	@Override// this is a test to delete
	public void start(Stage primaryStage) {
		Model model = new Model(primaryStage);
		for (String name : model.getWindowNames()) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/" + name + ".fxml"));
				Parent root = (Parent) fxmlLoader.load();
				BaseController controller = fxmlLoader.<BaseController>getController();
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
				model.addWindowController(name, controller, scene);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		model.setScene();
	}

	public static void main(String[] args) {
		if (args.length > 1) {
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			System.out.println("TODO: connect to host " + host + " with port " + port);
		}
		launch();
	}
}
