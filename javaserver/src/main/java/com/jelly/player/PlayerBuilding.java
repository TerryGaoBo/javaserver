package com.jelly.player;

import io.nadron.app.Player;
import io.nadron.app.impl.OperResultType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PlayerBuilding extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerBuilding.class);
	/**
	 * 建筑等级 key id value level
	 */
	@JsonProperty("bl")
	Map<Integer, BuildInstance> builds = Maps.newHashMap();
	
	public void upLevel(int type, Player player) {
		BuildInstance b = getBuild(type);
		if (b != null) {
			Building building = config.buildings.getBuilding(type);
			int[] upLevel = building.getUpLevel();
			int maxLv = upLevel.length;
			int needPlayerLv = upLevel[b.getLevel()];
			if (player.getProperty().getLevel() < needPlayerLv) {
				player.sendMiddleMessage(MessageCode.LEVEL_NOT_ENOUGH);
				return;
			}
			if (maxLv <= b.getLevel()) {
				logger.error("建筑已经达到等级上限lv={},maxLv={}", b.getLevel(), maxLv);
				return;
			}
			int tarLv = b.getLevel() + 1;
			VariousItemEntry[] upItem = config.buildings.getUpItems(type, tarLv);
			int code = VariousItemUtil.checkBonus(player, upItem, false);
			if (code != MessageCode.OK) {
				player.sendMiddleMessage(code);
				return;
			}
			VariousItemUtil.doBonus(player, upItem, LogConst.UP_BUILDING_COST, false);
			b.setLevel(tarLv);
			appendChangeMap("bd", type, b.toJson());
		}
	}

	public void checkOpenBuilding(int playerLv,Player player,boolean needSend) {
		Collection<Building> allBuilding = AllGameConfig.getInstance().buildings.getAllBuilding();
		for (Building building : allBuilding) {
			if (building.getUpLevel() != null  && playerLv >= building.getUpLevel()[0] && !builds.containsKey(building.getId())) {
				builds.put(building.getId(), new BuildInstance());
				if (needSend) {
					int dayFinishCount = BuildingType.getBuildType(building.getId()).getDayFinishCount(player);
					player.getBuilding().appendChangeMap("bd", building.getId(), player.getBuilding().getBuild(building.getId()).toJson(dayFinishCount));
				}
				
			}
		}
	}
	
	public boolean checkMaxFinishAndAddFunCd(int buildingType, Building building, Player player,int logId, int idx) {
		BuildingType buildType = BuildingType.getBuildType(buildingType);
		if (getTodayFinish(buildingType) + 1 < buildType.getDayFinishCount(player)) {
			if (logId == LogConst.DIG_ITEM) {
				addCd(building);
			}else {
				addFunCd(building,idx);
			}
			
		}
		if (isMaxFinish(buildingType, player)) {
			int code = VariousItemUtil.doBonus(player, building.getFuncCost()[idx], logId, false);
			if (code != MessageCode.OK) {
				player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
				return false;
			}
		}
		return true;
	}
	
	public void checkOpenBuilding(int playerLv,Player player) {
		checkOpenBuilding(playerLv, player, true);
	}
	@Override
	public String getKey() {
		return "buildings";
	}

	public int getBuildLevel(int type) {
		BuildInstance b = getBuild(type);
		return b == null ? 0 : b.getLevel();
	}

	public boolean isCding(int type, int idx) {
		return getBuild(type).isCding(idx);
	}

	public boolean isCding(int type) {
		return getBuild(type).isCding(0);
	}
	public boolean isCdingWithDelta(int type, int minute) {
		BuildInstance build = getBuild(type);
		if (build == null) {
			logger.error("isCdingWithDelta type:{} is null",type);
			return false;
		}
		return build.isCdingWithDelta(0,minute);
	}
	

	public void addCd(int type, int duration, int idx) {
		BuildInstance build = getBuild(type);
		if (build != null) {
			build.addCd(idx, duration);
			appendChangeMap("bd", type, build.toJson());
		} else {
			logger.error("addCd type is null . type = {}", type);
		}
	}

	public void addCd(int type, int duration) {
		addCd(type, duration, 0);
	}

	public void addCd(int type, int[] duration) {
		addCd(type, duration[0]);
	}
	
	public void addCd(Building building) {
		addCd(building.getId(), building.getCd()[0]);
	}

	/**
	 * 功能CD添加
	 * @param building
	 */
	public void addFunCd(Building building) {
		addFunCd(building, 0);
	}
	/**
	 * 功能CD添加
	 * @param building
	 * @param idx
	 */
	public void addFunCd(Building building,int idx) {
		addCd(building.getId(), building.getCd()[idx + 1],idx+1);
	}
	
	public void addFunCdWithDuration(Building building,int idx,int duration) {
		addCd(building.getId(), duration,idx+1);
	}
	
	public boolean isFunCding(int type, int idx) {
		return getBuild(type).isCding(idx+1);
	}

	public boolean isFunCding(int type) {
		return getBuild(type).isCding(1);
	}
	
	public boolean isForbid(int type, int time, int idx) {
		return getBuild(type).isForbid(idx, time);
	}

	public boolean isForbid(int type, int time) {
		return getBuild(type).isForbid(0, time);
	}

	public BuildInstance getBuild(int type) {
		return builds.get(type);
	}

	public int getTodayFinish(int type) {
		BuildInstance build = getBuild(type);
		return build.getTodayFinish();
	}

	/**
	 * 注意一定要在最后检测 达到了最大完成个数 如果未达到则添加今日完成次数
	 * @param player 添加了VIP的检测
	 */
	public boolean isMaxFinish(int type, Player player) {
		BuildingType buildType = BuildingType.getBuildType(type);
		return isMaxFinish(type, buildType.getDayFinishCount(player));
	}
	
	/**
	 * 注意一定要在最后检测 达到了最大完成个数 如果未达到则添加今日完成次数
	 */
	private boolean isMaxFinish(int type,int dayFinish) {
		BuildInstance build = getBuild(type);
		boolean isMax = build.getTodayFinish() >= dayFinish;
		if (!isMax) {
			build.addTodayFinish();
			appendChangeMap("bd", type, build.toJson(dayFinish));
		}
		return isMax;
	}
	
	/**
	 * 购买次数 TODO是否有其他建筑也会购买次数，造成收益
	 * @param player
	 * @param type
	 * @return
	 */
	public boolean buyValue(Player player, int type) {
		BuildInstance build = getBuild(type);
		Building building = config.buildings.getBuilding(type);
		if (building.getFuncCost() == null) {
			logger.error("没有配置功能消耗");
			return false;
		}
		int code = VariousItemUtil.doBonus(player, building.getFuncCost()[0], LogConst.BUY_VALUE, false);
		if (code != MessageCode.OK) {
			logger.error("金钱不足");
			return false;
		}
		build.reduceTodayFinish();
		appendChangeMap("bd", type, build.toJson(BuildingType.getBuildType(type).getDayFinishCount(player)));
		return true;
	}
	
	/**
	 * 添加vip次数
	 * @param player
	 */
	public void addPvpTimes(Player player) {
		BuildInstance build = getBuild(7);
		int times = (Integer)config.gconst.getConstant(GameConstId.PVP_INC);
		build.addTodayFinish(-times);
		appendChangeMap("bd", 7, build.toJson(BuildingType.getBuildType(7).getDayFinishCount(player)));
	}

	/**
	 * 清理CD
	 * 
	 * @param player
	 * @param type
	 * @return 是否清理成功
	 */
