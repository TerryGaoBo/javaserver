/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Guild {
	/**军团等级*/
	private Integer lv;
	/**军团升级经验*/
	private Integer exp;
	/**升阶条件*/
	private int[] upgrade;
	/**军团人数上限*/
	private Integer maxnumber;
	/**军团财富上限*/
	private Integer maxmoney;
	/**军团职务数量*/
	private String[] jobnumber;
	/**军团部队总数*/
	private Integer guildarmy;
	/**军团据点*/
	private int[] armyposition;
	/**军团科技最高品质*/
	private Integer techquality;
	/**偷袭所获贡献*/
	private Integer attackcontribution;
	/**偷袭所获经验*/
	private Integer attackexp;
	/**偷袭所获财富*/
	private Integer attackmoney;
	/**军团战战斗贡献*/
	private Integer battlecontribution;
	/**军团战战斗经验*/
	private Integer battleexp;
	/**占据据点贡献*/
	private Integer positioncontribution;
	/**占据据点经验*/
	private Integer positionexp;
	/**军团战胜利贡献*/
	private Integer warcontribution;
	/**军团战胜利经验*/
	private Integer warexp;
	/**军团战胜利财富*/
	private Integer warmoney;
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
	public int[] getUpgrade(){
		 return this.upgrade;
	}
	public  void setUpgrade(int[] upgrade){
		this.upgrade = upgrade ;
	}
	public Integer getMaxnumber(){
		 return this.maxnumber;
	}
	public  void setMaxnumber(Integer maxnumber){
		this.maxnumber = maxnumber ;
	}
	public Integer getMaxmoney(){
		 return this.maxmoney;
	}
	public  void setMaxmoney(Integer maxmoney){
		this.maxmoney = maxmoney ;
	}
	public String[] getJobnumber(){
		 return this.jobnumber;
	}
	public  void setJobnumber(String[] jobnumber){
		this.jobnumber = jobnumber ;
	}
	public Integer getGuildarmy(){
		 return this.guildarmy;
	}
	public  void setGuildarmy(Integer guildarmy){
		this.guildarmy = guildarmy ;
	}
	public int[] getArmyposition(){
		 return this.armyposition;
	}
	public  void setArmyposition(int[] armyposition){
		this.armyposition = armyposition ;
	}
	public Integer getTechquality(){
		 return this.techquality;
	}
	public  void setTechquality(Integer techquality){
		this.techquality = techquality ;
	}
	public Integer getAttackcontribution(){
		 return this.attackcontribution;
	}
	public  void setAttackcontribution(Integer attackcontribution){
		this.attackcontribution = attackcontribution ;
	}
	public Integer getAttackexp(){
		 return this.attackexp;
	}
	public  void setAttackexp(Integer attackexp){
		this.attackexp = attackexp ;
	}
	public Integer getAttackmoney(){
		 return this.attackmoney;
	}
	public  void setAttackmoney(Integer attackmoney){
		this.attackmoney = attackmoney ;
	}
	public Integer getBattlecontribution(){
		 return this.battlecontribution;
	}
	public  void setBattlecontribution(Integer battlecontribution){
		this.battlecontribution = battlecontribution ;
	}
	public Integer getBattleexp(){
		 return this.battleexp;
	}
	public  void setBattleexp(Integer battleexp){
		this.battleexp = battleexp ;
	}
	public Integer getPositioncontribution(){
		 return this.positioncontribution;
	}
	public  void setPositioncontribution(Integer positioncontribution){
		this.positioncontribution = positioncontribution ;
	}
	public Integer getPositionexp(){
		 return this.positionexp;
	}
	public  void setPositionexp(Integer positionexp){
		this.positionexp = positionexp ;
	}
	public Integer getWarcontribution(){
		 return this.warcontribution;
	}
	public  void setWarcontribution(Integer warcontribution){
		this.warcontribution = warcontribution ;
	}
	public Integer getWarexp(){
		 return this.warexp;
	}
	public  void setWarexp(Integer warexp){
		this.warexp = warexp ;
	}
	public Integer getWarmoney(){
		 return this.warmoney;
	}
	public  void setWarmoney(Integer warmoney){
		this.warmoney = warmoney ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
