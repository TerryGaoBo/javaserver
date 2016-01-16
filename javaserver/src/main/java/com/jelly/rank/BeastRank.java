package com.jelly.rank;

import com.dol.cdf.common.config.JsonEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.util.concurrent.AtomicLongMap;

public class BeastRank extends JsonEntity{
	
	public static final int RANK_MAX_ARENA_COUNT = 5000;
	
	@JsonProperty("br")
	AtomicLongMap<String> map = AtomicLongMap.create();
	
	public void addHurtNumber(String id, int value){
		map.put(id, value);
	}
	
	
}
