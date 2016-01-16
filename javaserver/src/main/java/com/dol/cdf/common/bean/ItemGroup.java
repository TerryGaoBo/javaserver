/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class ItemGroup {
	/**id*/
	private Integer id;
	/**所有道具*/
	private VariousItemEntry[] items;
	/**需要忍币数量*/
	private int[] coin;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public VariousItemEntry[] getItems(){
		 return this.items;
	}
	public  void setItems(VariousItemEntry[] items){
		this.items = items ;
	}
	public int[] getCoin(){
		 return this.coin;
	}
	public  void setCoin(int[] coin){
		this.coin = coin ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
