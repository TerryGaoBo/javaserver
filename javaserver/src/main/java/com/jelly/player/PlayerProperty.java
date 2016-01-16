package com.jelly.player;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.Level;
import com.dol.cdf.common.bean.Recharge;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.bean.Vip;
import com.dol.cdf.common.collect.IntList;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.common.config.GiftConfigManager.GiftInfo;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jelly.activity.ActivityType;
import com.jelly.activity.GiftType;
import com.jelly.activity.WorldActivity;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.quest.TaskType;
import com.jelly.rank.GameRankMaster;

public class PlayerProperty extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerProperty.class);

	public final static short EXP_FROM_UNKNOW = 0;
	public final static short EXP_FROM_TASK = 1;

	public final static int[] VIP_DAY_VALUE = {1,3,5,7,9,12};

	
	@JsonProperty("go")
	private int gold = 0;

	@JsonProperty("sv")
	private int silver = 100000;

	@JsonProperty("co")
	private int coin = 0;
	
	@JsonProperty("jdd")
	private int arenapoint = 0;

	@JsonProperty("le")
	private int level = 1;

	@JsonProperty("ex")
	private int exp = 0;

	@JsonProperty("en")
	private int energy = 0;

	@JsonProperty("be")
	private int buyEnergyTimes = 0;

	@JsonProperty("st")
	private int buySilverTimes = 0;
	
	@JsonProperty("bt")
	private int buyPvpTimes = 0;

	@JsonProperty("lb")
	private int lastBuyEnergyTime = 0;
	
	@JsonProperty("lp")
	private int lastBuyPvpTime = 0;

	@JsonProperty("ls")
	private int lastBuySilverTime = 0;

	@JsonProperty("la")
	private int lastAddEnergyTime = 0;

	@JsonProperty("es")
	private int rewardEnergyStatus = 0;

	@JsonProperty("lr")
	private int lastRewardEnergyTime = 0;

	@JsonProperty("ft")
	private int firstOnlineTime = 0;

	@JsonProperty("lt")
	private int lastOnlineTime = 0;

	@JsonProperty("vl")
	private int vipLv = 0;

	@JsonProperty("vo")
	private int vipScore = 0;

	@JsonProperty("mpt")
	private int monthlyPayment = 0; // 月卡过期时间戳

	@JsonProperty("lmp")
	private int lastMP = 0; // 上次领取月卡奖励的时间戳

	//首充状态 key-itemId value- 1-充值过 0-没充过
	@JsonProperty("fpaid")
	private Map<String, Integer> payStatus = new HashMap<String, Integer>();

	@JsonProperty("ne")
	private String net;

	// 渠道
	@JsonProperty("ch")
	private String ch;

	@JsonProperty("us")
	private String userId;

	// /当前天数 从1开始
	@JsonProperty("da")
	private int comeDays = 1;

	// 上次领奖时间
	@JsonProperty("lc")
	private int lastDailyRewardTime = 0;

	// 主界面获得银币的次数
	@JsonProperty("ts")
	private int takeSilverTimes = 0;

	// 最后一次停服奖励给予的时间
	@JsonProperty("lg")
	private int lastStopGameGiveTime = 0;

	// 副本重置次数 key:章_关 value:次数
	@JsonProperty("fbrn")
	private Map<String, Integer> fubenReset = new HashMap<String, Integer>();
	// 副本每日使用次数 key:章_关 value:次数
	@JsonProperty("fbu")
	private Map<String, Integer> fubenUse = new HashMap<String, Integer>();
	// 上次副本重置时间
	@JsonProperty("lfbr")
	private int lastFubenReset = 0;
	// 上次副本使用时间
	@JsonProperty("lfbu")
	private int lastFubenUse = 0;
	
	//	精英副本重置次数 key:章_关 value:次数
	@JsonProperty("efbrn")
	private Map<String, Integer> eliteFubenReset = Maps.newHashMap();
	//	精英副本每日使用次数 key:章_关 value:次数
	@JsonProperty("efbu")
	private Map<String, Integer> eliteFubenUse = Maps.newHashMap();
	//	上次精英副本重置时间
	@JsonProperty("elfbr")
	private int eliteLastFubenReset = 0;
	//	上次精英副本使用时间
	@JsonProperty("elfbu")
	private int eliteLastFubenUse = 0;
	
	@JsonProperty("os")
	private int operStatus = 0;

	// 各种卡标记是否领取
	@JsonProperty("cm")
	private Set<String> cardMark = Sets.newHashSet();
	
	//专精点
	@JsonProperty("sp")	
	private int spoint = 0;
	//key giftId,
	@JsonProperty("gs")	
	private Map<Integer, Integer> giftStatus = Maps.newHashMap();
	
	//key giftId, value gift value
	@JsonProperty("gv")	
	private Map<Integer, Integer> giftValue = Maps.newHashMap();
	//key giftId,
	@JsonProperty("lgt")	
	private Map<Integer, Integer> lastGiftTime = Maps.newHashMap();
	
	//玩家被封之前的等级
	@JsonProperty("bv")	
	private int banPlayerLv = 0;
	
	private int lastSpeakInChatTime = 0;
	
	@JsonProperty("fprt")
	private int firstPayResetTimes = 0;//首充重置次数
	
	@JsonProperty("luck")
	private Map<Integer,Map<Integer,Integer>> luckyValues = Maps.newHashMap();
	
	@JsonProperty("lvp")
	private int lastVipPrize = 0; // 上次领取vip每日奖励的时间
	
	/**
	 * vip 奖励上次领取时间
	 * @return
	 */
	public int getLastVipPrize() {
		return this.lastVipPrize;
	}

	public void setLastVipPrize(int lastVipPrize) {
		this.lastVipPrize = lastVipPrize;
	}

	/**
	 * 增加幸运的步进值
	 * @param type
	 */
	public void increaseLuckStep(int type, int quality, int luckStep){
		Map<Integer, Integer> map = luckyValues.get(type);
		if(map == null) {
			map = Maps.newHashMap();
			luckyValues.put(type, map);
		}
		Integer value = map.get(quality);
		if(value == null) {
			map.put(quality, luckStep);
		}else {
			map.put(quality, luckStep + value);
		}
	}
	
	public int getLuckQualityStep(int type, int quality) {
		Map<Integer, Integer> map = luckyValues.get(type);
		if(map == null) {
			return 0;
		}
		Integer steps = map.get(quality);
		return steps == null? 0 : steps;
	}
	
	/**
	 * 充值幸运的步进值
	 * @param type
	 * @param quality
	 */
	public void resetLuckStep(int type, int quality) {
		Map<Integer, Integer> map = luckyValues.get(type);
		if(map == null) {
			return;
		}
		map.remove(quality);
	}
	
	
	public int getFirstPayResetTimes() {
		return firstPayResetTimes;
	}
	
	public void setFirstPayResetTimes(int times) {
		firstPayResetTimes = times;
		addChange("fprt", firstPayResetTimes);
	}
	
	/**
	 * 请求上次说话的时间
	 * @param time
	 * @return
	 */
	public int getLastSpeakInChatTime() {
		return this.lastSpeakInChatTime;
	}

	public void setLastSpeakInChatTime(int lastSpeakInChatTime) {
		this.lastSpeakInChatTime = lastSpeakInChatTime;
	}

	/**
	 * 请求礼包数值
	 * @param type
	 * @return
	 */
	public int getGiftValue(int id) {
		Integer val = giftValue.get(id);
		return val == null ? 0 : val;
	}
	
	public void banProperty() {
		this.banPlayerLv = this.getLevel();
		this.setLevel(3);
	}
	public void unBanProperty() {
		this.setLevel(this.banPlayerLv);
		this.banPlayerLv = 0;
	}
	public int getBanPlayerLv() {
		return this.banPlayerLv;
	}
	
	/**
	 * 上次奖励礼包时间
	 * @param id
	 * @return
	 */
	public int getLastGiftTime(int id) {
		Integer time = lastGiftTime.get(id);
		return time == null ? 0 : time;
	}
	
	public void setLastGiftTime(int id, int time){
		lastGiftTime.put(id, time);
	}
	
	/**
	 * 累计数值
	 * @param type
	 */
	public void addGiftValue(int id, int count) {
		int val = getGiftValue(id);
		giftValue.put(id, val + count);
		addChange("giftValue", convertToObjectNode(giftValue));
	}
	
	/**
	 * 设置数值
	 * @param type
	 */
	public void setGiftValue(int type, int count) {
		giftValue.put(type, count);
		addChange("giftValue", convertToObjectNode(giftValue));
	}
	
	
	/**
	 * 获得礼包的领取状态
	 * @param id
	 * @return
	 */
	public int getGiftStatus(int id) {
		Integer status =  giftStatus.get(id);
		return status == null? 0 : status;
	}
	
	
	public boolean containGiftStatus(int id, int idx) {
		int giftStatus = getGiftStatus(id);
		return giftStatus != 0;
//		return (giftStatus & 1 << idx) != 0;
	}
	
	public void addGiftStatus(int type, int idx) {
		int currStatus = getGiftStatus(type);
		currStatus = 1;
//		currStatus |= 1 << idx;
		giftStatus.put(type, currStatus);
		addChange("giftStatus", convertToObjectNode(giftStatus));
	}
	
	public void resetGiftStatus(int id) {
		giftStatus.put(id, 0);
		giftValue.remove(id);
		lastGiftTime.remove(id);
		addChange("giftStatus", convertToObjectNode(giftStatus));
		addChange("giftValue", convertToObjectNode(giftValue));
	}
	
	public int getSpoint() {
		return spoint;
	}

	public void setSpoint(int spoint) {
		this.spoint = spoint;
		addChange("spoint", spoint);
	}
	
	/**
	 * 添加专精点
	 * @param count
	 */
	public void addSpoint(int count) {
		this.spoint += count;
		addChange("spoint", spoint);
	}

	public boolean containCardMark(String mark) {
		return cardMark.contains(mark);
	}

	public void addCardMark(String mark) {
		cardMark.add(mark);
	}

	public static final int FIRST_NORMAL_STUDY_SKILL = 0x00001;
	public static final int FIRST_SENOR_STUDY_SKILL = 0x00002;
	public static final int REGISTER = 0x00004;
	public static final int BUY_FUND = 0x00008;
	public static final int SECOND_SENOR_RAFFLE = 0x00010;//第二次高级抓取
	public static final int UC_FIRST_LOGIN = 0x00020;//UC首次登陆礼包

	private boolean isShowAct = false;

	private AllPlayersCache allPlayersCache;

	private RoleEntity role;

	public PlayerProperty() {
		energy = energyMax();
	}

	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

	private int energyMax() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.INIT_ENERGY_NUM);
	}

	private int buyEnergyNum() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.BUY_ENERGY_NUM);
	}

	private int energyInc() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.ENERGY_INC);
	}

	private int[] energyCost() {
		return (int[]) AllGameConfig.getInstance().gconst.getConstant(GameConstId.ENERGY_COST);
	}
	
	private int[] pvpCost() {
		return (int[]) AllGameConfig.getInstance().gconst.getConstant(GameConstId.PVP_COUNT_COST);
	}

	private int[] silverCost() {
		return (int[]) AllGameConfig.getInstance().gconst.getConstant(GameConstId.SILVER_COST);
	}

	private int buySilverNum() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.BUY_SILVER_NUM);
	}

	public void addExp(int count, int from, Player player) {
		switch (from) {
		case EXP_FROM_TASK:

			break;

		default:
			break;
		}
		addExp(count, player);
	}

	public int getCoin() {
		return coin;
	}

	public void addCoin(int amount) {
		this.coin += amount;
		addChange("coin", coin);
	}

	public void getDayReward() {
		comeDays++;
		lastDailyRewardTime = TimeUtil.getCurrentTime();
		addChange("dr", 0);
		addChange("days", comeDays);
	}

	public int getRewardDay() {
		return comeDays;
	}

	/**
	 * 拉面馆固定时间给体力
	 * 
	 * @param player
	 */
	public void giveEnergy(Player player) {
		if (!TimeUtil.isSameDay(lastRewardEnergyTime)) {
			rewardEnergyStatus = 0;
		}
		Building building = AllGameConfig.getInstance().buildings.getBuilding(BuildingType.LMG.getId());
		if (ContextConfig.GIVE_ENERGY_FIX_TIME_OPEN) {
			int rewardHour = getGiveHour(building.getTime());
			if (rewardEnergyStatus != rewardHour) {
				rewardEnergyStatus = rewardHour;
				VariousItemUtil.doBonus(player, building.getGive(), LogConst.BUILDING_GIVE, true);
				addChange("ges", 0);
				player.sendResult(OperResultType.PRODUCE, VariousItemUtil.itemToJson(building.getGive()));
				lastRewardEnergyTime = TimeUtil.getCurrentTime();
			}
		} else {
			player.sendResult(OperResultType.PRODUCE, 3041);// 没到领奖时间
			ObjectNode value = jackson.createObjectNode();
			value.put("ges", 0);
			player.sendMessage(getKey(), value);
		}
	}

	public int getGiveHour(int[] time) {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int rewardHour = 0;
		for (int t : time) {
			if (hour >= t) {
				rewardHour = t;
			}
		}
		return rewardHour;
	}

	public boolean giveEnergyByTimer() {
		int maxEnergy = energyMax();
		if (energy >= maxEnergy) {
			lastAddEnergyTime = TimeUtil.getCurrentTime();
			return false;
		}
		int add_value = energyInc();
		addEnergyLimit(add_value);
		lastAddEnergyTime = TimeUtil.getCurrentTime();
		return true;
	}


	public void checkGiftStatus() {
		Map<Integer, GiftType> actGiftMap = WorldActivity.actGiftMap;
		Set<Integer> needRemoveId = Sets.newHashSet();
		for (Integer gsid : giftStatus.keySet()) {
			if(actGiftMap.get(gsid) == null) {
				needRemoveId.add(gsid);
			}
		}
		for (Integer id : needRemoveId) {
			giftStatus.remove(id);
			giftValue.remove(id);
			lastGiftTime.remove(id);
		}
		for (Integer gid : actGiftMap.keySet()) {
			if (!giftStatus.containsKey(gid)) {
				giftStatus.put(gid, 0);
			}
		}
	}
	
	/**
	 * 添加购买次数
	 */
	private void addBuyPvpTimes() {
		if (!TimeUtil.isSameDay(lastBuyPvpTime)) {
			this.buyPvpTimes = 0;
		}
		this.buyPvpTimes++;
		addChange("pt", buyPvpTimes);
		this.lastBuyPvpTime = TimeUtil.getCurrentTime();
	}
	
	/**
	 * 添加购买次数
	 */
	private void addBuyEnergy1Times() {
		if (!TimeUtil.isSameDay(lastBuyEnergyTime)) {
			this.buyEnergyTimes = 0;
		}
		this.buyEnergyTimes++;
		addChange("bet", buyEnergyTimes);
		this.lastBuyEnergyTime = TimeUtil.getCurrentTime();
	}

	/**
	 * 添加银币次数
	 */
	private void addBuySilverTimes() {
		if (!TimeUtil.isSameDay(lastBuySilverTime)) {
			this.buySilverTimes = 0;
		}
		this.buySilverTimes++;
		addChange("sbet", buySilverTimes);
		this.lastBuySilverTime = TimeUtil.getCurrentTime();
	}

	private void setEnergy(int energy) {
		this.energy = energy;
		addChange("energy", energy);
	}

	public void checkRewardEnergy(Player player) {
		if (ContextConfig.GIVE_ENERGY_FIX_TIME_OPEN) {
			Building building = AllGameConfig.getInstance().buildings.getBuilding(BuildingType.LMG.getId());
			int rewardHour = getGiveHour(building.getTime());
			if (rewardEnergyStatus != rewardHour) {
				ObjectNode value = jackson.createObjectNode();
				value.put("ges", 1);
				player.sendMessage(getKey(), value);
			}
		}
	}

	public void addVipScore(Player player, int score) {
		vipScore += score;
		addChange("vipSr", vipScore);
		int tarVipLv = config.activitys.getVipLevelByScore(vipScore);
		if (tarVipLv == this.vipLv) {
			return;
		}
		for (int i = vipLv + 1; i <= tarVipLv; i++) {
			// 给予升级奖励
			Vip vip = config.activitys.getVip(i);
			VariousItemEntry[] rewards = vip.getReward();
			if (rewards != null) {
				player.getMail().addSysItemMail(rewards, MessageCode.VIP_BONUS, MessageCode.VIP_BONUS);
			}
//			GameContext context = new GameContext();
//			context.setPlayer(player);
//			if (rewardEffectGFs != null && rewardEffectGFs.length > 0) {
//				for (IEffectGF gf : rewardEffectGFs) {
//					gf.execute(context);
//				}
//			}
		}
		setVipLv(tarVipLv);
		if(player.getPlayerSession() != null) {
			vipLevelUpEvent(player);
		}
	}

	private void vipLevelUpEvent(Player player) {
		if (player.getBuilding().getBuild(BuildingType.SYKC.getId()) != null) {
			ObjectNode json = jackson.createObjectNode();
			json.with("bd").put(BuildingType.SYKC.getId() + "", player.getBuilding().getBuild(BuildingType.SYKC.getId()).toJson(BuildingType.SYKC.getDayFinishCount(player)));
			player.sendMessage(player.getBuilding().getKey(), json);
			JsonNode openJson = player.getExam().toOpenJson(player);
			player.sendMessage(player.getExam().getKey(), openJson);
		}
		if (player.getBuilding().getBuild(BuildingType.LMG.getId()) != null) {
			BuildingType.LMG.open(player);
		}
	}

	public void addExp(int count, Player player) {
		if (count <= 0)
			return;
		int newXp = exp + count;
		Level upLevel = AllGameConfig.getInstance().levels.getLevel(level + 1);
		int levelUpExp = upLevel.getExp();
		if (newXp >= levelUpExp) {
			if (level >= AllGameConfig.getInstance().levels.getLevels().size() - 1) {
				if (exp < levelUpExp) {
					this.setExp(levelUpExp);
				}
			} else {
				// 升级了
				this.setLevel(level + 1);
				this.setExp(0);
				levelUpEvent(getLevel(), player);
				this.addExp(newXp - levelUpExp, player);

			}
		} else {
			this.setExp(newXp);
		}
	}

	private void levelUpEvent(int playerLv, Player player) {
		// 升级添加等级体力
		addEnergy(playerLv);
		player.getBuilding().checkOpenBuilding(playerLv, player);
		if (role != null) {
			allPlayersCache.updateRoleLevel(playerLv - 1, playerLv, role, player);
		}
		// 五级加入竞技场排行
		if (playerLv == 5) {
			GameRankMaster.getInstance().arenaRank.addRank(player);
		}
		// 三级打开任务累计
		if (playerLv == 3) {
			player.getTask().toOpenJson(player);
		}
		if (playerLv == 4) {
			player.getMail().addSysMail(MessageCode.MAIL_TITLE_1, MessageCode.MAIL_CONTENT_1);
		}
		// 升级日志
		LogUtil.doLevelupLog((DefaultPlayer) player);
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
		addChange("exp", exp);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		addChange("lv", level);
	}

	public void buyEnergy(Player player) {
		Vip vip = config.activitys.getVip(vipLv);
		int times = vip.getTimes()[VipConstant.BUY_ENERGY_TIMES];
		if (buyEnergyTimes >= times) {
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		int gold = IntList.getIntValueLimit(buyEnergyTimes, energyCost());
		VariousItemEntry needGold = new VariousItemEntry("gold", gold);
		int doBonus = VariousItemUtil.doBonus(player, needGold, LogConst.BUY_ENERGY, false);
		if (doBonus != MessageCode.OK) {
			player.sendMiddleMessage(doBonus);
			return;
		}
		addBuyEnergy1Times();
		addEnergy(buyEnergyNum());
		player.getTask().dispatchEvent(player, TaskType.BUY_ENERGY);
	}
	
	public void buyPVPTimes(Player player) {
		Vip vip = config.activitys.getVip(vipLv);
		int times = vip.getTimes()[VipConstant.PVP_TIMES];
		if (buyPvpTimes >= times) {
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		int gold = IntList.getIntValueLimit(buyPvpTimes, pvpCost());
		VariousItemEntry needGold = new VariousItemEntry("gold", gold);
		int doBonus = VariousItemUtil.doBonus(player, needGold, LogConst.BUY_PVP_TIMES, false);
		if (doBonus != MessageCode.OK) {
			player.sendMiddleMessage(doBonus);
			return;
		}
		addBuyPvpTimes();
		player.getBuilding().addPvpTimes(player);
	}

	public void buySilver(Player player) {
		Vip vip = config.activitys.getVip(vipLv);
		int times = vip.getTimes()[VipConstant.BUY_SILVER_TIMES];
		if (buySilverTimes >= times) {
			player.sendMiddleMessage(MessageCode.FAIL);
			return;
		}
		int gold = IntList.getIntValueLimit(buySilverTimes, silverCost());
		VariousItemEntry needGold = new VariousItemEntry("gold", gold);
		int doBonus = VariousItemUtil.doBonus(player, needGold, LogConst.BUY_SILVER, false);
		if (doBonus != MessageCode.OK) {
			player.sendMiddleMessage(doBonus);
			return;
		}
		addBuySilverTimes();
		addSilver(buySilverNum());
		LogUtil.doAcquireLog((DefaultPlayer) player, LogConst.BUY_SILVER, buySilverNum(), player.getProperty().getSilver(), "silver");
	}
	

	// public VariousItemEntry getVipItem() {
	// if (vipLv > 0) {
	// return config.activitys.getVip(vipLv).getItem()[0];
	// }
	// return VariousItemEntry.EMPTY_ITEM_ENTRY;
	// }

	public boolean getVipFun(int funType) {
		if (vipLv > 0) {
			return config.activitys.getVip(vipLv).getFun()[funType] == 1;
		}
		return false;
	}

	public void addGold(int amount) {
		this.gold += amount;
		// logger.info("this.gold change {}",amount);
		addChange("gold", gold);
	}
	
	public void addArenaPoint(int amount)
	{
		this.arenapoint += amount;
		addChange("arenapoint", arenapoint);
	}

	public void addSilver(int amount) {
		this.silver += amount;
		addChange("silver", silver);
	}

	public void addMonthlyPayment(int days) {
		if (this.monthlyPayment < TimeUtil.getCurrentTime()) {
			this.monthlyPayment = TimeUtil.getEndOfDays(TimeUtil.getCurrentTime(), days);
		} else {
			this.monthlyPayment = TimeUtil.getEndOfDays(this.monthlyPayment, days);
		}

		addChange("mpay", this.monthlyPayment);
		addChange("mCard", getMonthlyPayDays());
	}

	// 更新领取月卡奖励时间
	public void updateMPTime() {
		this.lastMP = TimeUtil.getCurrentTime();
		addChange("lastMP", this.lastMP);
	}

	/**
	 * 剩余月卡天数
	 * 
	 * @return
	 */
	public int getMonthlyPayDays() {
		if (monthlyPayment < TimeUtil.getCurrentTime()) {
			return 0;
		}
		if (TimeUtil.isSameDay(monthlyPayment)) {
			if (TimeUtil.isSameDay(lastMP)) {
				return 0;
			} else {
				return 1;
			}
		}
		return TimeUtil.getDays(System.currentTimeMillis(), monthlyPayment * 1000L);
	}

	public int getLastMP() {
		return this.lastMP;
	}

	public int getMonthlyPayment() {
		return this.monthlyPayment;
	}

	/**
	 * 添加体力
	 * 
	 * @param amount
	 */
	public void addEnergy(int amount) {
		setEnergy(this.energy + amount);
	}

	/**
	 * 添加具有最大上限的体力
	 * 
	 * @param amount
	 */
	public void addEnergyLimit(int amount) {
		int maxEnergy = energyMax();
		int targetEnergy = energy + amount;
		int realEnergy = targetEnergy > maxEnergy ? maxEnergy : targetEnergy;
		setEnergy(realEnergy);
	}

	public int getVipScore() {
		return vipScore;
	}

	public int getVipLv() {
		return vipLv;
	}

	public boolean isVip() {
		return vipLv > 0;
	}
	

	public boolean isLockLastSkill() {
		return vipLv < 3;
	}
	
	public void setVipLv(int vipLv) {
		this.vipLv = vipLv;
		addChange("vipLv", vipLv);
	}

	/**
	 * 是否有足够的钱，
	 * 
	 * @param gold
	 *            金币
	 * @param silver
	 *            钻石
	 * @return
	 */
	public boolean hasEnoughMoney(Integer gold, Integer silver, Integer coin) {
		if (gold != null && this.gold < gold) {
			return false;
		}
		if (silver != null && this.silver < silver) {
			return false;
		}
		if (coin != null && this.coin < coin) {
			return false;
		}
		
//		if (arenapoint != null && this.arenapoint < arenapoint) {
//			return false;
//		}
		
		return true;
	}
	
	public boolean hasEnoughArenaPoint(int value)
	{
		if (arenapoint >= value) {
			return true;
		}
		return false;
	}

	public void changeMoney(Integer gold, Integer silver, Integer coin) {
		if (gold != null && gold != 0) {
			addGold(gold);
		}
		if (silver != null && silver != 0) {
			addSilver(silver);
		}
		if (coin != null && coin != 0) {
			addCoin(coin);
		}
	}

	public void changeMoney(Integer gold, Integer silver) {
		changeMoney(gold, silver, null);
	}

	public boolean hasEnoughMoney(String type, Integer value) {
		if ("gold".equals(type)) {
			return hasEnoughMoney(value, null, null);
		} else if ("silver".equals(type)) {
			return hasEnoughMoney(null, value, null);
		} else if ("coin".equals(type)) {
			return hasEnoughMoney(null, null, value);
		} else if("arenapoint".equals(type)){
			return hasEnoughArenaPoint(value);
		}
		return false;
	}

	public boolean hasEnoughEnergy(int value) {
		if (energy >= value) {
			return true;
		}
		return false;
	}
	
	public boolean hasEnoughSpoint(int value) {
		if (spoint >= value) {
			return true;
		}
		return false;
	}

	public int getTakeSilverTimes() {
		return takeSilverTimes;
	}

	public void setTakeSilverTimes(int takeSilverTimes) {
		this.takeSilverTimes = takeSilverTimes;
	}

	public void changeMoney(String type, Integer value, int reason, Player player) {
		if ("gold".equals(type)) {
			changeMoney(value, 0);
			if(value < 0) {
				GiftType.CONSUME_1DAY.addValue(player, Math.abs(value));
			}
		} else if ("silver".equals(type)) {
			changeMoney(0, value);
		} else if ("coin".equals(type)) {
			changeMoney(null, null, value);
		} else if("arenapoint".equals(type)){
			addArenaPoint(value);
		}
	}
	
	public void setOnline() {
		if(AllGameConfig.getInstance().env == RuntimeEnv.OTHER) {
			setGold(25555550);
			setEnergy(100);
			setLevel(50);
			setSpoint(1000000);
			setSilver(5000000);
			setVipLv(6);
		}
		int currentTime = TimeUtil.getCurrentTime();
		if (firstOnlineTime == 0) {
			firstOnlineTime = currentTime;
			comeDays = 1;
			lastDailyRewardTime = 0;
		}
		if (lastDailyRewardTime != 0 && !TimeUtil.isSameDay(lastDailyRewardTime)) {

			if (comeDays > 28) {
				comeDays = 1;
			}
		}
		if (!TimeUtil.isSameDay(lastOnlineTime)) {
			// 添加累计登陆天数
			takeSilverTimes = 0;
			//isShowAct = true;
			// 发送活动
			
		}
		isShowAct = true;
		if(AllGameConfig.getInstance().env == RuntimeEnv.TEST) {
			if(firstOnlineTime != currentTime) {
				//测试环境vip三倍增长
				int days = TimeUtil.getDays(firstOnlineTime, currentTime);
				this.vipLv = IntList.getIntValueLimit(days, VIP_DAY_VALUE);
				if(this.vipLv > 12) {
					this.vipLv = 12;
				}
			}
		}
		
		
		
		// 处理行动力
		int energyMax = energyMax();
		if (energy < energyMax) {
			Duration duration = new Duration(lastAddEnergyTime * 1000L, currentTime * 1000L);
			int standardMinutes = (int)duration.getStandardMinutes();
			if (standardMinutes > 0) {
				long diffCount = standardMinutes / 30;
				if(diffCount > 0) {
					long todayPlusMinute = TimeUtil.getTodayPlusMinute(-standardMinutes%30);
					lastAddEnergyTime = (int)(todayPlusMinute/1000f);
					int targetEnergy = (int) diffCount * energyInc() + energy;
					this.energy = targetEnergy > energyMax ? energyMax : targetEnergy;
				}
			}
		}
		if (!TimeUtil.isSameDay(currentTime,lastBuyEnergyTime)) {
			buyEnergyTimes = 0;
			lastBuyEnergyTime = currentTime;
		}
		if (!TimeUtil.isSameDay(currentTime,lastBuySilverTime)) {
			buySilverTimes = 0;
			lastBuySilverTime = currentTime;
		}
		if (!TimeUtil.isSameDay(currentTime,lastBuyPvpTime)) {
			buyPvpTimes = 0;
			lastBuyPvpTime = currentTime;
		}
		if (!TimeUtil.isSameDay(currentTime,lastRewardEnergyTime)) {
			rewardEnergyStatus = 0;
		}
		lastOnlineTime = currentTime;

		// 处理副本重置
		if (!TimeUtil.isSameDay(lastFubenReset)) {
			clearFubenReset(1);
			clearFubenReset(2);
		}
		if (!TimeUtil.isSameDay(lastFubenUse)) {
			clearFubenUse(1);
			clearFubenUse(2);
		}

	}

	public void setOffline() {
		// allPlayersCache.updatePlayerInfo(player);
		// allPlayersCache.updatePlayerInfoToDB(player);
	}

	public String getLogNet() {
		if (AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
			return String.valueOf(100000 + Integer.parseInt(net));
		}
		return net;
	}
	
	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	@Override
	public String getKey() {
		return "player";
	}

	public int getLastOnlineTime() {
		return lastOnlineTime;
	}

	@Override
	public JsonNode toWholeJson() {
		checkGiftStatus();
		ObjectNode obj = jackson.createObjectNode();
		obj.put("gold", gold);
		obj.put("silver", silver);
		obj.put("arenapoint", arenapoint);
		obj.put("lv", level);
		obj.put("exp", exp);
		obj.put("days", comeDays);
		obj.put("dr", canDailyReward());
		obj.put("energy", energy);
		obj.put("bet", buyEnergyTimes);
		obj.put("sbet", buySilverTimes);
		obj.put("userId", userId);
		obj.put("vipSr", vipScore);
		obj.put("vipLv", vipLv);
		obj.put("fpaid", payStatus());
		obj.put("beastOpen", ActivityType.BEAST_ORDER.isActive() ? 1 : 0);
		obj.put("fn", (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TAKE_NUM) - takeSilverTimes);
		obj.put("coin", coin);
		obj.put("fbrn", fubenResetInfo());
		obj.put("fbu", fubenUseInfo());
		obj.put("efbrn", eliteFubenResetInfo());
		obj.put("efbu", eliteFubenUseInfo());
		obj.put("spoint", spoint);
		obj.put("giftStatus",  convertToObjectNode(giftStatus));
		obj.put("giftDay",  converGiftDayJSON());
		obj.put("giftValue", convertToObjectNode(giftValue));
		obj.put("fund", containStatus(BUY_FUND) ? 1 : 0);
		obj.put("operStatus", operStatus);
		//obj.put("paid", getPaid());
		obj.put("ch", getCh());
		obj.put("mCard", getMonthlyPayDays());
		obj.put("pt",buyPvpTimes);
		obj.put("isOpenMonthCard", (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.PREFER_IS_OPEN_MONTHCARD));
		obj.put("isOpenGiftCode", (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.PREFER_IS_OPEN_GIFTCODE));
		return obj;
	}
	
	private ObjectNode converGiftDayJSON() {
		ObjectNode obj = jackson.createObjectNode();
		for (Integer id : giftStatus.keySet()) {
			GiftInfo giftInfo = config.gifts.getGiftInfo(id);
			if(giftInfo.getStartDayString() == null) {
				continue;
			}else {
				ObjectNode info = jackson.createObjectNode();
				info.put("start",giftInfo.getStartDayString());
				info.put("end",giftInfo.getEndDayString());
				obj.put(id+"", info);
			}
		}
		return obj;
	}
	
	public JsonNode toSimpleJson() {
		ObjectNode obj = jackson.createObjectNode();
		obj.put("gold", gold);
		obj.put("silver", silver);
		obj.put("arenapoint", arenapoint);
		obj.put("lv", level);
		obj.put("exp", exp);
		obj.put("userId", userId);
		obj.put("vipSr", vipScore);
		obj.put("vipLv", vipLv);
		obj.put("coin", coin);
		obj.put("spoint", spoint);
		obj.put("ch", getCh());
		obj.put("ft", this.firstOnlineTime);
		obj.put("lt", this.lastOnlineTime);
		obj.put("ban", this.banPlayerLv>0?1:0);
		return obj;
	}

	public int getFirstOnlineTime() {
		return firstOnlineTime;
	}

	public int getMoney(String currency) {
		if (currency.equals("silver")) {
			return silver;
		} else if (currency.equals("gold")) {
			return gold;
		} else if (currency.equals("coin")) {
			return coin;
		}else if(currency.equals("arenapoint")){
			return this.arenapoint;
		}else {
			return -1;
		}
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getSilver() {
		return silver;
	}

	public void setSilver(int silver) {
		this.silver = silver;
	}
	
	public int getArenaPoint()
	{
		return this.arenapoint;
	}

	/**
	 * @return 1-充值过 0-没充过
	 */
	public int getPaid() {
		return payStatus.size()>0?1:0;
	}
	
	public int getPayStatus(String itemId) {
		Integer status = payStatus.get(itemId);
		if (status == null) {
			return 0;
		}
		return status.intValue();
	}

	public void setPaid(String itemId) {
		payStatus.put(itemId, 1);
		addChange("fpaid", payStatus());
	}
	public void clearPaid() {
		payStatus = new HashMap<String, Integer>();
		addChange("fpaid", payStatus());
	}

	public AllPlayersCache getAllPlayersCache() {
		return allPlayersCache;
	}

	public void setAllPlayersCache(AllPlayersCache allPlayersCache) {
		this.allPlayersCache = allPlayersCache;
	}

	// 能否领取每日奖励 1-能 0-不能
	public int canDailyReward() {
		if (TimeUtil.isSameDay(lastDailyRewardTime)) {
			return 0;
		}
		return 1;
	}

	public boolean isShowAct() {
		return isShowAct;
	}

	public void setShowAct(boolean isShowAct) {
		this.isShowAct = isShowAct;
	}

	public int getLastStopGameGiveTime() {
		return lastStopGameGiveTime;
	}

	public void setLastStopGameGiveTime(int lastStopGameGiveTime) {
		this.lastStopGameGiveTime = lastStopGameGiveTime;
	}

	public int getFubenReset(int advType, int cid, int sid) {
		Integer count = 0;
		if (advType == 1) {	//	普通副本
			Integer v = fubenReset.get(cid + "_" + sid);
			if (v != null) {
				count = v;
			}
		} else if (advType == 2) {	//	精英副本
			Integer v = eliteFubenReset.get(cid + "_" + sid);
			if (v != null) {
				count = v;
			}
		}
		return count;
	}

	public void addFubenReset(int advType, int cid, int sid, int count) {
		String key = cid + "_" + sid;
		if (advType == 1) {	//	普通副本
			Integer value = fubenReset.get(key);
			if (value == null) {
				value = 0;
			}
			fubenReset.put(key, count + value);
			lastFubenReset = TimeUtil.getCurrentTime();
			addChange("fbrn", fubenResetInfo());
		} else if (advType == 2) {	//	精英副本
			Integer value = eliteFubenReset.get(key);
			if (value == null) {
				value = 0;
			}
			eliteFubenReset.put(key, count + value);
			eliteLastFubenReset = TimeUtil.getCurrentTime();
			addChange("efbrn", eliteFubenResetInfo());
		}
	}

	public int getFubenUse(int advType, int cid, int sid) {
		if (advType == 1) {
			Integer count = fubenUse.get(cid + "_" + sid);
			if (count == null) {
				return 0;
			} else {
				return count.intValue();
			}
		} else {
			Integer count = eliteFubenUse.get(cid + "_" + sid);
			if (count == null) {
				return 0;
			} else {
				return count.intValue();
			}
		}
	}

	public void addFubenUse(int advType, int cid, int sid, int count) {
		if (advType == 1) {
			String key = cid + "_" + sid;
			Integer value = fubenUse.get(key);
			if (value == null) {
				value = 0;
			}
			fubenUse.put(key, count + value);
			lastFubenUse = TimeUtil.getCurrentTime();
			addChange("fbu", fubenUseInfo());
		} else {
			String key = cid + "_" + sid;
			Integer value = eliteFubenUse.get(key);
			if (value == null) {
				value = 0;
			}
			this.eliteFubenUse.put(key, count + value);
			this.eliteLastFubenReset = TimeUtil.getCurrentTime();
			addChange("efbu", eliteFubenUseInfo());
		}
	}

	public void resetFubenUse(int advType, int cid, int sid) {
		String key = cid + "_" + sid;
		if (advType == 1) {
			fubenUse.put(key, 0);
			addChange("fbu", fubenUseInfo());
		} else if (advType == 2) {
			eliteFubenUse.put(key, 0);
			addChange("efbu", eliteFubenUseInfo());
		}
	}

	public void clearFubenReset(int advType) {
		if (advType == 1) {
			fubenReset.clear();
			lastFubenReset = TimeUtil.getCurrentTime();
			addChange("fbrn", fubenResetInfo());
		} else if (advType == 2) {
			eliteFubenReset.clear();
			eliteLastFubenReset = TimeUtil.getCurrentTime();
			addChange("efbrn", eliteFubenResetInfo());
		}
	}

	public void clearFubenUse(int advType) {
		if (advType == 1) {
			fubenUse.clear();
			lastFubenUse = TimeUtil.getCurrentTime();
			addChange("fbu", fubenUseInfo());
		} else if (advType == 2) {
			eliteFubenUse.clear();
			eliteLastFubenReset = TimeUtil.getCurrentTime();
			addChange("efbu", eliteFubenUseInfo());
		}
	}

	public int getLastFubenReset() {
		return lastFubenReset;
	}

	public int getLastFubenUse() {
		return lastFubenUse;
	}
	
	public int getEliteLastFubenReset() {
		return this.eliteLastFubenReset;
	}
	
	public int getEliteLastFubenUse() {
		return this.eliteLastFubenUse;
	}

	public boolean containStatus(final int status) {
		return (operStatus & status) != 0;
	}

	public void addStatus(final int status) {
		operStatus |= status;
		addChange("operStatus", operStatus);
	}

	public ArrayNode fubenUseInfo() {
		ArrayNode array = jackson.createArrayNode();
		for (String key : fubenUse.keySet()) {
			ArrayNode node = jackson.createArrayNode();
			node.add(key);
			node.add(fubenUse.get(key));
			array.add(node);
		}
		return array;
	}
	public ArrayNode fubenResetInfo() {
		ArrayNode array = jackson.createArrayNode();
		for (String key : fubenReset.keySet()) {
			ArrayNode node = jackson.createArrayNode();
			node.add(key);
			node.add(fubenReset.get(key));
			array.add(node);
		}
		return array;
	}
	public ArrayNode eliteFubenResetInfo() {
		ArrayNode arr = jackson.createArrayNode();
		for (String k : eliteFubenReset.keySet()) {
			ArrayNode node = jackson.createArrayNode();
			node.add(k);
			node.add(eliteFubenReset.get(k));
			arr.add(node);
		}
		return arr;
	}
	public ArrayNode eliteFubenUseInfo() {
		ArrayNode arr = jackson.createArrayNode();
		for (String k : eliteFubenUse.keySet()) {
			ArrayNode node = jackson.createArrayNode();
			node.add(k);
			node.add(eliteFubenUse.get(k));
			arr.add(node);
		}
		return arr;
	}
	
	public ArrayNode payStatus() {
		ArrayNode array = jackson.createArrayNode();
		List<Recharge> list = AllGameConfig.getInstance().activitys.rechargeList;
		for (int i=1;i<list.size(); i++) {
			Recharge recharge = list.get(i);
			Integer status = payStatus.get(recharge.getId());
			if (status != null && status.intValue() == 1) {
				array.add(1);
			} else {
				array.add(0);
			}
		}
		return array;
	}
	
	// 更新领取vip每日奖励时间
	public void updateLVPTime() {
		this.lastVipPrize = TimeUtil.getCurrentTime();
		addChange("lvp", this.lastVipPrize);
	}
}
