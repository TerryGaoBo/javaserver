package com.dol.cdf.common.bean;

public interface BaseItem {
	
	public Integer getId();

	public String getAlt();

	public Integer getCategory();

	public Integer getQuality();

	public VariousItemEntry[] getPrice();

	public VariousItemEntry[] getSell();

	public Integer getStackmax();
}
