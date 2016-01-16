

package com.dol.cdf.log.msg;
import io.nadron.app.Player;

import com.dol.cdf.log.LogConst;

public class PlayerLoginLogMessage extends BaseLogMessage
{
	public int new_player;
	public int today_first;
	

	public PlayerLoginLogMessage (Player player)
	{
		super(player);
		this.log_type = LogConst.LOG_LOGIN;
	}
	
	
	public int getNew_player()
	{
		return new_player;
	}
		
	public void setNew_player(int new_player)
	{
		this.new_player = new_player;
	}
	public int getToday_first()
	{
		return today_first;
	}
		
	public void setToday_first(int today_first)
	{
		this.today_first = today_first;
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
				+ "&new_player=" + this.new_player
				+ "&today_first=" + this.today_first
				+ "&"	;
	}
}
