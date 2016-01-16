package com.jelly.hero;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PlayerPractise extends DynamicJsonProperty{
	
	private static final Logger logger = LoggerFactory.getLogger(PlayerPractise.class);
	// 普通修炼或者高级修炼的heroID
	@JsonProperty("ph")
	int practiseHeroId = -1;

	@JsonProperty("pt")
	int practiseType = -1;
	
	// 修炼
	public final static short NORMAL_PRACTISE = 0;
	public final static short SENIOR_PRACTISE = 1;
	public final static short SHINING_PRACTISE = 2;//普照的修炼
	
	public static final short WQ_PLAYER_COUNT = 40;

	public int getPractiseHeroId() {
		return practiseHeroId;
	}

	public void setPractiseHeroId(int practiseHeroId) {
		this.practiseHeroId = practiseHeroId;
		addChange("hid", practiseHeroId);
	}

	public int getPractiseType() {
		return practiseType;
	}

	public void setPractiseType(int practiseType) {
		this.practiseType = practiseType;
		addChange("ptype", practiseType);
		addChange("shine", ContextConfig.SENIOR_PRATISE_PLAYER_NAME);
	}
	
	public boolean inPractise(int hid) {
			return (hid == practiseHeroId) && (practiseType != -1);
	}
	
	@Override
	public JsonNode toOpenJson(Player player) {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		//兼容老数据
		if (practiseHeroId != -1 && player.getHeros().getHero(practiseHeroId) == null) {
			practiseHeroId = -1;
		}
		obj.put("hid", practiseHeroId);
		obj.put("ptype", practiseType);
		obj.put("shine", ContextConfig.SENIOR_PRATISE_PLAYER_NAME);
		return obj;
	}
	
	
	
	/**
	 * 修炼 TODO 离线和在线获取奖励通知
	 * 
	 * @param player
	 * @param hid
	 * @param pType
	 * @param hourType
	 */
	public void practise(Player player, Integer hid, int pType) {
		Hero hero = player.getHeros().getHero(hid);
		if(hero == null) {
			logger.error("hero not exist",hid);
			return;
		}
		
		int buildingType = BuildingType.WQ.getId();
		boolean cding = player.getBuilding().isFunCding(buildingType);
		Building building = config.buildings.getBuilding(buildingType);
		if (cding) {
			logger.error("cding ...");
			return;
		}
		if (practiseType != -1) {
			logger.error("practiseType != -1");
			return;
		}
		
		int code = VariousItemUtil.doBonus(player, building.getFuncCost()[pType], LogConst.HERO_PRATISE, false);
		if (code != MessageCode.OK) {
			logger.error("金钱不足");
			return;
		}
		player.getHeros().addHeroState(hid, HeroState.WENQUAN_STATE);
		setPractiseHeroId(hid);
		player.getBuilding().addFunCd(building);
		if (pType == NORMAL_PRACTISE) {
			if(ContextConfig.isShiningPratise()) {
				pType = SHINING_PRACTISE;
			}
		}else if (pType == SENIOR_PRACTISE) {
			ContextConfig.shinePratise(player.getRole().getName());
		}
		setPractiseType(pType);
		//温泉埋点
		LogUtil.doSpaLog((DefaultPlayer)player, hero.getRoleId(), pType+1);
	}

	public void endPractise(Player player, int clientCd) {
		if (practiseType == -1) {
			logger.error("practiseType not start");
			return;
		}
		int buildingType = BuildingType.WQ.getId();
		boolean cding = player.getBuilding().isFunCding(buildingType);
		Building building = config.buildings.getBuilding(buildingType);
		if (!cding) {
			float shiningPercent = 1;
			int temppractiseType = getPractiseType();
			if (temppractiseType == SHINING_PRACTISE) {
				temppractiseType = NORMAL_PRACTISE;
				//光环笼罩加层
				shiningPercent = 1.2f;
			}
			int value = building.getInitValue()[temppractiseType];
			float targetValue =  value*shiningPercent;
			Hero hero = player.getHeros().getHero(getPractiseHeroId());
			if(hero != null){
				hero.addExp((int)targetValue, player);
				hero.removeHeroState(HeroState.WENQUAN_STATE);
//				player.getHeros().removeHeroState(getPractiseHeroId(), HeroState.WENQUAN_STATE);
				player.getHeros().appendChangeMap("hes", getPractiseHeroId(), hero.toJson());
			}
			
			setPractiseType(-1);
			// TODO 发送更改消息
		}else {
			if (clientCd <= 10) {
				player.sendResult(OperResultType.PRODUCE,MessageCode.FAIL);
				return;
			}
			Hero hero = player.getHeros().getHero(getPractiseHeroId());
			if(hero != null){
				hero.removeHeroState(HeroState.WENQUAN_STATE);
			}
			setPractiseType(-1);
			setPractiseHeroId(-1);
			player.getBuilding().resetCd(player, buildingType,1);
		}
		player.sendResult(OperResultType.PRODUCE,MessageCode.OK);
	}

	@Override
	public String getKey() {
		return "pracInfo";
	}

	@Override
	public JsonNode toWholeJson() {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("hid", practiseHeroId);
		obj.put("ptype", practiseType);
		obj.put("shine", ContextConfig.SENIOR_PRATISE_PLAYER_NAME);
		return obj;
	}

}
