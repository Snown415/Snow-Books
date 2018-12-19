package snow.views.business;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import snow.user.business.Transaction;
import snow.user.business.TransactionType;
import snow.views.Controller;

public class BusinessViewController extends Controller implements Initializable {

	private @FXML Label welcomeLabel, totalTransaction, totalIncome, totalSavings, personalSavings, taxCut, totalProfit;
	private @FXML ComboBox<TransactionType> transactionType;
	private @FXML TextField transactionName, transactionAmount, transactionRecipient;
	private @FXML DatePicker datePicker;
	private @FXML Spinner<Double> savingsSpinner;
	private @FXML Button addTransaction;

	private @FXML TableView<Transaction> activityTable;
	private @FXML TableColumn<Transaction, Object> type, name, amount, recipient, saving, savingAmount, profit, date,
			date_Month, date_Day;

	private @FXML ListView<String> activityList;
	private @FXML TilePane budgetTilePane;
	private @FXML TabPane mainPane;

	private @FXML LineChart<String, Double> activityChart;
	private @FXML CategoryAxis chartCategories;
	private ObservableList<String> categories = FXCollections.observableArrayList();


	private String[] months = new DateFormatSymbols().getMonths();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		for (String month : months)
			categories.add(month);

		chartCategories.setCategories(categories);
	
	}
}