//	public boolean clearCd(Player player, int type) {
//		BuildInstance build = getBuild(type);
//		Building building = config.buildings.getBuilding(type);
//		if (building.getClearcd() == null) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("没有配置清CD所消耗的金币 buildID ＝ {}", building);
//			}
//			
//		} else {
//			if (build.getClearTimes() >= building.getClearcd().length) {
//				logger.info("达到最大购买次数上限 clearTImes ＝ {}", build.getClearTimes());
//				return false;
//			}
//			int needGold = building.getClearcd()[build.getClearTimes()];
//			VariousItemEntry item = new VariousItemEntry("gold", needGold);
//			int doBonus = VariousItemUtil.doBonus(player, item, LogConst.CLEAR_CD, false);
//			if (doBonus != MessageCode.OK) {
//				logger.error("金币不足gold = {},needGold={}", player.getProperty().getGold(), needGold);
//				return false;
//			}
//		}
//		if (building.getClearcd()== null || building.getClearcd().length == 1) {
//			build.clearCd(false);
//		} else {
//			build.clearCd(true);
//		}
//		appendChangeMap("bd", type, build.toJson());
//		return true;
//	}
	
	public void resetCd(Player player, int type,int idx) {
		BuildInstance build = getBuild(type);
		build.resetCdTime(idx);
		appendChangeMap("bd", type, build.toJson());
	}

	
	/**
	 * 周期奖励，目前有拉面馆
	 * @param player
	 * @param type
	 * @param param
	 */
	public void produce(Player player, int type, JsonNode param) {
		Building building = config.buildings.getBuilding(type);
		int buildLevel = getBuildLevel(type);
		if(buildLevel == 0) {
			logger.error("外挂还是什么，怎么能够没开启等级就能领取奖励,userId:{},name:{}",player.getRole().getUserId(),player.getRole().getName());
			return;
		}
		VariousItemEntry[] produce = building.getProduce();
		if (produce == null) {
			logger.error("procude is null buildId = {}", type);
			player.sendResult(OperResultType.PRODUCE,MessageCode.FAIL);
			return;
		}
		//奖励的内容
		List<VariousItemEntry> items = Lists.newArrayList();
		//奖励的索引
		int i = 0;
		//5分钟的差值
		if (isCdingWithDelta(type,5)) {
			// 处于CD中
			if(player.getRole() != null) {
				logger.error("cding error cd time:{},userId:{},name:{}",getBuild(type).getCdTime(0),player.getRole().getUserId(),player.getRole().getName());
			}
			player.sendResult(OperResultType.PRODUCE, MessageCode.FAIL);
			return;
		}
		for (VariousItemEntry p : produce) {
			// 建筑升级增加的个数
			float upValue = p.getAmount() * ((buildLevel - 1) * building.getUpValue()[i] / 100f);
			items.add(new VariousItemEntry(p.getType(), p.getAmount() + (int) upValue));
			i++;
		}
		addCd(type, building.getCd());
//		//拉面馆给VIP奖励
//		if (type == BuildingType.LMG.getId() && player.getProperty().isVip()) {
//			Vip vip = config.activitys.getVip(player.getProperty().getVipLv());
//			if (vip.getItem() != null) {
//				for (VariousItemEntry variousItemEntry : vip.getItem()) {
//					items.add(variousItemEntry);
//				}
//			}
//		}
		VariousItemUtil.doBonus(player, items, LogConst.BUILDING_GIVE, true);

		player.sendResult(OperResultType.PRODUCE, VariousItemUtil.itemToJson(items));
	}

	@Override
	public JsonNode toWholeJson() {
		return null;
	}
	
	public JsonNode toAllJson(Player player) {
		ObjectNode obj = jackson.createObjectNode();
		for (Entry<Integer, BuildInstance> b : builds.entrySet()) {
			obj.put(b.getKey() + "", b.getValue().toJson(BuildingType.getBuildType(b.getKey()).getDayFinishCount(player)));
		}
		ObjectNode jsoNode = jackson.createObjectNode();
		jsoNode.put("bd", obj);
		return jsoNode;
	}

	public Map<Integer, BuildInstance> getBuilds() {
		return builds;
	}
}
