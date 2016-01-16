package com.jelly.team;

import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class WarHoldBase {
	
	@JsonProperty("baseHero")
	private List<WarBaseHeroOne> baseHero = Lists.newArrayList();
	
	@JsonProperty("i")
	private Integer id = 0; // 据点id ( 0-20 )
	
	@JsonProperty("t")
	private String teamName = "";
	
	@JsonProperty("sign")
	private Integer sign = 0; // 0 未被占领过，1 占领过
	
	@JsonProperty("ko")
	private Integer koState = 0; // 此据点是否被地方占领过了（此据点是空据点，此值才起作用）    0 为占领，1 占领过
	
	public WarHoldBase()
	{
	}
	
	public void setSign(Integer value)
	{
		this.sign = value;
	}
	
	public Integer getSign()
	{
		return this.sign;
	}
	
	public void setKoState(Integer value)
	{
		this.koState = value;
	}
	public Integer getKoState()
	{
		return this.koState;
	}
	
	
	public Integer getID()
	{
		return id;
	}
	
	public void setID(Integer id)
	{
		this.id = id;
	}
	
	public void addBaseHero(WarBaseHeroOne o)
	{
		if(baseHero.size()>=3){
			return;
		}
		baseHero.add(o);
	}
	
	public List<WarBaseHeroOne> getBaseHero()
	{
		return baseHero;
	}
	
	public void clearBaseList(Team team)
	{
		for (WarBaseHeroOne warBaseHeroOne : baseHero) {
			ArmyHero wo = team.getArmyHeroByIndex(warBaseHeroOne.getGuid(), warBaseHeroOne.getWarIndex());
			if(wo != null){
				wo.setStrongID(-1);
				wo.setheroWhere(-1);
			}else{
				continue;
			}
		}
	}
	
	public ObjectNode toJson() {
		teamName = "";
		
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		
		ArrayNode earr = DynamicJsonProperty.jackson.createArrayNode();
		for (WarBaseHeroOne idx : baseHero) {
			if (idx != null) {
				earr.add(idx.toJson());
				teamName = idx.getTeamName();
			} else {
				earr.addNull();
			}
		}
		
		obj.put("warBase", earr);
		obj.put("id",id);
		obj.put("teamName", teamName);
		return obj;
	}
}
