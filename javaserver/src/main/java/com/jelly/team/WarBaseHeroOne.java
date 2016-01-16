package com.jelly.team;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WarBaseHeroOne {

	@JsonProperty("t")
	private String teamName = "";
	
	@JsonProperty("n")
	private String name = "";   // 玩家名字
	
	@JsonProperty("g")
	private String guid = "";
	
	@JsonProperty("m")
	private String modelID = "";
	
	@JsonProperty("h")
	private float hps = 1;
	
	@JsonProperty("i")
	private Integer warIndex = 0;  // 所在军团部队索引
	
	public WarBaseHeroOne()
	{
	}
	
	public void setHps(float hps)
	{
		this.hps = hps;
	}
	
	public float getHps()
	{
		return this.hps;
	}
	
	public void setWarIndex(Integer index)
	{
		warIndex = index;
	}
	
	public Integer getWarIndex()
	{
		return warIndex;
	}
	
	public void setDatas(String guid,String name,String teamName,String modelID,float hps,Integer warIndex)
	{
		this.guid = guid;
		this.name = name;
		this.teamName = teamName;
		this.modelID = modelID;
		this.hps = hps;
		this.warIndex = warIndex;
	}
	
	public void setTeamName(String teamName)
	{
		this.teamName = teamName;
	}
	
	public String getGuid()
	{
		return this.guid;
	}
	
	public String getTeamName()
	{
		return this.teamName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ObjectNode toJson() {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		
		if(guid.equals("")){
			return obj;
		}
		
		obj.put("guid", guid);
		obj.put("name", name);
		obj.put("teamName", teamName);
		obj.put("modelID", modelID);
		obj.put("warIndex", warIndex);
		obj.put("hps", hps);
		
		return obj;
	}
	
}
