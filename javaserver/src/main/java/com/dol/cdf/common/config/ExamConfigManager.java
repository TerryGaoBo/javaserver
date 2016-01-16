package com.dol.cdf.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.bean.Exam;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.jelly.activity.ActivityType;
import com.jelly.hero.IHero;
import com.jelly.hero.Monster;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.player.IFighter;

public class ExamConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_FORM = "Exam.json";

	private Map<Integer, Exam> examMap;
	
	public Exam getExam(int id) {
		return examMap.get(id);
	}
	
	@Override
	public void loadConfig() {
		examMap = new HashMap<Integer, Exam>();
		List<Exam> list = readConfigFile(JSON_FILE_FORM, new TypeReference<List<Exam>>() {
		});
		for (Exam exam : list) {
			examMap.put(exam.getId(), exam);
		}
	}
	
//	/**
//	 * 获得所有的经验
//	 * @param level 当前通过的层
//	 * @return
//	 */
//	public int getAllExp(int level) {
//		int sum = 0;
//		if (examMap.get(level) == null) {
//			return sum;
//		}
//		for (int i = 1; i <= level; i++) {
//			Integer exp = examMap.get(i).getExp();
//			sum += exp;
//		}
//		return sum;
//	}
	
	public int getExamRxp(int level) {
		if (examMap.get(level) == null) {
			return 0;
		}
		Integer rxp = examMap.get(level).getRxp();
		float muti = Float.parseFloat(ActivityType.EXAM_MUTI_EARN.getValue());
		int real =(int) (rxp * muti);
		return real;
	}
	
	public int getExamSilver(int level) {
		if (examMap.get(level) == null) {
			return 0;
		}
		Integer silver = examMap.get(level).getSilver();
		float muti = Float.parseFloat(ActivityType.EXAM_MUTI_EARN.getValue());
		int real =(int) (silver * muti);
		return real;
	}
	
	public int getExamExp(int level) {
		if (examMap.get(level) == null) {
			return 0;
		}
		Integer exp = examMap.get(level).getExp();
		float muti = Float.parseFloat(ActivityType.EXAM_MUTI_EARN.getValue());
		int real =(int) (exp * muti);
		return real;
	}
	/**
	 * 获得道具
	 * @param level 当前通过的层
	 * @return
	 */
	public VariousItemEntry getItem(int level) {
		Exam exam = examMap.get(level);
		if (exam == null) {
			return null;
		}
		if (exam.getGroupId() == null) {
			return null;
		}
		float rate = exam.getRate()*Float.parseFloat(ActivityType.EXAM_MUTI_RATE.getValue());
		if (!Rnd.getRandomPercent(rate)) {
			return null;
		}
		VariousItemEntry item = AllGameConfig.getInstance().drops.getVariousItemByGroupId(exam.getGroupId());
		return item;
	}
	
	public DefenderGroup getDefenderGroup(int level) {
		Exam exam = examMap.get(level);
		List<IFighter> fighters = Lists.newArrayList();
		IHero enemy = new Monster(exam.getRid());
		enemy.setLevel(exam.getLevel());
		fighters.add(new BaseFighter(enemy));
		return new DefenderGroup(fighters);
	}
	
	


}