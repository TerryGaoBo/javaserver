/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class DropGroup {
	/**库ID*/
	private Integer id;
	/**物品ID*/
	private VariousItemRateEntry[] item;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public VariousItemRateEntry[] getItem(){
		 return this.item;
	}
	public  void setItem(VariousItemRateEntry[] item){
		this.item = item ;
	}
	@Override
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
