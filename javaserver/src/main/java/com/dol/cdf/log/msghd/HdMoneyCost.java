package com.dol.cdf.log.msghd;


/**
 * 货币消耗
 */
public class HdMoneyCost extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int cause;//消耗方式（1、装备强化；2、资质修炼；3.。。。）
	private int count;	//获得数量
	private int total;	//当前总量
	private String moneyType;//类型（1、经验 2、元宝 3、铜钱。。。）
	

	public HdMoneyCost(String channelId) {
		super();
		this.type = "moneycost";
		this.channelId = channelId;
	}
	
	public HdMoneyCost(String serverId, String channelId, String accountId, String roleId, int level, int cause, int count, int total, String moneyType) {
		super();
		this.type = "moneycost";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.cause = cause;
		this.count = count;
		this.total = total;
		this.moneyType = moneyType;
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
				cause + "," +
				count + "," +
				total + "," +
				moneyType;
	}
	
}
