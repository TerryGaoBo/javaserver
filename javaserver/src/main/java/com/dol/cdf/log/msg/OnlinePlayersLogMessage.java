

package com.dol.cdf.log.msg;
import com.dol.cdf.log.LogConst;

public class OnlinePlayersLogMessage extends BaseLogMessage
{
	public int online_players;
	

	public OnlinePlayersLogMessage ()
	{
		super();
		this.log_type = LogConst.LOG_ONLINE;
	}
	
	
	public int getOnline_players()
	{
		return online_players;
	}
		
	public void setOnline_players(int online_players)
	{
		this.online_players = online_players;
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
				+ "&online_players=" + this.online_players
				+ "&"	;
	}
}
