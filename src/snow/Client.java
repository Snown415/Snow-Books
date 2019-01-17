package snow;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import snow.session.Session;
import snow.session.packet.PacketType;
import snow.transaction.Transaction;

/**
 * Contains the Main method for launching the application;
 * handles all the JavaFX initialization
 * 
 * @author Snow
 *
 */
public class Client extends Application {
	
	private @Getter @Setter static Session session;
	
	private static @Getter ObservableMap<String,Transaction> transactions = FXCollections.observableHashMap();
	
	public static void main(String[] args) {
		setSession(new Session(null));
		PacketType.init();
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		session.setStage(stage);
		session.setView();
	}

}
