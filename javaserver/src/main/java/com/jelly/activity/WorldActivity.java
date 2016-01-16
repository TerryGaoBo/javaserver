package com.jelly.activity;

import io.nadron.app.Player;
import io.nadron.app.Task;
import io.nadron.service.TaskManagerService;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.ActivityConfigManager;
import com.dol.cdf.common.config.ActivityConfigManager.ActivityWrapper;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.GiftConfigManager.GiftInfo;
import com.dol.cdf.common.constant.GameConstId;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicLongMap;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.rank.GameRankMaster;

public class WorldActivity {

	private static final Logger logger = LoggerFactory.getLogger(WorldActivity.class);

	private static final String ACT_ID_KEY = "actId";

	public static final int MAX_BEAST_ROOM_PLAYERS = 20;
	public static final int MAX_SHOW_ACTS = 20;

	TaskManagerService taskManagerService;

	public static Date COMPATE_YEAR = DateUtils.truncate(new Date(), Calendar.YEAR);
	
	public static Date COMPATE_MONTH = DateUtils.truncate(new Date(), Calendar.MONTH);
	
	public static Date COMPATE_DAY = TriggerUtils.getDateOf(0, 0, 0);
	
	/**
	 * 服务器正在进行中得活动
	 */
	public static Set<Integer> runningActivities;

	/**
	 * 服务器正在进行中得活动
	 */
	public static Set<Integer> stoppedActivities;

	public static Set<ActivityType> activityTypes;

	Scheduler scheduler;
	SchedulerFactoryBean schedulerFactoryBean;
	/**
	 * 显示带顺序的活动列表
	 */
	List<ActivityWrapper> showActList;
	/**
	 * 尾兽造成伤害key name
	 */
	AtomicLongMap<String> beastActHurtsMap;

	TreeMap<String, Long> sortedActPlayerMap;

	/**
	 * 尾兽活动名次 key name
	 */
	Map<String, Integer> beastActOrderMap;

	ScheduledFuture<?> beastScheduledFuture;
	/**
	 * 玩家充值数据 key guid
	 */
	static AtomicLongMap<String> payMap;

	private Multimap<Integer, String> leveledWars;

	private Map<String, Integer> warPlayerInfos;

	ActivityJsonEntity actEntity;
	
	public static Set<GiftType> activeGiftTypes;
	
	public static Map<Integer, GiftType> actGiftMap;
	
	public static Date serverStartDate;

	// 幸运奖钨金钢
	public static final String luckyReward = "<6007;1>";
	public static final int luckyPersionCount = 10;
	public static final String top1_reward = "<6008;2>";
	public static final String top5_reward = "<6008;1>";
	public static String top50_reward = "<6006;1>";
	public static String top50ItemNum = "1";
	public static String top50ItemName = "";
	public static String top1ItemName = "";
	public static String top1ItemNum = "1";
	public static String top5ItemName = "";
	public static String top5ItemNum = "1";
	public static String luckyItemName = "";
	public static String luckyItemNum = "1";

