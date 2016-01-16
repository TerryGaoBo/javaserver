package com.dol.cdf.common.config;

import io.nadron.app.Player;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Catchninja;
import com.dol.cdf.common.bean.QualityRate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.jelly.activity.ActivityType;
import com.jelly.player.GradeType;

public class QualityRateConfigManager extends BaseConfigLoadManager {

	public static final int HERO_NORMAL = 1;
	public static final int HERO_MID = 2;
	public static final int HERO_HIGH = 3;
	public static final int FIRST_HERO_MID = 4;
	public static final int FIRST_HERO_HIGH = 5;
	public static final int STUDY_NINJITSU_SKILL_NORMAL = 11;
	public static final int STUDY_NINJITSU_SKILL_HIGH = 12;
	public static final int STUDY_PSYCHIC_SKILL_NORMAL = 13;
	public static final int STUDY_PSYCHIC_SKILL_HIGH = 14;
	public static final int QUEST = 20;
	public static final int BEAST = 21;

	public static final int MAX_RATE = 10000;

	private Map<Integer, ImmutableRangeMap<Integer, List<Integer>>> gradeRates;
	
	private Map<Integer,QualityRate> qualityRateMap;

	@Override
	public void loadConfig() {
		gradeRates = Maps.newHashMap();
		qualityRateMap = Maps.newHashMap();
		List<QualityRate> list = readConfigFile("QualityRate.json", new TypeReference<List<QualityRate>>() {
		});
		ImmutableRangeMap.Builder<Integer, List<Integer>> builder = ImmutableRangeMap.builder();
		Integer currentType = 0;
		for (QualityRate h : list) {
			if (h.getType() != currentType && currentType != 0) {
				gradeRates.put(currentType, builder.build());
				builder = ImmutableRangeMap.builder();
			}
			Range<Integer> range = Range.closed(h.getMinLv(), h.getMaxLv());
			builder.put(range, Lists.newArrayList(h.getDrate(), h.getCrate(), h.getBrate(), h.getArate(), h.getSrate(), h.getSsrate()));
			currentType = h.getType();
			qualityRateMap.put(h.getType(), h);
		}
		gradeRates.put(currentType, builder.build());
		getBeastRates(1);
	}
	
	public void replaceConfig(Map<Integer, ImmutableRangeMap<Integer, List<Integer>>> gradeRates, 
			Map<Integer,QualityRate> qualityRateMap) {
		this.gradeRates = gradeRates;
		this.qualityRateMap = qualityRateMap;
	}
	
	public Map<Integer, ImmutableRangeMap<Integer, List<Integer>>> getGradeRates() {
		return this.gradeRates;
	}
	
	public Map<Integer, QualityRate> getQualityRateMap() {
		return this.qualityRateMap;
	}

	public QualityRate getQualityRateByType(int type) {
		return qualityRateMap.get(type);
	}
	
	public int heroNormalGrade(int buildingLv, Player player,boolean cding) {
		if(!cding){
			return rndGradeWithDelaFreeLuckFunc(HERO_NORMAL, buildingLv, 1, player);
		}
		return rndGrade(HERO_NORMAL, buildingLv, player);
	}

	public int heroMidGrade(int buildingLv, Player player,boolean cding) {
		float muti = Float.parseFloat(ActivityType.RAFF_MUTI_2.getValue());
		if(!cding){
			return rndGradeWithDelaFreeLuckFunc(HERO_MID, buildingLv, muti, player);
		}
		return rndGradeWithDelta(HERO_MID, buildingLv, muti, player);
	}

	public int heroHighGrade(int buildingLv, Player player) {
		return rndGrade(HERO_HIGH, buildingLv, player);
	}

	public int firstHeroHgihGrade(int buildingLv, Player player) {
		return rndGrade(FIRST_HERO_HIGH, buildingLv, player);
	}

	public int firstHeroMidGrade(int buildingLv, Player player) {
		return rndGrade(FIRST_HERO_MID, buildingLv, player);
	}

