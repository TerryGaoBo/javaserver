package com.dol.cdf.common.constant;

public class GameConstId {

	public static final int CONST_START_ID = 0;

	public static final int BAG_EQP = CONST_START_ID + 1;

	public static final int BAG_ITEM = BAG_EQP + 1;

	public static final int BAG_MATERIAL = BAG_ITEM + 1;

	public static final int ITEM_CATEGORY = BAG_MATERIAL + 1;

	/**
	 * 暴击几率
	 */
	public static final int CRIT_RATE = ITEM_CATEGORY + 1;

	/**
	 * 暴击倍数
	 */
	public static final int CRIT_MULTIPLE = CRIT_RATE + 1;

	// 好友上限
	public static final int FRIENDS_MAX_NUM = CRIT_MULTIPLE + 1;

	// 初始体力
	public static final int INIT_ENERGY_NUM = FRIENDS_MAX_NUM + 1;

	// 每次购买的数量
	public static final int BUY_ENERGY_NUM = INIT_ENERGY_NUM + 1;
	// 每半小时增加的体力
	public static final int ENERGY_INC = BUY_ENERGY_NUM + 1;

	// 购买体力消耗的金币
	public static final int ENERGY_COST = ENERGY_INC + 1;

	// 忍者合成等级限制
	public static final int PER_LV_INC = ENERGY_COST + 1;

	public static final int MAX_QUALITY_LV = PER_LV_INC + 1;

	// 购买银币消耗的金币
	public static final int SILVER_COST = MAX_QUALITY_LV + 1;

	// 每次购买的数量
	public static final int BUY_SILVER_NUM = SILVER_COST + 1;

	// 后援团添加的属性基础数值
	public static final int AID_INC = BUY_SILVER_NUM + 1;

	// 开格价格
	public static final int OPEN_SLOT_PRICE = AID_INC + 1;

	// 打劫得到的银币
	public static final int FORCE_SILVER = OPEN_SLOT_PRICE + 1;
	//每天点击忍者获得的银币的次数
	public static final int TAKE_NUM = FORCE_SILVER + 1;
	//每次点击忍者获得的银币的数量
	public static final int TAKE_SILVER = TAKE_NUM + 1;
	
	public static final int COST_MUTI = TAKE_SILVER + 1;
	//忍界大战消耗的忍币
	public static final int RS_COIN = COST_MUTI + 1;
	//忍界大战忍者所需等级
	public static final int RS_ROLE_LEVEL = RS_COIN + 1;
	//重置副本花费
	public static final int FB_RESET_COST = RS_ROLE_LEVEL + 1;
	//初级专区第一次给的忍者ID
	public static final int FIRST_NORMAL_REC = FB_RESET_COST + 1;
	//高级抓取第一个给的忍者ID
	public static final int FIRST_MID_REC = FIRST_NORMAL_REC + 1;
	//购买基金所消耗的金币数
	public static final int FUND_COST = FIRST_MID_REC + 1;
	//高级抓取第二个给的忍者ID
	public static final int SECOND_MID_REC = FUND_COST + 1;
	//高级抓取第一个给的忍者ID后的CD时间
	public static final int FIRST_MID_REC_CD = SECOND_MID_REC + 1;
	// 购买PVP次数消耗的金币
	public static final int PVP_COUNT_COST = FIRST_MID_REC_CD + 1;

	// 刷新商城消耗的基础金币
	public static final int REFRESH_SHOPPING_COST = PVP_COUNT_COST + 1;
	// 刷新黑市消耗的基础银币
	public static final int REFRESH_BLACK_COST = REFRESH_SHOPPING_COST + 1;

