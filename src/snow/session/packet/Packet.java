package snow.session.packet;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Packet {

	public @Getter @Setter PacketType type;
	public @Getter @Setter Object[] data;
	public @Getter @Setter Object[] fullPacket;
	
	private @Getter @Setter List<Object> packetData = new LinkedList<>();
	
	public Packet(PacketType type, Object[] data) {
		setType(type);
		setData(data);
		
		packetData.add(type.getPacketId());
		
		for (Object o : data)  {
			packetData.add(o);
		}
		
		setFullPacket(packetData.toArray());
	}
	
	public int getPacketId() {
		return type.getPacketId();
	}
}
