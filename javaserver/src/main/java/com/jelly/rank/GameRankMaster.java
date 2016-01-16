package com.jelly.rank;

import io.nadron.context.AppContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.activity.ActivityType;
import com.jelly.activity.AdventureRank;
import com.jelly.activity.ExamRank;
import com.jelly.activity.GoldRank;
import com.jelly.activity.LevelRank;
import com.jelly.activity.PayRank;
import com.jelly.activity.PowerRank;
import com.jelly.activity.ScoreRank;
import com.jelly.activity.WorldActivity;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.node.datastore.mapper.RoleMapper;

public class GameRankMaster {

	private static final int RANK_PAGE_SIZE = 10;

	private static final Logger logger = LoggerFactory.getLogger(GameRankMaster.class);

	private static GameRankMaster instance = null;
	/** 竞技场排名信息 */
	public ArenaRank arenaRank = new ArenaRank();

	public GoldRank goldRank = new GoldRank();
	public ExamRank examRank = new ExamRank();
	public AdventureRank advRank = new AdventureRank();
	public LevelRank levelRank = new LevelRank();
	public PowerRank powerRank = new PowerRank();
	public PayRank payRank = new PayRank();
	public ScoreRank scoreRank = new ScoreRank();

	private WorldActivity worldActivity;
	
	private AllPlayersCache allPlayersCache;

	RoleMapper roleMapper;

	GameRankMaster() {
		readValue();
		roleMapper = AppContext.getBean(RoleMapper.class);
		worldActivity = AppContext.getBean(WorldActivity.class);
		allPlayersCache = AppContext.getBean(AllPlayersCache.class);
	}

	private void readValue() {
		ArenaRank arenaRankFromFile = (ArenaRank) arenaRank.readValues();
		if (arenaRankFromFile != null) {
			arenaRank = arenaRankFromFile;
		}

		GoldRank tmpGoldRank = (GoldRank) goldRank.readValues();
		if (tmpGoldRank != null) {
			goldRank = tmpGoldRank;
		}

		PayRank tmpPayRank = (PayRank) payRank.readValues();
		if (tmpPayRank != null) {
			payRank = tmpPayRank;
		}

		ExamRank tmpExamRank = (ExamRank) examRank.readValues();
		if (tmpExamRank != null) {
			examRank = tmpExamRank;
		}

		AdventureRank tmpAdvRank = (AdventureRank) advRank.readValues();
		if (tmpAdvRank != null) {
			advRank = tmpAdvRank;
		}

		LevelRank tmpLevelRank = (LevelRank) levelRank.readValues();
		if (tmpLevelRank != null) {
			levelRank = tmpLevelRank;
		}

		PowerRank tmpPowerRank = (PowerRank) powerRank.readValues();
		if (tmpPowerRank != null) {
			powerRank = tmpPowerRank;
		}
	}

	public ArenaRank getArenaRank() {
		return arenaRank;
	}
	

	public void setArenaRank(ArenaRank arenaRank) {
		this.arenaRank = arenaRank;
	}

	public void caculateTopList() {

		logger.info("start caculate topList");
		long l = System.currentTimeMillis();
		List<RoleEntity> entities = roleMapper.loadAdventureTop100();
		List<ArrayNode> topList = Lists.newArrayList();
		for (RoleEntity roleEntity : entities) {
			topList.add(advRank.toJsonArray(roleEntity));
		}
		advRank.setTopList(topList);
		advRank.writevalues();

		entities = roleMapper.loadExamTop100();
		topList = Lists.newArrayList();
		for (RoleEntity roleEntity : entities) {
			topList.add(examRank.toJsonArray(roleEntity));
		}
		examRank.setTopList(topList);
		examRank.writevalues();

		entities = roleMapper.loadLevelTop100();
		topList = Lists.newArrayList();
		for (RoleEntity roleEntity : entities) {
			topList.add(levelRank.toJsonArray(roleEntity));
		}
		levelRank.setTopList(topList);
		levelRank.writevalues();

		entities = roleMapper.loadGoldTop100();
		topList = Lists.newArrayList();
		for (RoleEntity roleEntity : entities) {
			topList.add(goldRank.toJsonArray(roleEntity));
		}
		goldRank.setTopList(topList);
		goldRank.writevalues();

		entities = roleMapper.loadPowerTop50000();
		topList = Lists.newArrayList();
		Map<String, Integer> name2IdxMap = Maps.newHashMap();
		int i = 0;
		for (RoleEntity roleEntity : entities) {
			if (i < 100) {
				topList.add(powerRank.toJsonArray(roleEntity));
			}
			i++;
			name2IdxMap.put(roleEntity.getName(), i);
		}
		powerRank.setTopList(topList);
		powerRank.setName2IdxMap(name2IdxMap);
		powerRank.writevalues();
		logger.info("end caculate topList cost : {} ms..", (System.currentTimeMillis() - l));
	}

	public void caculatePayRank() {
		Map<String, Long> payMap = worldActivity.getSortedPayMap();
		List<ArrayNode> topList = Lists.newArrayList();
		for (Entry<String, Long> entry : payMap.entrySet()) {
			String guid = entry.getKey();
			Long value = entry.getValue();
			RoleEntity e = allPlayersCache.getPlayerInfo(guid);
			topList.add(payRank.toJsonArray(e, value/10));
		}
		payRank.setTopList(topList);
		payRank.writevalues();
	}

	/**
	 * 保存竞技场和无尽之塔的排行榜
	 */
	public void saveRank() {
		arenaRank.writevalues();
		logger.info("saveRank finish");
	}
	
	/**
	 * 保存积分排行榜
	 **/
	public void saveScoreRank() {
		scoreRank.writevalues();
		logger.info("saveScoreRank finish");
	}
	
	public void createScoreRankBak() {
		scoreRank.backUp();
		logger.info("saveScoreRankBak finish");
	}
	
	/**
	 * 清空积分排行榜
	 **/
	public void cleanupScoreRank() {
		scoreRank.cleanup();
		scoreRank.writevalues();
		logger.info("cleanupScoreRank finish");
	}
	
	public void loadScoreRank() {
		try {
			ScoreRank tmpScoreRank = (ScoreRank) scoreRank.readValues();
			if (tmpScoreRank != null) {
				scoreRank = tmpScoreRank;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void updateScoreRank(String id, int sr) {
		scoreRank.updateScore(id, sr);
	}

	public static GameRankMaster getInstance() {
		if (instance == null) {
			synchronized (GameRankMaster.class) {
				if (instance == null) {
					instance = new GameRankMaster();
				}
			}
		}
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public List<RankModel> getRankPage(int type, int page) {
		List<RankModel> result = new ArrayList<RankModel>();
		int fromIndex = (page - 1) * RANK_PAGE_SIZE + 1;
		int toIndex = fromIndex + RANK_PAGE_SIZE - 1;
		result = arenaRank.getModels(fromIndex, toIndex);
		return result;
	}
}
