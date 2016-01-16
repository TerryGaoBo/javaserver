package com.jelly.player;

//	关卡类型
public enum AdventureType {
	COMMON {
		@Override
		public String getName() {
			return "common";
		}
	},	//	普通关卡
	ELITE {
		@Override
		public String getName() {
			return "elite";
		}
	}	//	精英关卡
	;
	
	public abstract String getName();
}
