package com.jelly.rank;

import io.nadron.app.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.dol.cdf.common.config.JsonEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Maps;

public class ArenaRank extends JsonEntity{
	
	public static final int RANK_MAX_ARENA_COUNT = 5000;
	@JsonProperty("indexRank")
	ConcurrentMap<Integer, RankModel> indexRank = Maps.newConcurrentMap();
	
//	Map<Integer, RankModel> indexRank = Maps.newHashMap();
	
	//名称对应名次
	@JsonProperty("guidIndex")
	ConcurrentMap<String, Integer> guidIndex = Maps.newConcurrentMap();
	
//	Map<String, Integer> guidIndex = Maps.newHashMap();
	
	public synchronized void clear(){
		this.indexRank.clear();
		this.guidIndex.clear();
	}
	
	public synchronized boolean addRank(Player player) {
		if(size()>=RANK_MAX_ARENA_COUNT){
			return false;
		}
		int index = size()+1;
		add(index, new RankModel(player));
		return true;
	}
	
	private void add(Integer index, RankModel rankModel) {
		this.indexRank.put(index, rankModel);
		this.guidIndex.put(rankModel.getGuid(), index);
	}
	
	public int size() {
		return this.indexRank.size();
	}
	
	public ArrayNode getRankRecord(int index){
		RankModel rankModel = this.getRankModel(index);
		ArrayNode createArrayNode = mapper.createArrayNode();
		if (rankModel != null && rankModel.getRecords() != null) {
			for (RankRecord record : rankModel.getRecords() ) {
				createArrayNode.add(record.toJson(mapper));
			}
			rankModel.setRecords(null);
		}
		return createArrayNode;
	}

	public List<RankModel> getModels(int fromIndex, int toIndex) {
		if (fromIndex <= 0) {
			fromIndex = 1;
		}
		if (toIndex >= this.size()) {
			toIndex = this.size();
		}
		if (fromIndex > toIndex) {
			return Collections.emptyList();
		}
		List<RankModel> models = new ArrayList<RankModel>(toIndex - fromIndex
				+ 1);
		for(int i=fromIndex;i<=toIndex;i++){
			models.add(this.indexRank.get(i));
		}
		return models;
	}
	

	/**
	 * 根据用户guid获得该用户排名
	 * 
	 * @param guid
	 * @return
	 */
	public int getIndex(String guid) {
		Integer result = this.guidIndex.get(guid);
		if(result==null){
			result=RANK_MAX_ARENA_COUNT+Math.abs(guid.hashCode())%RANK_MAX_ARENA_COUNT;
		}
		return result;
	}
	
	public int getIndexByPlayer(Player player)
	{
		if(size()>=RANK_MAX_ARENA_COUNT){
			return getIndex(player.getId());
		}
		
		if(guidIndex.keySet().contains(player.getId())){
			return getIndex(player.getId());
		}
		int index = size()+1;
		add(index, new RankModel(player));
		return index;
	}
	
	/**
	 * 根据该用户排名获得该用户排名信息
	 * 
	 * @param index
	 * @return
	 */
	public RankModel getRankModel(int index) {
		return this.indexRank.get(index);
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public RankModel getRankModel(String guid) {
		int index=this.getIndex(guid);
		return this.indexRank.get(index);
	}
	
	/**
	 * 交换两个用户的排名
	 * 
	 * @param guid1
	 * @param index1
	 * @param guid2
	 * @param index2
	 */
	public void changeIndex(Player player,String guid1, int index1, String guid2,
			int index2) {
		RankModel tmp = null;
		if(index1>RANK_MAX_ARENA_COUNT){
			tmp=new RankModel(player);
			this.guidIndex.remove(guid2);
			this.indexRank.remove(index2);
		}else{
			tmp = this.indexRank.get(index1);
			this.guidIndex.put(guid2, index1);
			this.indexRank.put(index1, this.indexRank.get(index2));
		}
		this.guidIndex.put(guid1, index2);
		this.indexRank.put(index2,  tmp);
	}

	/**
	 * 获取前N名玩家
	 */
	public List<String> getTopN(int n) {
		List<String> guidList = new ArrayList<String>();
		for (int i=1; i<=n; i++) {
			RankModel rankModel = indexRank.get(i);
			if (rankModel != null) {
				guidList.add(rankModel.getGuid());
			}
			
		}
		return guidList;
	}
	

}
