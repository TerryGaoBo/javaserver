package com.dol.cdf.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Level;
import com.fasterxml.jackson.core.type.TypeReference;

public class LevelConfigManager extends BaseConfigLoadManager {
	private final Map<Integer, Level> levels=new HashMap<Integer, Level>();
	
	@Override
	public void loadConfig() {
		List<Level> list = readConfigFile("Level.json", new TypeReference<List<Level>>() {
		});
		for (int i=0; i<list.size(); i++) {
			levels.put(list.get(i).getLv(), list.get(i));
		}
	}
	public Map<Integer, Level> getLevels() {
		return levels;
	}
	
	public Level getLevel(int lv) {
		return levels.get(lv);
	}

}