	public void resetActivitys() {
		Date nowDate = new Date();
		runningActivities = Sets.newConcurrentHashSet();
		stoppedActivities = Sets.newConcurrentHashSet();
		activityTypes = Sets.newConcurrentHashSet();
		beastActHurtsMap = AtomicLongMap.create();
		sortedActPlayerMap = Maps.newTreeMap();
		beastActOrderMap = Maps.newHashMap();
		showActList = Lists.newArrayList();
		actEntity = new ActivityJsonEntity();
		warPlayerInfos = Maps.newConcurrentMap();
		activeGiftTypes = Sets.newHashSet();
		actGiftMap = Maps.newHashMap();
		serverStartDate = TimeUtil.formatDateStringShort(ContextConfig.START_DATE);
		ActivityJsonEntity actFromFile = (ActivityJsonEntity) actEntity.readValues();
		if (actFromFile != null) {
			actEntity = actFromFile;
		}
		payMap = AtomicLongMap.create(actEntity.getPayMap());
		Map<Integer, Collection<String>> emptyMap = Maps.newHashMap();
		Map<Integer, Collection<String>> map = actEntity.getLeveledWars();
		for (Entry<Integer, Collection<String>> entry : map.entrySet()) {
			Collection<String> guids = entry.getValue();
			Integer level = entry.getKey();
			for (String guid : guids) {
				warPlayerInfos.put(guid, level);
			}
		}
		Supplier<Set<String>> factory = new SetSupplier();
		Multimap<Integer, String> multimap = Multimaps.newMultimap(emptyMap, factory);
//		for (Entry<Integer, Collection<String>> entry : map.entrySet()) {
//			multimap.putAll(entry.getKey(), entry.getValue());
//		}
		for (Entry<String, Integer> entry : warPlayerInfos.entrySet()) {
			multimap.put(entry.getValue(), entry.getKey());
		}
		leveledWars = Multimaps.synchronizedMultimap(multimap);
		try {

			setBeastRewardItemNameAndNum();

			if (schedulerFactoryBean != null) {
				schedulerFactoryBean.destroy();
			}
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			schedulerFactoryBean = new SchedulerFactoryBean() {
				@Override
				protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName) {
					return scheduler;
				}
			};
			JobFactory jobFactory = new AdaptableJobFactory();
			schedulerFactoryBean.setJobFactory(jobFactory);
			ActivityConfigManager actConfig = AllGameConfig.getInstance().activitys;
			Collection<ActivityWrapper> activitys = actConfig.getActivitys();
			List<Trigger> triggers = Lists.newArrayList();
			List<JobDetail> jobs = Lists.newArrayList();
			// 发布时间对应的活动map
			for (ActivityWrapper activity : activitys) {
				stoppedActivities.add(activity.getId());
				// 关闭的活动不用启动
				if (activity.getClose() == null) {
					String showTime = activity.getShowTime();
					if (showTime != null) {
						showActList.add(activity);
					}
					if(activity.getStartTime() != null || activity.getStartDay() != null) {
						cronDury(nowDate, activity, activity.getStartTime(), activity.getEndTime(), triggers, jobs);
					}else if(activity.getShowTime() == null){
						startActivity(activity.getId());
						activity.setShowDate(TimeUtil.getDayBeforeOrAfter(-1000));
						activity.setStartDate(TimeUtil.getDayBeforeOrAfter(-1000));
						activity.setEndDate(TimeUtil.getDayBeforeOrAfter(1000));
						showActList.add(activity);
					}
					checkExchangeOrderActivity(nowDate, activity);
				}
			}
			if (!triggers.isEmpty()) {
				Trigger[] triggerArray = triggers.toArray(new Trigger[triggers.size()]);
				JobDetail[] jobArray = jobs.toArray(new JobDetail[jobs.size()]);
				schedulerFactoryBean.setJobDetails(jobArray);
				schedulerFactoryBean.setTriggers(triggerArray);
				try {
					schedulerFactoryBean.afterPropertiesSet();
					schedulerFactoryBean.start();
				} finally {
					// schedulerFactoryBean.destroy();
				}
			} else {
				logger.error("why no activities.");
			}
			caculateActiveGifts(nowDate);
			
			caculateGiveEnergy(nowDate);
			
		} catch (Exception e) {
			logger.error("World activity error :{}", e);
		}
		
	}
	/**
	 * 计算是否要开启体力奖励
	 * @param date
	 */
	private void caculateGiveEnergy(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if((hour >= 12 && hour < 14) || (hour >= 18 && hour < 20)) {
			logger.info("give_energy_fix_time_open hour={}",hour);
			ContextConfig.GIVE_ENERGY_FIX_TIME_OPEN = true;
		}
	}
	
	private boolean isRunningDayAct(Integer openStatus) {
		if(openStatus == null) {
			return true;
		}
//		int oldServerMax = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.OLD_SERVER_MAX);
//		int serverId = Integer.parseInt(ContextConfig.SERVER_ID);
//		boolean isOld = serverId <= oldServerMax;
//		if(openStatus == 1) {
//			return isOld;
//		}else if(openStatus == 2) {
//			return !isOld;
//		}
		return true;
	}
	
	public void caculateActiveGifts(Date nowDate) throws ParseException { 
		activeGiftTypes.clear();
		actGiftMap.clear();
		GiftType.resetAllTypes();
		Collection<GiftInfo> allGift = AllGameConfig.getInstance().gifts.getAllGiftInfo();
		List<Integer> activeGiftIds = Lists.newArrayList();
		for(GiftInfo gift : allGift){
			if(gift.getClose() != null) continue;
			Integer startDay = gift.getStartDay();
			Integer endDay = gift.getEndDay();
			
			boolean isKaiFu = false;
			if(startDay != null){
				if(isRunningDayAct(gift.getOldOpen())) {
					//处理开服活动
					int days = TimeUtil.getDays(serverStartDate,nowDate);
					Date startDate = TimeUtil.getDayBeforeOrAfter(serverStartDate.getTime(), startDay - 1);
					startDate = TimeUtil.getDayBeforeOrAfterWithSecond(startDate, 5);//增加5秒防止跟结束活动时间在一起
					Date endDate = TimeUtil.getDayBeforeOrAfter(serverStartDate.getTime(), endDay);
					gift.setStartDayString(startDate);
					gift.setEndDayString(endDate);
					if(days >= (startDay - 1) && days < endDay) {
						addActiveGift(gift);
						activeGiftIds.add(gift.getId());
						isKaiFu = true;
					}
				}
			} 
			if(gift.getStartTime() != null && !isKaiFu){
				if(isRunningDayAct(gift.getOldOpen())) {
					
					String temp = gift.getStartTime();
					//处理开始和结束日期活动
					Date compareDate = getCompareDate(nowDate, temp);
					CronTrigger startTrigger;
					
					startTrigger = createCronTrigger(temp);
					
					startTrigger.setStartTime(compareDate);
					CronTrigger endTrigger = createCronTrigger(gift.getEndTime());
					endTrigger.setStartTime(compareDate);
					Date startDate = startTrigger.getFireTimeAfter(compareDate);
					gift.setStartDayString(startDate);
					Date endDate = endTrigger.getFireTimeAfter(compareDate);
					gift.setEndDayString(endDate);
					if(nowDate.after(startDate) && nowDate.before(endDate)){
						addActiveGift(gift);
						activeGiftIds.add(gift.getId());
					}
				}
			}
			
			if(startDay ==  null && gift.getStartTime() == null){
				addActiveGift(gift);
				activeGiftIds.add(gift.getId());
			}
		}
		logger.info("active gift ids : {}", activeGiftIds);
	}

	public void addActiveGift(GiftInfo gift) {
		GiftType giftType = GiftType.getGiftType(gift.getType());
		giftType.addGiftId(gift.getId());
		activeGiftTypes.add(giftType);
		actGiftMap.put(gift.getId(), giftType);
	}
	
	public void doOneDayCron() {
		try {
			Date nowDate = new Date();
			caculateActiveActivities(nowDate);
			caculateActiveGifts(nowDate);
			
			List<String> list = GameRankMaster.getInstance().getArenaRank().getTopN(5);
			logger.info("=================开始输出竞技场排名=================");
			for (int i=0; i<list.size(); i++) {
				logger.info("第{}名 {}", (i+1), list.get(i));
			}
			logger.info("=================输出竞技场排名结束=================");
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}
	public void caculateActiveActivities(Date nowDate) {
		logger.info("CalcCompareDayTask");
		COMPATE_YEAR = DateUtils.truncate(nowDate, Calendar.YEAR);
		COMPATE_MONTH = DateUtils.truncate(nowDate, Calendar.MONTH);
		COMPATE_DAY = TriggerUtils.getDateOf(0, 0, 0);
		ContextConfig.SHOW_EXCHANGE_ORDER = false;
		logger.info("COMPATE_YEAR : {}",TimeUtil.formatDate(COMPATE_YEAR));
		logger.info("COMPATE_MONTH : {}",TimeUtil.formatDate(COMPATE_MONTH));
		logger.info("COMPATE_DAY : {}",TimeUtil.formatDate(COMPATE_DAY));
		Collection<ActivityWrapper> activitys = AllGameConfig.getInstance().activitys.getActivitys();
		for (ActivityWrapper activity : activitys) {
			if(activity.getClose() == null) {
				activity.setShowAndEndDate();
				checkExchangeOrderActivity(nowDate, activity);
			}
		}
		logger.info("SHOW_EXCHANGE_ORDER : {}",ContextConfig.SHOW_EXCHANGE_ORDER);
	}
	public void checkExchangeOrderActivity(Date nowDate, ActivityWrapper activity) {
		boolean isExchange = false;
		for (int i = 0; i < activity.getTypes().length; i++) {
			if(activity.getTypes()[i] == ActivityType.EXCHANGE_ORDER.getId()) {
				isExchange = true;
				break;
			}
		}
		if(isExchange && activity.getEndDate() != null) {
			Date oneDayAfter = TimeUtil.getDayBeforeOrAfter(activity.getEndDate(),1);
			if(nowDate.after(activity.getEndDate()) && nowDate.before(oneDayAfter)) {
				ContextConfig.SHOW_EXCHANGE_ORDER = true;
			}
		}
	}
	
	public Set<GiftType> getActiveGiftTypes(){
		return activeGiftTypes;
	}
	
	public void setBeastRewardItemNameAndNum() {
		VariousItemEntry itemEntry = VariousItemUtil.parse1(top1_reward)[0];
		int itemId = Integer.parseInt(itemEntry.getType());
		//top1ItemName = AllGameConfig.getInstance().items.getBaseItem(itemId).getAlt();
		top1ItemNum = itemEntry.getAmount() + "";
		//logger.info("top1ItemName {}, num:{}", top1ItemName, top1ItemNum);

		itemEntry = VariousItemUtil.parse1(top5_reward)[0];
		itemId = Integer.parseInt(itemEntry.getType());
		//top5ItemName = AllGameConfig.getInstance().items.getBaseItem(itemId).getAlt();
		top5ItemNum = itemEntry.getAmount() + "";
		//logger.info("top5ItemName {}, num:{}", top5ItemName, top5ItemNum);

		itemEntry = VariousItemUtil.parse1(luckyReward)[0];
		itemId = Integer.parseInt(itemEntry.getType());
		//luckyItemName = AllGameConfig.getInstance().items.getBaseItem(itemId).getAlt();
		luckyItemNum = itemEntry.getAmount() + "";
		//logger.info("luckyItemName {}, num:{}", luckyItemName, luckyItemNum);
	}

	public void startBeastScheduledFuture() {
		beastScheduledFuture = taskManagerService.scheduleAtFixedRate(new calcBeastOrderTask(), 1, 1, TimeUnit.MINUTES);
	}

	public void stopBeastScheduledFuture() {
		if (beastScheduledFuture != null) {
			beastScheduledFuture.cancel(true);
		}
		caclSortBeastActOrder();

	}

	protected class calcBeastOrderTask implements Task {

		@Override
		public void run() {
			logger.info("run calcBeastOrderTask...");
			caclSortBeastActOrder();
		}

		@Override
		public Object getId() {
			return null;
		}

		@Override
		public void setId(Object id) {
		}

	}

	public void caclSortBeastActOrder() {
		Map<String, Long> asMap = beastActHurtsMap.asMap();
		ValueComparator bvc = new ValueComparator(asMap);
		TreeMap<String, Long> sortedMap = new TreeMap<String, Long>(bvc);
		sortedMap.putAll(asMap);
		sortedActPlayerMap = sortedMap;
		int i = 1;
		for (String name : sortedMap.keySet()) {
			beastActOrderMap.put(name, i);
			i++;
		}
	}

	public void clearBeastActHurtsMap() {
		beastActHurtsMap.clear();
	}

	public Set<String> getBeastActHurtPlayers() {
		return beastActHurtsMap.asMap().keySet();
	}

	public void addBeastHurt(String name, long value) {
		beastActHurtsMap.addAndGet(name, value);
		Integer posInteger = beastActOrderMap.get(name);
		// 如果前十名重新计算名次
		if (posInteger != null && posInteger <= 10) {
			caclSortBeastActOrder();
		}
	}

	public void addPayValue(String guid, long value) {
		// 只有活动开始的时候才会添加payMap
		if (ActivityType.EXCHANGE_GOLD.isActive() || ActivityType.EXCHANGE_ORDER.isActive()) {
			payMap.addAndGet(guid, value);
		}
	}

	public Map<String, Long> getPayMap() {
		return payMap.asMap();
	}

	public TreeMap<String, Long> getSortedBeastActHurtsMap() {
		return sortedActPlayerMap;
	}

	public TreeMap<String, Long> getSortedPayMap() {
		Map<String, Long> map = payMap.asMap();
		ValueComparator bvc = new ValueComparator(map);
		TreeMap<String, Long> sorted_map = new TreeMap<String, Long>(bvc);
		sorted_map.putAll(map);
		return sorted_map;
	}

	public ArrayNode getBeastActTopFiveList() {
		ArrayNode arrayNode = DynamicJsonProperty.jackson.createArrayNode();
		Map<String, Long> map = getSortedBeastActHurtsMap();
		int i = 0;
		for (Entry<String, Long> entry : map.entrySet()) {
			i++;
			if (i <= 5) {
				ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
				array.add(entry.getKey()).add(entry.getValue()).add(i);
				arrayNode.add(array);
			}
		}
		return arrayNode;
	}

	/**
	 * 获得我的位置信息
	 * 
	 * @param name
	 * @return
	 */
	public ArrayNode getPostionNode(String name) {
		ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
		Integer posInteger = beastActOrderMap.get(name);
		if (posInteger != null) {
			array.add(name).add(beastActHurtsMap.get(name)).add(posInteger);
		}
		return array;
	}

	static class ValueComparator implements Comparator<String> {

		Map<String, Long> base;

		public ValueComparator(Map<String, Long> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		@Override
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	
	
	public Collection<ObjectNode> getShowActs() {
		Comparator<Date> cm = new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {
				if(o1.after(o2)) {
					return -1;
				}else{
					return 1;
				}
			}
		};
		TreeMap<Date, ObjectNode> actTreeMap = new TreeMap<Date, ObjectNode>(cm);
		try {
			Date nowDate = new Date();
			int i = 0;
			for (ActivityWrapper activity : showActList) {
				Date showDate = activity.getShowDate();
				Date endDate = activity.getEndDate();
				if (nowDate.after(showDate) && nowDate.before(endDate)) {
					ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
					node.put("id", activity.getId());
					node.put("start", activity.getStartDayString());
					node.put("end", activity.getEndDayString());
					actTreeMap.put(showDate,node);
					if(logger.isDebugEnabled()) {
						String formatDate = TimeUtil.formatDate(showDate);
						logger.debug("actId:{},actName:{}, date:{}",activity.getId(),activity.getName(),formatDate);
						
					}
					i++;
					if(i >= MAX_SHOW_ACTS){
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return actTreeMap.values();
	}

	// public long getTriggerSeconds(String showTime) {
	// String[] t = showTime.split(" ");
	// int seconds = Integer.parseInt(t[0]);
	// int minutes = Integer.parseInt(t[1]);
	// int hours = Integer.parseInt(t[2]);
	// int days = Integer.parseInt(t[3]);
	// int months = Integer.parseInt(t[4]);
	// return
	// months*TimeUnit.DAYS.toSeconds(30)+TimeUnit.DAYS.toSeconds(days)+TimeUnit.HOURS.toSeconds(hours)+TimeUnit.MINUTES.toSeconds(minutes)+seconds;
	// }

	public long getTriggerSeconds(Date nowDate, String showTime) throws ParseException {
		CronTrigger trigger = createCronTrigger(showTime);
		Date showDate = trigger.getFireTimeAfter(nowDate);
//		long time = DateUtils.getFragmentInSeconds(showDate, Calendar.MONTH);
//		return time;
		return showDate.getTime();
	}

	public JobDetail createJobDetail(int actId, Class<? extends Job> jobclazz) {
		JobDetail jobDetail = new JobDetail();
		jobDetail.setJobClass(jobclazz);
		jobDetail.setName(getJobName(actId, jobclazz));
		jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
		jobDetail.getJobDataMap().put(ACT_ID_KEY, actId);
		return jobDetail;
	}

	public String getJobName(int actId, Class<? extends Job> jobclazz) {
		return jobclazz.getSimpleName() + "-" + actId;
	}

	private void cronDury(Date nowDate, ActivityWrapper activity, String startExpress, String endExpress, List<Trigger> triggers, List<JobDetail> jobs) {
		try {
			int actId = activity.getId();
			//logger.info("startDate:{}, endDate:{},nowDate:{}", TimeUtil.formatDate(startDate), TimeUtil.formatDate(endDate), TimeUtil.formatDate(nowDate));
			
			Integer startDay = activity.getStartDay();
			Integer endDay = activity.getEndDay();
			if(startDay != null){
				//处理开服活动
				if(isRunningDayAct(activity.getOldOpen())) {
					int days = TimeUtil.getDays(serverStartDate,nowDate);
					Date startDate = TimeUtil.getDayBeforeOrAfter(serverStartDate.getTime(), startDay - 1);
					startDate = TimeUtil.getDayBeforeOrAfterWithSecond(startDate, 5);//增加5秒防止跟结束活动时间在一起
					Date endDate = TimeUtil.getDayBeforeOrAfter(serverStartDate.getTime(), endDay);
					activity.setShowDate(startDate);
					activity.setStartDate(startDate);
					activity.setEndDate(endDate);
					if(days >= (startDay - 1) && days < endDay) {
						startActivity(actId);
						addJobAndTrigger(actId, triggers, jobs, createCronTrigger(endDate), StopActivityJob.class);
						showActList.add(activity);
					}else if(days < (startDay - 1)){
						addJobAndTrigger(actId, triggers, jobs, createCronTrigger(startDate), StartActivityJob.class);
						addJobAndTrigger(actId, triggers, jobs, createCronTrigger(endDate), StopActivityJob.class);
						showActList.add(activity);
					}
				}
			}else if(startExpress != null){
				if(isRunningDayAct(activity.getOldOpen())) {
					Date compareDate = getCompareDate(nowDate, startExpress);
					CronTrigger startTrigger = createCronTrigger(startExpress);
					startTrigger.setStartTime(compareDate);
					CronTrigger endTrigger = createCronTrigger(endExpress);
					endTrigger.setStartTime(compareDate);
					Date startDate = startTrigger.getFireTimeAfter(compareDate);
					Date endDate = endTrigger.getFireTimeAfter(compareDate);
					// 重置tigger
					startTrigger.setStartTime(nowDate);
					endTrigger.setStartTime(nowDate);
//					if (nowDate.before(startDate)) {
//						// 之间开始两个区间即可
//						addJobAndTrigger(actId, triggers, jobs, startTrigger, StartActivityJob.class);
//						addJobAndTrigger(actId, triggers, jobs, endTrigger, StopActivityJob.class);
//					} else if (nowDate.after(startDate) && nowDate.before(endDate)) {
//						// 从现在开始打开活动，并添加结束时间
//						startActivity(actId);
//						addJobAndTrigger(actId, triggers, jobs, endTrigger, StopActivityJob.class);
//					} else if (nowDate.after(endDate)) {
//						// if (!startExpress.endsWith("?")) {
//						// 今天过期的活动会调用这个
//						addJobAndTrigger(actId, triggers, jobs, startTrigger, StartActivityJob.class);
//						addJobAndTrigger(actId, triggers, jobs, endTrigger, StopActivityJob.class);
//						// }
//					} else {
//						logger.error("how it ?");
//					}
					if (nowDate.after(startDate) && nowDate.before(endDate)) {
						// 从现在开始打开活动，并添加结束时间
						startActivity(actId);
					}
					addJobAndTrigger(actId, triggers, jobs, startTrigger, StartActivityJob.class);
					addJobAndTrigger(actId, triggers, jobs, endTrigger, StopActivityJob.class);
				}
			}else {
				startActivity(actId);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	private Date getCompareDate(Date nowDate, String express) {
		if (express.endsWith("?")) {
			if(express.endsWith("* ?")) {
				return COMPATE_MONTH;
			}
			return COMPATE_YEAR;
		} else {
			return COMPATE_DAY;
		}
	}

	public void addJobAndTrigger(Integer actId, List<Trigger> triggers, List<JobDetail> jobs, Trigger trigger, Class<? extends Job> jobclazz) {
		JobDetail jobDetail = createJobDetail(actId, jobclazz);
		trigger.setName("trigger-" + jobDetail.getName());
		trigger.setJobName(jobDetail.getName());
		triggers.add(trigger);
		jobs.add(jobDetail);
	}

	private CronTrigger createCronTrigger(String express) throws ParseException {
		CronTrigger trigger0 = new CronTrigger();
		// trigger0.setName("myTrigger" + jobName);
		trigger0.setGroup(Scheduler.DEFAULT_GROUP);
		// trigger0.setJobName(jobName);
		trigger0.setJobGroup(Scheduler.DEFAULT_GROUP);
		trigger0.setCronExpression(express);
		return trigger0;
	}
	
	private Trigger createCronTrigger(Date date) throws ParseException {
		SimpleTrigger trigger0 = new SimpleTrigger("myTrigger-day", Scheduler.DEFAULT_GROUP, date, null,
	                 0,
	                 0L);
		return trigger0;
	}

	public static class StartActivityJob implements Job {

		@Override
		public synchronized void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
			int actId = (Integer) jobExecutionContext.getJobDetail().getJobDataMap().get(ACT_ID_KEY);
			startActivity(actId);
		}
	}

	public static void startActivity(int actId) {
		boolean add = runningActivities.add(actId);
		logger.info("StartActivityJob, add runningActivities actId:{}, result:{}", actId, add);
		boolean remove = stoppedActivities.remove(actId);
		logger.info("StartActivityJob, remove stoppedActivities actId:{}, result:{}", actId, remove);
		ActivityWrapper activity = AllGameConfig.getInstance().activitys.getActivity(actId);
		int[] types = activity.getTypes();
		int i = 0;
		for (int id : types) {
			ActivityType activityType = ActivityType.getActById(id);
			String reward = null;
			if (activity.getItem() != null && i < activity.getItem().length) {
				reward = activity.getItem()[i];
			}
			Integer mailText = null;
			if (activity.getMailText() != null && i < activity.getMailText().length) {
				mailText = activity.getMailText()[i];
			}
			String value = null;
			if (activity.getValues() != null && i < activity.getValues().length) {
				value = activity.getValues()[i];
			}
			activityType.active(actId, value, reward, mailText);
			activityTypes.add(activityType);
			i++;
		}
		
	}
	
	public static void stopActivity(int actId) {
		boolean remove = runningActivities.remove(actId);
		logger.info("StopActivityJob, remove runningActivities actId:{}, result:{}", actId, remove);
		boolean add = stoppedActivities.add(actId);
		logger.info("StopActivityJob, add stoppedActivities actId:{}, result:{}", actId, add);
		ActivityWrapper activity = AllGameConfig.getInstance().activitys.getActivity(actId);
		int[] types = activity.getTypes();
		boolean containPayAct = false;
		for (int id : types) {
			ActivityType activityType = ActivityType.getActById(id);
			activityTypes.remove(activityType);
			activityType.stop();
			if(activityType == ActivityType.EXCHANGE_GOLD || activityType == ActivityType.EXCHANGE_ORDER) {
				containPayAct = true;
			}
		}
		if(containPayAct) {
			payMap.clear();
		}
	}

	public static class StopActivityJob implements Job {

		@Override
		public synchronized void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
			int actId = (Integer) jobExecutionContext.getJobDetail().getJobDataMap().get(ACT_ID_KEY);
			stopActivity(actId);
		}
	}

	public TaskManagerService getTaskManagerService() {
		return taskManagerService;
	}

	public void setTaskManagerService(TaskManagerService taskManagerService) {
		this.taskManagerService = taskManagerService;
	}

	public void shutdown() {
		actEntity.setLeveledWars(leveledWars.asMap());
		actEntity.setPayMap(payMap.asMap());
		actEntity.writevalues();

		logger.info("save activity json file");
	}

	public void sendAllActivityMsg(Player player, int show) {
		long l = System.currentTimeMillis();
		Collection<ObjectNode> allActivitys = getShowActs();
		if(allActivitys.isEmpty()) {
			show = 0;
		}
		logger.info("ShowActs duration:{}ms....", (System.currentTimeMillis() - l));
		ObjectNode act = DynamicJsonProperty.jackson.createObjectNode();
		act.put("list", DynamicJsonProperty.convertToJsonNode(allActivitys));
		act.put("show", show);
		player.sendMessage("activities", act);
	}

	public void addDefendWarPlayer(Integer playerLv, String guid) {
		if(warPlayerInfos.containsKey(guid)) return;
		warPlayerInfos.put(guid, playerLv);
		leveledWars.put(playerLv, guid);
	}

	public boolean isInWar(String guid) {
		return warPlayerInfos.containsKey(guid);
	}

	public void removeDefendWarPlayer(String guid) {
		Integer level = warPlayerInfos.remove(guid);
		if (level != null) {
			leveledWars.remove(level, guid);
		}
	}

	public Multimap<Integer, String> getLeveledWars() {
		return leveledWars;
	}

	public void setLeveledWars(Multimap<Integer, String> leveledWars) {
		this.leveledWars = leveledWars;
	}

	private static class QueueSupplier implements Supplier<Queue<String>> {

		@Override
		public Queue<String> get() {
			return new LinkedList<String>();
		}
	}
	
	private static class SetSupplier implements Supplier<Set<String>> {

		@Override
		public Set<String> get() {
			return new HashSet<String>();
		}
	}

	public String getDefenderGuid(Player player, AllPlayersCache allPlayersCache) {
		int level = player.getProperty().getLevel();
		List<Integer> lvs = Lists.newArrayList();
		int min = (level - 5) <= 0 ? 0 : level - 5;
		int max = level + 5 >= 99 ? 99 : level + 5;
		for (int i = min; i <= max; i++) {
			lvs.add(i);
		}
		Collections.shuffle(lvs);
		for (Integer lv : lvs) {
			Collection<String> collection = getLeveledWars().get(lv);
			if (collection != null && collection.size() > 0) {
				for (String string : collection) {
					if (!string.equals(player.getId())) {
						return string;
					}
				}
			}
		}

		RoleEntity rndRoundPlayer = allPlayersCache.getRndRoundPlayer(Lists.newArrayList(player.getRole()), level, 0);
		return rndRoundPlayer.getGuid();
	}
	
	public long getActivityEndTimeByID(Integer actid)
	{
		if(actid == null){
			return 0;
		}
		ActivityConfigManager actConfig = AllGameConfig.getInstance().activitys;
		Collection<ActivityWrapper> activitys = actConfig.getActivitys();
		// 发布时间对应的活动map
		for (ActivityWrapper activity : activitys) {
			if(activity.getId().intValue() == actid.intValue()){
				long endtime = 0;
				try{
					Date nowDate = new Date();
					String startExpress = activity.getStartTime(); 
					String endExpress = activity.getEndTime();
					
					Date compareDate = getCompareDate(nowDate, startExpress);
					
					CronTrigger endTrigger = new CronTrigger();
					endTrigger.setCronExpression(endExpress);
					endTrigger.setStartTime(compareDate);
					
					Date endDate = endTrigger.getFireTimeAfter(compareDate);
					endtime = endDate.getTime();
					
					return endtime;
				} catch (Exception e) {
					endtime = 0;
				}
				return endtime;
			}
		}
		
		return 0;
	}
}
