/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class GuildWar {
	/**军阶*/
	private Integer lv;
	/**进攻计分*/
	private Integer attackpoint;
	/**防守计分*/
	private Integer defendpoint;
	/**占领得分*/
	private Integer standpoint;
	/**最终胜利贡献*/
	private Integer overwincontribution;
	/**最终失败贡献*/
	private Integer overlosecontribution;
	/**每次胜利贡献*/
	private Integer oncewincontribution;
	/**每次失败贡献*/
	private Integer oncelosecontribution;
	/**胜利军团经验*/
	private Integer winguildexp;
	/**胜利财富*/
	private Integer winmoney;
	/**胜方前三名和参与者获得的道具奖励*/
	private VariousItemEntry[] winrewarditem;
	/**败方前三名和参与者获得的道具奖励*/
	private VariousItemEntry[] loserewarditem;
	/**平局获得的道具奖励*/
	private VariousItemEntry[] drawrewarditem ;
	public Integer getLv(){
		 return this.lv;
	}
	public  void setLv(Integer lv){
		this.lv = lv ;
	}
	public Integer getAttackpoint(){
		 return this.attackpoint;
	}
	public  void setAttackpoint(Integer attackpoint){
		this.attackpoint = attackpoint ;
	}
	public Integer getDefendpoint(){
		 return this.defendpoint;
	}
	public  void setDefendpoint(Integer defendpoint){
		this.defendpoint = defendpoint ;
	}
	public Integer getStandpoint(){
		 return this.standpoint;
	}
	public  void setStandpoint(Integer standpoint){
		this.standpoint = standpoint ;
	}
	public Integer getOverwincontribution(){
		 return this.overwincontribution;
	}
	public  void setOverwincontribution(Integer overwincontribution){
		this.overwincontribution = overwincontribution ;
	}
	public Integer getOverlosecontribution(){
		 return this.overlosecontribution;
	}
	public  void setOverlosecontribution(Integer overlosecontribution){
		this.overlosecontribution = overlosecontribution ;
	}
	public Integer getOncewincontribution(){
		 return this.oncewincontribution;
	}
	public  void setOncewincontribution(Integer oncewincontribution){
		this.oncewincontribution = oncewincontribution ;
	}
	public Integer getOncelosecontribution(){
		 return this.oncelosecontribution;
	}
	public  void setOncelosecontribution(Integer oncelosecontribution){
		this.oncelosecontribution = oncelosecontribution ;
	}
	public Integer getWinguildexp(){
		 return this.winguildexp;
	}
	public  void setWinguildexp(Integer winguildexp){
		this.winguildexp = winguildexp ;
	}
	public Integer getWinmoney(){
		 return this.winmoney;
	}
	public  void setWinmoney(Integer winmoney){
		this.winmoney = winmoney ;
	}
	public VariousItemEntry[] getWinrewarditem(){
		 return this.winrewarditem;
	}
	public  void setWinrewarditem(VariousItemEntry[] winrewarditem){
		this.winrewarditem = winrewarditem ;
	}
	public VariousItemEntry[] getLoserewarditem(){
		 return this.loserewarditem;
	}
	public  void setLoserewarditem(VariousItemEntry[] loserewarditem){
		this.loserewarditem = loserewarditem ;
	}
	public VariousItemEntry[] getDrawrewarditem (){
		 return this.drawrewarditem ;
	}
	public  void setDrawrewarditem (VariousItemEntry[] drawrewarditem ){
		this.drawrewarditem  = drawrewarditem  ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
