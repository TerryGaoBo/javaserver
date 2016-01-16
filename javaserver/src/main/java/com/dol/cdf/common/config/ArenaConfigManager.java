package com.dol.cdf.common.config;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Arena;
import com.dol.cdf.common.bean.Arenapoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

public class ArenaConfigManager extends BaseConfigLoadManager {

	private static final String ARENA_FILE_FORM = "Arena.json";
	private Map<Integer, Arena> arena;
	
	private static final String ARENA_FILE_RANK_FROM = "Arenapoint.json";
	private Map<Integer,Arenapoint> arenaRankAwards;
	

	@Override
	public void loadConfig() {
		arena = Maps.newHashMap();
		List<Arena> list = readConfigFile(ARENA_FILE_FORM, new TypeReference<List<Arena>>() {
		});
		for (int i = 0; i < list.size(); i++) {
			arena.put(list.get(i).getId(), list.get(i));
		}
		
		
		arenaRankAwards = Maps.newHashMap();
		List<Arenapoint> listrank = readConfigFile(ARENA_FILE_RANK_FROM, new TypeReference<List<Arenapoint>>(){});
		for(int k = 0;k<listrank.size();k++){
			arenaRankAwards.put(listrank.get(k).getId(), listrank.get(k));
		}
		
	}

	public Map<Integer, Arena> getArena() {
		return arena;
	}
	
	public Map<Integer,Arenapoint> getArenaPoint()
	{
		return this.arenaRankAwards;
	}

}
