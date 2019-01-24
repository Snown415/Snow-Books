package snow.views.main.transaction;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import lombok.Getter;
import snow.Client;
import snow.session.packet.PacketProcessor;
import snow.session.packet.impl.TransactionPacket;
import snow.transaction.Budget;
import snow.transaction.Transaction;
import snow.transaction.TransactionType;
import snow.views.Controller;
import snow.views.View;
import snow.views.main.MainViewController;

public class TransactionTableController extends Controller implements Initializable {

	private @FXML Button submit;
	private @FXML ComboBox<TransactionType> newType;
	private @FXML ComboBox<String> budgetSelection, currencyType;
	private @FXML DatePicker newDate;
	private @FXML TextField transactionId, newRecipient, email, phone, newAmount, newSaving;
	private @FXML Tooltip savingTip;
	private @Getter @FXML AnchorPane glassPane;

	private @FXML TableView<Transaction> activityTable;
	private @FXML TableColumn<Transaction, Object> type, budget, name, amount, recipient, saving, savingAmount, profit, date,
			date_Month, date_Day;

	
	private String[] valueKeys = { "type", "currencytype", "budget", "date", "id", "recipient", "email", "phone",
			"amount", "saving%", "saving" };
	private LinkedHashMap<String, Object> currentValues = new LinkedHashMap<>();

	private ObservableList<TransactionType> types = FXCollections.observableArrayList(TransactionType.values());
	private ObservableList<String> currencies = FXCollections.observableArrayList("USD", "EUR");
	
	private Transaction removedTransaction;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		glassPane.setPickOnBounds(false);
		newType.setItems(types);
		budgetSelection.getItems().add("None");

		if (Client.getBudgets().size() > 0) {
			
			populateBudgets.setOnSucceeded(e -> {
				System.out.println("Finished added budgets.");
			});
			
			populateBudgets.setOnFailed(e -> {
				System.err.println("Failed to add budgets.");
			});
			
			new Thread(populateBudgets).start();
		}

		handleTypeSelection();
		handleComboBoxes();

		newDate.setValue(LocalDate.now());
		savingTip.setAutoHide(false);
		savingTip.setOnShowing(e -> {
			savingTip.setText("Saving: " + calculateSavings());
		});

		handleTableFactories();
		refreshValues();
		activityTable.getItems().setAll(Client.getTransactions().values());
		
