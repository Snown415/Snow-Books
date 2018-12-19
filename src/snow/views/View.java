package snow.views;

import lombok.Getter;
import lombok.Setter;
import snow.views.business.BusinessViewController;
import snow.views.login.LoginViewController;
import snow.views.main.MainViewController;

public enum View {
	
	MAIN("Main", "Home Page", MainViewController.class, false),
	BUSINESS("Business", "Your Business", BusinessViewController.class, false),
	LOGIN("Login", "Login", LoginViewController.class, false);
	
	private @Getter @Setter String name, fxml, title;
	private @Getter @Setter Class<Controller> controller;
	private @Getter @Setter boolean resizeable;
	
	@SuppressWarnings("unchecked")
	private View(String name, String title, Class<?> controller, Boolean resize) {
		setName(name);
		setTitle("Snow Books | " + title);
		setFxml(name + ".fxml");
		setController((Class<Controller>) controller);
		setResizeable(resize);
	}

}
