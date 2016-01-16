package com.dol.cdf.log.msghd;


/**
 * 注入尾兽之力
 */
public class HdBeast extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int ninjaId;
	private int count;//尾数（一尾、二尾、….九尾）
	private int beastId;//注入尾兽之力得到的忍术id

	public HdBeast(String channelId) {
		super();
		this.type = "beast";
		this.channelId = channelId;
	}
	
	public HdBeast(String serverId, String channelId, String accountId, String roleId, int level,int ninjaId, int count,int beastId) {
		super();
		this.type = "beast";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.ninjaId = ninjaId;
		this.count = count;
		this.beastId = beastId;
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
				count + "," +
				beastId;
	}
	
}
