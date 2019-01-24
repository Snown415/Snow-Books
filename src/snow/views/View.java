package snow.views;

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import snow.views.login.LoginViewController;
import snow.views.main.MainViewController;
import snow.views.main.transaction.TransactionChartController;
import snow.views.main.transaction.TransactionTableController;

public enum View {
	
	MAIN("Main", "Home Page", MainViewController.class, false, false),
	LOGIN("Login", "Login", LoginViewController.class, false, false),
	ACTIVITY_CHART("ActivityTable", null, TransactionChartController.class, false, true),
	TRANSACTION_TABLE("TransactionTable", null, TransactionTableController.class, false, true);
	
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
