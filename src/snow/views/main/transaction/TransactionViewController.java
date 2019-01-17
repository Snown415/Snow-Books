package snow.views.main.transaction;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import snow.Client;
import snow.session.packet.impl.TransactionPacket;
import snow.session.packet.impl.TransactionPacket.TransactionProcesser;
import snow.transaction.Transaction;
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
	
	private @FXML LineChart<String, Double> activityChart;
	private @FXML CategoryAxis chartCategories;
	private String[] months = new DateFormatSymbols().getShortMonths();
	private ObservableList<String> categories = FXCollections.observableArrayList(months);

	private String[] valueKeys = { "type", "currencytype", "budget", "date", "id", "recipient", "email", "phone",
			"amount", "saving%", "saving" };
	private LinkedHashMap<String, Object> currentValues = new LinkedHashMap<>();

	private ObservableList<String> types = FXCollections.observableArrayList("Income", "Expense");
	private ObservableList<String> currencies = FXCollections.observableArrayList("USD", "EUR");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void handleTable() {
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
		activityTable.getItems().setAll(Client.getTransactions().values());
	}
	
	public void handleChart() {
		activityChart.setCreateSymbols(false);
		chartCategories.setCategories(categories);
		plotInitialData();
	}

	public void onSubmit() {
		refreshValues();
		int length = currentValues.size();
		Object[] array = new Object[length];
		int count = 0;

		for (String key : currentValues.keySet()) {
			array[count] = currentValues.get(key);
			count++;
		}

		session.getEncoder().sendPacket(true, new TransactionPacket(TransactionProcesser.ADD_TRANSACTION, true, array));
	}
	
	public void hotkeyCheck(Event e) throws IOException {
		KeyEvent event = (KeyEvent) e;

		KeyCode code = event.getCode();
		
		if (code == KeyCode.DELETE) {
			System.out.println("Removing all data");
			session.getEncoder().sendPacket(true, new TransactionPacket(TransactionProcesser.REMOVE_TRANSACTION, true, "REMOVEALLDATA"));
			activityTable.getItems().clear();
			Client.getTransactions().clear();
		}

	}

	public void addTransaction() {
		LinkedHashMap<String, Object> map = currentValues;
		refreshValues();

		Transaction t = new Transaction(map.get("type"), map.get("currencyType"), map.get("budget"), map.get("date"),
				map.get("id"), map.get("recipient"), map.get("email"), map.get("phone"), map.get("amount"),
				map.get("saving%"));

		activityTable.getItems().add(t);
	}

	public void addTransaction(Transaction t) {
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

		MenuItem[] items = { new MenuItem("Edit"), new MenuItem("Remove") };
		
		items[0].setOnAction(e -> {
			// TODO edit transaction
		});
		
		items[1].setOnAction(e -> {
			Transaction selection = activityTable.getSelectionModel().getSelectedItem();
			activityTable.getItems().remove(selection);
			session.getEncoder().sendPacket(true, new TransactionPacket(TransactionProcesser.REMOVE_TRANSACTION, true, selection.getName()));
		});
		
		ContextMenu menu = new ContextMenu(items);

		for (TableColumn<Transaction, ?> column : activityTable.getColumns()) {

			@SuppressWarnings("unchecked")
			TableColumn<Transaction, Object> c = (TableColumn<Transaction, Object>) column;

			c.setCellFactory(new Callback<TableColumn<Transaction, Object>, TableCell<Transaction, Object>>() {
				@Override
				public TableCell<Transaction, Object> call(TableColumn<Transaction, Object> col) {
					final TableCell<Transaction, Object> cell = new TableCell<Transaction, Object>() {
						@Override
						protected void updateItem(Object item, boolean empty) {
							super.updateItem(item, empty);
							if (empty || item == null) {
								setText(null);
							} else {
								setContextMenu(menu);
								setText(item.toString());
							}
						}
					};
					return cell;
				}
			});
		}

	}
	
	
	private void plotInitialData() {
		XYChart.Series<String, Double> series = new XYChart.Series<>();
		XYChart.Data<String, Double> data;

		LinkedHashMap<String, Double> monthlyData = new LinkedHashMap<>();
		LinkedHashMap<String, List<String>> transactionData = new LinkedHashMap<>();

		// Generate Map TODO store map in InitialData
		for (Transaction t : Client.getTransactions().values()) {
			String month = months[t.getDate().getMonthValue() - 1];
			double amount = formatDouble(t.getAmount());

			if (transactionData.containsKey(month)) {
				List<String> value = transactionData.get(month);
				
				value.add("$" + amount + " on " + t.getDate().toString());
			} else {
				LinkedList<String> list = new LinkedList<>();
				list.add("$" + amount + " on " + t.getDate().toString());
				transactionData.put(month, list);
			}

			if (monthlyData.containsKey(month)) {
				Double value = monthlyData.get(month);
				monthlyData.put(month, value + amount);
			} else
				monthlyData.put(month, amount);
		}

		// Use Map to plot points
		for (String key : monthlyData.keySet()) {
			data = new XYChart.Data<>(key, monthlyData.get(key));

			StringBuilder sb = new StringBuilder();
			for (String s : transactionData.get(key)) {
				sb.append(s + "\n");
			}

			Tooltip tip = new Tooltip(sb.toString()); // TODO make custom tooltip system
			tip.setStyle("-fx-font-size:15;");
			VBox v = new VBox();
			Tooltip.install(v, tip);

			v.getChildren().add(new Label(String.valueOf(monthlyData.get(key))));
			data.setNode(v);

			series.getData().add(data);
		}

		activityChart.getData().add(series);
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
