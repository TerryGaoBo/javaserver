package com.dol.cdf.log.msghd;

public class HdNinja extends HdBaseLog{
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int ninjaId;
	private int catchType;//抓取方式（9登录奖励）
	
	public HdNinja(String serverId, String accountId, String roleId, int level,
			int ninjaId, int catchType) {
		super();
		this.type = "ninja";
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.ninjaId = ninjaId;
		this.catchType = catchType;
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
		sb.append(accountId);
		sb.append(",");
		sb.append(roleId);
		sb.append(",");
		sb.append(level);
		sb.append(",");
		sb.append(ninjaId);
		sb.append(",");
		sb.append(catchType);
		
		return sb.toString();
	}
	
	
	
}
