package com.dol.cdf.common.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.BaseItem;
import com.dol.cdf.common.bean.Item;
import com.dol.cdf.common.bean.ItemGroup;
import com.dol.cdf.common.bean.Material;
import com.dol.cdf.common.bean.Shop;
import com.dol.cdf.common.bean.ShopItemGroup;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

public class ItemConfigManager extends BaseConfigLoadManager {
	
	private static final String JSON_FILE_ACC = "Accessory.json";
	
	private static final String JSON_FILE_ITEM = "Item.json";
	
	private static final String JSON_FILE_MAT = "Material.json";
	
	private static final String JSON_FILE_ITEM_GROUP = "ItemGroup.json";
	
	private static final String JSON_FILE_SHOPITEM_GROUP = "ShopItemGroup.json";
	
	private static final String JSON_FILE_NEW_SHOP_ITEMS = "Shop.json";
	
	/**
	 * 装备
	 */
	private Map<Integer, Accessory> accMap;
	/**
	 * 道具
	 */
	private Map<Integer, Item> itemMap;
	
	
	/**
	 * 材料
	 */
	private Map<Integer, Material> matMap;
	
	
	
	/**
	 * 所有道具的container类型
	 */
	private Map<Integer, Short> itemContainer = new HashMap<Integer, Short>();
	
	
	/**
	 * 道具组
	 */
	private List<ItemGroup> itemGroups;
	
	private Map<Integer, ItemGroup> itemGroupMap;
	
	private ItemGroup currItemGroup; 
	
	/**
	 * 商店组   黑市组
	 */
	private List<ShopItemGroup> shopGroups;
	
	private Map<Integer, ShopItemGroup> allShopGoods, allMysteryGoods;	//	商城商品与商城神秘商品
	
	private int shopGoodsRate, mysteryGoodsRate;	//	商店商品与神秘商品随机权重总和
	
	private Map<Integer, ShopItemGroup> allBlackGoods, allBlackMysteryGoods;	//	黑市商品与黑市神秘商品
	
	private int blackGoodsRate, blackMysteryGoodsRate;	//	黑市商品与黑市神秘随机权重总和
	
	
	/**
	 *  新商店 数据
	 **/
	private List<Shop> newShopItems;
	private Map<Integer,Shop> goldShopItems; // 金币商店
	private Map<Integer, Shop> heishiShopItems;// 黑市
	private Map<Integer, Shop> renjieShopItems;// 忍界商店
	private Map<Integer, Shop> jingjiShopItems;// 竞技场商店
	private Map<Integer, Shop> juntuanShopItems;// 军团商店
	
	
	
	private void loadAccessory() {
		String jsonFile = JSON_FILE_ACC;
		accMap = new HashMap<Integer, Accessory>();
		List<Accessory> list = readConfigFile(jsonFile, new TypeReference<List<Accessory>>() {
		});
		for (Accessory Accessory : list) {
			accMap.put(Accessory.getId(), Accessory);
			itemContainer.put(Accessory.getId(), ItemConstant.CON_EQUIP);
		}
	}
	
	private void loadItemGroup() {
		String jsonFile = JSON_FILE_ITEM_GROUP;
		itemGroupMap = Maps.newHashMap();
		itemGroups = readConfigFile(jsonFile, new TypeReference<List<ItemGroup>>() {
		});
		for (ItemGroup group : itemGroups) {
			itemGroupMap.put(group.getId(), group);
		}
		
		currItemGroup = itemGroups.get(Rnd.get(itemGroups.size()));
		logger.info("curretItemGroup id:{}",currItemGroup.getId());
	}
	
