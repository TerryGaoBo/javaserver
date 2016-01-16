package com.dol.cdf.log.msghd;


/**
 * 上忍考试
 */
public class HdNinjaTest extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int floor;
	private int ismoney;//是否消耗金币（0、否；1、是）

	public HdNinjaTest(String channelId) {
		super();
		this.type = "test";
		this.channelId = channelId;
	}
	
	public HdNinjaTest(String serverId, String channelId, String accountId, String roleId, int level,int floor, int ismoney) {
		super();
		this.type = "test";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.floor = floor;
		this.ismoney = ismoney;
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
				floor + "," +
				ismoney;
	}
	
}
