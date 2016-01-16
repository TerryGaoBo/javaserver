package com.jelly.player;

import io.nadron.app.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.Adventure;
import com.dol.cdf.common.bean.ItemGroupEnum;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.activity.ActivityType;
import com.jelly.hero.Hero;
import com.jelly.hero.PlayerHeros;

public class PlayerAdventure extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerHeros.class);

	@JsonProperty("ad")
	List<List<Integer>> adventrues = Lists.newArrayList();
	
	@JsonProperty("ead")
	List<List<Integer>> eliteAdventures = Lists.newArrayList();

	/**
	 * 通过关卡 TODO 如何发奖励给客户端
	 * 
	 * @param player
	 * @param cid
	 * @param sid
	 * @return
	 */
	public List<VariousItemEntry> passStage(Player player, int advenType, int cid, int sid) {
		Adventure adv = config.adventures.getAdventure(advenType, cid, sid);
		int chapterStageCount = config.adventures.getChapterStageCount(cid);
		if (adv == null)
			return null;
		if (adv.getPreStage() != null && !containStage(advenType, cid, adv.getPreStage())) {
			logger.error("前置关卡未开启");
			return null;
		}
		VariousItemEntry needEnergyEntry = new VariousItemEntry("energy", adv.getEnergy());
		if(VariousItemUtil.checkBonus(player, needEnergyEntry, false)!=MessageCode.OK){
			logger.error("体力不足");
			return null;
		}
		List<VariousItemEntry> rewards = getNormalRewards(player, adv);
		if (containStage(advenType, cid, sid)) {
			VariousItemEntry itemEntry = config.drops.getVariousItemByGroupId(adv.getItemGroup());
			rewards.add(itemEntry);
			VariousItemUtil.doBonus(player, rewards, LogConst.ADVENURE_FIGHT, true);
		} else {
			VariousItemUtil.doBonus(player, rewards, LogConst.ADVENURE_FIGHT, true);
			List<Integer> list = getAdvByCid(advenType, cid);
			list.add(sid);
			if (chapterStageCount == sid) {
				incChapter(advenType);
			}
			
			if (adv.getFirstItem() != null) {
				for (VariousItemEntry item : adv.getFirstItem()) {
					//如果是装备判断出战身上的装备，如果没有则装上
					if (item.getGroup() == ItemGroupEnum.bag) {
						Accessory acc = config.items.getAccessoryById(Integer.parseInt(item.getType()));
						if (acc != null) {
							boolean isAdd = player.getHeros().addMarsEquip(acc);
							if (!isAdd) {
								VariousItemUtil.doBonus(player, item, LogConst.ADVENURE_FIGHT, true);
							}
						}else {
							VariousItemUtil.doBonus(player, item, LogConst.ADVENURE_FIGHT, true);
						}
					}
					rewards.add(item);
				}
			}
		}
		//扣除体力
		
		VariousItemUtil.doBonus(player, needEnergyEntry, LogConst.ADVENURE_FIGHT, false);
		addAdvRxp(player, adv, rewards,1);
		addChange("adv", convertToArrayNode(adventrues));
		addChange("eadv", convertToArrayNode(eliteAdventures));
		return rewards;
	}

	private void addAdvRxp(Player player, Adventure adv, List<VariousItemEntry> rewards,int count) {
		if (adv.getRxp() != null) {
			float muti = Float.parseFloat( ActivityType.ADV_MUTI.getValue());
			int real = (int)(adv.getRxp()*count * muti);
			for (Integer hid : player.getHeros().getMarIds()) {
				Hero hero = player.getHeros().getHero(hid);
				hero.addExp(real, player);
				player.getHeros().appendChangeMap("hes", hid, hero.toJson());
				rewards.add(new VariousItemEntry(player.getHeros().getFirstHeroRoleId(), real));
			}
		}
	}

	private List<Integer> getAdvByCid(int advenType, int cid) {
		if (advenType == 1) {
			if (adventrues.size() < cid) {
				incChapter(advenType);
			}
			return adventrues.get(cid - 1);
		} else {
			if (eliteAdventures.size() < cid) {
				incChapter(advenType);
			}
			return eliteAdventures.get(cid - 1);
		}
	}

	private List<VariousItemEntry> getNormalRewards(Player player, Adventure adv, int count) {
		List<VariousItemEntry> rewards = Lists.newArrayList();
		float muti = Float.parseFloat(ActivityType.ADV_MUTI.getValue());
		if (adv.getSilver() != null) {
			int real =(int) (adv.getSilver() * count * muti);
			rewards.add(new VariousItemEntry("silver", real));
		}
		if (adv.getExp() != null && adv.getExp() > 0) {
			//
			if(player.getProperty().getLevel() < 10) {
				muti = 1;
			}
			int real =(int) (adv.getExp() * count * muti);
			rewards.add(new VariousItemEntry("exp", real));
		}
		return rewards;
	}

	private List<VariousItemEntry> getNormalRewards(Player player, Adventure adv) {
		return getNormalRewards(player, adv, 1);
	}

	private static final int SWEEP_TYPE_1 = 1;
	private static final int SWEEP_TYPE_10 = 10;

	public List<VariousItemEntry> sweepAdv(Player player, int advenType, int cid, int sid, int count) {

//		if (count == SWEEP_TYPE_1 || count == SWEEP_TYPE_10) {
			if (containStage(advenType, cid, sid)) {
				Adventure adv = config.adventures.getAdventure(advenType, cid, sid);
				VariousItemEntry needEnergyEntry = new VariousItemEntry("energy", adv.getEnergy() * count);
				int code = VariousItemUtil.doBonus(player, needEnergyEntry, LogConst.ADVENURE_SWEEP, false);
				if (code != MessageCode.OK) {
					player.sendMiddleMessage(code);
					return Collections.EMPTY_LIST;
				}
				List<VariousItemEntry> rewards = getNormalRewards(player, adv, count);
				VariousItemUtil.doBonus(player, rewards, LogConst.ADVENURE_SWEEP, true);
				addAdvRxp(player, adv, rewards,count);
				Map<String, Integer> itemMap = Maps.newHashMap();
				for (int i = 0; i < count; i++) {
					VariousItemEntry itemEntry = config.drops.getVariousItemByGroupId(adv.getItemGroup());
					
					VariousItemUtil.doBonus(player, itemEntry, LogConst.ADVENURE_SWEEP, true);

					if (itemMap.containsKey(itemEntry.getType())) {
						int amount = itemMap.get(itemEntry.getType());
						itemMap.put(itemEntry.getType(), amount + itemEntry.getAmount());
					} else {
						itemMap.put(itemEntry.getType(), itemEntry.getAmount());
					}

				}
				if (!itemMap.isEmpty()) {
					List<VariousItemEntry> items = VariousItemUtil.mapToVariousItem(itemMap); 
					rewards.addAll(items);
				}
				// 不最终使用VariousItemUtil.doBonus（rewards），为了放置道具给我超出上限
				return rewards;
			}
//		}
		return Collections.EMPTY_LIST;

	}

	public int getChapter() {
		return adventrues.size();
	}
	
	public boolean isPass(int cid, int sid) {
		if(getChapter() < cid) {
			return false;
		}
		List<Integer> chapters =  adventrues.get(cid - 1);
		if(chapters.size() == 0) {
			return false;
		}
		Integer firstStage = chapters.get(0);
		if(firstStage != null && firstStage == -1) {
			return true;
		}
		if (!chapters.contains(sid)) {
			return false;
		}else {
			return true;
		}
	}

	public boolean containStage(int advenType, int cid, int sid) {
		List<Integer> list = getAdvByCid(advenType, cid);
		if (list == null) {
			return false;
		} else {
			if(list.size() == 1 && list.get(0) == -1) {
				return true;
			}
			return list.contains(sid);
		}

	}

	/**
	 * 获得主线最大的章
	 * @return
	 */
	public int getMaxMainChapter() {
		if(getChapter() == 0) {
			return 0;
		}
		List<Integer> list = adventrues.get(getChapter() - 1);
		if(list.isEmpty()) {
			return getChapter() -1;
		}
		return getChapter();
	}
	
	/**
	 * 获得主线最大节
	 * @return
	 */
	public int getMaxMainStage() {
		if(getChapter() == 0) {
			return 0;
		}
		List<Integer> list = adventrues.get(getChapter() - 1);
		if(list.isEmpty()) {
			int preChapter = getChapter() - 1;
			return config.adventures.getChapterStageCount(preChapter);
		}
		int currStageCount = config.adventures.getChapterStageCount(getChapter());
		if(list.get(0) == -1) {
			return currStageCount;
		}
		for (int i = currStageCount; i >= 1; i--) {
			if(list.contains(i)) {
				return i;
			}
		}
		return 0;
	}
	
	public void incChapter(int advenType) {
		checkOldAdvIsAllPass(advenType);
		if (advenType == 1) {
			adventrues.add(new ArrayList<Integer>());
		} else {
			eliteAdventures.add(new ArrayList<Integer>());
		}
	}

	private void checkOldAdvIsAllPass(int advenType) {
		if (advenType == 1) {
			int ci= 1;
			for (List<Integer> advIds : adventrues) {
				if (advIds.size() == config.adventures.getStageMax(ci)) {
					advIds.clear();
					advIds.add(-1);
				}
				ci++;
			}
		} else {
			int ci= 1;
			for (List<Integer> advIds : eliteAdventures) {
				if (advIds.size() == config.adventures.getStageMax(ci)) {
					advIds.clear();
					advIds.add(-1);
				}
				ci++;
			}
		}
	}

	@Override
	public JsonNode toWholeJson() {
		ObjectNode obj = jackson.createObjectNode();
		obj.put("adv", convertToArrayNode(adventrues));		
		obj.put("eadv", convertToArrayNode(eliteAdventures));		
		return obj;
		
//		return forge();
	}
	
	private JsonNode forge() {
		ObjectNode obj = jackson.createObjectNode();
		
		List<List<Integer>> advs = Lists.newArrayList();
		advs.add(Lists.newArrayList(-1));
		advs.add(Lists.newArrayList(1,2,3,15,4,5,6,7,8,9,10,11,12,13,14));
		advs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26));
		advs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,27,26));
		advs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,30,24,25,26,27,28,29,31));
		advs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,22));
		advs.add(Lists.newArrayList(1,2,3,4,5,6,26,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,25,24));
		advs.add(Lists.newArrayList(-1));
		advs.add(Lists.newArrayList(-1));
		advs.add(Lists.newArrayList(-1));
		advs.add(Lists.newArrayList(1,2,3,4,5,6,44,7,8,9,10,41,11,12,13,14,15,16));
		this.adventrues = advs;
		obj.put("adv", convertToArrayNode(advs));
		
		List<List<Integer>> eadvs = Lists.newArrayList();
		eadvs.add(Lists.newArrayList(-1));
		eadvs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));
		eadvs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25));
		eadvs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25));
		eadvs.add(Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29));
		eadvs.add(Lists.newArrayList(1));
		eadvs.add(Lists.newArrayList(1));
		eadvs.add(Lists.newArrayList(1));
		eadvs.add(Lists.newArrayList(1,2,3,4,5,6));
		eadvs.add(Lists.newArrayList(1));
		eadvs.add(new ArrayList<Integer>());
		eadvs.add(new ArrayList<Integer>());
		this.eliteAdventures = eadvs;
		obj.put("eadv", convertToArrayNode(eadvs));	
		
		return obj;
	};

	@Override
	public String getKey() {
		return "adventure";
	}

}
