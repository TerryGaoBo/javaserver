package com.dol.cdf.log.msghd;

/**
 * 装备洗练
 * @author wubin
 *
 */
public class HdEquipLel extends HdBaseLog{
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int equipId;//装备id
	private int equiplel;//第几次洗练
	
	public HdEquipLel(String serverId, String accountId, String roleId,
			int level, int equipId, int equiplel) {
		super();
		this.type="equiplel";
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.equipId = equipId;
		this.equiplel = equiplel;
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
		sb.append(equipId);
		sb.append(",");
		sb.append(equiplel);

		return sb.toString();
		
		
		
		
	}
	
	
	
}
