package com.jelly.combat;

import io.nadron.app.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.config.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.combat.context.CBConst;
import com.jelly.combat.context.CBContext;
import com.jelly.combat.result.CombatActionResultFactory;
import com.jelly.player.BaseCombatGroup;
import com.jelly.player.ICombatGroup;
import com.jelly.player.IFighter;

public class BaseCombatManager implements ICombatManager {

	private static final Logger logger = LoggerFactory.getLogger(BaseCombatManager.class);

	public final static ObjectMapper jackson = ObjectMapperFactory.createGameCombatObjectMapper();

	private final ICombatGroup attackerGroup;

	private final ICombatGroup defenderGroup;

	protected final CBContext cb;

	public static final String COMBAT_COMMAND = "fightRes";

	ObjectNode ato;
	
	ObjectNode deo;
	
	public BaseCombatManager(ICombatGroup attackerGroup, ICombatGroup defenderGroup) {
		this.attackerGroup = attackerGroup;
		this.defenderGroup = defenderGroup;
		cb = new CBContext(this);
	}

	public void start() {
		//long l = System.currentTimeMillis();
		beforeFight();
		startFightRound();
		afterFight();
		//logger.info("combat duration:{}ms....", (System.currentTimeMillis() - l));
	}

	public void beforeFight() {
		this.ato = ((BaseCombatGroup) this.getAttackerGroup()).toJson();
		this.deo = ((BaseCombatGroup) this.getDefenderGroup()).toJson();
		attackerGroup.getFirstFighter().setEnemy(defenderGroup.getFirstFighter());
		defenderGroup.getFirstFighter().setEnemy(attackerGroup.getFirstFighter());

		cb.setAttacker(attackerGroup.getFirstFighter());
		cb.setDefender(defenderGroup.getFirstFighter());
		attackerGroup.getFirstFighter().activeGroupSkill();

		if (defenderGroup.getFirstFighter().isDead() == false) {
			cb.setAttacker(defenderGroup.getFirstFighter());
			cb.setDefender(attackerGroup.getFirstFighter());
			defenderGroup.getFirstFighter().activeGroupSkill();
		}

		checkCombatIsOver();
		if (!cb.getCombatResultBoolean(CombatResultType.IF_GAME_OVER)) {
			attackerGroup.getFirstFighter().activeFight();
			defenderGroup.getFirstFighter().activeFight();
		}
		
		// 初始化战斗数据
	}

	public void afterFight() {
		attackerGroup.unActive();
		defenderGroup.unActive();
	}

	private void checkCombatIsOver() {
		boolean isOver = true;
		for (IFighter fighter : attackerGroup.getFighters()) {
			if (!fighter.isDead()) {
				isOver = false;
				break;
			}
		}
		if (isOver) {
			defenderGroup.doWin();
			cb.putCombatResultBoolean(CombatResultType.IF_GAME_OVER, true);
		}else {
			isOver = true;
			for (IFighter fighter : defenderGroup.getFighters()) {
				if (!fighter.isDead()) {
					isOver = false;
				}
			}
			if (isOver) {
				attackerGroup.doWin();
				cb.putCombatResultBoolean(CombatResultType.IF_GAME_OVER, true);
			}
		}
	}

	private void startFightRound() {
		if (!cb.getCombatResultBoolean(CombatResultType.IF_GAME_OVER)) {
			do {
				ICombatGroup nextGroup = cb.getNextAttackGroup();
				IFighter currentFighter = nextGroup.getNextFighter();

				// TODO !currentFighter.getEnemy().isDead() 多人模式不适用
				if (currentFighter != null) {
					//test master 1
//					if (logger.isDebugEnabled()) {
//						logger.debug("currentFighter index ={}, currentFighter RoleId = {}, hp = {}", currentFighter.getIdx(), currentFighter.getHero().getRoleId(), currentFighter.getHp());
//					}
					cb.setAttacker(currentFighter);
					cb.setDefender(currentFighter.getEnemy());
					currentFighter.cast();
					// //处理当前玩家被反弹死的的逻辑
					if (currentFighter.isDead()) {
						IFighter nextFighter = nextGroup.getNextFighter();
						if (nextFighter == null) {
							nextGroup.doFail();
							if (attackerGroup != nextGroup) {
								attackerGroup.doWin();
							} else {
								defenderGroup.doWin();
							}
							break;
						}
					}
				} else {
					nextGroup.doFail();
					if (attackerGroup != nextGroup) {
						attackerGroup.doWin();
					} else {
						defenderGroup.doWin();
					}
					break;
				}
				cb.incrementCurrentIndex();
			} while (cb.getCurrentIndex() < CBConst.MAX_ROUNT);
		}
		CombatActionResultFactory.createGameOverResult(cb);
	}

	@Override
	public ICombatGroup getAttackerGroup() {
		return this.attackerGroup;
	}

	@Override
	public ICombatGroup getDefenderGroup() {
		return this.defenderGroup;
	}

	public ObjectNode getCombatResult() {
		ObjectNode res = jackson.createObjectNode();
		res.put("fighters", jackson.createArrayNode().add(ato).add(deo));
		res.put("win", this.getAttackerGroup().isWin() ? 1 : 0);
		res.put("reward", getRewardJson());
		res.put("combat", jackson.convertValue(cb.getBattleMsg(), ArrayNode.class));
		ObjectNode msg = jackson.createObjectNode();
		msg.put(COMBAT_COMMAND, res);
		return msg;
	}

	public ObjectNode getRewardJson() {
		return null;
	}

	public void sendBattleResult(Player player) {
		// List<CombatActionResult> battleMsg = cb.getBattleMsg();
		// List<CombatActionResult> tmpMsg = Lists.newArrayList();
		// for (int i = 0; i < battleMsg.size(); i++) {
		// tmpMsg.add(battleMsg.get(i));
		// if ((i+1) % 10 == 0) {
		// player.sendMessage("combat", jackson.convertValue(tmpMsg,
		// ArrayNode.class));
		// tmpMsg.clear();
		// }
		// }
		// player.sendMessage("combat", jackson.convertValue(tmpMsg,
		// ArrayNode.class));
	}

}
