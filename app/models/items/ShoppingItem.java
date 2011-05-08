package models.items;

import java.math.BigDecimal;

import javax.persistence.Embedded;

import play.data.validation.Min;

import models.fields.PriceField;

public class ShoppingItem extends BasicItem {
	@Min(0)
	public BigDecimal price = new BigDecimal(0);
	@Min(1)
	public int amount = 1;
	
}
