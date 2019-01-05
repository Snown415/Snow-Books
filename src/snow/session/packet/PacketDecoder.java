package snow.session.packet;

import lombok.Getter;
import lombok.Setter;
import snow.session.Session;
import snow.session.packet.impl.LoginPacket;
import snow.session.packet.impl.LogoutPacket;
import snow.session.packet.impl.RegistrationPacket;

public class PacketDecoder {
	
	private @Getter @Setter Session session;
	
	public PacketDecoder(Session session) {
		setSession(session);
	}

	public void handleResponse(Object... data) {
		Integer action = (Integer) data[0];
		session.setLastPacket(System.currentTimeMillis());
		
		if (!PacketType.getPacketTypes().containsKey(action)) {
			System.err.println("Invalid Packet; ID: " + action + "");
			return;
		}
		
		PacketType type = PacketType.getPacketTypes().get(action);
		
		System.out.println("Processing Packet: " + type.name());
		
		Packet packet;
		
		switch (type) {
		case LOGIN:
			packet = new LoginPacket(type, data);
			packet.process();
			break; 
			
		case REGISTER:
			packet = new RegistrationPacket(type, data);
			packet.process();
			break;
			
		case LOGOUT:
			new LogoutPacket(type, data).process();
			break;
			
		default:
			System.err.println("Invalid packet! ID: " + action);
			break;
			
		}
	}

}
