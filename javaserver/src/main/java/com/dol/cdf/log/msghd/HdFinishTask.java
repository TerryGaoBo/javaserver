package com.dol.cdf.log.msghd;


/**
 * 完成任务
 */
public class HdFinishTask extends HdBaseLog {
	
	private String serverId;
	private String accountId;
	private String roleId;
	private int level;
	private String taskId;	//任务id
	private int result;	//战斗结果（0、失败；1、成功）

	public HdFinishTask(String channelId) {
		super();
		this.type = "finishtask";
		this.channelId = channelId;
	}
	
	public HdFinishTask(String serverId, String channelId, String accountId, String roleId, int level, String taskId, int result) {
		super();
		this.type = "finishtask";
		this.channelId = channelId;
		this.serverId = serverId;
		this.accountId = accountId;
		this.roleId = roleId;
		this.level = level;
		this.taskId = taskId;
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
				taskId + "," +
				result;
	}
	
}
