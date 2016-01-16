package com.jelly.team;

import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class ArmyStrongHold {
	
	@JsonProperty("herolist")
	private List<ArmyHoldHeroIdx> herolist = Lists.newArrayList();
	
	@JsonProperty("id")
	private Integer id;
	
	public ArmyStrongHold(){
		
	}
	
	public ArmyStrongHold(Integer id)
	{
		this();
		this.id = id;
	}
	
	public List<ArmyHoldHeroIdx> getStrongHoldRoles() {
		return herolist;
	}

	public void setStrongHoldRoles(List<ArmyHoldHeroIdx> strongHoldRoles) {
		this.herolist = strongHoldRoles;
	}
	
	public void setStrongHoldRolesAndAdd(Integer idx,ArmyHoldHeroIdx heroIdx){
		try{
			herolist.set(idx,heroIdx);
		}catch(Exception e){
			herolist.add(heroIdx);
		}
	}
	
	public boolean removeStrongHoldRoles(String guid,Integer heroidx){
		for(ArmyHoldHeroIdx idx:herolist){
			if(idx.getGuid().equals(guid) && idx.getIdx().intValue()==heroidx){
				herolist.remove(idx);
				return true;
			}
		}
		return false;
	}
	
	public boolean setStrongHoldIDX(String guid,Integer heroidx,Integer newheroidx)
	{
		for(ArmyHoldHeroIdx idx:herolist){
			if(idx.getGuid().equals(guid) && idx.getIdx().intValue()==heroidx){
				idx.setIdx(newheroidx);
				return true;
			}
		}
		return false;
	}
	
	public void removeStrongHoldRolesByID(int index){
		herolist.remove(index);
	}
	public void removeStrongHoldRolesByArmyHero(ArmyHoldHeroIdx index){
		herolist.remove(index);
	}
	public void setStrongHoldRolesByIdIndex(int idx,ArmyHoldHeroIdx index){
		herolist.set(idx,index);
	}
	public void addStrongHoldRoles(ArmyHoldHeroIdx index){
		herolist.add(index);
	}
	
	public ObjectNode toJson(Team t) {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		ArrayNode earr = DynamicJsonProperty.jackson.createArrayNode();
		for (ArmyHoldHeroIdx idx : herolist) {
			if (idx != null) {
				earr.add(idx.toJson(t));
			} else {
				earr.addNull();
			}
		}
		obj.put("strongID", this.id.intValue());
		obj.put("strongholdRoles", earr);
		return obj;
	}
}
