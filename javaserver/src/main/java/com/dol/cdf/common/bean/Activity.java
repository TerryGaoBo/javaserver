/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Activity {
	/**ID*/
	private Integer id;
	/**名称*/
	private String name;
	/**是否关闭*/
	private Integer close;
	/**展示时间*/
	private String showTime;
	/**是否需要发邮件*/
	private Integer send;
	/**开服开始时间*/
	private Integer startDay;
	/**开服结束时间*/
	private Integer endDay;
	/**开始时间*/
	private String startTime;
	/**结束时间*/
	private String endTime;
	/**活动内容*/
	private String content;
	/**活动方式*/
	private int[] types;
	/**活动数值*/
	private String[] values;
	/**道具奖励*/
	private String[] item;
	/**邮件内容*/
	private int[] mailText;
	/**老服开启*/
	private Integer oldOpen;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public String getName(){
		 return this.name;
	}
	public  void setName(String name){
		this.name = name ;
	}
	public Integer getClose(){
		 return this.close;
	}
	public  void setClose(Integer close){
		this.close = close ;
	}
	public String getShowTime(){
		 return this.showTime;
	}
	public  void setShowTime(String showTime){
		this.showTime = showTime ;
	}
	public Integer getSend(){
		 return this.send;
	}
	public  void setSend(Integer send){
		this.send = send ;
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
	public String getContent(){
		 return this.content;
	}
	public  void setContent(String content){
		this.content = content ;
	}
	public int[] getTypes(){
		 return this.types;
	}
	public  void setTypes(int[] types){
		this.types = types ;
	}
	public String[] getValues(){
		 return this.values;
	}
	public  void setValues(String[] values){
		this.values = values ;
	}
	public String[] getItem(){
		 return this.item;
	}
	public  void setItem(String[] item){
		this.item = item ;
	}
	public int[] getMailText(){
		 return this.mailText;
	}
	public  void setMailText(int[] mailText){
		this.mailText = mailText ;
	}
	public Integer getOldOpen(){
		 return this.oldOpen;
	}
	public  void setOldOpen(Integer oldOpen){
		this.oldOpen = oldOpen ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
