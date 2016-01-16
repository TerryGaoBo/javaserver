package com.jelly.player;

import java.util.HashMap;
import java.util.Map;

import com.dol.cdf.common.TimeUtil;
import com.fasterxml.jackson.databind.JsonNode;

public class CoolDown {
	
	/**
	 * cd列表
	 * key-cd类型 value-结束时间戳
	 */
	private Map<Integer, Integer> cdList;
	
	/**
	 * 清cd的使用次数
	 */
	private Map<Integer, Integer> clearTimes;
	
	/**
	 * 最后一次使用清cd
	 */
	private long lastClear;
	
	public CoolDown() {
		cdList = new HashMap<Integer, Integer>();
		clearTimes = new HashMap<Integer, Integer>();
		lastClear = System.currentTimeMillis();
	}
	
	/**
	 * 加cd
	 * @param type
	 * @param seconds
	 */
	public boolean addCd(int type, int seconds) {
		if (getFinishTime(type) == 0 || getCdTime(type) <= 0) {
			cdList.put(type, now() + seconds);
		} else {
			cdList.put(type, getFinishTime(type) + seconds);
		}
		
		return true;
	}
	
	/**
	 * 清cd
	 * @param type
	 */
	public void clearCd(int type) {
		if (cdList.get(type) != null) {
			cdList.put(type, 0);
		}
		if (clearTimes.get(type) == null) {
			clearTimes.put(type, 1);
		} else {
			clearTimes.put(type, clearTimes.get(type) + 1);
		}
	}
	
	/**
	 * cd结束时间
	 * @param type
	 * @return
	 */
	private int getFinishTime(int type) {
		Integer time = cdList.get(type);
		if (time == null) {
			return 0;
		}
		return time.intValue();
	}
	
	/**
	 * cd剩余时间(秒)
	 * @param type
	 * @return <0 说明cd已结束
	 */
	protected int getCdTime(int type) {
		Integer ts = cdList.get(type);
		if (ts == null) {
			return -1;
		}
		return cdList.get(type) - now();
	}
	
	/**
	 * 是否在冷却中
	 * @param type
	 * @return
	 */
	public boolean isCoolDowning(int type) {
		ECoolDown e = ECoolDown.parse(type);
		if (e == ECoolDown.unknow) {
			return true;
		}
		return isCoolDowning(e);
	}
	
	public boolean isCoolDowning(ECoolDown type) {
		return getCdTime(type.getCdType()) > type.getCdMax();
	}
	
	/**
	 * 重置清cd次数
	 */
	public boolean needResetTimes() {
		if (TimeUtil.isSameDay(lastClear, System.currentTimeMillis())) {
			return false;
		}
		for (int type : clearTimes.keySet()) {
			clearTimes.put(type, 0);
		}
		return true;
	}
	
	public int getClearCdTimes(int type) {
		Integer times = clearTimes.get(type);
		if (times == null) {
			return 0;
		}
		return times.intValue();
	}
	
	
	public JsonNode toJson() {
		return null;
	}
	
	private int now() {
		return TimeUtil.getCurrentTime();
	}
	

}
