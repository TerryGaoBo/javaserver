package com.jelly.player;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.ItemGroupEnum;
import com.dol.cdf.common.bean.ShopItemGroup;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

public class PlayerShop extends DynamicJsonProperty {
	
	@JsonProperty("sg")
	private Map<Integer, ShopGoods> shopGoods;	//	商店商品

	@JsonProperty("bg")
	private Map<Integer, ShopGoods> blackGoods;	//	黑市商品
	
	@JsonProperty("bsr")
	private int buyShopRefreshTimes;
	
	@JsonProperty("bbr")
	private int buyBlackRefreshTimes;
	
	@JsonProperty("lst")
	private int lastShopRefreshTime;

	@JsonProperty("lbt")
	private int lastBlackRefreshTime;
	
	// 每天每件超值商品出现次数,该成员会在每日21:00被清空
	@JsonProperty("aog")
	private Map<Integer, Integer> appearOverflowGoods;
	
	public PlayerShop() {
		this.shopGoods = Maps.newHashMap();
		this.blackGoods = Maps.newHashMap();
		this.appearOverflowGoods = Maps.newHashMap();
		this.buyShopRefreshTimes = 0;
		this.buyBlackRefreshTimes = 0;
		this.lastShopRefreshTime = 0;
		this.lastBlackRefreshTime = 0;
	}
	
	public void setBuyBlackRefreshTimes(int bbr) {
		this.buyBlackRefreshTimes = bbr;
		addChange("bbr", this.buyBlackRefreshTimes);
	}
	
	public int getBuyBlackRefreshTimes() {
		return this.buyBlackRefreshTimes;
	}
	
	public void setBuyShopRefreshTimes(int bsr) {
		this.buyShopRefreshTimes = bsr;
		addChange("bsr", this.buyShopRefreshTimes);
	}
	
	public int getBuyShopRefreshTimes() {
		return this.buyShopRefreshTimes;
	}
	
	public int getLastShopRefreshTime() {
		return lastShopRefreshTime;
	}

	public void setLastShopRefreshTime(int lastShopRefreshTime) {
		this.lastShopRefreshTime = lastShopRefreshTime;
		addChange("lst", this.lastShopRefreshTime);
	}

	public int getLastBlackRefreshTime() {
		return lastBlackRefreshTime;
	}

	public void setLastBlackRefreshTime(int lastBlackRefreshTime) {
		this.lastBlackRefreshTime = lastBlackRefreshTime;
		addChange("lbt", this.lastBlackRefreshTime);
	}
	
	public void setShopGoods(Map<Integer, ShopGoods> shopGoods) {
		this.shopGoods = shopGoods;
		for (Map.Entry<Integer, ShopGoods> entry : this.shopGoods.entrySet()) {
			appendChangeMap("sg", String.valueOf(entry.getKey()), entry.getValue().toWholeJson());
		}
	}

	public void setBlackGoods(Map<Integer, ShopGoods> blackGoods) {
		this.blackGoods = blackGoods;
		for (Map.Entry<Integer, ShopGoods> entry : this.blackGoods.entrySet()) {
			appendChangeMap("bg", String.valueOf(entry.getKey()), entry.getValue().toWholeJson());
		}
	}

	public void setAppearOverflowGoods(Map<Integer, Integer> appearOverflowGoods) {
		this.appearOverflowGoods = appearOverflowGoods;
	}
	
