package com.dol.cdf.common.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailGiveProps {
	
	@JsonProperty("t")
	private String title;
	
	@JsonProperty("c")
	private String content;
	
	@JsonProperty("s")
	private int sendTime;
	
	@JsonProperty("r")
	private String reward;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSendTime() {
		return sendTime;
	}

	public void setSendTime(int sendTime) {
		this.sendTime = sendTime;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}  
	
	
	
	
}
