package com.dol.cdf.common;

/**
 * 用于给客户端发送的code码，对应中文提示信息。在Text.json配置。 相同的提示使用相同的code码
 * 
 * 考虑到将来会有多语言，以后新添加的提示信息，尽量给客户端发送code码
 * 
 * @author zhoulei
 * 
 */
public class MessageCode {

	/** 不正确(只能做逻辑假判断) */
	public final static int FAIL = -1;
	/** 正确为0(只能做逻辑真判断) */
	public final static int OK = 0;

	public final static int ADVENURE_FIGHT = 100;

	public final static int ADVENURE_SWEEP = 101;

	/** 未入包的物品 */
	public final static int GIVE_ITEM = 102;
	/** 夺宝未入背包*/
	public final static int DIG_ITEM = 103;
	
	/** 上忍考试未入包*/
	public final static int EXAM_END_REWARD = 104;
	/** 忍界大战奖励*/
	public final static int WAR_REWARD = 105;
	/** 忍币兑换奖励*/
	public final static int WAR_EXCHANGE = 106;
	/** 购买商品未入背包*/
	public final static int BUY_GOODS = 53;
	
	/** 您的装备不足 */
	public final static int EQUIP_LACK = 994;
	/** 您的道具不足 */
	public final static int ITEM_LACK = 995;
	/** 您的材料不足 */
	public final static int MAT_LACK = 996;
	
	/** 装备栏已满，请清理 */
	public final static int EQUIP_FULL = 997;
	/** 道具栏已满，请清理 */
	public final static int ITEM_FULL = 998;
	/** 材料栏已满，请清理 */
	public final static int MAT_FULL = 999;
	
	/** 背包已满，请清理 */
	public final static int BAG_FULL = 1000;
	/** 服务器正在维护中 */
	public final static int SERVER_MAINTAIN = 1001;
	/** 服务器爆满，请稍后登录 */
	public final static int SERVER_FULL = 1002;
	/** 与服务器的连接已丢失 请重新载入页面 */
	public final static int PLEASE_RELOAD = 1003;
	/** 添加好友成功 */
	public final static int ADD_FRIEND_SUCCESS = 1004;
	/** 目标玩家不存在 */
	public final static int PLAYER_NOT_EXIST = 1005;
	/** 删除好友成功 */
	public final static int REMOVE_FRIEND_SUCCESS = 1006;
	/** 获得{0}{1}个; */
	public final static int EARN_ITEM = 1007;
	/** 获得金币{0} */
	public final static int EARN_GOLD = 1008;
	/** 获得银币{0} */
	public final static int EARN_SILVER = 1009;
	/** 获得经验{0} */
	public final static int EARN_EXP = 1010;
	/** 消耗{0}个{1} */
	public final static int COST_ITEM = 1011;
	/** 消耗{0}金币 */
	public final static int COST_GOLD = 1012;
	/** 消耗{0}银币 */
	public final static int COST_SILVER = 1013;
	/** 名称长度不符 */
	public final static int INVALID_NAME_LENGTH = 1014;
	/** 名称含有非法字符 */
	public final static int INVALID_NAME = 1015;
	/** 名称重复 */
	public final static int DUPLICATE_NAME = 1016;
	/** 背包已满，无法卸下 */
	public final static int CONTAINER_FULL = 1017;
	/** 达到了最大任务完成上限 */
	public static final int MAX_FINISH_QUEST_COUNT = 1018;
	/** 您的等级不足 */
	public final static int LEVEL_NOT_ENOUGH = 1019;
	/** 您的金币不足 */
	public final static int GOLD_NOT_ENUGH = 1020;
	/** 您的查克拉不足 */
	public final static int CAKELA_NOT_ENUGH = 1021;
	/** 您的免费抽取次数已用完 */
	public final static int RAFFLE_NOT_ENUGH = 1022;
	/** 您的免费挖宝次数已用完 */
	public final static int DIG_NOT_ENUGH = 1023;
	/** 您的挑战次数已用完 */
	public final static int CHALLENGE_NOT_ENUGH = 1024;
	/** 已经达到上限，升级建筑吧 */
	public final static int EXAM_MAX_VALID = 1025;
	/** 您的免费学习次数已用完 */
	public final static int STDUY_NOT_ENUGH = 1026;
	/** 忍者数量已经达到上限 */
	public final static int HERO_FULL = 1027;
	/** 发送到邮箱里了 */
	public final static int HERO_TO_MAIL = 1028;
	/** 忍者合成等级不能超过{0}级 */
	public final static int COMPOSE_HERO_LIMIT = 1029;
	/** 邮件发送成功 */
	public final static int SEND_MAIL_SUCCESS = 1030;
	/** 分解成功 */
	public final static int DECOMPOSE_SUCCESS = 1031;
	/** 合成成功 */
	public final static int COMPOSE_SUCCESS = 1032;
	/** 您的体力不足 */
	public final static int ENERGY_NOT_ENUGH = 1033;
	/** 好友已达到上限 */
	public final static int FRIEND_LIMIT = 1034;
	/** 黑名单已达到上限 */
	public final static int BLACKLIST_LIMIT = 1035;
	/** 好友已存在 */
	public final static int FRIEND_EXISTS = 1036;
	/**请清理装备栏，去存放忍者的装备*/
	public final static int EXCHANGE_EQUIP_FULL = 1039;
	/**背包满，奖励进入邮箱*/
	public final static int ITEM_TO_MAIL = 1040;
	
