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
import lombok.Getter;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.views.Controller;

public class LoginViewController extends Controller implements Initializable {
	
	private @Getter @FXML Label message;
	private @FXML TextField username;
	private @FXML PasswordField password;
	private @FXML Button login;
	private @FXML CheckBox rememberUsername;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		currentSession = Client.getSession();
	}
	
	public void setLoginMessage(String value) {
		message.setText(value);
	}
	
	public void onRegister() {
		if (username.getText().length() < 3 || password.getText().length() < 3) {
			message.setText("Your username & password need to be atleast 3 characters long.");
			return;
		}
		
		Packet p = new Packet(PacketType.REGISTER, username.getText(), password.getText());
		currentSession.getEncoder().sendPacket(true, p);
		
		//MySQL.registerUser(username.getText(), password.getText());
		//Client.getSession().setUser(new User(username.getText()));
		//Client.getSession().setController(View.BUSINESS, true);
	}
	
	public void onLogin() throws IOException {
		if (username.getText().length() < 3 || password.getText().length() < 3) {
			message.setText("Your username & password need to be atleast 3 characters long.");
			return;
		}
		
		Packet p = new Packet(PacketType.LOGIN, username.getText(), password.getText());
		currentSession.getEncoder().sendPacket(true, p);
		
		// TODO Request login
	}
	
	public void onKeyPressed(Event e) throws IOException {
		KeyEvent event = (KeyEvent) e;
		
		KeyCode code = event.getCode();
		
		if (code == KeyCode.ENTER)
			onLogin();	
	}

}
