package com.dol.cdf.log.msghd;


/**
 * 充值
 */
public class HdRecharge extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private String amount;	//充值额度 rmb
	private String rechargeChannel;	//充值渠道（1、支付宝 2、爱贝 。。。）
	private String payType;	//充值类型（1、游戏内充值 2、网页充值）

	public HdRecharge(String channelId) {
		super();
		this.type = "recharge";
		this.channelId = channelId;
	}
	
	public HdRecharge(String serverId, String channelId, String accountId, String roleId, int level, String amount, String rechargeChannel, String payType) {
		super();
		this.type = "recharge";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.amount = amount;
		this.rechargeChannel = rechargeChannel;
		this.payType = payType;
	}
	
	@Override
	public String toString() {
		return getTime() + "," + 
				getAppkey() + "," +
				version + "," +
				type + "," +
				serverId + "," +
				getChannelId() + "," +
				accountId+ "," +
				roleId + "," +
				level + "," +
				amount + "," +
				rechargeChannel + "," +
				payType;
	}
	
}
