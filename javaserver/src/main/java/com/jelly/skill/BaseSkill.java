package com.jelly.skill;

import com.dol.cdf.common.bean.Skill;
import com.jelly.player.IFighter;

public abstract class BaseSkill implements ISkill{

	@Override
	public void onEvent(int eventType) {
		
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFighter getOwner() {
		return null;
	}

	@Override
	public void setOwner(IFighter pet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGet() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoss() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPercent() {
		return getSkillConfig().getProbability();
	}

	@Override
	public Skill getSkillConfig() {
		return null;
	}

}
