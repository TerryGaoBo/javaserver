package com.jelly.hero;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.Recruit;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.activity.ActivityType;
import com.jelly.combat.RecruitCombatManager;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.player.AttackerGroup;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.quest.TaskType;

public class PlayerRecruit extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerRecruit.class);

	/**
	 * 忍者的索引
	 */
	@JsonProperty("i")
	int idx;
	/**
	 * 进度
	 */
	@JsonProperty("p")
	int progress;

	@JsonProperty("t")
	List<String> teamMember;

	// 任务的建筑ID
	private static final int BUILDING_TYPE = BuildingType.YYT.getId();

	public int getIdx() {
		return idx;
	}

	public void incIdx() {
		Recruit recruit = config.recruits.getRecruit(idx);
		addChange("rid", recruit.getId());
		this.idx++;
		addChange("idx", idx);
	}

	public int getProgress() {
		return progress;
	}

	public void addProgress(int count) {
		this.progress += count;
		addChange("progress", progress);
	}

	public void setProgress(int progress) {
		this.progress = progress;
		addChange("progress", progress);
	}

	public boolean recruit() {
		if (progress >= 100) {
			incIdx();
			setProgress(0);
			return true;
		}
		return false;
	}

	/**
	 * 忍影堂招募
	 * 
	 * @param player
	 */
	public void recruit(Player player) {
		Recruit recruit = config.recruits.getRecruit(getIdx());
		if (player.getHeros().isMaxHeros(1)) {
			logger.info("达到最大忍着数量了");
			return;
		}

		if (recruit()) {
			player.getHeros().addHero(recruit.getId());
		}

	}

	public List<VariousItemEntry> addRecruitProgress(Player player,int count) {
		Recruit recruit = config.recruits.getRecruit(getIdx());
		List<VariousItemEntry> rewards = Lists.newArrayList();
		if (recruit.getSilver() != null) {
			rewards.add(new VariousItemEntry("silver", recruit.getSilver()));
		}
		if (recruit.getExp() != null) {
			rewards.add(new VariousItemEntry("exp", recruit.getExp()));
		}
		VariousItemUtil.doBonus(player, rewards, LogConst.RECRUIT_FIGHT, true);
		if (recruit.getRxp() != null) {
			rewards.add(new VariousItemEntry(player.getHeros().getFirstHeroRoleId(), recruit.getRxp()));
			player.getHeros().getFirstHero().addExp(recruit.getRxp(), player);
			player.getHeros().appendChangeMap("hes", player.getHeros().getMarIds().get(0), player.getHeros().getFirstHero().toJson());
		}
		addProgress(count);
		//扣除体力
		if(recruit.getEnergy() != null) {
			VariousItemUtil.doBonus(player, new VariousItemEntry("energy",needRealEnergy(recruit.getEnergy())), LogConst.RECRUIT_FIGHT, false);
		}
		return rewards;
	}

	private int needRealEnergy(int energy) {
		float muti = Float.parseFloat(ActivityType.REC_MUTI.getValue());
		int real = (int) (energy * muti);
		return real;
	}
	
	public void recFight(Player player,ObjectCacheService objectCache) {
		Recruit recruit = config.recruits.getRecruit(getIdx());
		if (player.getProperty().getLevel() < recruit.getNeedLv()) {
			logger.error("level not enough");
			return;
		}
		if (!player.getProperty().hasEnoughEnergy(needRealEnergy(recruit.getEnergy()))) {
			logger.error("energy not enough");
			return;
		}
		AttackerGroup attackerGroup = player.getHeros().getFirstAttacker();
		
		if(teamMember != null) {
			for (String guid : teamMember) {
				DefaultPlayer p = objectCache.getCache(guid,DefaultPlayer.class);
				if(p != null) {
					attackerGroup.getFighters().add(new BaseFighter(p.getHeros().getFirstHero()));
				}
			}
		}
//For test
//		int i = 1;
//		for (String guid : Lists.newArrayList("1","1")) {
//			i++;
//			Hero hero = new Hero(i);
//			attackerGroup.getFighters().add(new BaseFighter(hero));
//		}
		DefenderGroup defenderGroup = AllGameConfig.getInstance().recruits.getDefenderGroup(getIdx());
		RecruitCombatManager pveCombatManager = new RecruitCombatManager(attackerGroup, defenderGroup, player);
		pveCombatManager.start();
		
		player.getTask().dispatchEvent(player, TaskType.YYT_FIGHT);
	}

	@Override
	public JsonNode toOpenJson(Player player) {
		checkTeamMember(player);
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("idx", idx);
		obj.put("progress", progress);
		ArrayNode teamArray = teamMemberToArray(player);
		obj.put("team", teamArray);
		Recruit recruit = config.recruits.getRecruit(getIdx());
		obj.put("energy", needRealEnergy(recruit.getEnergy()));
		return obj;
	}

	private ArrayNode teamMemberToArray(Player player) {
		ArrayNode teamArray = jackson.createArrayNode();
		if (teamMember != null) {
			for (String guid : teamMember) {
				ArrayNode memberArray = jackson.createArrayNode();
				RoleEntity p = player.getAllPlayersCache().getPlayerInfo(guid);
				if(p != null) {
					memberArray.add(p.getCharId()).add(p.getName()).add(p.getLevel()).add(p.getHeroPow());
					teamArray.add(memberArray);
				}
			}

		}
		return teamArray;
	}

	private void checkTeamMember(Player player) {
		if (teamMember == null) {
			refreshTeamMember(player);
		}
	}

	public void refreshTeam(Player player) {
		Building building = config.buildings.getBuilding(BUILDING_TYPE);
		if (player.getBuilding().isMaxFinish(BUILDING_TYPE, player)) {
			int code = VariousItemUtil.doBonus(player, building.getFuncCost()[0], LogConst.REFRESH_TEAM, false);
			if (code != MessageCode.OK) {
				player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
				return;
			}
		}
		refreshTeamMember(player);
		addChange("team", teamMemberToArray(player));
	}

	public boolean refreshTeamMember(Player player) {
		List<RoleEntity> exsitRoles = Lists.newArrayList(player.getRole());
		RoleEntity rp = player.getAllPlayersCache().getRndRoundPlayer(exsitRoles, player.getProperty().getLevel(), 0);
		exsitRoles.add(rp);
		RoleEntity rp1 = player.getAllPlayersCache().getRndRoundPlayer(exsitRoles, player.getProperty().getLevel(), 0);
		teamMember = Lists.newArrayList();
		if (rp != null) {
			teamMember.add(rp.getGuid());
		}
		if(rp1 != null){
			teamMember.add(rp1.getGuid());
		}
		return true;
	}

	@Override
	public String getKey() {
		return "recInfo";
	}

	@Override
	public JsonNode toWholeJson() {
		return null;
	}

}
