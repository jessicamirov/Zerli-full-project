package client.model;

/**
 * saves info about user cart
 *
 * saves the product amounts and have calculate cart due to amount functions
 *
 * @author Katya
 */
public class CartItem {
	private int ammount;
	private ProductView product;

	public CartItem(ProductView product) {
		this.product = product;
		ammount = 1;
	}

	public int getAmmount() {
		return ammount;
	}

	public ProductView getProduct() {
		return product;
	}

	public void increaseAmmount() {
		ammount++;
	}

	public void decreaseAmmount() {
		if (ammount > 0)
			ammount--;
	}
}
