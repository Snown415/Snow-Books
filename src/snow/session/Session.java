package snow.session;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
 * Contains data of the current session;
 * Instance is created upon launching. 
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
		
		controller.setResource(controller.getClass().getResource(view.getFxml()));
		setCurrentView(view);
		
		if (setView)
			setView();
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
	
	public void finish() {
		Packet packet = new LogoutPacket(PacketType.LOGOUT);
		encoder.sendPacket(false, packet);
	}

}
