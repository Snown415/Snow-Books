package snow.session;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
	private @Getter LinkedHashMap<View, Controller> subviews = new LinkedHashMap<>();

	private @Getter @Setter long lastPacket;
	private @Getter @Setter boolean timedOut;

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

	private ScheduledExecutorService userService;

	private Runnable userTask = new Runnable() {

		@Override
		public void run() {
			int minutes = prefs.getTimeout();
			
			if (System.currentTimeMillis() - lastPacket >= (minutes * 60 * 1000)) {
				System.out.println("Timing out...");
				Platform.runLater(timeoutTask);
			}
		}
	};
	
	private Runnable timeoutTask = new Runnable() {

		@Override
		public void run() {
			setTimedOut(true);
			userService.shutdownNow();
			Packet packet = new LogoutPacket(PacketType.LOGOUT);
			encoder.sendPacket(true, packet);
		}
		
	};

	public void startSession() {
		System.out.println("Starting session");
		userService = Executors.newScheduledThreadPool(1);
		userService.scheduleAtFixedRate(userTask, 0, 1, TimeUnit.SECONDS);
	}

	public void setView() {
		if (currentView == null) {
			System.err.println("Invalid currentView");
			return;
		}

		viewManager.setLoader(new FXMLLoader(controller.getResource()));

		try {
			stage.setScene(new Scene(viewManager.getLoader().load()));
			setScene(stage.getScene());

			// Reset timeout timer
			scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
				lastPacket = System.currentTimeMillis();
				System.out.println("Clicked");
			});

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
		System.out.println("Finished...");
		
		if (userService != null) {
			userService.shutdownNow();
		}

		if (secondStage != null) {
			secondStage.close();
			secondStage = null;
		}
	}

}
