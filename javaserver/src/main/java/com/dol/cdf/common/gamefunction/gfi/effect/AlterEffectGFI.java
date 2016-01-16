package com.dol.cdf.common.gamefunction.gfi.effect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.jelly.combat.CombatResultType;
import com.jelly.combat.context.CBConst.GroupDefine;
import com.jelly.combat.context.CBContext;
import com.jelly.combat.event.CombatEventType;
import com.jelly.hero.BaseSkill;
import com.jelly.hero.IHero;
import com.jelly.player.IFighter;

public class AlterEffectGFI extends BaseBuffEffectGFI {

	private static final Logger logger = LoggerFactory.getLogger(AlterEffectGFI.class);
	
	private static float SKILL_UP_RATE = (Float)AllGameConfig.getInstance().gconst.getConstant(GameConstId.SKILL_STAR_UP_RATE);
	
	public AlterEffectGFI() {
		super(GFIId.GFI_EFFECT_ALTER);
	}

	public int getType() {
		return (Integer) parameter.getParamter("type");
	}

	@Override
	public void cancel(GameContext context) {
		// nothing to do
	}

	@Override
	public void execute(GameContext context) {
		// type:int:1,ratios:intarray:10;10;10,when:int:2,effect:int
		CBContext cb = context.getCBContextParam();
		int when = (Integer) parameter.getParamter("when");
		int ratioInteger = getRadio(context.getI());
		float ratio = ratioInteger / 100f;
		BaseSkill skill = context.getS();
		int skillId = skill.getSkill().getId();
		if (when == context.getE()) {
			switch (getType()) {
			/** 输出伤害百分比 */
			case IHero.HURT_TARGET_RATIO:
//				float tmpRatio = ratio;
				ratio += SKILL_UP_RATE * skill.getStarCount();
				
//				if(ratio != tmpRatio) {
//					System.out.println("before "+tmpRatio+"after "+ratio+skill.getSkill().toString());
//				}
				// 伤害目标只计算一次
				caclulateDamagePoint(cb.getAttacker(), cb.getDefender(), ratio, cb, skill);
				break;
			/** 受到伤害百分比 */
			case IHero.HURT_SELF_RATIO:
				caclulateDamagePoint(cb.getDefender(), cb.getAttacker(), ratio, cb, skill);
				break;
			/** HP上限百分比 */
			case IHero.HP_MAX_RATIO:
				int hpMax = context.getBuffOwner().getHpMax();
				int currHp = context.getBuffOwner().getHp();
				int changedMHp = -(int) (hpMax * ratio);
				changedMHp *= (1 + skill.getEffectRate());
				if (currHp + changedMHp > hpMax) {
					// 只有战斗前可以提高血量上限
					if (when == CombatEventType.BEFORE_FIGHT && currHp >= hpMax) {
						//logger.info("血量增加为：" + (currHp + changedMHp) + " hpMax：" + hpMax +"changedMHp:" + changedMHp + "currHp:"+currHp +"buffOwner:" + context.getBuffOwner().getHero().getRoleId()+"skillId:" + context.getS().getSkill().getId());
						context.getBuffOwner().addHpWithContext(changedMHp);
					} else {
						int tarChange = hpMax - currHp;
						if(tarChange < 0) {
							tarChange = 0;
						}
						context.getBuffOwner().addHpWithContext(tarChange);
					}
				} else {
					context.getBuffOwner().addHpWithContext(changedMHp);
				}
				if (when == CombatEventType.BEFORE_ATTACKING_OTHER && changedMHp != 0) {
					cb.addBeforeAttackChangeHP(skillId, changedMHp);
				}
				break;
			/** HP当前百分比 */
			case IHero.HP_CURRENT_RATIO:
				int currentHp = context.getBuffOwner().getHp();
				int changedCHp = -(int) (currentHp * ratio);
				changedCHp *= (1 + skill.getEffectRate());
				context.getBuffOwner().addHpWithContext(changedCHp);
				if (when == CombatEventType.BEFORE_ATTACKING_OTHER && changedCHp != 0) {
					cb.addBeforeAttackChangeHP(skillId, changedCHp);
				}
				break;
			/** 攻击 */
			case IHero.PROP_STRENGTH:
				doPropChange(CombatResultType.ATTACKER_ATTACK, cb, ratioInteger,skill.getEffectRate() );
				break;
			/** 防御 */
			case IHero.PROP_DEFENCE:
				doPropChange(CombatResultType.DEFENDER_DEFENCE, cb, ratioInteger ,skill.getEffectRate() );
				break;
			/** 速度 */
			case IHero.PROP_SPEED:

				break;
			/** 敏捷 */
			case IHero.PROP_AGILITY:
				doPropChange(CombatResultType.ATTACKER_HIT, cb, ratioInteger , skill.getEffectRate());
				break;
			/** 暴击率 */
			case IHero.PROP_CRIT_RATE:
				break;

			default:
				break;
			}
		}
	}

