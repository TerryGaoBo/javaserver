package com.jelly.activity;

import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.config.JsonEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.jelly.node.datastore.mapper.RoleEntity;

public class TopListJsonEntity extends JsonEntity{
	
	@JsonProperty("tops")
	List<ArrayNode> topList = Lists.newArrayList();

	public List<ArrayNode> getTopList() {
		return topList;
	}

	public void setTopList(List<ArrayNode> topList) {
		this.topList = topList;
	}
	
	public ArrayNode toJsonArray(RoleEntity e) {
		ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
		array.add(e.getName()).add(e.getLevel()).add(e.getCharId()).add(e.getPower());
		return array;
	}

	public void cleanup() {
		topList.clear();
	}
}
