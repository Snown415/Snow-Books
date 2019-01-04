package snow.views.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.ScaleTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.session.packet.impl.LogoutPacket;
import snow.views.Controller;
import snow.views.View;

public class MainViewController extends Controller implements Initializable {
	
	private @FXML TilePane budgetPane;
	private @FXML PieChart incomeChart, expenseChart, savingsChart;
	private @FXML Pagination pagination;
	private @FXML HBox chartContainer;
	private @FXML AnchorPane glassPane;
	
	private HBox hover = new HBox(10);
	private Label hoverText = new Label("Null");

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		PieChart.Data[] data = new PieChart.Data[] { new PieChart.Data("Services", 0.45), new PieChart.Data("Products", 0.55) };
		incomeChart.getData().addAll(data);
		
		// TODO Move into separate method
		hover.setStyle("-fx-background-color: black");
		hover.setVisible(false);
		hover.setAlignment(Pos.CENTER);
		hoverText = new Label("Null");
		hoverText.setTextFill(Paint.valueOf("#FFFFFF"));
		hoverText.setAlignment(Pos.CENTER);
		hover.getChildren().add(hoverText);
		hover.setPrefHeight(25);
		hover.setPrefWidth(100);
		glassPane.getChildren().add(hover);
		glassPane.setPickOnBounds(false);
		
		for (PieChart.Data d : incomeChart.getData()) {
			Node node = d.getNode();
			
			String percent = String.format("%.2f", d.getPieValue() * 100);
			
			node.setOnMouseEntered(e -> {
				hoverText.setText(percent + "%");
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
	
	public void hotkeyCheck(Event e) throws IOException {
		KeyEvent event = (KeyEvent) e;

		KeyCode code = event.getCode();

		if (code == KeyCode.T) {
			session.addView(View.TOOLBAR);
		}
		
		
	}

	
	public void onLogout() {
		Packet packet = new LogoutPacket(PacketType.LOGOUT);
		session.getEncoder().sendPacket(true, packet);
	}

}
