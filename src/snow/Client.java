package snow;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import snow.session.Session;
import snow.session.packet.PacketType;
import snow.transaction.Budget;
import snow.transaction.Transaction;
import snow.transaction.TransactionType;

/**
 * Contains the Main method for launching the application; handles all the
 * JavaFX initialization
 * 
 * @author Snow
 *
 */
public class Client extends Application {

	private @Getter @Setter static Session session;
	private static @Getter @Setter Thread mainThread;
	
	private static @Getter ObservableMap<String, Transaction> transactions = FXCollections.observableHashMap();
	private static @Getter ObservableMap<String, Budget> budgets = FXCollections.observableHashMap();

	public static void main(String[] args) {
		setMainThread(Thread.currentThread());
		setSession(new Session(null));
		PacketType.init();
		TransactionType.init();
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		session.setStage(stage);
		session.setView();
	}

}
