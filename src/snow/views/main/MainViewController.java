package snow.views.main;

import static snow.views.View.ACTIVITY_CHART;
import static snow.views.View.TRANSACTION_TABLE;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
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

	private @FXML PieChart incomeChart, expenseChart, savingsChart;
	private @FXML Accordion tools;
	private @FXML @Getter AnchorPane glassPane;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setGlassPane(glassPane);

		int service = 0, contract = 0, business = 0, personal = 0;
		int totalIncome = 0, totalExpense = 0;

		for (Transaction t : Client.getTransactions().values()) {
			switch (t.getType()) {
			case "Service":
				service++;
				totalIncome += t.getAmount();
				break;
			case "Contract":
				contract++;
				totalIncome += t.getAmount();
				break;
			case "Business Expense":
				business++;
				totalExpense -= t.getAmount();
				break;
			case "Personal Expense":
				personal++;
				totalExpense -= t.getAmount();
				break;
			}
		}

		PieChart.Data[] incomeData = { new Data("Service", totalIncome / service),
				new Data("Contract", totalIncome / contract) };
		PieChart.Data[] expenseData = { new Data("Business", totalExpense / business),
				new Data("Personal", totalExpense / personal) };

		incomeChart.getData().addAll(incomeData);
		expenseChart.getData().addAll(expenseData);

		tools.expandedPaneProperty().addListener((obs, ov, nv) -> {
			if (nv == null && !incomeChart.isLegendVisible()) {

				Timeline line = new Timeline(new KeyFrame(Duration.millis(100), e -> {
					toggleLegends();
				}));

				line.setDelay(Duration.millis(200));
				line.setAutoReverse(false);
				line.setRate(1);
				line.setCycleCount(1);
				line.playFromStart();

				return;
			}

			if (nv == null) {
				return;
			}

			if (nv.isExpanded() && incomeChart.isLegendVisible()) {
				toggleLegends();
			}

		});

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
				
				pane.setOnMouseClicked(e -> {
					
				});
				
				tools.getPanes().add(pane);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<Data> data = new ArrayList<>();
		data.addAll(incomeChart.getData());
		data.addAll(expenseChart.getData());

		for (PieChart.Data d : data) {
			animateDataNode(d);
		}

		generateTooltip();
	}

	private void toggleLegends() {
		boolean value = !incomeChart.isLegendVisible();
		incomeChart.setLegendVisible(value);
		expenseChart.setLegendVisible(value);
		savingsChart.setLegendVisible(value);
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
