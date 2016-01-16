package com.jelly.combat;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.util.List;

import com.dol.cdf.common.bean.QualityRef;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.player.ICombatGroup;

public class RecruitCombatManager extends BaseCombatManager {

	private final Player player;

	public static final String COMBAT_COMMAND = "fightRes";
	
	private int recuritGrade;

	private ObjectNode rewards;
	
	public RecruitCombatManager(ICombatGroup attackerGroup, ICombatGroup defenderGroup, Player player) {
		super(attackerGroup, defenderGroup);
		this.player = player;
		recuritGrade = defenderGroup.getFirstFighter().getHero().getQuality();
	}

	@Override
	public void afterFight() {
		super.afterFight();
		boolean win = this.getAttackerGroup().isWin();
		if(win) {
			QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(recuritGrade);
			List<VariousItemEntry> recResultAwards = player.getRecruit().addRecruitProgress(player,qualityRef.getFight());
			if (recResultAwards != null) {
				rewards = VariousItemUtil.itemToJson(recResultAwards);
			}
			//胜利后自动刷新队伍
			player.getRecruit().refreshTeamMember(player);
		}else {
			VariousItemUtil.doBonus(player, new VariousItemEntry("energy",1), LogConst.RECRUIT_FIGHT, false);
		}
		sendBattleResult(player);
		ObjectNode combatResult = getCombatResult();
		player.sendMessage(combatResult);
		LogUtil.doChurchChallengeLog((DefaultPlayer)player, win==true?1:0);
	}
	
	@Override
	public ObjectNode getRewardJson() {
		return rewards;
	}

}
