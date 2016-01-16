package com.jelly.combat.context;

public final class CBConst {
	/**
	 * 最大回合数 ；
	 */
	public static int MAX_ROUNT = 100;

	/**
	 * 格子总数
	 */
	public static int GRID_NUMBER = 100;

	/**
	 * 双方的距离格子数
	 */
	public static int BOTH_SIDES_GRID_COUNT = 10;

	public enum GroupDefine {
		attacker, defender;
	}
}
