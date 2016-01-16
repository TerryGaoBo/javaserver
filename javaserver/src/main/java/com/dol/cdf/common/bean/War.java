/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class War {
	/**id*/
	private Integer id;
	/**忍币*/
	private Integer coin;
	/**衰减度*/
	private Integer ratio;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getCoin(){
		 return this.coin;
	}
	public  void setCoin(Integer coin){
		this.coin = coin ;
	}
	public Integer getRatio(){
		 return this.ratio;
	}
	public  void setRatio(Integer ratio){
		this.ratio = ratio ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
