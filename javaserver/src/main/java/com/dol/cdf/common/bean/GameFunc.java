/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class GameFunc {
	/**编号*/
	private Integer id;
	/**符号*/
	private String symbol;
	/**参数表*/
	private String paramTable;
	/**允许类型*/
	private String validType;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public String getSymbol(){
		 return this.symbol;
	}
	public  void setSymbol(String symbol){
		this.symbol = symbol ;
	}
	public String getParamTable(){
		 return this.paramTable;
	}
	public  void setParamTable(String paramTable){
		this.paramTable = paramTable ;
	}
	public String getValidType(){
		 return this.validType;
	}
	public  void setValidType(String validType){
		this.validType = validType ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
