package io.nadron.example.lostdecade;

import io.nadron.app.GameRoom;
import io.nadron.app.PlayerSession;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.GameRoomSession;
import io.nadron.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.event.impl.SessionMessageHandler;
import io.nadron.service.GameStateManagerService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Beast;
import com.dol.cdf.common.bean.Recharge;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.bean.Vip;
import com.dol.cdf.common.collect.Pair;
import com.dol.cdf.common.config.ActivityConfigManager;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.common.entities.GlobalProps;
import com.dol.cdf.common.entities.LineProps;
import com.dol.cdf.common.entities.MailGiveProps;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jelly.activity.ActivityType;
import com.jelly.activity.WorldActivity;
import com.jelly.game.SessionCommandHandler;
import com.jelly.hero.HeroState;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.cache.UnionCacheService;
import com.jelly.player.PlayerProperty;
import com.jelly.rank.ArenaRank;
import com.jelly.rank.GameRankMaster;
import com.jelly.rank.RankModel;
import com.jelly.team.TeamManager;

/**
 * The onLogin method is overriden so that incoming player sessions can be
 * initialzied with event handlers to do user logic. In this scenario, the only
 * thing the handler does is to patch incoming messages to the GameRoom which in
 * turn has the game logic and state. In more real-world scenarios, the session
 * handler can have its own logic, for e.g. say validation to prevent cheating,
 * filtering, pre-processing of event etc.
 * 
 * @author Abraham Menacherry
 * 
 */
// @EnableScheduling
public class LDRoom extends GameRoomSession {
	private static final int canvasWidth = 512;
	private static final int canvasHeight = 480;
	private static final Logger logger = LoggerFactory.getLogger(LDRoom.class);
	private ObjectCacheService objectCacheService;
	private AllPlayersCache allPlayersCache;
	private UnionCacheService unionCacheService;
	private WorldActivity worldActivity;
	private GlobalProps globalProps;
	private LineProps lineProps;

	public ObjectCacheService getObjectCacheService() {
		return objectCacheService;
	}

	public AllPlayersCache getAllPlayersCache() {
		return allPlayersCache;
	}

	public WorldActivity getWorldActivity() {
		return worldActivity;
	}

	public LDRoom(GameRoomSessionBuilder builder, ObjectCacheService objectCacheService, AllPlayersCache allPlayersCache, WorldActivity worldActivity) {
		super(builder);
		this.objectCacheService = objectCacheService;
		this.allPlayersCache = allPlayersCache;
		this.worldActivity = worldActivity;
		// addHandler(new GameSessionHandler(this));
		// List<UnionEntity> allunion = unionCacheService.getAllunion();
		// for (UnionEntity unionEntity : allunion) {
		//
		// }
	}
	
	public LineProps getLineProps(){
		return lineProps;
	}
	
	public GlobalProps getGlobalProps() {
		return globalProps;
	}
	
	public void init() {
		globalProps = objectCacheService.getObject(new GlobalProps(), GlobalProps.class);
		globalProps.setObjectCache(objectCacheService);
		
		lineProps = objectCacheService.getObject(new LineProps(), LineProps.class);
		lineProps.setObjectCache(objectCacheService);
	}

