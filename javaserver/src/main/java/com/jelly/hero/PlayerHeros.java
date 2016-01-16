package com.jelly.hero;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.Beast;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.Role;
import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.common.config.CharacterManager.RoleGroupWrapper;
import com.dol.cdf.common.config.ItemConstant;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.jelly.activity.ActivityType;
import com.jelly.player.AttackerGroup;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.player.GradeType;
import com.jelly.player.IFighter;
import com.jelly.player.ItemInstance;
import com.jelly.player.PlayerProperty;
import com.jelly.quest.TaskType;
import com.jelly.rank.GameRankMaster;
import com.jelly.team.MyHero;

public class PlayerHeros extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerHeros.class);

	/**
	 * 保存出战的hero的索引
	 */
	@JsonProperty("mr")
	private List<Integer> mars;

	@JsonProperty("he")
	private Map<Integer, Hero> heros;

	@JsonProperty("ai")
	private List<Integer> aidList = Lists.newArrayList();
	@JsonProperty("hi")
	private int tmpStudyHeroId = 0;
	@JsonProperty("st")
	private int tmpSkillType = 0;
	@JsonProperty("si")
	private int tmpSkillId = 0;
	@JsonProperty("sx")
	private int tmpSkillIdx = 0;
	@JsonProperty("max")
	private short maxCount = 110;
	
	@JsonProperty("theros")
	private Map<Integer, Hero> theros = Maps.newHashMap();

	// 招募，因为有cd索引，所以从0开始
	public static final short NORMAL_RECRUIT = 0;
	public static final short MID_RECRUIT = 1;
	public static final short HIGH_RECRUIT = 2;
	// 高级抓取忍者数量
	public static final short HIGH_RECRUIT_NUMBER = 10;

	// 学习技能
	public final static short NORMAL_STUDY_SKILL = 0;
	public final static short SENOR_STUDY_SKILL = 1;
	public final static short ITEM_STUDY_SKILL = 2;

	// 转移经验
	public final static short EXCHANGE_EXP = 0;
	// 转移英雄
	public final static short EXCHANGE_HERO = 1;
	// 转移血继
	public final static short EXCHANGE_BLOOD = 2;

	private Hero currentHero;

	public PlayerHeros() {
	}

	public boolean isMaxHeros(int value) {
		return heros.size() + value > maxCount;
	}
	
	public int changeMaxCount(int sz) {
		return maxCount += sz;
	}
	

	public PlayerHeros(int roleId) {
		heros = Maps.newHashMap();
		theros = Maps.newHashMap();
		int heroId = generateHeroId();
		mars = Lists.newArrayList(heroId, null, null);
		Hero hero = new Hero(roleId);
		heros.put(heroId, hero);
		List<ItemInstance> lstInstances = Lists.newArrayList();
		lstInstances.add(ItemFactory.createItemInstance(3010, 1));
		lstInstances.add(ItemFactory.createItemInstance(3000, 1));
		lstInstances.add(ItemFactory.createItemInstance(3005, 1));
		lstInstances.add(ItemFactory.createItemInstance(3015, 1));
		hero.setEquip(lstInstances);
	}


	public int addHero(int roleId) {
		int heroId = generateHeroId();
		Hero hero = new Hero(roleId);
		heros.put(heroId, hero);
		appendChangeMap("hes", heroId, hero.toJson());
		return heroId;
	}
	
	public List<Integer> addHeros(List<Hero> hes) {
		List<Integer> newHeroIds = Lists.newArrayList();
		for (Hero h : hes) {
			int hesId = generateHeroId();
			heros.put(hesId, h);
			appendChangeMap("hes", hesId, h.toJson());
			newHeroIds.add(hesId);
		}
		return newHeroIds;
	}
	
	public int addOneHero(Hero hero)
	{
		int hesId = generateHeroId();
		heros.put(hesId,hero);
		appendChangeMap("hes", hesId, hero.toJson());
		return hesId;
	}

	private int generateHeroId() {
		Set<Integer> ids = heros.keySet();
		if (ids.isEmpty()) {
			return 1;
		} else {
			return Collections.max(ids) + 1;
		}
	}

	/**
	 * 设置当前玩家
	 * 
	 * @param hid
	 */
	public void setCurrentHero(int hid, Player player) {
		if (player.getInvenotry().getCurrentHeroId() == hid) {
			return;
		}
		Hero hero = heros.get(hid);
		if(hero == null) return;
		currentHero = hero;
		player.getInvenotry().setCharList(hero.getEquip());
		player.getInvenotry().setCurrentHeroId(hid);
	}

	public void openHero(int id) {
		addHero(id);
	}

	public Hero getCurrentHero() {
		return currentHero;
	}

	/**
	 * 设置出战英雄
	 * 
	 * @param id
	 */
	public void setToFighterHero(ArrayNode hids) {
		List<Integer> heroids = Lists.newArrayList();
		for (JsonNode jsonNode : hids) {
			int id = jsonNode.asInt();
			if (id == 0) {
				heroids.add(id);
			} else if (!heroids.contains(id)) {
				heroids.add(id);
			}
		}
		int idx = 0;
		for (Integer hid : heroids) {
			if (heros.containsKey(hid)) {
				mars.set(idx, hid);
				setAidValue(idx, hid);
			} else {
				mars.set(idx, null);
				setAidValue(idx, 0);
			}
			idx++;
			if (idx == 3) {
				break;
			}
		}
		addChange("mars", convertToArrayNode(mars));

	}

	public void replaceFighterHero(Player player, int srcId, int tarId) {
		int srcIdx = mars.indexOf(srcId);
		int tarIdx = mars.indexOf(tarId);
		if (srcIdx > -1) {
			if (tarIdx < 0) {
				mars.set(srcIdx, tarId);
				setAidValue(srcIdx, tarId);
			} else {
				mars.set(srcIdx, null);
				setAidValue(srcIdx, 0);
			}
			addChange("mars", convertToArrayNode(mars));
		}
		player.getWars().replaceFighterHero(player, srcId, tarId);

	}

	public void removeHeroSkill(int hid, int type, int idx) {
		Hero hero = heros.get(hid);
		hero.removeSkill(type, idx);
		appendChangeMap("hes", hid, hero.toJson());
	}
	
	public void upHeroSkill(Player player, int hid,int type, int idx){
		Hero hero = getHero(hid);
		int skillId = -1;
		if(hero != null) {
			skillId = hero.upSkillStar(player,type,idx);
			appendChangeMap("hes", hid, hero.toJson());
		}
		player.sendResult(OperResultType.UPSKILL,MessageCode.OK,skillId);
	}

	public void studyHeroSkill(Player player, int hid, int type, int studyType, int idx, int itemSKillId, int itemSKillGrade) {
		int buildingType = -1;
		int skillId = -1;
		int grade = -1;
		boolean isFirstStudy = false;
		int costMoney = 0;
		TaskType taskType;
		switch (type) {
		case Hero.SKILL_TYPE_NINJITSU:
			buildingType = BuildingType.RSXX.getId();
			taskType = TaskType.PRATICE_NINJITSU;
			if(idx == (Hero.NINJITSU_COUNT - 1) && player.getProperty().isLockLastSkill()){
				logger.error("not vip can not study fith NINJITSU");
				return;
			}
			break;
		case Hero.SKILL_TYPE_PSYCHIC:
			buildingType = BuildingType.TLC.getId();
			taskType = TaskType.PRATICE_PSYCHIC;
			break;
		default:
			logger.error("此类型不能学习type={}", type);
			return;
		}
		Hero hero = heros.get(hid);
		// Integer idx = hero.getSkillPlaceIdx(type);
		// if (idx < 0) {
		// logger.error("技能满了type={}", type);
		// return;
		// }
		
		if(itemSKillId != -1) {
			skillId = itemSKillId;
			grade = itemSKillGrade;
		}else {
			Building building = config.buildings.getBuilding(buildingType);
			int buildLv = player.getBuilding().getBuildLevel(buildingType);
			VariousItemEntry needGold = building.getFuncCost()[studyType];
			if (player.getBuilding().isFunCding(buildingType, studyType)) {
				// 处于CD中
				// 免费的不能花钱抽
				if (needGold.getAmount() <= 0) {
					logger.info("免费的不能花钱抽");
					return;
				} else {
					int code = VariousItemUtil.doBonus(player, needGold, LogConst.STUDY_SKILL, false);
					if (code != MessageCode.OK) {
						player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
						return;
					}
					costMoney = 1;
				}
			} else {
				if(studyType == NORMAL_STUDY_SKILL && !player.getProperty().containStatus(PlayerProperty.FIRST_NORMAL_STUDY_SKILL)) {
					player.getProperty().addStatus(PlayerProperty.FIRST_NORMAL_STUDY_SKILL);
					isFirstStudy = true;
				}
				if(studyType == SENOR_STUDY_SKILL && !player.getProperty().containStatus(PlayerProperty.FIRST_SENOR_STUDY_SKILL)) {
					player.getProperty().addStatus(PlayerProperty.FIRST_SENOR_STUDY_SKILL);
					isFirstStudy = true;
				}
				if (studyType == NORMAL_STUDY_SKILL) {
					//判断今日完成次数 大于最大免费次数
					if (player.getBuilding().getTodayFinish(buildingType)  >= BuildingType.getBuildType(buildingType).getDayFinishCount(player)) {
						costMoney = 1;
					}
					if (!player.getBuilding().checkMaxFinishAndAddFunCd(buildingType, building, player, LogConst.STUDY_SKILL, studyType)) {
						return;
					}
					
				} else {
					int code = VariousItemUtil.doBonus(player, needGold, LogConst.STUDY_SKILL, false);
					if (code != MessageCode.OK) {
						player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
						return;
					}
					costMoney = 1;
				}

			}
			
			if (studyType == NORMAL_STUDY_SKILL) {
				if (type == Hero.SKILL_TYPE_NINJITSU) {
					if (isFirstStudy) {
						skillId = 1152;
					} else {
						grade = config.rate.studyNinjitsuSkillNormalGrade(buildLv, player);
					}
					player.getActivity().dispatchEvent(ActivityType.PRA_0, player);
				} else if (type == Hero.SKILL_TYPE_PSYCHIC) {
					if (isFirstStudy) {
						skillId = 1111;
					} else {
						grade = config.rate.studyPsychicSkillNormalGrade(buildLv, player);
					}

				}
			} else if (studyType == SENOR_STUDY_SKILL) {
				if (type == Hero.SKILL_TYPE_NINJITSU) {
					if (isFirstStudy) {
						skillId = 1125;
					} else {
						grade = config.rate.studyNinjitsuSkillHighGrade(buildLv, player);
					}
					player.getActivity().dispatchEvent(ActivityType.PRA_1, player);
				} else if (type == Hero.SKILL_TYPE_PSYCHIC) {
					if (isFirstStudy) {
						skillId = 1039;
					} else {
						grade = config.rate.studyPsychicSkillHighGrade(buildLv, player);
					}
					player.getActivity().dispatchEvent(ActivityType.BRA_0, player);
				}
			} else{
				logger.error("study type error {}", studyType);
				return;
			}
			if (skillId == -1) {
				// 如果不是VIP玩家不能随机到SS级别忍者
				if (!player.getProperty().isVip() && GradeType.SS.getId() == grade) {
					grade--;
				}
				Skill rndSkill = config.skills.getRndSkill(grade, type, hero.getSkillBytype(type));
				skillId = rndSkill.getId();
			}
		}
		
		player.getTask().dispatchEvent(player, taskType, grade);
		Map<String, Integer> operResultParam = Maps.newHashMap();
		if (hero.getSkillId(type, idx) == null) {
			setHeroSkill(hero, type, idx, skillId, hid);
		} else {
			tmpStudyHeroId = hid;
			tmpSkillIdx = idx;
			tmpSkillId = skillId;
			tmpSkillType = type;
			operResultParam.put("replace",1);
		}
		operResultParam.put("sid", skillId);
		player.sendResult(OperResultType.STUDY_SKILL, MessageCode.OK,convertToObjectNode(operResultParam));
		if(grade == GradeType.S.getId()) {
			Skill skill = config.skills.getSkill(skillId);
			player.broadcastChatMessage(MessageCode.BROADCAST_SKILL_S, player.getName(),skill.getAlt());
		}else if(grade >= GradeType.SS.getId()) {
			Skill skill = config.skills.getSkill(skillId);
			player.broadcastChatMessage(MessageCode.BROADCAST_SKILL_SS, player.getName(),skill.getAlt());
		}
		if (hero != null) {
			if (type == Hero.SKILL_TYPE_NINJITSU) {
				LogUtil.doPractiseLog((DefaultPlayer)player, hero.getRoleId(), studyType+1, costMoney,skillId);
			} else if (type == Hero.SKILL_TYPE_PSYCHIC) {
				LogUtil.doScholopracticeLog((DefaultPlayer)player, hero.getRoleId(), studyType+1, costMoney,skillId);
			}
		}
	}

	public void giveUpSkill(Player player) {
		if (tmpStudyHeroId == 0) {
			player.sendResult(OperResultType.REPLACE_SKILL, MessageCode.FAIL);
			return;
		}
		Hero hero = getHero(tmpStudyHeroId);
		if (hero == null) {
			player.sendResult(OperResultType.REPLACE_SKILL, MessageCode.FAIL);
			return;
		}
		gainSKillPoints(player,tmpSkillId);
		tmpStudyHeroId = 0;
		player.sendResult(OperResultType.REPLACE_SKILL, MessageCode.OK);
	}
	
	/**
	 * 技能放弃并获得专精点
	 * @param player
	 * @param skillId
	 * @return
	 */
	private int  gainSKillPoints(Player player, int skillId) {
		Skill skill = config.skills.getSkill(skillId);
		if(skill != null) {
			int sponit = config.qref.getQualityRef(skill.getQuality()).getSp()[0];
			VariousItemEntry sponitEntry = new VariousItemEntry("spoint", sponit);
			VariousItemUtil.doBonus(player, sponitEntry, LogConst.GIVEUP_SKILL, true);
			player.sendMiddleMessage(MessageCode.SPOINT_GAIN,sponit+"");
			return sponit;
		}
		return 0;
	}
	
	public void replaceSkill(Player player,int idx) {
		if (tmpStudyHeroId == 0) {
			player.sendResult(OperResultType.REPLACE_SKILL, MessageCode.FAIL);
			return;
		}
		Hero hero = getHero(tmpStudyHeroId);
		if (hero == null) {
			player.sendResult(OperResultType.REPLACE_SKILL, MessageCode.FAIL);
			return;
		}
		
		int skillId = hero.getSkillId(tmpSkillType, idx);
		if(tmpSkillType == Hero.SKILL_TYPE_NINJITSU) {
			int wholeSkillPoints = hero.getWholeSkillPoints(skillId, tmpSkillType, idx);
			VariousItemEntry sponitEntry = new VariousItemEntry("spoint", wholeSkillPoints);
			VariousItemUtil.doBonus(player, sponitEntry, LogConst.GIVEUP_SKILL, true);
			player.sendMiddleMessage(MessageCode.SPOINT_GAIN,wholeSkillPoints+"");
			//通过放弃技能获取获得专精点埋点
			LogUtil.doPointLog(player, 1, skillId);
		}
		
		setHeroSkill(hero, tmpSkillType, idx, tmpSkillId, tmpStudyHeroId);
		tmpStudyHeroId = 0;
		player.sendResult(OperResultType.REPLACE_SKILL, MessageCode.OK);
		//技能替换埋点
		LogUtil.doChangeLog(player, hero.getRoleId(), tmpSkillId, skillId);
	}

	private void setHeroSkill(Hero hero, int skillType, int skillIdx, int skillId, int heroId) {
		hero.removeSkillStar(skillType, skillIdx);
		hero.setSkill(skillType, skillIdx, skillId);
		appendChangeMap("hes", heroId, hero.toJson());
	}

	public void exchangeExp(Player player, int type, ArrayNode srcIds, int tarId) {
		Hero tarHero = heros.get(tarId);
		List<Hero> srcHeros = Lists.newArrayList();
		Iterator<JsonNode> srcIdIterator = srcIds.iterator();
		while (srcIdIterator.hasNext()) {
			int srcId = srcIdIterator.next().asInt();
			if (srcId == tarId) {
				logger.error("同一忍者ID");
				return;
			}
			Hero srcHero = heros.get(srcId);
			if (srcHero == null) {
				logger.error("srcHero is null , srcId:{}", srcId);
				return;
			}
			
			if(srcHero.isHeroHaveState(HeroState.TEAM_ARMY_STATE)){
				continue;
			}
			
			if (srcHero.getQuality() > tarHero.getQuality()) {
				logger.error("exhangeExp 品质高的忍者经验不能转移到品质低的,src quality:{},tar quality:{}",srcHero.getQuality(),tarHero.getQuality());
				player.sendResult(OperResultType.SYS, MessageCode.FAIL);
				return;
			}

			if (srcHero.getRoleId() == tarHero.getRoleId()) {
				logger.error("相同忍着不能经验转移");
				return;
			}

			if (isInPractise(player, srcId, srcHero)) {
				return;
			}
			srcHeros.add(srcHero);
		}
		if (srcHeros.isEmpty())
			return;
		// logger.info("源忍者总数为:{}",srcHeros.size());
		if (isNotEnoughEquipContainer(player, srcHeros, tarHero)) {
			return;
		}
		int buildType = BuildingType.SYS.getId();
		Building building = config.buildings.getBuilding(buildType);
		float percent = building.getInitValue()[0] / 100f;  ////// 
		int exp = 0;
		int points = 0;
		for (Hero srcHero : srcHeros) {
//			exp += srcHero.getWholeExp() + 500; /// 吞噬的时候直接是几级的忍者有多少经验，就加多少经验
			exp += srcHero.getWholeExp();
			points += srcHero.getWholeStarPoint();
			LogUtil.doSwallowLog(player, type, tarHero.roleId, srcHero.roleId);	//	赫德日志
		}
//		int exchangeExp = (int) (exp * percent);   ///// 吞噬的时候直接是几级的忍者有多少经验，就加多少经验
		int exchangeExp =  exp;
		Iterator<JsonNode> iterator = srcIds.iterator();
		while (iterator.hasNext()) {
			int srcId = iterator.next().asInt();
			
			// 替换出战英雄
			replaceFighterHero(player, srcId, tarId);
			
			Hero srcHero = heros.get(srcId);
			if(srcHero == null){
				continue;
			}
			if(srcHero.isHeroHaveState(HeroState.TEAM_ARMY_STATE)){
				continue;
			}
			removeHero(srcId, player);
		}
		int preLv = tarHero.getLevel();
		tarHero.addExp(exchangeExp, player);
		tarHero.addStarPoint(points);
		appendChangeMap("hes", tarId, tarHero.toJson());
		if (tarHero.getLevel() > preLv) {
			player.getAllPlayersCache().updatePlayerPower(player);
		}

		player.sendResult(OperResultType.SYS, Lists.newArrayList(exchangeExp,points));
		player.getTask().dispatchEvent(player, TaskType.EXCHANGE_EXP);
	}

	public void exchange(Player player, int type, int srcId, int tarId, int srcIdx, int tarIdx) {
		if (srcId == tarId) {
			return;
		}
		
		logger.info("exchange ====>srcIdx = "+ srcIdx + "=== taridx = " + tarIdx);
		int buildType = BuildingType.SYS.getId();
		Building building = config.buildings.getBuilding(buildType);
		Hero srcHero = heros.get(srcId);
		Hero tarHero = heros.get(tarId);
		if(srcHero == null|| tarHero == null) {
			player.sendResult(OperResultType.SYS, MessageCode.FAIL);
			return;
		}
		if (type == EXCHANGE_BLOOD) {
			if(tarIdx == (Hero.NINJITSU_COUNT - 1) && !player.getProperty().isVip()){
				logger.error("not vip can not study fith NINJITSU");
				return;
			}
			Integer srcBloodSkill = srcHero.ninjitsu.get(srcIdx);
			int quatyt = srcHero.getSkillQualityBySkillid(srcBloodSkill.intValue(),srcHero.SKILL_TYPE_NINJITSU,srcIdx);
			quatyt = quatyt -1;
			if(quatyt < 0){
				quatyt = 0;
			}
			int code = VariousItemUtil.doBonus(player, building.getFuncCost()[quatyt], LogConst.EXCHANGE_BLOOD, false);
			if (code != MessageCode.OK) {
				player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
				player.sendResult(OperResultType.SYS, MessageCode.FAIL);
				return;
			}
//			Integer srcBloodSkill = srcHero.ninjitsu.get(srcIdx);
			for (int i = 0; i < tarHero.ninjitsu.size(); i++) {
				if (srcBloodSkill != null) {
					Integer skillId = tarHero.ninjitsu.get(tarIdx);
					if(skillId != null) {
						//TODO获得专精点并添加
					}
					tarHero.ninjitsu.set(tarIdx, srcBloodSkill);
					srcHero.ninjitsu.set(srcIdx, null);
					int srcSkillStar = srcHero.getSkillStarCount(Hero.SKILL_TYPE_NINJITSU, srcIdx);
					tarHero.setSkillStar(Hero.SKILL_TYPE_NINJITSU, tarIdx, srcSkillStar);
					srcHero.removeSkillStar(Hero.SKILL_TYPE_NINJITSU, srcIdx);
					appendChangeMap("hes", srcId, srcHero.toJson());
					appendChangeMap("hes", tarId, tarHero.toJson());
					
					break;
				}
			}
			
			LogUtil.doSwallowLog(player, type, tarHero.roleId, srcHero.roleId);	//	赫德日志

			player.sendResult(OperResultType.SYS, srcBloodSkill);
			player.getTask().dispatchEvent(player, TaskType.EXCHANGE_BLOOD);

		} else if (type == EXCHANGE_HERO) {
			if (srcHero.getRoleId() != tarHero.getRoleId()) {
				logger.error("不同忍着不能忍者转移");
				return;
			}
			if (isInPractise(player, srcId, srcHero)) {
				return;
			}
			int srcQLv = srcHero.getQlv();
			int tarQlv = srcQLv + 1 + tarHero.getQlv();
			int maxQLv = (Integer) config.gconst.getConstant(GameConstId.MAX_QUALITY_LV);
			if (tarQlv > maxQLv) {
				player.sendMiddleMessage(MessageCode.COMPOSE_HERO_LIMIT, maxQLv + "");
				return;
			}
			if (isNotEnoughEquipContainer(player, srcHero, tarHero)) {
				return;
			}
			int srcStarPoint = srcHero.getWholeStarPoint();
			int tarStarPoint = tarHero.getWholeStarPoint();
			if(tarStarPoint >= srcStarPoint) {
				int srcExp = srcHero.getWholeExp();
				tarHero.setQlv(tarQlv);
				tarHero.addExp(srcExp, player);
				tarHero.addStarPoint(srcStarPoint);
				appendChangeMap("hes", tarId, tarHero.toJson());
			}else {
				int tarExp = tarHero.getWholeExp();
				srcHero.setQlv(tarQlv);
				srcHero.addExp(tarExp, player);
				srcHero.addStarPoint(tarStarPoint);
				heros.put(tarId, srcHero);
				appendChangeMap("hes", tarId, srcHero.toJson());
			}
			removeHero(srcId, player);
			player.sendResult(OperResultType.SYS);
			// 替换出战英雄
			replaceFighterHero(player, srcId, tarId);
			LogUtil.doSwallowLog(player, type, tarHero.roleId, srcHero.roleId);	//	赫德日志
		}

	}
	
	public void trainHero(Player player, int hid, int itemId) {
		Hero hero = getHero(hid);
		if (hero == null) {
			logger.error("找不到给定id的忍者 {}", hid);
			return;
		}
		int sign = hero.train(player, itemId);
		if(sign==1){
			appendChangeMap("hes", hid, hero.toJson());
			player.sendResult(OperResultType.TRAIN);
		}
		
	}
	
	public void commitTrainProp(Player player, int hid, int type) {
		Hero hero = getHero(hid);
		if (hero == null) {
			logger.error("找不到给定id的忍者 {}", hid);
			return;
		}
		hero.commitTrainProp(player, type);
		appendChangeMap("hes", hid, hero.toJson());
		player.sendResult(OperResultType.TRAIN);
	}

	private boolean isNotEnoughEquipContainer(Player player, List<Hero> srcHeros, Hero tarHero) {
		int equipNum = 0;
		int[] array = { 0, 0, 0, 0 };
		int x = 0;
		for (ItemInstance item : tarHero.equip) {
			if (item != null) {
				array[x] = 1;
			}
			x++;
		}

		int[] tmpArray = Arrays.copyOf(array, array.length);
		// 忍者身上的装备放到背包
		for (Hero srcHero : srcHeros) {
			List<ItemInstance> srcEquip = srcHero.equip;
			for (int i = 0; i < srcEquip.size(); i++) {
				ItemInstance srcItem = srcEquip.get(i);
				if (srcItem != null) {
					if (tmpArray[i] == 1) {
						equipNum++;
					} else {
						tmpArray[i] = 1;
					}
				}
			}
		}
		if (equipNum > 0) {
			if (player.getInvenotry().getEmptyIdxCount(ItemConstant.CON_EQUIP) < equipNum) {
				player.sendResult(OperResultType.SYS, MessageCode.EXCHANGE_EQUIP_FULL);
				return true;
			}
		}

		tmpArray = Arrays.copyOf(array, array.length);

		List<ItemInstance> leftEquips = Lists.newArrayList();

		for (Hero srcHero : srcHeros) {
			List<ItemInstance> srcEquip = srcHero.equip;
			for (int i = 0; i < srcEquip.size(); i++) {
				ItemInstance srcItem = srcEquip.get(i);
				if (srcItem != null) {
					if (tmpArray[i] == 1) {
						leftEquips.add(srcItem);
					} else {
						tarHero.equip.set(i, srcItem);
						tmpArray[i] = 1;
					}
				}
			}
		}

		// 忍者身上的剩余装备放到背包
		for (ItemInstance itemInstance : leftEquips) {
			int idx = player.getInvenotry().getEmptyIndex(ItemConstant.CON_EQUIP);
			player.getInvenotry().addItemReally(itemInstance, itemInstance.getStackCount(), ItemConstant.CON_EQUIP, idx, LogConst.EXCHANGE_ITEM, player, false);
		}

		return false;
	}

	private boolean isNotEnoughEquipContainer(Player player, Hero srcHero, Hero tarHero) {
		int equipNum = 0;
		// 忍者身上的装备放到背包
		List<ItemInstance> srcEquip = srcHero.equip;
		for (int i = 0; i < srcEquip.size(); i++) {
			ItemInstance srcItem = srcEquip.get(i);
			if (srcItem == null) {
				continue;
			}
			ItemInstance tarItem = tarHero.equip.get(i);
			if (tarItem != null) {
				equipNum++;
			}
		}
		if (equipNum > 0) {
			if (player.getInvenotry().getEmptyIdxCount(ItemConstant.CON_EQUIP) < equipNum) {
				player.sendResult(OperResultType.SYS, MessageCode.EXCHANGE_EQUIP_FULL);
				return true;
			}
		}

		List<ItemInstance> leftEquips = Lists.newArrayList();
		// 忍者身上的装备替换到目标忍者身上
		for (int i = 0; i < srcEquip.size(); i++) {
			ItemInstance srcItem = srcEquip.get(i);
			if (srcItem == null) {
				continue;
			}
			ItemInstance tarItem = tarHero.equip.get(i);
			if (tarItem != null) {
				leftEquips.add(srcItem);
			} else {
				tarHero.equip.set(i, srcItem);
			}
		}

		// 忍者身上的剩余装备放到背包
		for (ItemInstance itemInstance : leftEquips) {
			int idx = player.getInvenotry().getEmptyIndex(ItemConstant.CON_EQUIP);
			player.getInvenotry().addItemReally(itemInstance, itemInstance.getStackCount(), ItemConstant.CON_EQUIP, idx, LogConst.EXCHANGE_ITEM, player, false);
		}

		return false;
	}

	private boolean isInPractise(Player player, Integer srcId, Hero srcHero) {
		if (player.getPractise().inPractise(srcId)) {
			player.sendResult(OperResultType.SYS, MessageCode.HERO_IN_PRACTISE);
			return true;
		}
		return false;
	}

	/**
	 * 删除英雄和相关数据
	 * 
	 * @param key
	 */
	private void removeHero(int srcId, Player player) {
		Hero removedHero = heros.remove(srcId);
		// 改为提前判断玩家身上是否有装备
		if (removedHero == null) {
			logger.error("removeHero is null , hero key = {},userId ={}", srcId, player.getProperty().getUserId());
		}
		// 为null表示删除英雄
		appendChangeMap("hes", srcId, null);
		// 删除参加忍界大战的忍者
		// // 后援团减少该忍者
		// int idx = aidList.indexOf(key);
		// if (idx > -1) {
		// aidList.set(idx, 0);
		// addChange("aids", convertToArrayNode(aidList));
		// }
	}
	
	public void removeHeros(Player player, List<Integer> srcIds) {
		for (Integer id : srcIds) {
			removeHero(id, player);
		}
	}
	
	public Hero removeHerosByID(Integer srcId){
		Hero removedHero = heros.remove(srcId);
		appendChangeMap("hes", srcId, null);
		theros.put(srcId, removedHero);
		return removedHero;
	}
	
	// 改变hero 状态
	public void addHeroState(Integer srcIds,Integer state)
	{
		Hero hero = getHero(srcIds);
		if(hero != null){
			hero.addHeroStates(state);
			appendChangeMap("hes", srcIds, hero.toJson());
		}
	}
	
	public void clearHerosAllState(Integer state)
	{
		for(Integer srid : this.heros.keySet()){
			removeHeroState(srid,state);
		}
	}
	
	public void removeHerosState(List<Integer> srcIds,Integer state)
	{
		for (Integer id : srcIds) {
			removeHeroState(id,state);
		}
	}
	
	public void removeHeroState(Integer srcIds,Integer state)
	{
		Hero hero = getHero(srcIds);
		if(hero != null){
			hero.removeHeroState(state);
			appendChangeMap("hes", srcIds, hero.toJson());
		}
	}
	
