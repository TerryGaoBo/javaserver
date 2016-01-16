package com.jelly.combat.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jelly.combat.ICombatManager;
import com.jelly.combat.PVPCombatManager;
import com.jelly.combat.result.CombatActionResult;
import com.jelly.player.ICombatGroup;
import com.jelly.player.IFighter;

public class CBContext {
	private static final Logger logger = LoggerFactory.getLogger(CBContext.class);

	public static class CBBattleMessage {
		public Map<Integer, Integer> beforeAttackChangeHPs;
		public List<Integer> beforeAttackRemovedBuffs;
		public Integer ahp;
		public Integer bhp;
		public Integer chp;
		public Integer cskill;
		public List<Integer> closeBufA;
		public List<Integer> closeBufB;

		public CBBattleMessage(Map<Integer, Integer> beforeAttackChangeHPs, List<Integer> beforeAttackRemovedBuffs, List<Integer> closeBufA, List<Integer> closeBufB) {
			this.beforeAttackChangeHPs = beforeAttackChangeHPs;
			this.beforeAttackRemovedBuffs = beforeAttackRemovedBuffs;
			this.closeBufA = closeBufA;
			this.closeBufB = closeBufB;
		}
	}

	// 从第一回合开始
	private int currentIndex = 1;

	//
	private boolean mustHit = false;

	private boolean isBeastFight = false;
	
	private boolean isMirrorFight = false;

	private long beastHurtValue = 0;

	private final List<CombatActionResult> battleMsg = new ArrayList<CombatActionResult>();

	private final CBBattleMessage data = new CBBattleMessage(new HashMap<Integer, Integer>(), new ArrayList<Integer>(), new ArrayList<Integer>(), new ArrayList<Integer>());

	public void clearBattleMessage() {
		data.beforeAttackChangeHPs.clear();
		data.beforeAttackRemovedBuffs.clear();
		data.closeBufA.clear();
		data.closeBufB.clear();
		data.ahp = null;
		data.bhp = null;
		data.chp = null;
		data.cskill = null;
	}

	public List<CombatActionResult> getBattleMsg() {
		return battleMsg;
	}

	public Map<Integer, Integer> getBeforeAttackChangeHPs() {
		return data.beforeAttackChangeHPs;
	}

	public void addBeforeAttackChangeHP(Integer skillId, Integer changeHP) {
		this.data.beforeAttackChangeHPs.put(skillId, changeHP);
	}

	public Integer getAhp() {
		return data.ahp;
	}

	public void setAhp(Integer ahp) {
		this.data.ahp = ahp;
	}

	public Integer getBhp() {
		return data.bhp;
	}

	public void setBhp(Integer bhp) {
		this.data.bhp = bhp;
	}

	public Integer getChp() {
		return data.chp;
	}

	public void setChp(Integer chp) {
		this.data.chp = chp;
	}

	public Integer getCskill() {
		return data.cskill;
	}

	public void setCskill(Integer cskill) {
		this.data.cskill = cskill;
	}

	public List<Integer> getBeforeAttackRemovedBuffs() {
		return data.beforeAttackRemovedBuffs;
	}

	public void addBeforeAttackRemovedBuff(Integer removedBuff) {
		this.data.beforeAttackRemovedBuffs.add(removedBuff);
	}

	public List<Integer> getCloseBufAs() {
		return data.closeBufA;
	}

	public List<Integer> getCloseBufBs() {
		return data.closeBufB;
	}

	public void addCloseBufA(Integer removedBuff) {
		this.data.closeBufA.add(removedBuff);
	}

	public void addCloseBufB(Integer removedBuff) {
		this.data.closeBufB.add(removedBuff);
	}

	private ICombatManager combatManager;

	private ICombatGroup lastAttackGroup;

