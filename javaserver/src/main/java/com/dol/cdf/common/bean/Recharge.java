/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Recharge {
	/**商品id*/
	private String id;
	/**充值金额*/
	private Integer rmb;
	/**充值金币*/
	private Integer gold;
	/**首冲赠送金额*/
	private Integer fistGive;
	/**普充赠送金额*/
	private Integer give;
	/**每日赠送金额*/
	private Integer dayGive;
	/**持续赠送天数*/
	private Integer days;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getRmb(){
		 return this.rmb;
	}
	public  void setRmb(Integer rmb){
		this.rmb = rmb ;
	}
	public Integer getGold(){
		 return this.gold;
	}
	public  void setGold(Integer gold){
		this.gold = gold ;
	}
	public Integer getFistGive(){
		 return this.fistGive;
	}
	public  void setFistGive(Integer fistGive){
		this.fistGive = fistGive ;
	}
	public Integer getGive(){
		 return this.give;
	}
	public  void setGive(Integer give){
		this.give = give ;
	}
	public Integer getDayGive() {
		return dayGive;
	}
	public void setDayGive(Integer dayGive) {
		this.dayGive = dayGive;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
