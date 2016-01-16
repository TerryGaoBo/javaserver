package com.jelly.quest;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.Quest;
import com.dol.cdf.common.bean.QuestRef;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import com.jelly.activity.ActivityType;

public class PlayerTask extends DynamicJsonProperty implements IPlayerTask {

	private static final Logger logger = LoggerFactory.getLogger(PlayerTask.class);

	public static final int MAX_ACCEPT_QUEST = 5;

	// 接收任务的状态
	@JsonProperty("ap")
	public Map<Integer, Integer> acceptTaskIds = new HashMap<Integer, Integer>();
	
	@JsonProperty("an")
	public Map<Integer, Integer> taskNums = new HashMap<Integer, Integer>();

	@JsonProperty("nq")
	private Set<Integer> needRefreshQuest = Sets.newHashSet();
	// 领奖的状态
	@JsonProperty("st")
	private int status = -1;

	@JsonProperty("lr")
	private int lastRefreshQuestTime;

	// 任务的建筑ID
	public static final int BUILDING_TYPE = BuildingType.JHS.getId();

	/**
	 * 派发任务
	 * 
	 * @param player
	 * @param type
	 * @param level
	 * @return
	 */
	public void dispatchEvent(Player player, TaskType type, int level) {
		for (Entry<Integer, Integer> entry : acceptTaskIds.entrySet()) {
			int status = entry.getValue();
			// 是否是进行中的任务
			if (status == TaskStatus.INPROCESS.ordinal()) {
				int id = entry.getKey();
				Quest quest = AllGameConfig.getInstance().quests.getQuest(id);
				if (quest.getType() == type.getId()) {
					// 目标等级是否达到
					if (quest.getTarLv() == null) {
						int taskNum = getTaskNum(id);
						taskNum += 1;
						if(taskNum >= quest.getNum()) {
							acceptTaskIds.put(id, TaskStatus.FINISHED.ordinal());
							addChange(id, acceptTaskIds.get(id));
							player.sendMiddleMessage(MessageCode.QUEST_FINISH, id + "");
							taskNums.remove(id);
						}else {
							taskNums.put(id, taskNum);
						}
					}else if(level == quest.getTarLv()){
						acceptTaskIds.put(id, TaskStatus.FINISHED.ordinal());
						addChange(id, acceptTaskIds.get(id));
						player.sendMiddleMessage(MessageCode.QUEST_FINISH, quest.getId() + "");
					}
				}
			}
		}
	}
	