	public void buy(ShopType type, int idx, Player player) {
		ShopGoods goods = null;
		String key = "";
		switch (type) {
		case SHOP:
			goods = shopGoods.get(idx);
			key = "sg";
			break;
		case BLACK:
			goods = blackGoods.get(idx);
			key = "bg";
			break;
		}
		VariousItemEntry costEntry = new VariousItemEntry(goods.getCostType(), goods.getCost());
		int chkRes = VariousItemUtil.checkBonus(player, costEntry, false);
		if (chkRes != MessageCode.OK) {
			player.sendResult(OperResultType.BUY_SHOP, chkRes);
			return;
		}
		if (goods.isBuyAlready()) {
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.FAIL);
			return;
		}
		VariousItemEntry nwEntry = new VariousItemEntry(goods.getId(), 1);
		chkRes = VariousItemUtil.checkBonus(player, nwEntry, true);
		VariousItemUtil.doBonus(player, nwEntry, LogConst.BUY_SHOP_ITEM, true);
		VariousItemUtil.doBonus(player, costEntry, LogConst.BUY_SHOP_ITEM, false);
		if (chkRes == MessageCode.OK) {
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.OK, idx);
		} else {
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.BUY_GOODS);
		}
		if (nwEntry.getGroup() == ItemGroupEnum.hero) {
			LogUtil.doGetNinjaLog((DefaultPlayer)player, Integer.parseInt(nwEntry.getType()), 1, LogConst.BUY_SHOP_ITEM);
		}
		goods.setBuyAlready(true);
		this.appendChangeMap(key, idx, goods.toWholeJson());
		if (idx == 5) {
			//	购买神秘商品后,系统会自动刷新一件新的神秘商品
			refreshGoods(type, idx);
		}
	}
	
	public void normalRefresh(ShopType type, Player player) {
		long intervalDay = TimeUtil.diffDays(TimeUtil.getCurrentTime() * 1000L, 
				type.getRefreshLastTime((DefaultPlayer)player) * 1000L);
		if (((shopGoods.isEmpty() || blackGoods.isEmpty()) || intervalDay >= 1)) {
			this.appearOverflowGoods.clear();
			for (int i = 0; i < 6; ++i) {
				refreshGoods(type, i);
			}
			updateLastFreeRefreshTimes(type, player);
		}
	}
	
	private void updateLastFreeRefreshTimes(ShopType type, Player player) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
		calendar.set(Calendar.HOUR_OF_DAY, 21);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		type.setLastRefreshTime((DefaultPlayer)player, (int)(calendar.getTimeInMillis() / 1000));
		type.setRefreshTimes((DefaultPlayer)player, 1);
	}
	
	public void refresh(ShopType type, Player player) {
		VariousItemEntry costEntry = type.getRefreshCost((DefaultPlayer)player);
		if (!player.getProperty().hasEnoughMoney(costEntry.getType(), costEntry.getAmount())) {
			// costEntry.getType()=="gold"或costEntry.getType()=="silver"
			// 这里为了方便直接返回SILVER_NOT_ENUGH表示失败,客户端需要在发送消息前检查并确保玩家有足够的金币与银币
			player.sendResult(OperResultType.REFRESH_SHOP, MessageCode.SILVER_NOT_ENUGH);
			return;
		}
		//	付费只有刷新5个随机商品,神秘商品不会被刷新
		for (int i = 0; i < 5; ++i) {
			refreshGoods(type, i);
		}
		VariousItemUtil.doBonus(player, costEntry, LogConst.BUY_SHOP_REFRESH_TIMES, false);
		updateCostRefreshTimes(type, player);
		player.sendResult(OperResultType.REFRESH_SHOP);
	}
	
	private void updateCostRefreshTimes(ShopType type, Player player) {
		type.setRefreshTimes((DefaultPlayer)player, type.getRefreshTimes((DefaultPlayer)player) + 1);
	}
	
	private void refreshGoods(ShopType type, int idx) {		
		ShopItemGroup grp = null;
		while (true) {
			if ((grp = type.randGoods(idx)) != null) {
				Integer overflow = grp.getOverflow();
				if (overflow != null && overflow > 0) {
					Integer val = appearOverflowGoods.get(grp.getId());
					if (val != null && val >= grp.getOverflow()) {
						continue;
					}
					appearOverflowGoods.put(grp.getId(), val == null ? 1 : val + 1);
				}
				break;
			}
		}
		
		//	生成商品贩卖价格
		int min = grp.getMinprice()[0].getAmount(), max = grp.getMaxprice()[0].getAmount();
		ShopGoods goods = new ShopGoods(grp.getId(), grp.getMaxprice()[0].getType(), 
				min == max ? min : Rnd.get(min, max), false);
		switch (type) {
		case SHOP:	//	普通商品
			shopGoods.put(idx, goods);
			this.appendChangeMap("sg", String.valueOf(idx), goods.toWholeJson());
			break;
		case BLACK:	//	黑市商品
			blackGoods.put(idx, goods);
			this.appendChangeMap("bg", String.valueOf(idx), goods.toWholeJson());
			break;
		}
	}

	@Override
	public String getKey() {
		return "shop";
	}

	@Override
	public JsonNode toWholeJson() {
		ObjectNode wholeJson = jackson.createObjectNode();
		
		ArrayNode sgArr = jackson.createArrayNode();	//	普通商品
		for (ShopGoods goods : shopGoods.values()) {
			sgArr.add(goods.toWholeJson());
		}
		wholeJson.put("sg", sgArr);
		
		ArrayNode bgArr = jackson.createArrayNode();	//	黑市商品
		for (ShopGoods goods : blackGoods.values()) {
			bgArr.add(goods.toWholeJson());
		}
		wholeJson.put("bg", bgArr);
		
		wholeJson.put("bsr", buyShopRefreshTimes);	//	已付费刷新商品次数
		wholeJson.put("bbr", buyBlackRefreshTimes);	//	已付费刷新黑市商品次数
		
		return wholeJson;
	}
}