package com.dol.cdf.common.config;

import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.constant.GameConstId;

public class ItemConstant {
	
	/** 装备栏初始格数 */
	public static int EQUIP_INIT_SLOT = 0;
	
	/** 道具栏初始格数 */
	public static int ITEM_INIT_SLOT = 0;
	
	/** 材料栏初始格数 */
	public static int MAT_INIT_SLOT = 0;
	
	/** 装备栏最大格数 */
	public static int EQUIP_MAX_SLOT = 0;
	
	/** 道具栏最大格数 */
	public static int ITEM_MAX_SLOT = 0;
	
	/** 材料栏最大格数 */
	public static int MAT_MAX_SLOT = 0;
	
	
	/** 装备栏底价 */
	public static int EQUIP_INIT_PRICE = 0;
	
	/** 道具栏底价 */
	public static int ITEM_INIT_PRICE = 0;
	
	/** 材料栏底价 */
	public static int MAT_INIT_PRICE = 0;
	
	
	/** 装备栏差价 */
	public static int EQUIP_ADD_PRICE = 0;
	
	/** 道具栏差价 */
	public static int ITEM_ADD_PRICE = 0;
	
	/** 材料栏差价 */
	public static int MAT_ADD_PRICE = 0;
	
	
	/** 人物装备栏containerId */
	public static final short CON_CHAR = 0;
	/** 背包装备栏containerId */
	public static final short CON_EQUIP = 1;
	/** 背包道具栏containerId */
	public static final short CON_ITEM = 2;
	/** 背包材料栏containerId */
	public static final short CON_MAT = 3;
	
	public static final int[] BAG_FULL_CODE = {-1,MessageCode.EQUIP_FULL,MessageCode.ITEM_FULL,MessageCode.MAT_FULL};
	public static final int[] BAG_LACK_CODE = {-1,MessageCode.EQUIP_LACK,MessageCode.ITEM_LACK,MessageCode.MAT_LACK};
	/**
	 * 攻击0
	   防御1
       血量2
       敏捷3
	 */
	public static final int[][] ACCESSORY_PROPS = {
			{0,3,2,1},
			{1,2,3,0},
			{2,0,1,3},
			{3,1,0,2}
			
	};
	
	public static final int WASH_STONE_ID = 6011;
	/**
	 * 背包栏初始、最大格数，开启底价、差价
	 */
	public static void loadBagConfig() {
		int[] equip = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.BAG_EQP);
		EQUIP_MAX_SLOT = equip[0];
		EQUIP_INIT_SLOT = equip[1];
		EQUIP_INIT_PRICE = equip[2];
		EQUIP_ADD_PRICE = equip[3];
		int[] item = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.BAG_ITEM);
		ITEM_MAX_SLOT = item[0];
		ITEM_INIT_SLOT = item[1];
		ITEM_INIT_PRICE = item[2];
		ITEM_ADD_PRICE = item[3];
		int[] mat = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.BAG_MATERIAL);
		MAT_MAX_SLOT = mat[0];
		MAT_INIT_SLOT = mat[1];
		MAT_INIT_PRICE = mat[2];
		MAT_ADD_PRICE = mat[3];
	}
	
	
	public static int getInitSlot(int containerId) {
		switch (containerId) {
		case ItemConstant.CON_EQUIP:
			return EQUIP_INIT_SLOT;
		case ItemConstant.CON_ITEM:
			return ITEM_INIT_SLOT;
		case ItemConstant.CON_MAT:
			return MAT_INIT_SLOT;
		default:
			return -1;
		}
	}
	
	public static int getMaxSlot(int containerId) {
		switch (containerId) {
		case ItemConstant.CON_EQUIP:
			return EQUIP_MAX_SLOT;
		case ItemConstant.CON_ITEM:
			return ITEM_MAX_SLOT;
		case ItemConstant.CON_MAT:
			return MAT_MAX_SLOT;
		default:
			return -1;
		}
	}
	
	public static int getInitPrice(int containerId) {
		switch (containerId) {
		case ItemConstant.CON_EQUIP:
			return EQUIP_INIT_PRICE;
		case ItemConstant.CON_ITEM:
			return ITEM_INIT_PRICE;
		case ItemConstant.CON_MAT:
			return MAT_INIT_PRICE;
		default:
			return 0;
		}
	}
	
	public static int getAddPrice(int containerId) {
		switch (containerId) {
		case ItemConstant.CON_EQUIP:
			return EQUIP_ADD_PRICE;
		case ItemConstant.CON_ITEM:
			return ITEM_ADD_PRICE;
		case ItemConstant.CON_MAT:
			return MAT_ADD_PRICE;
		default:
			return 0;
		}
	}
	
}
