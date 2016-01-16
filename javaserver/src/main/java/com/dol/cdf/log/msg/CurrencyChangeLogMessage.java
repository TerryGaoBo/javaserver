

package com.dol.cdf.log.msg;
import io.nadron.app.Player;

import com.dol.cdf.log.LogConst;

public class CurrencyChangeLogMessage extends BaseLogMessage
{
	public int beforeAmount;
	public int afterAmount;
	public int amount;
	

	public CurrencyChangeLogMessage (Player player)
	{
		super(player);
		this.log_type = LogConst.LOG_MONEY;
	}
	
	
	public int getBeforeAmount()
	{
		return beforeAmount;
	}
		
	public void setBeforeAmount(int beforeAmount)
	{
		this.beforeAmount = beforeAmount;
	}
	public int getAfterAmount()
	{
		return afterAmount;
	}
		
	public void setAfterAmount(int afterAmount)
	{
		this.afterAmount = afterAmount;
	}
	public int getAmount()
	{
		return amount;
	}
		
	public void setAmount(int amount)
	{
		this.amount = amount;
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
				+ "&beforeAmount=" + this.beforeAmount
				+ "&afterAmount=" + this.afterAmount
				+ "&amount=" + this.amount
				+ "&"	;
	}
}
