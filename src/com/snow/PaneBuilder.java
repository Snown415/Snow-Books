package com.snow;

import java.util.LinkedHashMap;

import com.snow.budget.Budget;

import lombok.Getter;

public class PaneBuilder {
	
	public static @Getter LinkedHashMap<Integer, Budget> budgets = new LinkedHashMap<>();
	
	public static void buildBudget(int index, Budget budget) {
		if (!validBudget(index, budget))
			return;
		
		Launch.getMvController().buildBudgetPane(budget);
	}
	
	private static boolean validBudget(int index, Budget budget) {
		if (budgets.containsKey(index)) {
			// Replace budget - Show & Wait
		} else {
			budgets.put(index, budget);
		}
		
		return budgets.get(index) == budget;
	}

}
