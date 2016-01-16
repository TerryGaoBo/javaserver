package com.dol.cdf.log.msghd;


/**
 * 泡温泉
 */
public class HdSwallow extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int sType;//吞噬类型（1、吞噬；2、灵魂合体；3、血继转移）
	private int ninjiaID;
	private int beNinjiaID;

	public HdSwallow(String channelId) {
		super();
		this.type = "swallow";
		this.channelId = channelId;
	}
	
	public HdSwallow(String serverId, String channelId, String accountId, String roleId, int level,int sType, int ninjiaID, int beNinjiaID) {
		super();
		this.type = "swallow";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.sType = sType;
		this.ninjiaID = ninjiaID;
		this.beNinjiaID = beNinjiaID;
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
				sType + "," +
				ninjiaID + "," +
				beNinjiaID;
	}
	
}
