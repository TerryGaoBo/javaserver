/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Vip {
	/**id*/
	private Integer id;
	/**所需Vip积分*/
	private Integer score;
	/**特权描述*/
	private String desc;
	/**一次性奖励*/
	private VariousItemEntry[] reward;
	/**次数*/
	private int[] times;
	/**专享功能*/
	private int[] fun;
	/**专享优惠*/
	private int[] discount;
	/**每日登陆奖励*/
	private VariousItemEntry[] dayprize;
	/**奖励邮件标题*/
	private Integer mailt;
	/**奖励邮件内容*/
	private Integer mailc;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getScore(){
		 return this.score;
	}
	public  void setScore(Integer score){
		this.score = score ;
	}
	public String getDesc(){
		 return this.desc;
	}
	public  void setDesc(String desc){
		this.desc = desc ;
	}
	public VariousItemEntry[] getReward(){
		 return this.reward;
	}
	public  void setReward(VariousItemEntry[] reward){
		this.reward = reward ;
	}
	public int[] getTimes(){
		 return this.times;
	}
	public  void setTimes(int[] times){
		this.times = times ;
	}
	public int[] getFun(){
		 return this.fun;
	}
	public  void setFun(int[] fun){
		this.fun = fun ;
	}
	public int[] getDiscount(){
		 return this.discount;
	}
	public  void setDiscount(int[] discount){
		this.discount = discount ;
	}
	public VariousItemEntry[] getDayprize(){
		 return this.dayprize;
	}
	public  void setDayprize(VariousItemEntry[] dayprize){
		this.dayprize = dayprize ;
	}
	public Integer getMailt(){
		 return this.mailt;
	}
	public  void setMailt(Integer mailt){
		this.mailt = mailt ;
	}
	public Integer getMailc(){
		 return this.mailc;
	}
	public  void setMailc(Integer mailc){
		this.mailc = mailc ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
