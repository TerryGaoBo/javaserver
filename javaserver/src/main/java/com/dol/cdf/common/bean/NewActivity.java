/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class NewActivity {
	/**活动id*/
	private Integer id;
	/**活动方式*/
	private int[] types;
	/**活动名称*/
	private String name;
	/**活动简述不读该字段*/
	private String detail;
	/**活动内容*/
	private String content;
	/**活动数值*/
	private String[] values;
	/**道具奖励*/
	private String[] item;
	/**是否发邮件*/
	private Integer send;
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
	public int[] getTypes(){
		 return this.types;
	}
	public  void setTypes(int[] types){
		this.types = types ;
	}
	public String getName(){
		 return this.name;
	}
	public  void setName(String name){
		this.name = name ;
	}
	public String getDetail(){
		 return this.detail;
	}
	public  void setDetail(String detail){
		this.detail = detail ;
	}
	public String getContent(){
		 return this.content;
	}
	public  void setContent(String content){
		this.content = content ;
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
	public Integer getSend(){
		 return this.send;
	}
	public  void setSend(Integer send){
		this.send = send ;
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
