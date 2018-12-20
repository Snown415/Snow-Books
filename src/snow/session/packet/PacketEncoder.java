package snow.session.packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.Getter;
import lombok.Setter;
import snow.session.Session;

public class PacketEncoder {

	private Socket socket;
	private @Getter @Setter Session session;
	
	public PacketEncoder(Session session) {
		setSession(session);
	}

	public void connect() {
		try {
			socket = new Socket("127.0.0.1", 43595);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			return;
		} catch (IOException e) {
			System.out.println("IO Exception");
			return;
		}
	}

	public void sendPacket(boolean response, Packet packet) {
		connect();
		ObjectOutputStream out;
		ObjectInputStream in;

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			
			out.writeObject(packet.getFullPacket());

			if (response)
				session.getDecoder().handleResponse((Object[]) in.readObject());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
