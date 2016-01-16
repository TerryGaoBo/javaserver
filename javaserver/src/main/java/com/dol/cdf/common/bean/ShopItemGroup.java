/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class ShopItemGroup {
	/**商店类型*/
	private Integer shopType;
	/**id*/
	private Integer id;
	/**权重*/
	private Integer weight;
	/**最小价格*/
	private VariousItemEntry[] minprice;
	/**最大价格*/
	private VariousItemEntry[] maxprice;
	/**是否超值*/
	private Integer overflow;
	public Integer getShopType(){
		 return this.shopType;
	}
	public  void setShopType(Integer shopType){
		this.shopType = shopType ;
	}
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getWeight(){
		 return this.weight;
	}
	public  void setWeight(Integer weight){
		this.weight = weight ;
	}
	public VariousItemEntry[] getMinprice(){
		 return this.minprice;
	}
	public  void setMinprice(VariousItemEntry[] minprice){
		this.minprice = minprice ;
	}
	public VariousItemEntry[] getMaxprice(){
		 return this.maxprice;
	}
	public  void setMaxprice(VariousItemEntry[] maxprice){
		this.maxprice = maxprice ;
	}
	public Integer getOverflow(){
		 return this.overflow;
	}
	public  void setOverflow(Integer overflow){
		this.overflow = overflow ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
