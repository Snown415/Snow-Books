package snow.views.toolbar;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import snow.views.Controller;
import snow.views.View;

public class ToolbarViewController extends Controller implements Initializable {
	
	private @FXML BorderPane mainPane;
	private @FXML ToolBar toolbar;
	private @FXML AnchorPane content;
	
	private ObservableList<Button> mainButtons = FXCollections.observableArrayList();
	
	View view = session.getCurrentView();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handleToolbar();	
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
