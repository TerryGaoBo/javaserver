package com.dol.cdf.log.msghd;


/**
 * 获得物品
 */
public class HdGetItem extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private String itemName;	//物品名称（某道具，某卡牌，某碎片。。。）
	private int cause;	//获得原因（1、任务A；2、挑战修炼塔；3、。。。。。）
	private int count;	//获得数量

	public HdGetItem(String channelId) {
		super();
		this.type = "getitem";
		this.channelId = channelId;
	}
	
	public HdGetItem(String serverId, String channelId, String accountId, String roleId, int level, String itemName, int cause ,int count) {
		super();
		this.type = "getitem";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.itemName = itemName;
		this.cause = cause;
		this.count = count;
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
				itemName + "," +
				cause + "," +
				count;
	}
	
}
