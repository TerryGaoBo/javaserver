/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class DayReward {
	/**day*/
	private Integer day;
	/**item*/
	private VariousItemEntry[] item;
	/**v2*/
	private Integer v2;
	public Integer getDay(){
		 return this.day;
	}
	public  void setDay(Integer day){
		this.day = day ;
	}
	public VariousItemEntry[] getItem(){
		 return this.item;
	}
	public  void setItem(VariousItemEntry[] item){
		this.item = item ;
	}
	public Integer getV2() {
		return v2;
	}
	public void setV2(Integer v2) {
		this.v2 = v2;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
