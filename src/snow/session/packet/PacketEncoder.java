package snow.session.packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
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

	public boolean connect(boolean test) {
		if (!test)
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
		if (!connect(false)) {
			System.err.println("Failed to send packet; The connection to the server was never made.");
			Alert a = new Alert(AlertType.ERROR, "Failed to connect");
			a.initModality(Modality.APPLICATION_MODAL);
			a.showAndWait();
			
			
			return;
		}
		
		session.setLastPacket(System.currentTimeMillis());

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