	public void dispatchMutiEvent(Player player, TaskType type, int count) {
		for (Entry<Integer, Integer> entry : acceptTaskIds.entrySet()) {
			int status = entry.getValue();
			// 是否是进行中的任务
			if (status == TaskStatus.INPROCESS.ordinal()) {
				int id = entry.getKey();
				Quest quest = AllGameConfig.getInstance().quests.getQuest(id);
				if (quest.getType() == type.getId()) {
					// 目标等级是否达到
					if (quest.getTarLv() == null) {
						int taskNum = getTaskNum(id);
						taskNum += count;
						if(taskNum >= quest.getNum()) {
							acceptTaskIds.put(id, TaskStatus.FINISHED.ordinal());
							addChange(id, acceptTaskIds.get(id));
							player.sendMiddleMessage(MessageCode.QUEST_FINISH, id + "");
							taskNums.remove(id);
						}else {
							taskNums.put(id, taskNum);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取任务数量
	 * @param id
	 * @return
	 */
	public int getTaskNum(int id) {
		Integer num = taskNums.get(id);
		return num == null ? 0 : num;
	}

	public void dispatchEvent(Player player, TaskType type) {
		dispatchEvent(player, type, 0);
	}

	@Override
	public String getKey() {
		return "quest";
	}

	@Override
	public JsonNode toWholeJson() {
		ObjectNode objectNode = jackson.createObjectNode();
		//Test code
//		for (Integer id : acceptTaskIds.keySet()) {
//			acceptTaskIds.put(id, TaskStatus.OVER.ordinal());
//		}
		objectNode.put("list", convertToJsonNode(acceptTaskIds));
		// 完成的数量
		objectNode.put("status", status);
		return objectNode;
	}

	@Override
	public JsonNode toOpenJson(Player player) {
		//兼容老数据
		if(acceptTaskIds.size() == MAX_ACCEPT_QUEST) {
			acceptTaskIds.clear();	
		}
		if (acceptTaskIds.isEmpty()) {
			initTask(player);
		} else {
			checkResetTaskStatus(player);
		}
		return toWholeJson();
	}

	public boolean checkResetTaskStatus(Player player) {
		checkRefreshQuest(player);
		// 这个应该是通用的方法
		int todayFinish = player.getBuilding().getTodayFinish(BUILDING_TYPE);
		if (todayFinish == 0) {
			player.getTask().setStatus(-1);
			return true;
		}
		return false;
	}

	/**
	 * 检测是否需要隔天刷新
	 * 
	 * @param player
	 */
	public void checkRefreshQuest(Player player) {
		int currentTime = TimeUtil.getCurrentTime();
		if (TimeUtil.isSameDay(lastRefreshQuestTime, currentTime)) {
			return;
		}
		for (Entry<Integer, Integer> acId : acceptTaskIds.entrySet()) {
			if (acId.getValue() == TaskStatus.FINISHED.ordinal()) {
				needRefreshQuest.add(acId.getKey());
			} else {
				int id = acId.getKey();
				Quest quest = AllGameConfig.getInstance().quests.getQuest(id);
				checkPutQuest(player, id, quest);
			}
		}
		taskNums.clear();
		lastRefreshQuestTime = currentTime;
	}

//	public static void doBonus(Player player, int taskId) {
//		Quest quest = AllGameConfig.getInstance().quests.getQuest(taskId);
//		if (quest == null) {
//			logger.error("do bonus taskId = {}", taskId);
//			return;
//		}
//		player.getProperty().changeMoney(0, quest.getSilver());
//		LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.TASK_FINISH, quest.getSilver(), player.getProperty().getSilver(), "silver");
//	}

	private void initTask(Player player) {
		List<Quest> dailyQuests = AllGameConfig.getInstance().quests.getDailyQuests();
		for (Quest quest : dailyQuests) {
			checkPutQuest(player, quest.getId(), quest);
		}
		lastRefreshQuestTime = TimeUtil.getCurrentTime();
	}

	/**
	 * 点击刷新客户端应该把任务都清理了 TODO 排除相同ID的任务
	 */
	@Override
	public void acceptTask(Player player) {

		for (int i = 0; i < MAX_ACCEPT_QUEST; i++) {
			int questGrade = AllGameConfig.getInstance().rate.questGrade(player.getBuilding().getBuildLevel(BUILDING_TYPE), player);
			Quest rndQuest = AllGameConfig.getInstance().quests.getRndQuest(questGrade, player.getProperty().getLevel());
			if (rndQuest == null) {
				logger.error("rndQuest is null playerLevel = {},gradeType={}", player.getProperty().getLevel(), questGrade);
			} else {
				int id = rndQuest.getId();
				// logger.error("questGrade = {},rndQuestId={}", questGrade,
				// id);
				if (acceptTaskIds.containsKey(id)) {
					i--;
					continue;
				}
				checkPutQuest(player, id, rndQuest);

			}
		}
		addChange("list", convertToJsonNode(acceptTaskIds));
	}

	public void checkPutQuest(Player player, int id, Quest quest) {
		if (quest.getType() == TaskType.BUY_CARD.getId() && player.getProperty().getMonthlyPayDays() > 0) {// 判断是否是已经充值过
			acceptTaskIds.put(id, TaskStatus.FINISHED.ordinal());
		} else {
			acceptTaskIds.put(id, TaskStatus.INPROCESS.ordinal());
		}
	}

	@Override
	public void submitTask(int id, Player player) {
		if (acceptTaskIds.get(id) != TaskStatus.FINISHED.ordinal()) {
			player.sendResult(OperResultType.QUEST, MessageCode.FAIL);
			return;
		}
		boolean checkResetTaskStatus = checkResetTaskStatus(player);
		if (checkResetTaskStatus) {
			addChange("status", getStatus());
		}
		if (player.getBuilding().isMaxFinish(BUILDING_TYPE, player)) {
			player.sendResult(OperResultType.QUEST, MessageCode.MAX_FINISH_QUEST_COUNT);
			return;
		}

		if (needRefreshQuest.contains(id)) {
			acceptTaskIds.put(id, TaskStatus.INPROCESS.ordinal());
			needRefreshQuest.remove(id);
		} else {
			acceptTaskIds.put(id, TaskStatus.OVER.ordinal());
		}

		Quest quest = AllGameConfig.getInstance().quests.getQuest(id);
		Float muti = Float.parseFloat(ActivityType.QUEST_MUTI.getValue());
		int real = (int) (quest.getSilver() * muti);
		VariousItemEntry variousItemEntry = new VariousItemEntry("silver", real);
		VariousItemUtil.doBonus(player, variousItemEntry, LogConst.LOG_TASK, true);
		addChange("list", convertToJsonNode(acceptTaskIds));
		player.sendResult(OperResultType.QUEST, VariousItemUtil.itemToJson(variousItemEntry));
		player.getActivity().dispatchEvent(ActivityType.QUEST_FINISH, player);
		LogUtil.doTaskLog((DefaultPlayer)player, id);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 获取完成总数的奖励，有个漏洞是玩家没有领取前面的奖励
	 * 
	 * @param player
	 * @param idx
	 */
	public void gainReward(Player player, int idx) {
		List<QuestRef> questRefs = config.quests.getQuestRefs();
		if (idx <= status) {
			logger.error(" 已经领取过了，idx:{}, status:{}", idx, status);
			return;
		}
		if (idx == status + 1) {
			QuestRef questRef = questRefs.get(idx);
			int todayFinish = player.getBuilding().getTodayFinish(BUILDING_TYPE);
			if (todayFinish < questRef.getCount()) {
				logger.error("没达到目标，need count:{}, finish count:{}", questRef.getCount(), todayFinish);
				return;
			}
			VariousItemEntry variousItemEntry = new VariousItemEntry("gold", questRef.getGold());
			VariousItemUtil.doBonus(player, variousItemEntry, LogConst.TASK_FINISH, true);
			status = idx;
			addChange("status", status);
			player.sendResult(OperResultType.QUEST, VariousItemUtil.itemToJson(variousItemEntry));
		}
	}

	/**
	 * 刷新任务
	 * 
	 * @param player
	 */
	public void refreshQuest(Player player) {
		Building building = config.buildings.getBuilding(BUILDING_TYPE);
		if (player.getBuilding().isFunCding(BUILDING_TYPE)) {
			int code = VariousItemUtil.doBonus(player, building.getFuncCost()[0], LogConst.REFRESH_TASK, false);
			if (code != MessageCode.OK) {
				player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
				return;
			} else {
				acceptTask(player);
			}
		} else {
			acceptTask(player);
			player.getBuilding().addFunCd(building);
		}
		player.sendResult(OperResultType.QUEST, 0, 0);

	}

}
