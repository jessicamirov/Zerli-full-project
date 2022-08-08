package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.gui.ServerController;
import server.model.DBManager;
import server.model.EchoServer;
import server.model.ServerManager;

/**
 * The only screen that connect to the sql workbench
 * 
 * @author Yohan and Niv
 */
public class ServerUI extends Application {
	/* Deletes all existing data from default database and adds default values. */
	private static boolean DO_INITIALIZE = true;
	// test to delete and antoher one
	/*
	 * Stops server together with closing GUI. If false - server will continue
	 * running.
	 */
	private static boolean STOP_WITH_GUI = true;

	private static ServerManager model;

	public static void main(String args[]) throws Exception {
//		if (DO_INITIALIZE) {
//			new InitializeDB().f(new DBManager());
//		}

		if (args.length > 0) {
			/*
			 * TODO: handle arguments and start server with arguments from command line
			 * instead of defaults.
			 */
			model = new ServerManager();
			model.setServer(EchoServer.getDefaultPort());
			model.setDBManager(DBManager.getDefaultURL(), DBManager.getDefaultUsername(),
					DBManager.getDefaultPassword());
			return;
		}

		launch(args);
		/* Close server on exit of the GUI: */
		if (model != null && STOP_WITH_GUI) {
			model.close();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		model = new ServerManager();

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/server.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		ServerController controller = fxmlLoader.<ServerController>getController();
		/*
		 * Pass a model to GUI controller so it could set correct port and SQL settings:
		 */
		controller.setServerModel(model);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("gui/style.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
