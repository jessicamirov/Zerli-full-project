package server.model;

import java.io.IOException;

/**
 * 
 * Stores 2 objects "EchoServer" and "DBManager" and provides APIs to change
 * them dynamically without restarting the program.
 * 
 * @author Katya and Ahron
 */
public class ServerManager {

	private EchoServer server;
	private DBManager manager;

	private Integer port = null;

	public void close() {
		/* Run to close all connections. */
		System.out.println("Closing all server connections.");
		manager.logOffAllUsers();
		stopServer();
	}

	public void setServer(int port) {
		stopServer();
		this.port = port;
		startServer();
	}

	public void setDBManager(String serverURL, String username, String password) {
		stopServer();
		manager = new DBManager(serverURL, username, password);
		startServer();
	}

	public DBManager getDBManager() {
		return manager;
	}

	public boolean isServerRunning() {
		return server != null && server.isListening();
	}

	public boolean isSQLCnnected() {
		return manager != null && manager.isConnected();
	}

	private void startServer() {
		if (port == null || manager == null) {
			return;
		}
		server = new EchoServer(port);
		server.setDBManager(manager);
		try {
			server.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopServer() {
		if (server == null) {
			return;
		}
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server = null;
	}
}
