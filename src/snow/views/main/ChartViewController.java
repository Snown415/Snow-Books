package snow.views.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import snow.views.Controller;

public class ChartViewController extends Controller implements Initializable {

	private @FXML PieChart incomeChart;
	private @FXML AnchorPane glassPane;
	
	private HBox hover;
	private Label hoverText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		PieChart.Data[] data = new PieChart.Data[] { new PieChart.Data("Services", 0.45),
				new PieChart.Data("Products", 0.55) };

		incomeChart.getData().addAll(data);

		for (PieChart.Data d : incomeChart.getData()) {
			animateDataNode(d);
		}
		
		generateTooltip();
	}
	
	private void generateTooltip() {
		hover = new HBox(10);
		hoverText = new Label(null);
		hover.setStyle("-fx-background-color: black");
		hover.setVisible(false);
		hover.setAlignment(Pos.CENTER);
		hoverText = new Label("Null");
		hoverText.setTextFill(Paint.valueOf("#FFFFFF"));
		hoverText.setAlignment(Pos.CENTER);
		hover.getChildren().add(hoverText);
		hover.setPrefHeight(25);
		hover.setPrefWidth(175);
		glassPane.getChildren().add(hover);
		glassPane.setPickOnBounds(false);
	}
	
	private void animateDataNode(Data d) {
		Node node = d.getNode();

		String percent = String.format("%.2f", d.getPieValue() * 100);

		node.setOnMouseEntered(e -> {
			hoverText.setText(d.getName() + ": " + percent + "%");
			hover.setVisible(true);

			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
			scaleTransition.setToX(1.05f);
			scaleTransition.setToY(1.05f);
			scaleTransition.play();
		});

		node.setOnMouseMoved(e -> {
			hover.setLayoutX(e.getSceneX() - (hover.getWidth() / 2));
			hover.setLayoutY(e.getSceneY() - 100);
		});

		node.setOnMouseExited(e -> {
			hover.setVisible(false);
			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
			scaleTransition.setToX(1f);
			scaleTransition.setToY(1f);
			scaleTransition.play();
		});
	}

}
