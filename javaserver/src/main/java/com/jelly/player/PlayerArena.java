package com.jelly.player;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Arena;
import com.dol.cdf.common.bean.Arenapoint;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.activity.ActivityType;
import com.jelly.combat.PVPCombatManager;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.quest.TaskType;
import com.jelly.rank.ArenaRank;
import com.jelly.rank.GameRankMaster;
import com.jelly.rank.RankModel;
import com.jelly.rank.RankRecord;

public class PlayerArena extends DynamicJsonProperty {
	private static final Logger logger = LoggerFactory.getLogger(PlayerArena.class);

	@JsonProperty("ls")
	private int lastRewardStatus = 0;
	
	@JsonProperty("lt")
	private int lastRewardTime = 0;
	// 临时存储玩家挑战数据
	private List<Integer> challengeInfo;

	ArenaRank arenaRank;

	// 任务的建筑ID
	private static final int BUILDING_TYPE = BuildingType.PVP.getId();

	public static List<Integer> createChallegeInfo(int index, ArenaRank arenaRank) {
		if (index >= ArenaRank.RANK_MAX_ARENA_COUNT) {
			index = ArenaRank.RANK_MAX_ARENA_COUNT;
		}
		List<Integer> indexs = new ArrayList<Integer>();
		if (index <= 5) {
			int begin = 1;
			while (indexs.size() < 5 && (indexs.size() < (arenaRank.size() - 1))) {
				if (begin != index) {
					indexs.add(begin);
				}
				begin++;
			}
		} else if (index <= 10) {
			int begin = index;
			while (indexs.size() < 5 && (indexs.size() < (arenaRank.size() - 1))) {
				if (begin != index) {
					indexs.add(begin);
				}
				begin--;
			}
		} else {
			Arena arena = getArena(index);
			if (indexs.size() < 5 && arenaRank.size() > 10) {
				indexs.add((int) (index * arena.getGrid1()));
				indexs.add((int) (index * arena.getGrid2()));
				indexs.add((int) (index * arena.getGrid3()));
				indexs.add((int) (index * arena.getGrid4()));
				indexs.add((int) (index * arena.getGrid5()));
			}
		}
		Collections.sort(indexs);
		return indexs;
	}

	/**
	 * 根据竞技场名次 获得该竞技场的奖励和可挑战信息
	 * 
	 * @param index
	 * @return
	 */
	public static Arena getArena(int index) {
		Arena arena = null;
		for (Arena arena2 : AllGameConfig.getInstance().arena.getArena().values()) {
			int rank[] = arena2.getRank();
			if (index >= rank[0] && index <= rank[1]) {
				arena = arena2;
				break;
			}
		}
		return arena;
	}
	
	public static Arenapoint getArenaPoint(int index)
	{
		Arenapoint area = null;
		for(Arenapoint area2 : AllGameConfig.getInstance().arena.getArenaPoint().values()){
			int [] rank = area2.getRank();
			if(index>=rank[0] && index<=rank[1]){
				area = area2;
				break;
			}
		}
		return area;
	}
	

	public int getLastRewardStatus() {
		return lastRewardStatus;
	}

	public ArenaRank getArenaRank() {
		if( arenaRank == null) {
			arenaRank = GameRankMaster.getInstance().getArenaRank();
		}
		return arenaRank;
	}
	
	public void setLastRewardStatus(int lastRewardStatus) {
		this.lastRewardStatus = lastRewardStatus;
	}

	@Override
	public String getKey() {
		return "arena";
	}

	@Override
	public JsonNode toOpenJson(Player player) {
		// 检测添加排行榜
//		int index = getArenaRank().getIndex(player.getId());
		int index = getArenaRank().getIndexByPlayer(player);
		challengeInfo = createChallegeInfo(index, getArenaRank());
		ObjectNode objectNode = jackson.createObjectNode();
		ArrayNode array = jackson.createArrayNode();
		for (Integer i : challengeInfo) {
			RankModel rankModel = getArenaRank().getRankModel(i);
			if (rankModel == null) {
				logger.error("player index:{}, fighter index:{}, player userId:{}",index,i,player.getProperty().getUserId());
				continue;
			}
			String guid = rankModel.getGuid();
			RoleEntity playerInfo = player.getAllPlayersCache().getPlayerInfo(guid);
			if (playerInfo != null) {
				ArrayNode arrayNode = rankModel.toJsonArray(playerInfo).add(i);
				int s = getArenaPointsFunc(i);
				arrayNode.add(s);
				array.add(arrayNode);
			}
		}

		// 名次
		objectNode.put("index", index);
		// 可挑战的人
		objectNode.put("fighters", array);
		if (!TimeUtil.isSameDay(lastRewardTime)) {
			objectNode.put("status", 1);
			int counts =arenaReward(player);
			objectNode.put("counts", counts);
		}
		
		return objectNode;
	}
	
	public void challege(Player player, int idx, ObjectCacheService objectCache) {
		if (challengeInfo == null || challengeInfo.contains(idx) == false) {
			logger.error("challengeInfo = {}", challengeInfo);
			int index = getArenaRank().getIndex(player.getId());
			challengeInfo = createChallegeInfo(index, getArenaRank());
		}
		if (player.getBuilding().isMaxFinish(BUILDING_TYPE, player)) {
			player.sendMiddleMessage(MessageCode.CHALLENGE_NOT_ENUGH);
			return;
		}
		doArena(player, idx, objectCache);
	}
	
