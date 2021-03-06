package snow.session.packet.impl;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketProcessor;
import snow.session.packet.PacketType;
import snow.transaction.Transaction;
import snow.views.View;
import snow.views.main.transaction.TransactionTableController;

public class TransactionPacket extends Packet {

	private @Getter @Setter PacketProcessor processor;

	public TransactionPacket(PacketProcessor processor, boolean sending, Object... data) {
		super(PacketType.TRANSACTION, data);
		setProcessor(processor);

		if (sending) {
			LinkedList<Object> objs = new LinkedList<>();
			ObservableList<Object> items = FXCollections.observableArrayList(data);
			objs.addAll(items);
			objs.addFirst(processor.ordinal());
			setData(objs.toArray());
			rebuild();
		}
	}

	@Override
	public void process() {
		TransactionTableController controller;

		switch (processor) {
		case ADD:

			if ((boolean) data[2]) {
				controller = (TransactionTableController) Client.getSession().getSubviews().get(View.TRANSACTION_TABLE);
				controller.addTransaction();
			} else {
				System.out.println(data[3].toString());
			}

			break;
		case REMOVE:
			if (!(boolean) data[2])
				break;

			String id = data[3].toString();

			if (id.equals("REMOVEALLDATA")) {
				Client.getTransactions().clear();
				break;
			}
			
			controller = (TransactionTableController) Client.getSession().getSubviews().get(View.TRANSACTION_TABLE);
			controller.removeTranscation();
			break;
		case REQUEST:

			if ((boolean) data[2]) {
				new Thread(populateTransactions).start();
			}

			break;
		default:
			break;
		}

	}

	private Task<Void> populateTransactions = new Task<Void>() {

		@Override
		protected Void call() throws Exception {
			Client.getTransactions().clear();
			Object[] transactions = (Object[]) data[3];

			for (Object o : transactions) {
				Transaction t = (Transaction) o;
				Client.getTransactions().put(t.getName(), t);
			}

			return null;
		}

	};

}
