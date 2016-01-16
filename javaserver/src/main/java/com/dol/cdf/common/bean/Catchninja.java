/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Catchninja {
	/**ID*/
	private Integer id;
	/**积分方式*/
	private int[] integralway;
	/**积分值*/
	private int[] integral;
	/**排行积分*/
	private Integer rankintegral;
	/**排行人数*/
	private Integer ranknumber;
	/**活动忍者品质*/
	private int[] quality;
	/**活动忍者ID*/
	private String[] ninjaID;
	/**活动忍者几率*/
	private int[] ninjaRate;
	/**活动幸运起步值*/
	private int[] luckStart;
	/**活动幸运步进值*/
	private int[] luckStep;
	/**活动幸运权重值*/
	private int[] luckValue;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public int[] getIntegralway(){
		 return this.integralway;
	}
	public  void setIntegralway(int[] integralway){
		this.integralway = integralway ;
	}
	public int[] getIntegral(){
		 return this.integral;
	}
	public  void setIntegral(int[] integral){
		this.integral = integral ;
	}
	public Integer getRankintegral(){
		 return this.rankintegral;
	}
	public  void setRankintegral(Integer rankintegral){
		this.rankintegral = rankintegral ;
	}
	public Integer getRanknumber(){
		 return this.ranknumber;
	}
	public  void setRanknumber(Integer ranknumber){
		this.ranknumber = ranknumber ;
	}
	public int[] getQuality(){
		 return this.quality;
	}
	public  void setQuality(int[] quality){
		this.quality = quality ;
	}
	public String[] getNinjaID(){
		 return this.ninjaID;
	}
	public  void setNinjaID(String[] ninjaID){
		this.ninjaID = ninjaID ;
	}
	public int[] getNinjaRate(){
		 return this.ninjaRate;
	}
	public  void setNinjaRate(int[] ninjaRate){
		this.ninjaRate = ninjaRate ;
	}
	public int[] getLuckStart(){
		 return this.luckStart;
	}
	public  void setLuckStart(int[] luckStart){
		this.luckStart = luckStart ;
	}
	public int[] getLuckStep(){
		 return this.luckStep;
	}
	public  void setLuckStep(int[] luckStep){
		this.luckStep = luckStep ;
	}
	public int[] getLuckValue(){
		 return this.luckValue;
	}
	public  void setLuckValue(int[] luckValue){
		this.luckValue = luckValue ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