	public void loadShopItemGroup() {
		allShopGoods = Maps.newHashMap();
		shopGoodsRate = 0;
		allMysteryGoods = Maps.newHashMap();
		mysteryGoodsRate = 0;
		allBlackGoods = Maps.newHashMap();
		blackGoodsRate = 0;
		allBlackMysteryGoods = Maps.newHashMap();
		blackMysteryGoodsRate = 0;
		
		String jsonFile = JSON_FILE_SHOPITEM_GROUP;
		shopGroups = readConfigFile(jsonFile, new TypeReference<List<ShopItemGroup>>() {
		});
		for (ShopItemGroup group : shopGroups) {
			if (group.getShopType() == 1){	//	商店商品
				allShopGoods.put(group.getId(), group);
				shopGoodsRate += group.getWeight();
			} else if (group.getShopType() == 2) {	//	黑市商品
				allBlackGoods.put(group.getId(), group);
				blackGoodsRate += group.getWeight();
			} else if (group.getShopType() == 3) {	//	商店神秘商品
				allMysteryGoods.put(group.getId(), group);
				mysteryGoodsRate += group.getWeight();
			} else if (group.getShopType() == 4) {	//	黑市神秘商品
				allBlackMysteryGoods.put(group.getId(), group);
				blackMysteryGoodsRate += group.getWeight();
			}
		}
	}
	private void loadNewShopItemsInfo()
	{
		goldShopItems = Maps.newHashMap();
		heishiShopItems = Maps.newHashMap();
		renjieShopItems = Maps.newHashMap();
		jingjiShopItems = Maps.newHashMap();
		juntuanShopItems = Maps.newHashMap();
		
		String jsonFile = JSON_FILE_NEW_SHOP_ITEMS;
		newShopItems = readConfigFile(jsonFile, new TypeReference<List<Shop>>() {
		});
		
		for(Shop shop : newShopItems){
			if(shop.getShopType() == 1){
				goldShopItems.put(shop.getOnlyid(), shop);
			}else if(shop.getShopType() == 2){
				heishiShopItems.put(shop.getOnlyid(), shop);
			}else if(shop.getShopType() == 3){
				renjieShopItems.put(shop.getOnlyid(), shop);
			}else if(shop.getShopType() == 4){
				jingjiShopItems.put(shop.getOnlyid(), shop);
			}else if(shop.getShopType() == 5){
				juntuanShopItems.put(shop.getOnlyid(), shop);
			}
		}
	}
	
	public Shop getGoldShopByID(Integer idx)
	{
		return goldShopItems.get(idx);
	}
	public Shop getHeishiShopByID(Integer idx)
	{
		return heishiShopItems.get(idx);
	}
	public Shop getRenjieShopByID(Integer idx)
	{
		return renjieShopItems.get(idx);
	}
	public Shop getJingjiShopByID(Integer idx)
	{
		return jingjiShopItems.get(idx);
	}
	public Shop getJuntuanShopByID(Integer idx)
	{
		return juntuanShopItems.get(idx);
	}
	
	public Map<Integer,Shop> getShopItemsList()
	{
		return this.goldShopItems;
	}
	
	public Map<Integer,Shop> getRenJieItemsList()
	{
		return this.renjieShopItems;
	}
	
	public Map<Integer,Shop> getJingjiShopItemsList()
	{
		return this.jingjiShopItems;
	}
	
	
	private ShopItemGroup randGoods(Collection<ShopItemGroup> allGoods, int rate) {
		int end = Rnd.get(rate) - 1, val = 0;
		for (ShopItemGroup goup : allGoods) {
			if ((val += goup.getWeight()) >= end) {
				return goup;
			}
		}
		return null;
	}
	
	//	商店商品
	public ShopItemGroup randShopGoods() {
		return randGoods(allShopGoods.values(), shopGoodsRate);
	}
	
	//	商店神秘商品
	public ShopItemGroup randMysteryGoods() {
		return randGoods(allMysteryGoods.values(), mysteryGoodsRate);
	}
	
	//	黑市商品
	public ShopItemGroup randBlackGoods() {
		return randGoods(allBlackGoods.values(), blackGoodsRate);
	}
	
	//	黑市神秘商品
	public ShopItemGroup randBlackMysteryGoods() {
		return randGoods(allBlackMysteryGoods.values(), blackMysteryGoodsRate);
	}
	
	public VariousItemEntry getGroupItem(int id, int idx) {
		return itemGroupMap.get(id).getItems()[idx];
	}
	
