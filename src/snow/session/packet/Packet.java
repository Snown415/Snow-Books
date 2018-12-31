package snow.session.packet;

import lombok.Getter;
import lombok.Setter;

public abstract class Packet {

	private @Getter @Setter PacketType type;
	private @Getter @Setter int packetId;
	protected @Getter @Setter Object[] data;
	private @Getter @Setter Object[] fullPacket;
	
	public Packet(PacketType type, Object... data) {
		setType(type);
		setPacketId(type.getPacketId());
		setData(data);
		fullPacket = new Object[data.length + 1];
		System.arraycopy(data, 0, fullPacket, 1, data.length);
		fullPacket[0] = packetId;
	}
	
	public abstract void process();
}
