package snow.views.toolbar;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import snow.views.Controller;
import snow.views.View;
import snow.views.ViewManager;

public class ToolbarViewController extends Controller implements Initializable {
	
	private @FXML BorderPane mainPane;
	private @FXML ToolBar toolbar;
	private @FXML AnchorPane content;
	
	private ObservableList<Button> mainButtons = FXCollections.observableArrayList();
	
	View view = session.getCurrentView();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		populateObservables();
		handleToolbar();	
	}
	
	private void populateObservables() {
		Button[] buttons;
		
		switch (view) {
		case MAIN:
			buttons = new Button[] { new Button("Add Transaction"), new Button("Manage Budgets") };
			
			buttons[0].setOnAction(e -> {
				Controller c = ViewManager.getController(View.ADD_TRANSACTION);
				c.setResource(View.ADD_TRANSACTION.getResource());
				try {
					FXMLLoader loader = new FXMLLoader(c.getResource());
					BorderPane pane = loader.load();
					content.getChildren().addAll(pane.getChildren());
					session.secondStage.setWidth(pane.getWidth());
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			});
			
			mainButtons.addAll(buttons);
			break;
		default:
			break;
		
		}	
	}

	private void handleToolbar() {	
		
		switch (view) {
		case MAIN:
			toolbar.getItems().setAll(mainButtons);
			break;
		default:
			break;
		
		}
	}

}
