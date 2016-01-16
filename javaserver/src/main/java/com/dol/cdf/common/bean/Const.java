/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class Const {
	/**段名*/
	private String name;
	/**具体值*/
	private String value;
	public String getName(){
		 return this.name;
	}
	public  void setName(String name){
		this.name = name ;
	}
	public String getValue(){
		 return this.value;
	}
	public  void setValue(String value){
		this.value = value ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
