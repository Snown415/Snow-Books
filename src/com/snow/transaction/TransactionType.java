package com.snow.transaction;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public enum TransactionType implements Serializable {
	INCOME("Income"),
	BUSINESS_EXPENSE("Business Expense"),
	NORMAL_EXPENSE("Normal Expense"),
	SAVINGS_WITHDRAW("Savings Withdraw"),
	REFUND("Refund");
	
	private @Getter @Setter String name;
	
	private TransactionType(String name) {
		setName(name);
	}
}
