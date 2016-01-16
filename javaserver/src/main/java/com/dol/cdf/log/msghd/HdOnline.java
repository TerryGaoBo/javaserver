package com.dol.cdf.log.msghd;


/**
 * 在线
 */
public class HdOnline extends HdBaseLog {
	
	private String serverId;
	private int count;

	public HdOnline() {
		super();
		this.type = "online";
		this.channelId = "";
	}
	
	public HdOnline(String serverId, int count) {
		super();
		this.type = "online";
		this.channelId = "";
		this.serverId = serverId;
		this.count = count;
	}
	
	@Override
	public String toString() {
		return getTime() + "," + 
				getAppkey() + "," +
				version + "," +
				type + "," +
				serverId + "," +
				count;
	}
	
}
