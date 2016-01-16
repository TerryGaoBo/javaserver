package com.dol.cdf.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.bean.Catchninja;
import com.dol.cdf.common.bean.Role;
import com.dol.cdf.common.bean.RoleGroup;
import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.bean.Training;
import com.dol.cdf.common.gamefunction.IEffectGF;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.activity.ActivityType;
import com.jelly.hero.Hero;

public class CharacterManager extends BaseConfigLoadManager {
			
	private final static String CONFIG_FILE_NAME = "Role.json";
	
	private final static String CONFIG_FILE_NAME_1 = "RoleGroup.json";
	
	//忍者培养配置文件
	private final static String CONFIG_FILE_NAME_TRAINING = "Training.json";

	private Map<Integer, Role> roleMap;
	
	private ArrayListMultimap<Integer, Role> gradeMap, actGradeMap;
	
	private Map<Integer, Training> trainingMap;
	List<RoleGroupWrapper> roleGroups;

	public Role getRoleById(int id) {
		return roleMap.get(id);
	}

	@Override
	public void loadConfig() {
		roleMap = new HashMap<Integer, Role>();
		gradeMap = ArrayListMultimap.create();
		actGradeMap = ArrayListMultimap.create();
		roleGroups = Lists.newArrayList();
		trainingMap = new HashMap<Integer, Training>();
		
		List<Role> loadSingleConfig = readConfigFile(CONFIG_FILE_NAME, new TypeReference<List<Role>>() {
		});
		for (Role role : loadSingleConfig) {
			roleMap.put(role.getId(), role);
			gradeMap.put(role.getQuality(), role);
		}
		
		String enstr = CONFIG_FILE_NAME_1;
		
		List<RoleGroup> rgList = readConfigFile(enstr, new TypeReference<List<RoleGroup>>() {
		});
		
		for (RoleGroup roleGroup : rgList) {
			roleGroups.add(new RoleGroupWrapper(roleGroup));
		}
		
		//忍者培养配置文件
		List<Training> trainingList = readConfigFile(CONFIG_FILE_NAME_TRAINING, new TypeReference<List<Training>>() {
		});
		
		for (Training training : trainingList) {
			trainingMap.put(training.getQuality(), training);
		}
		
		checkAllSKillExsitOrNot();
	}
	
	public ArrayListMultimap<Integer, Role> replaceConfig(Map<Integer, List<Integer>> newActGradeMap) {
		ArrayListMultimap<Integer, Role> oldActGradeMap = actGradeMap;
		actGradeMap.clear();
		for (Map.Entry<Integer, List<Integer>> entry : newActGradeMap.entrySet()) {
			Integer qual = entry.getKey();
			for (Integer rid : entry.getValue()) {
				if (rid != 0) {
					actGradeMap.put(qual, roleMap.get(rid));
				} else {
					actGradeMap.putAll(qual, gradeMap.get(qual));
				}
			}
		}
		return oldActGradeMap;
	}
	
	public void restoreConfig(ArrayListMultimap<Integer, Role> actGradeMap) {
		this.actGradeMap = actGradeMap;
	}
	
	private void checkAllSKillExsitOrNot() {
		SkillConfigManager skills = AllGameConfig.getInstance().skills;
		for (Role role : roleMap.values()) {
			for (int sid : role.getSkill()) {
				Skill skill = skills.getSkill(sid);
				if (skill == null) {
					logger.error("skill is not exsit. id = {}",sid);
				}
			}
			for (int sid : role.getEskill()) {
				Skill skill = skills.getSkill(sid);
				if (skill == null) {
					logger.error("eskill is not exsit. id = {}",sid);
				}
			}
		}
	}

	public Map<Integer, Role> getRoleMap() {
		return roleMap;
	}

	public List<Role> getRolesByGrade(int gradeType) {
		return gradeMap.get(gradeType);
	}
	
