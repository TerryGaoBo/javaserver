package com.dol.cdf.log.msghd;


/**
 * 忍术学校修炼
 */
public class HdPractice extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private int ninjaId;
	private int practiceType;//修炼方式（1、卷轴修炼；2、初级修炼；3.高级修炼）
	private int isMoney;//是否消耗金币（0：否；1：是）
	private int ninjutsuId;//学习到的忍术id

	public HdPractice(String channelId) {
		super();
		this.type = "practice";
		this.channelId = channelId;
	}
	
	public HdPractice(String serverId, String channelId, String accountId, String roleId, int level,int ninjaId, int practiceType, int isMoney,int ninjutsuId) {
		super();
		this.type = "practice";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.ninjaId = ninjaId;
		this.practiceType = practiceType;
		this.isMoney = isMoney;
		this.ninjutsuId = ninjutsuId;
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
				level + "," +
				ninjaId + "," +
				practiceType + "," +
				isMoney + "," +
				ninjutsuId;
	}
	
}
