package com.jelly.combat;



public class CombatCalculateUtils {
	
//
//	/**
//	 * 物理伤害计算
//	 * 
//	 * @param attacker
//	 * @param attackee
//	 */
//	public static void meleeDamageCalculateFlow(IGamePlayer attacker, IGamePlayer attackee){
//		
//		/**
//		 * 初始化context
//		 */
//
//		
//		
//		/**
//		 * 1 预处理 1.1 攻击方预处理 1.2 被攻击方预处理
//		 */
//
//		
//		/**
//		 * 2 伤害前计算，计算伤害的变化和浮动 2.1 计算猛击（猛击直接增加攻击）
//		 */
//		
//
//		// 派发事件
//		attacker.getCombatManager().dispatchEvent(CombatEventType.ON_ATTACKING_OTHER);
//		attackee.getCombatManager().dispatchEvent(CombatEventType.ON_BEING_ATTACKED);
//		
//		/**
//		 * 3 计算伤害 3.1 计算命中 3.2 计算招架 3.3 计算防御免伤 3.4 计算会心 3.5 计算伤害浮动
//		 */
//		// 3.1 计算命中
//
//		
//		// 派发事件
//		attacker.getCombatManager().dispatchEvent(CombatEventType.AFTER_ATTACK_OTHER);
//		attackee.getCombatManager().dispatchEvent(CombatEventType.AFTER_BE_ATTACKED);
//		
//
//	
//				/**
//		 * 4 扣除伤害 4.1 扣除伤害
//		 */
//		// 派发事件
//		attacker.getCombatManager().dispatchEvent(CombatEventType.AFTER_ATTACK_FINISHED);
//		attackee.getCombatManager().dispatchEvent(CombatEventType.AFTER_ATTACKED_FINISHED);
//		
//	}
//	
//	/**
//	 * 魔法伤害
//	 * 
//	 * @param attacker
//	 * @param attackee
//	 */
//	public static void magicDamageCalculateFlow(IGamePlayer attacker, IGamePlayer attackee){
//		
//		/**
//		 * 初始化context
//		 */
//
//		/**
//		 * 1 预处理 1.1 攻击方预处理 1.2 被攻击方预处理
//		 */
//		
//		
//		/**
//		 * 2 伤害前计算
//		 */
//		// 派发事件
//		attacker.getCombatManager().dispatchEvent(CombatEventType.ON_ATTACKING_OTHER);
//		attackee.getCombatManager().dispatchEvent(CombatEventType.ON_BEING_ATTACKED);
//		
//		/**
//		 * 3 计算伤害 3.1 法术命中 3.2 法术伤害 3.2 法术会心 3.2 法术伤害浮动
//		 */
//		// 3.1 计算命中
//		// CombatCalculateFilterFactory.createMagicHitCalculateFilter(attacker).filter(context);
//		// if(context.getCombatResultBoolean(CombatResultType.IF_DODGE)){
//		// // 闪避，中止计算
//		// return;
//		// }
//
//		
//		// 派发事件
//		attacker.getCombatManager().dispatchEvent(CombatEventType.AFTER_ATTACK_OTHER);
//		attackee.getCombatManager().dispatchEvent(CombatEventType.AFTER_BE_ATTACKED);
//		
//		
//	
//		/**
//		 * 4 扣除伤害 4.1 扣除伤害
//		 */
//		// CombatCalculateFilterFactory.createDamageMakeCalculateFilter(attacker).filter(context);
//		
//		// 派发事件
//		attacker.getCombatManager().dispatchEvent(CombatEventType.AFTER_ATTACK_FINISHED);
//		attackee.getCombatManager().dispatchEvent(CombatEventType.AFTER_ATTACKED_FINISHED);
//			
//	}
//	
//
 }
