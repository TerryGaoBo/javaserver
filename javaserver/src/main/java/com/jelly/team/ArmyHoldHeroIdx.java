package com.jelly.team;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.hero.Hero;

public class ArmyHoldHeroIdx {
	
	@JsonProperty("guid")
	private String guid;
	
	@JsonProperty("idx")
	private Integer idx;//所在部队的list索引 
	
	public ArmyHoldHeroIdx(){
		
	}
	public ArmyHoldHeroIdx(String guid,Integer idx){
		this.guid = guid;
		this.idx = idx;
	}
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Integer getIdx() {
		return idx;
	}
	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public ObjectNode toJson(Team team) {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("guid", this.guid);
		obj.put("idx", this.idx.intValue());
		Hero hero = team.getAmryHeroIDByidx(this.guid, this.idx);
		int moduleid = hero.getRoleId();
		int power = hero.getAllPower();
		obj.put("moduleID", moduleid+"");
		obj.put("power", power);
		return obj;
	}
}
