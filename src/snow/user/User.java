package snow.user;

import lombok.Getter;
import lombok.Setter;

public class User {

	private @Getter @Setter String username;
	private @Getter @Setter Integer ownerId;

	public User(String username) {
		setUsername(username);
	}

}
