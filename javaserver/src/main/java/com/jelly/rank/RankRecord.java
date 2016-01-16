package com.jelly.rank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RankRecord {

	String name;
	int win;
	int time;
	int order;
	public RankRecord(String name, int win, int time, int order) {
		super();
		this.name = name;
		this.win = win;
		this.time = time;
		this.order = order;
	}
	
	public RankRecord() {
	}


	public String getName() {	
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}


	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}
	
	public ObjectNode toJson(ObjectMapper mapper) {
		ObjectNode node = mapper.createObjectNode();
		node.put("name", name);
		node.put("win", win);
		node.put("time", time);
		node.put("order", order);
		return node;
	}

}
