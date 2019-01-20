package snow.transaction;

import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;

public enum TransactionType {
	SERVICE("Service", "Income"),
	CONTRACT("Contract", "Income"),
	SAVINGS("Savings", "Income"),
	BUSINESS_EXPENSE("Business Expense", "Expense"),
	PERSONAL_EXPENSE("Personal Expense", "Expense");
	
	private @Getter @Setter String name;
	private @Getter @Setter String type;
	
	private TransactionType(String name, String type) {
		setName(name);
		setType(type);
	}
	
	private static @Getter LinkedHashMap<String, TransactionType> transactionTypes = new LinkedHashMap<>();
	
	public static void init() {
		for (TransactionType t : TransactionType.values()) {
			transactionTypes.put(t.getName(), t);
		}
	}
}