	/**该忍者正在泡温泉中...,请勿打扰*/
	public final static int HERO_IN_PRACTISE = 1041;
	
	/**{0}已完成*/
	public final static int QUEST_FINISH = 1045;
	/**欢迎村长大人!*/
	public final static int MAIL_TITLE_1 = 1049;
	
	/**内容1*/
	public final static int MAIL_CONTENT_1 = 1050;
	
	/**活动奖励*/	
	public final static int MAIL_TITLE_REWARD = 1051;
	
	/**活动奖励,放入邮箱了,去看看吧~*/	
	public final static int MAIL_REWARD_TIP = 1052;
	
	//尾兽活动相关
	public final static int MAIL_BEAST_TITILE1 = 1053;
	public final static int MAIL_BEAST_CONTENT1 = 1054;
	public final static int MAIL_BEAST_TITILE2 = 1055;
	public final static int MAIL_BEAST_CONTENT2 = 1056;
	public final static int MAIL_BEAST_CONTENT2_1 = 1069;
	public final static int MAIL_BEAST_TITILE3 = 1057;
	public final static int MAIL_BEAST_CONTENT3 = 1058;
	//忍界大战相关
	public final static int MAIL_WAR_TITLE = 1059;
	public final static int MAIL_WAR_CONTENT_WIN = 1060;
	public final static int MAIL_WAR_CONTENT_FAIL = 1061;
	
	public final static int MAIL_TITLE_TEAM_DELATECOMMANDER = 0;
	public final static int MAIL_CONTENT_TEAM_DELATECOMMANDER = 0;
	
	// 抓忍者积分活动
	public final static int MAIL_SCORE_RANK_TITILE1 = 0;
	public final static int MAIL_SCORE_RANK_CONTENT1 = 0;
	
	/** 洗练成功 */
	public final static int WASH_SUCCESS = 1062;
	
	/** 月卡100金币 */
	public final static int MONTHLY_PAY_TID = 1065;
	/** 村长大人！为您奉上{0}金币的月卡奖励！您还可以连续领取{1}天。 */
	public final static int MONTHLY_PAY_REWARD = 1066;
	
	public final static int CARD_IS_USED = 6006;//	此卡号已经使用过了
	public final static int CARD_IS_WRONG = 6007;//	卡号错误
	public final static int CARD_CAN_NOT_USED = 6008;	//您已经领取过了，不能重复领取
	public final static int CARD_LEN = 6009;	//兑换码长度必须{0}位
	/** 您的专精点不足 */
	public final static int SPOINT_NOT_ENUGH = 6016;
	/**获得{0}专精点*/
	public final static int SPOINT_GAIN = 6012;
	/**不能使用其他渠道的兑换码*/
	public final static int BAN_OTHER_CHANNEL = 6026;
	/**VIP升级奖励*/
	public final static int VIP_BONUS = 6031;
	/**您的月卡已经成功续费到{0}天，请尽情的享受忍界的福利吧！*/
	public final static int MONTHLY_TIP = 6032;
	/**月卡续费*/
	public final static int MONTHLY_TIP_TITLE = 6034;
	/** 您的银币不足 */
	public final static int SILVER_NOT_ENUGH = 6062;
	/** vip2等级开启黑市。 */
	public final static int VIPLEVEL_NOT_ENUGH = 6063;
	
	/** S级忍者降临！{0}果然是预言之子，抓到了超强忍者 {1} 相助！ */
	public final static int BROADCAST_HERO_S = 6074;
	/**S级忍术重现忍界！{0}习得了逆天忍术 {1} ！ */
	public final static int BROADCAST_SKILL_S = 6075;	
	/**新帮会*/
	public final static int BROADCAST_CREATE_TEAM = 6114;
	/**开除军团成员*/
	public final static int BROADCAST_KICK_TEAM_MEMBER = 6154;
	/**忍界大战派出抢夺小队超时(海外印尼版本)*/
	public final static int RENJIE_ROB_TIMEOUT = 6087;	
	
