

package com.dol.cdf.log.msg;
import io.nadron.app.Player;

import com.dol.cdf.log.LogConst;

public class PlayerTaskLogMessage extends BaseLogMessage
{
	public int task_id;
	public String task_name;
	

	public PlayerTaskLogMessage (Player player)
	{
		super(player);
		this.log_type = LogConst.LOG_TASK;
	}
	
	
	public int getTask_id()
	{
		return task_id;
	}
		
	public void setTask_id(int task_id)
	{
		this.task_id = task_id;
	}
	public String getTask_name()
	{
		return task_name;
	}
		
	public void setTask_name(String task_name)
	{
		this.task_name = task_name;
	}

	
	@Override
	public String toString() {
		return "log_type=" + this.log_type
				+ "&log_time=" + this.log_time
				+ "&region_id=" + this.region_id
				+ "&host_id=" + this.host_id
				+ "&server_id=" + this.server_id
				+ "&account_id=" + this.account_id
				+ "&account_name=" + this.account_name
				+ "&char_id=" + this.char_id
				+ "&char_name=" + this.char_name
				+ "&level=" + this.level
				+ "&reason=" + this.reason
				+ "&param=" + this.param 
				+ "&task_id=" + this.task_id
				+ "&task_name=" + this.task_name
				+ "&"	;
	}
}