	public CBContext(ICombatManager combatManager) {
		this.combatManager = combatManager;
		for (IFighter fighter : combatManager.getAttackerGroup().getFighters()) {
			fighter.setCBContext(this);
		}
		for (IFighter fighter : combatManager.getDefenderGroup().getFighters()) {
			fighter.setCBContext(this);
		}
	}

	public CBContext() {

	}

	public ICombatGroup getLastAttackGroup() {
		return lastAttackGroup;
	}

	public void setLastAttackGroup(ICombatGroup lastAttackGroup) {
		this.lastAttackGroup = lastAttackGroup;
	}

	public ICombatGroup getNextAttackGroup() {
		if (lastAttackGroup == null) {
			lastAttackGroup = getFirstCombatGroup();
		} else if (lastAttackGroup == combatManager.getAttackerGroup()) {
			lastAttackGroup = combatManager.getDefenderGroup();
		} else if (lastAttackGroup == combatManager.getDefenderGroup()) {
			lastAttackGroup = combatManager.getAttackerGroup();
		}
		return lastAttackGroup;
	}

	private ICombatGroup getFirstCombatGroup() {
		if (combatManager instanceof PVPCombatManager) {
			try {
				int attAgility = combatManager.getAttackerGroup().getFirstFighter().getAgility();
				int defAgility = combatManager.getDefenderGroup().getFirstFighter().getAgility();
				if (attAgility >= defAgility) {
					return combatManager.getAttackerGroup();
				} else {
					return combatManager.getDefenderGroup();
				}
			} catch (Exception e) {
				return combatManager.getAttackerGroup();
			}

		} else {
			return combatManager.getAttackerGroup();
		}

	}

	public ICombatManager getCombatManager() {
		return combatManager;
	}

	public void putBattleMsg(CombatActionResult msg) {
		battleMsg.add(msg);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void incrementCurrentIndex() {
		currentIndex++;
		clearCombatResult();
	}

	/**
	 * 获取回合数
	 * 
	 * @return
	 */
	public int getRound() {
		return currentIndex / 2 + 1;
	}

	private IFighter attacker; // 攻击者
	private IFighter defender; // 被攻击者

	private final Map<Integer, Float> combatResultValue = new HashMap<Integer, Float>();; // 战斗中间结果数值集
	private final Map<Integer, Boolean> combatResultBoolean = new HashMap<Integer, Boolean>(); // 战斗中间结果布尔值集

	public IFighter getAttacker() {
		return attacker;
	}

	public void setAttacker(IFighter attacker) {
		this.attacker = attacker;
	}

	public IFighter getDefender() {
		return defender;
	}

	public void setDefender(IFighter defender) {
		this.defender = defender;
	}

	public void putCombatResultValue(int key, float value) {
		combatResultValue.put(key, value);
	}

	public float getCombatResultValue(int key) {
		if (combatResultValue.containsKey(key)) {
			return combatResultValue.get(key);
		}
		return 0;
	}

	public void putCombatResultBoolean(int key, boolean bool) {
		combatResultBoolean.put(key, bool);
	}

	public boolean getCombatResultBoolean(int key) {
		if (combatResultBoolean.containsKey(key)) {
			return combatResultBoolean.get(key);
		}
		return false;
	}

	public void clearCombatResult() {
		combatResultValue.clear();
		combatResultBoolean.clear();
	}

	public boolean isMustHit() {
		return mustHit;
	}

	public void setMustHit(boolean mustHit) {
		this.mustHit = mustHit;
	}

	public boolean isBeastFight() {
		return isBeastFight;
	}

	public void setBeastFight(boolean isBeastFight) {
		this.isBeastFight = isBeastFight;
	}

	
	public boolean isMirrorFight() {
		return isMirrorFight;
	}

	public void setMirrorFight(boolean isMirrorFight) {
		this.isMirrorFight = isMirrorFight;
	}

	public long getBeastHurtValue() {
		return this.beastHurtValue;
	}

	public void addbeastHurtValue(long value) {
		this.beastHurtValue += value;
	}
}
