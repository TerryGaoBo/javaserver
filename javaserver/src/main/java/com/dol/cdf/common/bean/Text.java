/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class Text {
	/**编号*/
	private Integer id;
	/**内容*/
	private String text;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public String getText(){
		 return this.text;
	}
	public  void setText(String text){
		this.text = text ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
