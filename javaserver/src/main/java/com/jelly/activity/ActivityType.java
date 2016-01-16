package com.jelly.activity;

import io.nadron.context.AppContext;
import io.nadron.event.Events;
import io.nadron.example.lostdecade.LDRoom;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.Util;
import com.dol.cdf.common.bean.Catchninja;
import com.dol.cdf.common.bean.QualityRate;
import com.dol.cdf.common.bean.Role;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.collect.Pair;
import com.dol.cdf.common.config.ActivityConfigManager.ActivityWrapper;
import com.dol.cdf.common.config.AllGameConfig;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.jelly.game.command.ChatConstants;
import com.jelly.rank.GameRankMaster;
import com.jelly.team.TeamManager;

public enum ActivityType {
	RAFF_0(1, -1), // 1、初级抓取次数
	RAFF_1(2, -1), // 2、高级抓取次数
	RAFF_2(3, -1), // 3、十连抓次数
	RAFF_MUTI_2(4, 1), // 4、高级抓取S级以上忍者加倍
	PRA_0(5, -1), // 5、忍术初级修炼N次
	PRA_1(6, -1), // 6、忍术高级修炼N次
	NIN_MUTI_0(7, 1), // 7、忍术初级修炼得道高级技能几率加倍
	NIN_MUTI_1(8, 1), // 8、忍术高级修炼得道S级以上技能几率加倍
	TAKE_MUTI(9, 1), // 9、打劫银币数量加倍
	TAKE_0(10, -1), // 10、打劫N次
	DIG_0(11, -1), // 11、完成N次夺宝
	BRA_0(12, -1), // 12、高级通灵学院,完成N次修炼
	PVP_ORDER(13, -2){
		@Override
		public void stop() {
			if (isActive()) {
				LDRoom ldRoom = AppContext.getBean(LDRoom.class);
				String[] strings = getValue().split(";");
				int[] vals = StringHelper.getIntList(strings);
				ldRoom.rankOrderReward(vals, getReward(), tid);
				super.stop();
			}
		}
	}, // 13、活动结束后竞技场排名xx至xx名
	PVP_0(14, -1), // 14、进行x场竞技场战斗
	EXAM_MUTI_RATE(15, 1), // 15、上忍考试掉落几率加倍
	EXAM_MUTI_EARN(16, 1), // 16、上忍考试经验和银币获得加倍
	EXAM_PASS(17, -1), // 17、上忍考试超过xx关 "现在时按照次数进行计算"
	BEAST_INJECT(18, -1), // 18、进行N次尾兽注力
	QUEST_MUTI(19, 1), // 19、集会所银币奖励加倍
	QUEST_FINISH(20, -1), // 20、集会所完成N个悬赏任务
	REC_MUTI(21, 1), // 21、影忍堂每次战斗消耗的体力减半
	ADV_MUTI(22, 1), // 22、普通推图战斗，经验和银币奖励加倍
	ENHANCE_CD(23, 0), // 23、强化不增加冷却时间
	EXCHANGE_GOLD(24, 0) {
		@Override
		public void stop() {
			// 100000;10000;1000;100;1
			if (isActive()) {
				String[] strings = getValue().split(";");
				int[] vals = StringHelper.getIntList(strings);
				LDRoom ldRoom = AppContext.getBean(LDRoom.class);
				ldRoom.payReward(vals, getReward(), tid);
				super.stop();
			}
		}
	}, // 24、冲值超过xx金币
	EXCHANGE_ORDER(25, -2) {
		@Override
		public void stop() {
			// 1-1;2-3;4-10
			if(isActive()) {
				String[] strings = getValue().split(";");
				int[] vals = StringHelper.getIntList(strings);
				LDRoom ldRoom = AppContext.getBean(LDRoom.class);
				ldRoom.payOrderReward(vals, getReward(), tid);
				GameRankMaster.getInstance().caculatePayRank();
				super.stop();
			}
			
		}
	}, // 25、冲值活动结束后冲值第x名至第x名可获得某道具
	LOGIN_ITEM(26, -1), // 26、登陆即可获得某道具
	BEAST_ORDER(27, -2) {
		@Override
		protected void start() {
			//先清理之前活动的数据再开始新的数据
			WorldActivity worldActivity = AppContext.getBean(WorldActivity.class);
			worldActivity.startBeastScheduledFuture();
			LDRoom room = AppContext.getBean(LDRoom.class);
			if (room != null) {
				ObjectNode node = beastInfoObj(1);
				room.sendBroadcast(Events.networkEvent(node));
			}
		}

		private ObjectNode beastInfoObj(int open) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			obj.put("beastOpen", open);
			ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
			node.put("player", obj);
			return node;
		}

