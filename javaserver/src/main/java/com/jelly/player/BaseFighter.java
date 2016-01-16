package com.jelly.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.CharacterManager.RoleGroupWrapper;
import com.dol.cdf.common.gamefunction.IEffectGF;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.buff.BuffFacotry;
import com.jelly.buff.IntermittentBuff;
import com.jelly.combat.CombatResultType;
import com.jelly.combat.PVBeastCombatManager;
import com.jelly.combat.context.CBConst.GroupDefine;
import com.jelly.combat.context.CBContext;
import com.jelly.combat.event.CombatEventHandlerObject;
import com.jelly.combat.event.CombatEventType;
import com.jelly.combat.event.ICombatEventHandler;
import com.jelly.combat.result.CombatActionResultFactory;
import com.jelly.hero.BaseSkill;
import com.jelly.hero.Hero;
import com.jelly.hero.IHero;

public class BaseFighter implements IFighter {

	private final IHero hero;

	/**
	 * 当前血量
	 */
	private int hp;

	/**
	 * 
	 */
	private int idx;

	private IFighter enemy;

	private int uuid;
	
	private CBContext context;

	private final boolean isTest = false;
	public final static int NO_EFFECT_SKILL_ID = 8888;
	public final static BaseSkill NO_EFFECT_SKILL_BASE;
	static {
		Skill neskill = new Skill();
		neskill.setId(NO_EFFECT_SKILL_ID);
		NO_EFFECT_SKILL_BASE = new BaseSkill(neskill);
	}
	
	
	public final static Skill DEFAULT_SKILL = AllGameConfig.getInstance().skills.getSkill(2000);
	public final static BaseSkill DEFAULT_SKILL_BASE = new BaseSkill(DEFAULT_SKILL);
	public final static Skill ATTACK_BACK_SKILL = AllGameConfig.getInstance().skills.getSkill(2001);
	public final static BaseSkill ATTACK_BACK_SKILL_BASE = new BaseSkill(ATTACK_BACK_SKILL);

	// public final static double[] NO_SKILL_RATES = { 0, 0, 0, 0, 0, 0, 0.2621,
	// 0.2097, 0.1678, 0.1342, 0.1074, 0.0859 };
	// public final static double[] NO_SKILL_RATES
	// ={0,0.8,0.6400,0.5120,0.4096,0.3277,0.2621,0.2097,0.1678,0.1342,0.1074,0.0859};
	public final static double[] NO_SKILL_RATES = { 0, 0.6, 0.45, 0.3, 0.2, 0.14, 0.1, 0.05, 0.05, 0.05, 0.05, 0.05 };

