package com.jelly.activity;

import io.nadron.app.Player;

import java.util.Map;
import java.util.Set;

import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jelly.player.PlayerProperty;

public enum GiftType {
	NONE(0){
		@Override
		public void reset(Player player) {
		}

		@Override
		public int check(Player player, int id, int value) {
			return MessageCode.FAIL;
		}

		@Override
		public void addValue(Player player, int value) {
		}
		
	},
	LV_GIFT(1) {//等级礼包
		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getLevel() >= value) {
				return MessageCode.OK;
			}
			return MessageCode.LEVEL_NOT_ENOUGH;
		}
		@Override
		public void reset(Player player) {
			
		}
		@Override
		public void addValue(Player player, int value) {
			
		}
	},
	FUND(2) {//基金
		@Override
		public void reset(Player player) {
			
		}

		@Override
		public int check(Player player, int id, int value) {
			if(!player.getProperty().containStatus(PlayerProperty.BUY_FUND)) {
				return MessageCode.FAIL;
			}
			if(player.getProperty().getLevel() < value) {
				return MessageCode.LEVEL_NOT_ENOUGH;
			}
			return MessageCode.OK;
		}
		@Override
		public void addValue(Player player, int value) {
			
		}
	},
	FIRST_FLUSH(3){//首冲反三倍
		@Override
		public void reset(Player player) {
			
		}

		@Override
		public int check(Player player, int id, int value) {
			return MessageCode.FAIL;
			
		}
		@Override
		public void addValue(Player player, int value) {
			
		}
		
	},
	RECHARGE_1DAY(4){//单日累计充值

		@Override
		public void reset(Player player) {
			for(Integer gid : giftIds){
				int lastGiftTime = player.getProperty().getLastGiftTime(gid);
				if(lastGiftTime != 0 && !TimeUtil.isSameDay(lastGiftTime)){
					player.getProperty().resetGiftStatus(gid);
				}
			}
		}

		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getGiftValue(id) >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}
		@Override
		public void addValue(Player player, int value) {
			this.reset(player);
			for(Integer gid :giftIds){
				player.getProperty().addGiftValue(gid, value);
				player.getProperty().setLastGiftTime(gid, TimeUtil.getCurrentTime());
			}
		}
		
	},
	RECHARGE_MAX(5){//单笔充值最大值

		@Override
		public void reset(Player player) {
		}
		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getGiftValue(id) >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}
		@Override
		public void addValue(Player player, int value) {
			for(Integer gid :giftIds){
				if(player.getProperty().getGiftValue(gid) < value) {
					player.getProperty().setGiftValue(gid, value);
					player.getProperty().setLastGiftTime(gid, TimeUtil.getCurrentTime());
				}
			}
		}
		
		
	},
	RECHARGE_DAY_BY_DAY(6){//连续充值

		@Override
		public void reset(Player player) {
			
		}

		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getGiftValue(id) >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}
		@Override
		public void addValue(Player player, int value) {
			for(Integer gid :giftIds){
				int time = player.getProperty().getLastGiftTime(gid);
				if(time == 0) {
					player.getProperty().setGiftValue(gid, 1);
					player.getProperty().setLastGiftTime(gid, TimeUtil.getCurrentTime());
				}else if(!TimeUtil.isSameDay(time)){ 
					player.getProperty().addGiftValue(gid, 1);
					player.getProperty().setLastGiftTime(gid, TimeUtil.getCurrentTime());
				}
				
			}
		}
		
	},
	RECHARGE_FIRST(7){//首次充值任意金额

		@Override
		public void reset(Player player) {
			
		}
		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getGiftValue(id) >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}

		@Override
		public void addValue(Player player, int value) {
			for(Integer gid :giftIds){
				player.getProperty().setGiftValue(gid, value);
			}
		}
		
	},
	CONSUME_1DAY(8){//当日累计消耗

		@Override
		public void reset(Player player) {
			for(Integer gid : giftIds){
				int lastGiftTime = player.getProperty().getLastGiftTime(gid);
				if(lastGiftTime != 0 && !TimeUtil.isSameDay(lastGiftTime)){
					player.getProperty().resetGiftStatus(gid);
				}
			}
		}

		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getGiftValue(id) >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}
		
		@Override
		public void addValue(Player player, int value) {
			this.reset(player);
			for(Integer gid :giftIds){
				player.getProperty().addGiftValue(gid, value);
				player.getProperty().setLastGiftTime(gid, TimeUtil.getCurrentTime());
			}
		}
		
	},
	COME_DAY(9){//连续登陆

		@Override
		public void reset(Player player) {
			for(Integer gid : giftIds){
				int lastGiftTime = player.getProperty().getLastGiftTime(gid);
				if(lastGiftTime == 0){
					player.getProperty().setGiftValue(gid, 1);
					player.getProperty().setLastGiftTime(gid, TimeUtil.getCurrentTime());
				}else if(!TimeUtil.isSameDay(lastGiftTime)) {
					player.getProperty().addGiftValue(gid, 1);
					player.getProperty().setLastGiftTime(gid, TimeUtil.getCurrentTime());
				}
			}
		}

		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getGiftValue(id) >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}
		
		@Override
		public void addValue(Player player, int value) {
		}
		
	},
	RECHARGE_N_DAYS(10){//连续累计充值

		@Override
		public void reset(Player player) {
		}

		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getGiftValue(id) >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}
		@Override
		public void addValue(Player player, int value) {
			for(Integer gid :giftIds){
				player.getProperty().addGiftValue(gid, value);
			}
		}
		
	},
	VIP_PACKAGE(11){

		@Override
		public void reset(Player player) {
			
		}

		@Override
		public int check(Player player, int id, int value) {
			if(player.getProperty().getVipLv() >= value){
				return MessageCode.OK;
			}
			return MessageCode.FAIL;
		}

		@Override
		public void addValue(Player player, int value) {
			
			
		}
		
	}
	;
	Set<Integer> giftIds = Sets.newHashSet();
	int type;
	
	private GiftType(int type) {
		this.type =  type;
	}
	
	public void addGiftId(int id){
		giftIds.add(id);
	}
	static Map<Integer, GiftType> giftTypeMap = Maps.newHashMap();

	static {
		for (GiftType activityType : GiftType.values()) {
			giftTypeMap.put(activityType.type, activityType);
		}
	}
	
	public static void resetAllTypes() {
		GiftType[] gts = GiftType.values();
		for (GiftType giftType : gts) {
			giftType.giftIds.clear();
		}
	}
	
	public static GiftType getGiftType(int type) {
		return giftTypeMap.get(type);
	}
	
	public abstract void reset(Player player);
	public abstract int check(Player player, int id, int value);
	public abstract void addValue(Player player,int value);
}
