package com.dol.cdf.log.msghd;


/**
 * 泡温泉
 */
public class HdSpa extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int ninjaId;
	private int spaType;//温泉类型（1、初级温泉；2、高级温泉）

	public HdSpa(String channelId) {
		super();
		this.type = "spa";
		this.channelId = channelId;
	}
	
	public HdSpa(String serverId, String channelId, String accountId, String roleId, int level,int ninjaId, int spaType) {
		super();
		this.type = "spa";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.ninjaId = ninjaId;
		this.spaType = spaType;
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
				ninjaId + "," +
				spaType;
	}
	
}
