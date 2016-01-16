package com.jelly.game.command;

public final class ChatConstants {


	public static final int SCOPE_WORLD 		= 1;	//	世界频道

	public static final int SCOPE_SYSTEM 		= 2;	//	系统频道
	
	public static final int SCOPE_TEAM 			= 3;	//	军团频道

	public static final int SCOPE_MIDDLE 		= 4;
	
	public static final int SCOPE_ACTIVE	    = 5;	//	活动频道
	
	public static final int CHAT_TIME_INTERVAL 	= 30;

	/**
	 * 用于区分全服公告的不同类型
	 * 
	 * @author shaobo.yang
	 * 
	 */
	public enum NoticeTypeEnum{
		/** 装备 */
		EQUIP(0),
		/** 塔罗 */
		TAROT(1),
		/** 图腾 */
		TOTEM(2),
		/* 纯文本 */
		NOMAL(3),
		/* 神龙祈福公告 */
		DRAGON(4);
		private final int value;
		private NoticeTypeEnum (int value){
			this.value=value;
		}
		public int getValue() {
			return value;
		}
	}
}
