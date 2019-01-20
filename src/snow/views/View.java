package snow.views;

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import snow.views.login.LoginViewController;
import snow.views.main.MainViewController;
import snow.views.main.subviews.PieViewController;
import snow.views.main.transaction.TransactionViewController;
import snow.views.toolbar.ToolbarViewController;

public enum View {
	
	MAIN("Main", "Home Page", MainViewController.class, false, false),
	LOGIN("Login", "Login", LoginViewController.class, false, false),
	TOOLBAR("Toolbar", "Toolbar", ToolbarViewController.class, false, false),
	PIE_CONTAINER("PieContainer", null, PieViewController.class, false, true),
	ACTIVITY_CHART("ActivityTable", null, TransactionViewController.class, false, true),
	TRANSACTION_TABLE("TransactionTable", null, TransactionViewController.class, false, true);
	
	private @Getter @Setter String name, fxml, title;
	private @Getter @Setter Class<Controller> controller;
	private @Getter @Setter URL resource;
	private @Getter @Setter boolean resizeable;
	private @Getter @Setter boolean subview;
	
	@SuppressWarnings("unchecked")
	private View(String name, String title, Class<?> controller, Boolean resize, Boolean subview) {
		setName(name);
		setTitle("Snow Books | " + title);
		setFxml(name + ".fxml");
		setController((Class<Controller>) controller);
		setResource(controller.getResource(fxml));
		setResizeable(resize);
		setSubview(subview);
	}

}
