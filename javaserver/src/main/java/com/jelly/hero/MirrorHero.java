package com.jelly.hero;

import java.util.List;
import java.util.Map;

public class MirrorHero implements IHero{

	Hero hero;
	
	int hp;
	
	int hpMax;
	
	float ratio;
	
	int uuid;
	
	int defineID;
	String defineGuid;
	
	
	public MirrorHero(Integer hid, Hero hero,int hp, int hpMax, float ratio) {
		try {
			this.hero = (Hero) hero.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		this.uuid = hid;
		this.hp = hp;
		this.hpMax = hpMax;
		this.ratio = ratio;
	}
	
	@Override
	public int getHpMax() {
		return hpMax;
	}
	
	
	
	@Override
	public int getStrength() {
		int strength = hero.getStrength();
		return (int)(strength * ratio);
	}
	
	@Override
	public int getAgility() {
		int agility  = hero.getAgility();
		return (int)(agility * ratio);
	}
	
	@Override
	public int getDefence() {
		int defence =  hero.getDefence();
		return (int)(defence*ratio);
	}

	@Override
	public int getRoleId() {
		return hero.getRoleId();
	}

	@Override
	public int getLevel() {
		return hero.getLevel();
	}

	@Override
	public void setLevel(int level) {
		
	}

	@Override
	public List<BaseSkill> getSkills() {
		return hero.getSkills();
	}

	@Override
	public int getTop() {
		return hero.getTop();
	}

	@Override
	public int getLeft() {
		return hero.getLeft();
	}

	@Override
	public int getRight() {
		return hero.getRight();
	}

	@Override
	public int getBottom() {
		return hero.getBottom();
	}

	@Override
	public int getQuality() {
		return hero.getQuality();
	}

	@Override
	public int getPower() {
		return hero.getPower();
	}

	@Override
	public int[] getEskill() {
		return hero.getEskill();
	}

	@Override
	public String getName() {
		return hero.getName();
	}

	@Override
	public int getUuid() {
		return uuid;
	}

	@Override
	public int getHp() {
		return hp;
	}

	@Override
	public int[] getChakra() {
		
		return hero.getChakra();
	}

	@Override
	public Map<Integer, Integer> getEquipRefineProps() {
		return this.hero.getEquipRefineProps();
	}
	
	@Override
	public int getDefineID()
	{
		return defineID;
	}
	
	@Override
	public void setDefineID(int id)
	{
		defineID = id;
	}
	@Override
	public String getDefineGuid()
	{
		return defineGuid;
	}
	@Override
	public void setDefineGuid(String guid)
	{
		defineGuid = guid;
	}
}
