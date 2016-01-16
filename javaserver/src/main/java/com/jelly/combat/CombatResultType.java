package com.jelly.combat;

public interface CombatResultType {

	public static final int ATTACKER_ATTACK = 1; // 攻击方攻击（持续修正）
	public static final int ATTACKER_HIT = 2; // 攻击方战技（持续修正）
	public static final int DEFENDER_DEFENCE = 3; // 被攻击方防御（持续修正）
	public static final int DEFENDER_DODGE = 4; // 被攻击方闪避（持续修正）
	
	public static final int ATTACKER_CRIT_RATE = 9; // 攻击方暴击(修正)
	

	
	public static final int BAN_SKILL = 10; // 禁止使用技能

	public static final int BAN_ACTION = 11; // 禁止行动
	
	
	
	public static final int DAMAGE_TYPE = 50; // 伤害类型
	public static final int DAMAGE_POINT = 51; // 伤害点
	
	public static final int REFLECT_POINT = 52; // 反击点
	public static final int CURE_POINT = 52; // 治疗点
	public static final int CURE_TYPE = 53; // 治疗类型
	public static final int MP_COST_POINT = 54; // 消耗的Mp点数
	public static final int CRIT_MULTIPLE = 55; // 会心倍率
	
	public static final int DRUG_POINT = 60; // 喂毒点数
	public static final int DRUG_DAMAGE = 61; // 喂毒爆发伤害
	public static final int DRUG_BURST_GRADE = 62; // 喂毒爆发等级
	
	public static final int IF_HIT = 101; // 命中与否
	public static final int IF_DODGE = 102; // 闪避与否
	public static final int IF_CRIT = 103; // 是否暴击
	public static final int IF_REFLECT = 105; // 免疫与否
	public static final int IF_HIT_OVER = 106; // 免疫与否
	public static final int IF_GAME_OVER = 107; // 是否游戏结束
	
	
}
