package snow.views.main.transaction;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import snow.session.packet.impl.TransactionPacket;
import snow.session.packet.impl.TransactionPacket.TransactionProcesser;
import snow.user.Transaction;
import snow.views.Controller;

public class TransactionViewController extends Controller implements Initializable {

	private @FXML Button submit;
	private @FXML ComboBox<String> newType, currencyType, budgetSelection;
	private @FXML DatePicker newDate;
	private @FXML TextField transactionId, newRecipient, email, phone, newAmount;
	private @FXML Spinner<Double> newSaving;
	private @FXML Tooltip savingTip;

	private @FXML TableView<Transaction> activityTable;
	private @FXML TableColumn<Transaction, Object> type, name, amount, recipient, saving, savingAmount, profit, date,
			date_Month, date_Day;

	private String[] valueKeys = { "type", "currencytype", "budget", "date", "id", "recipient", "email", "phone",
			"amount", "saving%", "saving" };
	private LinkedHashMap<String, Object> currentValues = new LinkedHashMap<>();

	private ObservableList<String> types = FXCollections.observableArrayList("Income", "Expense");
	private ObservableList<String> currencies = FXCollections.observableArrayList("USD", "EUR");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Initiating");
		newType.setItems(types);
		currencyType.setItems(currencies);
		newType.getSelectionModel().select(0);
		currencyType.getSelectionModel().select(0);
		newSaving.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 25));

		newDate.setValue(LocalDate.now());
		savingTip.setAutoHide(false);
		savingTip.setOnShowing(e -> {
			savingTip.setText("Saving: " + calculateSavings());
		});

		handleTableFactories();
		refreshValues();
	}

	public void onSubmit() {
		int length = currentValues.size();
		Object[] array = new Object[length + 1];
		int count = 0;

		for (String key : currentValues.keySet()) {
			array[count] = currentValues.get(key);
			count++;
		}

		session.getEncoder().sendPacket(true, new TransactionPacket(TransactionProcesser.ADD_TRANSACTION, true, array));
	}

	public void addTransaction() {
		refreshValues();
		
		Transaction t = new Transaction(currentValues.get("type"), currentValues.get("currencyType"),
				currentValues.get("budget"), currentValues.get("date"), currentValues.get("id"),
				currentValues.get("recipient"), currentValues.get("email"), currentValues.get("phone"),
				currentValues.get("amount"), currentValues.get("saving%"));
		
		activityTable.getItems().add(t);
	}

	private void handleTableFactories() {
		type.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("type"));
		name.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("name"));
		amount.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("amount"));
		recipient.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("recipient"));
		saving.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("savingPercent"));
		savingAmount.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("savingAmount"));
		profit.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("profit"));
		date_Month.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("month"));
		date_Day.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("day"));
	}

	private void refreshValues() {
		currentValues.clear();
		currentValues.put(valueKeys[0], newType.getSelectionModel().getSelectedItem());
		currentValues.put(valueKeys[1], currencyType.getSelectionModel().getSelectedItem());
		currentValues.put(valueKeys[2], budgetSelection.getSelectionModel().getSelectedItem());
		currentValues.put(valueKeys[3], newDate.getValue());
		currentValues.put(valueKeys[4], transactionId.getText());
		currentValues.put(valueKeys[5], newRecipient.getText());
		currentValues.put(valueKeys[6], email.getText());
		currentValues.put(valueKeys[7], phone.getText());
		currentValues.put(valueKeys[8], formatDouble(newAmount.getText()));
		currentValues.put(valueKeys[9], formatDouble(newSaving.getValue()));
		currentValues.put(valueKeys[10], calculateSavings());
	}

	private double calculateSavings() {
		double amount = newAmount.getText().isEmpty() ? 0 : Double.parseDouble(newAmount.getText());
		double value = amount * (newSaving.getValue() / 100);
		double formattedValue = formatDouble(value);
		return formattedValue;
	}

	private double formatDouble(double value) {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return Double.parseDouble(formatter.format(value));
	}

	private double formatDouble(String value) {

		if (value == null || value.isEmpty()) {
			return 0;
		}

		return formatDouble(Double.parseDouble(value));
	}

}
