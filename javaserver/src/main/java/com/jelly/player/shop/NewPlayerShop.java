package com.jelly.player.shop;

import io.nadron.app.Player;
import io.nadron.app.impl.OperResultType;

import java.util.Calendar;
import java.util.Map;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Shop;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;

/**
 * 玩家个人商店
 */
public class NewPlayerShop extends DynamicJsonProperty {
	
	@JsonProperty("sj")
	private Map<Integer, ShopItem> daojuItems;
	
	@JsonProperty("sl")
	private Map<Integer, ShopItem> cailiaoItems;
	
	@JsonProperty("sk")
	private Map<Integer, ShopItem> zhaohuankaItems;
	
	@JsonProperty("sp")
	private Map<Integer, ShopItem> vipItems;
	
	//商品的购买次数会在每日21:00被清空
	@JsonProperty("st")
	private int lastOpenTime = 0; // 上次打开时间，如果当前时间在21点之后，上次打开时间在21点之前那么清空，如果当前时间在21点之前 ，so
	
	@JsonProperty("sc")
	private int buyCount = 0; // 刷新次数
	
	private final int TIME_T = 21; // 每天21点更新
	
	
	//////////////////////////////////// 忍者大战商店 ////////////////////////////////////////////////////
	
	@JsonProperty("w")
	private Map<Integer, ShopItem> renjieItems;
	
	@JsonProperty("wt")
	private int warlastOpenTime = 0;
	
	@JsonProperty("wc")
	private int warbuyCount = 0; // 刷新次数
	
	
	///////////////////////////////////  竞技场商店  ////////////////////////////////////
	
	@JsonProperty("jw")
	private Map<Integer, ShopItem> jingjiItems;
	
	@JsonProperty("jt")
	private int jingjilastOpenTime = 0;
	
	@JsonProperty("jc")
	private int jingjibuyCount = 0; // 刷新次数
	
	public NewPlayerShop(){
		this.daojuItems = Maps.newHashMap();
		this.cailiaoItems = Maps.newHashMap();
		this.zhaohuankaItems = Maps.newHashMap();
		this.vipItems = Maps.newHashMap();
		this.lastOpenTime = 0;
		this.buyCount = 0;
		
		this.renjieItems = Maps.newHashMap();
		this.warlastOpenTime = 0;
		this.warbuyCount = 0;
		
		this.jingjiItems = Maps.newHashMap();
		this.jingjilastOpenTime = 0;
		this.jingjibuyCount = 0;
	}
	
	@Override
	public String getKey() {
		return "newShop";
	}
	
//	Calendar c = Calendar.getInstance();
//	int year = c.get(Calendar.YEAR); 
//	int month = c.get(Calendar.MONTH); 
//	int date = c.get(Calendar.DATE); 
//	int hour = c.get(Calendar.HOUR_OF_DAY); 
//	int minute = c.get(Calendar.MINUTE); 
//	int second = c.get(Calendar.SECOND); 
//	int hourse = c.get(Calendar.HOUR);
//	System.out.println(year + "/" + month + "/" + date + " " +hour + ":" +minute + ":" + second); 
	public void getShopInfo(Player player)
	{
		if(lastOpenTime <= 0){
			buyCount = 0;
			this.daojuItems.clear();
			this.cailiaoItems.clear();
			this.zhaohuankaItems.clear();
			this.vipItems.clear();
		}
		int ctime = TimeUtil.getCurrentTime();
		if(lastOpenTime<=0){
			lastOpenTime = ctime;
		}else{
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(ctime * 1000L);
			int year = c.get(Calendar.YEAR); 
			int hour = c.get(Calendar.HOUR_OF_DAY); 
			int day = c.get(Calendar.DAY_OF_YEAR);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTimeInMillis(lastOpenTime*1000L);
			int year2 = c2.get(Calendar.YEAR); 
			int hour2 = c2.get(Calendar.HOUR_OF_DAY); 
			int day2 = c2.get(Calendar.DAY_OF_YEAR);
			
			if(year != year2){
				this.clearBuyCounts();
			}else{
				if(day == day2){
					if(hour >= TIME_T){
						if(hour2 <= TIME_T){
							this.clearBuyCounts();
						}
					}
				}else{
					if(day - day2 == 1){
						if(hour2 <= TIME_T){
							this.clearBuyCounts();
						}
					}else{
						this.clearBuyCounts();
					}
				}
			}
		}
		
		player.sendMessage(getKey(),toWholeJson());
	}
	
