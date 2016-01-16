/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class BuildingRef {
	/**建筑ID*/
	private Integer id;
	/**建筑等级*/
	private Integer level;
	/**升级所需东西*/
	private VariousItemEntry[] upItem;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getLevel(){
		 return this.level;
	}
	public  void setLevel(Integer level){
		this.level = level ;
	}
	public VariousItemEntry[] getUpItem(){
		 return this.upItem;
	}
	public  void setUpItem(VariousItemEntry[] upItem){
		this.upItem = upItem ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
