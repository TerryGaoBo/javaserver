package com.jelly.player;

import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.ShopItemGroup;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.collect.IntList;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.constant.GameConstId;

public enum ShopType {
	SHOP(1) {
		@Override
		public boolean checkBuy(DefaultPlayer player) {
			
			return true;
		}
		@Override
		public boolean checkRefresh(DefaultPlayer player) {
			return true;
		}
		@Override
		public int getRefreshTimes(DefaultPlayer player) {
			
			return player.getShop().getBuyShopRefreshTimes();
		}
		@Override
		public int getRefreshLastTime(DefaultPlayer player) {
			
			return player.getShop().getLastShopRefreshTime();
		}
		@Override
		public void setRefreshTimes(DefaultPlayer player, int times) {
			
			player.getShop().setBuyShopRefreshTimes(times);
		}
		@Override
		public void setLastRefreshTime(DefaultPlayer player, int time) {
			player.getShop().setLastShopRefreshTime(time);
		}
		@Override
		public VariousItemEntry getRefreshCost(DefaultPlayer player) {
			int cost = IntList.getIntValueLimit(this.getRefreshTimes(player), 
					(int[]) AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_SHOPPING_COST));
			return new VariousItemEntry("silver", cost);
		}
		
		@Override
		public ShopItemGroup randGoods(int idx) {
			return idx < 5 ? AllGameConfig.getInstance().items.randShopGoods() 
					: AllGameConfig.getInstance().items.randMysteryGoods();
		}
		
		@Override
		public void normalRefresh(DefaultPlayer player) {
			player.getShop().normalRefresh(this, player);
		}
	},
	BLACK(2) {
		@Override
		public boolean checkBuy(DefaultPlayer player) {
			if (!player.getProperty().getVipFun(VipConstant.BUG_BLACK_FRESH)) {
				player.sendResult(OperResultType.BUY_SHOP,MessageCode.VIPLEVEL_NOT_ENUGH);
				return false;
			}
			return true;
		}
		
		@Override
		public boolean checkRefresh(DefaultPlayer player) {
			return player.getProperty().getVipFun(VipConstant.BUG_BLACK_FRESH);
		}

		@Override
		public int getRefreshTimes(DefaultPlayer player) {
			
			return player.getShop().getBuyBlackRefreshTimes();
			//return 0;
		}
		@Override
		public int getRefreshLastTime(DefaultPlayer player) {
			
			return player.getShop().getLastBlackRefreshTime();
//			return 0;
		}

		@Override
		public void setRefreshTimes(DefaultPlayer player, int times) {
			
//			player.getProperty().setBuyBlackRefreshTimes(times);
			player.getShop().setBuyBlackRefreshTimes(times);
		}

		@Override
		public void setLastRefreshTime(DefaultPlayer player, int time) {
			
			player.getShop().setLastBlackRefreshTime(time);
		}
		
		@Override
		public VariousItemEntry getRefreshCost(DefaultPlayer player) {
			int cost = IntList.getIntValueLimit(this.getRefreshTimes(player), 
					(int[]) AllGameConfig.getInstance().gconst.getConstant(GameConstId.REFRESH_BLACK_COST));
			return new VariousItemEntry("gold", cost);
		}
		
		@Override
		public ShopItemGroup randGoods(int idx) {
			return idx < 5 ? AllGameConfig.getInstance().items.randBlackGoods() 
					: AllGameConfig.getInstance().items.randBlackMysteryGoods();
		}
		
		@Override
		public void normalRefresh(DefaultPlayer player) {
			player.getShop().normalRefresh(this, player);
		}
	};
	
	public static ShopType parse(int type) {
		for (ShopType e : ShopType.values()) {
			if (e.getType() == type)
				return e;
		}
		return null;
	}
	
	int type;

	private ShopType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public void buyItem(DefaultPlayer player, ShopType type, int idx) {
		player.getShop().buy(type, idx, player);
	}
	
	public void buyRefresh(DefaultPlayer player) {
		player.getShop().refresh(this, player);
	}
	
	//public abstract void buyItem(DefaultPlayer player ,int id, int idx);
	public abstract boolean checkBuy(DefaultPlayer player);
	
	//public abstract void buyRefresh(DefaultPlayer player );
	public abstract boolean checkRefresh(DefaultPlayer player);
	
	public abstract void normalRefresh(DefaultPlayer player);
	
	public abstract int getRefreshTimes(DefaultPlayer player);
	
	public abstract int getRefreshLastTime(DefaultPlayer player);
	
	public abstract void setRefreshTimes(DefaultPlayer player, int times);
	
	public abstract void setLastRefreshTime(DefaultPlayer player, int time);
	
	public abstract VariousItemEntry getRefreshCost(DefaultPlayer player);
	
	public abstract ShopItemGroup randGoods(int idx);
};

