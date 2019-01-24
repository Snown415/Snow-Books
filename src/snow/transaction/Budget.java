package snow.transaction;

import java.io.Serializable;
import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;

public class Budget implements Serializable {

	private static final long serialVersionUID = -8239095937973633337L;

	private @Getter @Setter String name;
	private @Getter @Setter String description;
	private @Getter @Setter Double target;
	private @Getter @Setter Double additions;
	private @Getter @Setter Double remainder;
	private @Getter LinkedList<Transaction> transactions;

	private transient @Getter @Setter ObservableMap<String, Data> data = FXCollections.observableHashMap();

	public Budget(String name, String desc, Double target) {
		setName(name);
		setDescription(desc);
		setTarget(target);
		setAdditions(0.0);
		setRemainder(target);
		transactions = new LinkedList<>();
	}

	public void debug() {
		System.out.println(
				"Name: " + name + " Target: " + target + " Remainder " + remainder + " Additions: " + additions);
	}

	public void validate() {
		if (transactions == null)
			transactions = new LinkedList<>();

		if (data == null) {
			data = FXCollections.observableHashMap();
		}

		buildData();
	}

	public void addTransaction(Transaction t) {
		additions += t.getSavingAmount();
		calculateRemainder();
		transactions.add(t);
		buildData();
	}

	public void removeTransaction(Transaction t) {
		if (t.getAmount() < 0)
			additions -= t.getAmount();
		else
			additions -= t.getSavingAmount();

		calculateRemainder();
		transactions.remove(t);
		buildData();
	}

	public void addDeduction(Transaction t) {
		additions += t.getAmount();
		calculateRemainder();
		transactions.add(t);
		buildData();
	}

	private void calculateRemainder() {
		setRemainder(target - additions);
	}

	public AnchorPane buildChart() {
		ObservableList<Data> data = FXCollections.observableArrayList();
		
		data.addAll(getData().values());
		AnchorPane container = new AnchorPane();
		container.setPrefSize(200, 200);

		PieChart chart = new PieChart();
		chart.setPrefSize(200, 200);
		chart.setTitle(getName());
		chart.setLabelsVisible(false);
		chart.setData(data);
		container.getChildren().setAll(chart);

		return container;
	}

	private void buildData() {
		calculateRemainder();

		if (additions >= 0) {
			double current = additions / target;
			Data savedData = new Data("Saved", current);
			data.put(savedData.getName(), savedData);

		} else {
			data.remove("Saved");
		}

		if (remainder >= 0) {
			double remain = remainder / target;
			Data remainderData = new Data("Remainder", remain);
			data.put(remainderData.getName(), remainderData);
		}
	}

}
