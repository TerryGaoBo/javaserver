package com.jelly.player;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class PlayerOperResult extends DynamicJsonProperty{

	private Object param;
	
	private int res;
	
	private int type;
	
	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
		addChange("param", convertToJsonNode(this.param));
	}

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
		addChange("res", res);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
		addChange("type", this.type);
	}

	@Override
	public String getKey() {
		return "operResult";
	}

	@Override
	public JsonNode toWholeJson() {
		return null;
	}

}
