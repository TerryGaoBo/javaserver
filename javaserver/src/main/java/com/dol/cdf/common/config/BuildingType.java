package com.dol.cdf.common.config;

import io.nadron.app.Player;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.Vip;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.jelly.player.VipConstant;

public enum BuildingType {
	// 忍者屋
	RZW(1),
	// 忍术学校
	RSXX(2),
	// 拉面馆
	LMG(3) {
		@Override
		public int getDayFinishCount(Player player) {
			Vip vip = AllGameConfig.getInstance().activitys.getVip(player.getProperty().getVipLv());
			return vip.getTimes()[VipConstant.TAKE_FORCE_TIMES];
		}
	},
	// 夺宝
	DB(4),
	// 通灵池
	TLC(5),
	// 温泉
	WQ(6) {
		@Override
		public void open(Player player) {
			sendOpen(player, player.getPractise());
			super.open(player);
		}
	},
	// 竞技场
	PVP(7) {
		@Override
		public void open(Player player) {
			sendOpen(player, player.getArena());
			super.open(player);
		}

		@Override
		public boolean buyValue(Player player) {
			return player.getBuilding().buyValue(player, this.getId());
		}
	},
	// 实验室
	SYS(8),
	// 上忍考场
	SYKC(9) {
		@Override
		public int getDayFinishCount(Player player) {
//			 return 100;
			Vip vip = AllGameConfig.getInstance().activitys.getVip(player.getProperty().getVipLv());
			return vip.getTimes()[VipConstant.EXAM_TIMES];
		}

		@Override
		public void open(Player player) {
			sendOpen(player, player.getExam());
			super.open(player);
		}
	},
	// 尾兽坛
	WST(10) {
		@Override
		public void open(Player player) {
			super.open(player);
		}

		@Override
		public int getDayFinishCount(Player player) {
			return 10000;
		}
	},
	// 集会所
	JHS(11) {
		@Override
		public void open(Player player) {
			sendOpen(player, player.getTask());
			super.open(player);
		}
		// @Override
		// public boolean clearCd(Player player) {
		// if (super.clearCd(player)) {
		// player.getTask().acceptTask(player);
		// return true;
		// }
		// return false;
		// }
	},
	// 影忍堂
	YYT(12) {
		@Override
		public void open(Player player) {
			sendOpen(player, player.getRecruit());
			super.open(player);
		}
	},
	// 尾兽之怒
	WSZN(13);

	int id;

	private BuildingType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void open(Player player) {
		player.getBuilding().appendChangeMap("bd", this.getId(), player.getBuilding().getBuild(this.getId()).toJson(getDayFinishCount(player)));
		//logger.info("OPEN method buidid = {}", this.getId());
	}

	public void close(Player player) {
		player.getBuilding().getBuild(this.getId()).toJson();
		logger.error("no indify CLOSE method buidid = {}" + this.getId());
	}

	/**
	 * 获取今天的次数
	 * 
	 * @return
	 */
	public int getDayFinishCount(Player player) {
		Building building = AllGameConfig.getInstance().buildings.getBuilding(this.getId());
		if (building.getInitValue() == null) {
			return 0;
		}
		int value = building.getInitValue()[0];
		return value;

	}

	// public boolean clearCd(Player player) {
	// logger.info("clearCd method buidid = {}" + this.getId());
	// return player.getBuilding().clearCd(player, this.getId());
	// }

	public boolean buyValue(Player player) {
		logger.info("no indify BUYVALUE method buidid = {}" + this.getId());
		return true;
	}

	public void sendOpen(Player player, DynamicJsonProperty property) {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put(property.getKey(), property.toOpenJson(player));
		player.sendMessage(obj);
	}

	private static final Logger logger = LoggerFactory.getLogger(BuildingType.class);
	static Map<Integer, BuildingType> id2type = Maps.newHashMap();
	static {
		for (BuildingType type : BuildingType.values()) {
			id2type.put(type.getId(), type);
		}
	}

	public static BuildingType getBuildType(int id) {
		return id2type.get(id);
	}

}
