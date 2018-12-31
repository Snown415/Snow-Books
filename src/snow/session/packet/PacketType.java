package snow.session.packet;

import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;

public enum PacketType {
	
	LOGIN(1),
	REGISTER(2),
	LOGOUT(3);
	
	private @Getter @Setter int packetId;
	
	private PacketType(int id) {
		setPacketId(id);
	}
	
	public static void init() {
		for (PacketType type : PacketType.values()) 
			packetTypes.put(type.packetId, type);
	}
	
	public static @Getter LinkedHashMap<Integer, PacketType> packetTypes = new LinkedHashMap<>();

}
