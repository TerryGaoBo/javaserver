package com.jelly.skill;

import com.dol.cdf.common.bean.Skill;
import com.jelly.combat.event.ICombatEventHandler;
import com.jelly.player.IFighter;

public interface ISkill extends ICombatEventHandler {

	
	public int getId();
	
	public IFighter getOwner();

	public void setOwner(IFighter pet);

	/**
	 * 获得
	 */
	public void onGet();

	/**
	 * 失去
	 */
	public void onLoss();
	
	public Skill getSkillConfig();
	
	public void setSkillConfig(Skill skill);
	
	public int getPercent();
	
}
