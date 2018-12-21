package snow.session.packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.Getter;
import lombok.Setter;
import snow.Config;
import snow.session.Session;

public class PacketEncoder {
	
	private Socket socket;
	private @Getter @Setter Session session;
	
	public PacketEncoder(Session session) {
		setSession(session);
	}

	public boolean connect() {
		System.out.println("Connecting...");
		try {
			socket = new Socket(Config.HOST, Config.PORT);
			return true;
		} catch (UnknownHostException e) {
			System.err.println("Unknown host");
		} catch (IOException e) {
			System.err.println("IO Exception");
		} catch (SecurityException e) {
			System.err.println("Security Exception");
		}
		return false;
	}

	public void sendPacket(boolean response, Packet packet) {
		if (!connect()) {
			System.err.println("Failed to send packet; The connection to the server was never made.");
			return;
		}
		
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
