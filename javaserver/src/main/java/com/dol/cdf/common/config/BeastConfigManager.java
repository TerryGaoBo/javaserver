package com.dol.cdf.common.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Beast;
import com.fasterxml.jackson.core.type.TypeReference;

public class BeastConfigManager extends BaseConfigLoadManager {

	private static final String BEAST_FILE = "Beast.json";
	private Map<Integer, Beast> beast=new HashMap<Integer, Beast>();
	

	@Override
	public void loadConfig() {
		List<Beast> list = readConfigFile(BEAST_FILE, new TypeReference<List<Beast>>() {
		});
		for (int i=0; i<list.size(); i++) {
			beast.put(list.get(i).getId(), list.get(i));
		}
	}
	
	public Beast getBeast(int id) {
		return beast.get(id);
	}
	
	public Collection<Beast> getBeasts() {
		return beast.values();
	}


}

