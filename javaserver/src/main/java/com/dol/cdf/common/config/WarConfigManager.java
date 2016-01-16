package com.dol.cdf.common.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.War;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

public class WarConfigManager extends BaseConfigLoadManager {
	
	Map<Integer,War> warMap;
	
	Integer maxPassId = 0;
	@Override
	public void loadConfig() {
		warMap = Maps.newHashMap();
		List<War> list = readConfigFile("War.json", new TypeReference<List<War>>() {
		});
		for (War war : list) {
			warMap.put(war.getId(), war);
		}
		maxPassId = Collections.max(warMap.keySet());
	}
	
	public War getWar(int id) {
		return warMap.get(id);
	}
	
	public Integer getMaxPassId() {
		return maxPassId;
	}

}
