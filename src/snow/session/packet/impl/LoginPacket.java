package snow.session.packet.impl;

import snow.Client;
import snow.session.Session;
import snow.session.packet.Packet;
import snow.session.packet.PacketProcessor;
import snow.session.packet.PacketType;
import snow.user.User;
import snow.views.View;
import snow.views.login.LoginViewController;

public class LoginPacket extends Packet {

	public LoginPacket(PacketType type, Object... data) {
		super(type, data);
	}

	@Override
	public void process() {
		Session session = Client.getSession();
		boolean validLogin = (boolean) getData()[1];
		
		if (!validLogin) {
			String error = (String) getData()[2];
			
			if (session.getController() instanceof LoginViewController) {
				LoginViewController controller = (LoginViewController) session.getController();
				controller.setLoginMessage(error);
			}
			return;
		}
		
		session.getEncoder().sendPacket(true, new TransactionPacket(PacketProcessor.REQUEST, true));
		session.getEncoder().sendPacket(true, new BudgetPacket(PacketProcessor.REQUEST, true));
		
		String username = (String) getData()[2];
		User user = new User(username);
		session.setUser(user);
		session.setController(View.MAIN, true);
		session.startSession();
	}

}
