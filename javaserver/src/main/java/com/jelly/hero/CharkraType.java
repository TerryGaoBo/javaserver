package com.jelly.hero;

public class CharkraType {

	public static int CHARKRA_TYPE_NONE = 0;
	
	public static int CHARKRA_TYPE_FIRE = 1;
	
	public static int CHARKRA_TYPE_WIND = 2;
	
	public static int CHARKRA_TYPE_THUNDER = 3;
	
	public static int CHARKRA_TYPE_LAND = 4;
	
	public static int CHARKRA_TYPE_WATER = 5;

	public static boolean isRestrain(int first, int second){
		if(first==0 || second==0)return false;
		
		if( (first + 1) == second){
			return true;
		}
		if((first+1)>CHARKRA_TYPE_WATER){
			if(second==CHARKRA_TYPE_FIRE)
				return true;
		}
		return false;
	}
	
	public static boolean isBeRestrain(int first, int second){
		return isRestrain( second, first);
	}
}
