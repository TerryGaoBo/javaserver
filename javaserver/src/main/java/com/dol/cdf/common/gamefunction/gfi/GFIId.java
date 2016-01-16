package com.dol.cdf.common.gamefunction.gfi;

/**
 * 
 * @author zhoulei
 * 
 */
public final class GFIId {

	// 反弹或者受攻击后的计算 放到前面为了是的是还减免先计算然后再反弹
	public static final int GFI_EFFECT_REFLECT = 10000;

	// 改变属性的GFI
	public static final int GFI_EFFECT_ALTER = 10001;

	// 禁止使用技能
	public static final int GFI_EFFECT_BAN_SKILL = 10004;

	// 禁止行动
	public static final int GFI_EFFECT_BAN_ACTION = 10005;

	// 复活
	public static final int GFI_EFFECT_RECOVER = 10006;

	// 反弹
	public static final int GFI_EFFECT_DEBUFF = -10008;

	// 判断包格数
	public static final int GFI_CONDITION_CHECK_BAG_GRID = 11000;

	// 随机物品
	public static final int GFI_EFFECT_ROLL_ITEM = 11001;

	// 给予物品
	public static final int GFI_EFFECT_GIVE_ITEM = 11002;
	
	// 增加经验
	public static final int ADD_EXP_ITEM_TYPE = 11003;

}
