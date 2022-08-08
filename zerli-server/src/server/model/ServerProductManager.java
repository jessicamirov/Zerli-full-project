package server.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Role;
import common.interfaces.ProductManager;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.WeakPassword;
import common.request_data.ImageFile;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.User;

/**
 * Handles all products in db
 * 
 * @author Katya and Ahron
 */
public class ServerProductManager extends BaseSQL implements ProductManager {
	/* SQL SCHEMA: */
	private static String TABLE_NAME = "products";
	private static String PRODUCT_NAME = "name";
	private static String CATEGORY = "category";
	private static String PRICE = "price";
	private static String DISCOUNT = "discount";
	private static String IMAGE = "image";
	private static String INCATALOGUE = "inCatalogue";
	/* TODO: Define fields */

	private static String VARCHAR = " varchar(255)";
	private static String MEDIUMTEXT = " MEDIUMTEXT";
	private static String SMALLINT = " smallint";
	private static String DOUBLE = " double";
	private static String BOOLEAN = " boolean";

	/* End SQL SCHEMA */

	private User requestedBy;
	private Connection connection;

	private static Map<String, List<Product>> productsMock = null;
	private static Product product = null;

	private static String[] categories = { ALL_CATEGORY, "Bouquet", "Wedding", "Funeral", "Flowerpot", "Retail" };

	public ServerProductManager(User requestedBy, Connection connection) {
		this.requestedBy = requestedBy;
		this.connection = connection;

		/* Mock implementation, replace with SQL. */
		if (productsMock == null) {
			productsMock = new HashMap<String, List<Product>>();
			for (String category : getCategories()) {
				productsMock.put(category, new ArrayList<Product>());
			}
		}
	}

	/**
	 * reset all products in the db
	 * 
	 * @param Connection connection
	 * @author Katya and Ahron
	 */
	public static void resetProducts(Connection connection) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "CREATE TABLE " + TABLE_NAME + " (" + PRODUCT_NAME + VARCHAR + ", " + CATEGORY + VARCHAR + ", " + PRICE
				+ DOUBLE + ", " + DISCOUNT + SMALLINT + ", " + IMAGE + MEDIUMTEXT + ", " + INCATALOGUE + BOOLEAN
				+ ", PRIMARY KEY (" + PRODUCT_NAME + "));";
		try {
			runUpdate(connection, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO: Create table with schema - same as UserManager
	}

	@Override
	public List<String> getCategories() {
		return Arrays.asList(categories);
	}

	/**
	 * gets all products from db
	 * 
	 * @param String category
	 * @return ProductList
	 * @author Katya and Ahron
	 */
	@Override
	public ProductList getProducts(String category) {
		ProductList productList = new ProductList();
		productList.category = category;
		productList.items = new ArrayList<Product>();

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + CATEGORY + "=" + "'" + category + "'" + ";";
		if (category.equals("All")) {
			query = "SELECT * FROM " + TABLE_NAME + ";";
		}
		try {
			ResultSet rs = runQuery(connection, query);
			while (rs.next()) {
				Product product = new Product();
				product.name = rs.getString(PRODUCT_NAME);

				product.category = rs.getString(CATEGORY);
				// System.out.println("YOU ASKED FOR " + rs.getString(CATEGORY));
				product.price = rs.getDouble(PRICE);
				product.discount = rs.getInt(DISCOUNT);
				product.imageString = rs.getString(IMAGE);
				product.inCatalogue = (rs.getInt(INCATALOGUE) != 0 ? true : false);
				productList.items.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return productList;
	}

	/**
	 * get singular product from the db
	 * 
	 * @param String name
	 * @return Product
	 * @author Katya and Ahron
	 */
	public Product getProduct(String name) {

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + PRODUCT_NAME + "='" + name + "';";
		try {
			ResultSet rs = runQuery(connection, query);
			/* name is a key, so there can be 0 or 1 objects only. */
			while (rs.next()) {
				Product product = new Product();
				product.name = rs.getString(PRODUCT_NAME);
				System.out.println("NAME IS " + product.name);
				product.category = rs.getString(CATEGORY);
				System.out.println("CATEGORY IS " + product.category);
				product.price = rs.getDouble(PRICE);
				product.discount = rs.getInt(DISCOUNT);
				product.imageString = rs.getString(IMAGE);
				product.inCatalogue = (rs.getInt(INCATALOGUE) != 0 ? true : false);

				return product;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * add product to the db
	 * 
	 * @param Product product
	 * @return boolean
	 * @author Katya and Ahron
	 */
	@Override
	public boolean addProduct(Product product) throws PermissionDenied {
		if (requestedBy.userrole != Role.SUPPORT) {
			throw new PermissionDenied();
		}
		try {
			String query = "INSERT INTO " + TABLE_NAME + " VALUES (" + "'" + product.name + "', " + "'"
					+ product.category + "', " + "'" + product.price + "', " + "'" + product.discount + "', " + "'"
					+ product.imageString + "', " + "'" + (product.inCatalogue ? 1 : 0) + "'" + ");";
			// System.out.println("QUERY TO SQL : " + query);
			runUpdate(connection, query);

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * remove user from the db
	 * 
	 * @param Product product
	 * @author Katya and Ahron
	 */
	@Override
	public void removeProduct(Product product) {
		/* Not required for mock - can be easily added for SQL. */
	}

	/**
	 * changes if product in the catalog or not in catalog
	 * 
	 * @param String name
	 * @param boolean toFromCatalogue
	 * @return boolean
	 * @author Katya and Ahron
	 */
	@Override
	public boolean productToFromCatalogue(String name, boolean toFromCatalogue) throws PermissionDenied {
		if (requestedBy.userrole != Role.WORKER) {
			throw new PermissionDenied();
		}
		try {
			String query = "UPDATE " + TABLE_NAME + " SET " + INCATALOGUE + "='" + (toFromCatalogue ? 1 : 0)
					+ "' WHERE " + PRODUCT_NAME + "='" + name + "';";
			// System.out.println("QUERY TO SQL : " + query);
			runUpdate(connection, query);

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * sets discount to a product
	 * 
	 * @param String name
	 * @param int discount
	 * @return boolean
	 * @author Katya and Ahron
	 */
	@Override
	public boolean setDiscount(String name, int discount) throws PermissionDenied {
		if (requestedBy.userrole != Role.SUPPORT) {
			throw new PermissionDenied();
		}
		try {
			String query = "UPDATE " + TABLE_NAME + " SET " + DISCOUNT + "='" + discount + "' WHERE " + PRODUCT_NAME
					+ "='" + name + "';";
			// System.out.println("QUERY TO SQL : " + query);
			runUpdate(connection, query);

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}