package com.dol.cdf.log.msghd;


/**
 * 失去物品
 */
public class HdRemoveItem extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private String itemName;	//物品名称（某道具，某卡牌，某碎片。。。）
	private int cause;	//失去原因（1、装备强化；2、衣物强化；3、。。。。。）
	private int count;	//失去数量

	public HdRemoveItem(String channelId) {
		super();
		this.type = "removeitem";
		this.channelId = channelId;
	}
	
	public HdRemoveItem(String serverId, String channelId, String accountId, String roleId, int level, String itemName, int cause ,int count) {
		super();
		this.type = "removeitem";
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
