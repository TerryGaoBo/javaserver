package com.dol.cdf.common.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Skill;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.jelly.player.GradeType;

public class SkillConfigManager extends BaseConfigLoadManager {

	private Map<Integer, Skill> skills;

	Map<Integer, ArrayListMultimap<Integer, Skill>> maps;

	@Override
	public void loadConfig() {
		skills = Maps.newHashMap();
		maps = Maps.newHashMap();
		List<Skill> list = readConfigFile("Skill.json", new TypeReference<List<Skill>>() {
		});
		Multimap<Integer, Skill> gradeSkilMultimap = LinkedHashMultimap.create();
		for (Skill skill : list) {
			skills.put(skill.getId(), skill);
			if(skill.getQuality() != null) {
				gradeSkilMultimap.put(skill.getQuality(), skill);
			}
		}
		for (Integer grade : gradeSkilMultimap.keySet()) {
			Collection<Skill> msSkills = gradeSkilMultimap.get(grade);
			ArrayListMultimap<Integer, Skill> typeMapsMultimap = ArrayListMultimap.create();
			for (Skill skill : msSkills) {
				typeMapsMultimap.put(skill.getType(), skill);
			}
			maps.put(grade, typeMapsMultimap);
		}
	}

	public Map<Integer, Skill> getSkills() {
		return skills;
	}

	public Skill getSkill(int id) {
		return skills.get(id);
	}

	public Skill getRndSkill(int gradeType, int skillType, List<Integer> owendSkills) {
		List<Skill> target = maps.get(gradeType).get(skillType);
		if (owendSkills == null) {
			owendSkills = Collections.EMPTY_LIST;
		}
		int rndIdx = Rnd.get(target.size());
		Skill skill = target.get(rndIdx);
		if (!owendSkills.contains(skill.getId())) {
			return skill;
		}
		for (int i = rndIdx+1; i < target.size(); i++) {
			skill = target.get(i);
			if (!owendSkills.contains(skill.getId())) {
				return skill;
			}
		}
		for (int i = 0; i < rndIdx; i++) {
			skill = target.get(i);
			if (!owendSkills.contains(skill.getId())) {
				return skill;
			}
		}
		return skill;
		
	}
	
	public static final int SKILL_C = 5065;
	public static final int SKILL_B = 5061;
	public static final int SKILL_A = 5062;
	public static final int SKILL_S = 5063;
	public static final int SKILL_SS = 5064;
	
	public int getSkillGradeType(int id) {
		switch (id) {
		case SKILL_C:
			return GradeType.C.getId();
		case SKILL_B:
			return GradeType.B.getId();
		case SKILL_A:
			return GradeType.A.getId();
		case SKILL_S:
			return GradeType.S.getId();
		case SKILL_SS:
			return GradeType.SS.getId();
		}
		return 0;
	}
	
	public Skill getRndAttackSkill(int gradeType, int skillType) {
		List<Skill> gradeSkills = maps.get(gradeType).get(skillType);
		for (;;) {
			Skill skill = gradeSkills.get(Rnd.get(gradeSkills.size()));
			if (skill.getRefine() > 0) {
				return skill;
			}
		}
	}
	
	public static void main(String[] args) {
		for (int x = 0; x < 100;x++) {
			List<Integer> owendSkills = Lists.newArrayList();
			owendSkills.add(1125);
			for (int i = 0; i < 3; i++) {
				Skill rndSkill = AllGameConfig.getInstance().skills.getRndSkill(3,0, owendSkills);
				owendSkills.add(rndSkill.getId());
				System.out.println(rndSkill.getId());
			}
			System.out.println();
		}
		
		
	}
	
	/**
	 * 根据技能品质随机出来可精练的技能
	 * @param gradeType
	 * @return
	 */
	public Skill getRndAttackSkill(int gradeType) {
		ArrayListMultimap<Integer, Skill> typeMapsMultimap = maps.get(gradeType);
		List<Skill> skillList = Lists.newArrayList();
		for(Skill skill:typeMapsMultimap.values()){
			if(skill.getRefine()==1){
				skillList.add(skill);
			}
		}
		Skill skill = skillList.get(Rnd.get(skillList.size()));
		return skill;
	}

}
