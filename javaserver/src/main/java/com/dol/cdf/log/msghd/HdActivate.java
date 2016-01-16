package com.dol.cdf.log.msghd;

/**
 * 激活
 */
public class HdActivate extends HdBaseLog {
	
	private String channelId;
	private String deviceId;	//设备唯一标识

	public HdActivate(String channelId) {
		super();
		this.type = "activate";
		this.channelId = channelId;
	}
	
	public HdActivate(String deviceId, String channelId) {
		super();
		this.type = "activate";
		this.channelId = channelId;
		this.deviceId = deviceId;
	}
	
	@Override
	public String toString() {
		return getTime() + "," + 
				getAppkey() + "," +
				version + "," +
				type + "," +
				getChannelId() + "," +
				deviceId;
	}
	
}
