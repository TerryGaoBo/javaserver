package com.jelly.player;

import java.util.Map;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

public class MailInstance {
	
	@JsonProperty("id")
	private Integer mailId;

	@JsonProperty("h")
	private Integer hid;
	
	@JsonProperty("a")
	private String head;
	
	@JsonProperty("t")
	private Integer tid;
	
	@JsonProperty("x")
	private String text;
	
	@JsonProperty("m")
	private Integer time;
	
	@JsonProperty("w")
	private Map<String,Integer> reward = Maps.newHashMap();
	
	@JsonProperty("n")
	private Integer status;
	
	public MailInstance(){
		if(this.status == null){
			this.status = PlayerMail.MAIL_NEW;
		}
	}

	public MailInstance( int id) {
		this.status = PlayerMail.MAIL_NEW;
		this.mailId = id;
	}
	
	public Integer getId() {
		return this.mailId;
	}

	public void setId(Integer id) {
		this.mailId = id;
	}
	
	public Integer getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}


	public Integer getHid() {
		return hid;
	}

	public void setHid(int hid) {
		this.hid = hid;
	}


	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, Integer> getReward() {
		return reward;
	}

	public void setReward(Map<String, Integer> reward) {
		this.reward = reward;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	public void addReward(String type, int amount){
		reward.put(type, amount);
	}
	
	public void addRewards(VariousItemEntry[] item){
		for (VariousItemEntry variousItemEntry : item) {
			reward.put(variousItemEntry.getType(), variousItemEntry.getAmount());
		}
		
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public JsonNode toJson() {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		
		if(getId() != null){
			obj.put("id", getId());
		}
		if (getHid() != null) {
			obj.put("h", getHid());
		}else {
			obj.put("a", getHead());
		}
		if (status != null) {
			obj.put("n", status);
		}
		if (tid != null) {
			obj.put("t", tid);
		}
		if (text != null) {
			obj.put("x", text);
		}
		if (time != null) {
			obj.put("m", time);
		}
		if (!reward.isEmpty()) {
			obj.put("w", DynamicJsonProperty.convertToJsonNode(reward));
		}
		return obj;
	}
	
	public ObjectNode toShortJson() {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		if(getId() != null){
			obj.put("id", getId());
		}
		if (getHid() != null) {
			obj.put("h", getHid());
		}else {
			obj.put("a", getHead());
		}
		if (getStatus()!= null) {
			obj.put("n", getStatus());
		}
		return obj;
	}
}
