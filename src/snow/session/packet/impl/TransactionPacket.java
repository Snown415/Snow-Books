package snow.session.packet.impl;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
import snow.transaction.Transaction;
import snow.views.View;
import snow.views.main.transaction.TransactionViewController;

public class TransactionPacket extends Packet {

	private @Getter @Setter TransactionProcesser processer;

	public TransactionPacket(TransactionProcesser processer, boolean sending, Object... data) {
		super(PacketType.TRANSACTION, data);
		setProcesser(processer);

		if (sending) {
			LinkedList<Object> objs = new LinkedList<>();
			ObservableList<Object> items = FXCollections.observableArrayList(data);
			objs.addAll(items);
			objs.addFirst(processer.ordinal());
			setData(objs.toArray());
			rebuild();
		}
	}

	public enum TransactionProcesser {
		ADD_TRANSACTION, REMOVE_TRANSACTION, REQUEST_TRANSACTIONS
	}

	@Override
	public void process() {
		TransactionViewController controller;

		switch (processer) {
		case ADD_TRANSACTION:

			if ((boolean) data[2]) {
				controller = (TransactionViewController) Client.getSession().getSubviews().get(View.TRANSACTION_TABLE);
				controller.addTransaction();
			} else {
				System.out.println(data[3].toString());
			}

			break;
		case REMOVE_TRANSACTION:
			if (!(boolean) data[2])
				break;

			String id = data[3].toString();

			if (id.equals("REMOVEALLDATA")) {
				Client.getTransactions().clear();
				break;
			}
			
			System.out.println("Removing " + id);
			Client.getTransactions().remove(id);
			break;
		case REQUEST_TRANSACTIONS:

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
				System.out.println("Adding Transaction");
			}

			return null;
		}

	};

}
