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

	private boolean live = true;
	private boolean local = true;
	private Socket socket;
	private @Getter @Setter Session session;
	
	public PacketEncoder(Session session) {
		setSession(session);
	}

	public void connect() {
		System.out.println("Connecting...");
		try {
			socket = new Socket(live ? "184.91.35.220" : local ? "127.0.0.1" : "192.168.1.25", 43595);
			System.out.println("Created client socket.");
			return;
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.out.println("Security Exception");
			e.printStackTrace();
		}
		
		System.err.println("Failed to open socket.");
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
