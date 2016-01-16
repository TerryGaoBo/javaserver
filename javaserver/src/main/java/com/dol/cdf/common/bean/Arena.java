/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Arena {
	/**id*/
	private Integer id;
	/**排名区间*/
	private int[] rank;
	/**每日奖励包ID*/
	private Integer item;
	/**第1格的排名*/
	private Float grid1;
	/**第2格的排名*/
	private Float grid2;
	/**第3格的排名*/
	private Float grid3;
	/**第4格的排名*/
	private Float grid4;
	/**第5格的排名*/
	private Float grid5;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public int[] getRank(){
		 return this.rank;
	}
	public  void setRank(int[] rank){
		this.rank = rank ;
	}
	public Integer getItem(){
		 return this.item;
	}
	public  void setItem(Integer item){
		this.item = item ;
	}
	public Float getGrid1(){
		 return this.grid1;
	}
	public  void setGrid1(Float grid1){
		this.grid1 = grid1 ;
	}
	public Float getGrid2(){
		 return this.grid2;
	}
	public  void setGrid2(Float grid2){
		this.grid2 = grid2 ;
	}
	public Float getGrid3(){
		 return this.grid3;
	}
	public  void setGrid3(Float grid3){
		this.grid3 = grid3 ;
	}
	public Float getGrid4(){
		 return this.grid4;
	}
	public  void setGrid4(Float grid4){
		this.grid4 = grid4 ;
	}
	public Float getGrid5(){
		 return this.grid5;
	}
	public  void setGrid5(Float grid5){
		this.grid5 = grid5 ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
