package io.nadron.app.impl;

public enum OperResultType {

	// 忍者抽奖
	RZCJ(1),

	// 竞技场挑战
	PVP(2),

	// 技能学习
	STUDY_SKILL(3),

	// 挖宝结果
	DIG(4),
	// 上忍考试快速战斗
	SRKSKSZD(5),
	// 实验室
	SYS(6),
	// 扫荡
	SWEEP(7),
	// 强化
	ENHANCE(8),
	// 领取建筑奖励
	PRODUCE(9),
	// 合成
	COMPOSE(10),
	// 道具开物品
	ROLL_ITEM(11),
	// 验证充值订单
	PAY_VALID(12),
	// 创建名称
	CREATE_NAME(13),
	// 任务相关
	QUEST(14), 
	// 删除邮件
	MAIL(15),
	// 领取每日奖励
	DAILY_REWARD(16),
	
	// 技能替换
	REPLACE_SKILL(17),
	// 上忍考试选择跳到哪一关
	SRKSXZ(18),
	// 开启后援团
	OPEN_ADIS(19),
	
	// 拉面馆金币奖励
	LMG(20),
	// 购买道具
	BUY_ITEM(21),
	// 刷新道具
	REFRESH_ITEM(22),
	
	// 刷新道具
	WASH(23),
	// 重置副本
	RESET_FUBEN(24),
	//升级技能
	UPSKILL(25),
	//礼包获得
	GIFT_GAIN(26),
	// 刷新商城
	REFRESH_SHOP(27),
	//购买商城物品
	BUY_SHOP(28),
	//军团
	TEAM(29),
	// 精炼装备
	REFINE_EQUIP(30),
	// 忍者训练
	TRAIN(31),
	//军团据点
	TEAMSTRONGHOLD(32)
	;

	int id;

	private OperResultType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
