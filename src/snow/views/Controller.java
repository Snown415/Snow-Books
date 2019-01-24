package snow.views;

import java.net.URL;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.Session;
import snow.views.main.MainViewController;

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
		hover.setOpacity(0.75);
		hover.setAlignment(Pos.CENTER);
		hoverText = new Label("Null");
		hoverText.setTextFill(Paint.valueOf("#FFFFFF"));
		hoverText.setAlignment(Pos.CENTER);
		hoverText.setTextAlignment(TextAlignment.CENTER);
		hover.getChildren().add(hoverText);
		hover.setPrefHeight(15);
		hover.setPrefWidth(150);
		
		if (glassPane == null) {
			System.err.println("Glasspane was invalid... Couldn't generate tooltip.");
			return;
		}
		
		glassPane.getChildren().clear();
		glassPane.getChildren().add(hover);
		glassPane.setPickOnBounds(false);
	}
	
	private @Setter Double xpos = null, ypos = null;
	
	protected void setPosition(double x, double y) {
		setXpos(x);
		setYpos(y);
	}
	
	protected void processTip(Node node, String value, double scaleto) {
		
		hover.setPrefHeight((value.split("\n").length * 15) + 20);
		
		if (hover == null || hoverText == null) {
			generateTooltip();
		}
		
		node.setOnMouseEntered(e -> {
			hoverText.setText(value);
			hover.setVisible(true);
			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
			scaleTransition.setToX(scaleto);
			scaleTransition.setToY(scaleto);
			scaleTransition.play();
		});

		node.setOnMouseMoved(e -> {
			
			if (this instanceof MainViewController) {
				hover.setLayoutX(e.getSceneX() - (hover.getWidth() / 2));
				hover.setLayoutY(e.getSceneY() - 50);
			} else {
				if (e.getSceneX() > 400) 
					hover.setLayoutX(e.getSceneX() - hover.getWidth() - 50);
				 else
					 hover.setLayoutX(e.getSceneX());
				
				hover.setLayoutY(e.getY());
			}
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
