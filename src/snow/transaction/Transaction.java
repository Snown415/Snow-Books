package snow.transaction;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 8934627816010830377L;
	
	private @Getter @Setter String type, currencyType, budget, name, recipient, email, phone;
	private @Getter @Setter LocalDate date;
	private @Getter @Setter double amount, savingPercent, savingAmount, profit;
	private @Getter @Setter String month;
	private @Getter @Setter int day;

	public Transaction(Object type, Object currency, Object budget, Object date, Object id, Object recipient, Object email, Object phone, Object amount, Object savingPercent) {
		setType((String) type);
		setCurrencyType((String) currency);
		setBudget((String) budget);
		setDate((LocalDate) date);
		setName((String) id);
		setRecipient((String) recipient);
		setEmail((String) email);
		setPhone((String) phone);
		setAmount(amount == null ? 0 : formatDouble((double) amount));
		setSavingPercent(savingPercent == null ? 0 : formatDouble((double) savingPercent));
		setSavingAmount(formatDouble(determineSavingAmount()));
		setProfit(formatDouble(getAmount() - getSavingAmount()));
		
		setMonth(((LocalDate) date).getMonth().toString());
		setDay(((LocalDate) date).getDayOfMonth());
	}
	
	private Double formatDouble(double value) {
		NumberFormat formatter = new DecimalFormat("#0.00");
		double formatted = Double.parseDouble(formatter.format(value));
		return formatted;
	}
	
	private Double determineSavingAmount() {
		double formatted = formatDouble(savingPercent);

		if (formatted > 100)
			formatted = 100;

		if (formatted <= 0)
			return 0.0;

		double percent = formatted / 100;

		return formatDouble(amount * percent);
	}
}
