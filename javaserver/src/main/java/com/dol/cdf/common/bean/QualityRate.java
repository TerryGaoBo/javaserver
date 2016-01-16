/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class QualityRate {
	/**类型*/
	private Integer type;
	/**建筑最小等级*/
	private Integer minLv;
	/**建筑最大等级*/
	private Integer maxLv;
	/**D级百分比*/
	private Integer drate;
	/**C级百分比*/
	private Integer crate;
	/**B级百分比*/
	private Integer brate;
	/**A级百分比*/
	private Integer arate;
	/**S级百分比*/
	private Integer srate;
	/**SS级百分比*/
	private Integer ssrate;
	/**幸运品质*/
	private int[] luckQuality;
	/**幸运起步值*/
	private int[] luckStart;
	/**幸运步进值*/
	private int[] luckStep;
	/**幸运权重价值*/
	private int[] luckValue;
	public Integer getType(){
		 return this.type;
	}
	public  void setType(Integer type){
		this.type = type ;
	}
	public Integer getMinLv(){
		 return this.minLv;
	}
	public  void setMinLv(Integer minLv){
		this.minLv = minLv ;
	}
	public Integer getMaxLv(){
		 return this.maxLv;
	}
	public  void setMaxLv(Integer maxLv){
		this.maxLv = maxLv ;
	}
	public Integer getDrate(){
		 return this.drate;
	}
	public  void setDrate(Integer drate){
		this.drate = drate ;
	}
	public Integer getCrate(){
		 return this.crate;
	}
	public  void setCrate(Integer crate){
		this.crate = crate ;
	}
	public Integer getBrate(){
		 return this.brate;
	}
	public  void setBrate(Integer brate){
		this.brate = brate ;
	}
	public Integer getArate(){
		 return this.arate;
	}
	public  void setArate(Integer arate){
		this.arate = arate ;
	}
	public Integer getSrate(){
		 return this.srate;
	}
	public  void setSrate(Integer srate){
		this.srate = srate ;
	}
	public Integer getSsrate(){
		 return this.ssrate;
	}
	public  void setSsrate(Integer ssrate){
		this.ssrate = ssrate ;
	}
	public int[] getLuckQuality(){
		 return this.luckQuality;
	}
	public  void setLuckQuality(int[] luckQuality){
		this.luckQuality = luckQuality ;
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
