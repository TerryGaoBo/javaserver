/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Dig {
	/**id*/
	private Integer id;
	/**类型*/
	private String type;
	/**数量*/
	private Integer amount;
	/**几率*/
	private Integer rate;
	/**是否贵重*/
	private Integer precious;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public String getType(){
		 return this.type;
	}
	public  void setType(String type){
		this.type = type ;
	}
	public Integer getAmount(){
		 return this.amount;
	}
	public  void setAmount(Integer amount){
		this.amount = amount ;
	}
	public Integer getRate(){
		 return this.rate;
	}
	public  void setRate(Integer rate){
		this.rate = rate ;
	}
	public Integer getPrecious(){
		 return this.precious;
	}
	public  void setPrecious(Integer precious){
		this.precious = precious ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
