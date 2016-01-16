

package com.dol.cdf.log.msg;
import io.nadron.app.Player;

import com.dol.cdf.log.LogConst;

public class ItemTradeLogMessage extends BaseLogMessage
{
	public String item_inst_id;
	public int count;
	public int oper;
	public int price;
	public int day;
	public int cost;
	public int costType;
	

	public ItemTradeLogMessage (Player player)
	{
		super(player);
		this.log_type = LogConst.LOG_SHOP;
	}
	
	
	public String getItem_inst_id()
	{
		return item_inst_id;
	}
		
	public void setItem_inst_id(String item_inst_id)
	{
		this.item_inst_id = item_inst_id;
	}
	public int getCount()
	{
		return count;
	}
		
	public void setCount(int count)
	{
		this.count = count;
	}
	public int getOper()
	{
		return oper;
	}
		
	public void setOper(int oper)
	{
		this.oper = oper;
	}
	public int getPrice()
	{
		return price;
	}
		
	public void setPrice(int price)
	{
		this.price = price;
	}
	public int getDay()
	{
		return day;
	}
		
	public void setDay(int day)
	{
		this.day = day;
	}
	public int getCost()
	{
		return cost;
	}
		
	public void setCost(int cost)
	{
		this.cost = cost;
	}
	public int getCostType()
	{
		return costType;
	}
		
	public void setCostType(int costType)
	{
		this.costType = costType;
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
				+ "&item_inst_id=" + this.item_inst_id
				+ "&count=" + this.count
				+ "&oper=" + this.oper
				+ "&price=" + this.price
				+ "&day=" + this.day
				+ "&cost=" + this.cost
				+ "&costType=" + this.costType
				+ "&"	;
	}
}
