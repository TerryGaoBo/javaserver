package com.dol.cdf.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Adventure;
import com.dol.cdf.common.bean.DropGroup;
import com.dol.cdf.common.bean.Role;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.Vip;
import com.dol.cdf.common.collect.TwoKeyHashMap;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.common.constant.GameConstManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.jelly.hero.IHero;
import com.jelly.hero.Monster;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.player.IFighter;
import com.jelly.player.VipConstant;

public class AdventureConfigManager extends BaseConfigLoadManager {

	private final static String CONFIG_FILE_NAME_ADVENTURE = "Adventure.json";

	/**
	 * 远征冒险奖励 key1 chapter; key2 stage; value Adventure
	 * 
	 */
	TwoKeyHashMap<Integer, Integer, Adventure> commonAdventures = new TwoKeyHashMap<Integer, Integer, Adventure>();

	Map<Integer, Integer> commonChapterStageCount = new HashMap<Integer, Integer>();

	Map<Integer, Long> commonChapterStageMax;

	/**
	 * 远征精英冒险
	 **/
	TwoKeyHashMap<Integer, Integer, Adventure> eliteAdventures = new TwoKeyHashMap<Integer, Integer, Adventure>();

	Map<Integer, Integer> eliteChapterStageCount = Maps.newHashMap();

	Map<Integer, Long> eliteChapterStageMax;

	@Override
	public void loadConfig() {
		List<Adventure> adventureConfig = readConfigFile(
				CONFIG_FILE_NAME_ADVENTURE,
				new TypeReference<List<Adventure>>() {
				});

		// 普通副本每章关卡数量统计<章节ID,关卡数量>
		AtomicLongMap<Integer> commonMap = AtomicLongMap.create();
		// 精英副本每章关卡数量统计<章节ID,关卡数量>
		AtomicLongMap<Integer> eliteMap = AtomicLongMap.create();

		for (Adventure adv : adventureConfig) {
			int advType = adv.getType();
			if (advType == 1) { // 普通副本
				commonMap.getAndIncrement(adv.getChapter());
				commonAdventures.put(adv.getChapter(), adv.getStage(), adv);
				Integer sid = commonChapterStageCount.get(adv.getChapter());
				if (sid == null) {
					commonChapterStageCount.put(adv.getChapter(),
							adv.getStage());
				} else {
					Integer preId = commonAdventures.get(adv.getChapter(), sid)
							.getPreStage();
					if (preId == null) {
						preId = 0;
					}
					if (adv.getPreStage() != null && adv.getPreStage() > preId) {
						commonChapterStageCount.put(adv.getChapter(),
								adv.getStage());
					}
				}
			} else if (advType == 2) { // 精英副本
				eliteMap.getAndIncrement(adv.getChapter());
				eliteAdventures.put(adv.getChapter(), adv.getStage(), adv);
				if (eliteChapterStageCount.containsKey(adv.getChapter())) {
					Integer sid = eliteChapterStageCount.get(adv.getChapter());
					Integer preEliteSid = eliteAdventures.get(adv.getChapter(),
							sid).getPreStage();
					if (preEliteSid == null) {
						preEliteSid = 0;
					}
					if (adv.getPreStage() != null
							&& adv.getPreStage() > preEliteSid) {
						eliteChapterStageCount.put(adv.getChapter(),
								adv.getStage());
					}
				} else {
					eliteChapterStageCount
							.put(adv.getChapter(), adv.getStage());
				}
			}
		}
		commonChapterStageMax = commonMap.asMap();
		eliteChapterStageMax = eliteMap.asMap();
		checkRoleIsExsit(adventureConfig);
		checkRewardDataEqual(adventureConfig);
		checkRewardDataEqual1(adventureConfig);
	}

	public int getStageMax(int chapterId) {
		Long max = commonChapterStageMax.get(chapterId);
		if (max == null) {
			return -1;
		} else {
			return (int) ((long) max);
		}
	}

	private void checkRoleIsExsit(List<Adventure> adventureConfig) {
		for (Adventure adventure : adventureConfig) {
			CharacterManager roles = AllGameConfig.getInstance().characterManager;
			int[] enemy = adventure.getEnemy();
			for (int i : enemy) {
				Role role = roles.getRoleById(i);
				if (role == null) {
					logger.error("Role is not exsit. id = {}", i);
				}
			}

		}
	}

