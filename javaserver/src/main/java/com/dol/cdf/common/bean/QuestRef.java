/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class QuestRef {
	/**完成数量*/
	private Integer count;
	/**金币奖励*/
	private Integer gold;
	public Integer getCount(){
		 return this.count;
	}
	public  void setCount(Integer count){
		this.count = count ;
	}
	public Integer getGold(){
		 return this.gold;
	}
	public  void setGold(Integer gold){
		this.gold = gold ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
