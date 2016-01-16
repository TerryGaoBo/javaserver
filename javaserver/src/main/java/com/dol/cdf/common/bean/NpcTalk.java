/**
* auto generated, do not change it !!!!!
*/
package com.dol.cdf.common.bean;
import com.dol.cdf.common.StringHelper;
public class NpcTalk {
	/**编号*/
	private Integer id;
	/**顺序*/
	private Integer idx;
	/**角色*/
	private Integer roleId;
	/**特殊角色*/
	private String url;
	/**特殊角色名*/
	private String name;
	/**myRoleId*/
	private Integer myRoleId;
	/**level*/
	private Integer level;
	/**方位*/
	private Integer leftRight;
	/**对话*/
	private String NpcWords;
	public Integer getId(){
		 return this.id;
	}
	public  void setId(Integer id){
		this.id = id ;
	}
	public Integer getIdx(){
		 return this.idx;
	}
	public  void setIdx(Integer idx){
		this.idx = idx ;
	}
	public Integer getRoleId(){
		 return this.roleId;
	}
	public  void setRoleId(Integer roleId){
		this.roleId = roleId ;
	}
	public String getUrl(){
		 return this.url;
	}
	public  void setUrl(String url){
		this.url = url ;
	}
	public String getName(){
		 return this.name;
	}
	public  void setName(String name){
		this.name = name ;
	}
	public Integer getMyRoleId(){
		 return this.myRoleId;
	}
	public  void setMyRoleId(Integer myRoleId){
		this.myRoleId = myRoleId ;
	}
	public Integer getLevel(){
		 return this.level;
	}
	public  void setLevel(Integer level){
		this.level = level ;
	}
	public Integer getLeftRight(){
		 return this.leftRight;
	}
	public  void setLeftRight(Integer leftRight){
		this.leftRight = leftRight ;
	}
	public String getNpcWords(){
		 return this.NpcWords;
	}
	public  void setNpcWords(String NpcWords){
		this.NpcWords = NpcWords ;
	}
	@Override
	public String toString(){
		return StringHelper.obj2String(this, null);
	}
}
