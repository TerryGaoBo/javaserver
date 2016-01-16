package com.jelly.activity;

import java.util.Collection;
import java.util.Map;

import com.dol.cdf.common.config.JsonEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

public class ActivityJsonEntity extends JsonEntity{
	/**
	 * 玩家充值数据 key guid
	 */
	@JsonProperty("pay")
	Map<String, Long> payMap = Maps.newHashMap();
	
	@JsonProperty("wars")
	Map<Integer, Collection<String>> leveledWars = Maps.newHashMap();

	public Map<String, Long> getPayMap() {
		return payMap;
	}

	public void setPayMap(Map<String, Long> payMap) {
		this.payMap = payMap;
	}

	public Map<Integer, Collection<String>> getLeveledWars() {
		return leveledWars;
	}

	public void setLeveledWars(Map<Integer, Collection<String>> leveledWars) {
		this.leveledWars = leveledWars;
	}
	
	
}
