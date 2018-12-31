package snow.session;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class Preferences implements Serializable {

	private static final long serialVersionUID = -6612917409540407117L;
	
	private @Getter @Setter boolean rememberUsername;
	private @Getter @Setter boolean autoLogin;
	
	private @Getter @Setter String username;
	private @Getter @Setter String password;
	
	public Preferences() {
		setRememberUsername(false);
		setAutoLogin(false);
	}
	
	public Preferences(boolean remember, boolean auto, String u, String p) {
		setRememberUsername(remember);
		setAutoLogin(auto);
		setUsername(u);
		setPassword(p);
	}

}
