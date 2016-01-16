package com.dol.cdf.log.msghd;

/**
 * 活动
 */
public class HdActivity extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private String activityId;//活动id

	public HdActivity(String channelId) {
		super();
		this.type = "activity";
		this.channelId = channelId;
	}
	
	public HdActivity(String serverId, String channelId, String accountId, String roleId, int level, String activityId) {
		super();
		this.type = "activity";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.activityId = activityId;
	}
	
	@Override
	public String toString() {
		return getTime() + "," + 
				getAppkey() + "," +
				version + "," +
				type + "," +
				serverId + "," +
				accountId+ "," +
				roleId + "," +
				level + "," +
				activityId;
	}
	
}
