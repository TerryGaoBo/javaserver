package com.dol.cdf.common.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Quest;
import com.dol.cdf.common.bean.QuestRef;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class QuestConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_FORM = "Quest.json";
	private static final String JSON_FILE_1 = "QuestRef.json";

	private Map<Integer, Quest> questMap;
	// key 品质 value 任务ID
	Multimap<Integer, Quest> questQualityMap = LinkedHashMultimap.create();
	
	private List<Quest> dailyQuests;
	

	private List<QuestRef> questRefs;

	public Quest getQuest(int id) {
		return questMap.get(id);
	}

	public Collection<Quest> getAllQuest() {
		return questMap.values();
	}

	/**
	 * 随机任务
	 * 
	 * @param type
	 * @param playerLv
	 * @return
	 */
	public Quest getRndQuest(int type, int playerLv) {
		Collection<Quest> list = questQualityMap.get(type);
		List<Quest> tempQuests = Lists.newArrayList();
		for (Quest quest : list) {
			if (playerLv >= quest.getMinLv() && playerLv <= quest.getMaxLv()) {
				tempQuests.add(quest);
			}
		}
		//logger.info("quest type = {}, plaeyrLv = {}, count = {}", type, playerLv, tempQuests.size());
		int i = Rnd.get(tempQuests.size());
		return tempQuests.get(i);
	}

	@Override
	public void loadConfig() {
		questMap = new HashMap<Integer, Quest>();
		// key 品质 value 任务ID
		questQualityMap = LinkedHashMultimap.create();
		dailyQuests = Lists.newArrayList();
		List<Quest> list = readConfigFile(JSON_FILE_FORM, new TypeReference<List<Quest>>() {
		});
		questRefs = readConfigFile(JSON_FILE_1, new TypeReference<List<QuestRef>>() {
		});
		for (Quest q : list) {
			questMap.put(q.getId(), q);
			questQualityMap.put(q.getQuality(), q);
			if(q.getDaily() != null && q.getDaily() == 1) {
				dailyQuests.add(q);
			}
		}

	}
	
	public List<Quest> getDailyQuests() {
		return dailyQuests;
	}

	public List<QuestRef> getQuestRefs() {
		return questRefs;
	}

}