	public int studyNinjitsuSkillNormalGrade(int buildingLv, Player player) {
		float muti = Float.parseFloat(ActivityType.NIN_MUTI_0.getValue());
		return rndGradeWithDelta(STUDY_NINJITSU_SKILL_NORMAL, buildingLv, muti, player);
	}

	public int studyNinjitsuSkillHighGrade(int buildingLv, Player player) {
		float muti = Float.parseFloat(ActivityType.NIN_MUTI_1.getValue());
		return rndGradeWithDelta(STUDY_NINJITSU_SKILL_HIGH, buildingLv,muti, player);
	}

	public int studyPsychicSkillNormalGrade(int buildingLv, Player player) {
		return rndGrade(STUDY_PSYCHIC_SKILL_NORMAL, buildingLv, player);
	}

	public int studyPsychicSkillHighGrade(int buildingLv, Player player) {
		return rndGrade(STUDY_PSYCHIC_SKILL_HIGH, buildingLv, player);
	}

	public int questGrade(int buildingLv, Player player) {
		return rndGrade(QUEST, buildingLv, player);
	}

	public List<Integer> getBeastRates(int buildingLv) {
		List<Integer> qrate = gradeRates.get(BEAST).get(buildingLv);
		return qrate;
	}

	private int rndGrade(int type, int buildingLv, Player player) {
		return rndGradeWithDelta(type, buildingLv, 1, player);
	}

	private int rndGradeWithDelta(int type, int buildingLv, float muti, Player player) {
		List<Integer> qrate = gradeRates.get(type).get(buildingLv);
		if (qrate == null) {
			logger.error("building id = {} herorate is null", buildingLv);
			return -1;
		}
		qrate = changeRateWithLuck(type, qrate, player);
		List<Integer> rates = resetQualityRate(qrate, muti);
		
		// 0-99
		int i = Rnd.get(MAX_RATE);
		for (int j = 0; j < rates.size(); j++) {
			Integer rate = rates.get(j);
			if (rate != null && rate > i) {
				int hitQuality = j + 1;
				player.getProperty().resetLuckStep(type, hitQuality);
				if(logger.isDebugEnabled()) {
					logger.debug("hitQuality:{}, type:{}",hitQuality,type);
				}
				return hitQuality;
			}
		}
		return -1;
	}
	
	private int rndGradeWithDelaFreeLuckFunc(int type, int buildingLv, float muti, Player player) {
		List<Integer> qrate = gradeRates.get(type).get(buildingLv);
		if (qrate == null) {
			logger.error("building id = {} herorate is null", buildingLv);
			return -1;
		}
		qrate = changeRateWithLuckFreeFunc(type, qrate, player);
		List<Integer> rates = resetQualityRate(qrate, muti);
		
		// 0-99
		int i = Rnd.get(MAX_RATE);
		for (int j = 0; j < rates.size(); j++) {
			Integer rate = rates.get(j);
			if (rate != null && rate > i) {
				int hitQuality = j + 1;
				player.getProperty().resetLuckStep(type, hitQuality);
				if(logger.isDebugEnabled()) {
					logger.debug("hitQuality:{}, type:{}",hitQuality,type);
				}
				return hitQuality;
			}
		}
		return -1;
	}
	
