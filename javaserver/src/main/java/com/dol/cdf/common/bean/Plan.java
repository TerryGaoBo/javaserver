/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Plan {
	/**计划id*/
	private Integer id;
	/**是否开放*/
	private Integer close;
	/**类型*/
	private Integer type;
	/**展示时间*/
	private String showTime;
	/**开服开始时间*/
	private Integer startDay;
	/**开服结束时间*/
	private Integer endDay;
	/**开始时间*/
	private String startTime;
	/**结束时间*/
	private String endTime;
	/**活动id*/
	private int[] activity_gift_id;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getClose(){
		 return this.close;
	}
	public  void setClose(Integer close){
		this.close = close ;
	}
	public Integer getType(){
		 return this.type;
	}
	public  void setType(Integer type){
		this.type = type ;
	}
	public String getShowTime(){
		 return this.showTime;
	}
	public  void setShowTime(String showTime){
		this.showTime = showTime ;
	}
	public Integer getStartDay(){
		 return this.startDay;
	}
	public  void setStartDay(Integer startDay){
		this.startDay = startDay ;
	}
	public Integer getEndDay(){
		 return this.endDay;
	}
	public  void setEndDay(Integer endDay){
		this.endDay = endDay ;
	}
	public String getStartTime(){
		 return this.startTime;
	}
	public  void setStartTime(String startTime){
		this.startTime = startTime ;
	}
	public String getEndTime(){
		 return this.endTime;
	}
	public  void setEndTime(String endTime){
		this.endTime = endTime ;
	}
	public int[] getActivity_gift_id(){
		 return this.activity_gift_id;
	}
	public  void setActivity_gift_id(int[] activity_gift_id){
		this.activity_gift_id = activity_gift_id ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
