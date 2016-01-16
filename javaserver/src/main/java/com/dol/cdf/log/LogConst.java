package com.dol.cdf.log;

import com.dol.cdf.common.MessageCode;

public class LogConst {

	/**
	 * 日志类型
	 */
	public static final int LOG_LOADING = 10000; // 加载点
	public static final int LOG_ONLINE = 10002; // 同时在线
	public static final int LOG_LOGIN = 10003; // 登录
	public static final int LOG_LOGOUT = 10004; // 退出
	public static final int LOG_ITEM = 10005; // 玩家道具流水
	public static final int LOG_SHOP = 10006; // 商店交易
	public static final int LOG_PAYMENT = 10007; // 充值
	public static final int LOG_MONEY = 10008; // 玩家货币流水

	/**
	 * 货币类型
	 */
	public static final int MONEY_GOLD = -1; // 金币
	public static final int MONEY_SILVER = -2; // 银币


	// 道具相关
	public static final int ITEM_SELL = 1; // 卖出道具
	public static final int ITEM_USE = 2; // 道具使用
	public static final int ITEM_ENCHANGE = 3; // 强化道具
	// 任务
	public static final int TASK_FINISH = 4; //每日任务完成数量奖励
	public static final int LOG_TASK = 5; // 玩家任务
	// 远征
	public static final int BUY_ENERGY = 6; // 购买行动力

	/** 竞技场排名奖励 */
	public static final int ARENA_REWARD = 7;

	/** 礼包奖励 */
	public static final int OPER_TYPE_ROLLITEM = 8;
	/** 每日登录奖励 */
	public static final int ACTIVE_REWARD = 9;

	/** 背包 购买格子 */
	public static final int PACKAGE_OPEN_SLOT = 10;//开背包格子
	public static final int CLEAR_CD = 11;//清CD

	public static final int BUILDING_GIVE = 12;//建筑物产出

	public static final int ITEM_COMPOSE = 13;//道具合成

	public static final int ITEM_DECOMPOSE = 14;//装备分解
	public static final int STUDY_SKILL = 15;//学习技能
	public static final int REFRESH_TEAM = 17;//刷新队伍
	public static final int HERO_PRATISE = 18;//忍者修炼
	public static final int RAFFLE_HERO = 19;//抽忍者

	public static final int REFRESH_TASK = 20;//刷新任务

	public static final int BUY_VALUE = 21;

	public static final int EXAM_OPTION_COST = 22;//上忍考试选择关卡

	public static final int EXAM_QUICK_COST = 24;//上忍考试快速战斗

	public static final int BUY_SILVER = 25; // 购买银币

	public static final int CLEAR_CD_ENHANCE = 26; // 清理强化CD

	public static final int RECRUIT_FIGHT = 27; // 影忍堂招募

	public static final int EXCHANGE_ITEM = 28; // 转移英雄给的脱下来的道具放到背包
	
//	public static final int SELL = 29; // 出售

	public static final int UP_BUILDING_COST = 30;//升级建筑消费
	
	public static final int GET_FROM_MAIL = 31;//从邮件里提取
	public static final int BEAST_INJECT = 32;//尾兽技能注入
	public static final int EXCHANGE_BLOOD = 33;//血迹转移
//	public static final int SWEEP_COST = 34;//扫荡扣金币
	public static final int FIGHT_BEAST_SILVER = 35;//挑战尾兽人柱力给予的奖励
	public static final int WAR_REWARD = 36;//忍界大战的奖励
	public static final int WAR_BUY_ITEM = 37;//忍界大战的购买道具
	public static final int WASH_ACCESSORY = 38;//洗练
	public static final int PAY_ADD_GOLD = 39;	//充值加金币
	public static final int GM_GIVE = 40;	//GM给予
	public static final int LAMIAN_TAKEFORCE = 41;	//拉面屋打劫
	public static final int MAINSCENE_TAKEFORCE = 42;	//主界面打劫小人
	public static final int REFRESH_WARITEM = 43;	//刷新忍币商店
	public static final int CARD_REWARD = 44;	//礼包兑换奖励
	public static final int RESET_FUBEN = 45;	//重置副本
	public static final int GIVEUP_SKILL = 46;	//放弃技能
	public static final int UP_STAR_SKILL = 47;	//技能升星
	public static final int GIFT_GAIN = 48;	//礼包获得
	public static final int BUY_FUND = 49;	//购买基金
	public static final int WSZN_FLIGHT = 50;	//尾兽之怒直接挑战
	
	public static final int BUY_PVP_TIMES = 51; // 购买竞技场挑战次数
	
	public static final int BUY_SHOP_REFRESH_TIMES = 52; // 购买商店刷新次数
	public static final int BUY_SHOP_ITEM = 53; // 购买商店与黑市物品
	
	public static final int REFINE_ACC = 54;
	public static final int TRAINING_HERO = 55;//培养忍者
	
	public static final int SHOP_BUY_ITEM = 56;//商店购买道具
	
	public static final int ARENA_RESULT = 57;// 竞技场挑战结果
	
	/**
	 * 与MessageCode相同的日志，目的是记录并提示，发到邮件里用 从100开始
	 */
	public static final int ADVENURE_FIGHT = MessageCode.ADVENURE_FIGHT; // 远征战斗奖励
	public static final int ADVENURE_SWEEP = MessageCode.ADVENURE_SWEEP; // 远征扫塔奖励
	public static final int GIVE_ITEM = MessageCode.GIVE_ITEM;	//使用道具
	public static final int DIG_ITEM = MessageCode.DIG_ITEM; //挖宝
	
	public static final int EXAM_END_REWARD = MessageCode.EXAM_END_REWARD;//104 上忍考试

	///////////////----====++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++/////////////////////
	
	public static final int ARENA_JINGJI_LOG_1  = 200; // 竞技场挑战胜利
	public static final int ARENA_JINGJI_LOG_2  = 201; // 竞技场挑战失败
	public static final int ARENA_JINGJI_LOG_3 = 202;// 竞技场排名奖励
	public static final int RENJIE_LOG_1 = 203;// 忍界大战胜利
	public static final int RENJIE_LOG_2 = 204;// 忍界大战打劫
	
	public static final int SHOP_JINGJI_1 = 205;// 竞技商店购买 
	public static final int SHOP_RENJIE_1 = 206;// 忍界商店 
	public static final int SHOP_PLAYERSHOP_1 = 207;// 个人商店购买 
}
