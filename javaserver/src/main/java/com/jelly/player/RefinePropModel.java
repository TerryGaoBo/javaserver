package com.jelly.player;


import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RefinePropModel extends DynamicJsonProperty{
	/**
	 * 
	 */
	@JsonProperty("id")
	private Integer skillId;
	
	@JsonProperty("effect")
	private Integer effectVal;
	
	public RefinePropModel(){
		
	}
	public RefinePropModel(Integer skillId, Integer effectVal) {
		super();
		this.skillId = skillId;
		this.effectVal = effectVal;
	}
	public Integer getSkillId() {
		return skillId;
	}
	public void setSkillId(Integer skillId) {
		this.skillId = skillId;
	}
	public Integer getEffectVal() {
		return effectVal;
	}
	public void setEffectVal(Integer effectVal) {
		this.effectVal = effectVal;
	}
	@Override
	public String getKey() {
		return null;
	}
	@Override
	public JsonNode toWholeJson() {
		ObjectNode node = jackson.createObjectNode();
		node.put("skillId", getSkillId());
		node.put("effectVal", getEffectVal());
		return node;
	}
	
	
	
}
