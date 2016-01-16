/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class QualityRef {
	/**id*/
	private Integer id;
	/**强化加点*/
	private Integer enhance;
	/**分解道具*/
	private int[] item;
	/**每次挑战进度*/
	private Integer fight;
	/**强化消耗银币*/
	private Integer ecsilver;
	/**强化次数系数*/
	private Float eratio;
	/**第一次洗练*/
	private Integer wash1;
	/**第二次洗练*/
	private Integer wash2;
	/**第三次洗练*/
	private Integer wash3;
	/**品质点*/
	private int[] points;
	/**升级品质点*/
	private int[] upVals;
	/**技能专精点*/
	private int[] sp;
	/**技能升级消耗专精点*/
	private int[] spV;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getEnhance(){
		 return this.enhance;
	}
	public  void setEnhance(Integer enhance){
		this.enhance = enhance ;
	}
	public int[] getItem(){
		 return this.item;
	}
	public  void setItem(int[] item){
		this.item = item ;
	}
	public Integer getFight(){
		 return this.fight;
	}
	public  void setFight(Integer fight){
		this.fight = fight ;
	}
	public Integer getEcsilver(){
		 return this.ecsilver;
	}
	public  void setEcsilver(Integer ecsilver){
		this.ecsilver = ecsilver ;
	}
	public Float getEratio(){
		 return this.eratio;
	}
	public  void setEratio(Float eratio){
		this.eratio = eratio ;
	}
	public Integer getWash1(){
		 return this.wash1;
	}
	public  void setWash1(Integer wash1){
		this.wash1 = wash1 ;
	}
	public Integer getWash2(){
		 return this.wash2;
	}
	public  void setWash2(Integer wash2){
		this.wash2 = wash2 ;
	}
	public Integer getWash3(){
		 return this.wash3;
	}
	public  void setWash3(Integer wash3){
		this.wash3 = wash3 ;
	}
	public int[] getPoints(){
		 return this.points;
	}
	public  void setPoints(int[] points){
		this.points = points ;
	}
	public int[] getUpVals(){
		 return this.upVals;
	}
	public  void setUpVals(int[] upVals){
		this.upVals = upVals ;
	}
	public int[] getSp(){
		 return this.sp;
	}
	public  void setSp(int[] sp){
		this.sp = sp ;
	}
	public int[] getSpV(){
		 return this.spV;
	}
	public  void setSpV(int[] spV){
		this.spV = spV ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
