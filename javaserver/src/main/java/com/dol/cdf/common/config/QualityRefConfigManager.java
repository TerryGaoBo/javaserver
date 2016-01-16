package com.dol.cdf.common.config;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.QualityRef;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.jelly.player.GradeType;

public class QualityRefConfigManager extends BaseConfigLoadManager {
	
	Map<Integer, QualityRef> map = Maps.newHashMap();
	
	int starUpLength = 0;
	
	int spointUpLength = 0;
	
	@Override
	public void loadConfig() {
		List<QualityRef> list = readConfigFile("QualityRef.json", new TypeReference<List<QualityRef>>() {
		});
		for (QualityRef qualityRef : list) {
			map.put(qualityRef.getId(), qualityRef);
		}
		starUpLength = getQualityRef(GradeType.A.getId()).getUpVals().length;
		spointUpLength = getQualityRef(GradeType.A.getId()).getSpV().length;
		
	}
	
	public QualityRef getQualityRef(int type) {
		return map.get(type);
	}
	
	public int getQualityStarUpLength() {
		return starUpLength;
	}

	public int getSpointUpLength() {
		return spointUpLength;
	}
}
