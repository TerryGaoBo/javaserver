package com.jelly.activity;

import io.nadron.app.Player;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PlayerActivity extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerActivity.class);
	
	@JsonProperty("ac")
	Map<Integer, Map<Integer, Integer>> acts = Maps.newHashMap();

	/**
	 * 如果活动没再运行，清理玩家活动
	 * @param worldActivity
	 */
	public void checkAndClear(WorldActivity worldActivity) {
		Set<Integer> racSet = worldActivity.runningActivities;
		Set<Integer> removedId = Sets.newHashSet();
		for (Integer actId : acts.keySet()) {
			if(racSet.contains(actId) == false) {
				removedId.add(actId);
			}
		}
		for (Integer id : removedId) {
			clearAct(id);
		}
	}

	public int addActCount(int actId, int actType, int value) {
		int totelVal = value;
		Map<Integer, Integer> map = acts.get(actId);
		if (map == null) {
			map = Maps.newHashMap();
			map.put(actType, value);
			acts.put(actId, map);
		} else {
			Integer currVal = map.get(actType);
			if (currVal == null) {
				currVal = 0;
			}
			totelVal = value + currVal;
			map.put(actType, totelVal);
		}
		return totelVal;
	}
	
	public void setActCount(int actId, int actType, int value ) {
		Map<Integer, Integer> map = acts.get(actId);
		if (map == null) {
			map = Maps.newHashMap();
			map.put(actType, value);
			acts.put(actId, map);
		} else {
			map.put(actType, value);
		}
	}

	void clearAct(int actId) {
		acts.remove(actId);
	}

	private int getActCount(int actId, int actType) {
		Map<Integer, Integer> map = acts.get(actId);
		if (map == null) {
			return 0;
		}
		Integer currVal = map.get(actType);
		if (currVal == null) {
			return 0;
		}
		return currVal;

	}

	/**
	 * 分发活动信息
	 * 
	 * @param type
	 * @param player
	 */
	public void dispatchEvent(ActivityType type, Player player) {
		float tarVal = Float.parseFloat(type.getValue());
		if (tarVal > 0) {
			Integer actId = type.getActId();
			if(actId != null) {
				int actType = type.getId();
				int currCount = getActCount(actId, actType);
				if (currCount < tarVal ) {
					int afterVal = addActCount(actId, actType, 1);
					//logger.info("after val:{}, actId:{},actType:{}",afterVal,actId,actType);
					if (afterVal >= tarVal) {
						// 可以发奖了
						VariousItemEntry[] rewards = type.getNormalRewards();
						player.getMail().addSysItemMail(rewards, MessageCode.MAIL_TITLE_REWARD, type.getTid());
						player.sendMiddleMessage(MessageCode.MAIL_REWARD_TIP);
						//如果是十连抓可以重复领取奖励
						if(type == ActivityType.RAFF_2) {
							setActCount(actId, actType, 0);
						}
					}
				}
			}
		}
	}
	
	public void dispatchEventRaffHeros(ActivityType type, Player player){
		float tarVal = Float.parseFloat(type.getValue());
		if (tarVal > 0) {
			Integer actId = type.getActId();
			if(actId != null) {
				int actType = type.getId();
//				int currCount = getActCount(actId, actType);
				VariousItemEntry[] rewards = type.getNormalRewards();
				player.getMail().addSysItemMail(rewards, MessageCode.MAIL_TITLE_REWARD, type.getTid());
				player.sendMiddleMessage(MessageCode.MAIL_REWARD_TIP);
				setActCount(actId, actType, 0);
			}
		}
	}
	
	public void dispatchExamPassEvent(int tarPass,Player player) {
		ActivityType type = ActivityType.EXAM_PASS;
		if(type.isActive()){
			int[] vals = StringHelper.getIntList(type.getValue().split(";"));
			Map<Integer, Integer> finishs = Maps.newLinkedHashMap();
			for (int i = 0; i < vals.length; i++) {
				if (tarPass >= vals[i]) {
					finishs.put(vals[i],i);
				}else {
					break;
				}
			}
			if (finishs.isEmpty()) {
				return;
			}
			Integer actId = type.getActId();
			if (actId != null) {
				int actType = type.getId();
				int actCount = getActCount(actId, actType);
				String[] itemStrings = type.getReward().split(",");
				for (Integer fi : finishs.keySet()) {
					if(fi > actCount) {
						VariousItemEntry[] items = VariousItemUtil.parse1(itemStrings[finishs.get(fi)]);
						player.getMail().addSysItemMail(items, MessageCode.MAIL_TITLE_REWARD, type.getTid());
						setActCount(actId, actType, fi);
						player.sendMiddleMessage(MessageCode.MAIL_REWARD_TIP);
					}
				}
			}
			
		}
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public JsonNode toWholeJson() {
		return null;
	}

}
