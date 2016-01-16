package com.jelly.hero;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.config.AllGameConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Monster extends BaseHero {

	List<BaseSkill> skills;
	
	public Monster(int roleId) {
		super(roleId);
		this.skills = Lists.newArrayList();
		for (int sid : this.getEskill()) {
			Skill skill = AllGameConfig.getInstance().skills.getSkill(sid);
			this.skills.add(new BaseSkill(skill));
		}
	}

	@Override
	public List<BaseSkill> getSkills() {
		return this.skills;
	}

	@Override
	public Map<Integer, Integer> getEquipRefineProps() {
		return Maps.newHashMap();
	}

}
