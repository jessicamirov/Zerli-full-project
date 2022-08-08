package client.controller;

import java.util.List;

import client.model.ProductView;
import common.request_data.Product;
import common.request_data.ProductList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * ShopMainController represent the catalog screen. creates the items which be
 * displayed on catalog table
 * 
 * @author Katya, Aharon
 */
public class ShopMainController extends BaseController {
	@FXML
	Text mainHeader;
	@FXML
	GridPane content;
	@FXML
	ScrollPane scrollPane;

	private static int NUM_COLUMNS = 3;
	private static int NUM_PER_PAGE = 6;
	private static int START_INDEX = 0;

	@Override
	public void update() {
	}

	private int getNumColumns() {
		return NUM_COLUMNS;
	}

	/**
	 * Represents the product for the catalog
	 *
	 * @param String view
	 * @author Katya and Aharon
	 */
	@Override
	public void setView(String view) {
		scrollPane.setContent(content);
		mainHeader.setText(view + ":");
		ProductList products = model.getProductManager().getProducts(view);
		int numColumns = getNumColumns();
		int currentColumn = 0;
		int currentRow = 0;
		content.getChildren().clear();
		content.getColumnConstraints().clear();
		content.getRowConstraints().clear();
		for (Product p : products.items) {
			if (p.inCatalogue) {
				ProductView productView = new ProductView(p);
				VBox pr = productView.getViewObject();
				Button b = (Button) pr.getChildren().get(2);
				b.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						model.getCartManager().addToCart(productView.getProductName());

					}
				});
				content.add(pr, currentColumn, currentRow);
				currentColumn = (currentColumn + 1) % numColumns;
				if (currentColumn == 0) {
					currentRow += 1;
				}
			}
		}

	}

}