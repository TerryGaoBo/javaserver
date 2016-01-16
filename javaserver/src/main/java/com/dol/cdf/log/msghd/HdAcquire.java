package com.dol.cdf.log.msghd;

/**
 * 货币或经验获得
 */
public class HdAcquire extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int cause;//获得方式（1、充值；2、任务A；3.赠送。。）
	private int count;	//获得数量
	private int total;	//当前总量
	private String moneyType;//类型（1、经验 2、元宝 3、铜钱。。。）
	

	public HdAcquire(String channelId) {
		super();
		this.type = "acquire";
		this.channelId = channelId;
	}
	
	public HdAcquire(String serverId, String channelId, String accountId, String roleId, int level, int cause, int count, int total, String moneyType) {
		super();
		this.type = "acquire";
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
