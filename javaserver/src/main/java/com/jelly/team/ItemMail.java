package com.jelly.team;

import io.nadron.app.Player;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.VariousItemEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ItemMail {
	
	@JsonProperty("h")
	private Integer title;
	
	@JsonProperty("c")
	private Integer content;
	
	@JsonProperty("ins")
	private List<String> insWords = Lists.newArrayList();
	
	@JsonProperty("f")
	private Map<String,Integer> reward = Maps.newHashMap();
	
	public ItemMail(){
		this.reward = Maps.newHashMap();
	}
	
	public void setSysMail(Integer title,Integer content,String... insWords)
	{
		this.title = title;
		this.content = content;
		this.insWords =  Lists.newArrayList();
		for(int i =0;i<insWords.length;i++){
			this.insWords.add(insWords[i]);
		}
	}
	
	public void addRewards(String type,int count){
		this.reward.put(type, count);
	}
	
	public void addPlayerSysMail(Player player){
		String [] words = this.insWords.toArray(new String[this.insWords.size()]);
		player.getMail().addSysMail(this.title,this.content,words);
	}
	
	public void addPlayerSysItemsMail(Player player){
		String [] words = this.insWords.toArray(new String[this.insWords.size()]);
		
		List<VariousItemEntry> rwards = Lists.newArrayList();
		for (Map.Entry<String,Integer> mmm : reward.entrySet()){
			String type = mmm.getKey();
			Integer count = mmm.getValue();
			VariousItemEntry ks = new VariousItemEntry(type, count);
			rwards.add(ks);
		}
		
		VariousItemEntry [] items = rwards.toArray(new VariousItemEntry[rwards.size()]);
		player.getMail().addSysItemMail(items, this.title, this.content, words);
	}
	
	public boolean isAttachMent()
	{
		return reward.size()>0;
	}
	
}