	// 12,18点拉面馆奖励体力
	@Scheduled(cron = "1 0 12,18 * * ?")
	public void giveEnertyOpen() {
		ContextConfig.GIVE_ENERGY_FIX_TIME_OPEN = true;
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("sysGiveEnergy", ContextConfig.SYS_KEY);
		sendBroadcast(Events.networkEvent(obj, DeliveryGuarantyOptions.INSIDE));
		logger.info("sysGiveEnergy time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
	}
	
	// 每周日晚上12：00 10 分执行
	@Scheduled(cron = "1 10 0 * * SUN")
	public void clearTeamMemberPaiQianCiShu() {
		TeamManager.getSingleton().clearTeamMemberPaiCishu();
		logger.info("clearTeamMemberPaiQianCiShu time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
//		例  "0 0 12 ? * WED" 在每星期三下午12:00 执行,
	}
	
	//每天12点零五秒的
	@Scheduled(cron = "3 0 0 * * ?")
	public void oneDayCron() {
		worldActivity.doOneDayCron();
		logger.info("oneDayCron time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
	}
	
	//每天5点零一秒的
	@Scheduled(cron = "1 0 5 * * ?")
	public void caclRank() {
		GameRankMaster.getInstance().caculateTopList();
		logger.info("caclRank time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
	}

	// 半个小时加体力
	@Scheduled(cron = "0 0/30 * * * ?")
	public void addEnertyByTimer() {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("sysAddEnergy", ContextConfig.SYS_KEY);
		sendBroadcast(Events.networkEvent(obj, DeliveryGuarantyOptions.INSIDE));
		logger.info("addEnertyByTimer time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
	}
	
	// 14,20点拉面馆去掉给体力
	@Scheduled(cron = "0 0 14,20 * * ?")
	public void giveEnertyClose() {
		ContextConfig.GIVE_ENERGY_FIX_TIME_OPEN = false;
	}

	//	每45分钟对服务器内所有军团数据做一次存档
	@Scheduled(cron = "0 15/45 * * * ?")
	public void saveTeams() {
		TeamManager.getSingleton().saveAllTeams(false);
		logger.info("saveTeams time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
	}
	
	//	半个小时更新一次军团排名
	@Scheduled(cron = "0 20/40 * * * ?")
	public void calcTeamRank() {
		TeamManager.getSingleton().calcTeamRank();
		logger.info("calcTeamRank time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
	}
	
	//	每天保存一次竞技场排名数据
	@Scheduled(cron = "0 0 3 * * ?")
	public void saveArenaRank() {
		GameRankMaster.getInstance().saveRank();
		logger.info("saveArenaRank Complete time:{}", TimeUtil.formatDateLong(System.currentTimeMillis()));
	}
	
	public LDRoom(GameRoomSessionBuilder builder) {
		super(builder);
	}

	public boolean liftPlayer(String name) {
		String guid = allPlayersCache.getPlayerIdByName(name);
		if(guid != null) {
			globalProps.unBanPlayer(guid);
			DefaultPlayer player = objectCacheService.getCache(guid, DefaultPlayer.class);
			if(player != null) {
				player.unBanPlayer();
				getObjectCacheService().putObject(player);
				return true;
			}
			
		}
		return false;
	}
	
	public boolean banPlayer(String name)
	{
		String guid = allPlayersCache.getPlayerIdByName(name);
		if(guid != null) {
			globalProps.addBanPlayer(guid);
			
			PlayerSession playerSession = getSessions().get(guid);
			if (playerSession != null) {
				disconnectSession(playerSession);
				if (null != playerSession.getTcpSender()) {
					playerSession.getTcpSender().close();
				}
			}
			
			DefaultPlayer player = getObjectCacheService().getCache(guid, DefaultPlayer.class);
			if(player != null){
				player.banPlayer();
				player.logoff();
				getObjectCacheService().putObject(player);
				player = null;	
			}
			
			return true;
		}
		return false;
	}
	@Override
	public void onLogin(final PlayerSession playerSession) {
		try {
			long startTime = System.nanoTime();
			playerSession.addHandler(new SessionCommandHandler(playerSession, objectCacheService, this.getSessions(), allPlayersCache, worldActivity));
			playerSession.getPlayer().sendPlayerWholeJSON();
			long time = System.nanoTime() - startTime;
			logger.info("onLogin cost time {} ms..", time / (1000 * 1000));

			boolean showAct = playerSession.getPlayer().getProperty().isShowAct();
			// showAct = true;// test should remove
			if (showAct) {
				worldActivity.sendAllActivityMsg(playerSession.getPlayer(), 1);
				playerSession.getPlayer().getProperty().setShowAct(false);
			}
			if (playerSession.getPlayer().getProperty().getLevel() >= 3) {
				playerSession.getPlayer().getActivity().dispatchEvent(ActivityType.LOGIN_ITEM, playerSession.getPlayer());
			}
			playerSession.getPlayer().getActivity().checkAndClear(worldActivity);
			// 月卡奖励
			PlayerProperty pp = playerSession.getPlayer().getProperty();
			if (!TimeUtil.isSameDay(pp.getLastMP()) && pp.getMonthlyPayDays() > 0) {
				ActivityConfigManager config = AllGameConfig.getInstance().activitys;
				Recharge recharge = config.getRechargeMP();
				if (TimeUtil.isSameDay(pp.getMonthlyPayment())) {
					playerSession.getPlayer().getMail().addSysItemMail("gold", recharge.getDayGive(), MessageCode.MONTHLY_PAY_TID, MessageCode.MONTHLY_PAY_REWARD, String.valueOf(recharge.getDayGive()), "0");
				} else {
					playerSession.getPlayer().getMail().addSysItemMail("gold", recharge.getDayGive(), MessageCode.MONTHLY_PAY_TID, MessageCode.MONTHLY_PAY_REWARD, String.valueOf(recharge.getDayGive()), String.valueOf(pp.getMonthlyPayDays()));
				}
				pp.updateMPTime();
			}
			//vip login prize every day 
			if(pp.getVipLv()>0 && !TimeUtil.isSameDay(pp.getLastVipPrize())){
				ActivityConfigManager config = AllGameConfig.getInstance().activitys;
				Vip vip = config.getVip(pp.getVipLv());
				if(vip.getDayprize() != null && vip.getDayprize().length>0 && vip.getMailt() != null && vip.getMailc() != null){
					playerSession.getPlayer().getMail().addSysItemMail(vip.getDayprize(), vip.getMailt(), vip.getMailc());
					pp.updateLVPTime();
				}
			}
			//停服补偿奖励
			List<MailGiveProps> mails = globalProps.getMails();
			for (MailGiveProps mp : mails) {
				if(pp.getLastStopGameGiveTime() < mp.getSendTime() && mp.getSendTime() > pp.getFirstOnlineTime() ) {
					playerSession.getPlayer().getMail().addSysItemMail(mp.getReward(),mp.getTitle(),mp.getContent());
					pp.setLastStopGameGiveTime(mp.getSendTime());
				}
			}
			
			//UC首日登录礼包
			if (pp.getCh().equals("000255") && TimeUtil.isSameDay(WorldActivity.serverStartDate.getTime(), pp.getFirstOnlineTime()*1000L) 
					&& TimeUtil.getDays(WorldActivity.serverStartDate.getTime(), System.currentTimeMillis()) >= 1) {
				if (!pp.containStatus(PlayerProperty.UC_FIRST_LOGIN)) {
					playerSession.getPlayer().getMail().addSysItemMail("<5126;1>","九游登录礼包","九游登录礼包");
					pp.addStatus(PlayerProperty.UC_FIRST_LOGIN);
				} 
			}
			
			// 判断赫德首充重置
			if (AllGameConfig.getInstance().env == RuntimeEnv.STAGE
					|| AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
				int firstPayReset = (Integer) AllGameConfig.getInstance().gconst
						.getConstant(GameConstId.FIRST_PAY_RESET);
				if (pp.getFirstPayResetTimes() < firstPayReset) {
					pp.clearPaid();
					pp.setFirstPayResetTimes(firstPayReset);
				}
			}
			
			/// 登录，重新重置下
			DefaultPlayer player = (DefaultPlayer) playerSession.getPlayer();
			
			List<Integer> heros = TeamManager.getSingleton().popCancelRolesFromTeamArmy(player);
			if (heros != null && heros.size()>0) {	
				for(int h=0;h<heros.size();h++){
					Integer hid = heros.get(h);
					player.getHeros().removeHeroState(hid, HeroState.TEAM_ARMY_STATE);
				}
			}
			
			boolean csign = player.getClearDataSign();
			if(csign == true){
				player.getTeam().setLeaveTeam("");
				player.getTeam().clearApplyTeams();
				player.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
			}
			player.setClearDataSign(false);
			
		} catch (Exception e) {
			logger.error("onLogin error", e);
		}
	}

	public void kickRoomAllPlayers() {
		logger.info("LDRoom is kickRoomAllPlayers and close game");
		ContextConfig.GAME_OPEN = false;
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("gameover", "1");
		sendBroadcast(Events.networkEvent(obj));
		//存储玩家数据
		for (PlayerSession ps : getSessions().values()) {
			if(ps != null) {
				try {
					ps.getPlayer().logoff();
				} catch (Exception e) {
					logger.error("",e);
				}
				
			}
		}
	}
	
	
	public int getOnlineCount() {
		return getSessions().size();
	}

	public void payReward(int[] values, String rewards, int tid) {
		long startTime = System.nanoTime();
		String[] itemStrings = rewards.split(",");
		Map<String, Long> payMap = worldActivity.getPayMap();
		for (Entry<String, Long> entry : payMap.entrySet()) {
			String guid = entry.getKey();
			Long goldNum = entry.getValue();
			String rewardItemString = null;
			for (int i = 0; i < values.length; i++) {
				if (goldNum >= values[i]) {
					rewardItemString = itemStrings[i];
					break;
				}
			}
			sysGiveItem(rewardItemString, guid, tid);
		}
		long time = System.nanoTime() - startTime;
		logger.info("payReward cost time {} ms..", time / (1000 * 1000));
	}

	public void payOrderReward(int[] values, String rewards, int tid) {
		long startTime = System.nanoTime();
		String[] itemStrings = rewards.split(",");
		Map<String, Long> payMap = worldActivity.getSortedPayMap();
		int i = 0;
		// 1;3;10
		for (Entry<String, Long> entry : payMap.entrySet()) {
			String rewardItemString = null;
			String guid = entry.getKey();
			int j = 0;
			for (int val : values) {
				if (i + 1 <= val) {
					rewardItemString = itemStrings[j];
					break;
				}
				j++;
			}
			sysGiveItem(rewardItemString, guid, tid);

			i++;
			if (i >= values[values.length - 1]) {
				break;
			}
		}
		long time = System.nanoTime() - startTime;
		logger.info("payOrderReward cost time {} ms..", time / (1000 * 1000));
	}

	public void beastOrderReward(String rewards) {
		long startTime = System.nanoTime();
		String[] splitStr = rewards.split(",");
		int[] values = StringHelper.getIntList(splitStr[0].split(";"));
		int[] counts = StringHelper.getIntList(splitStr[1].split(";"));
		int beastId = (int) Float.parseFloat(ActivityType.BEAST_ORDER.getValue());
		Beast beast = AllGameConfig.getInstance().beast.getBeast(beastId);
		Integer itemId = beast.getItemId();
		TreeMap<String, Long> map = worldActivity.getSortedBeastActHurtsMap();

		String topOneNameString = "";
		List<String> topFiveName = Lists.newArrayList();
		int i = 0;
		for (Entry<String, Long> entry : map.entrySet()) {
			String name = entry.getKey();
			String guid = allPlayersCache.getPlayerIdByName(name);
			String rewardItemString = null;
			if (i == 0) {
				rewardItemString = WorldActivity.top1_reward;
				topOneNameString = name;
				sysGiveItem(rewardItemString, guid, MessageCode.MAIL_BEAST_TITILE2, MessageCode.MAIL_BEAST_CONTENT2, WorldActivity.top1ItemName, WorldActivity.top1ItemNum);
			} else if(i > 0 && i < 5) {
				rewardItemString = WorldActivity.top5_reward;
				topFiveName.add(name);
				sysGiveItem(rewardItemString, guid, MessageCode.MAIL_BEAST_TITILE2, MessageCode.MAIL_BEAST_CONTENT2, WorldActivity.top5ItemName, WorldActivity.top5ItemNum);
			}else {
				rewardItemString = WorldActivity.top50_reward;
				sysGiveItem(rewardItemString, guid, MessageCode.MAIL_BEAST_TITILE2, MessageCode.MAIL_BEAST_CONTENT2_1, WorldActivity.top50ItemName, WorldActivity.top50ItemNum);
			}
			i++;
			if (i == 50) {
				break;
			}
		}

		String topFiveNamesString = StringUtils.join(topFiveName, "、");

		int size = map.size();
		List<String> luckyName;
		if (size <= WorldActivity.luckyPersionCount) {
			luckyName = Lists.newArrayList(map.keySet());
		} else {
			luckyName = Lists.newArrayList();
			List<String> tempList = Lists.newArrayList(map.keySet());
			Set<Integer> luckyIdx = Sets.newHashSet();
			for (int j = 0; j < size; j++) {
				luckyIdx.add(Rnd.get(size));
				if (luckyIdx.size() >= WorldActivity.luckyPersionCount) {
					break;
				}
			}
			for (Integer idx : luckyIdx) {
				luckyName.add(tempList.get(idx));
			}
		}

		for (String name : luckyName) {
			String guid = allPlayersCache.getPlayerIdByName(name);
			sysGiveItem(WorldActivity.luckyReward, guid, MessageCode.MAIL_BEAST_TITILE3, MessageCode.MAIL_BEAST_CONTENT3, WorldActivity.luckyItemName, WorldActivity.luckyItemNum);
		}

		String luckyPlayerNames = StringUtils.join(luckyName, "、");

		i = 0;

		// 1;3;10
		for (Entry<String, Long> entry : map.entrySet()) {
			String rewardItemString = null;
			String name = entry.getKey();
			Long hurtVal = entry.getValue();
			int j = 0;
			String itemNUmberString = "";
			for (int val : values) {
				if (i + 1 <= val) {
					rewardItemString = parseItemString(counts, itemId, j);
					itemNUmberString += counts[j];
					break;
				}
				j++;
			}
			String guid = allPlayersCache.getPlayerIdByName(name);
			sysGiveItem(rewardItemString, guid, MessageCode.MAIL_BEAST_TITILE1, MessageCode.MAIL_BEAST_CONTENT1, hurtVal + "", (i + 1) + "", itemNUmberString, beast.getNum()+"", topOneNameString, WorldActivity.top1ItemName, WorldActivity.top1ItemNum, topFiveNamesString, WorldActivity.top5ItemName, WorldActivity.top5ItemNum, WorldActivity.luckyItemName, WorldActivity.luckyItemNum, luckyPlayerNames,WorldActivity.top50ItemNum);

			i++;
			if (i >= values[values.length - 1]) {
				break;
			}
		}

		long time = System.nanoTime() - startTime;
		logger.info("beastOrderReward cost time {} ms..", time / (1000 * 1000));
	}
	
	public void giveScoreRankReward(Map<Pair<Integer, Integer>, String> order2RewardMap) {
		int max = AllGameConfig.getInstance().activitys.getCatchNinja().getRanknumber();
		List<ObjectNode> topRank = GameRankMaster.getInstance().scoreRank.getRangeRank(1, max);
		logger.info("giveScoreRewardPlayers{}", topRank.toString());
		for (Map.Entry<Pair<Integer, Integer>, String> entry : order2RewardMap.entrySet()) {
			String reward = entry.getValue();
			int from = entry.getKey().getFirst();
			int to = entry.getKey().getSecond();
			for (; from <= to && (from - 1) < topRank.size() && topRank.get(from - 1) != null; ++from) {
				sysGiveItem(reward, topRank.get(from - 1).get("id").asText(), 
						MessageCode.MAIL_TITLE_REWARD, ActivityType.RAFFLE_SCORE.getTid());
			}
		}
	
	}

	public String parseItemString(int[] counts, Integer itemId, int i) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("<").append(itemId).append(";").append(counts[i]).append(">");
		return sBuilder.toString();
	}

	public void rankOrderReward(int[] values, String rewards, int tid) {
		long startTime = System.nanoTime();
		String[] itemStrings = rewards.split(",");
		int max = values[values.length - 1];
		ArenaRank arenaRank = GameRankMaster.getInstance().getArenaRank();
		for (int i = 1; i <= max; i++) {
			String rewardItemString = null;
			int j = 0;
			for (int val : values) {
				if (i <= val) {
					//logger.info("奖励索引"+j);
					rewardItemString = itemStrings[j];
					break;
				}
				j++;
			}
			if (rewardItemString != null) {
				RankModel rankModel = arenaRank.getRankModel(i);
				if (rankModel != null) {
					sysGiveItem(rewardItemString, rankModel.getGuid(), tid);
				}
			}
		}

		long time = System.nanoTime() - startTime;
		logger.info("rankOrderReward cost time {} ms..", time / (1000 * 1000));

	}

	public void sysGiveItem(String rewardItemString, String guid, int contentId) {
		sysGiveItem(rewardItemString, guid, MessageCode.MAIL_TITLE_REWARD, contentId, "");
	}

	public void sysGiveItem(String rewardItemString, String guid, int titleId, int contentId, int isTip, String... contentText) {
		PlayerSession playerSession = getSessions().get(guid);
		if (rewardItemString != null) {
			if (playerSession != null) {
				ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
				ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
				msg.put("item", rewardItemString);
				msg.put("titleId", titleId);
				msg.put("contentId", contentId);
				msg.put("isTip", isTip);
				msg.put("contentText", StringUtils.join(contentText, "|"));
				obj.put("sysGiveItem", msg);
				playerSession.onEvent(Events.event(obj));
			} else {
				DefaultPlayer cache = objectCacheService.getCache(guid, DefaultPlayer.class);
				VariousItemEntry[] items = VariousItemUtil.parse1(rewardItemString);
				cache.getMail().addSysItemMail(items, titleId, contentId, contentText);
				objectCacheService.putObject(cache);
			}
		}
	}

	public void sysGiveItem(String rewardItemString, String guid, int titleId, int contentId, String... contentText) {
		sysGiveItem(rewardItemString, guid, titleId, contentId, 1, contentText);
	}

	public void gmGiveItem(String type, int count, String name) {
		String guid = allPlayersCache.getPlayerIdByName(name);
		if (guid != null) {
			DefaultPlayer cache = objectCacheService.getCache(guid, DefaultPlayer.class);
			VariousItemEntry itemEntry = new VariousItemEntry(type, count);
			VariousItemUtil.doBonus(cache, itemEntry, LogConst.GM_GIVE, true);
			objectCacheService.putObject(cache);
			logger.info("RESULT : GM give item sucess");
		}

	}

	public void shutdown() {
		logger.info("LDRoom is ShutDown");
	}

	private static int getRandomPos(int axisVal) {
		long round = Math.round(32 + (Math.random() * (axisVal - 64)));
		return (int) round;
	}

	/**
	 * This handler is attached to the GameRoom and all game related logic is
	 * embedded in it. Its a SESSION_MESSAGE handler and does not take care of
	 * other events which are sent to the room.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public static class GameSessionHandler extends SessionMessageHandler {
		Entity monster;
		GameRoom room;// not really required. It can be accessed as getSession()
						// also.

		public GameSessionHandler(GameRoomSession session) {
			super(session);
			this.room = session;
			GameStateManagerService manager = room.getStateManager();
			LDGameState state = (LDGameState) manager.getState();
			// Initialize the room state.
			state = new LDGameState();
			state.setEntities(new HashSet<Entity>());
			manager.setState(state); // set it back on the room
			this.monster = state.getMonster();
		}

		@Override
		public void onEvent(Event event) {
			// Entity hero = ((LDGameState) event.getSource()).getHero();
			// Session session = event.getEventContext().getSession();
			// update(hero, session);
		}

	}
}
