package snow.session.packet;

import lombok.Getter;
import lombok.Setter;
import snow.session.Session;
import snow.user.User;
import snow.views.View;
import snow.views.login.LoginViewController;

public class PacketDecoder {
	
	private @Getter @Setter Session session;
	
	public PacketDecoder(Session session) {
		setSession(session);
	}

	public void handleResponse(Object... data) {
		Integer action = (Integer) data[0];
		
		if (!PacketType.getPacketTypes().containsKey(action)) {
			System.err.println("Invalid Packet; ID: " + action + "");
			return;
		}
		
		PacketType type = PacketType.getPacketTypes().get(action);
		
		switch (type) {
		case LOGIN:
			break;
		case REGISTER:
			boolean validRegistration = (boolean) data[1];
			
			if (!validRegistration) {
				String error = (String) data[2];
				System.err.println("Invalid Registration; Error: " + error);
				
				if (session.getController() instanceof LoginViewController) {
					LoginViewController controller = (LoginViewController) session.getController();
					controller.setLoginMessage(error);
				}
				
				return;
			}
			
			String username = (String) data[2];
			User user = new User(username);
			
			getSession().setUser(user);
			getSession().setController(View.MAIN, true);
			break;
		default:
			break;
			
		}
	}

}
