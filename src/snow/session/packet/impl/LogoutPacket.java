package snow.session.packet.impl;

import snow.session.packet.Packet;
import snow.session.packet.PacketType;

public class LogoutPacket extends Packet {

	public LogoutPacket(PacketType type, Object... data) {
		super(type, data);
	}

	@Override
	public void process() {
		// No need..
	}

}
