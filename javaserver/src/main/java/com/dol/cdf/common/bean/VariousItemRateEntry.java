package com.dol.cdf.common.bean;


public class VariousItemRateEntry extends VariousItemEntry {

	private int rate;

	public VariousItemRateEntry(String type, int amount, int rate) {
		super(type, amount);
		this.rate = rate;
	}

	public int getRate() {
		return rate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + rate;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariousItemRateEntry other = (VariousItemRateEntry) obj;
		if (rate != other.rate)
			return false;
		return true;
	}

	

}
