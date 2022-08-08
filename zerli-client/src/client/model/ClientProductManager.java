package client.model;

import java.util.List;

import common.Request;
import common.RequestType;
import common.interfaces.ProductManager;
import common.request_data.CategoriesList;
import common.request_data.Product;
import common.request_data.ProductList;
import common.request_data.ServerError;
import common.request_data.User;

/**
 * represents the client selected products
 *
 * get the current user and set his functions
 * 
 * @author Katya and Aharon
 */
public class ClientProductManager implements ProductManager {

	private EchoClient client;
	public User currentUser;
	ClientProductManager clientproductmanager;
	
	public ClientProductManager(EchoClient client) {
		this.client = client;
		this.currentUser = null;
		clientproductmanager=this;
	}
	
//////////////////////////////////
	public ClientProductManager(EchoClient client,ClientProductManager clientproductmanager) {
		this.client = client;
		this.currentUser = null;
		this.clientproductmanager=clientproductmanager;
	}

	public void setCurrentUser(User user) {
		currentUser = user;
	}

	@Override
	public List<String> getCategories() {
		Request request = client.requestServer(new Request(RequestType.GET_CATEGORIES, currentUser, null));
		if (request.requestType != RequestType.GET_CATEGORIES) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}
		return CategoriesList.fromJson(request.data).items;
	}

	/**
	 * get user products
	 *
	 * @author Katya and Aharon
	 */
	@Override
	public ProductList getProducts(String category) {
		ProductList productList = new ProductList();

		productList.category = category;
		Request request = client.requestServer(new Request(RequestType.GET_PRODUCTS, currentUser, productList));
		if (request.requestType != RequestType.GET_PRODUCTS) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}

		return ProductList.fromJson(request.data);
	}

	/**
	 * get product
	 *
	 * @author Katya and Aharon
	 */
	public Product getProduct(String name) {
		Product product = new Product();
		product.name = name;
		Request request = client.requestServer(new Request(RequestType.GET_PRODUCT, currentUser, product));
		if (request.requestType != RequestType.GET_PRODUCT) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return null;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return null;
		}

		return Product.fromJson(request.data);
	}

	/**
	 * add product to client list
	 *
	 * @author Katya and Aharon
	 */
	@Override
	public boolean addProduct(Product product) {
		return true;
	}

	/**
	 * remove product to client list
	 *
	 * @author Katya and Aharon
	 */
	@Override
	public void removeProduct(Product product) {
		/* TODO: not required via Client yet. Can implement later. */
	}

	@Override
	public boolean productToFromCatalogue(String name, boolean toFromCatalogue) {
		Product product = new Product();
		product.name = name;
		product.inCatalogue = toFromCatalogue;
		Request request = client.requestServer(new Request(RequestType.TOFROM_CATALOGUE, currentUser, product));
		if (request.requestType != RequestType.TOFROM_CATALOGUE) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return false;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}
		return true;
	}

	@Override
	public boolean setDiscount(String name, int discount) {
		Product product = new Product();
		product.name = name;
		product.discount = discount;
		Request request = client.requestServer(new Request(RequestType.SET_DISCOUNT, currentUser, product));
		if (request.requestType != RequestType.SET_DISCOUNT) {
			if (request.requestType == RequestType.FORBIDDEN) {
				return false;
			}
			ServerError error = ServerError.fromJson(request.data);
			System.out.println(error.message);
			return false;
		}
		return true;
	}
}