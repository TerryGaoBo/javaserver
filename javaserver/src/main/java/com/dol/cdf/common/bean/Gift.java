/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class Gift {
	/**id*/
	private Integer id;
	/**类型*/
	private Integer type;
	/**排序*/
	private Integer idx;
	/**活动图片*/
	private String img;
	/**描述1*/
	private String desc1;
	/**描述2*/
	private String desc2;
	/**开服开始时间*/
	private Integer startDay;
	/**开服结束时间*/
	private Integer endDay;
	/**开始时间*/
	private String startTime;
	/**结束时间*/
	private String endTime;
	/**礼包内容*/
	private VariousItemEntry[] items;
	/**所需条件*/
	private Integer value;
	/**提示*/
	private String tip;
	/**是否关闭*/
	private Integer close;
	/**老服开启*/
	private Integer oldOpen;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getType(){
		 return this.type;
	}
	public  void setType(Integer type){
		this.type = type ;
	}
	public Integer getIdx(){
		 return this.idx;
	}
	public  void setIdx(Integer idx){
		this.idx = idx ;
	}
	public String getImg(){
		 return this.img;
	}
	public  void setImg(String img){
		this.img = img ;
	}
	public String getDesc1(){
		 return this.desc1;
	}
	public  void setDesc1(String desc1){
		this.desc1 = desc1 ;
	}
	public String getDesc2(){
		 return this.desc2;
	}
	public  void setDesc2(String desc2){
		this.desc2 = desc2 ;
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
	public VariousItemEntry[] getItems(){
		 return this.items;
	}
	public  void setItems(VariousItemEntry[] items){
		this.items = items ;
	}
	public Integer getValue(){
		 return this.value;
	}
	public  void setValue(Integer value){
		this.value = value ;
	}
	public String getTip(){
		 return this.tip;
	}
	public  void setTip(String tip){
		this.tip = tip ;
	}
	public Integer getClose(){
		 return this.close;
	}
	public  void setClose(Integer close){
		this.close = close ;
	}
	public Integer getOldOpen(){
		 return this.oldOpen;
	}
	public  void setOldOpen(Integer oldOpen){
		this.oldOpen = oldOpen ;
	}
	@Override
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
