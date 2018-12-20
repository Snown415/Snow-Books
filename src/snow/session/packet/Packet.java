package snow.session.packet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class Packet {

	private @Getter @Setter PacketType type;
	private @Getter @Setter Object[] data;
	private @Getter @Setter List<Object> packetData = new LinkedList<>();
	
	public Packet(PacketType type, Object... data) {
		setType(type);
		
		packetData.add(getPacketId());
		List<Object> list = Arrays.asList(data);
		packetData.addAll(list);
		setData(packetData.toArray());
	}
	
	public abstract void process();
	
	public int getPacketId() {
		return type.getPacketId();
	}
}
