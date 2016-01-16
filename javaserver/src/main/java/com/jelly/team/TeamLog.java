package com.jelly.team;

import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.TimeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class TeamLog {
	
	@JsonProperty("ty")
	public Integer type;   // 1 军团战 ， 2 占领敌方 
	
	@JsonProperty("mn")
	public String myName;
	
	@JsonProperty("ms")
	public Integer myscore;
	
	@JsonProperty("idx")
	public Integer idx;
	
	@JsonProperty("warType")
	public Integer warType;
	
	@JsonProperty("ml")
	public List<Integer> mlist = Lists.newArrayList();
	
	@JsonProperty("w")
	public Integer win;
	
	@JsonProperty("en")
	public String eName;
	
	@JsonProperty("es")
	public Integer escore;
	
	@JsonProperty("el")
	public List<Integer> elist = Lists.newArrayList();
	
	@JsonProperty("ll")
	public Integer lv;
	
	@JsonProperty("date")
	public Integer date = TimeUtil.getCurrentTime();
	
	public TeamLog(){
	}
	
	public ObjectNode getLog()
	{
		ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
		node.put("type", type);
		node.put("myName", myName);
		node.put("myscore", myscore);
		node.put("idx", idx);
		node.put("mlist",  DynamicJsonProperty.convertToArrayNode(mlist));
		node.put("win", win);
		node.put("eName", eName);
		node.put("escore", escore);
		node.put("elist", DynamicJsonProperty.convertToArrayNode(elist));
		node.put("lv", lv);
		int sdate =  TimeUtil.getCurrentTime() - date;
		if(sdate <0){
			sdate = 0;
		}
		node.put("date", sdate);
		node.put("warType", warType);
		return node;
	}
}
