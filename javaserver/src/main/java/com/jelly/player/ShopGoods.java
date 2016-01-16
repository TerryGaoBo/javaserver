package com.jelly.player;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShopGoods extends DynamicJsonProperty {

	@JsonProperty("id")
	private int id;
	
	@JsonProperty("ct")
	private String costType;
	
	@JsonProperty("pc")
	private int cost;

	@JsonProperty("iba")
	private boolean isBuyAlready;
	
	@JsonProperty("cnt")
	private int count;
	
	public ShopGoods() {}
	
	public ShopGoods(int id, String costType, int cost, boolean isBuyAlready) {
		this.id = id;
		this.costType = costType;
		this.cost = cost;
		this.isBuyAlready = isBuyAlready;
		this.count = 1;
	}

	public boolean isBuyAlready() {
		return isBuyAlready;
	}

	public void setBuyAlready(boolean isBuyAlready) {
		this.isBuyAlready = isBuyAlready;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCostType() {
		return costType;
	}

	public void setCostType(String costType) {
		this.costType = costType;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public String getKey() {
		return null;
	}

	@Override
	public JsonNode toWholeJson() {
		ObjectNode node = jackson.createObjectNode();
		node.put("id", getId());
		node.put("price", getCost());
		node.put("priceType", getCostType());
		node.put("isBuyAlready", isBuyAlready());
		return node;
	}

}
