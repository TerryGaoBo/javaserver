package com.dol.cdf.common.bean;

public class CollectItemEntry {

	String id;
	
	String name;
	
	int rate;
	
	public CollectItemEntry(String id, String name, int rate) {
		super();
		this.id = id;
		this.name = name;
		this.rate = rate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
