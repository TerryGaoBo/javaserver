package com.jelly.combat;

import io.nadron.app.Player;

import java.util.Calendar;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.activity.WorldActivity;
import com.jelly.player.ICombatGroup;

public class PVBeastCombatManager extends BaseCombatManager {

	private final Player player;

	private ObjectNode rewards;
	
	
	private WorldActivity worldActivity;

	public PVBeastCombatManager(ICombatGroup attackerGroup, ICombatGroup defenderGroup, Player player, WorldActivity worldActivity) {
		super(attackerGroup, defenderGroup);
		// 如果小于第二章这设置必须命中
		cb.setBeastFight(true);
		this.player = player;
		this.worldActivity = worldActivity;
	}

	@Override
	public void afterFight() {
		super.afterFight();
		ObjectNode combatResult = getCombatResult();
		player.sendMessage(combatResult);
		worldActivity.addBeastHurt(player.getName(), cb.getBeastHurtValue());
	}

	@Override
	public ObjectNode getRewardJson() {
		return rewards;
	}

	public static float getBasetCurrentHpRate() {
		Calendar calendar = Calendar.getInstance();
		int i = 30 - calendar.get(Calendar.MINUTE);
		return i / 30f;
	}

}
