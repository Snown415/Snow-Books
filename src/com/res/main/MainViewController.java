package com.res.main;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.snow.InitialData;
import com.snow.budget.Budget;
import com.snow.transaction.Transaction;
import com.snow.transaction.TransactionType;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.Duration;
import lombok.Getter;

public class MainViewController implements Initializable {

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

	private @Getter InitialData data;

	private String[] months = new DateFormatSymbols().getMonths();

	public void setData(InitialData data) {
		this.data = data;
		populateTable();
		// refreshTotals();
		plotInitialData();
	}

	public void buildBudgetPane(Budget budget) {
		try {
			FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("BudgetPane.fxml"));
			VBox node = loader.load();
			
			budgetTilePane.getChildren().add(node);

			node.setOnMouseEntered(e -> {
				ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
				scaleTransition.setToX(1.05f);
				scaleTransition.setToY(1.05f);
				scaleTransition.play();
			});

			node.setOnMouseExited(e -> {
				ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
				scaleTransition.setToX(1f);
				scaleTransition.setToY(1f);
				scaleTransition.play();
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onTransactionAdded() {
		if (!validTransaction())
			return;

		TransactionType tt = transactionType.getSelectionModel().getSelectedItem();
		String n = transactionName.getText();
		String r = transactionRecipient.getText();
		LocalDate date = datePicker.getValue();
		Double a = Double.valueOf(transactionAmount.getText());
		Double s = Double.valueOf(savingsSpinner.getValue());

		Transaction t = new Transaction(tt, date, n, r, a, s);

		activityTable.getItems().add(t);
		// refreshTotals();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		for (String month : months)
			categories.add(month);

		chartCategories.setCategories(categories);
		// savingsSpinner.setValueFactory(new
		// SpinnerValueFactory.DoubleSpinnerValueFactory(35, 100));
		//handleTransactionTypes();
		handleTableFactories();
		addTableFactory();
		

		mainPane.getSelectionModel().selectedIndexProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				if (nv.intValue() == 1) {
					for (int i = 0; i < 6; i++) {
						buildBudgetPane(null);
					}
				}
			}
		});
	}

	private void populateTable() {
		for (Transaction t : data.transactionList) {
			activityTable.getItems().add(t);
		}
	}

	private void refreshTotals() {
		totalTransaction.setText("Total Transactions: " + data.getTransactions());
		totalIncome.setText("Total Income: " + data.getIncome());
		totalSavings.setText("Total Savings: " + data.getSavings());
		personalSavings.setText("Personal Savings: " + data.getPersonalSavings());
		taxCut.setText("Tax Cut: " + data.getTaxCut());
		totalProfit.setText("Profit: " + data.getProfit());
	}

	private void plotInitialData() {
		XYChart.Series<String, Double> series = new XYChart.Series<>();
		XYChart.Data<String, Double> data;

		LinkedHashMap<String, Double> monthlyData = new LinkedHashMap<>();
		LinkedHashMap<String, List<String>> transactionData = new LinkedHashMap<>();

		// Generate Map TODO store map in InitialData
		for (Transaction t : this.data.transactionList) {
			String month = months[t.getDate().getMonthValue() - 1];

			if (transactionData.containsKey(month)) {
				List<String> value = transactionData.get(month);
				value.add("$" + t.getAmount() + " on " + t.getDate().toString());
			} else {
				LinkedList<String> list = new LinkedList<>();
				list.add("$" + t.getAmount() + " on " + t.getDate().toString());
				transactionData.put(month, list);
			}

			if (monthlyData.containsKey(month)) {
				Double value = monthlyData.get(month);
				monthlyData.put(month, value + t.getAmount());
			} else
				monthlyData.put(month, t.getAmount());
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

	private boolean validTransaction() {
		TransactionType tt = transactionType.getSelectionModel().getSelectedItem();

		if (tt == null) {
			sendError("Invalid Transaction Type Selected", "Please select a transaction type to continue.");
			return false;
		}

		if (transactionName.getText().isEmpty()) {
			sendError("Invalid Transaction Name", "Please supply a transaction name.");
			return false;
		}

		if (transactionAmount.getText().isEmpty()) {
			sendError("Invalid Transaction Amount", "Please supply a transaction amount.");
			return false;
		}

		if (transactionRecipient.getText().isEmpty()) {
			sendError("Invalid Transaction Recipient", "Please supply a recipient.");
			return false;
		}

		if (savingsSpinner.getValue() == null || savingsSpinner.getValue().isNaN()) {
			sendError("Invalid Saving %", "Please supply a value of 35 - 100");
			return false;
		}

		return true;
	}

	private void handleTransactionTypes() {
		for (TransactionType type : TransactionType.values())
			transactionType.getItems().add(type);

		Callback<ListView<TransactionType>, ListCell<TransactionType>> cellFactory = new Callback<ListView<TransactionType>, ListCell<TransactionType>>() {
			@Override
			public ListCell<TransactionType> call(ListView<TransactionType> l) {
				return new ListCell<TransactionType>() {
					@Override
					protected void updateItem(TransactionType type, boolean empty) {
						super.updateItem(type, empty);
						if (type == null || empty) {
							setGraphic(null);
						} else {
							setText(type.getName());
						}
					}
				};
			}
		};

		transactionType.setCellFactory(cellFactory);
		transactionType.setButtonCell(cellFactory.call(null));
	}

	private void handleTableFactories() {
		type.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("type"));
		name.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("name"));
		amount.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("amount"));
		recipient.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("recipient"));
		saving.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("savings"));
		savingAmount.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("savingAmount"));
		profit.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("profit"));
		date_Month.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("month"));
		date_Day.setCellValueFactory(new PropertyValueFactory<Transaction, Object>("day"));
	}

	private void addTableFactory() {
		MenuItem[] items = { new MenuItem("Delete") };

		items[0].setOnAction(e -> {
			if (deleteTransaction()) {
				Transaction t = activityTable.getSelectionModel().getSelectedItem();
				data.getTransactionList().remove(t);
				data.handleData();
				activityTable.getItems().clear();
				activityChart.getData().clear();

				plotInitialData();
				populateTable();
				refreshTotals();
			}
		});

		ContextMenu options = new ContextMenu();
		options.getItems().addAll(items);

		activityTable.setRowFactory(tv -> {
			TableRow<Transaction> cell = new TableRow<Transaction>() {
				@Override
				protected void updateItem(Transaction item, boolean empty) {
					super.updateItem(item, empty);

					if (!empty) {
						// setText(item.getName());
						setContextMenu(options);
					}
				}
			};

			return cell;
		});
	}

	private boolean deleteTransaction() {

		Transaction t = activityTable.getSelectionModel().getSelectedItem();

		Alert c = new Alert(AlertType.CONFIRMATION);
		c.setTitle("Deleting Transaction");
		c.setHeaderText("You're about to delete " + t.getName());
		c.setContentText("Are you sure you wish to delete " + t.getName());
		c.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		c.showAndWait();

		return c.getResult() == ButtonType.YES;
	}

	private void sendError(String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error!");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.show();
	}

}
