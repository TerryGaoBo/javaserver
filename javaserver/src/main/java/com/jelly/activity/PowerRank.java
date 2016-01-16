package com.jelly.activity;

import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PowerRank extends TopListJsonEntity {
	
	Map<String, Integer> name2IdxMap = Maps.newHashMap();
	
	public void setName2IdxMap(Map<String, Integer> name2IdxMap) {
		this.name2IdxMap = name2IdxMap;
	}
	
	public Map<String, Integer> getName2IdxMap() {
		return name2IdxMap;
	}
	
	public Integer getPowerIndex(String name) {
		return name2IdxMap.get(name);
	}
	
	public void cleanup() {
		name2IdxMap.clear();
	}
}
