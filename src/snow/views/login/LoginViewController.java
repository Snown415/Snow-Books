package snow.views.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import snow.Client;
import snow.user.User;
import snow.views.Controller;
import snow.views.View;
import sql.MySQL;

public class LoginViewController extends Controller implements Initializable {
	
	private @FXML Label message;
	private @FXML TextField username;
	private @FXML PasswordField password;
	private @FXML Button login;
	private @FXML CheckBox rememberUsername;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		currentSession = Client.getSession();
	}
	
	public void onRegister() {
		if (username.getText().length() < 3 || password.getText().length() < 3) {
			message.setText("Your username & password need to be atleast 3 characters long.");
			return;
		}
		
		if (MySQL.foundUser(username.getText())) {
			message.setText("There is already a user named '" + username.getText() + "'.");
			return;
		}
		
		MySQL.registerUser(username.getText(), password.getText());
		Client.getSession().setUser(new User(username.getText()));
		Client.getSession().setController(View.BUSINESS, true);
	}
	
	public void onLogin() throws IOException {
		if (username.getText().length() < 3 || password.getText().length() < 3) {
			message.setText("Your username & password need to be atleast 3 characters long.");
			return;
		}
		
		if (!MySQL.validLogin(username.getText(), password.getText())) {
			message.setText(MySQL.loginReturn);
			return;
		}
		
		Client.getSession().setUser(new User(username.getText()));
		Client.getSession().setController(View.MAIN, true);
	}
	
	public void onKeyPressed(Event e) throws IOException {
		KeyEvent event = (KeyEvent) e;
		
		KeyCode code = event.getCode();
		
		if (code == KeyCode.ENTER)
			onLogin();	
	}

}