	private List<Integer> changeRateWithLuckFreeFunc(int type, List<Integer> rates,
			Player player) {
		QualityRate qualityRate = qualityRateMap.get(type);
		if (logger.isDebugEnabled()) {
			logger.debug("before change with luck rates : {}", rates);
		}
		List<Integer> newRates = Lists.newArrayList(rates);
		int rateMaxQuality = getRateMaxQuality(newRates);
		if (qualityRate != null && qualityRate.getLuckQuality() != null) {
			int i = 0;
			for (Integer quality : qualityRate.getLuckQuality()) {
				if (quality > rateMaxQuality) {
					break;
				}
//				player.getProperty().increaseLuckStep(type, quality,
//						qualityRate.getLuckStep()[i]);
				int luckQualityStep = player.getProperty().getLuckQualityStep(
						type, quality);
				if (logger.isDebugEnabled()) {
					logger.debug("player luck step : {}, quality:{}, type:{}",
							luckQualityStep, quality, type);
				}
				if (luckQualityStep > qualityRate.getLuckStart()[i]) {
					int luckDelta = qualityRate.getLuckValue()[i]
							* (luckQualityStep - qualityRate.getLuckStart()[i]);
					for (int j = 0; j < quality - 1; j++) {
						Integer rate = newRates.get(j);
						if (rate != null) {
							if (rate == MAX_RATE)
								continue;
							int tarRate = rate - luckDelta;
							if (tarRate < 0) {
								tarRate = 0;
							}
							newRates.set(j, tarRate);
						}
					}
				}
				i++;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("after change with luck rates : {}", newRates);
		}
		return newRates;
	}
	
	private List<Integer> changeRateWithLuck(int type, List<Integer> rates,
			Player player) {
		QualityRate qualityRate = qualityRateMap.get(type);
		if (logger.isDebugEnabled()) {
			logger.debug("before change with luck rates : {}", rates);
		}
		List<Integer> newRates = Lists.newArrayList(rates);
		int rateMaxQuality = getRateMaxQuality(newRates);
		if (qualityRate != null && qualityRate.getLuckQuality() != null) {
			int i = 0;
			for (Integer quality : qualityRate.getLuckQuality()) {
				if (quality > rateMaxQuality) {
					break;
				}
				player.getProperty().increaseLuckStep(type, quality,
						qualityRate.getLuckStep()[i]);
				int luckQualityStep = player.getProperty().getLuckQualityStep(
						type, quality);
				if (logger.isDebugEnabled()) {
					logger.debug("player luck step : {}, quality:{}, type:{}",
							luckQualityStep, quality, type);
				}
				if (luckQualityStep > qualityRate.getLuckStart()[i]) {
					int luckDelta = qualityRate.getLuckValue()[i]
							* (luckQualityStep - qualityRate.getLuckStart()[i]);
					for (int j = 0; j < quality - 1; j++) {
						Integer rate = newRates.get(j);
						if (rate != null) {
							if (rate == MAX_RATE)
								continue;
							int tarRate = rate - luckDelta;
							if (tarRate < 0) {
								tarRate = 0;
							}
							newRates.set(j, tarRate);
						}
					}
				}
				i++;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("after change with luck rates : {}", newRates);
		}
		return newRates;
	}
	
	private int getRateMaxQuality(List<Integer> rates) {
		for (int i = rates.size() - 1; i >= 0 ; i--) {
			Integer rate = rates.get(i);
			if(rate != null && rate == MAX_RATE) {
				return i + 1;
			}
		}
		return 1;
	}

	private List<Integer> resetQualityRate(List<Integer> qrates, float muti) {
		if (muti == 1.0f) {
			return qrates;
		}
		List<Integer> list = Lists.newArrayListWithCapacity(qrates.size());
		int endChangeIdx = 0;
		if (qrates.size() >= GradeType.S.getId()) {
			endChangeIdx = GradeType.A.getId() - 1;
		} else if (qrates.size() >= GradeType.A.getId()) {
			endChangeIdx = GradeType.A.getId() - 2;
		}
		for (int i = 0; i < qrates.size(); i++) {
			if (i <= endChangeIdx) {
				Integer rate = qrates.get(i);
				Integer val = null;
				if (rate != null) {
					val = MAX_RATE - (int) (((MAX_RATE - rate) * muti));
				}
				
				list.add(val);
			} else {
				list.add(qrates.get(i));
			}
		}
		logger.info("quality, rate change list:{}", list);
		return list;

	}

	
//	public static void main(String[] args) {
//		for (int i = 1; i < 100; ++i) {
//			System.out.println(Range.closed(0, 99));
//		}
//	}
}
