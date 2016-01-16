/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Card {
	/**名称*/
	private String type;
	/**奖励*/
	private VariousItemEntry[] reward;
	/**标记位*/
	private String mark;
	/**渠道号*/
	private String channel;
	/**礼包名字*/
	private String name;
	public String getType(){
		 return this.type;
	}
	public  void setType(String type){
		this.type = type ;
	}
	public VariousItemEntry[] getReward(){
		 return this.reward;
	}
	public  void setReward(VariousItemEntry[] reward){
		this.reward = reward ;
	}
	public String getMark(){
		 return this.mark;
	}
	public  void setMark(String mark){
		this.mark = mark ;
	}
	public String getChannel(){
		 return this.channel;
	}
	public  void setChannel(String channel){
		this.channel = channel ;
	}
	public String getName(){
		 return this.name;
	}
	public  void setName(String name){
		this.name = name ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
