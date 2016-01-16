package com.dol.cdf.log.msghd;


/**
 * 抓忍者
 */
public class HdCatch extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int ninjaId;
	private int catchType;//抓取方式（1、初级抓取；2、高级抓取；3.特技抓取）
	private int isMoney;//是否消耗金币（0：否；1：是）

	public HdCatch(String channelId) {
		super();
		this.type = "catch";
		this.channelId = channelId;
	}
	
	public HdCatch(String serverId, String channelId, String accountId, String roleId, int level,int ninjaId, int catchType, int isMoney) {
		super();
		this.type = "catch";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.ninjaId = ninjaId;
		this.catchType = catchType;
		this.isMoney = isMoney;
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
				catchType + "," +
				isMoney + "," +
				ninjaId;
	}
	
}