	private void checkRewardDataEqual(List<Adventure> adventureConfig) {
		for (Adventure adventure : adventureConfig) {
			VariousItemEntry[] firstItem = adventure.getFirstItem();
			DropGroupConfigManager drops = AllGameConfig.getInstance().drops;
			List<DropGroup> dropGroups = drops.getDropGroups(adventure
					.getItemGroup());
			for (DropGroup drop : dropGroups) {
				for (VariousItemEntry item : drop.getItem()) {
					if (item.getType().equals("")) {
						continue;
					}
					boolean contain = false;
					for (VariousItemEntry fi : firstItem) {
						if (fi.getType().equals(item.getType())) {
							contain = true;
						}
					}
					if (!contain) {
						// logger.error("ItemGroup have But FirstItem do not. item diff id:{},itemId:{},dropId:{}",adventure.getId(),item.getType(),adventure.getItemGroup());
					}
				}

			}
		}
	}

	private void checkRewardDataEqual1(List<Adventure> adventureConfig) {
		for (Adventure adventure : adventureConfig) {
			VariousItemEntry[] firstItem = adventure.getFirstItem();
			DropGroupConfigManager drops = AllGameConfig.getInstance().drops;
			List<DropGroup> dropGroups = drops.getDropGroups(adventure
					.getItemGroup());
			for (VariousItemEntry fi : firstItem) {
				boolean contain = false;
				for (DropGroup drop : dropGroups) {
					for (VariousItemEntry item : drop.getItem()) {
						if (item.getType().equals("")) {
							continue;
						}
						if (fi.getType().equals(item.getType())) {
							contain = true;
						}
					}
				}
				if (!contain) {
					logger.error(
							"FirstItem have But ItemGroup do not. diff id:{},itemId:{},dropId:{}",
							adventure.getId(), fi.getType(),
							adventure.getItemGroup());
				}
			}

		}
	}

	public DefenderGroup getDefenderGroup(int type, int cid, int sid) {
		Adventure adventure = getAdventure(type, cid, sid);
		List<IFighter> fighters = Lists.newArrayList();
		int i = 0;
		for (int rid : adventure.getEnemy()) {
			IHero enemy = new Monster(rid);
			enemy.setLevel(adventure.getLevel()[i]);
			fighters.add(new BaseFighter(enemy));
			i++;
		}
		return new DefenderGroup(fighters);
	}

	public int[] getEnemy(int chapterId, int stageId) {
		Adventure adventure = commonAdventures.get(chapterId, stageId);
		return adventure == null ? new int[0] : adventure.getEnemy();
	}

	public Adventure getAdventure(int advType, int cid, int sid) {
		if (advType == 1)
			return commonAdventures.get(cid, sid);
		else if (advType == 2)
			return eliteAdventures.get(cid, sid);
		return null;
	}

	/**
	 * 获取重置副本价格
	 * 
	 * @param <br>
	 *        avdentType - 副本类型</br>
	 * @param <br>
	 *        idx - none </br>
	 **/
	public int getResetFubenGold(int avdentType, int idx) {
		int[] costs = null;
		GameConstManager constMgr = AllGameConfig.getInstance().gconst;
		if (avdentType == 1) { // 普通副本
			costs = (int[]) constMgr.getConstant(GameConstId.FB_RESET_COST);
		} else if (avdentType == 2) { // 精英副本
			costs = (int[]) constMgr.getConstant(GameConstId.ELITE_RESET_COST);
		} else {
			return 0;
		}
		return idx >= costs.length ? costs[costs.length - 1] : costs[idx];
	}

	/**
	 * 获取每日可重置副本次数
	 * 
	 * @param <br>
	 *        avdentType - 副本类型</br>
	 * @param <br>
	 *        vipLv - VIP等级</br>
	 **/
	public int getResetFubenTimes(int avdentType, int vipLv) {
		Vip vip = AllGameConfig.getInstance().activitys.getVip(vipLv);
		if (avdentType == 1) {
			return vip.getTimes()[VipConstant.FUBEN_TIMES];
		} else if (avdentType == 2) {
			return vip.getTimes()[VipConstant.ELITE_TIMES];
		}
		return 0; // 无效的副本类型
	}

	/**
	 * 获得章的最大关卡数
	 * 
	 * @param chapterId
	 * @return
	 */
	public int getChapterStageCount(int chapterId) {
		Integer stageId = commonChapterStageCount.get(chapterId);
		return stageId == null ? 0 : stageId;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; ++i) {
			System.out.println("elite reset fuben cost - "
					+ AllGameConfig.getInstance().adventures.getResetFubenGold(
							2, i));
		}

		for (int i = 0; i < 10; ++i) {
			System.out.println("common reset fuben cost - "
					+ AllGameConfig.getInstance().adventures.getResetFubenGold(
							1, i));
		}

		for (int i = 1; i <= 12; ++i) {
			System.out.println("vip "
					+ i
					+ " reset count - "
					+ AllGameConfig.getInstance().adventures
							.getResetFubenTimes(1, i));
		}
	}
}
