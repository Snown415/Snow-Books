package snow.session.packet.impl;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import snow.Client;
import snow.session.packet.Packet;
import snow.session.packet.PacketType;
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
		ADD_TRANSACTION, REMOVE_TRANSACTION, SEND_TRANSACTIONS
	}

	@Override
	public void process() {
		switch (processer) {
		case ADD_TRANSACTION:
			
			if ((boolean) data[2]) {
				TransactionViewController controller = (TransactionViewController) Client.getSession().getSubviews().get(View.TRANSACTION_TABLE);
				controller.addTransaction();
				System.out.println("Added transaction.");
			}
			
			break;
		case REMOVE_TRANSACTION:
			break;
		case SEND_TRANSACTIONS:
			break;
		default:
			break;
		}

	}

}
