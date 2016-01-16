package com.jelly.hero;

import com.jelly.player.ItemInstance;


public class ItemFactory {
	
	
	
//	/**
//	 * 生成测试数据
//	 * @param inventory
//	 */
//	private static void initTestData(Inventory inventory) {
////		List<ItemInstance> equips = inventory.getContainerById(ItemConstant.CON_EQUIP);
////		equips.set(0, createItemInstance(ItemConstant.CON_EQUIP, 1, 1, -1));
////		List<ItemInstance> items = inventory.getContainerById(ItemConstant.CON_ITEM);
////		items.set(0, createItemInstance(ItemConstant.CON_ITEM, 20001, 1, -1));
////		List<ItemInstance> gems = inventory.getContainerById(ItemConstant.CON_GEM);
////		gems.set(0, createItemInstance(ItemConstant.CON_GEM, 10000, 1, -1));
////		List<ItemInstance> mats = inventory.getContainerById(ItemConstant.CON_MAT);
////		mats.set(0, createItemInstance(ItemConstant.CON_MAT, 30001, 1, -1));
//		List<ItemInstance> weap = inventory.getContainerById(ItemConstant.CON_WEAP);
//		weap.set(0, createItemInstance(ItemConstant.CON_WEAP, 1, 1, -1));
//	}
//	
	public static ItemInstance createItemInstance(int itemId, int count) {
		ItemInstance item = new ItemInstance();
		item.setItemId(itemId);
		item.setStackCount(count);
		return item;
	}
	
	
	

}