//	public void removeHeros(Player player, int... srcIds) {
//		for (int id : srcIds) {
//			removeHero(id, player);
//		}
//	}

	@Override
	public JsonNode toWholeJson() {
		// Hero hero = new Hero(1001);
		// hero.setLevel(100);
		// heros.put(1001, hero);
		//暂时处理的代码，把派遣的状态去掉，帮会战数据修正好了，需要把这段代码去掉
//		clearHerosAllState(HeroState.TEAM_ARMY_STATE);
		ObjectNode obj = jackson.createObjectNode();
		// ObjectNode herosObj = jackson.createObjectNode();
		// for (Entry<Integer, Hero> entry : heros.entrySet()) {
		// herosObj.put(entry.getKey() + "", entry.getValue().toJson());
		// }
		// obj.put("hes", herosObj);
		obj.put("mars", convertToArrayNode(mars));
		obj.put("aids", convertToArrayNode(aidList));
		obj.put("max", maxCount);
		return obj;
	}

	public JsonNode friendInfo() {
		ObjectNode obj = jackson.createObjectNode();
		obj.put("mars", convertToArrayNode(mars));
		ObjectNode herosObj = jackson.createObjectNode();
		List<Integer> marHeros = getMarIds();
		for (Integer hid : marHeros) {
			Hero hero = heros.get(hid);
			herosObj.put(hid + "", hero.toJson());
		}
		obj.put("hes", herosObj);
		return obj;
	}


	public void sendShardHeros(Player player) {

		String key = "hes";
		ObjectNode herosObj = jackson.createObjectNode();
		int i = 0;
		for (Entry<Integer, Hero> entry : heros.entrySet()) {
			herosObj.put(entry.getKey() + "", entry.getValue().toJson());
			if (i % 10 == 0) {
				player.sendMessage(key, herosObj);
				herosObj.removeAll();
			}
			i++;
		}
		if (herosObj.size() > 0) {
			player.sendMessage(key, herosObj);
		}

	}
	
	public Map<Integer, Hero> getAllHero(){
		return heros;
	}
	/**
	 * 根据role id 判断是否有这种忍者
	 * 
	 * @param roleId
	 * @return
	 */
	public boolean hasThisHero(int roleId) {
		return getHeroByRoleId(roleId) != null;
	}
	
	public Hero getHeroByRoleId(int roleId) {
		Collection<Hero> values = heros.values();
		for (Hero hero : values) {
			if (hero.getRoleId() == roleId) {
				return hero;
			}
		}
		return null;
	}

	/**
	 * 通过ID获得英雄
	 * 
	 * @param idx
	 * @return
	 */
	public Hero getHero(int id) {
		Hero hero = heros.get(id);
		return hero;
	}
	
	public List<MyHero> getHeros(List<Integer> ids) {
		List<MyHero> hes = Lists.newArrayList();
		for (Integer id : ids) {
			Hero h = getHero(id);
			if (h != null && !h.isHeroHaveState(HeroState.TEAM_ARMY_STATE)) {
				MyHero myhero = new MyHero(id.intValue(), h);
				hes.add(myhero);
			}
		}
		return hes;
	}

	@Override
	public String getKey() {
		return "heros";
	}

	public List<Integer> getMarIds() {
		List<Integer> mlis = FluentIterable.from(mars).filter(Predicates.notNull()).toList();
		List<Integer> ml = Lists.newArrayList();
		for(Integer id : mlis)
		{
			Hero hero = getHero(id.intValue());
			if(hero == null){
				continue;
			}
			ml.add(id);
		}
		return ml;
	}
	
	public List<Integer> getMarIdsWithNull(){
		return getMarIds();
//		return mars;
	}

	public int getFirstHeroRoleId() {
		return getFirstHero().getRoleId();
	}

	public Hero getFirstHero() {
		return heros.get(getMarIds().get(0));
	}
	
	

	/**
	 * 获得玩家的战斗力
	 * 
	 * @return
	 */
	public int getPlayerPower() {
		int power = 0;
		for (Hero h : getMarHeros()) {
			power += h.getAllPower();
		}
		return power;
	}

	/**
	 * 第一个忍着的战斗力
	 * 
	 * @return
	 */
	public int getFirstHeroPower() {
		Hero firstHero = heros.get(getMarIds().get(0));
		return firstHero.getAllPower();

	}

	public List<Hero> getMarHeros() {
		List<Hero> hmars = Lists.newArrayList();
		for (Integer id : getMarIds()) {
			if(heros.get(id) ==null){
				continue;
			}
			hmars.add(heros.get(id));
		}
		return hmars;
	}

	public AttackerGroup getAttackerGroup() {
		List<IFighter> fighterAList = getFighters();
		return new AttackerGroup(fighterAList);
	}

	private List<IFighter> getFighters() {
		List<IFighter> fighterAList = Lists.newArrayList();
		List<RoleGroupWrapper> hitGroups = getHitGroups();
		Map<Integer, Integer> caculateAidProps = caculateAidProps();
		List<Integer> mls = Lists.newArrayList();
		for (int id : getMarIds()) {
			Hero hero = heros.get(id);
			if(hero == null){
				mls.add(id);
				logger.info("====null=getFighters=="+id);
				continue;
			}
			hero.setLevelUp(false);
			IFighter fighterA = new BaseFighter(hero, hitGroups, caculateAidProps);
			fighterAList.add(fighterA);
		}
		
		for(Integer kid : mls)
		{
			for(int i = 0;i<mars.size();i++){
				Integer id = mars.get(i);
				if(kid.intValue() == id.intValue()){
					mars.remove(i);
					break;
				}
			}
		}
		
		return fighterAList;
	}

	public DefenderGroup getDefenderGroup() {
		List<IFighter> fighterAList = getFighters();
		return new DefenderGroup(fighterAList);
	}

	public AttackerGroup getFirstAttacker() {
		List<IFighter> fighterAList = Lists.newArrayList();
		Hero hero = getFirstHero();
		hero.setLevelUp(false);
		IFighter fighterA = new BaseFighter(hero);
		fighterAList.add(fighterA);
		return new AttackerGroup(fighterAList);
	}

	public boolean addMarsEquip(Accessory acc) {
		List<Integer> marsId = getMarIds();
		int idx = acc.getCategory();
		for (int id : marsId) {
			Hero hero = heros.get(id);
			ItemInstance equip = hero.getEquip().get(idx);
			if (equip == null) {
				hero.getEquip().set(idx, ItemFactory.createItemInstance(acc.getId(), 1));
				appendChangeMap("hes", id, hero.toJson());
				return true;
			}
		}
		return false;
	}

	// idx 参照 recruit const
	public void raffleHero(int idx, Player player) {
		int build = BuildingType.RZW.getId();

		if (isMaxHeros(idx == HIGH_RECRUIT ? HIGH_RECRUIT_NUMBER : 1)) {
			logger.info("达到最大忍着数量了");
			return;
		}
		int buildingLv = player.getBuilding().getBuildLevel(build);
		
		Building building = config.buildings.getBuilding(build);
		VariousItemEntry needGold = building.getFuncCost()[idx];

		if (idx == HIGH_RECRUIT) {
			if (needGold.getAmount() <= 0) {
				logger.info("免费的不能花钱抽");
				return;
			} else {
				//进行活动判定 是否为半价活动
				float muti = Float.parseFloat(ActivityType.RAFFLE_MUTI.getValue());
				VariousItemEntry tNeedGold = new VariousItemEntry("gold", (int)(needGold.getAmount() * muti));
				
				int code = VariousItemUtil.doBonus(player, tNeedGold, LogConst.RAFFLE_HERO, false);
				if (code != MessageCode.OK) {
					player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
					return;
				}
			}
			openTenHeros(player, buildingLv);
			return;
		}
		

		boolean cding = player.getBuilding().isFunCding(build, idx);
		boolean isFirstRaffle = false;
		int costMoney = 0;
		if (cding) {
			// 处于CD中
			// 免费的不能花钱抽
			if (needGold.getAmount() <= 0) {
				logger.info("免费的不能花钱抽");
				return;
			} else {
				int code = VariousItemUtil.doBonus(player, building.getFuncCost()[idx], LogConst.RAFFLE_HERO, false);
				if (code != MessageCode.OK) {
					player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
					return;
				}
				costMoney = 1;
			}

		} else {
			if (player.getBuilding().getBuilds().get(build).getCdTime(idx + 1) == 0 && idx != HIGH_RECRUIT) {
				isFirstRaffle = true;
			}
			if (idx == NORMAL_RECRUIT) {
				if (player.getBuilding().getTodayFinish(build)  >= BuildingType.getBuildType(build).getDayFinishCount(player)) {
					costMoney = 1;
				}
				if (!player.getBuilding().checkMaxFinishAndAddFunCd(build, building, player, LogConst.RAFFLE_HERO, idx)) {
					return;
				}
				
			} else {
				if(isFirstRaffle) {
					int cdDuration = (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.FIRST_MID_REC_CD);
					player.getBuilding().addFunCdWithDuration(building, idx,cdDuration);
				}else {
					player.getBuilding().addFunCd(building, idx);
				}
			}

		}
		int grade = -1;
		int raffleSr = 0;
		int roleId = -1;
		// 第一次招募
		if (isFirstRaffle) {
			if (idx == NORMAL_RECRUIT) {
				roleId = (Integer)config.gconst.getConstant(GameConstId.FIRST_NORMAL_REC);
			} else if (idx == MID_RECRUIT) {
				roleId = (Integer)config.gconst.getConstant(GameConstId.FIRST_MID_REC);
			}
		} else if((idx ==  MID_RECRUIT) && !player.getProperty().containStatus(PlayerProperty.SECOND_SENOR_RAFFLE)){
			player.getProperty().addStatus(PlayerProperty.SECOND_SENOR_RAFFLE);
			roleId = (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.SECOND_MID_REC);
		}else {
			switch (idx) {
			case NORMAL_RECRUIT:
				grade = config.rate.heroNormalGrade(buildingLv, player,cding);
				break;
			case MID_RECRUIT:
				grade = config.rate.heroMidGrade(buildingLv, player,cding);
				raffleSr = AllGameConfig.getInstance().activitys.getRaffleHeroScore(MID_RECRUIT);
				break;
			case HIGH_RECRUIT:
				grade = config.rate.heroHighGrade(buildingLv, player);
				raffleSr = AllGameConfig.getInstance().activitys.getRaffleHeroScore(HIGH_RECRUIT);
				break;
			default:
				break;
			}

		}
		
		if (raffleSr > 0) {
			GameRankMaster.getInstance().updateScoreRank(player.getId(), raffleSr);
		}
		
		// roleId = 27;
		if (roleId != -1) {
			openHero(roleId);
			Role role = config.characterManager.getRoleById(roleId);
			grade = role.getQuality();
			dispatchRaffleEvent(idx, player, grade);
			player.sendResult(OperResultType.RZCJ, MessageCode.OK, roleId);
			LogUtil.doCatchLog((DefaultPlayer)player, role.getId(), idx+1, costMoney);
			if(grade == GradeType.S.getId()) {
				player.broadcastChatMessage(MessageCode.BROADCAST_HERO_S, player.getName(),role.getAlt());
			}else if(grade >= GradeType.SS.getId()) {
				player.broadcastChatMessage(MessageCode.BROADCAST_HERO_SS, player.getName(),role.getAlt());
			}
		} else if (grade != -1) {

			if (idx == NORMAL_RECRUIT && grade >= GradeType.A.getId()) {
				Role rRole = config.characterManager.getRndRole(grade);
				player.sendResult(OperResultType.RZCJ, MessageCode.FAIL, rRole.getId());
			} else {
				// 如果不是VIP玩家不能随机到SS级别忍者
				if (player.getProperty().getVipScore() < 1000 && GradeType.SS.getId() == grade) {
					grade--;
				}
				Role rRole = config.characterManager.getRndRole(grade);

				openHero(rRole.getId());

				player.sendResult(OperResultType.RZCJ, MessageCode.OK, rRole.getId());

				dispatchRaffleEvent(idx, player, grade);
				LogUtil.doCatchLog((DefaultPlayer)player, rRole.getId(), idx+1, costMoney);
				if(grade == GradeType.S.getId()) {
					player.broadcastChatMessage(MessageCode.BROADCAST_HERO_S, player.getName(),rRole.getAlt());
				}else if(grade >= GradeType.SS.getId()) {
					player.broadcastChatMessage(MessageCode.BROADCAST_HERO_SS, player.getName(),rRole.getAlt());
				}
			}

		} else {
			player.sendResult(OperResultType.RZCJ, MessageCode.FAIL);
		}
	}

	public void dispatchRaffleEvent(int idx, Player player, int grade) {
		player.getTask().dispatchEvent(player, TaskType.RECRUIT_SUCESS, grade);
		if (idx == NORMAL_RECRUIT) {
			player.getActivity().dispatchEvent(ActivityType.RAFF_0, player);
		} else {
			player.getActivity().dispatchEvent(ActivityType.RAFF_1, player);
		}
	}

	private void openTenHeros(Player player, int buildingLv) {
		List<Integer> hidGrades = Lists.newArrayList();
		for (int i = 0; i < HIGH_RECRUIT_NUMBER; i++) {
			int grade = config.rate.heroHighGrade(buildingLv, player);
			hidGrades.add(grade);
			// 如果不是VIP玩家不能随机到SS级别忍者
			if (!player.getProperty().isVip() && GradeType.SS.getId() == grade) {
				grade--;
			}
		}
		Collections.sort(hidGrades, Collections.reverseOrder());
		if (hidGrades.get(0) < GradeType.A.getId()) {
			hidGrades.set(0, GradeType.A.getId());
		}
		for (int i = 1; i < hidGrades.size(); i++) {
			Integer grade = hidGrades.get(i);
			if (grade >= GradeType.A.getId()) {
				hidGrades.set(i, GradeType.B.getId());
			}
		}

		List<Integer> heroids = Lists.newArrayList();
		for (Integer grade : hidGrades) {
			Role rRole = config.characterManager.getRndRole(grade);
			int hid = rRole.getId();
			openHero(hid);
			heroids.add(hid);
			player.getTask().dispatchEvent(player, TaskType.RECRUIT_SUCESS, grade);
			player.getActivity().dispatchEvent(ActivityType.RAFF_1, player);
			if(grade == GradeType.S.getId()) {
				player.broadcastChatMessage(MessageCode.BROADCAST_HERO_S, player.getName(),rRole.getAlt());
			}else if(grade >= GradeType.SS.getId()) {
				player.broadcastChatMessage(MessageCode.BROADCAST_HERO_SS, player.getName(),rRole.getAlt());
			}
		}
		Collections.shuffle(heroids);
		player.sendResult(OperResultType.RZCJ, MessageCode.OK, convertToArrayNode(heroids));

		int sr = config.activitys.getRaffleHeroScore(HIGH_RECRUIT);
		if (sr > 0) {
			GameRankMaster.getInstance().updateScoreRank(player.getId(), sr);
		}
		
//		player.getActivity().dispatchEvent(ActivityType.RAFF_2, player);
		
		player.getActivity().dispatchEventRaffHeros(ActivityType.RAFF_2, player);
		
		for (Integer ninjaId: heroids) {
			LogUtil.doCatchLog((DefaultPlayer)player, ninjaId, HIGH_RECRUIT+1, 1);
		}
	}

	public static final int[] fit_pos = { 6, 7, 8 };

	public static final int FIT_ROW = 3;
	public static final int FIT_COLUMN = 5;
	public static final int MAX_FIT_TEAM = FIT_ROW * FIT_COLUMN;

	/**
	 * 计算后援团的匹配属性加成
	 * 
	 * @return
	 */
	public Map<Integer, Integer> caculateAidProps() {
		AtomicLongMap<Integer> map = AtomicLongMap.create();
		if (aidList.isEmpty())
			return Collections.EMPTY_MAP;
		for (int i = 0; i < aidList.size(); i++) {
			int id = aidList.get(i);
			if (id == 0)
				continue;
			Hero hero = getHero(id);
			if (hero == null) {
				logger.debug("hero is null. id:{}", id);
				continue;
			}
			int rdx = i + 1;
			if (rdx % FIT_COLUMN != 0) {
				// if (logger.isDebugEnabled()) {
				// logger.debug("have right index = {}", i);
				// }

				int rid = aidList.get(rdx);
				if (rid != 0) {
					Hero rhero = getHero(rid);
					if (rhero == null) {
						logger.debug("rhero is null. id:{}", id);
						continue;
					}
					if (hero.getRight() == rhero.getLeft()) {
						map.getAndIncrement(hero.getRight());
					}
				}
			}
			int bdx = i + FIT_COLUMN;
			if (bdx < MAX_FIT_TEAM) {
				// logger.debug("have bottom index = {}", i);
				int bid = aidList.get(bdx);
				if (bid != 0) {
					Hero bhero = getHero(bid);
					if (bhero == null) {
						logger.debug("bhero is null. id:{}", id);
						continue;
					}
					if (hero.getBottom() == bhero.getTop()) {
						map.getAndIncrement(hero.getBottom());
					}
				}
			}
		}

		int[] aidInc = (int[]) config.gconst.getConstant(GameConstId.AID_INC);
		Map<Integer, Long> propNumbers = map.asMap();
		Map<Integer, Integer> propIncs = Maps.newHashMap();
		for (Entry<Integer, Long> propNumber : propNumbers.entrySet()) {
			Integer key = propNumber.getKey();
			Integer value = (int) (aidInc[key - 1] * propNumber.getValue());
			propIncs.put(key, value);
		}

		return propIncs;
	}
	
	public void removeHeroByRid(Player player,int rid) {
		List<Integer> rids = Lists.newArrayList();
		for (Entry<Integer, Hero> e : heros.entrySet()) {
			Integer key = e.getKey();
			Hero value = e.getValue();
			if(value.getRoleId() == rid) {
				rids.add(key);
			}
		}
		for (Integer id : rids) {
			heros.remove(id);
			removeHero(id, player);
		}
	}

	/**
	 * 是否具有组合技
	 * 
	 * @return
	 */
	public List<RoleGroupWrapper> getHitGroups() {
		List<RoleGroupWrapper> hitGroups = Lists.newArrayList();
		List<Integer> rids = Lists.newArrayList();
		for (Integer id : aidList) {
			if (id != null && id != 0) {
				Hero hero = getHero(id);
				if (hero != null) {
					int rid = hero.getRoleId();
					rids.add(rid);
				}

			}
		}
		// if (logger.isDebugEnabled()) {
		// logger.debug("role ids {}", convertToArrayNode(rids));
		// }

		List<RoleGroupWrapper> roleGroups = AllGameConfig.getInstance().characterManager.getRoleGroups();
		for (RoleGroupWrapper roleGroup : roleGroups) {
			int[] roles = roleGroup.getRoles();
			boolean haveAll = true;
			for (int i : roles) {
				if (!rids.contains(i)) {
					haveAll = false;
					break;
				}
			}
			if (haveAll) {
				hitGroups.add(roleGroup);
			}
		}
		return hitGroups;

	}

	public List<Integer> getAidList() {
		return aidList;
	}

	public void setAidList(List<Integer> aidList) {
		this.aidList = aidList;
		addChange("aids", convertToArrayNode(aidList));
		if (logger.isDebugEnabled()) {
			logger.debug("roleGrop size = {}", getHitGroups().size());
		}
		if (logger.isDebugEnabled()) {
			Map<Integer, Integer> calc = caculateAidProps();
			logger.debug("cacl add power = {}", calc);
		}
	}

	private void setAidValue(int idx, int value) {
		if (aidList.isEmpty()) {
			return;
		}

		List<Integer> tmpAids = Lists.newArrayList(aidList);
		for (Integer p : fit_pos) {
			tmpAids.set(p, 0);
		}
		// 删除已经有的
		int indexof = tmpAids.indexOf(value);
		if (indexof > -1) {
			aidList.set(indexof, 0);
		}
		aidList.set(fit_pos[idx], value);
		addChange("aids", convertToArrayNode(aidList));
	}

	/**
	 * 技能注入
	 * 
	 * @param player
	 * @param hid
	 * @param bid
	 */
	public void skillInject(Player player, int hid, int bid) {
		Beast beast = config.beast.getBeast(bid);
		VariousItemEntry needCarKla = new VariousItemEntry(beast.getItemId(), beast.getCost());
		int code = VariousItemUtil.checkBonus(player, needCarKla, false);
		if (code == MessageCode.OK) {
			int buildLevel = player.getBuilding().getBuildLevel(BuildingType.WST.getId());
			List<Integer> allSkill = Lists.newArrayList();
			for (int i = 0; i < beast.getChapter().length; i++) {
				int cid = beast.getChapter()[i];
				int sid = beast.getStage()[i];
				if (player.getAdventure().isPass(cid, sid)) {
					allSkill.add(beast.getSkills()[i]);
				}
			}
			if (allSkill.size() == 0) {
				logger.error("没有可随机的技能");
				return;
			}
			VariousItemUtil.doBonus(player, needCarKla, LogConst.BEAST_INJECT, false);
			List<Integer> qualityRates = Lists.newArrayList();
			for (Integer sid : allSkill) {
				Integer quality = config.skills.getSkill(sid).getQuality();
				List<Integer> beastRates = config.rate.getBeastRates(buildLevel);
				qualityRates.add(beastRates.get(quality - 1));

			}
			int index = Rnd.getIndex(qualityRates);
			int skillId = beast.getSkills()[index];
			int type = Hero.SKILL_TYPE_BEAST;
			Hero hero = getHero(hid);
			if (hero.getSkillId(type, 0) == null) {
				setHeroSkill(hero, type, 0, skillId, hid);
			} else {
				tmpStudyHeroId = hid;
				tmpSkillIdx = 0;
				tmpSkillId = skillId;
				tmpSkillType = Hero.SKILL_TYPE_BEAST;
			}

			player.sendResult(OperResultType.STUDY_SKILL, MessageCode.OK, skillId);
			player.getTask().dispatchEvent(player, TaskType.FILL_BEAST, beast.getNum());
			player.getActivity().dispatchEvent(ActivityType.BEAST_INJECT, player);
			Skill skill = config.skills.getSkill(skillId);
			if(skill.getQuality() == GradeType.S.getId()) {
				player.broadcastChatMessage(MessageCode.BROADCAST_SKILL_S, player.getName(),skill.getAlt());
			}else if(skill.getQuality() >= GradeType.SS.getId()) {
				player.broadcastChatMessage(MessageCode.BROADCAST_SKILL_SS, player.getName(),skill.getAlt());
			}
			//尾兽埋点
			LogUtil.doBeastLog((DefaultPlayer)player, hero.getRoleId(), beast.getNum(),skillId);
		} else {
			player.sendMiddleMessage(code);
		}
	}
	
	/**
	 * 封账号
	 */
	public void banHero(){
		int[] giveHeroIds = {15,16,2};
		List<Integer> heroIds = Lists.newArrayList();
		for (int i = 0; i < giveHeroIds.length; i++) {
			int addHero = addHero(giveHeroIds[i]);
			heroIds.add(addHero);
		}
		this.mars = heroIds;
	}
}
