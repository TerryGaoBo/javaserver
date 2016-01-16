/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.*;
public class Beast {
	/**id*/
	private Integer id;
	/**尾数*/
	private Integer num;
	/**名称*/
	private String name;
	/**图片*/
	private String icon;
	/**技能*/
	private int[] skills;
	/**技能开启条件 远征章*/
	private int[] chapter;
	/**技能开启条件 远征关*/
	private int[] stage;
	/**注入尾兽之力所需查克拉*/
	private int cost;
	/**对应道具ID*/
	private Integer itemId;
	/**对应的人柱力*/
	private Integer role;
	/**人柱力等级*/
	private Integer level;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getNum(){
		 return this.num;
	}
	public  void setNum(Integer num){
		this.num = num ;
	}
	public String getName(){
		 return this.name;
	}
	public  void setName(String name){
		this.name = name ;
	}
	public String getIcon(){
		 return this.icon;
	}
	public  void setIcon(String icon){
		this.icon = icon ;
	}
	public int[] getSkills(){
		 return this.skills;
	}
	public  void setSkills(int[] skills){
		this.skills = skills ;
	}
	public int[] getChapter(){
		 return this.chapter;
	}
	public  void setChapter(int[] chapter){
		this.chapter = chapter ;
	}
	public int[] getStage(){
		 return this.stage;
	}
	public  void setStage(int[] stage){
		this.stage = stage ;
	}
	public int getCost(){
		 return this.cost;
	}
	public  void setCost(int cost){
		this.cost = cost ;
	}
	public Integer getItemId(){
		 return this.itemId;
	}
	public  void setItemId(Integer itemId){
		this.itemId = itemId ;
	}
	public Integer getRole(){
		 return this.role;
	}
	public  void setRole(Integer role){
		this.role = role ;
	}
	public Integer getLevel(){
		 return this.level;
	}
	public  void setLevel(Integer level){
		this.level = level ;
	}
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
