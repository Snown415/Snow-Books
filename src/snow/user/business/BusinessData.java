package snow.user.business;

import java.io.Serializable;
import java.util.LinkedList;

import lombok.Getter;

public class BusinessData implements Serializable {

	private static final long serialVersionUID = 6941832058785849236L;
	
	private @Getter LinkedList<Transaction> transactions;
	
	public BusinessData() {
		transactions = new LinkedList<>();
	}

}
