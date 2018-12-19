package snow.views.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;
import snow.views.Controller;

public class MainViewController extends Controller implements Initializable {
	
	private @FXML TilePane businessPane;
	
	private String NewBusinessName;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	private Alert createRenamingPopup() {
		Alert node = new Alert(AlertType.CONFIRMATION);
		node.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		node.setTitle("Rename business"); // TODO Get business name
		node.setHeaderText("Are you sure you wish to rename your business?");
		
		TextField newName = new TextField();
		newName.setPromptText("Enter New Name");
		newName.setPrefWidth(175);
		newName.setPrefHeight(25);
		
		newName.setOnKeyReleased(e -> {
			NewBusinessName = newName.getText();
		});
		
		HBox pane = new HBox();
		pane.setAlignment(Pos.CENTER);
		pane.getChildren().add(newName);
		
		node.getDialogPane().setContent(pane);
		return node;
	}
	
	private ContextMenu createBusinessContextMenu(Node pane) {
		MenuItem[] items = { new MenuItem("View"), new MenuItem("Rename"), new MenuItem("Add Image"), new MenuItem("Delete") };
		
		items[0].setOnAction(e -> {
			// View Business Details
		});
		
		items[1].setOnAction(e -> {
			Alert alert = createRenamingPopup();
			alert.showAndWait();
			
			if (alert.getResult() == ButtonType.YES) {
				System.out.println("Change name to " + NewBusinessName);
			}
		});
		
		items[2].setOnAction(e -> {
			// Add Image
		});
		
		items[3].setOnAction(e -> {
			// Remove / Delete Business
		});
		
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(items);
		return menu;
	}
	
	public void onAddBusiness() {
		HBox pane = new HBox();
		pane.setAlignment(Pos.TOP_CENTER);
		pane.setStyle("-fx-background-color: gray");
		pane.setPadding(new Insets(10));
		ContextMenu menu = createBusinessContextMenu(pane);
		
		Label title = new Label("Your Business");
		pane.getChildren().add(title);
		
		pane.setOnMouseEntered(e -> {
			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), pane);
			scaleTransition.setToX(1.05f);
			scaleTransition.setToY(1.05f);
			scaleTransition.play();
		});

		pane.setOnMouseExited(e -> {
			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), pane);
			scaleTransition.setToX(1f);
			scaleTransition.setToY(1f);
			scaleTransition.play();
		});

		pane.setOnMouseClicked(e -> {	
			if (e.getButton() == MouseButton.SECONDARY) {
				menu.show(pane, e.getScreenX(), e.getScreenY());
			} else if (e.getButton() == MouseButton.PRIMARY) {
				menu.hide();
			}
			
		});
		
		businessPane.getChildren().add(pane);
	}

}