	// 购买PVP增加次数
	public static final int PVP_INC = REFRESH_BLACK_COST + 1;
	//
	public static final int OLD_SERVER_MAX = PVP_INC + 1;
	//技能升星提升几率
	public static final int SKILL_STAR_UP_RATE = OLD_SERVER_MAX + 1;
	//	超值商品每日刷新次数
	public static final int HIGH_VALUE_GOODS = SKILL_STAR_UP_RATE + 1;
	//	普通副本每日可重置次数
	public static final int SWEEP_COMMON_STAGE = HIGH_VALUE_GOODS + 1;
	//	精英副本每日可重置次数
	public static final int SWEEP_ELITE_STAGE = SWEEP_COMMON_STAGE + 1;
	//	精英副本重置价格
	public static final int ELITE_RESET_COST = SWEEP_ELITE_STAGE + 1;
	
	//	是否开启月卡功能
	public static final int PREFER_IS_OPEN_MONTHCARD = ELITE_RESET_COST + 1;
	//	是否开启兑换礼包码功能
	public static final int PREFER_IS_OPEN_GIFTCODE = PREFER_IS_OPEN_MONTHCARD + 1;	
	
	//	军团显示数量
	public static final int TEAM_PAGE_IN_MAX_TEAMS = PREFER_IS_OPEN_GIFTCODE + 1;
	//	创建军团消耗的金币与银币
	public static final int TEAM_CREATE_COSTS = TEAM_PAGE_IN_MAX_TEAMS + 1;
	//	创建军团要求的忍者村等级
	public static final int TEAM_CREATE_VILLAGE_LEVEL = TEAM_CREATE_COSTS + 1;
	//	玩家同一时间最多可申请加入的军团数量
	public static final int TEAM_MAX_APPLY_TEAMS = TEAM_CREATE_VILLAGE_LEVEL + 1;
	//	入团等级
	public static final int TEAM_JOIN_LEVEL = TEAM_MAX_APPLY_TEAMS + 1;
	//	军团公告文字最大长度
	public static final int TEAM_MAX_ANNOUNCE_LEN = TEAM_JOIN_LEVEL + 1;
	//	军团名称长度
	public static final int TEAM_NAME_LEN = TEAM_MAX_ANNOUNCE_LEN + 1;
	//	军团介绍文字最大长度
	public static final int TEAM_MAX_INTRO_LEN = TEAM_NAME_LEN + 1;
	
	//	首次重置翻倍
	public static final int FIRST_PAY_RESET = TEAM_MAX_INTRO_LEN + 1;
	
	//	弹劾军团长批准投票数
	public static final int TEAM_DELATE_PASS_VOTES = FIRST_PAY_RESET + 1;
	
	// 军团捐赠金币与财富转换比率
	public static final int TEAM_DONATE_GOLD_CONV_WEALTH = TEAM_DELATE_PASS_VOTES + 1;
	
	// 商店刷新次数消耗金币
	public static final int REFRESH_SHOP_COST = TEAM_DONATE_GOLD_CONV_WEALTH + 1;
	// 商店购买最大刷新次数
	public static final int REFRESH_SHOP_TIMES = REFRESH_SHOP_COST + 1;
	
	public static final int REFRESH_NINJASHOP_COST = REFRESH_SHOP_TIMES + 1; //忍界商店商店刷新消耗金币
	public static  final int REFRESH_NINJASHOP_TIMES = REFRESH_NINJASHOP_COST + 1; //忍界商店商店刷新次数
	
	public static final int REFRESH_ARENASHOP_COST = REFRESH_NINJASHOP_TIMES + 1; //竞技商店商店刷新消耗金币
	public static final int REFRESH_ARENASHOP_TIMES = REFRESH_ARENASHOP_COST + 1; //竞技商店商店刷新次数
	
	public static final int WIN_ARENA_GET_POINT = REFRESH_ARENASHOP_TIMES + 1; //胜利一场获得的竞技点
	public static final int LOSE_ARENA_GET_POINT = WIN_ARENA_GET_POINT + 1; //失败异常获得的竞技点
	
	public static final int SEND_NINJA_REWARD = LOSE_ARENA_GET_POINT+1;//派遣增加贡献值和军团财富
	
}
