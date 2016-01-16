package com.dol.cdf.common.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.BuildingRef;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class BuildingConfigManager extends BaseConfigLoadManager {

	Map<Integer, Building> buildings;
	Table<Integer, Integer, VariousItemEntry[]> brefs;
	private static final String JSON_FILE = "Building.json";
	private static final String JSON_FILE_1 = "BuildingRef.json";

	@Override
	public void loadConfig() {
		buildings = Maps.newHashMap();
		brefs = HashBasedTable.create(); 
		List<Building> list = readConfigFile(JSON_FILE, new TypeReference<List<Building>>() {
		});
		for (Building b : list) {
			buildings.put(b.getId(), b);
		}

		List<BuildingRef> list1 = readConfigFile(JSON_FILE_1, new TypeReference<List<BuildingRef>>() {
		});
		for (BuildingRef buildingRef : list1) {
			brefs.put(buildingRef.getId(), buildingRef.getLevel(), buildingRef.getUpItem());
		}
		
	}

	public VariousItemEntry[] getUpItems(int type, int lv) {
		return brefs.get(type, lv);
	}
//	public int getMaxBuildLv(int type) {
//		int max = (Integer) Collections.max(brefs.columnKeySet()); 
//		return max;
//	}

	public Building getBuilding(int type) {
		return buildings.get(type);
	}

	public Collection<Building> getAllBuilding() {
		return buildings.values();
	}
}
