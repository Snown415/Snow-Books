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
import snow.transaction.Budget;
import snow.views.main.MainViewController;

public class BudgetPacket extends Packet {

	private @Getter @Setter PacketProcessor processor;

	public BudgetPacket(PacketProcessor processor, boolean sending, Object... data) {
		super(PacketType.BUDGET, data);
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
		MainViewController controller;

		switch (processor) {
		case ADD:
			controller = (MainViewController) Client.getSession().getController();
			if ((boolean) data[2]) {
				controller.addBudget();
			} else {
				System.out.println(data[3].toString());
			}

			break;
		case REMOVE:
			if (!(boolean) data[2])
				break;

			String id = data[3].toString();

			if (id.equals("REMOVEALLDATA")) {
				Client.getBudgets().clear();
				break;
			}

			System.out.println("Removing " + id);
			Client.getBudgets().remove(id);
			break;
		case REQUEST:

			if ((boolean) data[2]) {
				new Thread(populateBudgets).start();
			}
			break;
			
		default:
			break;
		}
	}

	private Task<Void> populateBudgets = new Task<Void>() {

		@Override
		protected Void call() throws Exception {
			Client.getBudgets().clear();
			Object[] budgets = (Object[]) data[3];

			for (Object o : budgets) {
				Budget b = (Budget) o;
				Client.getBudgets().put(b.getName(), b);
			}

			return null;
		}

	};

}
