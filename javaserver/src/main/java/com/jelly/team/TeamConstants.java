package com.jelly.team;

/**
 * 军团常量类
 **/
public final class TeamConstants {
	
	/**
	 * 以下常量定义了一组对军团成员或数据操作的常量
	 **/
	public static final int TEAM_JOIN_MEMBER = 0; // 加入新成员
	public static final int TEAM_REFUSE_MEMBER = 1; // 拒绝加入请求
	public static final int TEAM_DISMISS_MEMBER = 2; // 开除成员
	public static final int TEAM_MODIFY_MEMBER_TITLE = 3; // 提升或降低成员职务
	public static final int TEAM_DELATE_COMMANDER = 4; // 弹劾军团长
	public static final int TEAM_DISSOLVE = 5; // 解散军团
	public static final int TEAM_MODIFY_COMMANDER = 6; // 委任新军团长
	public static final int TEAM_UPGRADE = 7; // 提升军团阶级
	public static final int TEAM_CREATE = 8; // 创建军团
	public static final int TEAM_CANCEL_ARMY_ROLE = 9; // 撤回派出到部队的忍者
	
	/**
	 *  军团战争
	 */
	public static final int TEAM_WAR_OPEN = 10; // 军团战开启
	public static final int TEAM_WAR_CLOSE = 11; // 军团战结束
	public static final int TEAM_WAR_WIN = 12; // 军团战胜利
	public static final int TEAM_WAR_LOSE = 13; // 军团战失败
	public static final int TEAM_WAR_DRAW = 14; // 军团战平局
	
	public static final int TEAM_WAR_ON_PLAYER_HERO_DIE = 19; // 军团战过程中，对手英雄死亡，更新对手英雄状态
	public static final int TEAM_WAR_END_RANK_MAIL = 20; // 军团战结束发送邮箱
	
	/**
	 * 军团系统邮件标题与内容常量
	 **/
	public final static int MAIL_TITLE_JOIN_MEMBER = 6133;
	public final static int MAIL_CONTENT_JOIN_MEMBER = 6134;	
	
	
	/**
	 *  军团战争中，hero 列表分类
	 */
	public final static int WAR_INDEX_A = 0; // 在忍者列表
	public final static int WAR_INDEX_B = 1; // 在军团部队列表
	
	
	/**
	 *  军团部队中次数的冷却时间
	 */
	public final static long HERO_CD_TIME = 10*3600;
	
	public final static int WAR_HOLD_STATE_0 = 0;// 在我方据点
	public final static int WAR_HOLD_STATE_1 = 0;// 我的英雄在地方据点中
	public final static int WAR_HOLD_STATE = -1;// 不在据点中
	
	public final static int WAR_BASE_STATE_A = 0; // 未被占领
	public final static int WAR_BASE_STATE_B = 1; // 占领
	
}