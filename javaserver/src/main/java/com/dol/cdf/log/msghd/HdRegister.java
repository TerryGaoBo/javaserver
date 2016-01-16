package com.dol.cdf.log.msghd;


/**
 * 注册
 */
public class HdRegister extends HdBaseLog {
	
	private String deviceId;	//设备唯一标识
	private String accountId;
	private String accName;
	private String deviceModel;//设备机型
	private String deviceSystem;//设备系统
	private String network;//上网类型

	public HdRegister(String channelId) {
		super();
		this.type = "register";
		this.channelId = channelId;
	}
	
	public HdRegister( String channelId, String deviceId, String accountId, String accName, String deviceModel, String deviceSystem, String network) {
		super();
		this.type = "register";
		this.channelId = channelId;
		this.deviceId = deviceId;
		this.accountId = accountId;
		this.accName = accName;
		this.deviceModel = deviceModel;
		this.deviceSystem = deviceSystem;
		this.network = network;
	}
	
	@Override
	public String toString() {
		return getTime() + "," + 
				getAppkey() + "," +
				version + "," +
				type + "," +
				getChannelId() + "," +
				deviceId + "," +
				accountId+ "," +
				accName + "," +
				deviceModel + "," +
				deviceSystem + "," +
				network;
	}
	
}
