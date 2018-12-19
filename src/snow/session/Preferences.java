package snow.session;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class Preferences implements Serializable {

	private static final long serialVersionUID = -6612917409540407117L;
	
	public @Getter @Setter boolean rememberUsername;
	public @Getter @Setter String username;
	
	public Preferences() {
		setRememberUsername(false);
		setUsername(null);
	}

}
