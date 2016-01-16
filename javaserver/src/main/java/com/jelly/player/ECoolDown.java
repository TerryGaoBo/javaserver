package com.jelly.player;

import java.util.concurrent.TimeUnit;

public enum ECoolDown {

	unknow(0, 0, TimeUnit.SECONDS), 
	arena(1, 5,TimeUnit.MINUTES), 
	union(2, 23,TimeUnit.HOURS);

	private int cdType;

	private TimeUnit timeUnit;
	
	private int cdMax;

	public int getCdType() {
		return cdType;
	}

	ECoolDown(int cdType, int cdMax, TimeUnit timeUnit) {
		this.cdType = cdType;
		this.cdMax = cdMax;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * 返回时间戳（秒）
	 * 
	 * @return
	 */
	public long getCdMax() {
		return timeUnit.toSeconds(cdMax);
	}

	public static ECoolDown parse(int type) {
		for (ECoolDown e : ECoolDown.values()) {
			if (e.getCdType() == type)
				return e;
		}
		return ECoolDown.unknow;
	}

	public static boolean validate(int type) {
		for (ECoolDown e : ECoolDown.values()) {
			if (e.getCdType() == type)
				return true;
		}
		return false;
	}
}
