/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Recruit {
	/**id*/
	private Integer id;
	/**所需玩家等级*/
	private Integer needLv;
	/**所需玩家体力*/
	private Integer energy;
	/**等级*/
	private Integer level;
	/**奖励玩家经验*/
	private Integer exp;
	/**奖励的忍者经验*/
	private Integer rxp;
	/**奖励玩家银币*/
	private Integer silver;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getNeedLv(){
		 return this.needLv;
	}
	public  void setNeedLv(Integer needLv){
		this.needLv = needLv ;
	}
	public Integer getEnergy(){
		 return this.energy;
	}
	public  void setEnergy(Integer energy){
		this.energy = energy ;
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
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
