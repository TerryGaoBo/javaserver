package com.dol.cdf.log.msghd;

/**
 * 通灵学院修炼
 */
public class HdScholopractice extends HdBaseLog {

	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int ninjaId;
	private int practiceType;// 修炼方式（1、初级修炼；2、高级修炼）
	private int isMoney;// 是否消耗金币（0：否；1：是）
	private int scholopracticeId;//学习到的忍术id
	
	public HdScholopractice(String channelId) {
		super();
		this.type = "Scholopractice";
		this.channelId = channelId;
	}

	public HdScholopractice(String serverId, String channelId,
			String accountId, String roleId, int level, int ninjaId,
			int practiceType, int isMoney,int scholopracticeId) {
		super();
		this.type = "Scholopractice";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.ninjaId = ninjaId;
		this.practiceType = practiceType;
		this.isMoney = isMoney;
		this.scholopracticeId = scholopracticeId;
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
		sb.append(practiceType);
		sb.append(",");
		sb.append(isMoney);
		sb.append(",");
		sb.append(scholopracticeId);
		
		return sb.toString();
//		return getTime() + "," + getAppkey() + "," + version + "," + type + ","
//				+ serverId + "," + accountId + "," + roleId + "," + level + ","
//				+ ninjaId + "," + practiceType + "," + isMoney;
	}

}
