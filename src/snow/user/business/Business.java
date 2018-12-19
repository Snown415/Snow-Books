package snow.user.business;

import lombok.Getter;
import lombok.Setter;
import snow.user.User;

public class Business {
	
	private @Getter @Setter String businessName;
	private @Getter @Setter User businessOwner;
	private @Getter @Setter BusinessData businessData;
	
	public Business(String name, User owner) {
		setBusinessName(name);
		setBusinessOwner(owner);
	}
	
	public void handlePersistence() {
		
	}

}
