package com.snow.transaction;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;

import com.snow.Launch;

import lombok.Getter;
import lombok.Setter;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 1050000771208702237L;

	private @Getter @Setter TransactionType type;
	private @Getter @Setter LocalDate date;
	private @Getter @Setter String name;
	private @Getter @Setter String recipient;
	private @Getter @Setter Double amount;
	private @Getter @Setter Double savings;
	private @Getter @Setter Double savingAmount;
	private @Getter @Setter Double profit;

	private @Getter @Setter Month month;
	private @Getter @Setter int day;

	public Transaction(TransactionType type, LocalDate date, String name, String recipient, Double amount, Double savings) {
		setType(type);
		setName(name);
		setRecipient(recipient);
		setAmount(amount);
		setSavings(savings);
		handleDate(date);
		setSavingAmount(determineSavingAmount());
		setProfit(amount - getSavingAmount());

		Launch.getData().addTransaction(this);
	}

	private void handleDate(LocalDate date) {
		if (date == null)
			setDate(LocalDate.now());
		else
			setDate(date);

		setMonth(date.getMonth());
		setDay(date.getDayOfMonth());
	}

	private Double determineSavingAmount() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		double formatted = Double.parseDouble(formatter.format(savings));

		if (formatted > 100)
			formatted = 100;

		if (formatted <= 0)
			return 0.0;

		double percent = formatted / 100;

		return Double.parseDouble(formatter.format(amount * percent));
	}
}
