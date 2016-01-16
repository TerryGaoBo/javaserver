package com.jelly.team;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  参加军团战中军团成员
 */
public class WarMember {
	
	@JsonProperty("d")
	private String guid = "";  
	
	@JsonProperty("sc")
	private Integer score = 0; // 得分
	
	@JsonProperty("cd")
	private long time = 0; //时间戳
	
	public WarMember()
	{
	}
	
	public void setScore(Integer value)
	{
		this.score += value;
		this.time = System.currentTimeMillis();
	}
	
	public long getTimes()
	{
		return this.time;
	}
	
	public Integer getScore()
	{
		return this.score;
	}
	
	public void setGuid(String guid)
	{
		this.guid = guid;
	}
	
	public String getGuid()
	{
		return guid;
	}
}
