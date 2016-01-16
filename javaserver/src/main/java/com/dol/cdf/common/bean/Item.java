/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.gamefunction.IConditionGF;
import com.dol.cdf.common.gamefunction.IEffectGF;
public class Item implements BaseItem {
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
	/**购买价格*/
	private VariousItemEntry[] price;
	/**出售价格*/
	private VariousItemEntry[] sell;
	/**最大迭加上限*/
	private Integer stackmax;
	/**等级*/
	private Integer level;
	/**品质*/
	private Integer quality;
	/**道具使用条件*/
	private IConditionGF[] useConditons;
	/**道具使用的效果*/
	private IEffectGF[] useEffects;
	/**卷轴在界面的排序*/
	private Integer sidx;
	/**卷轴对应的技能*/
	private Integer sid;
	/**学习所需卷轴数量*/
	private Integer snum;
	@Override
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
	@Override
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
	@Override
	public Integer getCategory(){
		 return this.category;
	}
	public  void setCategory(Integer category){
		this.category = category ;
	}
	@Override
	public VariousItemEntry[] getPrice(){
		 return this.price;
	}
	public  void setPrice(VariousItemEntry[] price){
		this.price = price ;
	}
	@Override
	public VariousItemEntry[] getSell(){
		 return this.sell;
	}
	public  void setSell(VariousItemEntry[] sell){
		this.sell = sell ;
	}
	@Override
	public Integer getStackmax(){
		 return this.stackmax;
	}
	public  void setStackmax(Integer stackmax){
		this.stackmax = stackmax ;
	}
	public Integer getLevel(){
		 return this.level;
	}
	public  void setLevel(Integer level){
		this.level = level ;
	}
	@Override
	public Integer getQuality(){
		 return this.quality;
	}
	public  void setQuality(Integer quality){
		this.quality = quality ;
	}
	public IConditionGF[] getUseConditons(){
		 return this.useConditons;
	}
	public  void setUseConditons(IConditionGF[] useConditons){
		this.useConditons = useConditons ;
	}
	public IEffectGF[] getUseEffects(){
		 return this.useEffects;
	}
	public  void setUseEffects(IEffectGF[] useEffects){
		this.useEffects = useEffects ;
	}
	public Integer getSidx(){
		 return this.sidx;
	}
	public  void setSidx(Integer sidx){
		this.sidx = sidx ;
	}
	public Integer getSid(){
		 return this.sid;
	}
	public  void setSid(Integer sid){
		this.sid = sid ;
	}
	public Integer getSnum(){
		 return this.snum;
	}
	public  void setSnum(Integer snum){
		this.snum = snum ;
	}
	@Override
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
