package com.jelly.combat;

import io.nadron.app.Player;

import java.util.List;

import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.player.ICombatGroup;

public class ExamCombatManager extends BaseCombatManager {

	private final Player player;

	public static final String COMBAT_COMMAND = "fightRes";
	

	private ObjectNode rewards;
	
	public ExamCombatManager(ICombatGroup attackerGroup, ICombatGroup defenderGroup, Player player) {
		super(attackerGroup, defenderGroup);
		this.player = player;
	}

	@Override
	public void afterFight() {
		super.afterFight();
		boolean win = this.getAttackerGroup().isWin();
		if(win) {
			List<VariousItemEntry> recResultAwards = player.getExam().addExamBonus(player);
			if (recResultAwards != null) {
				rewards = VariousItemUtil.itemToJson(recResultAwards);
			}
		}
		sendBattleResult(player);
		ObjectNode combatResult = getCombatResult();
		player.sendMessage(combatResult);
	}
	
	@Override
	public ObjectNode getRewardJson() {
		return rewards;
	}

}
