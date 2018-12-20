package com.snow;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

import com.snow.transaction.Transaction;

import lombok.Getter;
import lombok.Setter;

public class InitialData implements Serializable {

	private static final long serialVersionUID = 2125711619707949762L;

	public static final String PATH = System.getProperty("user.home") + "/initialData.data";

	public @Getter LinkedList<Transaction> transactionList = new LinkedList<>();

	private @Getter @Setter Integer transactions;
	private @Getter @Setter Double income;
	private @Getter @Setter Double savings;
	private @Getter @Setter Double personalSavings;
	private @Getter @Setter Double taxCut;
	private @Getter @Setter Double profit;

	public InitialData() {
		setTransactions(0);
		setIncome(0.0);
		setSavings(0.0);
		setPersonalSavings(0.0);
		setTaxCut(0.0);
		setProfit(0.0);
	}

	public void addTransaction(Transaction t) {
		transactions++;
		this.income += t.getAmount();
		this.savings += t.getSavingAmount();
		setIncome(formatDouble(getIncome()));
		setTaxCut(formatDouble(getIncome() * .20));
		setSavings(formatDouble(getSavings()));
		setPersonalSavings(formatDouble(getSavings() - getTaxCut()));
		setProfit(getIncome() - getSavings());

		transactionList.add(t);

		try {
			Serialize.saveInitialData(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleData() {
		double total = 0;
		double savings = 0;

		setTransactions(transactionList.size());

		for (Transaction t : transactionList) {
			total += t.getAmount();
			savings += t.getSavingAmount();
		}

		setIncome(formatDouble(total));
		setTaxCut(formatDouble(total * .20));
		setSavings(formatDouble(savings));
		setPersonalSavings(formatDouble(getSavings() - getTaxCut()));
		setProfit(getIncome() - getSavings());
	}

	private double formatDouble(double value) {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return Double.parseDouble(formatter.format(value));
	}
}
