package snow.user;

import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;
import snow.user.business.Business;

public class User {

	private @Getter @Setter String username;
	private @Getter @Setter Integer ownerId;
	private @Getter @Setter LinkedList<Business> businessList;

	public User(String username) {
		setUsername(username);
		setBusinessList(new LinkedList<>());
	}

}
