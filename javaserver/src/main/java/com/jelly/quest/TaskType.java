package com.jelly.quest;

public enum TaskType {
	//强化任意装备
	ENHANCE(1),
	//合成任意装备
	COMPOSE(2),
	//成功招募忍者
	RECRUIT_SUCESS(3),
	//修炼忍术
	PRATICE_NINJITSU(4),
	//修炼通灵术
	PRATICE_PSYCHIC(5),
	//经验转移
	EXCHANGE_EXP(6),
	//血继限界的转移
	EXCHANGE_BLOOD(7),
	//尾兽之力的注入
	FILL_BEAST(8),
	//在竞技场进行战斗
	PVP(9),
	//竞技场获得胜利
	PVP_WIN(10),
	//完成上忍考试
	FINISH_EXAM(11),
	//在影忍堂进行战斗
	YYT_FIGHT(12),
	//夺宝
	DIG(13),
	//购买体力
	BUY_ENERGY(14),
	//调整任意忍者在布阵中的位置
	LINEUP(15),
	//打劫
	TAKE_FORCE(16),
	//购买月卡
	BUY_CARD(17),
	// 参与忍者大战
	RENZHE_DAZHAN(18);
	
	int id;
	
	public int getId() {
		return id;
	}

	private TaskType(int id) {
		this.id = id;
	}

}
