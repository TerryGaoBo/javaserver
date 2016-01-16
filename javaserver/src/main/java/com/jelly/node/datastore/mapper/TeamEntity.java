package com.jelly.node.datastore.mapper;

public class TeamEntity {

	private String name;
	
	private byte[] val;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public byte[] getVal() {
		return this.val;
	}
	
	public void setVal(byte[] val) {
		this.val = val;
	}
}
