package snow.views;

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.Session;

/**
 * Abstract class for controllers
 * 
 * @author Snow
 *
 */
public abstract class Controller {
	
	@Getter @Setter
	protected URL Resource;
	
	@Getter @Setter
	protected Session session = Client.getSession();
}
