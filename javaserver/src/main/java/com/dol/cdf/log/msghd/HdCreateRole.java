package com.dol.cdf.log.msghd;

/**
 * 创建角色
 */
public class HdCreateRole extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private String roleName;
	private String idfa;
	private String dev;

	public HdCreateRole(String channelId) {
		super();
		this.type = "rolebuild";
		this.channelId = channelId;
	}
	
	public HdCreateRole(String serverId, String channelId, String accountId, String roleId, String roleName, String idfa, String dev) {
		super();
		this.type = "rolebuild";
		this.channelId = channelId;
		this.serverId = serverId;
		this.channelId = channelId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.roleName = roleName;
		this.idfa = idfa;
		this.dev = dev;
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
		sb.append(roleId);
		sb.append(",");
		sb.append(roleName);
		if (idfa != null) {
			sb.append(",");
			sb.append(idfa);
		}
		if (dev != null) {
			sb.append(",");
			sb.append(dev);
		}
		return sb.toString();
	}
	
}
