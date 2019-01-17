package snow.views.main;

import static snow.views.View.ACTIVITY_CHART;
import static snow.views.View.TRANSACTION_TABLE;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.session.packet.impl.LogoutPacket;
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		

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
				tools.getPanes().add(pane);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (PieChart.Data d : incomeChart.getData()) {
			animateDataNode(d);
		}
	}

	private void toggleLegends() {
		boolean value = !incomeChart.isLegendVisible();
		incomeChart.setLegendVisible(value);
		expenseChart.setLegendVisible(value);
		savingsChart.setLegendVisible(value);
	}

	private void animateDataNode(Data d) {
		Node node = d.getNode();

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
