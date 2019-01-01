package snow.views.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import snow.views.Controller;

public class TransactionViewController extends Controller implements Initializable {
	
	private @FXML ComboBox<String> type, currencyType, budgetSelection;
	private @FXML TextField transactionId, recipient, email, phone, saving;
	private @FXML Label savingAmount;
	private @FXML Button submit;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}

}
