package com.jelly.team;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class TeamMember {
	
	@JsonProperty("guid")
	private String guid;
	
	@JsonProperty("title")
	private TeamMemberTitle title;	//	职务
	
	@JsonProperty("contribute")
	private int contribute = 0;	// 贡献
	
	@JsonProperty("pci")
	private int pci = 0;// 派遣总次数（每周日晚上十二点清0）
	
	@JsonProperty("mail")
	private List<ItemMail> mailList = Lists.newArrayList();
	
	public TeamMember() {}
	
	public TeamMember(String guid, TeamMemberTitle title) {
		this.guid = guid;
		this.title = title;
	}

	public String getGuid() {
		return this.guid;
	}
	
	public TeamMemberTitle getTitle() {
		return this.title;
	}
	
	public void setTitle(TeamMemberTitle title) {
		this.title = title;
	}
	
	public int getContribute() {
		return contribute;
	}
	
	public int addContribute(int contribute) {
		return this.contribute += contribute;
	}
	
	public int reduceContribute(int contribute) {
		return this.contribute -= contribute;
	}
	
	public int getPci()
	{
		return this.pci;
	}
	public void setPci(int value)
	{
		this.pci = value;
	}
	
	////------------------------------ 
	public void addMailItem(ItemMail item){
		if(mailList == null){
			mailList = Lists.newArrayList();
		}
		mailList.add(item);
		
		if(mailList.size()>=60){
			for(int i=0;i<mailList.size();i++){
				ItemMail mm = mailList.get(i);
				if(!mm.isAttachMent()){
					mailList.remove(i);
					break;
				}
			}
		}
	}
	public List<ItemMail> getMailItem(){
		if(mailList == null){
			mailList = Lists.newArrayList();
		}
		return this.mailList;
	}
	public void clearMailItem()
	{
		if(this.mailList != null){
			this.mailList.clear();
		}
	}
}
