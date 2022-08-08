package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
* runs the statements for the sql
 * 
 * @author Katya and Ahron
 */
public class BaseSQL {
	protected static ResultSet runQuery(Connection connection, String query) throws SQLException {
		/* Returns information from SQL database in the form of ResultSet */
		System.out.println(query);
		return connection.prepareStatement(query).executeQuery();
	}

	protected static int runUpdate(Connection connection, String query) throws SQLException {
		/*
		 * Modifies SQL without returning result. Returns 1 if modification was
		 * successful.
		 */
		System.out.println(query);
		return connection.prepareStatement(query).executeUpdate();
	}
}
