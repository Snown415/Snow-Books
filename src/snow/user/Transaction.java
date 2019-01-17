package snow.user;

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

	public Transaction(Object type, Object currency, Object budget, Object date, Object name, Object recipient, Object email, Object phone, Object amount, Object savingPercent) {
		setType((String) type);
		setCurrencyType((String) currency);
		setBudget((String) budget);
		setDate((LocalDate) date);
		setName((String) name);
		setRecipient((String) recipient);
		setEmail((String) email);
		setPhone((String) phone);
		setAmount(amount == null ? 0 : (double) amount);
		setSavingPercent(savingPercent == null ? 0 : (double) savingPercent);
		setSavingAmount(determineSavingAmount());
		setProfit(getAmount() - getSavingAmount());
		
		setMonth(((LocalDate) date).getMonth().toString());
		setDay(((LocalDate) date).getDayOfMonth());
	}
	
	private Double determineSavingAmount() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		double formatted = Double.parseDouble(formatter.format(savingPercent));

		if (formatted > 100)
			formatted = 100;

		if (formatted <= 0)
			return 0.0;

		double percent = formatted / 100;

		return Double.parseDouble(formatter.format(amount * percent));
	}
}
