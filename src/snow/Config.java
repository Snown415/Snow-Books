package snow;

public class Config {
	
	public static final Integer PORT = 43595; // The port used to connect to the server socket
	
	public static final boolean LIVE = false; // Run the client to connect with outside connections
	public static final boolean LOCAL = false; // Run the client to connect with localhost
	
	public static final String HOST = LIVE ? "184.91.35.220" : LOCAL ? "127.0.0.1" : "192.168.1.25"; // The server host
	

}
