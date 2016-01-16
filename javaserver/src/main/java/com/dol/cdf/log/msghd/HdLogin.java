package com.dol.cdf.log.msghd;

/**
 * 登录
 */
public class HdLogin extends HdBaseLog {

	private String serverId;
	private String accountId;
	private String accName;
	private String roleId;
	private String roleName;
	private int level;
	private String IP;

	public HdLogin(String channelId) {
		super();
		this.type = "login";
		this.channelId = channelId;
	}

	public HdLogin(String serverId, String channelId, String accountId,
			String accName, String roleId, String roleName, int level,String IP) {
		super();
		this.type = "login";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accName = accName;
		this.accountId = accountId;
		this.roleId = roleId;
		this.roleName = roleName;
		this.level = level;
		this.IP = IP;
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
		sb.append(",");
		sb.append(IP);
		return sb.toString();
	}

}