	public int getItemNeedCoin(int id, int idx) {
		return itemGroupMap.get(id).getCoin()[idx];
	}
	
	public ItemGroup getCurrItemGroup() {
		return currItemGroup;
	}
	
//	public void rndCurrItemGroup() {
//		currItemGroup = itemGroups.get(Rnd.get(itemGroups.size()));
//		logger.info("curretItemGroup id:{}",currItemGroup.getId());
//	}
	
	private void loadItem() {
		String jsonFile = JSON_FILE_ITEM;
		itemMap = new HashMap<Integer, Item>();
		List<Item> list = readConfigFile(jsonFile, new TypeReference<List<Item>>() {
		});
		for (Item item : list) {
			itemMap.put(item.getId(), item);
			itemContainer.put(item.getId(), ItemConstant.CON_ITEM);
		}
		
		//兼容variousItem初始化的时候没有道具数据
		list = readConfigFile(jsonFile, new TypeReference<List<Item>>() {
		});
		for (Item item : list) {
			itemMap.put(item.getId(), item);
		}
	}
	

	private void loadMaterial() {
		String jsonFile = JSON_FILE_MAT;
		matMap = new HashMap<Integer, Material>();
		List<Material> list = readConfigFile(jsonFile, new TypeReference<List<Material>>() {
		});
		for (Material mat : list) {
			matMap.put(mat.getId(), mat);
			itemContainer.put(mat.getId(), ItemConstant.CON_MAT);
		}
	}
	
	
	public Accessory getAccessoryById(int id) {
		return accMap.get(id);
	}
	
	public Item getItemById(int id) {
		return itemMap.get(id);
	}
	
	
	public Material getMaterialById(int id) {
		return matMap.get(id);
	}
	
	public Short getItemContainerId(int itemId) {
		Short containerId = itemContainer.get(itemId);
		if (containerId == null) {
			logger.error("道具不存在 itemId=" + itemId);
			return null;
		}
		return containerId;
	}
	
	public BaseItem getBaseItem(int id) {
		Short containerId = itemContainer.get(id);
		if (containerId == null) {
			return null;
		}
		if (containerId == ItemConstant.CON_MAT) {
			return AllGameConfig.getInstance().items.getMaterialById(id);
		} else if (containerId == ItemConstant.CON_ITEM) {
			return AllGameConfig.getInstance().items.getItemById(id);
		} else {
			return AllGameConfig.getInstance().items.getAccessoryById(id);
		}
	}
	
	public String getItemName(int itemId) {
		BaseItem item = getBaseItem(itemId);
		if (item == null) {
			logger.error("道具不存在 itemId=" + itemId);
			return "";
		}
		return item.getAlt();
	}

	public Map<Integer, Accessory> getAccMap() {
		return accMap;
	}

	public Map<Integer, Item> getItemMap() {
		return itemMap;
	}

	public Map<Integer, Material> getMatMap() {
		return matMap;
	}

	@Override
	public void loadConfig() {
		loadAccessory();
		loadMaterial();
		loadItem();
		loadItemGroup();
		loadShopItemGroup();
		loadNewShopItemsInfo();
		ItemConstant.loadBagConfig();
	}
	
	public void loadReloadConfig()
	{
		loadAccessory();
		loadMaterial();
		loadItem();
		loadItemGroup();
//		loadShopItemGroup();
		loadNewShopItemsInfo();
		ItemConstant.loadBagConfig();
	}
	
	public static void main(String[] args) {
		try {
			ItemConfigManager mgr = new ItemConfigManager();
			mgr.loadConfig();
			//	统计刷新商城商品时每件商品出现概率
			int testCnt = 5000;
			Map<Integer, Integer> stats = Maps.newHashMap();
			for (int i = 0; i < testCnt; ++i) {
				ShopItemGroup goup = mgr.randMysteryGoods();
				System.out.println(goup.getMaxprice()[0]);
				stats.put(goup.getId(), stats.containsKey(goup.getId()) ? stats.get(goup.getId()) + 1 : 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
