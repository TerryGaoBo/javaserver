package com.jelly.combat;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.util.List;

import com.dol.cdf.common.bean.Adventure;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.player.ICombatGroup;
import com.jelly.team.TeamManager;

public class PVECombatManager extends BaseCombatManager {

	private final int chapterId;

	private final int stageId;
	
	private final int advenType;

	private final Player player;

	private ObjectNode rewards;

	public PVECombatManager(ICombatGroup attackerGroup, ICombatGroup defenderGroup, int advenType, int chapterId, int stageId, Player player) {
		super(attackerGroup, defenderGroup);
		// 如果小于第二章这设置必须命中

		if (chapterId < 3 && player.getAdventure().getChapter() < 3) {
			cb.setMustHit(true);
		}
		this.chapterId = chapterId;
		this.stageId = stageId;
		this.player = player;
		this.advenType = advenType;
	}

	@Override
	public void afterFight() {
		super.afterFight();
		boolean win = this.getAttackerGroup().isWin();
		Adventure adv = AllGameConfig.getInstance().adventures.getAdventure(advenType, chapterId, stageId);
		if (win) {
			// 挑战成功
			List<VariousItemEntry> passStage = player.getAdventure().passStage(player, advenType, chapterId, stageId);
			if (passStage != null) {
				rewards = VariousItemUtil.itemToJson(passStage);
			}
			player.getProperty().addFubenUse(adv.getType(), chapterId, stageId, 1);//扣使用次数
		} else {
			// 挑战失败失败
			// 口一点体力
			//	普通副本挑战失败扣除1点体力, 精英副本挑战失败扣除挑战该关卡消耗的50%的体力
			int decEnergy = adv.getType() == 1 ? 1 : (int)(adv.getEnergy() / 2);
			VariousItemEntry needEnergyEntry = new VariousItemEntry("energy", decEnergy);
			VariousItemUtil.doBonus(player, needEnergyEntry, LogConst.ADVENURE_FIGHT, false);
		}
		
		if (adv.getType() == 1) {
			LogUtil.doGuanQiaLog((DefaultPlayer)player, adv.getChapter() + "-" + adv.getStage(), 2, win ? 1 : 0);
		} else {
			LogUtil.doGuanQiaLog((DefaultPlayer)player, adv.getChapter() + "-" + adv.getStage(), 4, win ? 1 : 0);
		}

		player.getTeam().addExp(adv.getGuildEXP());
		sendBattleResult(player);
		ObjectNode combatResult = getCombatResult();
		player.sendMessage(combatResult);
//		LogUtil.doGuanQiaLog((DefaultPlayer)player, chapterId + "-" + stageId, 2, (win==true?1:0));
	}

	@Override
	public ObjectNode getRewardJson() {
		return rewards;
	}

}
