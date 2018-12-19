package snow.views;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.Session;

/**
 * Handles the changing of views within the 
 * JavaFX application
 * 
 * @author Snow
 *
 */
public class ViewManager {
	
	public static Controller getController(View view) {
		
		try {
			return view.getController().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private @Getter @Setter Session session;
	private @Getter @Setter FXMLLoader loader;
	
	public ViewManager(Session session) {
		setSession(session);
		setLoader(new FXMLLoader());
	}
	
	public void sendLogin() throws IOException {
		Client.getSession().setController(View.LOGIN, true);
	}

}
