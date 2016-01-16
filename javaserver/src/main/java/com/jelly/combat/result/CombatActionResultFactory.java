package com.jelly.combat.result;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jelly.combat.CombatResultType;
import com.jelly.combat.context.CBContext;
import com.jelly.combat.event.CombatEventType;
import com.jelly.player.IFighter;

public class CombatActionResultFactory {

	public static CombatActionResult createRemovedBuffsResult(CBContext context){
		if(context.getBeforeAttackRemovedBuffs().isEmpty()) return null;
		CombatActionResult result = new CombatActionResult();
		result.setList(Sets.newHashSet(context.getBeforeAttackRemovedBuffs()));
		result.setIdx(context.getAttacker().getIdx());
		result.setWhen(CombatEventType.BUFF_REMOVED);
		context.putBattleMsg(result);
		return result;
	}
	
	public static CombatActionResult createBeforeFightResult(CBContext context,List<Integer> sids, int idx){
		if(sids.isEmpty()) return null;
		CombatActionResult result = new CombatActionResult();
		result.setList(Sets.newHashSet(sids));
		result.setIdx(idx);
		result.setWhen(CombatEventType.BEFORE_FIGHT);
		context.putBattleMsg(result);
		return result;
	}
	
	public static CombatActionResult createChangeHPsResult(CBContext context,int when){
		if(context.getBeforeAttackChangeHPs().isEmpty()) return null;
		CombatActionResult result = new CombatActionResult();
		result.setWhen(when);
		result.setHpMap(Maps.newHashMap(context.getBeforeAttackChangeHPs()));
		result.setIdx(context.getAttacker().getIdx());
		context.putBattleMsg(result);
		return result;
	}
	
	public static CombatActionResult createDodgeResult(CBContext context, int skillId,int when){
		CombatActionResult result = new CombatActionResult();
		result.setSid(skillId);
		result.setWhen(when);
		result.setIdx(context.getAttacker().getIdx());
		result.setAhp(context.getAhp());
		result.setBhp(context.getBhp());
		result.setChp(context.getChp());
		result.setCid(context.getCskill());
		result.setStatus(ActionStatus.ATTACKEE_DODGE);
		result.setCloseBufA(Lists.newArrayList(context.getCloseBufAs()));
		result.setCloseBufB(Lists.newArrayList(context.getCloseBufBs()));
		context.putBattleMsg(result);
		return result;
	}
	
	public static CombatActionResult createHitResult(CBContext context,int skillId,int when){
		CombatActionResult result = new CombatActionResult();
		result.setWhen(when);
		result.setSid(skillId);
		result.setIdx(context.getAttacker().getIdx());
		result.setAhp(context.getAhp());
		result.setBhp(context.getBhp());
		result.setChp(context.getChp());
		result.setCid(context.getCskill());
		if(context.getCombatResultBoolean(CombatResultType.IF_CRIT)){
			result.setStatus(ActionStatus.ATTACKER_CRIT);
		}else{
			result.setStatus(ActionStatus.ATTACKER_HIT);
		}
		result.setCloseBufA(Lists.newArrayList(context.getCloseBufAs()));
		result.setCloseBufB(Lists.newArrayList(context.getCloseBufBs()));
		context.putBattleMsg(result);
		return result;
	}
	
	
	public static CombatActionResult createDeadResult(CBContext context,IFighter fighter){
		CombatActionResult result = new CombatActionResult();
		result.setWhen(CombatEventType.ON_DEAD);
		result.setIdx(fighter.getIdx());
		context.putBattleMsg(result);
		return result;
	}
	public static CombatActionResult createGameOverResult(CBContext context){
		CombatActionResult result = new CombatActionResult();
		result.setWhen(CombatEventType.GAME_OVER);
		context.putBattleMsg(result);
		return result;
	}
	
	public static CombatActionResult createRecoverResult(CBContext context, IFighter fighter,int skillId, int recoverHp){
		CombatActionResult result = new CombatActionResult();
		result.setWhen(CombatEventType.RECOVER);
		result.setIdx(fighter.getIdx());
		result.setAhp(recoverHp);
		result.setSid(skillId);
		context.putBattleMsg(result);
		return result;
	}
	
	
	/**
	 * 发送反击消息
	 * @param context
	 * @param fighter
	 * @param skillId
	 * @return
	 */
	public static CombatActionResult createAttackBackResult(CBContext context, IFighter fighter,int skillId){
		CombatActionResult result = new CombatActionResult();
		result.setWhen(CombatEventType.ATTACK_BACK);
		result.setIdx(fighter.getIdx());
		result.setBhp(context.getBhp());
		result.setSid(skillId);
		result.setStatus(ActionStatus.ATTACKER_HIT);
		context.putBattleMsg(result);
		return result;
	}
}
