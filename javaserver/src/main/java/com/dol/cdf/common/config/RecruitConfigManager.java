package com.dol.cdf.common.config;

import java.util.List;

import com.dol.cdf.common.bean.Recruit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jelly.hero.IHero;
import com.jelly.hero.Monster;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.player.IFighter;

public class RecruitConfigManager extends BaseConfigLoadManager {
	
	List<Recruit> list;
	@Override
	public void loadConfig() {
		list = readConfigFile("Recruit.json", new TypeReference<List<Recruit>>() {
		});
	}

	public Recruit getRecruit(int idx) {
		Preconditions.checkElementIndex(idx, list.size());
		return list.get(idx);
	} 
	
	public DefenderGroup getDefenderGroup(int idx) {
		Recruit recruit = getRecruit(idx);
		List<IFighter> fighters = Lists.newArrayList();
		IHero enemy = new Monster(recruit.getId());
		enemy.setLevel(recruit.getLevel());
		fighters.add(new BaseFighter(enemy));
		return new DefenderGroup(fighters);
	}
}
