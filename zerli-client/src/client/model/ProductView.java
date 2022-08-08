package client.model;

import common.request_data.ImageFile;
import common.request_data.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * holds the product with his information
 * 
 * @author Yohan and Niv
 */
public class ProductView {
	private String name;
	private double price;
	private int discount; // Percent
	private String category;
	private Image image;

	public ProductView(Product product) {
		this.name = product.name;
		this.price = product.price;
		this.discount = product.discount; // Percent
		this.category = product.category;
		this.image = ImageFile.asImage(product.imageString);
	}

	public String getCategory() {
		return category;
	}

	public VBox getViewObject() {
		VBox vBox = new VBox();
		vBox.getStyleClass().add("product-view");
		vBox.setAlignment(Pos.CENTER);
		ImageView imageView = new ImageView();
		imageView.setImage(image);
		imageView.setFitWidth(100);
		imageView.setFitHeight(100);
		vBox.getChildren().add(imageView);
		vBox.getChildren().add(getTextName());
		vBox.getChildren().add(getPriceButton());
		return vBox;
	}

	public VBox getViewObjectForCart() {
		VBox vBox = new VBox();
		HBox hBox = new HBox();
		vBox.getStyleClass().add("product-view");
		vBox.setAlignment(Pos.CENTER);
		ImageView imageView = new ImageView();
		imageView.setImage(image);
		imageView.setFitWidth(100);
		imageView.setFitHeight(100);
		vBox.getChildren().add(imageView);
		vBox.getChildren().add(getTextName());
		vBox.getChildren().add(getTextPrice());
		hBox.getChildren().add(getPlusButton());
		hBox.getChildren().add(getAmountText());
		hBox.getChildren().add(getMinusButton());
		hBox.setSpacing(5);
		hBox.setAlignment(Pos.CENTER);
		vBox.getChildren().add(hBox);

		return vBox;
	}

	public VBox getViewObjectForWorker() {
		VBox vBox = new VBox();
		vBox.getStyleClass().add("product-view");
		vBox.setAlignment(Pos.CENTER);
		ImageView imageView = new ImageView();
		imageView.setImage(image);
		imageView.setFitWidth(100);
		imageView.setFitHeight(100);
		vBox.getChildren().add(imageView);
		vBox.getChildren().add(getTextName());
		vBox.getChildren().add(new Text("" + this.price));
		vBox.getChildren().add(new Button("Add to catalogue"));
		vBox.getChildren().add(new Button("Remove from catalogue"));
		vBox.setSpacing(3);

		return vBox;
	}

	public VBox getViewObjectForSupplier() {
		VBox vBox = new VBox();
		vBox.getStyleClass().add("product-view");
		vBox.setAlignment(Pos.CENTER);
		ImageView imageView = new ImageView();
		imageView.setImage(image);
		imageView.setFitWidth(100);
		imageView.setFitHeight(100);
		vBox.getChildren().add(imageView);
		vBox.getChildren().add(getTextName());
		vBox.getChildren().add(getTextPrice());
		vBox.getChildren().add(new ChoiceBox<Integer>());
		vBox.getChildren().add(new Button("Save discount"));
		vBox.setSpacing(3);
		return vBox;
	}

	private Text getTextName() {
		Text text = new Text(name);
		text.setTextAlignment(TextAlignment.CENTER);
		return text;
	}

	private Text getTextPrice() {
		Text text = new Text("Price: " + price);
		text.setTextAlignment(TextAlignment.CENTER);
		return text;
	}

	private Button getPriceButton() {
		Button b = new Button();
		if (discount != 0) {
			b.setText("" + (price - 0.01) + "nis (-" + discount + "%)");
		} else {
			b.setText("" + (price - 0.01) + "nis");
		}
		return b;
	}

	private Button getPlusButton() {
		Button b = new Button();
		b.setText("+");
		return b;
	}

	private Button getMinusButton() {
		Button b = new Button();
		b.setText("-");
		return b;
	}

	public String getProductName() {
		return this.name;
	}

	private Text getAmountText() {
		Text text = new Text();
		return text;
	}
}