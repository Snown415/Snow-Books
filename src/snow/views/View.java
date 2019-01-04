package snow.views;

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import snow.views.business.BusinessViewController;
import snow.views.login.LoginViewController;
import snow.views.main.MainViewController;
import snow.views.main.TransactionViewController;
import snow.views.toolbar.ToolbarViewController;

public enum View {
	
	MAIN("Main", "Home Page", MainViewController.class, false),
	BUSINESS("Business", "Your Business", BusinessViewController.class, false),
	LOGIN("Login", "Login", LoginViewController.class, false),
	ADD_TRANSACTION("AddTransaction", "New Transaction", TransactionViewController.class, false),
	TOOLBAR("Toolbar", "Toolbar", ToolbarViewController.class, true);
	
	private @Getter @Setter String name, fxml, title;
	private @Getter @Setter Class<Controller> controller;
	private @Getter @Setter URL resource;
	private @Getter @Setter boolean resizeable;
	
	@SuppressWarnings("unchecked")
	private View(String name, String title, Class<?> controller, Boolean resize) {
		setName(name);
		setTitle("Snow Books | " + title);
		setFxml(name + ".fxml");
		setController((Class<Controller>) controller);
		setResource(controller.getResource(fxml));
		setResizeable(resize);
	}

}
