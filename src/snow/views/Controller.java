package snow.views;

import java.net.URL;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.Session;

/**
 * Abstract class for controllers
 * 
 * @author Snow
 *
 */
public abstract class Controller {
	
	@Getter @Setter
	protected URL resource;
	
	@Getter @Setter
	protected Session session = Client.getSession();
	
	@Getter @Setter
	private AnchorPane glassPane;
	
	protected HBox hover;
	protected Label hoverText;

	protected void generateTooltip() {
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
	
	protected void processTip(Node node, String value, double scaleto) {
		node.setOnMouseEntered(e -> {
			hoverText.setText(value);
			hover.setVisible(true);
			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
			scaleTransition.setToX(scaleto);
			scaleTransition.setToY(scaleto);
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
	
	protected void processTip(Node node, String value) {
		processTip(node, value, 1.05);
	}

}
