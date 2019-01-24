package snow.views.main;

import static snow.views.View.ACTIVITY_CHART;
import static snow.views.View.TRANSACTION_TABLE;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketProcessor;
import snow.session.packet.PacketType;
import snow.session.packet.impl.BudgetPacket;
import snow.session.packet.impl.LogoutPacket;
import snow.transaction.Budget;
import snow.views.Controller;
import snow.views.View;
import snow.views.main.transaction.TransactionTableController;

public class MainViewController extends Controller implements Initializable {
	
	private @FXML Accordion tools;
	private @FXML @Getter AnchorPane glassPane;
	private @FXML HBox subviewContainer;
	private @FXML Label welcome;

	private LinkedHashMap<String, PieChart> budgetCharts = new LinkedHashMap<>();
	private Object[] currentBudget = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		welcome.setText("Welcome, " + session.getUser().getUsername() + "!");
		setGlassPane(glassPane);
		generateTooltip();
		initializeTools();
		generateTooltip();

		for (Budget b : Client.getBudgets().values()) {
			buildBudget(b);
			b.debug();
		}
	}

	private void initializeTools() {
		for (MainTool t : MainTool.values()) {
			LinkedHashMap<View, Controller> map = Client.getSession().getSubviews();
			Node node;

			try {
				System.out.println("Loading resource " + t.getView().getResource());
				FXMLLoader loader = new FXMLLoader(t.getView().getResource());
				node = loader.load();
				map.put(t.getView(), loader.getController());
				TitledPane pane = new TitledPane(t.getTitle(), node);
				tools.getPanes().add(pane);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void onAddBudget() {
		VBox container = new VBox();
		container.setSpacing(5);
		TextField name = new TextField();
		name.setPromptText("Budget Name");
		TextArea desc = new TextArea();
		desc.setPromptText("Budget Description (Optional)");

		TextField amount = new TextField();
		amount.setPromptText("Budget Goal");

		amount.setOnKeyTyped(e -> {
			KeyEvent event = (KeyEvent) e;

			if (!StringUtils.isNumeric(event.getCharacter()) && !event.getCharacter().equals(".")) {
				e.consume();
			}
		});

		container.getChildren().addAll(name, amount, desc);

		Dialog<Object[]> a = new Dialog<>();
		a.setTitle("Add Budget");
		a.setHeaderText("Build your own budget to keep finances in line.");
		a.getDialogPane().setContent(container);

		a.setResultConverter(
				rc -> rc == ButtonType.FINISH ? new Object[] { name.getText(), desc.getText(), amount.getText() }
						: null);

		a.getDialogPane().getButtonTypes().setAll(ButtonType.FINISH, ButtonType.CANCEL);

		a.showAndWait().ifPresent(data -> {
			if (data == null)
				return;

			data[2] = Double.parseDouble((String) data[2]);

			currentBudget = data;

			session.getEncoder().sendPacket(true, new BudgetPacket(PacketProcessor.ADD, true, data));
		});

	}

	public void addBudget() {
		String name = (String) currentBudget[0];
		String desc = (String) currentBudget[1];
		Double amount = (Double) currentBudget[2];
		
		TransactionTableController tvc = (TransactionTableController) session.getSubviews().get(View.TRANSACTION_TABLE);
		
		if (tvc != null) 
			tvc.addBudget(name);

		Budget b = new Budget(name, desc, amount);
		Client.getBudgets().put(name, b);
		buildBudget(b);
	}

	public void rebuildBudget(Budget b) {
		if (!budgetCharts.containsKey(b.getName())) {
			System.err.println("Invalid budget name: " + b.getName());
			return;
		}

		PieChart chart = budgetCharts.get(b.getName());
		int index = subviewContainer.getChildren().indexOf(chart.getParent());
		buildBudget(b, index);
	}

	private void buildBudget(Budget b) {
		buildBudget(b, -1);
	}

	private void buildBudget(Budget b, int index) {
		b.validate();

		AnchorPane container = b.buildChart();
		PieChart chart = (PieChart) container.getChildren().get(0);

		for (Data d : chart.getData()) {
			animateDataNode(d);
		}

		budgetCharts.put(b.getName(), chart);

		if (index != -1) {
			subviewContainer.getChildren().set(index, container);
		} else {
			subviewContainer.getChildren().add(container);
		}
	}

	private void animateDataNode(Data d) {
		Node node = d.getNode();
		
		if (d.getName().equals("Saved")) {
			node.setStyle("-fx-pie-color: #088A08;");
		} else {
			node.setStyle("-fx-pie-color: #610B0B;");
		}
		
		String percent = String.format("%.2f", d.getPieValue() * 100);
		processTip(node, d.getName() + ": " + percent + "%");
	}

	public void onLogout() {
		Packet packet = new LogoutPacket(PacketType.LOGOUT);
		session.getEncoder().sendPacket(true, packet);
	}
	
	public enum MainTool {
		TRANSACTION("Manage Transactions", TRANSACTION_TABLE), ACTIVITY("Annual Activity", ACTIVITY_CHART);

		private @Getter @Setter String title;
		private @Getter @Setter View view;

		private MainTool(String title, View subview) {
			setTitle(title);
			setView(subview);
		}
	}
}
