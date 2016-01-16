/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Quest {
	/**编号*/
	private Integer id;
	/**任务名称*/
	private Integer alt;
	/**品质*/
	private Integer quality;
	/**接受任务最小等级*/
	private Integer minLv;
	/**接受任务最大等级*/
	private Integer maxLv;
	/**快捷按钮跳转*/
	private String skip;
	/**完成类型*/
	private Integer type;
	/**目标完成等级*/
	private Integer tarLv;
	/**目标完成次数*/
	private Integer num;
	/**完成描述*/
	private String desc;
	/**银币奖励*/
	private Integer silver;
	/**日常任务*/
	private Integer daily;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getAlt(){
		 return this.alt;
	}
	public  void setAlt(Integer alt){
		this.alt = alt ;
	}
	public Integer getQuality(){
		 return this.quality;
	}
	public  void setQuality(Integer quality){
		this.quality = quality ;
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
	public String getSkip(){
		 return this.skip;
	}
	public  void setSkip(String skip){
		this.skip = skip ;
	}
	public Integer getType(){
		 return this.type;
	}
	public  void setType(Integer type){
		this.type = type ;
	}
	public Integer getTarLv(){
		 return this.tarLv;
	}
	public  void setTarLv(Integer tarLv){
		this.tarLv = tarLv ;
	}
	public Integer getNum(){
		 return this.num;
	}
	public  void setNum(Integer num){
		this.num = num ;
	}
	public String getDesc(){
		 return this.desc;
	}
	public  void setDesc(String desc){
		this.desc = desc ;
	}
	public Integer getSilver(){
		 return this.silver;
	}
	public  void setSilver(Integer silver){
		this.silver = silver ;
	}
	public Integer getDaily(){
		 return this.daily;
	}
	public  void setDaily(Integer daily){
		this.daily = daily ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
