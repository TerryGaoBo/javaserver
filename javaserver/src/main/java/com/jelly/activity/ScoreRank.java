package com.jelly.activity;

import io.nadron.context.AppContext;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.JsonEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.datastore.mapper.RoleEntity;

public class ScoreRank extends JsonEntity {
	
	@JsonProperty("id2Sr")
	private Map<String, Integer> id2SrMap;
	
	@JsonProperty("id2Rank")
	private Map<String, Integer> id2RankMap;
	
	@JsonProperty("rank2ID")
	private Map<Integer, String> rank2IDMap;
	
	private AllPlayersCache allPlayersCache;
	
	
	public ScoreRank() {
		rank2IDMap = Maps.newHashMap();
		id2SrMap = Maps.newHashMap();
		id2RankMap = Maps.newHashMap();		
		allPlayersCache = AppContext.getBean(AllPlayersCache.class);
	}
	
	public synchronized void cleanup() {
		id2SrMap.clear();
		id2RankMap.clear();
		rank2IDMap.clear();
	}
	
	public synchronized void updateScore(String id, int score) {
		id2SrMap.put(id, id2SrMap.containsKey(id) ? id2SrMap.get(id) + score : score);
		int enterSr = AllGameConfig.getInstance().activitys.getCatchNinja().getRankintegral();
		if (id2SrMap.get(id) >= enterSr) {
			updateRank(id);
		}
	}

	public synchronized List<ObjectNode> getRangeRank(int from, int to) {
		List<ObjectNode> ranklist = Lists.newArrayList();
		for (; from <= to && rank2IDMap.get(from) != null; ++from) {
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			RoleEntity role = allPlayersCache.getPlayerInfo(rank2IDMap.get(from));
			info.put("id", rank2IDMap.get(from));
			info.put("name", role.getName());
			info.put("charId", role.getCharId());
			info.put("score", id2SrMap.get(rank2IDMap.get(from)));
			ranklist.add(info);
		}
		return ranklist;
	}
	
	public synchronized ArrayNode getRangeRankGM(int from, int to) {
		if (to == 0) {
			to = rank2IDMap.size();
		}
		ArrayNode ranklist = DynamicJsonProperty.jackson.createArrayNode();
		for (; from <= to && rank2IDMap.get(from) != null; ++from) {
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			info.put("ranking", from);
			String guid = rank2IDMap.get(from);
			info.put("name", allPlayersCache.getPlayerInfo(guid).getName());
			info.put("score", id2SrMap.get(guid));
			ranklist.add(info);
		}
		return ranklist;
	}
	
	public ObjectNode getRankInfo(String id) {
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		Integer ranking = id2RankMap.get(id);
		Integer score = id2SrMap.get(id);
		info.put("ranking", ranking != null ? ranking : 0);
		info.put("name", allPlayersCache.getPlayerInfo(id).getName());
		info.put("score", score != null ? score : 0);
		return info;
	}
	
	private void updateRank(String uid) {
		int score = id2SrMap.get(uid);
		int oldRank;
		if (id2RankMap.containsKey(uid)) {
			oldRank = id2RankMap.get(uid);
		} else {
			oldRank = id2RankMap.size() + 1;
			id2RankMap.put(uid, oldRank);
			rank2IDMap.put(oldRank, uid);
		}
		int newRank = oldRank;
		for (; newRank > 1; --newRank) {
			if (score <= id2SrMap.get(rank2IDMap.get(newRank - 1))) {				
				break;
			}
		}
		
		String origID = rank2IDMap.put(newRank, uid);
		id2RankMap.put(uid, newRank);
		++newRank;
		for (; newRank <= oldRank; ++newRank) {
			id2RankMap.put(origID, newRank);
			origID = rank2IDMap.put(newRank, origID);
		}
	}
	
//	public void printDebug() {
//		System.out.println("#####################DEBUG######################\r\n");
//		for (Map.Entry<Integer, String> e : rank2IDMap.entrySet()) {
//			System.out.printf("%d %s:%d\r\n", e.getKey(), e.getValue(), id2SrMap.get(e.getValue()));
//		}
//	}
//	
//	public static void main(String[] args) {
//		ScoreRank sr = new ScoreRank();
//		int[] scores = new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
//		String[] names = new String[] { "Saber", "Lancer", "Archer", "Rider",
//				"Caster", "Assassi", "Berserker" };
//		for (int i = 0; i < 50; ++i) {
//			sr.updateScore(names[(int) (Math.random() * names.length)],
//					scores[(int) (Math.random() * scores.length)]);
//		}
//		sr.printDebug();
//	}
}
