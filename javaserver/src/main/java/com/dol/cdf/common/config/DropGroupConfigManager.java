package com.dol.cdf.common.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Dig;
import com.dol.cdf.common.bean.DropGroup;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemRateEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DropGroupConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_DROPGROUP = "DropGroup.json";
	
	private static final String JSON_FILE_FORM_1 = "Dig.json";

	private Map<Integer, List<DropGroup>> dropGroupMap;
	
	private Map<Integer,Dig> digItems;
	
	private Set<Integer> preciousIds;
	
	public static final int[] RND_PRECIOUS_TIMES = {2,3,4,5,6};
	
	public List<DropGroup> getDropGroups(int id) {
		return dropGroupMap.get(id);
	}
	
	@Override
	public void loadConfig() {
		dropGroupMap = Maps.newHashMap();
		digItems = Maps.newHashMap();
		preciousIds = Sets.newHashSet();
		List<DropGroup> list = readConfigFile(JSON_FILE_DROPGROUP, new TypeReference<List<DropGroup>>() {});
		for (DropGroup group : list) {
			List<DropGroup> groupList = dropGroupMap.get(group.getId());
			if (groupList == null) {
				groupList = new ArrayList<DropGroup>();
				groupList.add(group);
				dropGroupMap.put(group.getId(), groupList);
			} else {
				groupList.add(group);
			}
		}
		
		List<Dig> digList = readConfigFile(JSON_FILE_FORM_1, new TypeReference<List<Dig>>() {
		});
		for (Dig d : digList) {
			digItems.put(d.getId(), d);
			if(d.getPrecious() != null && d.getPrecious() == 1) {
				preciousIds.add(d.getId());
			}
		}
	}
	
	/**
	 * 随机获取最贵物品的ID
	 * @param times
	 * @return
	 */
	public List<Integer> getRndPreciousIds(){
		List<Integer> list = Lists.newArrayList();
		int times = RND_PRECIOUS_TIMES[Rnd.get(RND_PRECIOUS_TIMES.length)];
		for (int i = 0; i < times; i++) {
			int dropRate = Rnd.get(10000);
			for (int j = 1; j <= digItems.size(); j++) {
				if (digItems.get(j).getRate() > dropRate) {
					int id = digItems.get(j).getId();
					if (preciousIds.contains(id)) {
						list.add(id);
					}
					break;
				}
			}
		}
		
		return list;
	}

	public Dig getDigItemById(int id) {
		Dig dig = digItems.get(id);
		return dig;
	}
	
	/**
	 * 通过groupID根基几率随机道具
	 * 
	 * @param id
	 * @return
	 */
	public VariousItemEntry getVariousItemByGroupId(int id) {
		List<DropGroup> drops = getDropGroups(id);
		int dropRate = Rnd.get(10000);
		for (int j = 0; j < drops.size(); j++) {
			VariousItemRateEntry item = drops.get(j).getItem()[0];
			if (item.getRate() > dropRate) {
				return item;
			}
		}
		logger.error("groupId" + id);
		return null;
	}
	
	public int getIdxInGroup(int groupId, VariousItemEntry item) {
		List<DropGroup> drops = getDropGroups(groupId);
		for (int i=0; i<drops.size(); i++) {
			DropGroup drop = drops.get(i);
			VariousItemRateEntry[]  ii = drop.getItem();
			if (item.getType().equals(ii[0].getType()) && item.getAmount() == ii[0].getAmount()) {
				return i;
			}
		}
		return 0;
	}

}