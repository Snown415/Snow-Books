package snow.session;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import snow.session.packet.Packet;
import snow.session.packet.PacketDecoder;
import snow.session.packet.PacketEncoder;
import snow.session.packet.PacketType;
import snow.session.packet.impl.LogoutPacket;
import snow.user.User;
import snow.views.Controller;
import snow.views.View;
import snow.views.ViewManager;

/**
 * Contains data of the current session; Instance is created upon launching.
 * 
 * @author Snow
 *
 */
public class Session {

	private @Getter @Setter User user;
	private @Getter @Setter Stage stage;
	private @Getter @Setter ViewManager viewManager;

	private @Getter @Setter PacketEncoder encoder;
	private @Getter @Setter PacketDecoder decoder;
	private @Getter @Setter Preferences prefs;

	private @Getter @Setter View currentView;
	private @Getter @Setter Scene scene;
	private @Getter Controller controller;
	
	// Tooltip
	protected @Getter @Setter HBox hover;
	protected @Getter @Setter Label hoverText;

	public Session(User user) {
		setViewManager(new ViewManager(this));

		if (user == null)
			setController(View.LOGIN, false);

		encoder = new PacketEncoder(this);
		decoder = new PacketDecoder(this);
	}

	public void setController(View view, boolean setView) {
		controller = ViewManager.getController(view);

		if (controller == null)
			return;

		controller.setResource(view.getResource());
		setCurrentView(view);

		if (setView)
			setView();
	}

	public Stage secondStage;

	public void addView(View view) {
		
		if (secondStage != null) {
			secondStage.close();
		}
		
		secondStage = new Stage();
		Controller controller = ViewManager.getController(view);
		FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(view.getFxml()));

		try {
			secondStage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		secondStage.initStyle(StageStyle.UTILITY);
		secondStage.setTitle(view.getTitle());
		secondStage.setResizable(view.isResizeable());
		secondStage.show();
	}

	public void removeAdditionalViews() {

	}

	public void setView() {
		if (currentView == null) {
			System.err.println("Invalid currentView");
			return;
		}

		viewManager.setLoader(new FXMLLoader(controller.getResource()));

		try {
			stage.setScene(new Scene(viewManager.getLoader().load()));
			stage.setTitle(currentView.getTitle());
			stage.setResizable(currentView.isResizeable());
			stage.setOnCloseRequest(e -> finish());
			stage.show();
			controller = viewManager.getLoader().getController();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("unchecked")
	public Node addSubview(Node node, View subview) {
		
		if (!subview.isSubview()) {
			System.err.println("This view isn't a subview; " + subview.name());
			return null;
		}
		
		if (node instanceof ListView) {
			ListView<Node> pane = (ListView<Node>) node;
			Node view;
			
			try {
				view = FXMLLoader.load(subview.getResource());
				StackPane layout = new StackPane();
				layout.setAlignment(Pos.CENTER);
				layout.getChildren().add(view);
				pane.getItems().add(layout);
				return view;
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		
		return null;
	}
	
	public void finish() {
		Packet packet = new LogoutPacket(PacketType.LOGOUT);
		encoder.sendPacket(false, packet);
		
		if (secondStage != null) {
			secondStage.close();
			secondStage = null;
		}
	}

}
