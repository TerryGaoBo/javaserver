package com.jelly.player.shop;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShopItem  extends DynamicJsonProperty{
	
	@JsonProperty("idx")
	private Integer idx = 0; // 唯一id
	
	@JsonProperty("s")
	private boolean isBuy = false; // 是否已经购买了此商品
	
	public ShopItem()
	{
	}
	
	public ShopItem(Integer idx , boolean isBuy)
	{
		this.idx = idx;
		this.isBuy = isBuy;
	}
	
	@Override
	public String getKey() {
		return "";
	}
	
	@Override
	public JsonNode toWholeJson() {
		ObjectNode wholeJson = jackson.createObjectNode();
		wholeJson.put("idx", this.idx);
		return wholeJson;
	}
}
