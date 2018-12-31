package snow.views.login;

import java.awt.SplashScreen;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import lombok.Setter;
import snow.Serialize;
import snow.session.Preferences;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.session.packet.impl.LoginPacket;
import snow.session.packet.impl.RegistrationPacket;
import snow.views.Controller;

public class LoginViewController extends Controller implements Initializable {

	private @Getter @FXML Label message;
	private @FXML TextField username;
	private @FXML PasswordField password;
	private @FXML CheckBox rememberUsername, autoLogin;

	private @Setter Preferences prefs;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Preferences prefs = Serialize.loadPreferences();
		
		session.getStage().setOnShown(e -> {
			closeSplash();
		});

		if (prefs == null) {
			prefs = new Preferences();
		}

		setPrefs(prefs);
		
		rememberUsername.setSelected(prefs.isRememberUsername());
		autoLogin.setSelected(prefs.isAutoLogin());

		if (prefs.isRememberUsername())
			username.setText(prefs.getUsername());

		processAutoLogin();
	}

	private void processAutoLogin() {
		if (prefs.isAutoLogin()) {
			password.setText(prefs.getPassword());
			
			session.getStage().setOnShown(e -> {
				closeSplash();
				
				try {
					onLogin();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public void setLoginMessage(String value) {
		message.setText(value);
	}

	public void onRegister() {
		if (username.getText().length() < 3 || password.getText().length() < 3) {
			message.setText("Your username & password need to be at least 3 characters long.");
			return;
		}

		savePreferences();
		Packet packet = new RegistrationPacket(PacketType.REGISTER, username.getText(), password.getText());
		session.getEncoder().sendPacket(true, packet);
	}

	public void onLogin() throws IOException {
		if (username.getText().length() < 3 || password.getText().length() < 3) {
			message.setText("Your username & password need to be at least 3 characters long.");
			return;
		}

		savePreferences();
		
		if (!session.getEncoder().connect()) {
			message.setText("Couldn't connect to the server\nplease try again later.");
			return;
		}

		Packet packet = new LoginPacket(PacketType.LOGIN, username.getText(), password.getText());
		session.getEncoder().sendPacket(true, packet);
	}

	public void onKeyPressed(Event e) throws IOException {
		KeyEvent event = (KeyEvent) e;

		KeyCode code = event.getCode();

		if (code == KeyCode.ENTER)
			onLogin();
	}

	public void savePreferences() {
		boolean remember = rememberUsername.isSelected();
		boolean auto = autoLogin.isSelected();
		String u, p;
		u = username.getText();
		p = password.getText();

		prefs.setRememberUsername(remember);
		prefs.setAutoLogin(auto);
		prefs.setUsername(remember || auto ? u : null);
		prefs.setPassword(auto ? p : null);
		Serialize.savePreferences(prefs);
	}
	
	private void closeSplash() {
		SplashScreen ss = SplashScreen.getSplashScreen();
		
		if (ss != null) {
			ss.close();
		}
	}

}
