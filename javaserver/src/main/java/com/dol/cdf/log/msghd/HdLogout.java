package com.dol.cdf.log.msghd;

/**
 * 退出
 */
public class HdLogout extends HdBaseLog {

	private String serverId;
	private String accountId;
	private String accName;
	private String roleId;
	private String roleName;
	private int level;

	public HdLogout(String channelId) {
		super();
		this.type = "logout";
		this.channelId = channelId;
	}

	public HdLogout(String serverId, String channelId, String accountId,
			String accName, String roleId, String roleName, int level) {
		super();
		this.type = "logout";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.accName = accName;
		this.roleId = roleId;
		this.roleName = roleName;
		this.level = level;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTime());
		sb.append(",");
		sb.append(getAppkey());
		sb.append(",");
		sb.append(version);
		sb.append(",");
		sb.append(type);
		sb.append(",");
		sb.append(serverId);
		sb.append(",");
		sb.append(getChannelId());
		sb.append(",");
		sb.append(accountId);
		sb.append(",");
		sb.append(accName);
		sb.append(",");
		sb.append(roleId);
		sb.append(",");
		sb.append(roleName);
		sb.append(",");
		sb.append(level);
		return sb.toString();
	}

}
