package snow.views.main.transaction;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import snow.Client;
import snow.transaction.Transaction;
import snow.views.Controller;

public class TransactionChartController extends Controller implements Initializable {

	private @FXML LineChart<String, Double> activityChart;
	private @FXML CategoryAxis chartCategories;
	private @Getter @FXML AnchorPane glassPane;
	
	private String[] months = new DateFormatSymbols().getShortMonths();
	private ObservableList<String> categories = FXCollections.observableArrayList(months);

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		chartCategories.setCategories(categories);
		setGlassPane(glassPane);
		generateTooltip();
		plotInitialData();
	}
	
	public void refreshChart() {
		System.out.println("Refreshing Chart...");
		
		for (XYChart.Series<String, Double> d : activityChart.getData()) {
			d.getData().clear();
		}
		
		activityChart.getData().clear();
		plotInitialData();
	}

	private void plotInitialData() {
		XYChart.Series<String, Double> series = new XYChart.Series<>();
		XYChart.Data<String, Double> data;

		LinkedHashMap<String, Double> monthlyData = new LinkedHashMap<>();
		LinkedHashMap<String, List<String>> transactionData = new LinkedHashMap<>();

		// Generate Map
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

			VBox v = new VBox();

			Label value = new Label(String.valueOf(monthlyData.get(key)));
			value.setStyle("-fx-font-size:12");
			v.getChildren().add(value);

			processTip(v, sb.toString(), 1.5);
			data.setNode(v);
			series.getData().add(data);
		}

		activityChart.getData().add(series);
	}

	private double formatDouble(double value) {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return Double.parseDouble(formatter.format(value));
	}

}
