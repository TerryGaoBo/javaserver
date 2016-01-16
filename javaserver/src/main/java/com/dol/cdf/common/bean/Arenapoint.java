/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Arenapoint {
	/**id*/
	private Integer id;
	/**排名区间*/
	private int[] rank;
	/**每日奖励竞技点*/
	private Integer reward_arenapoint;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public int[] getRank(){
		 return this.rank;
	}
	public  void setRank(int[] rank){
		this.rank = rank ;
	}
	public Integer getReward_arenapoint(){
		 return this.reward_arenapoint;
	}
	public  void setReward_arenapoint(Integer reward_arenapoint){
		this.reward_arenapoint = reward_arenapoint ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
