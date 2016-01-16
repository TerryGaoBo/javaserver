package com.dol.cdf.log.msghd;

/**
 * 专精点log
 * @author wubin
 *
 */
public class HdPoint extends HdBaseLog{

	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int gettype;//获得方式（1、忍术转化；2、秘籍）
	private int ninjutsuId;//转化为专精点的忍术ID（若秘籍转化为专精点，为0）
	
	public HdPoint(String serverId, String accountId, String roleId, int level,
			int gettype, int ninjutsuId) {
		super();
		this.type = "point";
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.gettype = gettype;
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
		sb.append(accountId);
		sb.append(",");
		sb.append(roleId);
		sb.append(",");
		sb.append(level);
		sb.append(",");
		sb.append(gettype);
		sb.append(",");
		sb.append(ninjutsuId);
		
		return sb.toString();
		
	}
	
	
}
