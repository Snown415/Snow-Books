package snow.views.main.subviews;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import snow.views.Controller;

public class PieViewController extends Controller implements Initializable {
	
	private @FXML PieChart piechart;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		/**
		 * 
		 * List<Data> data = new ArrayList<>(); data.addAll(incomeChart.getData());
		 * data.addAll(expenseChart.getData());
		 * 
		 * for (PieChart.Data d : data) { animateDataNode(d); }
		 * 
		 * generateTooltip();
		 * 
		 */
	}
	
	public void initializeChart(String title) {
		piechart.setTitle(title);
	}
	
	public void populateChart() {
		
	}

}
