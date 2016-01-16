package com.jelly.combat;

import io.nadron.app.Player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.player.ICombatGroup;

public class PVPCombatManager extends BaseCombatManager{

	
	protected final Player player;
	
	public PVPCombatManager(ICombatGroup attackerGroup,
			ICombatGroup defenderGroup, Player player) {
		super(attackerGroup, defenderGroup);
		this.player = player;
	}
	
	@Override
	public void afterFight() {
		super.afterFight();
		sendBattleResult(player);
		ObjectNode combatResult = getCombatResult();
		player.sendMessage(combatResult);
	}
	
}
