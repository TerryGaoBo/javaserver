package com.jelly.node.datastore.mapper;

import java.sql.Timestamp;

public class Pay {
	private Timestamp logTime;
	private String guid;
	private String userId;
	private String channel;
	private int status;
	private String itemId;
	private String orderId;
	private int rmb;
	private int exchange;
	private int bouns;
	private String param;
	
	public Pay() {
		this.logTime = new Timestamp(System.currentTimeMillis());
	}
	
	public Timestamp getLogTime() {
		return logTime;
	}
	public void setLogTime(Timestamp logTime) {
		this.logTime = logTime;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getRmb() {
		return rmb;
	}
	public void setRmb(int rmb) {
		this.rmb = rmb;
	}
	public int getExchange() {
		return exchange;
	}
	public void setExchange(int exchange) {
		this.exchange = exchange;
	}
	public int getBouns() {
		return bouns;
	}
	public void setBouns(int bouns) {
		this.bouns = bouns;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	

}