	public final static double[] GROUP_SKILL_RATES = { 0, 0.3, 0.5, 0.75, 0.9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
//	public final static double[] GROUP_SKILL_RATES = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	private static final Logger logger = LoggerFactory.getLogger(BaseFighter.class);
	/**
	 * 主动技能
	 */
	Map<Integer, List<BaseSkill>> activeSkills = new HashMap<Integer, List<BaseSkill>>();

	List<Integer> allSkill = Lists.newArrayList();

	/**
	 * 技能的使用次数几率
	 */
	Map<Integer,Integer> skillUseRecord = Maps.newHashMap();

	List<RoleGroupWrapper> roleGroups;

	Map<Integer, Integer> propIncs;
	
	Map<Integer, Integer> skillEffectPlus = Maps.newHashMap();
	
	private int recoverTimes = 0;

	@Override
	public int getRecoverTimes() {
		return recoverTimes;
	}

	@Override
	public void setRecoverTimes(int recoverTimes) {
		this.recoverTimes = recoverTimes;
	}

	public BaseFighter(IHero hero) {
		this.hero = hero;
		this.hp = hero.getHp();
		for (BaseSkill bs : hero.getSkills()) {
			this.allSkill.add(bs.getSkill().getId());
		}
//		this.allSkill.add(1141);
//		addActiveSkill(new BaseSkill(AllGameConfig.getInstance().skills.getSkill(1141)));
		for (BaseSkill bs : hero.getSkills()) {
			addActiveSkill(bs);
			//TEST TODO remove it
//			if (hero instanceof Monster) {
//				addActiveSkill(skillId);
//			}else {
//				addActiveSkill(1113);
//			}
			
		}
		this.skillEffectPlus = hero.getEquipRefineProps();
	}

	public BaseFighter(IHero hero, List<RoleGroupWrapper> roleGroups, Map<Integer, Integer> propIncs) {
		this(hero);
		this.roleGroups = Lists.newArrayList();
		this.propIncs = propIncs;
		for (RoleGroupWrapper roleGroup : roleGroups) {
			Integer sid = roleGroup.getSkill();
			if (sid != null) {
				this.allSkill.add(sid);
				Skill skill = AllGameConfig.getInstance().skills.getSkill(sid);
				addActiveSkill(new BaseSkill(skill));
			}else {
				this.roleGroups.add(roleGroup);
			}
		}
		this.hp = this.getHpMax();

	}

	private void addActiveSkill(BaseSkill bs) {
		Skill skill = bs.getSkill();
		List<BaseSkill> list = activeSkills.get(skill.getWhen());
		if (list == null) {
			list = new ArrayList<BaseSkill>();
			list.add(bs);
			activeSkills.put(skill.getWhen(), list);
		} else {
			boolean containThisSkill = false;
			for (BaseSkill baseSkill : list) {
				if(baseSkill.getSkill().getId() == bs.getSkill().getId()) {
					containThisSkill = true;
					break;
				}
			}
			if(!containThisSkill) {
				list.add(bs);
			}
		}
	}

	@Override
	public void cast() {

		dispatchEvent(CombatEventType.BEFORE_ATTACKING_OTHER);
		getEnemy().dispatchEvent(CombatEventType.BEFORE_BEING_ATTACKED);

		// 检测是否有这个回合需要接触的buff
		// Collection<List<CombatEventHandlerObject>> values =
		// eventHandlers.values();
		// for (List<CombatEventHandlerObject> list : values) {
		// CombatEventHandlerObject[] listCopy = new
		// CombatEventHandlerObject[list.size()];
		// list.toArray(listCopy);
		// for (CombatEventHandlerObject combatEventHandlerObject : listCopy) {
		// combatEventHandlerObject.getHandler().checkEvent();
		// }
		// }
		// 检测when=5的技能
		List<CombatEventHandlerObject> list = eventHandlers.get(CombatEventType.AFTER_BEING_ATTACKED);
		if (list != null && list.size() > 0) {
			CombatEventHandlerObject[] listCopy = new CombatEventHandlerObject[list.size()];
			list.toArray(listCopy);
			for (CombatEventHandlerObject combatEventHandlerObject : listCopy) {
				combatEventHandlerObject.getHandler().checkEvent();
			}
		}

		// 不一定在战斗前取消buff，也有可能在被攻击的时候取消buff
		CombatActionResultFactory.createRemovedBuffsResult(context);
		CombatActionResultFactory.createChangeHPsResult(context, CombatEventType.BEFORE_ATTACKING_OTHER);

		context.clearBattleMessage();

		if (processIfDead()) {
			return;
		}

		if (context.getCombatResultBoolean(CombatResultType.BAN_ACTION)) {
			// 不允许行动，直接返回
			return;
		}
		BaseSkill castSkill = DEFAULT_SKILL_BASE;
		if (!context.getCombatResultBoolean(CombatResultType.BAN_SKILL)) {
			castSkill = randomCastSkill();
		}

		if (isTest) {
			castSkill = DEFAULT_SKILL_BASE;
		}

		//首先计算出技能释放所需数值，查克拉属性值。
		calcBeforeCastSkill(castSkill);
		
		boolean hit = isHit(castSkill);

		if (hit) {

			registerBuffHandler(castSkill);

			dispatchEvent(CombatEventType.ON_ATTACKING_OTHER);

			getEnemy().dispatchEvent(CombatEventType.ON_BEING_ATTACKED);

			getEnemy().dispatchEvent(CombatEventType.AFTER_BEING_ATTACKED);

			//根据acttype来的现在只有1，写固定了
			if (ArrayUtils.contains(castSkill.getSkill().getActType(), 1)) {
				List<ICombatEventHandler> removedBuffList = ((BaseFighter) getEnemy()).removeActHandlers(1);
				if (removedBuffList != null) {
					for (ICombatEventHandler combatEventHandler : removedBuffList) {
						if (combatEventHandler instanceof IntermittentBuff) {
							IntermittentBuff iBuff = ((IntermittentBuff) combatEventHandler);
							context.addCloseBufB(iBuff.getSkillId());
							iBuff.onCancel();
							//碎冰的时候打出1.5倍伤害
							float damagePoint = context.getCombatResultValue(CombatResultType.DAMAGE_POINT);
							damagePoint = damagePoint * 1.5f;
							context.putCombatResultValue(CombatResultType.DAMAGE_POINT, damagePoint);
						}
					}

				}
			}

			// 吸血
			dispatchEvent(CombatEventType.AFTER_ATTACK_OTHER);

			processHit(castSkill);

			context.clearBattleMessage();

			// 处理攻击者和防御者的死亡逻辑
			if (processIfDead()) {
				return;
			}
			if (getEnemy().processIfDead()) {
				return;
			}

			processAttackBack(castSkill);

			// 处理攻击者和防御者的死亡逻辑
			if (processIfDead()) {
				return;
			}
			if (getEnemy().processIfDead()) {
				return;
			}

		} else {
			// 对于主动技能来说即使闪避了也要出发
			IEffectGF[] alter_self = castSkill.getSkill().getAlterSelf();
			if (alter_self != null) {
				for (IEffectGF iEffectGF : alter_self) {
					BuffFacotry.createIntermittentBuff(this, iEffectGF, castSkill).onActive();
				}
			}
			dispatchEvent(CombatEventType.ON_ATTACKING_OTHER);
			// 闪避
			CombatActionResultFactory.createDodgeResult(getCBContext(), castSkill.getSkill().getId(), CombatEventType.ON_ATTACKING_OTHER);
		}

		context.clearBattleMessage();
	}

	@Override
	public boolean processIfDead() {
		//logger.debug("idx {},eventHandlers.size {}:",this.getIdx(), eventHandlers.size());
		if (isDead()) {
			
			CombatActionResultFactory.createDeadResult(context, this);
			dispatchEvent(CombatEventType.ON_DEAD);
			if(isDead()) {
				getEnemy().unregigsterAll();
			}
			return isDead();
		}
		return false;
	}

	private void processHit(BaseSkill castSkill) {
		// 计算伤害值
		float damagePoint = context.getCombatResultValue(CombatResultType.DAMAGE_POINT);
		if (damagePoint != 0) {
			// 计算上下5点上伤害点
			int currentIndex = context.getCurrentIndex();
			int deltaPoint = 0;
			if (currentIndex % 2 == 0) {
				deltaPoint = -currentIndex % 5;
			} else {
				deltaPoint = currentIndex % 5;
			}
			damagePoint += deltaPoint;
			if (damagePoint < 0) {
				damagePoint = 0;
			}
			getEnemy().addHpWithContext(-(int) damagePoint);
		}

		CombatActionResultFactory.createHitResult(context, castSkill.getSkill().getId(), CombatEventType.ON_ATTACKING_OTHER);
	}

	private void processAttackBack(BaseSkill castSkill) {
		if (!context.getCombatResultBoolean(CombatResultType.IF_REFLECT) && castSkill.getSkill().getId().equals(DEFAULT_SKILL.getId())) {
			// TODO 计算反击
			boolean isAttackBack = Rnd.getRandomPercent(ATTACK_BACK_SKILL.getProbability() / 100f);
			if (isAttackBack) {
				registerBuffHandler(ATTACK_BACK_SKILL_BASE);
				getEnemy().dispatchEvent(CombatEventType.ATTACK_BACK);
				CombatActionResultFactory.createAttackBackResult(context, getEnemy(), ATTACK_BACK_SKILL.getId());
			}

		}
	}

	@Override
	public void registerBuffHandler(BaseSkill castSkill) {
		IEffectGF[] alter_self = castSkill.getSkill().getAlterSelf();
		IEffectGF[] alter_target = castSkill.getSkill().getAlterTarget();
		activeBuffs(castSkill, alter_self, alter_target);
	}

	private void activeBuffs(BaseSkill skill, IEffectGF[] alter_self, IEffectGF[] alter_target) {
		if (alter_self != null) {
			for (IEffectGF iEffectGF : alter_self) {
				BuffFacotry.createIntermittentBuff(this, iEffectGF, skill).onActive();
			}
		}
		if (alter_target != null) {
			for (IEffectGF iEffectGF : alter_target) {
				BuffFacotry.createIntermittentBuff(getEnemy(), iEffectGF, skill).onActive();
			}
		}
	}

	public boolean isHit(BaseSkill skill) {
		
		if (context.isMustHit()) {
			return true;
		}
		
		float attackerHit = this.getAgility() * (1 + getCBContext().getCombatResultValue(CombatResultType.ATTACKER_HIT) / 100f);

//		float attackeeDodge = this.getEnemy().getHero().getAgility() * (1 + getCBContext().getCombatResultValue(CombatResultType.DEFENDER_DODGE) / 100f);
		float attackeeDodge = this.getEnemy().getAgility();
		if(context.isBeastFight() &&  idx == GroupDefine.attacker.ordinal()) {
			attackeeDodge = this.getAgility();
		}
		// 命中率计算：命中率 = 攻方命中 / （攻方命中 + 受方闪避）
		// 若闪避<0，则命中率 = (攻方命中 - 受方闪避) / 攻方命中
		// float hitRate = attackeeDodge > 0 ? attackerHit / (attackerHit +
		// attackeeDodge) : (attackerHit - attackeeDodge) / attackerHit;

		// 攻守双方敏捷相同的情况下，命中=95%
		// 敏捷每差2%，命中提升或降低1%
		float hitRate = (((attackerHit - attackeeDodge) / 2.0f) / attackeeDodge) + 0.95f;
		
		if (logger.isDebugEnabled()) {
			//logger.debug("idx : {} skill : {}  hitRate:{}",getIdx(),skill.getId(),hitRate);
		}
		//计算查克拉加成
		hitRate *= (1+skill.getHitRate());
		// 判断命中
		boolean ifHit = Rnd.get() < hitRate;
		if (isTest) {
			ifHit = true;
		}
		;

		return ifHit;
	}
	public void calcBeforeCastSkill(BaseSkill skill){
		//设置命中率
		skill.calcChakraHitRate(this.getHero(), this.getEnemy().getHero());
		//设置chakra效果加成
		skill.calcChakraEffectRate(this.getHero(), this.getEnemy().getHero());
		//计算装备精练效果加成
		float effectRate = skill.getEffectRate();
		if (skillEffectPlus.containsKey(skill.getSkill().getId())) {
			effectRate += skillEffectPlus.get(skill.getSkill().getId()).floatValue() / 100;
			skill.setEffectRate(effectRate);
		}
		if(logger.isDebugEnabled()){
			logger.debug("getHero chakra={}", this.getHero().getChakra());
			logger.debug("getHitRate:{}, effectrate={}, chakra={}" , skill.getHitRate(), effectRate, skill.getSkill().getChakra());
			logger.debug("getEnemy getHero chakra={}", this.getEnemy().getHero().getChakra());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BaseSkill> getSKillByWhen(int when) {
		List<BaseSkill> skills = activeSkills.get(when);
		return skills == null ? Collections.EMPTY_LIST : skills;
	}

	private BaseSkill randomCastSkill() {
		boolean isCastSkill = false;
		int sum = 0;
		List<BaseSkill> activeSkills = getSKillByWhen(CombatEventType.ON_ATTACKING_OTHER);
		for (BaseSkill iSkill : activeSkills) {
			sum += iSkill.getSkill().getProbability();
		}
		isCastSkill = !Rnd.getRandomPercent(NO_SKILL_RATES[activeSkills.size()]);
		if (isTest) {
			isCastSkill = true;
		}
		BaseSkill castSkill = null;
		int rnd = Rnd.get(sum);
		if (isCastSkill) {
			int tempProbability = 0;
			for (BaseSkill iSkill : activeSkills) {
				tempProbability += iSkill.getSkill().getProbability();
				if (tempProbability >= rnd) {
					castSkill = iSkill;
					break;
				}
			}
		}
		// 如果超过次数上限则主动技能中删除改技能
		if (castSkill != null) {
			int skillId = castSkill.getSkill().getId();
			Integer useredTimes = skillUseRecord.get(skillId) ;
			if(useredTimes != null) {
				useredTimes +=1;
			}else {
				useredTimes = 1;
			}
			skillUseRecord.put(skillId, useredTimes);
//			if(castSkill.getSkill().getId() == 1141) {
//				System.out.println("1141========"+useredTimes + " limit = " + castSkill.getSkill().getLimit() + "idx ==" + this.getIdx());
//			}
			if (castSkill.getSkill().getLimit() != 0 && useredTimes >= castSkill.getSkill().getLimit()) {
				activeSkills.remove(castSkill);
			}
			return castSkill;
		} else {
			return DEFAULT_SKILL_BASE;
		}

	}

	private final Map<Integer, List<CombatEventHandlerObject>> eventHandlers = new HashMap<Integer, List<CombatEventHandlerObject>>();

	private final Map<Integer, List<ICombatEventHandler>> actRemovedEventHandlers = new HashMap<Integer, List<ICombatEventHandler>>();

	public List<ICombatEventHandler> removeActHandlers(int type) {
		return actRemovedEventHandlers.remove(type);
	}

	public void removeActHandlers(int type, ICombatEventHandler handler) {
		List<ICombatEventHandler> list = actRemovedEventHandlers.get(type);
		if (list != null) {
			list.remove(handler);
		}
	}

	public void addActHandlers(int type, ICombatEventHandler obj) {
		List<ICombatEventHandler> list = actRemovedEventHandlers.get(type);
		if (list == null) {
			list = new ArrayList<ICombatEventHandler>();
			list.add(obj);
			actRemovedEventHandlers.put(type, list);
		} else {
			list.add(obj);
		}
	}

	@Override
	public void dispatchEvent(int eventType) {
		List<CombatEventHandlerObject> list = eventHandlers.get(eventType);

		if (list != null) {
			CombatEventHandlerObject[] listCopy = new CombatEventHandlerObject[list.size()];
			list.toArray(listCopy);
			// copy出来了及时铲除了所有的buff handler也能执行，例如debuff的效果后续的效果也能执行
			for (CombatEventHandlerObject object : listCopy) {
				object.getHandler().onEvent(eventType);
			}
		}
	}

	@Override
	public void registerEventHandler(ICombatEventHandler handler, int eventType, int priority) {
		List<CombatEventHandlerObject> list = eventHandlers.get(eventType);
		if (list == null) {
			list = new ArrayList<CombatEventHandlerObject>();
			eventHandlers.put(eventType, list);
		} else {
			// 如果相同的时间处理器，则删除掉，（现在判断是否相同用skillID）
			unregigsterEventHandler(handler, eventType);
		}
		// 创建对象
		CombatEventHandlerObject object = new CombatEventHandlerObject(handler, priority);
		list.add(object);
		// 按照优先级排序
		// 直接使用默认排序算法，因为这个列表肯定不长
		Collections.sort(list);
	}
	
	@Override
	public void removeTargetEventHanler(int eventType){
		eventHandlers.remove(eventType);
	}

	@Override
	public void unregigsterEventHandler(ICombatEventHandler handler, int eventType) {
		List<CombatEventHandlerObject> list = eventHandlers.get(eventType);
		if (list == null)
			return;

		CombatEventHandlerObject handlerObject = null;
		for (CombatEventHandlerObject obj : list) {
			if (obj.getHandler().equals(handler)) {
				handlerObject = obj;
				break;
			}
		}
		if (handlerObject != null)
			list.remove(handlerObject);

	}

	@Override
	public IHero getHero() {
		return hero;
	}

	@Override
	public int getIdx() {
		return idx;
	}

	@Override
	public void setIdx(int idx) {
		this.idx = idx;
	}

	@Override
	public int getHp() {
//		if (logger.isDebugEnabled()) {
//			logger.debug("hp = {}, role id = {}, hashcode= {}", hp, hero.getRoleId(), this.hashCode());
//		}
		
		return hp;
	}

	@Override
	public void addHpWithContext(int hp) {
		if (context.getAttacker() == this) {
			context.setAhp(hp);
		} else {
			context.setBhp(hp);
		}
		//人柱力
		if(isDefenderInvincible()) {
			if(hp < 0) {
				context.addbeastHurtValue(Math.abs(hp));
			}
			//不进行HP更改
		}else {
			addHp(hp);
		}
	}

	@Override
	public void setHp(int hp) {
		this.hp = hp;
	}

	@Override
	public void addHp(int hp) {
		if (hp == 0) {
			return;
		}
		this.hp += hp;
		// 之前判断过了，注释掉当前的判断
		// if (this.hp > this.getHero().getHp()) {
		// this.hp = this.getHero().getHp();
		// } else if (this.hp <= 0) {
		//
		// }
//		if (logger.isDebugEnabled()) {
//			logger.debug(this.getIdx() + " current hp = " + this.hp + "role Id = " + hero.getRoleId());
//		}
	}

	@Override
	public boolean isDead() {
		return getHp() <= 0;
	}

	@Override
	public void setEnemy(IFighter enemy) {
		this.enemy = enemy;

	}

	@Override
	public IFighter getEnemy() {
		return enemy;
	}

	@Override
	public void setCBContext(CBContext context) {
		this.context = context;
	}

	@Override
	public CBContext getCBContext() {
		return context;
	}

	@Override
	public void unregigsterAll() {
		eventHandlers.clear();

	}

	@Override
	public String toString() {
		return StringHelper.obj2String(this, null);
	}

	public ObjectNode toJson() {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("id", hero.getRoleId());
		if (isDefenderInvincible()) {
			obj.put("hpRate", PVBeastCombatManager.getBasetCurrentHpRate());
		}else if(context.isMirrorFight()){
			obj.put("hp", getHpMax());
			obj.put("currHp", getHp());
		}else {
			obj.put("hp", getShowHpMax());
			obj.put("currHp", getShowHpMax());
		}
		obj.put("skillList", DynamicJsonProperty.convertToArrayNode(allSkill));
		obj.put("lv", hero.getLevel());
		if (hero instanceof Hero) {
			obj.put("exp", ((Hero) hero).getExp());
			obj.put("lvUp", ((Hero) hero).isLevelUp());
		}

		return obj;
	}
	
	public int getShowHpMax() {
		int radio = 0;
		if(this.roleGroups != null) {
			for (RoleGroupWrapper roleGroup : this.roleGroups) {
				Integer hpMaxRatio = roleGroup.getHpMaxRatio();
				if(hpMaxRatio != null) {
					radio+=hpMaxRatio;
				}
			}
		}
		int hpMax = getHpMax();
		float showHp = hpMax*1.0f*(1+radio/100f);
		return (int)showHp;
	}
	
	private boolean isDefenderInvincible() {
		return context.isBeastFight() && idx == GroupDefine.defender.ordinal();
	}

	@Override
	public void activeGroupSkill() {
		List<BaseSkill> groupSkills = getSKillByWhen(CombatEventType.GROUP_SKILL);
		int size = groupSkills.size();
		if (size <= 0 || !Rnd.getRandomPercent(GROUP_SKILL_RATES[size])) {
			return;
		}
		int i = Rnd.get(size);

		BaseSkill castSkill = groupSkills.get(i);

		registerBuffHandler(castSkill);

		getEnemy().dispatchEvent(CombatEventType.ON_BEING_ATTACKED);
		
		processHit(castSkill);

		context.clearBattleMessage();
		
		if(getEnemy().isDead()) {
			getEnemy().setHp(1);
		}
		
		context.clearCombatResult();
		
		getEnemy().removeTargetEventHanler(CombatEventType.ON_BEING_ATTACKED);
	}

	@Override
	public void activeFight() {
		List<BaseSkill> beforeFightSkill = getSKillByWhen(CombatEventType.BEFORE_FIGHT);
		List<Integer> bsids = Lists.newArrayList();
		for (BaseSkill skill : beforeFightSkill) {
			registerBuffHandler(skill);
			bsids.add(skill.getSkill().getId());
		}
		if (roleGroups != null) {
			// 添加添加组合机的效果
			for (RoleGroupWrapper roleGroup : roleGroups) {
				activeBuffs(NO_EFFECT_SKILL_BASE, roleGroup.getAlterSelf(), roleGroup.getAlterTarget());
			}
		}
		//roleGroups使用一次就删除，解决玩家空血还打不死的bug，原因是roleGroups的属性更改重复添加了
		roleGroups = null;
		CombatActionResultFactory.createBeforeFightResult(context, bsids, getIdx());
		
		//临时添加只适用于组合机添加血量上限使用
		dispatchEvent(CombatEventType.BEFORE_FIGHT);
	}

	@Override
	public int getStrength() {
		return hero.getStrength() + getIncProp(1);
	}

	@Override
	public int getDefence() {
		return hero.getDefence() + getIncProp(2);
	}

	@Override
	public int getHpMax() {
		return hero.getHpMax() + getIncProp(3);
	}

	@Override
	public int getAgility() {
		return hero.getAgility() + getIncProp(4);
	}

	private int getIncProp(int key) {
		if (propIncs != null) {
			Integer prop = propIncs.get(key);
			if (prop != null) {
				return prop;
			}
		}
		return 0;
	}

	@Override
	public int getUuid() {
		return hero.getUuid();
	}

	

}
