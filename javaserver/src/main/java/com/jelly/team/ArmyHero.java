package com.jelly.team;

import com.dol.cdf.common.TimeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jelly.hero.Hero;

public class ArmyHero {
	
	@JsonProperty("hid")
	private Integer hid = -1;
	
	@JsonProperty("o")
	private Hero hero = null;
	
	@JsonProperty("t")
	private long t = 0; // 派遣的时间戳，派遣开始，则计时开始，（例如：每隔10小时候次数+1）
	
	@JsonProperty("e")
	private Integer strongID = -1; // 所在据点id (0-20)
	
	@JsonProperty("time")
	private long time = 0;	// 派遣的时间戳
	
	/////
	@JsonProperty("h")
	private Integer heroWhere = -1;// -1 不在据点 0 在我的据点中，1 在敌方据点中
	
	@JsonProperty("hps")
	private float hps = 1;
	
	public ArmyHero()
	{
		this.hps = 1;
		this.hid = -1;
	}
	public ArmyHero(Hero o)
	{
		this();
		this.hero = o;
		this.time = TimeUtil.getCurrentTime();
	}
	
	public void setHid(Integer id)
	{
		this.hid = id;
	}
	
	public Integer getHid()
	{
		return this.hid;
	}
	
	public void setHps(float hps)
	{
		this.hps = hps;
	}

	public float getHps()
	{
		return hps;
	}
	
	public void setheroWhere(Integer value)
	{
		this.heroWhere = value;
	}
	public Integer getheroWhere()
	{
		return this.heroWhere;
	}
	
	public Hero getHero()
	{
		return this.hero;
	}
	
	public void setHero(Hero o)
	{
		this.hero = o;
	}
	
	public void setStrongID(Integer strongID)
	{
		this.strongID = strongID;
	}
	
	public Integer getStrongID()
	{
		return this.strongID;
	}
	
	public void setCurrentTime()
	{
		this.time = TimeUtil.getCurrentTime();
	}
	
	// 返回计算次数
	public int getHeroCiShu()
	{
		long kc = TimeUtil.getCurrentTime();
		long t = kc - this.time;
		int count = 0;
		if(t<0){
			return count;
		}
		count =  (int) (t/TeamConstants.HERO_CD_TIME);
		if(count>0){
			this.setCurrentTime();
		}
		return count;
	}
}
