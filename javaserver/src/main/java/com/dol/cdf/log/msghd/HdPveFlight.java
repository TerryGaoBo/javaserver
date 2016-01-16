package com.dol.cdf.log.msghd;


/**
 * 关卡战斗
 */
public class HdPveFlight extends HdBaseLog {
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private String guanqiaid;//关卡id（1：崆峒派-xxxx；2：中南派。。。。n：修炼之塔），具体到某一个小关卡
	private int ftype;	//战斗类型（1：扫荡；2：挑战；3：。。。）
	private String npcId;	//NPC对应id
	private int result;	//战斗结果（0、失败；1、成功）

	public HdPveFlight(String channelId) {
		super();
		this.type = "PVEfight";
		this.channelId = channelId;
	}
	
	public HdPveFlight(String serverId, String channelId, String accountId, String roleId, int level, String guanqiaid, int ftype, String npcId, int result) {
		super();
		this.type = "PVEfight";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.guanqiaid = guanqiaid;
		this.ftype = ftype;
		this.npcId = npcId;
		this.result = result;
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
				guanqiaid + "," +
				ftype + "," +
				npcId + "," +
				result;
	}
	
}