		setGlassPane(glassPane);
		generateTooltip();
	}

	public void validateNumeric(Event e) {
		KeyEvent keyevent = (KeyEvent) e;

		if (!StringUtils.isNumeric(keyevent.getCharacter())) {
			e.consume();
		}
	}

	public void addBudget(String budget) {
		budgetSelection.getItems().add(budget);
	}
	
	private Task<Void> populateBudgets = new Task<Void>() {

		@Override
		protected Void call() throws Exception {
			for (String b : Client.getBudgets().keySet()) {
				budgetSelection.getItems().add(b);
			}
			return null;
		}
		
	};
	
	private void handleComboBoxes() {
		currencyType.setItems(currencies);
		newType.getSelectionModel().select(0);
		budgetSelection.getSelectionModel().select(0);
		currencyType.getSelectionModel().select(0);
	}

	private void handleTypeSelection() {
		newType.setCellFactory(new Callback<ListView<TransactionType>, ListCell<TransactionType>>() {

			@Override
			public ListCell<TransactionType> call(ListView<TransactionType> param) {
				final ListCell<TransactionType> cell = new ListCell<TransactionType>() {
					@Override
					protected void updateItem(TransactionType item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setText(null);
						} else {
							setText(item.getName());
						}
					}
				};
				return cell;
			}

		});

		newType.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			if (nv == null)
				return;

			if (nv.equals(TransactionType.SAVINGS)) {
				newSaving.setText(String.valueOf(100));
				newSaving.setDisable(true);
				newRecipient.setDisable(true);
			} else if (nv.equals(TransactionType.BUSINESS_EXPENSE) || nv.equals(TransactionType.PERSONAL_EXPENSE)) {
				if (budgetSelection.getSelectionModel().getSelectedItem() != null) {
					newSaving.setText(String.valueOf(0));
					newSaving.setDisable(true);
					newRecipient.setDisable(true);
				}

			} else {
				if (newSaving.isDisable()) {
					newRecipient.setDisable(false);
					newSaving.setDisable(false);
				}
			}
		});

		budgetSelection.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			if (nv == null)
				return;

			TransactionType type = newType.getSelectionModel().getSelectedItem();

			if (type == null)
				return;

			if (type == TransactionType.BUSINESS_EXPENSE || type == TransactionType.PERSONAL_EXPENSE) {
				newSaving.setText(String.valueOf(0));
				newSaving.setDisable(true);
				newRecipient.setDisable(true);
			} else {
				if (newSaving.isDisable()) {
					newRecipient.setDisable(false);
					newSaving.setDisable(false);
				}
			}
		});

	}

	public void onSubmit() {
		refreshValues();
		int length = currentValues.size();
		Object[] array = new Object[length];
		int count = 0;
		
		if (transactionId.getText() == null || transactionId.getText().isEmpty()) {
			System.err.println("Invalid transaction Id"); // TODO add error reporting
			return;
		}

		for (String key : currentValues.keySet()) {
			array[count] = currentValues.get(key);
			count++;
		}

		session.getEncoder().sendPacket(true, new TransactionPacket(PacketProcessor.ADD, true, array));
	}

	public void addTransaction() {
		LinkedHashMap<String, Object> map = currentValues;
		refreshValues();

		Transaction t = new Transaction(map.get("type"), map.get("currencyType"), map.get("budget"), map.get("date"),
				map.get("id"), map.get("recipient"), map.get("email"), map.get("phone"), map.get("amount"),
				map.get("saving%"));
		
		Client.getTransactions().put(t.getName(), t);

		if (t.getBudget() != null && !t.getBudget().equals("None") && Client.getBudgets().containsKey(t.getBudget())) {
			Budget b = Client.getBudgets().get(t.getBudget());
			b.validate();

			if (t.getType().contains("Expense"))
				b.addDeduction(t);
			else
				b.addTransaction(t);

			MainViewController controller = (MainViewController) session.getController();
			controller.rebuildBudget(b);
		}

		activityTable.getItems().add(t);
		
		TransactionChartController chart = (TransactionChartController) session.getSubviews().get(View.ACTIVITY_CHART);
		chart.refreshChart();
	}
	
	public void removeTranscation() {
		if (removedTransaction == null)
			return;
		
		MainViewController controller = (MainViewController) session.getController();
		
		activityTable.getItems().remove(removedTransaction);
		String budget = removedTransaction.getBudget();
		
		if (budget != null && !budget.equals("None")) {
			Budget b = Client.getBudgets().get(budget);
			b.removeTransaction(removedTransaction);
			controller.rebuildBudget(b);
		}
		
		Client.getTransactions().remove(removedTransaction.getName());
		removedTransaction = null;
		
		TransactionChartController chart = (TransactionChartController) session.getSubviews().get(View.ACTIVITY_CHART);
		chart.refreshChart();
	}

	public void addTransaction(Transaction t) {
		activityTable.getItems().add(t);
	}

	private void handleTableFactories() {
		type.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("type"));
		budget.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("budget"));
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
			removedTransaction = selection;
			session.getEncoder().sendPacket(true,
					new TransactionPacket(PacketProcessor.REMOVE, true, selection.getName()));
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

	

	private void refreshValues() {
		currentValues.clear();
		currentValues.put(valueKeys[0], newType.getSelectionModel().getSelectedItem().getName());
		currentValues.put(valueKeys[1], currencyType.getSelectionModel().getSelectedItem());
		currentValues.put(valueKeys[2], budgetSelection.getSelectionModel().getSelectedItem());
		currentValues.put(valueKeys[3], newDate.getValue());
		currentValues.put(valueKeys[4], transactionId.getText());
		currentValues.put(valueKeys[5], newRecipient.getText());
		currentValues.put(valueKeys[6], email.getText());
		currentValues.put(valueKeys[7], phone.getText());
		currentValues.put(valueKeys[8], formatDouble(newAmount.getText()));
		currentValues.put(valueKeys[9], formatDouble(newSaving.getText()));
		currentValues.put(valueKeys[10], calculateSavings());
	}

	private double calculateSavings() {
		double amount = newAmount.getText().isEmpty() ? 0 : Double.parseDouble(newAmount.getText());
		double saving = newSaving.getText().isEmpty() ? 0 : Double.parseDouble(newSaving.getText());
		double value = amount * (saving / 100);
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