	public void clearBuyCounts()
	{
		this.buyCount = 0;
		this.daojuItems.clear();
		this.cailiaoItems.clear();
		this.zhaohuankaItems.clear();
		this.vipItems.clear();
		this.lastOpenTime = TimeUtil.getCurrentTime();
	}
	
	public void buyShopItemFunc(Player player,JsonNode object)
	{
		int idx = object.get("idx").asInt(); //唯一id
		Shop shop = AllGameConfig.getInstance().items.getGoldShopByID(idx);
		if(shop == null){
			return;
		}
		int type = shop.getPage().intValue();
		VariousItemEntry gold = shop.getPrice()[0];
		int chkRes = VariousItemUtil.checkBonus(player, gold, false);
		if (chkRes != MessageCode.OK) {
			player.sendMiddleMessage(MessageCode.getErrorTxtID(gold.getType()));
			return;
		}
		
		 Map<Integer, ShopItem> tempList = null;
		if(type == 1){
			tempList = this.daojuItems;
		}else if(type == 2){
			tempList = this.cailiaoItems;
		}else if(type == 3){
			tempList = this.zhaohuankaItems;
		}else if(type == 4){
			tempList = this.vipItems;
		}
		
		if(tempList == null){
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		
		if(tempList.get(shop.getOnlyid()) != null){
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		
		int itemid = shop.getId();
		int num = shop.getNum();
		
		VariousItemEntry nwEntry = new VariousItemEntry(itemid,num);
		chkRes = VariousItemUtil.checkBonus(player, nwEntry, true);
		if(chkRes != MessageCode.OK){
			player.sendMiddleMessage(chkRes);
			player.sendMessage(getKey(),toWholeJson());
			return;
		}
		
		VariousItemUtil.doBonus(player, nwEntry, LogConst.SHOP_PLAYERSHOP_1, true);
		VariousItemUtil.doBonus(player, gold, LogConst.SHOP_PLAYERSHOP_1, false);
		if (chkRes == MessageCode.OK) {
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.OK, itemid);
		}else{
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.BUY_GOODS);
		}
		
//		int code = VariousItemUtil.doBonus(player, gold, LogConst.SHOP_BUY_ITEM, false);
//		if (code != MessageCode.OK) {
//			player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
//			return;
//		}
		
		ShopItem item = new ShopItem(shop.getOnlyid(),true);
		tempList.put(shop.getOnlyid(), item);
		
//		if(type == 1){
//			this.daojuItems.put(shop.getOnlyid(), item);
//		}else if(type == 2){
//			this.cailiaoItems.put(shop.getOnlyid(), item);
//		}else if(type == 3){
//			this.zhaohuankaItems.put(shop.getOnlyid(), item);
//		}else if(type == 4){
//			this.vipItems.put(shop.getOnlyid(), item);
//		}
		
		player.sendMessage(getKey(),toWholeJson());
	}
	
	public void refreshShopFunc(Player player)
	{
		int[] golds = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_SHOP_COST);
		Integer maxCount = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_SHOP_TIMES);
		if(buyCount>=maxCount.intValue()){
			player.sendMiddleMessage(MessageCode.SHOP_TIPS_1);
			return;
		}
		int gold = golds[buyCount];
		if(!player.getProperty().hasEnoughMoney(gold,null,null)){
			player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
			return;
		}
		
