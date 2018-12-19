package snow.user;

import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;
import snow.user.business.Business;
import sql.MySQL;

public class User {

	private @Getter @Setter String username;
	private @Getter @Setter Integer ownerId;
	private @Getter @Setter LinkedList<Business> businessList;

	public User(String username) {
		setUsername(username);
		setOwnerId(MySQL.getOwnershipId(username));
		setBusinessList(new LinkedList<>());

		if (ownerId != 0)
			populateBusinessList();
	}

	private void populateBusinessList() {
		String list = MySQL.getBusinessList(ownerId);

		if (list == null) {
			return;
		}

		String[] ids = list.split(";");

		for (String id : ids) {
			String name = MySQL.getBusinessName(Integer.parseInt(id));

			if (name == null) {
				System.err.println("The Business identified as " + id + " didn't return a valid name.");
				continue;
			}

			Business business = new Business(name, this);
			business.handlePersistence();
		}
	}

}
