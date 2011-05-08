package models.fields;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;

public class PriceField extends BasicField {
	
	private int amount = 1;
	
	PriceField(BigDecimal price, int amount, String name) {
		super(price.toString());
		this.amount = amount;
		if(name == null){ name = "Price"; }
		this.name = name;
	}
	
	public BigDecimal getValue(){
		return new BigDecimal(data);
	}
	
	@Override
	public String toString(){
		NumberFormat format = NumberFormat.getCurrencyInstance();		
		return name+": "+format.format(new BigDecimal(data).longValue()); 
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) throws Exception {
		if(amount > 0){
			this.amount = amount;
		}else{
			throw new Exception("Amount must be greator than 0");
		}
	}
	
	public BigDecimal getTotal(){
		return getValue().multiply(new BigDecimal(amount));
	}
}
