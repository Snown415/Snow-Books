package snow.session.packet.impl;

import javafx.scene.paint.Paint;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.views.View;
import snow.views.login.LoginViewController;

public class LogoutPacket extends Packet {

	public LogoutPacket(PacketType type, Object... data) {
		super(type, data);
	}

	@Override
	public void process() {
		Client.getSession().finish();
		Client.getSession().setController(View.LOGIN, true);
		Client.getBudgets().clear();
		Client.getTransactions().clear();
		LoginViewController c = (LoginViewController) Client.getSession().getController();
		
		if (Client.getSession().isTimedOut()) {
			c.getMessage().setTextFill(Paint.valueOf("red"));
			c.setLoginMessage("You have timed out due to inactivity.");
			Client.getSession().setTimedOut(false);
		}
	}

}
