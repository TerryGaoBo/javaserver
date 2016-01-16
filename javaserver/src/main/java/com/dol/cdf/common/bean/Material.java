/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class Material implements BaseItem {
	/**id*/
	private Integer id;
	/**美术图标*/
	private String icon;
	/**名称*/
	private String alt;
	/**显示类型*/
	private String form;
	/**类型*/
	private Integer category;
	/**品质*/
	private Integer quality;
	/**说明*/
	private String tip;
	/**购买价格*/
	private VariousItemEntry[] price;
	/**出售价格*/
	private VariousItemEntry[] sell;
	/**最大迭加上限*/
	private Integer stackmax;
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
	public String getForm(){
		 return this.form;
	}
	public  void setForm(String form){
		this.form = form ;
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
	public String getTip(){
		 return this.tip;
	}
	public  void setTip(String tip){
		this.tip = tip ;
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
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
