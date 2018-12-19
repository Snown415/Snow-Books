package snow;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import snow.session.Session;
import sql.MySQL;

/**
 * Contains the Main method for launching the application;
 * handles all the JavaFX initialization
 * 
 * @author Snow
 *
 */
public class Client extends Application {
	
	private @Getter @Setter static Session session;
	
	public static void main(String[] args) {
		setSession(new Session(null));
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		session.setStage(stage);
		session.setView();
		MySQL.init();
	}

}
