package com.dol.cdf.common.config;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Activity;
import com.dol.cdf.common.bean.Catchninja;
import com.dol.cdf.common.bean.DayReward;
import com.dol.cdf.common.bean.NewActivity;
import com.dol.cdf.common.bean.Plan;
import com.dol.cdf.common.bean.Recharge;
import com.dol.cdf.common.bean.Vip;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.jelly.activity.ActivityType;
import com.jelly.activity.WorldActivity;

public class ActivityConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_FORM = "DayReward.json";
	private static final String JSON_FILE_FORM_1 = "Vip.json";
	private static final String JSON_FILE_FORM_1_IOS = "Vip_ios.json";
	private static final String JSON_FILE_FORM_2 = "Recharge.json";
	private static final String JSON_FILE_FORM_2_IOS = "Recharge_ios.json";
	
//	private static final String JSON_FILE_FORM_3 = "Activity.json";
	
	private static final String JSON_FILE_FROM_CATCHNINJA = "Catchninja.json";
	
	private static final String JSON_NEW_ACTIVITY_JSON = "NewActivity.json";
	private static final String JSON_PLAN_JSON="Plan.json";
	

	private Map<Integer, DayReward> dayRewardMap;
	private Map<Integer, Vip> vipMap;
	private List<Vip> vips;
	private Map<String, Recharge> rechargeMap;
	private Map<Integer, ActivityWrapper> activityMap;
	public List<Recharge> rechargeList;
	private Map<Integer, Catchninja> catchNinja;
	
	public DayReward getDayRewards(int id) {
		return dayRewardMap.get(id);
	}
	
	@Override
	public void loadConfig() {
		
		dayRewardMap = Maps.newHashMap();
		List<DayReward> list = readConfigFile(JSON_FILE_FORM, new TypeReference<List<DayReward>>() {});
		for (DayReward reward : list) {
			dayRewardMap.put(reward.getDay(), reward);
		}
		
		vipMap = Maps.newHashMap();
		vips = readConfigFile(AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE ? JSON_FILE_FORM_1_IOS 
				: JSON_FILE_FORM_1, new TypeReference<List<Vip>>() {});
		for (Vip vip : vips) {
			vipMap.put(vip.getId(), vip);
		}
		
		rechargeMap = Maps.newHashMap();
		rechargeList = readConfigFile(AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE ? 
				JSON_FILE_FORM_2_IOS : JSON_FILE_FORM_2, new TypeReference<List<Recharge>>() {});
		for (Recharge recharge : rechargeList) {
			rechargeMap.put(recharge.getId(), recharge);
		}
		
		catchNinja = Maps.newHashMap();
		List<Catchninja> list4 = readConfigFile(JSON_FILE_FROM_CATCHNINJA,
				new TypeReference<List<Catchninja>>() {
				});
		for (Catchninja cn : list4) {
			catchNinja.put(cn.getId(), cn);
		}
		
		
		activityMap = Maps.newHashMap();
//		String fileName = JSON_FILE_FORM_3;
//		List<Activity> list3 = readConfigFile(fileName, new TypeReference<List<Activity>>() {});
//		for (Activity activity : list3) {
//			ActivityWrapper activityWrapper = new ActivityWrapper(activity);
//			activityWrapper.setShowTrigger();
//			activityWrapper.setStartTrigger();
//			activityWrapper.setEndTrigger();
//			activityWrapper.setShowAndEndDate();
//			activityMap.put(activity.getId(), activityWrapper);
//		}
		
		Map<Integer, NewActivity> actMap = Maps.newHashMap();
		List<NewActivity> listAct = readConfigFile(JSON_NEW_ACTIVITY_JSON, new TypeReference<List<NewActivity>>() {});
		for(NewActivity act : listAct){
			actMap.put(act.getId(), act);
		}
		
		List<Plan> listPlan = readConfigFile(JSON_PLAN_JSON, new TypeReference<List<Plan>>() {});
		for (Plan plan : listPlan){
			if(plan.getType() != 1){
				continue;
			}
			
			if(plan.getActivity_gift_id() == null || plan.getActivity_gift_id().length ==0){
				continue;
			}
			
			for(int ts = 0;ts<plan.getActivity_gift_id().length;ts++){
				int id = plan.getActivity_gift_id()[ts];
				NewActivity nact = actMap.get(id);
				
				Activity activity = new Activity();
				activity.setId(nact.getId());
				activity.setClose(plan.getClose());
				activity.setStartDay(plan.getStartDay());
				activity.setEndDay(plan.getEndDay());
				activity.setStartTime(stringRevertFunc(plan.getStartTime()));
				activity.setEndTime(stringRevertFunc(plan.getEndTime()));
				activity.setShowTime(stringRevertFunc(plan.getShowTime()));
				
				activity.setName(nact.getName());
				activity.setSend(nact.getSend());
				activity.setContent(nact.getContent());
				activity.setTypes(nact.getTypes());
				activity.setValues(nact.getValues());
				activity.setItem(nact.getItem());
				activity.setMailText(nact.getMailText());
				activity.setOldOpen(nact.getOldOpen());
				
				ActivityWrapper activityWrapper = new ActivityWrapper(activity);
				activityWrapper.setShowTrigger();
				activityWrapper.setStartTrigger();
				activityWrapper.setEndTrigger();
				activityWrapper.setShowAndEndDate();
				activityMap.put(activity.getId(), activityWrapper);
			}
		}
	}
	
	private String stringRevertFunc(String value)
	{
		if(value == null){
			return null;
		}
		String kk = "";
		if(value.equals("")){
			return kk;
		}
		
		String [] sk = value.split(" ");
		for(int i = sk.length-1;i>=0;i--){
			if(sk[i].equals("")){
			}else{
				kk = kk+sk[i] + " ";
			}
		}
		kk = kk.trim();
		return kk;
	}
	
	public Catchninja getCatchNinja() {
		return catchNinja.get(ActivityType.RAFFLE_SCORE.getActId());
	}

	public Collection<ActivityWrapper> getActivitys() {
		return activityMap.values();
	}
	
	public ActivityWrapper getActivity(int id) {
		return activityMap.get(id);
	}
	
	public Recharge getRecharge(String itemId) {
		return rechargeMap.get(itemId);
	}
	
	public Recharge getRechargeMP() {
		for (Recharge r : rechargeMap.values()) {
			if (r.getDays()!=null &&r.getDays().intValue() > 0)
				return r;
		}
		return null;
	}
	
	public Vip getVip(int id) {
		return vipMap.get(id);
	}
	
	public int getVipLevelByScore(int score) {
//		if (score < vips.get(0).getScore() ) {
//			return 0;
//		}
		if (score >= vips.get(vips.size() -1).getScore() ) {
			return vips.get(vips.size() -1).getId();
		}
		for (int i = 0; i < vips.size() -1; i++) {
			int next = i+1;
			if (score >= vips.get(i).getScore() && score < vips.get(next).getScore()) {
				return vips.get(i).getId();
			}
		}
		return 0;
	}
	
	public int getRaffleHeroScore(int raffleSolution) {			
		int sr = 0;
		if (ActivityType.RAFFLE_SCORE.isActive()) {
			sr = getCatchNinja().getIntegral()[raffleSolution - 1];
		}
		return sr;
	}
	
	public static class ActivityWrapper{
		
		private Activity activity;

		private CronTrigger showTrigger;
		
		private CronTrigger startTrigger;
		
		private Date showDate;
		
		private CronTrigger endTrigger;
		
		private Date compareShowDate;
		 
		private Date endDate;
		
		private Date startDate;
		
		private String startDayString;
		
		private String endDayString;

		public Date getEndDate() {
			return endDate;
		}
		
		public Date getShowDate() {
			return showDate;
		}
		
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
			this.startDayString = TimeUtil.formatChineseTime(startDate);
		}
		
		public Date getStartDate() {
			return startDate;
		}
		
		public void setShowDate(Date date) {
			this.showDate = date;
		}
		
		
		public void setEndDate(Date date) {
			this.endDate = date;
			this.endDayString = TimeUtil.formatChineseTime(endDate);
		}
		
		public void setShowAndEndDate() {
			if(getShowTime() == null) return;
			if(getShowTime().endsWith("?")) {
				if(getShowTime().endsWith("* ?")) {
					this.compareShowDate = WorldActivity.COMPATE_MONTH;
				}else {
					this.compareShowDate = WorldActivity.COMPATE_YEAR;
				}
				
			}else {
				this.compareShowDate = WorldActivity.COMPATE_DAY;
			}
			showTrigger.setStartTime(compareShowDate);
			this.showDate = showTrigger.getFireTimeAfter(compareShowDate);
			startTrigger.setStartTime(compareShowDate);
			this.startDate = startTrigger.getFireTimeAfter(compareShowDate);
			endTrigger.setStartTime(compareShowDate);
			this.endDate = endTrigger.getFireTimeAfter(compareShowDate);
			this.startDayString = TimeUtil.formatChineseTime(getStartDate());
			this.endDayString = TimeUtil.formatChineseTime(getEndDate());
		}

		public String getStartDayString() {
			return startDayString;
		}
		
		public String getEndDayString() {
			return endDayString;
		}
		
		public void setShowTrigger() {
			String showTime = getShowTime();
			if(showTime == null) return;
			CronTrigger trigger = new CronTrigger();
			trigger.setGroup(Scheduler.DEFAULT_GROUP);
			trigger.setJobGroup(Scheduler.DEFAULT_GROUP);
			try {
				trigger.setCronExpression(showTime);
			} catch (ParseException e) {
				logger.error("",e);
			}
			this.showTrigger = trigger;
		}
		
		public void setEndTrigger() {
			String endTime = getEndTime();
			if(endTime == null) return;
			CronTrigger trigger = new CronTrigger();
			trigger.setGroup(Scheduler.DEFAULT_GROUP);
			trigger.setJobGroup(Scheduler.DEFAULT_GROUP);
			try {
				trigger.setCronExpression(endTime);
			} catch (ParseException e) {
				logger.error("",e);
			}
			this.endTrigger = trigger;
		}
		
		public void setStartTrigger() {
			String startTime = getStartTime();
			if(startTime == null) return;
			CronTrigger trigger = new CronTrigger();
			trigger.setGroup(Scheduler.DEFAULT_GROUP);
			trigger.setJobGroup(Scheduler.DEFAULT_GROUP);
			try {
				trigger.setCronExpression(startTime);
			} catch (ParseException e) {
				logger.error("",e);
			}
			this.startTrigger = trigger;
		}

		public ActivityWrapper(Activity activity) {
			this.activity = activity;
		}

		@Override
		public boolean equals(Object obj) {
			return activity.equals(obj);
		}

		public Integer getId() {
			return activity.getId();
		}

		public String getName() {
			return activity.getName();
		}

		public Integer getClose() {
			return activity.getClose();
		}

		public String getShowTime() {
			return activity.getShowTime();
		}

		public Integer getSend() {
			return activity.getSend();
		}

		public String getStartTime() {
			return activity.getStartTime();
		}

		public String getEndTime() {
			return activity.getEndTime();
		}

		public String getContent() {
			return activity.getContent();
		}

		public int[] getTypes() {
			return activity.getTypes();
		}

		public String[] getValues() {
			return activity.getValues();
		}

		public String[] getItem() {
			return activity.getItem();
		}

		public int[] getMailText() {
			return activity.getMailText();
		}

		public Integer getStartDay() {
			return activity.getStartDay();
		}

		public void setStartDay(Integer startDay) {
			activity.setStartDay(startDay);
		}

		public Integer getEndDay() {
			return activity.getEndDay();
		}

		public void setEndDay(Integer endDay) {
			activity.setEndDay(endDay);
		}

		@Override
		public int hashCode() {
			return activity.hashCode();
		}

		public void setId(Integer id) {
			activity.setId(id);
		}

		public void setName(String name) {
			activity.setName(name);
		}

		public void setClose(Integer close) {
			activity.setClose(close);
		}

		public void setShowTime(String showTime) {
			activity.setShowTime(showTime);
		}

		public void setSend(Integer send) {
			activity.setSend(send);
		}

		public void setStartTime(String startTime) {
			activity.setStartTime(startTime);
		}

		public void setEndTime(String endTime) {
			activity.setEndTime(endTime);
		}

		public void setContent(String content) {
			activity.setContent(content);
		}

		public void setTypes(int[] types) {
			activity.setTypes(types);
		}

		public void setValues(String[] values) {
			activity.setValues(values);
		}

		public void setItem(String[] item) {
			activity.setItem(item);
		}

		public void setMailText(int[] mailText) {
			activity.setMailText(mailText);
		}
		
		public Integer getOldOpen(){
			 return activity.getOldOpen();
		}

		@Override
		public String toString() {
			return activity.toString();
		}
		
	}

}