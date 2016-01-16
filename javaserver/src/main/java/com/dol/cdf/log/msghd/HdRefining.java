package com.dol.cdf.log.msghd;
/**
 * 装备精练
 * @author wubin
 *
 */
public class HdRefining extends HdBaseLog{
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int equipId;
	private int ninjutsuId;
	
	
	public HdRefining(String serverId, String accountId, String roleId,
			int level, int equipId, int ninjutsuId) {
		super();
		this.type = "refining";
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.equipId = equipId;
		this.ninjutsuId = ninjutsuId;
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
		sb.append(level);
		sb.append(",");
		sb.append(equipId);
		sb.append(",");
		sb.append(ninjutsuId);
		return sb.toString();
	}
	
	
	
	
}
