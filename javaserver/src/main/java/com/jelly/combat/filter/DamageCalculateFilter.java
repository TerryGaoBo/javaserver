package com.jelly.combat.filter;

import com.jelly.combat.CombatResultType;
import com.jelly.combat.context.CBContext;

public class DamageCalculateFilter implements ICombatCalculateFilter {

	@Override
	public void filter(CBContext context) {
	
		//read from context
		float attackerAttack = context.getCombatResultValue(CombatResultType.ATTACKER_ATTACK);
		float attackeeDefence = context.getCombatResultValue(CombatResultType.DEFENDER_DEFENCE);
		
		// 计算伤害率
		// 防御大于0时，伤害率 = 攻击 / (攻击 + 防御)
		// 防御小于0时，伤害率 = (攻击 - 防御) / 攻击
		float damageRate = attackeeDefence > 0 ? attackerAttack / (attackerAttack + attackeeDefence) : (attackerAttack - attackeeDefence) / attackerAttack;
		
		// 计算伤害
		float damagePoint = attackerAttack * damageRate;
		
		//write to context
		context.putCombatResultValue(CombatResultType.DAMAGE_POINT, damagePoint);
	}

}
