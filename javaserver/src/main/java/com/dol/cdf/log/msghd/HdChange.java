package com.dol.cdf.log.msghd;

public class HdChange extends HdBaseLog{

	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int ninjaId;
	private int ninjutsuID1;//进行替换的忍术ID
	private int ninjutsuID2;//被替换下的忍术ID
	
	
	public HdChange(String serverId, String accountId, String roleId,
			int level, int ninjaId, int ninjutsuID1, int ninjutsuID2) {
		super();
		this.type = "change";
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.ninjaId = ninjaId;
		this.ninjutsuID1 = ninjutsuID1;
		this.ninjutsuID2 = ninjutsuID2;
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
		sb.append(ninjutsuID1);
		sb.append(",");
		sb.append(ninjutsuID2);
		
		return sb.toString();
		
	}
	
	
	
}
