package com.jelly.player;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.TimeUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ChallengeModel {
	// 挑战结果信息   时间(s),type(0主动，1：被动),用户,结果(0输 ，//1赢)，改变后排名(如果为-1则未变化)
	private int time;

	private int type;

	private String name;
	
	private int res;

	private int toLv;

	public ChallengeModel() {
	}


	public ChallengeModel(int time, int type, String name, int res, int toLevel) {
		super();
		this.time = time;
		this.type = type;
		this.name = name;
		this.res = res;
		this.toLv = toLevel;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getRes() {
		return res;
	}


	public void setRes(int res) {
		this.res = res;
	}


	public int getToLv() {
		return toLv;
	}


	public void setToLv(int toLv) {
		this.toLv = toLv;
	}


	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}


	public ArrayNode toJsonArray() {
		ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
		array.add((TimeUtil.getCurrentTime() - time));
		array.add(type);
		array.add(name);
		array.add(res);
		array.add(toLv);
		return array;
	}

}
