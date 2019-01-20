package snow.views.main;

import static snow.views.View.ACTIVITY_CHART;
import static snow.views.View.TRANSACTION_TABLE;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.session.packet.impl.LogoutPacket;
import snow.transaction.Transaction;
import snow.views.Controller;
import snow.views.View;
import snow.views.main.transaction.TransactionViewController;

public class MainViewController extends Controller implements Initializable {

	public enum MainTool {
		TRANSACTION("Manage Transactions", TRANSACTION_TABLE), ACTIVITY("Annual Activity", ACTIVITY_CHART);

		private @Getter @Setter String title;
		private @Getter @Setter View view;

		private MainTool(String title, View subview) {
			setTitle(title);
			setView(subview);
		}
	}

	// private @FXML PieChart incomeChart, expenseChart, savingsChart;
	private @FXML Accordion tools;
	private @FXML @Getter AnchorPane glassPane;
	private @FXML HBox subviewContainer;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setGlassPane(glassPane);
		generateTooltip();
		initializeSubviews();
		initializeTools();
		generateTooltip();
	}
	
	@Override
	protected void generateTooltip() {
		
	}

	private void initializeTools() {
		for (MainTool t : MainTool.values()) {
			LinkedHashMap<View, Controller> map = Client.getSession().getSubviews();
			Node node;

			try {
				System.out.println("Loading resource " + t.getView().getResource());
				FXMLLoader loader = new FXMLLoader(t.getView().getResource());
				node = loader.load();

				TransactionViewController controller = (TransactionViewController) loader.getController();

				switch (t.getView()) {
				case ACTIVITY_CHART:
					controller.handleChart();
					break;
				case TRANSACTION_TABLE:
					controller.handleTable();
					break;
				default:
					break;

				}

				map.put(t.getView(), loader.getController());
				TitledPane pane = new TitledPane(t.getTitle(), node);

				tools.getPanes().add(pane);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void initializeSubviews() {
		Node incomeChart = session.addSubview(subviewContainer, View.PIE_CONTAINER);
		Node expenseChart = session.addSubview(subviewContainer, View.PIE_CONTAINER);

		List<Transaction> income, expense;
		income = new LinkedList<>();
		expense = new LinkedList<>();
		double totalIncome = 0;
		double totalExpense = 0;

		for (String key : Client.getTransactions().keySet()) {
			Transaction t = Client.getTransactions().get(key);
			double value = t.getAmount();
			System.out.println("Processing " + t.getName());

			if (t.getType().contains("Expense")) {
				expense.add(t);
				totalExpense += value;
			} else {
				income.add(t);
				totalIncome += value;
			}
		}

		processView(incomeChart, "Income", totalIncome, income);
		processView(expenseChart, "Expense", totalExpense, expense);
	}

	private void processView(Node view, String title, double total, List<Transaction> data) {
		HashMap<String, List<Double>> col = new LinkedHashMap<>();
		Double totalValue = 0.0;

		if (view instanceof PieChart) {
			PieChart chart = (PieChart) view;
			List<Double> list;
			chart.setTitle(title);

			for (Transaction t : data) {
				if (col.containsKey(t.getType())) {
					list = col.get(t.getType());
					list.add(t.getAmount());
				} else {
					list = new LinkedList<>();
					list.add(t.getAmount());
					col.put(t.getType(), list);
				}

				if (t.getType().contains("Expense"))
					totalValue -= t.getAmount();
				else
					totalValue += t.getAmount();
			}

			ObservableList<Data> chartData = FXCollections.observableArrayList();

			for (String key : col.keySet()) {
				String display = key;
				if (key.contains("Expense")) {
					 display = key.replace("Expense", "");
				}
				
				Data d = new PieChart.Data(display, totalValue / col.get(key).size());
				chartData.add(d);
			}
			
			chart.setData(chartData);
			
			for (Data d : chart.getData()) {
				animateDataNode(d);
			}
		}
	}

	private void animateDataNode(Data d) {
		Node node = d.getNode();
		String percent = String.format("%.2f", d.getPieValue() * 100);
		processTip(node, d.getName() + ": " + percent + "%");
	}

	public void hotkeyCheck(Event e) throws IOException {
		KeyEvent event = (KeyEvent) e;

		KeyCode code = event.getCode();

	}

	public void onLogout() {
		Packet packet = new LogoutPacket(PacketType.LOGOUT);
		session.getEncoder().sendPacket(true, packet);
	}

}