	public Training getTraining(int quality) {
		return trainingMap.get(quality);
	}

	
//	public Role getRndRole(int gradeType) {
//		List<Role> rolesByGrade = getRolesByGrade(gradeType);
//		int i = Rnd.get(rolesByGrade.size());
//		return rolesByGrade.get(i);
//	}
//	public Role getRndRole(int gradeType) {
//		if (ActivityType.RAFFLE_SCORE.isActive()) {
//			Catchninja actData = AllGameConfig.getInstance().activitys.getCatchNinja();
//			int npos = -1;
//			int[] actQuals = actData.getQuality();
//			for (int i = 0; i < actQuals.length; ++i) {
//				if (actQuals[i] == gradeType) {
//					npos = i;
//					break;
//				}
//			}
//			if (npos != -1) {
//				String[] v = actData.getNinjaID()[npos].split("|");
//				if (Integer.parseInt(v[0]) != 0) {
//					List<Role> actRoles = Lists.newArrayList();
//					for (String id : v) {
//						actRoles.add(roleMap.get(Integer.parseInt(id)));
//					}
//					return actRoles.get(Rnd.get(actRoles.size()));
//				}
//			}
//		}
//		List<Role> rolesByGrade = getRolesByGrade(gradeType);
//		int i = Rnd.get(rolesByGrade.size());
//		return rolesByGrade.get(i);
//	}
	public Role getRndRole(int gradeType) {
		if (ActivityType.RAFFLE_SCORE.isActive() 
			&& actGradeMap.containsKey(gradeType)) {
			List<Role> rolesByGrade = actGradeMap.get(gradeType);
			return rolesByGrade.get(Rnd.get(rolesByGrade.size()));
		}
		List<Role> rolesByGrade = getRolesByGrade(gradeType);
		return rolesByGrade.get(Rnd.get(rolesByGrade.size()));
	}

	public List<RoleGroupWrapper> getRoleGroups() {
		return roleGroups;
	}
	
	
	public static class RoleGroupWrapper{
		
		RoleGroup roleGroup;
		
		Integer hpMaxRatio;

		public RoleGroupWrapper(RoleGroup roleGroup) {
			super();
			this.roleGroup = roleGroup;
			IEffectGF[] alterSelf = roleGroup.getAlterSelf();
			if(alterSelf != null) {
				for (IEffectGF iEffectGF : alterSelf) {
					int type = (Integer)iEffectGF.getGfi().getParameter().getParamter("type");
					if(type == Hero.HP_MAX_RATIO) {
						int ratios = ((int[])iEffectGF.getGfi().getParameter().getParamter("ratios"))[0];
						hpMaxRatio = Math.abs(ratios);
					}
				}
			}
		}

		public Integer getHpMaxRatio() {
			return hpMaxRatio;
		}
		
		@Override
		public boolean equals(Object obj) {
			return roleGroup.equals(obj);
		}

		public Integer getId() {
			return roleGroup.getId();
		}

		public String getName() {
			return roleGroup.getName();
		}

		public String getDesc() {
			return roleGroup.getDesc();
		}

		public int[] getRoles() {
			return roleGroup.getRoles();
		}

		public IEffectGF[] getAlterSelf() {
			return roleGroup.getAlterSelf();
		}

		public IEffectGF[] getAlterTarget() {
			return roleGroup.getAlterTarget();
		}

		public Integer getSkill() {
			return roleGroup.getSkill();
		}

		@Override
		public int hashCode() {
			return roleGroup.hashCode();
		}

		public void setId(Integer id) {
			roleGroup.setId(id);
		}

		public void setName(String name) {
			roleGroup.setName(name);
		}

		public void setDesc(String desc) {
			roleGroup.setDesc(desc);
		}

		public void setRoles(int[] roles) {
			roleGroup.setRoles(roles);
		}

		public void setAlterSelf(IEffectGF[] alterSelf) {
			roleGroup.setAlterSelf(alterSelf);
		}

		public void setAlterTarget(IEffectGF[] alterTarget) {
			roleGroup.setAlterTarget(alterTarget);
		}

		public void setSkill(Integer skill) {
			roleGroup.setSkill(skill);
		}

		@Override
		public String toString() {
			return roleGroup.toString();
		}
		
	}
	

	
}
