package com.jelly.player;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.TimeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.hero.PlayerRecruit;

public class BuildInstance {
	
	private static final Logger logger = LoggerFactory.getLogger(PlayerRecruit.class);
	@JsonProperty("l")
	private Integer level = 1;
	@JsonProperty("d")
	private List<Integer> cdTime = Lists.newArrayList();

	// 清理次数
	@JsonProperty("c")
	private int clearTimes;

	// 今日完成次数
	@JsonProperty("t")
	private int todayFinish;
	// 完成时间
	@JsonProperty("f")
	private int finishTime;

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void resetCdTime(int idx) {
		cdTime.set(idx, 0);
	}
	
	public void clearCd(boolean needIncrement) {
		for (int i = 0; i < cdTime.size(); i++) {
			cdTime.set(i, 0);
		}
		if (needIncrement) {
			this.clearTimes++;
		}
	}

	public int getClearTimes() {
		return clearTimes;
	}

	/**
	 * 添加CD
	 * 
	 * @param idx
	 * @param duration
	 *            单位分钟
	 */
	public void addCd(int idx, int duration) {
		checkCd(idx);
		int targetTime = (int) (TimeUtil.getCurrentTime() + TimeUnit.MINUTES.toSeconds(duration));
		cdTime.set(idx, targetTime);
	}

	/**
	 * 是否处于禁止操作的时间段中
	 * 
	 * @param idx
	 * @param time
	 *            单位分钟
	 * @return
	 */
	public boolean isForbid(int idx, int time) {
		checkCd(idx);
		int duration = cdTime.get(idx) - TimeUtil.getCurrentTime();
		return duration >= TimeUnit.MINUTES.toSeconds(time);
	}

	/**
	 * 检测是否处于CD中
	 * 
	 * @param idx
	 * @return
	 */
	public boolean isCding(int idx) {
		checkCd(idx);
		return cdTime.get(idx) > TimeUtil.getCurrentTime();
	}
	
	public boolean isCdingWithDelta(int idx,int minute) {
		checkCd(idx);
		return cdTime.get(idx) > TimeUtil.getCurrentTime()+TimeUnit.MINUTES.toSeconds(minute);
	}

	public int getCdTime(int idx) {
		checkCd(idx);
		return cdTime.get(idx);
	}

	/**
	 * 检测CD索引
	 * 
	 * @param idx
	 */
	public void checkCd(int idx) {
		if (cdTime.isEmpty() || cdTime.size() <= idx) {
			for (int i = cdTime.size(); i < idx + 1; i++) {
				cdTime.add(0);
			}
		}
	}

	/**
	 * 检测是否需要重置时间
	 */
	private void checkReset() {
		if (todayFinish != 0 && !TimeUtil.isSameDay(finishTime)) {
			todayFinish = 0;
		}
	}

	public int getTodayFinish() {
		checkReset();
		return todayFinish;
	}

	public void addTodayFinish() {
		todayFinish++;
		finishTime = TimeUtil.getCurrentTime();
	}

	public void reduceTodayFinish() {
		todayFinish--;
	}

	public void addTodayFinish(int count) {
		todayFinish += count;
	}

	
	public ObjectNode toJson() {
		checkReset();
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("lv", level);
		ArrayNode timeArray = DynamicJsonProperty.jackson.createArrayNode();
		for (Integer ctime : cdTime) {
			int diffTime = ctime - TimeUtil.getCurrentTime();
			if (diffTime < 0) {
				diffTime = 0;
			}
			timeArray.add(diffTime);
		}
		if (timeArray.size() > 0) {
			obj.put("cd", timeArray);
		}
		return obj;
	}
	
	public ObjectNode toJson(int dayFinishCount) {
		ObjectNode obj = toJson();
		if (dayFinishCount != 0) {
			// 剩余次数
			obj.put("num", dayFinishCount - todayFinish);
		}
		return obj;
	}

}
