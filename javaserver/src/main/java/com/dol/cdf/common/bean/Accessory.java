/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class Accessory implements BaseItem {
	/**id*/
	private Integer id;
	/**美术图标*/
	private String icon;
	/**名称*/
	private String alt;
	/**提示*/
	private String tip;
	/**类型*/
	private Integer category;
	/**品质*/
	private Integer quality;
	/**攻击*/
	private Integer att;
	/**防御*/
	private Integer def;
	/**血量*/
	private Integer hp;
	/**敏捷*/
	private Integer agi;
	/**等级*/
	private Integer level;
	/**购买价格*/
	private VariousItemEntry[] price;
	/**出售价格*/
	private VariousItemEntry[] sell;
	/**最大迭加上限*/
	private Integer stackmax;
	/**配方id*/
	private Integer fid;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public String getIcon(){
		 return this.icon;
	}
	public  void setIcon(String icon){
		this.icon = icon ;
	}
	public String getAlt(){
		 return this.alt;
	}
	public  void setAlt(String alt){
		this.alt = alt ;
	}
	public String getTip(){
		 return this.tip;
	}
	public  void setTip(String tip){
		this.tip = tip ;
	}
	public Integer getCategory(){
		 return this.category;
	}
	public  void setCategory(Integer category){
		this.category = category ;
	}
	public Integer getQuality(){
		 return this.quality;
	}
	public  void setQuality(Integer quality){
		this.quality = quality ;
	}
	public Integer getAtt(){
		 return this.att;
	}
	public  void setAtt(Integer att){
		this.att = att ;
	}
	public Integer getDef(){
		 return this.def;
	}
	public  void setDef(Integer def){
		this.def = def ;
	}
	public Integer getHp(){
		 return this.hp;
	}
	public  void setHp(Integer hp){
		this.hp = hp ;
	}
	public Integer getAgi(){
		 return this.agi;
	}
	public  void setAgi(Integer agi){
		this.agi = agi ;
	}
	public Integer getLevel(){
		 return this.level;
	}
	public  void setLevel(Integer level){
		this.level = level ;
	}
	public VariousItemEntry[] getPrice(){
		 return this.price;
	}
	public  void setPrice(VariousItemEntry[] price){
		this.price = price ;
	}
	public VariousItemEntry[] getSell(){
		 return this.sell;
	}
	public  void setSell(VariousItemEntry[] sell){
		this.sell = sell ;
	}
	public Integer getStackmax(){
		 return this.stackmax;
	}
	public  void setStackmax(Integer stackmax){
		this.stackmax = stackmax ;
	}
	public Integer getFid(){
		 return this.fid;
	}
	public  void setFid(Integer fid){
		this.fid = fid ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