	/** SS忍者的提示 */
	public final static int BROADCAST_HERO_SS = 6101;
	/**SS忍术的提示 */
	public final static int BROADCAST_SKILL_SS = 6102;
	/**军团名称已经存在*/
	public final static int TEAM_NAME_EXIST = 6111;
	
	public final static int TEAM_DUPLICATE = 6115;
	
	public final static int TEAM_NO_EXIST = 6115; // 军团不存在
	public final static int TEAM_DISMISS = 6122; // 军团已被解散
	public final static int TEAM_JOIN_OEHTERTEAM = 6132; // 已经加入其他的军团
	public final static int TEAM_RIGHTS_REFUSED = 6148; // 成员权限不足
	public final static int TEAM_MEMBER_FULL = 6117; // 很成员已满, 无法加入
	public final static int TEAM_NO_TEAM_MEMBER = 6153;	//	成员不存在
	public final static int TEAM_NO_MYTEAM = 6149; // 没有所属军团
	public final static int TEAM_ANNOUNCEMENT_REFUSE_WORDS = 6150; // 军团名称包含违禁字
	public final static int TEAM_NAME_REFUSE_WORDS = 6112;	//	军团名称中包含违禁字
	public final static int TEAM_INTRO_REFUSE_WORDS = 6113; // 军团介绍包含违禁字
	public final static int TEAM_NOT_ENOUGH_TITLE = 6151; // 权限已无法再调整该成员的职务
	public final static int TEAM_SPONSOR_DELATE_TIMES = 6152; // 当前无法进行弹劾	
	public final static int TEAM_KICK_MEMBER = 6128;	//	开除军团成员
	public final static int TEAM_CROSS_AGREE_JOIN_APPLY = 6155;
	public final static int TEAM_MEMBER_LEAVE = 6157;	//	退出军团
	public final static int TEAM_MODIFY_MEMBER_TITLE = 6158;
	public final static int TEAM_REFUSE_JOIN_TEAM = 6159;
	
	public final static int MAIL_TITLE_TEAM_DISMISS = 6123;//军团解散邮件标题ID
	public final static int MAIL_CONTENT_TEAM_DISMISS = 6124; // 军团解散邮件内容ID
	
	public final static int BROADCAST_APPOINT_NEW_TEAM_COMMANDER = 6126;
	public final static int CHAT_TEAM_UPGRADE_PROMPT = 6182;
	public final static int CHAT_TEAM_DONATE_PROMPT = 6180;
	
	public final static int MAIL_TEAM_DELATE_COMMANDER_TITLE = 6137; // 军团长被弹劾邮件标题ID
	public final static int MAIL_TEAM_DELATE_COMMANDER_CONTENT = 6138; // 军团长被弹劾邮件内容ID
	
	public final static int MAIL_TITLE_TEAM_AGREE_JOIN = 6133; 	// 申请加入军团通过邮件标题ID—6133
	public final static int MAIL_CONTENT_TEAM_AGREE_JOIN = 6134;	// 申请加入军团通过邮件内容ID

	public final static int MAIL_TITLE_TEAM_KICK_MEMBER = 6129;	//	；   成员被踢出军团邮件标题ID—6129
	public final static int MAIL_CONTENT_TEAM_KICK_MEMBER = 6130;// 成员被踢出军团错误提示ID—6128
	
	public final static int TEAM_APPLY_JOIN_TOO_MUCH = 6118; // 超出可申请加入军团上限
	
	public final static int TEAM_DONATE_WEALTH_COMPLETE = 6179; // 捐款完成
	public final static int TEAM_STRONGHOLD_ERROR = 6118; // 军团据点不能占领
	
