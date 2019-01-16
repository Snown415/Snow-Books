package snow.views.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.session.packet.impl.LogoutPacket;
import snow.views.Controller;
import snow.views.View;

public class MainViewController extends Controller implements Initializable {

	private @FXML PieChart incomeChart, expenseChart, savingsChart;
	private @FXML ListView<Node> contentList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		session.addSubview(contentList, View.CHART);
		session.addSubview(contentList, View.ADD_TRANSACTION);
	}	

	public void hotkeyCheck(Event e) throws IOException {
		KeyEvent event = (KeyEvent) e;

		KeyCode code = event.getCode();

		if (code == KeyCode.T) {
			session.addView(View.TOOLBAR);
		}
	}

	public void onLogout() {
		Packet packet = new LogoutPacket(PacketType.LOGOUT);
		session.getEncoder().sendPacket(true, packet);
	}

}
