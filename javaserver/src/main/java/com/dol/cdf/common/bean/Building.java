/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Building {
	/**编号*/
	private Integer id;
	/**名称*/
	private String name;
	/**图片*/
	private String icon;
	/**描述*/
	private String desc;
	/**周期产出*/
	private VariousItemEntry[] produce;
	/**周期时间*/
	private int[] cd;
	/**花金币清周期时间*/
	private int[] clearcd;
	/**固定时间产出*/
	private VariousItemEntry[] give;
	/**固定时间*/
	private int[] time;
	/**建筑上方图标*/
	private String[] picon;
	/**升级所需玩家等级*/
	private int[] upLevel;
	/**升级效果描述*/
	private String[] upDesc;
	/**初始数值*/
	private int[] initValue;
	/**升级数值*/
	private int[] upValue;
	/**功能消耗*/
	private VariousItemEntry[] funcCost;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public String getName(){
		 return this.name;
	}
	public  void setName(String name){
		this.name = name ;
	}
	public String getIcon(){
		 return this.icon;
	}
	public  void setIcon(String icon){
		this.icon = icon ;
	}
	public String getDesc(){
		 return this.desc;
	}
	public  void setDesc(String desc){
		this.desc = desc ;
	}
	public VariousItemEntry[] getProduce(){
		 return this.produce;
	}
	public  void setProduce(VariousItemEntry[] produce){
		this.produce = produce ;
	}
	public int[] getCd(){
		 return this.cd;
	}
	public  void setCd(int[] cd){
		this.cd = cd ;
	}
	public int[] getClearcd(){
		 return this.clearcd;
	}
	public  void setClearcd(int[] clearcd){
		this.clearcd = clearcd ;
	}
	public VariousItemEntry[] getGive(){
		 return this.give;
	}
	public  void setGive(VariousItemEntry[] give){
		this.give = give ;
	}
	public int[] getTime(){
		 return this.time;
	}
	public  void setTime(int[] time){
		this.time = time ;
	}
	public String[] getPicon(){
		 return this.picon;
	}
	public  void setPicon(String[] picon){
		this.picon = picon ;
	}
	public int[] getUpLevel(){
		 return this.upLevel;
	}
	public  void setUpLevel(int[] upLevel){
		this.upLevel = upLevel ;
	}
	public String[] getUpDesc(){
		 return this.upDesc;
	}
	public  void setUpDesc(String[] upDesc){
		this.upDesc = upDesc ;
	}
	public int[] getInitValue(){
		 return this.initValue;
	}
	public  void setInitValue(int[] initValue){
		this.initValue = initValue ;
	}
	public int[] getUpValue(){
		 return this.upValue;
	}
	public  void setUpValue(int[] upValue){
		this.upValue = upValue ;
	}
	public VariousItemEntry[] getFuncCost(){
		 return this.funcCost;
	}
	public  void setFuncCost(VariousItemEntry[] funcCost){
		this.funcCost = funcCost ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
