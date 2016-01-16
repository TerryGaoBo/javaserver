package com.jelly.rank;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.activity.ActivityType;

public enum RankType {
	power(1) {
		@Override
		public List<ArrayNode> getRankList() {
			return GameRankMaster.getInstance().powerRank.getTopList();
		}
	},
	adv(2){
		@Override
		public List<ArrayNode> getRankList() {
			return GameRankMaster.getInstance().advRank.getTopList();
		}
	},
	exam(3){
		@Override
		public List<ArrayNode> getRankList() {
			return GameRankMaster.getInstance().examRank.getTopList();
		}
	},
	level(4){
		@Override
		public List<ArrayNode> getRankList() {
			return GameRankMaster.getInstance().levelRank.getTopList();
		}
	},
	gold(5){
		@Override
		public List<ArrayNode> getRankList() {
			return GameRankMaster.getInstance().goldRank.getTopList();
		}
	},
	score(6) {
		@Override
		public List<ArrayNode> getRankList() {
			List<ArrayNode> ranklist = Lists.newArrayList();
			for (ObjectNode node : GameRankMaster.getInstance().scoreRank.getRangeRank(1, 10)) {
				ArrayNode info = DynamicJsonProperty.jackson.createArrayNode();
				info.add(node.get("name").asText());
				info.add(node.get("charId").asInt());
				info.add(node.get("score").asInt());
				ranklist.add(info);
			}
			return ranklist;
		}
	}
	;
	
	int id;
	
	private RankType(int id) {
		this.id = id;
	}
	static Map<Integer, RankType> id2ActTypsMap = Maps.newHashMap();

	static {
		for (RankType type : RankType.values()) {
			id2ActTypsMap.put(type.id, type);
		}
	}
	
	public static RankType getActById(int id) {
		return id2ActTypsMap.get(id);
	}
	
	abstract public List<ArrayNode> getRankList();
}
