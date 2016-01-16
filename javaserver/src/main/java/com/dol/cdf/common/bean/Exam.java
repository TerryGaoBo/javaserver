/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Exam {
	/**id*/
	private Integer id;
	/**忍者ID*/
	private Integer rid;
	/**等级*/
	private Integer level;
	/**奖励玩家经验*/
	private Integer exp;
	/**奖励忍者经验*/
	private Integer rxp;
	/**奖励玩家银币*/
	private Integer silver;
	/**是否掉落道具概率*/
	private Float rate;
	/**掉落组*/
	private Integer groupId;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getRid(){
		 return this.rid;
	}
	public  void setRid(Integer rid){
		this.rid = rid ;
	}
	public Integer getLevel(){
		 return this.level;
	}
	public  void setLevel(Integer level){
		this.level = level ;
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
	public Integer getSilver(){
		 return this.silver;
	}
	public  void setSilver(Integer silver){
		this.silver = silver ;
	}
	public Float getRate(){
		 return this.rate;
	}
	public  void setRate(Float rate){
		this.rate = rate ;
	}
	public Integer getGroupId(){
		 return this.groupId;
	}
	public  void setGroupId(Integer groupId){
		this.groupId = groupId ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