	private void doPropChange(int targetProp, CBContext cb, int ratioInteger, float charkraRate) {
		float prop = cb.getCombatResultValue(targetProp);
		prop += (ratioInteger - 100);
		prop += (charkraRate * 100);
		cb.putCombatResultValue(targetProp, prop);
	}

	private void caclulateDamagePoint(IFighter fighterA, IFighter fighterB, float ratio, CBContext cb, BaseSkill skill) {
		float damagePoint = cb.getCombatResultValue(CombatResultType.DAMAGE_POINT);
		boolean hitOver = cb.getCombatResultBoolean(CombatResultType.IF_HIT_OVER);
		// 造成伤害 正常情况下是when=2
		if (!hitOver && damagePoint == 0) {
			// 攻击者攻击力
			float attack = fighterA.getStrength() * ((ratio * 100 + cb.getCombatResultValue(CombatResultType.ATTACKER_ATTACK)) / 100f);
			// 获得防守者的防御力
			
			int def = fighterB.getDefence();
			if(cb.isBeastFight() &&  fighterA.getIdx() == GroupDefine.attacker.ordinal()) {
				def = fighterA.getDefence();
			}
			float defence = def * ((ratio * 100 + cb.getCombatResultValue(CombatResultType.DEFENDER_DEFENCE)) / 100f);
			if (attack >= defence) {
				damagePoint = 2 * attack - defence;
			} else {
				damagePoint = attack * attack / defence;
			}
			// float damageRate = defence > 0 ? attack / (attack + defence) :
			// (attack - defence) / attack;
			//
			// // 计算伤害
			// damagePoint = (attack * damageRate) * ratio;

			// 计算是否有暴击
			int critRate = (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.CRIT_RATE);
			boolean isCrit = Rnd.getRandomPercent(critRate / 100f);
			if (isCrit) {
				cb.putCombatResultBoolean(CombatResultType.IF_CRIT, true);
				damagePoint *= (Float) AllGameConfig.getInstance().gconst.getConstant(GameConstId.CRIT_MULTIPLE);
			}
//			damagePoint = damagePoint == 0 ? 1 : damagePoint;
			
			if(logger.isDebugEnabled()){
				logger.debug("before damage :{}" , damagePoint);
			}
			//计算查克拉加成
			damagePoint *= (1+skill.getEffectRate());
			if(logger.isDebugEnabled()){
				logger.debug("after damage :{}, effectrate={}, ID={}" , damagePoint, skill.getEffectRate(), skill.getSkill().getId());
			}
			cb.putCombatResultValue(CombatResultType.DAMAGE_POINT, damagePoint);
			cb.putCombatResultBoolean(CombatResultType.IF_HIT_OVER, true);
			// fighterB.addHpWithContext(-(int)damagePoint);
			// 吸血，正常情况下是when=3
		} else if ((Integer) parameter.getParamter("when") == CombatEventType.AFTER_ATTACK_OTHER) {
			// 吸血直接修改数值
			fighterB.addHpWithContext(-(int) (damagePoint * ratio));
			// 是否有伤害减免的buff
		} else if ((Integer) parameter.getParamter("when") == CombatEventType.AFTER_BEING_ATTACKED) {
			// fighterB.addHp(-(int)(damagePoint * ratio));
			damagePoint = damagePoint * ratio;
			cb.putCombatResultValue(CombatResultType.DAMAGE_POINT, damagePoint);
		} else if ((Integer) parameter.getParamter("when") == CombatEventType.ATTACK_BACK) {
			// damagePoint = damagePoint * ratio;
			int realDamage = -(int) (damagePoint * ratio);
			cb.setBhp(realDamage);
			fighterA.addHp(-(int) (damagePoint * ratio));
		}

	}

}