//		player.getProperty().changeMoney(-gold,0);
		
		player.getProperty().changeMoney("gold", -gold, 0, player);
		
		buyCount = buyCount+1;
		
		this.daojuItems.clear();
		this.cailiaoItems.clear();
		this.zhaohuankaItems.clear();
		this.vipItems.clear();
		
		player.sendMessage(getKey(),toWholeJson());
	}
	
	@Override
	public JsonNode toWholeJson() {
		ObjectNode wholeJson = jackson.createObjectNode();
		wholeJson.put("buyCount", this.buyCount); // 刷新次数
		ArrayNode itemsArr = DynamicJsonProperty.jackson.createArrayNode();
		for (Integer key : daojuItems.keySet()) {
			itemsArr.add(key.intValue());
		}
		wholeJson.put("daojuItems", itemsArr);
		
		ArrayNode cailiaoArr = DynamicJsonProperty.jackson.createArrayNode();
		for ( Integer key : cailiaoItems.keySet()){
			cailiaoArr.add(key.intValue());
		}
		wholeJson.put("cailiaoItems", cailiaoArr);
		
		ArrayNode zhaohuankaArr = DynamicJsonProperty.jackson.createArrayNode();
		for(Integer key : zhaohuankaItems.keySet()){
			zhaohuankaArr.add(key.intValue());
		}
		wholeJson.put("zhaohuankaItems", zhaohuankaArr);
		
		ArrayNode vipArr = DynamicJsonProperty.jackson.createArrayNode();
		for(Integer key : vipItems.keySet()){
			vipArr.add(key.intValue());
		}
		wholeJson.put("vipItems", vipArr);
		
		Map<Integer,Shop> goldShopItems = AllGameConfig.getInstance().items.getShopItemsList();
		
		ArrayNode shopItemLists = DynamicJsonProperty.jackson.createArrayNode();
		for(Shop shop : goldShopItems.values())
		{
			ObjectNode d = jackson.createObjectNode();
			d.put("shopType", shop.getShopType());
			d.put("onlyid", shop.getOnlyid());
			d.put("id", shop.getId());
//			d.put("itemname", shop.getItemname());
			d.put("num", shop.getNum());
			d.put("price", shop.getPrice()[0].toString());
			d.put("order", shop.getOrder());
			d.put("page", shop.getPage());
			shopItemLists.add(d);
		}
		
		wholeJson.put("shopItemLists", shopItemLists);
		
		return wholeJson;
	}
	
	//////////////////////////////////// 忍界商店 ////////////////////////
	public void getWarShopInfo(Player player)
	{
		if(warlastOpenTime <= 0){
			warbuyCount = 0;
			this.renjieItems.clear();
		}
		int ctime = TimeUtil.getCurrentTime();
		if(warlastOpenTime<=0){
			warlastOpenTime = ctime;
		}else{
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(ctime * 1000L);
			int year = c.get(Calendar.YEAR); 
			int hour = c.get(Calendar.HOUR_OF_DAY); 
			int day = c.get(Calendar.DAY_OF_YEAR);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTimeInMillis(warlastOpenTime*1000L);
			int year2 = c2.get(Calendar.YEAR); 
			int hour2 = c2.get(Calendar.HOUR_OF_DAY); 
			int day2 = c2.get(Calendar.DAY_OF_YEAR);
			
			if(year != year2){
				this.clearWarCounts();
			}else{
				if(day == day2){
					if(hour >= TIME_T){
						if(hour2 <= TIME_T){
							this.clearWarCounts();
						}
					}
				}else{
					if(day - day2 == 1){
						if(hour2 <= TIME_T){
							this.clearWarCounts();
						}
					}else{
						this.clearWarCounts();
					}
				}
			}
		}
		
		player.sendMessage("warShopMsg",toWarJson());
	}
	
	public void buyWarShopFunc(Player player,JsonNode object)
	{
		int idx = object.get("idx").asInt(); //唯一id
		Shop shop = AllGameConfig.getInstance().items.getRenjieShopByID(idx);
		if(shop == null){
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		
		VariousItemEntry coin = shop.getPrice()[0];
		int chkRes = VariousItemUtil.checkBonus(player, coin, false);
		if (chkRes != MessageCode.OK) {
			player.sendMiddleMessage(MessageCode.getErrorTxtID(coin.getType()));
			return;
		}
		
		if(renjieItems.get(shop.getOnlyid()) != null){
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		
		int itemid = shop.getId();
		int num = shop.getNum();
		
		VariousItemEntry nwEntry = new VariousItemEntry(itemid,num);
		chkRes = VariousItemUtil.checkBonus(player, nwEntry, true);
		
		if(chkRes != MessageCode.OK){
			player.sendMiddleMessage(chkRes);
			player.sendMessage("warShopMsg",toWarJson());
			return;
		}
		
		VariousItemUtil.doBonus(player, nwEntry, LogConst.SHOP_RENJIE_1, true);
		VariousItemUtil.doBonus(player, coin, LogConst.SHOP_RENJIE_1, false);
		if (chkRes == MessageCode.OK) {
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.OK, itemid);
		}else{
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.BUY_GOODS);
		}
		
		ShopItem item = new ShopItem(shop.getOnlyid(),true);
		renjieItems.put(shop.getOnlyid(), item);
		
		player.sendMessage("warShopMsg",toWarJson());
	}
	
	public void refreshWarShopFunc(Player player)
	{
		int[] golds = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_NINJASHOP_COST);
		Integer maxCount = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_NINJASHOP_TIMES);
		if(warbuyCount>=maxCount.intValue()){
			player.sendMiddleMessage(MessageCode.SHOP_TIPS_1);
			return;
		}
		
		int gold = golds[warbuyCount];
		if(!player.getProperty().hasEnoughMoney(gold,null,null)){
			player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
			return;
		}
		
