/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class Formula {
	/**配方id*/
	private Integer id;
	/**产出物品id*/
	private Integer product;
	/**消耗品*/
	private VariousItemEntry[] cost;
	/**显示的位置*/
	private Integer show;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getProduct(){
		 return this.product;
	}
	public  void setProduct(Integer product){
		this.product = product ;
	}
	public VariousItemEntry[] getCost(){
		 return this.cost;
	}
	public  void setCost(VariousItemEntry[] cost){
		this.cost = cost ;
	}
	public Integer getShow(){
		 return this.show;
	}
	public  void setShow(Integer show){
		this.show = show ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
