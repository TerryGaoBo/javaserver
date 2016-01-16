

package com.dol.cdf.log.msg;
import com.dol.cdf.log.LogConst;

public class FlashUILogMessage extends BaseLogMessage
{
	

	public FlashUILogMessage (String accountId, String charId, String charName, int nodeId)
	{
		super();
		this.log_type = LogConst.LOG_LOADING;
		this.account_id = accountId;
		this.char_id = charId;
		this.char_name = charName;
		this.reason = nodeId;
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
				+ "&"	;
	}
}
