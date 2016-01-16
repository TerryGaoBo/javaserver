package com.dol.cdf.log.msghd;


/**
 * 升级
 */
public class HdLevelup extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private String roleName;
	private int level2;	//当前等级
	private int level1;	//升级前等级
	private int levelCost;//升级耗时

	public HdLevelup(String channelId) {
		super();
		this.type = "levelup";
		this.channelId = channelId;
	}
	
	public HdLevelup(String serverId, String channelId, String accountId, String roleId, String roleName, int level2, int level1, int levelCost) {
		super();
		this.type = "levelup";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.roleName = roleName;
		this.level2 = level2;
		this.level1 = level1;
		this.levelCost = levelCost;
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
				roleName + "," +
				level2 + "," +
				level1 + "," +
				(levelCost == 0 ? "" : levelCost);
	}
	
}
