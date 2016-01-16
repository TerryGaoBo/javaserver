/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Level {
	/**等级*/
	private Integer lv;
	/**人物经验*/
	private Integer exp;
	/**英雄经验*/
	private Integer rxp;
	public Integer getLv(){
		 return this.lv;
	}
	public  void setLv(Integer lv){
		this.lv = lv ;
	}
	public Integer getExp(){
		 return this.exp;
	}
	public  void setExp(Integer exp){
		this.exp = exp ;
	}
	public Integer getRxp(){
		 return this.rxp;
	}
	public  void setRxp(Integer rxp){
		this.rxp = rxp ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
