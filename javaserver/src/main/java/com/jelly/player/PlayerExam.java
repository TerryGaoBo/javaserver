package com.jelly.player;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.activity.ActivityType;
import com.jelly.combat.BaseCombatManager;
import com.jelly.combat.ExamCombatManager;
import com.jelly.hero.Hero;
import com.jelly.quest.TaskType;

public class PlayerExam extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerExam.class);

	// 表示玩家当前可以打到那个关卡
	@JsonProperty("cp")
	private int currPass = -1;

	// 表示玩家最大通过的关卡
	@JsonProperty("mp")
	private int maxPass = 0;

	// 任务的建筑ID
	public static final int BUILDING_TYPE = BuildingType.SYKC.getId();

	public static final int INIT_VALUE = 10000;
	public static final int UP_VALUE = 1000;
	public static final int END_EXAM_REWARD_SILVER = 10000;
	public static final int QUICK_EXAM_NEED_GOLD = 20;

	public List<Integer> getPassOption() {
		return Lists.newArrayList(0, maxPass / 2, maxPass);
	}

	public List<Integer> getPassCost() {
		if (maxPass == 0) {
			return Lists.newArrayList(0, 0, 0);
		}
		int midPass = maxPass / 2;
		int midCost = (midPass * ((midPass/10) +1)* UP_VALUE + INIT_VALUE)/2;
		int upMaxCost = Math.round((maxPass*(maxPass/10f) * 0.5f));
		return Lists.newArrayList(INIT_VALUE, midCost , upMaxCost);
	}

	private int getValidMaxPass(int buildLevel) {
		Building building = AllGameConfig.getInstance().buildings.getBuilding(BUILDING_TYPE);
		int value = building.getInitValue()[0];
		return value;
	}

	public void quickExam(Player player, ArrayNode node) {
		if (currPass == -1) {
			logger.error("请选择闯关方式");
			return;
		}
		VariousItemEntry needGoldEntry = new VariousItemEntry("gold", QUICK_EXAM_NEED_GOLD);
		int code = VariousItemUtil.doBonus(player, needGoldEntry, LogConst.EXAM_QUICK_COST, false);
		if (code != MessageCode.OK) {
			player.sendMiddleMessage(code);
			return;
		}
		while (examFight(player, node)) {
			this.currPass++;
			LogUtil.doNinjaTestLog((DefaultPlayer)player, this.currPass, 1);
		}
		setCurrPass(this.currPass, player);

	}

	private ArrayNode getExamBonus(Player player, int i) {
		ArrayNode array = jackson.createArrayNode();
		int examSilver = config.exams.getExamSilver(i);

		player.getProperty().addExp(config.exams.getExamExp(i), player);
		player.getProperty().changeMoney(0, examSilver);
		LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.EXAM_END_REWARD, config.exams.getExamExp(i), player.getProperty().getExp(), "exp");
		LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.EXAM_END_REWARD, examSilver, player.getProperty().getExp(), "silver");
		array.add(i);
		array.add(examSilver);
		VariousItemEntry item = config.exams.getItem(i);
		if (item != null) {
			array.add(item.getType()).add(item.getAmount());
			VariousItemUtil.doBonus(player, item, LogConst.EXAM_END_REWARD, true);
		}
		return array;
	}

	private void rewardExamRxp(Player player) {
		int totalRxp = getTotalRxp();
		for (Integer hid : player.getHeros().getMarIds()) {
			Hero hero = player.getHeros().getHero(hid);
			hero.addExp(totalRxp, player);
			player.getHeros().appendChangeMap("hes", hid, hero.toJson());
		}
	}

	private int getTotalRxp() {
		int expSum = 0;
		for (int i = 1; i <= this.currPass; i++) {
			expSum += config.exams.getExamRxp(i);
		}
		return expSum;
	}

	public List<VariousItemEntry> addExamBonus(Player player) {
		int tarPass = currPass + 1;
		List<VariousItemEntry> rewards = Lists.newArrayList();
		int examRxp = config.exams.getExamRxp(tarPass);
		if (examRxp != 0) {
			rewards.add(new VariousItemEntry(player.getHeros().getFirstHeroRoleId(), examRxp));

		}
		int exp = config.exams.getExamExp(tarPass);
		if (exp != 0) {
			rewards.add(new VariousItemEntry("exp", exp));
			player.getProperty().addExp(exp, player);
			LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.EXAM_END_REWARD, exp, player.getProperty().getExp(), "exp");
		}
		VariousItemEntry item = config.exams.getItem(tarPass);
		if (item != null) {
			rewards.add(item);
			VariousItemUtil.doBonus(player, item, LogConst.EXAM_END_REWARD, true);
		}
		setCurrPass(tarPass, player);
		return rewards;
	}

	public void doExamOption(Player player, int idx,ArrayNode node) {
		if (currPass != -1) {
			logger.error("已经选过闯关方式");
			return;
		}
		VariousItemEntry needMoney;
		Integer cost = getPassCost().get(idx);
		// 最后一个消耗金币
		if (idx == 2) {
			needMoney = new VariousItemEntry("gold", cost);
		} else {
			needMoney = new VariousItemEntry("silver", cost);
		}
		if (player.getBuilding().isMaxFinish(BUILDING_TYPE, player)) {
			player.sendMiddleMessage(MessageCode.CHALLENGE_NOT_ENUGH);
			return;
		}
		int code = VariousItemUtil.doBonus(player, needMoney, LogConst.EXAM_OPTION_COST, false);
		if (code != MessageCode.OK) {
			player.sendMiddleMessage(code);
			return;
		}

		Integer opt = getPassOption().get(idx);
		
		for (int i = 1; i <= opt; i++) {
			node.add(getExamBonus(player, i));
		}
		setCurrPass(opt, player);
		
		
//		int silverSum = 0;
//		for (int i = 1; i <= this.currPass; i++) {
//			int examSilver = config.exams.getExamSilver(i);
//			silverSum += examSilver;
//		}
//		if(silverSum > 0) {
//			VariousItemEntry silver = new VariousItemEntry("silver", silverSum);
//			VariousItemUtil.doBonus(player, silver, LogConst.EXAM_OPTION_COST, true);
//			player.sendMiddleMessage(MessageCode.EARN_SILVER, silverSum+"");
//		}

	}

	public int getCurrPass() {
		return currPass;
	}

	public void setCurrPass(int currPass, Player player) {
		this.currPass = currPass;
		if (this.currPass > maxPass) {
			maxPass = currPass;
			addChange("maxPass", maxPass);
		}
		addChange("currPass", currPass);
		if(this.currPass > 0) {
			player.getActivity().dispatchExamPassEvent(currPass, player);
		}
	}
	
	public int getMaxPass() {
		return maxPass;
	}

	public boolean examFight(Player player, ArrayNode node) {
		if (currPass == -1) {
			logger.error("请先选择闯关方式");
			return false;
		}
		int tarPass = currPass + 1;
		BuildInstance build = player.getBuilding().getBuild(BUILDING_TYPE);
		int validMaxPass = getValidMaxPass(build.getLevel());
		if (tarPass > validMaxPass) {
			// player.sendMiddleMessage(MessageCode.EXAM_MAX_VALID);
			return false;
		}
		AttackerGroup attackerGroup = player.getHeros().getAttackerGroup();
		DefenderGroup defenderGroup = config.exams.getDefenderGroup(tarPass);
		new BaseCombatManager(attackerGroup, defenderGroup).start();
		if (attackerGroup.isWin) {
			node.add(getExamBonus(player, tarPass));
		} else {
			return false;
		}
		return true;
	}

	public boolean examFight(Player player) {
		if (currPass == -1) {
			logger.error("请先选择闯关方式");
			return false;
		}
		int tarPass = currPass + 1;
		BuildInstance build = player.getBuilding().getBuild(BUILDING_TYPE);
		int validMaxPass = getValidMaxPass(build.getLevel());
		if (tarPass > validMaxPass) {
			player.sendMiddleMessage(MessageCode.EXAM_MAX_VALID);
			return false;
		}
		AttackerGroup attackerGroup = player.getHeros().getAttackerGroup();
		DefenderGroup defenderGroup = config.exams.getDefenderGroup(tarPass);
		new ExamCombatManager(attackerGroup, defenderGroup, player).start();
		return true;
	}

	public void endExam(Player player) {
		if (currPass < 0) {
			logger.error("还没开始闯关");
			return;
		}
		// 奖励1w银币
		// VariousItemEntry silver = new VariousItemEntry("silver",
		// END_EXAM_REWARD_SILVER);
		// VariousItemUtil.doBonus(player, silver, LogConst.EXAM_END_REWARD,
		// true);
		rewardExamRxp(player);
		setCurrPass(-1, player);
		player.getTask().dispatchEvent(player, TaskType.FINISH_EXAM);
	}

	@Override
	public JsonNode toOpenJson(Player player) {
		ObjectNode objectNode = jackson.createObjectNode();
		if (currPass == -1) {
			objectNode.put("opt", convertToArrayNode(getPassOption()));
			objectNode.put("cost", convertToArrayNode(getPassCost()));
		}
		objectNode.put("currPass", currPass);
		objectNode.put("maxPass", maxPass);
		objectNode.put("rewardSilver", END_EXAM_REWARD_SILVER);
		objectNode.put("quickGold", QUICK_EXAM_NEED_GOLD);
		objectNode.put("muti", ActivityType.EXAM_MUTI_EARN.getValue());
		return objectNode;
	}

	@Override
	public JsonNode toWholeJson() {
		return null;
	}

	@Override
	public String getKey() {
		return "exam";
	}

}