

package com.dol.cdf.log.msg;
import io.nadron.app.Player;

import com.dol.cdf.log.LogConst;

public class PaymentLogMessage extends BaseLogMessage
{
	public String oid;
	public int gold;
	public String rmb;
	public long finishTime;
	

	public PaymentLogMessage (Player player)
	{
		super(player);
		this.log_type = LogConst.LOG_PAYMENT;
	}
	
	
	public String getOid()
	{
		return oid;
	}
		
	public void setOid(String oid)
	{
		this.oid = oid;
	}
	public int getGold()
	{
		return gold;
	}
		
	public void setGold(int silver)
	{
		this.gold = silver;
	}
	public String getRmb()
	{
		return rmb;
	}
		
	public void setRmb(String rmb)
	{
		this.rmb = rmb;
	}
	public long getFinishTime()
	{
		return finishTime;
	}
		
	public void setFinishTime(long finishTime)
	{
		this.finishTime = finishTime;
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
				+ "&oid=" + this.oid
				+ "&gold=" + this.gold
				+ "&rmb=" + this.rmb
				+ "&finishTime=" + this.finishTime
				+ "&"	;
	}
}
