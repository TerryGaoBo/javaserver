/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Shop {
	/**商店类型*/
	private Integer shopType;
	/**唯一id*/
	private Integer onlyid;
	/**道具id*/
	private Integer id;
	/**道具名称*/
	private Integer itemname;
	/**数量*/
	private Integer num;
	/**价格*/
	private VariousItemEntry[] price;
	/**顺序*/
	private Integer order;
	/**所在页*/
	private Integer page;
	public Integer getShopType(){
		 return this.shopType;
	}
	public  void setShopType(Integer shopType){
		this.shopType = shopType ;
	}
	public Integer getOnlyid(){
		 return this.onlyid;
	}
	public  void setOnlyid(Integer onlyid){
		this.onlyid = onlyid ;
	}
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getItemname(){
		 return this.itemname;
	}
	public  void setItemname(Integer itemname){
		this.itemname = itemname ;
	}
	public Integer getNum(){
		 return this.num;
	}
	public  void setNum(Integer num){
		this.num = num ;
	}
	public VariousItemEntry[] getPrice(){
		 return this.price;
	}
	public  void setPrice(VariousItemEntry[] price){
		this.price = price ;
	}
	public Integer getOrder(){
		 return this.order;
	}
	public  void setOrder(Integer order){
		this.order = order ;
	}
	public Integer getPage(){
		 return this.page;
	}
	public  void setPage(Integer page){
		this.page = page ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
