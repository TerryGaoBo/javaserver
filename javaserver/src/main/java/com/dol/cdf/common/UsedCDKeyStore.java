package com.dol.cdf.common;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.config.JsonEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UsedCDKeyStore extends JsonEntity{

	Map<String,List<String>> cdKeys = Maps.newHashMap();
	
	public List<String> getCdKeys(String channel) {
		return cdKeys.get(channel);
	}


	public Map<String, List<String>> getCdKeys() {
		return cdKeys;
	}


	public void setCdKeys(Map<String, List<String>> cdKeys) {
		this.cdKeys = cdKeys;
	}

	public boolean containCDKey(String channel, String key){
		List<String> cdKeys2 = getCdKeys(channel);
		if(cdKeys2 == null) return false;
		return cdKeys2.contains(key);
	}
	
	public void addKey(String channel,String key){
		List<String> list = cdKeys.get(channel);
		if(list == null) {
			list = Lists.newArrayList();
			list.add(key);
			cdKeys.put(channel, list);
		}else {
			list.add(key);
		}
	}
	
	public static void main(String[] args) {
	}
}