//		player.getProperty().changeMoney(-gold,0);
		
		player.getProperty().changeMoney("gold", -gold, 0, player);
		
		warbuyCount = warbuyCount+1;
		
		this.renjieItems.clear();
		
		player.sendMessage("warShopMsg",toWarJson());
	}
	
	private JsonNode toWarJson() {
		ObjectNode wholeJson = jackson.createObjectNode();
		wholeJson.put("warbuyCount", this.warbuyCount); // 刷新次数
		
		ArrayNode itemsArr = DynamicJsonProperty.jackson.createArrayNode();
		for (Integer key : renjieItems.keySet()) {
			itemsArr.add(key.intValue());
		}
		wholeJson.put("warItems", itemsArr);
		
		Map<Integer,Shop> goldShopItems = AllGameConfig.getInstance().items.getRenJieItemsList();
		
		ArrayNode shopItemLists = DynamicJsonProperty.jackson.createArrayNode();
		for(Shop shop : goldShopItems.values())
		{
			ObjectNode d = jackson.createObjectNode();
			d.put("shopType", shop.getShopType());
			d.put("onlyid", shop.getOnlyid());
			d.put("id", shop.getId());
			d.put("num", shop.getNum());
			d.put("price", shop.getPrice()[0].toString());
			d.put("order", shop.getOrder());
			d.put("page", shop.getPage());
			shopItemLists.add(d);
		}
		
		wholeJson.put("warShopItemLists", shopItemLists);
		
		return wholeJson;
	}
	public void clearWarCounts()
	{
		this.warbuyCount = 0;
		this.renjieItems.clear();
		this.warlastOpenTime = TimeUtil.getCurrentTime();
	}

	///////////////////////////////////// 竞技商店 /////////////////////////////////////
	
	public void getJingjiInfo(Player player)
	{
		if(jingjilastOpenTime <= 0){
			jingjibuyCount = 0;
			this.jingjiItems.clear();
		}
		int ctime = TimeUtil.getCurrentTime();
		if(jingjilastOpenTime<=0){
			jingjilastOpenTime = ctime;
		}else{
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(ctime * 1000L);
			int year = c.get(Calendar.YEAR); 
			int hour = c.get(Calendar.HOUR_OF_DAY); 
			int day = c.get(Calendar.DAY_OF_YEAR);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTimeInMillis(jingjilastOpenTime*1000L);
			int year2 = c2.get(Calendar.YEAR); 
			int hour2 = c2.get(Calendar.HOUR_OF_DAY); 
			int day2 = c2.get(Calendar.DAY_OF_YEAR);
			
			if(year != year2){
				this.clearJingjiCounts();
			}else{
				if(day == day2){
					if(hour >= TIME_T){
						if(hour2 <= TIME_T){
							this.clearJingjiCounts();
						}
					}
				}else{
					if(day - day2 == 1){
						if(hour2 <= TIME_T){
							this.clearJingjiCounts();
						}
					}else{
						this.clearJingjiCounts();
					}
				}
			}
		}
		
		player.sendMessage("jingjiShopMsg",toJingjiJson());
	}
	public void buyJingjiShopFunc(Player player,JsonNode object)
	{
		int idx = object.get("idx").asInt(); //唯一id
		Shop shop = AllGameConfig.getInstance().items.getJingjiShopByID(idx);
		if(shop == null){
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		
		VariousItemEntry coin = shop.getPrice()[0];
		int chkRes = VariousItemUtil.checkBonus(player, coin, false);
		if (chkRes != MessageCode.OK) {
			player.sendMiddleMessage(MessageCode.getErrorTxtID(coin.getType()));
			return;
		}
		
		if(jingjiItems.get(shop.getOnlyid()) != null){
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		
		int itemid = shop.getId();
		int num = shop.getNum();
		
		VariousItemEntry nwEntry = new VariousItemEntry(itemid,num);
		chkRes = VariousItemUtil.checkBonus(player, nwEntry, true);
		
		if(chkRes != MessageCode.OK){
			player.sendMiddleMessage(chkRes);
			player.sendMessage("jingjiShopMsg",toJingjiJson());
			return;
		}
		
		VariousItemUtil.doBonus(player, nwEntry, LogConst.SHOP_JINGJI_1, true);
		VariousItemUtil.doBonus(player, coin, LogConst.SHOP_JINGJI_1, false);
		if (chkRes == MessageCode.OK) {
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.OK, itemid);
		}else{
			player.sendResult(OperResultType.BUY_SHOP, MessageCode.BUY_GOODS);
		}
		
		ShopItem item = new ShopItem(shop.getOnlyid(),true);
		jingjiItems.put(shop.getOnlyid(), item);
		
		player.sendMessage("jingjiShopMsg",toJingjiJson());
	}
	
	public void refreshJingjiShopFunc(Player player)
	{
		int[] golds = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_ARENASHOP_COST);
		Integer maxCount = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_ARENASHOP_TIMES);
		if(jingjibuyCount>=maxCount.intValue()){
			player.sendMiddleMessage(MessageCode.SHOP_TIPS_1);
			return;
		}
		
		int gold = golds[jingjibuyCount];
		if(!player.getProperty().hasEnoughMoney(gold,null,null)){
			player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
			return;
		}
		
