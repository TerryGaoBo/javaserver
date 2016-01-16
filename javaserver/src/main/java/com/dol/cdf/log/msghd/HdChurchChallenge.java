package com.dol.cdf.log.msghd;


/**
 * 隐忍堂挑战
 */
public class HdChurchChallenge extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int result;	//战斗结果（0、失败；1、成功）

	public HdChurchChallenge(String channelId) {
		super();
		this.type = "churchchallenge";
		this.channelId = channelId;
	}
	
	public HdChurchChallenge(String serverId, String channelId, String accountId, String roleId, int level, int result) {
		super();
		this.type = "churchchallenge";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.result = result;
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
				result;
	}
	
}
