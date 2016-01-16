

package com.dol.cdf.log.msg;
import io.nadron.app.Player;

import com.dol.cdf.log.LogConst;

public class ItemChangeLogMessage extends BaseLogMessage
{
	public int item_index;
	public int item_tmpl_id;
	public String item_inst_id;
	public int count_delta;
	public int count_stack;
	

	public ItemChangeLogMessage (Player player)
	{
		super(player);
		this.log_type = LogConst.LOG_ITEM;
	}
	
	
	public int getItem_index()
	{
		return item_index;
	}
		
	public void setItem_index(int item_index)
	{
		this.item_index = item_index;
	}
	public int getItem_tmpl_id()
	{
		return item_tmpl_id;
	}
		
	public void setItem_tmpl_id(int item_tmpl_id)
	{
		this.item_tmpl_id = item_tmpl_id;
	}
	public String getItem_inst_id()
	{
		return item_inst_id;
	}
		
	public void setItem_inst_id(String item_inst_id)
	{
		this.item_inst_id = item_inst_id;
	}
	public int getCount_delta()
	{
		return count_delta;
	}
		
	public void setCount_delta(int count_delta)
	{
		this.count_delta = count_delta;
	}
	public int getCount_stack()
	{
		return count_stack;
	}
		
	public void setCount_stack(int count_stack)
	{
		this.count_stack = count_stack;
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
				+ "&item_index=" + this.item_index
				+ "&item_tmpl_id=" + this.item_tmpl_id
				+ "&item_inst_id=" + this.item_inst_id
				+ "&count_delta=" + this.count_delta
				+ "&count_stack=" + this.count_stack
				+ "&"	;
	}
}