//		player.getProperty().changeMoney(-gold,0);
		
		player.getProperty().changeMoney("gold", -gold, 0, player);
		
		jingjibuyCount = jingjibuyCount+1;
		
		this.jingjiItems.clear();
		
		player.sendMessage("jingjiShopMsg",toJingjiJson());
	}
	
	private JsonNode toJingjiJson() {
		ObjectNode wholeJson = jackson.createObjectNode();
		wholeJson.put("jingjibuyCount", this.jingjibuyCount); // 刷新次数
		
		ArrayNode itemsArr = DynamicJsonProperty.jackson.createArrayNode();
		for (Integer key : jingjiItems.keySet()) {
			itemsArr.add(key.intValue());
		}
		wholeJson.put("jingjiItems", itemsArr);
		
		Map<Integer,Shop> goldShopItems = AllGameConfig.getInstance().items.getJingjiShopItemsList();
		
		ArrayNode shopItemLists = DynamicJsonProperty.jackson.createArrayNode();
		for(Shop shop : goldShopItems.values())
		{
			ObjectNode d = jackson.createObjectNode();
			d.put("shopType", shop.getShopType());
			d.put("onlyid", shop.getOnlyid());
			d.put("id", shop.getId());
			d.put("num", shop.getNum());
			d.put("price", shop.getPrice()[0].toString());
			d.put("order", shop.getOrder());
//			d.put("page", shop.getPage());
			shopItemLists.add(d);
		}
		
		wholeJson.put("jingjiShopItemLists", shopItemLists);
		
		return wholeJson;
	}
	
	public void clearJingjiCounts()
	{
		this.jingjibuyCount = 0;
		this.jingjiItems.clear();
		this.jingjilastOpenTime = TimeUtil.getCurrentTime();
	}
}
