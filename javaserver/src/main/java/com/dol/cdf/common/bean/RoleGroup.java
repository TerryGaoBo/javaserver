/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.gamefunction.IEffectGF;
public class RoleGroup {
	/**id*/
	private Integer id;
	/**名称*/
	private String name;
	/**描述*/
	private String desc;
	/**成员列表*/
	private int[] roles;
	/**alterSelf*/
	private IEffectGF[] alterSelf;
	/**alterTarget*/
	private IEffectGF[] alterTarget;
	/**skill*/
	private Integer skill;
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
	public String getDesc(){
		 return this.desc;
	}
	public  void setDesc(String desc){
		this.desc = desc ;
	}
	public int[] getRoles(){
		 return this.roles;
	}
	public  void setRoles(int[] roles){
		this.roles = roles ;
	}
	public IEffectGF[] getAlterSelf(){
		 return this.alterSelf;
	}
	public  void setAlterSelf(IEffectGF[] alterSelf){
		this.alterSelf = alterSelf ;
	}
	public IEffectGF[] getAlterTarget(){
		 return this.alterTarget;
	}
	public  void setAlterTarget(IEffectGF[] alterTarget){
		this.alterTarget = alterTarget ;
	}
	public Integer getSkill(){
		 return this.skill;
	}
	public  void setSkill(Integer skill){
		this.skill = skill ;
	}
	@Override
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
