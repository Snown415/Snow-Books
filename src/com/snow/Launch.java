package com.snow;

import java.io.IOException;

import com.res.main.MainViewController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class Launch extends Application {

	public @Getter @Setter static InitialData data;
	public @Getter @Setter static MainViewController mvController;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		InitialData d = Serialize.loadInitialData();

		if (d == null) {
			d = new InitialData();
			Serialize.saveInitialData(d);
		}

		setData(d);

		FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("WIP.fxml"));
		Scene scene = new Scene(loader.load());
		//scene.getStylesheets().add(MainViewController.class.getResource("Main.css").toString());
		stage.setScene(scene);
		stage.setTitle("Revenue Handler");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> finish());
		stage.show();

		MainViewController controller = loader.getController();
		controller.setData(getData());
		setMvController(controller);
	}

	private void finish() {
		try {
			Serialize.saveInitialData(data);
			System.exit(0);
		} catch (IOException e) {
			System.err.println("COULD NOT SAVE DATA!");
			e.printStackTrace();
		}
	}

}
