package snow.session.packet.impl;

import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.views.View;

public class LogoutPacket extends Packet {

	public LogoutPacket(PacketType type, Object... data) {
		super(type, data);
	}

	@Override
	public void process() {
		Client.getSession().setController(View.LOGIN, true);
	}

}