		@Override
		public void stop() {
			if(isActive()) {
				WorldActivity worldActivity = AppContext.getBean(WorldActivity.class);
				worldActivity.stopBeastScheduledFuture();
				LDRoom room = AppContext.getBean(LDRoom.class);
				ObjectNode node = beastInfoObj(0);
				room.sendBroadcast(Events.networkEvent(node));
				room.beastOrderReward(getReward());
				super.stop();
				worldActivity.clearBeastActHurtsMap();
			}
		}
	},// 27、尾兽之怒
	RAFFLE_MUTI(28, 1), // 28 十连抽半价活动
	RAFFLE_SCORE(30, -2) {	// 30 限时忍者活动
		
		private Map<Integer, ImmutableRangeMap<Integer, List<Integer>>> actBeforeGradeRates;
		private Map<Integer, QualityRate> actBeforeQualityRateMap;
		private ArrayListMultimap<Integer, Role> actGradeMap;
		
		@Override
		protected void start() {
			actBeforeGradeRates = AllGameConfig.getInstance().rate.getGradeRates();
			actBeforeQualityRateMap = AllGameConfig.getInstance().rate.getQualityRateMap();
			
			Map<Integer, ImmutableRangeMap<Integer, List<Integer>>> newActBeforeGradeRates = actBeforeGradeRates;
			Map<Integer, QualityRate> newActBeforeQualityRateMap = actBeforeQualityRateMap;
			
			// 忍者品质权重
			Catchninja actData = AllGameConfig.getInstance().activitys.getCatchNinja();
			for (int type : actData.getIntegralway()) {
				QualityRate qrate = newActBeforeQualityRateMap.get(type);
				int[] quals = actData.getQuality();
				int[] rolesRate = actData.getNinjaRate();
				for (int i = 0; i < quals.length; ++i) {
					int qual = quals[i];
					switch (qual) {
					case 5:   // S-Level
						qrate.setSrate(rolesRate[i]);
						break;
					case 6:   // SS-Level
						qrate.setSsrate(rolesRate[i]);
						break;
					}
					
					int here = Util.indexOf(qrate.getLuckQuality(), qual);
					qrate.getLuckStart()[here] = actData.getLuckStart()[i];
					qrate.getLuckStep()[here] = actData.getLuckStep()[i];
					qrate.getLuckValue()[here] = actData.getLuckValue()[i];
				}
				
				newActBeforeQualityRateMap.put(type, qrate);
				ImmutableRangeMap.Builder<Integer, List<Integer>> builder = ImmutableRangeMap.builder();
				Range<Integer> range = Range.closed(qrate.getMinLv(), qrate.getMaxLv());
				builder.put(range, Lists.newArrayList(
						qrate.getDrate(), 
						qrate.getCrate(), 
						qrate.getBrate(), 
						qrate.getArate(), 
						qrate.getSrate(), 
						qrate.getSsrate()));
				newActBeforeGradeRates.put(type, builder.build());
			}
			// 将忍者屋抓忍者的权重替换成为活动期间使用的权重值
			AllGameConfig.getInstance().rate.replaceConfig(newActBeforeGradeRates, newActBeforeQualityRateMap);
		
			// 同品质忍者权重值
			Map<Integer, List<Integer>> actRoleGradeMap = Maps.newHashMap();
			int[] quals = actData.getQuality();
			String[] roles = actData.getNinjaID();
			for (int i = 0; i < quals.length; ++i) {
				List<Integer> rolesID = Lists.newArrayList();
				for (String id : roles[i].split(";")) {
					rolesID.add(Integer.parseInt(id));
				}
				actRoleGradeMap.put(quals[i], rolesID);
			}
			actGradeMap = AllGameConfig.getInstance().characterManager.replaceConfig(actRoleGradeMap);
			
			GameRankMaster.getInstance().loadScoreRank();
			
			// 广播活动开始的消息
			ActivityWrapper act = AllGameConfig.getInstance().activitys.getActivity(getActId());
			broadcastActMessage(MessageCode.BROADCAST_RAFFLE_NINJA_START, 
					String.valueOf(act.getStartDate().getTime()), String.valueOf(act.getEndDate().getTime()));
		}
		
		@Override
		public void stop() {
			
			logger.info("activity {} end execute give rewards ", RAFFLE_SCORE);
			long l = System.currentTimeMillis();
			
			// 恢复到活动之前的忍者权重配置
			AllGameConfig.getInstance().rate.replaceConfig(actBeforeGradeRates, actBeforeQualityRateMap);
			AllGameConfig.getInstance().characterManager.restoreConfig(actGradeMap);
			
			GameRankMaster.getInstance().createScoreRankBak();
			
			// 根据在排行榜内玩家排行发送对应该名次的奖品
			Map<Pair<Integer, Integer>, String> order2RewardMap = Maps.newHashMap();
			String[] rewards = reward.split(","), rewardGrades = value.split(";");
			for (int i = 0, j = 1; i < rewardGrades.length; ++i) {
				int e = Integer.parseInt(rewardGrades[i]);
				order2RewardMap.put(new Pair<Integer, Integer>(j, e), rewards[i]);
				j = e + 1;
			}
//			logger.info("order2RewardMap=" + order2RewardMap.toString());
			LDRoom room = AppContext.getBean(LDRoom.class);
			room.giveScoreRankReward(order2RewardMap);
			
			GameRankMaster.getInstance().cleanupScoreRank();
			
			// 广播活动结束消息
			broadcastActMessage(MessageCode.BROADCAST_RAFFLE_NINJA_END);
			
			logger.info("activity {} end cost {}ms", (System.currentTimeMillis() - l));
		}
		
		private void broadcastActMessage(int tid, String... strs) {
			ObjectNode cmd = DynamicJsonProperty.jackson.createObjectNode();
			cmd.put("tid", tid);
			cmd.put("param", DynamicJsonProperty.convertToArrayNode(strs));
			cmd.put("scope", ChatConstants.SCOPE_ACTIVE);
			ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
			node.put("chat", cmd);
			AppContext.getBean(LDRoom.class).sendBroadcast(Events.networkEvent(node));
		}
	},
	TEAMS_WAR(31, -1){
		@Override
		protected void start() {
			if(ContextConfig.TEAMS_WAR_OPEN == false){
				TeamManager.getSingleton().teamWarStartFunc();
				ContextConfig.TEAMS_WAR_OPEN = true;
			}
			
			System.out.println("军团战开始====="+ContextConfig.TEAMS_WAR_OPEN);
		}
		@Override
		public void stop() {
			if(ContextConfig.TEAMS_WAR_OPEN == true){
				ContextConfig.TEAMS_WAR_OPEN = false;
				TeamManager.getSingleton().teamWarEndFunc();
			}
			
			System.out.println("军团战结束====="+ContextConfig.TEAMS_WAR_OPEN);
		}
		
	}, // 31 军团战活动
	;

	int id;
	// -1表示次数，-2表示累计， =0表示无用的初始值，>0表示加倍的数量
	float type;

	String value;

	// 活动ID
	Integer actId;

	String reward;

	/**
	 * text id 用于邮件内容
	 */
	Integer tid;

	static Map<Integer, ActivityType> id2ActTypsMap = Maps.newHashMap();
	
	static Logger logger = LoggerFactory.getLogger(ActivityType.class);

	static {
		for (ActivityType activityType : ActivityType.values()) {
			id2ActTypsMap.put(activityType.id, activityType);
		}
	}

	public static ActivityType getActById(int id) {
		return id2ActTypsMap.get(id);
	}

	// 开始活动是调用
	public void active(Integer actId, String value, String reward, Integer tid) {
		this.actId = actId;
		this.value = value;
		this.reward = reward;
		this.tid = tid;
		start();
	}

	protected void start() {
	}

	// 邮件内容
	public Integer getTid() {
		return this.tid;
	}

	public int getId() {
		return this.id;
	}

	public Integer getActId() {
		return this.actId;
	}

	// 关闭活动时候调用
	public void stop() {
		resetValue();
	}

	ActivityType(int id, float type) {
		this.id = id;
		this.type = type;
	}

	public boolean isActive() {
		return actId != null;
	}
	
	public String getValue() {
		if (value == null) {
			return type + "";
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void resetValue() {
		this.value = null;
		this.reward = null;
		this.tid = null;
		this.actId = null;
	}

	public String getReward() {
		return this.reward;
	}

	public VariousItemEntry[] getNormalRewards() {
		if (this.reward == null) {
			return VariousItemEntry.EMPTY_ARRAY;
		} else {
			VariousItemEntry[] parse = VariousItemUtil.parse1(this.reward);
			return parse;
		}
	}
	
//	public static void main(String[] args) {
//		List<Integer> lvs = Lists.newArrayList();
//		lvs.add(1);
//		lvs.add(2);
//		lvs.add(3);
//		lvs.add(6);
//		lvs.add(10);
//		
//		List<String> ranklist = Lists.newArrayList();
//		ranklist.add("Sloven");
//		ranklist.add("Lancer");
//		ranklist.add("Rider");
//		ranklist.add("Archer");
//		ranklist.add("Saber");
////		ranklist.add("Assassi");
////		ranklist.add("Caster");
////		ranklist.add("Berserker");
////		ranklist.add("Luffy");
////		ranklist.add("Zero");
//		
//		//<107;1>,<106;1>,<105;1>,<104;1>,<103;1>
//		//"1;2;3;6;10",
//		List<String> rewards = Lists.newArrayList(new String[] { "<107;1>",
//				"<106;1>", "<105;1>", "<104;1>", "<103;1>" });
//		
//		Map<String, String> id2RewardMap = Maps.newHashMap();
//		int j = 1;
//		for (int i = 0; i < lvs.size(); ++i) {
//			String reward = rewards.get(i);
//			int e = lvs.get(i);
//			for (; j <= e && j <= ranklist.size(); ++j) {
//				id2RewardMap.put(ranklist.get(j - 1), reward);
//			}
//		}
//		System.out.println(id2RewardMap);
//	}
}