	public void revenge(Player player, String guid, ObjectCacheService objectCache) {
		if (player.getBuilding().isMaxFinish(BUILDING_TYPE, player)) {
			player.sendMiddleMessage(MessageCode.CHALLENGE_NOT_ENUGH);
			return;
		}
		int index = getArenaRank().getIndex(guid);
		doArena(player, index, objectCache);
	}

	private void doArena(Player player, int idx, ObjectCacheService objectCache) {
		RankModel rankModel = getArenaRank().getRankModel(idx);
		if(rankModel == null){
			player.sendMiddleMessage(MessageCode.FAIL);
			logger.error("玩家已经不再排行榜里了");
			return;
		}
		DefaultPlayer cache = objectCache.getCache(rankModel.getGuid(), DefaultPlayer.class);
		if (cache == null) {
			logger.error("不存在该玩家{}", rankModel.getGuid());
			return;
		}
		AttackerGroup attackerGroup = player.getHeros().getAttackerGroup();
		DefenderGroup defenderGroup = cache.getHeros().getDefenderGroup();
		PVPCombatManager pvpCombatManager = new PVPCombatManager(attackerGroup, defenderGroup, player);
		pvpCombatManager.start();
		boolean win = pvpCombatManager.getAttackerGroup().isWin();
		// TODO 如果应没CD，继续，如果输了添加CD
		int result = MessageCode.FAIL;
		int playerIdx = getArenaRank().getIndex(player.getId());
		if (win) {
			// 交换位置
			if (playerIdx > idx) {
				getArenaRank().changeIndex(player, player.getId(), playerIdx, rankModel.getGuid(), idx);
				rankModel.addRecord(new RankRecord(player.getRole().getName(),0,TimeUtil.getCurrentTime(),playerIdx));
			}else {
				rankModel.addRecord(new RankRecord(player.getRole().getName(),0,TimeUtil.getCurrentTime(),idx));
			}
			
			result = MessageCode.OK;
			//
			player.getTask().dispatchEvent(player, TaskType.PVP_WIN);
		} else {
			//名次没变化
			rankModel.addRecord(new RankRecord(player.getRole().getName(),1,TimeUtil.getCurrentTime(),0));
		}
		player.sendResult(OperResultType.PVP, result);
		player.getTask().dispatchEvent(player, TaskType.PVP);
		player.getActivity().dispatchEvent(ActivityType.PVP_0, player);
		
		addChallegeRewards(win,player);
		
	}

	public void requestRankRecord(Player player) {
		int index = getArenaRank().getIndex(player.getId());
		if (index <= ArenaRank.RANK_MAX_ARENA_COUNT) {
			JsonNode node = getArenaRank().getRankRecord(index);
			player.sendMessage("rankRecordInfo", node);
		}else {
			player.sendMessage("rankRecordInfo", jackson.createArrayNode());
		}
	}
	
	/**
	 * 每日竞技场奖励
	 * @param player
	 */
	public int arenaReward(Player player) {
		lastRewardTime = TimeUtil.getCurrentTime();
		int index = getArenaRank().getIndex(player.getId());
//		Arena arena = getArena(index); //
//		Integer itemId = arena.getItem();
//		VariousItemEntry item = new VariousItemEntry(itemId,1);
//		VariousItemUtil.doBonus(player, item, LogConst.ARENA_REWARD, true);
		
		Arenapoint ap = getArenaPoint(index);
		Integer apt =ap.getReward_arenapoint();
		VariousItemEntry item = new VariousItemEntry("arenapoint",apt);
		VariousItemUtil.doBonus(player, item, LogConst.ARENA_REWARD, true);
		
		LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.ARENA_JINGJI_LOG_3, apt.intValue(), player.getProperty().getArenaPoint(), "arenapoint");
		
		return apt.intValue();
	}
	
	public int getArenaPointsFunc(int rankid)
	{
		Arenapoint ap = getArenaPoint(rankid);
		Integer apt =ap.getReward_arenapoint();
		return apt;
	}
	
	private void addChallegeRewards(boolean win,Player player)
	{
		Integer pts ;
		if(win){
			pts = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.WIN_ARENA_GET_POINT);
		}else{
			pts = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.LOSE_ARENA_GET_POINT);
		}
		VariousItemEntry item = new VariousItemEntry("arenapoint",pts);
		VariousItemUtil.doBonus(player, item, LogConst.ARENA_RESULT, true);
		
		
		if(win){
			LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.ARENA_JINGJI_LOG_1, pts.intValue(), player.getProperty().getArenaPoint(), "arenapoint");
		}else{
			LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.ARENA_JINGJI_LOG_2, pts.intValue(), player.getProperty().getArenaPoint(), "arenapoint");
		}
	}
	
	@Override
	public JsonNode toWholeJson() {
		if (!TimeUtil.isSameDay(lastRewardTime)) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			obj.put("status", 1);
			return obj;
		}
		return null;
	}

}