package com.dol.cdf.common.gamefunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.GameFunc;
import com.dol.cdf.common.config.BaseConfigLoadManager;
import com.dol.cdf.common.gamefunction.gfi.GFIModel;
import com.fasterxml.jackson.core.type.TypeReference;

public class GameFunctionConfigManager extends BaseConfigLoadManager {

	private final static String CONFIG_FILE_NAME = "GameFunction.json";

	public Map<Integer, GFIModel> gfiIndexMap;

	public Map<String, GFIModel> gfiSymbolMap;

	public GFIModel getGFIBySymbol(String symbol) {
		return gfiSymbolMap.get(symbol);
	}

	@Override
	public void loadConfig() {
		gfiIndexMap = new HashMap<Integer, GFIModel>();
		gfiSymbolMap = new HashMap<String, GFIModel>();
		List<GameFunc> configList = readConfigFile(CONFIG_FILE_NAME, new TypeReference<List<GameFunc>>() {
		});
		for (GameFunc gf : configList) {
			GFIModel gfi = new GFIModel(gf);
			gfiIndexMap.put(gf.getId(), gfi);
			gfiSymbolMap.put(gf.getSymbol(), gfi);
		}
	}
}
