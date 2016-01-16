package com.jelly.combat.event;


public interface CombatEventType {

	
	/**
	 *战斗结束
	 * 
	 */
	public static final int GAME_OVER = -1;
	
	//0.开场前；1.攻击前；2攻击时（主动技能）；3攻击后；4被攻击前；5被攻击时(反击)；6死亡时;7领悟；8buff关闭；9buff释放
	/**
	 * 开场前(开始战斗之前之前)
	 * 
	 */
	public static final int BEFORE_FIGHT = 0;
	
	
	/**
	 * 进行攻击之前
	 * 
	 */
	public static final int BEFORE_ATTACKING_OTHER = 1;
	
	/**
	 * 攻击时（主动技能）
	 */
	public static final int ON_ATTACKING_OTHER  = 2;

	/**
	 * 攻击后
	 */
	public static final int AFTER_ATTACK_OTHER = 3;
	
	/**
	 * 进行攻击之前
	 * 
	 */
	public static final int BEFORE_BEING_ATTACKED = 4;
	
	/**
	 * 正在攻击时（程序特殊处理了。为了和数据一致）
	 */
	public static final int ON_BEING_ATTACKED = ON_ATTACKING_OTHER;
	
	
	/**
	 * 被攻击后(客户端的反击？)
	 */
	public static final int AFTER_BEING_ATTACKED = 5;
	
	
	/**
	 * 死亡
	 */
	public static final int ON_DEAD = 6;
	
	/**
	 * 复活
	 */
	public static final int RECOVER = 7;
	
	
	/**
	 * buff删除
	 */
	public static final int BUFF_REMOVED = 8;
	
	/**
	 * 反击
	 */
	public static final int ATTACK_BACK = 9;

	/**
	 * 领悟新技能
	 */
	public static final int GROUP_SKILL = 100; 
	
}