	public final static int TEAM_STRONGHOLD_STILLHOLD_ARMY = 6230; // 该据点已经被部队占领
	public final static int TEAM_WAR_NOTOPEN = 6118; // 军团战未开启
	public final static int TEAM_STRONGHOLD_NUMERROR = 6118; // 据点最多三个忍者
	public final static int TEAM_WAR_CANNOT_SENDROLES_TO_SH = 6254; // 军团战期间不可派出部队忍者到据点
	public final static int TEAM_WAR_CANNOT_HEROISDEAD = 6118; // 忍者已死亡
	public final static int TEAM_WAR_OPEN_TITLE = 6224; // 军团战开始邮件ID
	public final static int TEAM_WAR_OPEN_CONTENT = 6225; // 军团战开始邮件内容
	public final static int TEAM_WAR_WIN_TITLE = 6258; // 军团战胜利邮件ID
	public final static int TEAM_WAR_WIN_CONTENT = 6259; // 军团战胜利邮件内容
	public final static int TEAM_WAR_LOSE_TITLE = 6260; // 军团战失败邮件ID
	public final static int TEAM_WAR_LOSE_CONTENT = 6261; // 军团战失败邮件内容
	public final static int TEAM_WAR_DRAW_TITLE = 6262; // 军团战平局邮件ID
	public final static int TEAM_WAR_DRAW_CONTENT = 6263; // 军团战平局邮件内容
	
	public final static int TEAM_WAR_CANNOT_DISSOLVE = 6257; // 军团战期间不可解散军团
	public final static int TEAM_WAR_CANNOT_LEAVE = 6256; // 军团战期间不可退出军团
	public final static int TEAM_WAR_CANNOT_DISMISS = 6255; // 军团战期间不可开除团员
	public final static int TEAM_WAR_CANNOT_CANCELROLES_FROM_ARMY = 6253; // 军团战期间不可撤回部队内忍者
	public final static int TEAM_WAR_CANNOT_SENDROLES_TO_ARMY = 6252; // 军团战期间不可派出忍者到部队
	public final static int TEAM_DONATE_WAEALTH = 6179; // 捐款完成
	public final static int TEAM_CANCEL_ROLES_FROM_ARMY_COMPLETE =  6212; // 撤回忍者成功
	public final static int TEAM_SEND_ROLES_TO_ARMY_COMPLETE = 6206; // 派遣忍者成功
	
	public final static int TEAM_ARMY_FULL = 6208; // 部队人数已满
	public final static int TEAM_CANNOT_MORE_ARMY = 6207; // 超出能够派出到部队忍者数量上限
	public final static int TEAM_CANNOT_UPGRADE = 6183;
	public final static int TEAM_ARMY_CANCELHERO_TITLE = 6218;
	public final static int TEAM_ARMY_CANCELHERO_CONTENT = 6219;
	
	public final static int TEAM_CANNOT_DONATE_MORE_WEALTH = 6178;
	
	public final static int BROADCAST_RAFFLE_NINJA_START = 6176;
	public final static int BROADCAST_RAFFLE_NINJA_END = 6177;
	/**这个属性已经无法再降低了！**/
	public final static int HEROTRAIN_PROPENPUGH = 6248;
	public final static int HEROTRAIN_OTHERPROPISFULL = 6249;
	
	////////// 军团战提示-----////////////////
	////////////////////////////////////////////////
	public final static int TEAM_WAR_CANNOT_FIGHT = 6267; // 前置据点未完成
	public final static int TEAM_STRONGHOLD_STILLHOLD = 6227; // 该据点已被我方军团占领！
	
	public final static int WAR_TIPS_6268 = 6269;//军团战争已结束
	public final static int WAR_TIPS_6269 = 6269;//本日军团战争结束
	public final static int WAR_TIPS_6270 = 6270;//本日军团战争开始
	
	public final static int WAR_TIPS_6271 = 6271;// 胜利，排名奖励标题
	public final static int WAR_TIPS_6272 = 6272;// 胜利，排名奖励内容
	
	public final static int WAR_TIPS_6273 = 6273;// 失败，排名奖励标题
	public final static int WAR_TIPS_6274 = 6274;// 失败，排名奖励内容
	
	public final static int WAR_TIPS_6275 = 6275;// 上阵忍者为空
	
	public final static int SHOP_TIPS_1 = 6372;// 当天刷新次数达上线
	
	public final static int JINGJIDIAN_1 = 6378;//您的竞技币不足。
	public final static int RENBI_1 = 3062;//您的忍币不足
	
	public final static int MESSAGE_7095 = 7095; // 军团战平局 邮件title
	public final static int MESSAGE_7096 = 7096; // 军团战平局 邮件content
	
	public static int getErrorTxtID(String type)
	{
		if ("gold".equals(type)) {
			return MessageCode.GOLD_NOT_ENUGH; 
		} else if ("silver".equals(type)) {
			return MessageCode.SILVER_NOT_ENUGH;
		} else if ("coin".equals(type)) {
			return MessageCode.RENBI_1;
		} else if("arenapoint".equals(type)){
			return MessageCode.JINGJIDIAN_1;
		}
		return MessageCode.FAIL;
	}
}
