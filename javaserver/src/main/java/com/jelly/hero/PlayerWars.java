package com.jelly.hero;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Vip;
import com.dol.cdf.common.bean.War;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.activity.WorldActivity;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.player.AttackerGroup;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.player.IFighter;
import com.jelly.player.VipConstant;

public class PlayerWars extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerWars.class);

	@JsonProperty("cp")
	private int currPass = -1;
	// 是否领奖，0未领取，1表示领取
	@JsonProperty("st")
	private int status = 0;
	@JsonProperty("de")
	private String defenderId;
	@JsonProperty("dh")
	private Map<Integer, Float> defenderHps = Maps.newHashMap();

	@JsonProperty("wd")
	private List<Integer> warDefenders = Lists.newArrayList();

	@JsonProperty("wa")
	private List<Integer> warAttackers = Lists.newArrayList();

	// 今日完成次数
	@JsonProperty("tf")
	private int todayFinish;
	// 完成时间
	@JsonProperty("ft")
	private int finishTime;
	
	// 今日打劫次数
	@JsonProperty("tn")
	private int takeNum;
	// 今日打劫时间
	@JsonProperty("tt")
	private int takeTime;
	//	派出队伍开始抢夺时间(ms)
	@JsonProperty("srt")
	private long startRobTime;

	//最大打劫次数
	public static final int MAX_TAKE_NUM = 10;
	
	@JsonProperty("wr")
	private Map<Integer, Float> warHpRates = Maps.newHashMap();
	
	DefaultPlayer other;
	
	public void initWarList(Player player, WorldActivity worldActivity, AllPlayersCache allPlayersCache) {
		if (defenderId == null) {
			String id = worldActivity.getDefenderGuid(player, allPlayersCache);
			setDefenderId(id);
		}
		if(warDefenders.isEmpty()) {
			warDefenders = Lists.newArrayList(player.getHeros().getMarIdsWithNull());
		}
		if(warAttackers.isEmpty()) {
			warAttackers = Lists.newArrayList(player.getHeros().getMarIdsWithNull());
		}
		for (Integer attId : warAttackers) {
			if(attId == null) continue;
			if (warHpRates.containsKey(attId) == false) {
				warHpRates.put(attId, 1f);
			}
		}
		int needLv = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.RS_ROLE_LEVEL);
		for (Entry<Integer, Hero> entry : player.getHeros().getAllHero().entrySet()) {
			Integer hid = entry.getKey();
			Hero hero = entry.getValue();
			if (hero.getLevel() >= needLv) {
				if (warHpRates.containsKey(hid) == false) {
					warHpRates.put(hid, 1f);
				}
			}
		}
		
	}
	
	
	public void setWarHeroHps(int hid, int hp, float hpRate) {
		warHpRates.put(hid, hpRate);
	}

	public List<Integer> getOtherWarDefenders() {
		if (other.getWars().isEmptyWarDefenders()) {
			return other.getHeros().getMarIds();
		} else {
			return FluentIterable.from(other.getWars().getWarDefenders()).filter(Predicates.notNull()).toList();
		}
	}
	
	public List<Integer> getWarDefenders(){
		return warDefenders;
	}

	public boolean isEmptyWarDefenders() {
		if (warDefenders.isEmpty()) {
			return true;
		}
		for (Integer id : warDefenders) {
			if (id != null) {
				return false;
			}
		}
		return true;
	}

	public List<Integer> getWarAttackers() {
		return warAttackers;
	}


	public void replaceFighterHero(Player player, int srcId, int tarId) {
		Float remove = warHpRates.remove(srcId);
		if (remove != null ) {
			int srcIdx = warAttackers.indexOf(srcId);
			if(srcIdx > -1 && warAttackers.indexOf(tarId) == -1) {
				warAttackers.set(srcIdx, tarId);
			}
		}
		int defSrcIdx = warDefenders.indexOf(srcId);
		if(defSrcIdx > -1) {
			 if( warDefenders.indexOf(tarId) == -1) {
				 warDefenders.set(defSrcIdx, tarId);
			 }else {
				 warDefenders.set(defSrcIdx, null);
			}
		}
	}
	
	public void setWarAttackers(ArrayNode arrayNode) {
		for (int i = 0; i < arrayNode.size(); i++) {
			//最多三个忍者
			if(i >= 3) break;
			int hid = arrayNode.get(i).asInt();
			if (warHpRates.containsKey(hid)) {
				try {
					warAttackers.set(i, hid);
				} catch (Exception e) {
					warAttackers.add(hid);
				}
			} else {
				try {
					warAttackers.set(i, null);
				} catch (Exception e) {
					warAttackers.add(null);
				}
			}

		}
	}

	public void setWarDefenders(ArrayNode arrayNode, Player player) {
		if(arrayNode == null) return;
		if (warDefenders.isEmpty()) {
			warDefenders = Lists.newArrayList(player.getHeros().getMarIdsWithNull());
		}
		for (int i = 0; i < arrayNode.size(); i++) {
			//最多三个忍者
			if(i >= 3) break;
			int hid = arrayNode.get(i).asInt();
			if (player.getHeros().getHero(hid) != null) {
				try {
					warDefenders.set(i, hid);
				} catch (Exception e) {
					warDefenders.add(hid);
				}
				
			}
		}
	}


	@Override
	public String getKey() {
		return "warInfo";
	}

	@Override
	public JsonNode toWholeJson() {
		return null;
	}

	public void sendDefenderJson() {

	}

	public void comeNewDefenderPlayer() {

	}

	public List<MirrorHero> getMirrorHeroAttackers(Player player) {
		List<MirrorHero> list = Lists.newArrayList();
		for (Integer attId : getWarAttackers()) {
			if(attId == null) continue;
			Hero hero = player.getHeros().getHero(attId);
			if(hero == null) continue;
			Float hpRate = warHpRates.get(attId);
			if (hpRate == null) {
				warHpRates.put(attId, 1f);
				hpRate = warHpRates.get(attId);
			}
			if(hpRate <= 0) continue;
			MirrorHero mirrorHero = new MirrorHero(attId,hero,(int) (hpRate*hero.getHpMax()),hero.getHpMax(), 1);
			list.add(mirrorHero);
		}
		if(list.isEmpty()) {
			List<Integer> marsIds = player.getHeros().getMarIds();
			for (Integer attId : marsIds) {
				Float hpRate = warHpRates.get(attId);
				if(hpRate == null) {
					warHpRates.put(attId, 1f);
					hpRate = warHpRates.get(attId);
				}
				Hero hero = player.getHeros().getHero(attId);
				if(hero == null) {
					continue;
				}
				MirrorHero mirrorHero = new MirrorHero(attId,hero,(int) (hpRate*hero.getHpMax()),hero.getHpMax(), 1);
				list.add(mirrorHero);
			}
		}
		return list;
	}

	public List<MirrorHero> getMirrorHeroDefenders( War war) {
		List<MirrorHero> list = Lists.newArrayList();
		for (Integer attId : getOtherWarDefenders()) {
			if(attId == null) continue;
			Hero hero = other.getHeros().getHero(attId);
			if (hero == null) continue;
			Float hpRate = defenderHps.get(attId);
			if(hpRate == null) {
				defenderHps.put(attId, 1f);
				hpRate = defenderHps.get(attId);
			}
			
			if( hpRate <=0 ) continue;
			MirrorHero mirrorHero = new MirrorHero(attId,hero, (int)( hpRate * hero.getHpMax()),hero.getHpMax(), war.getRatio() / 100f);
			list.add(mirrorHero);
		}
		if(list.isEmpty()) {
			List<Integer> otherMarsIds = other.getHeros().getMarIds();
			for (Integer attId : otherMarsIds) {
				Float hpRate = defenderHps.get(attId);
				if(hpRate == null) {
					defenderHps.put(attId, 1f);
					hpRate = defenderHps.get(attId);
				}
				Hero hero = other.getHeros().getHero(attId);
				if(hero == null) continue;
				MirrorHero mirrorHero = new MirrorHero(attId,hero, (int)( hpRate * hero.getHpMax()),hero.getHpMax(), war.getRatio() / 100f);
				list.add(mirrorHero);
			}
		}
		return list;
	}

	public War getWarConfig() {
		int tarPass = currPass + 1;
		if (tarPass >= config.wars.getMaxPassId()) {
			logger.error("超过了最大上限");
			tarPass = currPass;
		}
		War war = config.wars.getWar(tarPass);
		return war;
	}

	public AttackerGroup getWarAttackerGroup(Player player) {
		List<MirrorHero> mirrorHeroAttackers = getMirrorHeroAttackers(player);
		List<IFighter> fighterAList = Lists.newArrayList();
		for (MirrorHero hero : mirrorHeroAttackers) {
			IFighter fighterA = new BaseFighter(hero);
			fighterAList.add(fighterA);
		}
		return new AttackerGroup(fighterAList);
	}

	public DefenderGroup getWarDefenderGroup( War war) {
		List<MirrorHero> mirrorHeroAttackers = getMirrorHeroDefenders(war);
		List<IFighter> fighterAList = Lists.newArrayList();
		for (MirrorHero hero : mirrorHeroAttackers) {
			IFighter fighterA = new BaseFighter(hero);
			fighterAList.add(fighterA);
		}
		return new DefenderGroup(fighterAList);
	}

	public JsonNode warsInfo() {
		List<Integer> warFighters = getOtherWarDefenders();
		ObjectNode obj = jackson.createObjectNode();
		ObjectNode herosObj = jackson.createObjectNode();
		
		for (Integer hid : warFighters) {
			Hero hero = other.getHeros().getHero(hid);
			if (hero != null) {
				if(defenderHps.get(hid) == null) {
					defenderHps.put(hid, 1f);
				}
				herosObj.put(hid + "", hero.toHpJson(defenderHps.get(hid)));
			} else {
				logger.error("wars is null");
			}
		}
		if(herosObj.size() == 0) {
			warFighters = other.getHeros().getMarIds();
			for (Integer hid : warFighters) {
				Hero hero = other.getHeros().getHero(hid);
				if (hero != null) {
					if(defenderHps.get(hid) == null) {
						defenderHps.put(hid, 1f);
					}
					herosObj.put(hid + "", hero.toHpJson(defenderHps.get(hid)));
				} else {
					logger.error("seconds wars is null");
				}
			}
		}
		obj.put("wars", convertToArrayNode(warFighters));
		obj.put("hes", herosObj);
		return obj;
	}


	public ObjectNode toWarInfo(Player player, WorldActivity worldActivity, AllPlayersCache allPlayersCache) {
		long startTime = System.nanoTime();
		checkReset();
		initWarList(player,worldActivity,allPlayersCache);
		other = player.getObjectCache().getCache(defenderId, DefaultPlayer.class);
		ObjectNode objectNode = jackson.createObjectNode();
		objectNode.put("currPass", currPass);
		int totalWarTimes = getTotalWarTimes(player);
		objectNode.put("total", totalWarTimes);
		objectNode.put("num", totalWarTimes - todayFinish);
		objectNode.put("status", status);
		objectNode.put("takeTotal", MAX_TAKE_NUM);
		objectNode.put("takeNum", MAX_TAKE_NUM - takeNum);
		objectNode.put("defendStatus", worldActivity.isInWar(player.getId())?1:0);
		objectNode.put("name", player.getAllPlayersCache().getNameByPlayerId(defenderId));
		objectNode.put("defenders", warsInfo());
		objectNode.put("allAttackers", DynamicJsonProperty.convertToJsonNode(warHpRates));
		objectNode.put("currAttackers", DynamicJsonProperty.convertToJsonNode(warAttackers));
		objectNode.put("myDefenders", DynamicJsonProperty.convertToJsonNode(warDefenders));
		long time = System.nanoTime() - startTime;
		logger.info("toWarInfo cost time {} ms..", time / (1000 * 1000));
		return objectNode;
	}
	
	public void restartWar(Player player,WorldActivity worldActivity, AllPlayersCache allPlayersCache) {
		if (isMaxFinish(player)) {
			logger.error("已经没有次数挑战了");
			return;
		}
		resetPassStatus();
		ObjectNode objectNode = jackson.createObjectNode();
		ObjectNode warInfo = toWarInfo(player, worldActivity, allPlayersCache);
		objectNode.put(player.getWars().getKey(), warInfo);
		player.sendMessage(objectNode);
	}

	public void resetPassStatus() {
		setCurrPass(0);
		setStatus(0);
		setDefenderId(null);
		defenderHps.clear();
		warHpRates.clear();
	}
	
	public void earnReward(Player player) {
		if (currPass == 0) {
			return;
		}
		if (status == 0) {
			War war = config.wars.getWar(currPass);
			if (war != null) {
				Integer coin = war.getCoin();
				player.getProperty().changeMoney("coin", coin, MessageCode.WAR_REWARD, player);
				LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.WAR_REWARD, coin, player.getProperty().getCoin(), "coin");
			}
			setStatus(1);
		}
	}
	
	/**
	 * 召回派出打劫的队伍
	 **/
	public void chkAndRecallTimeoutAttackers(Player player, WorldActivity worldActivity) {
		if (getStartRobTime() == 0) {
			worldActivity.removeDefendWarPlayer(player.getId());
			player.getMail().addSysMail(MessageCode.MAIL_WAR_TITLE, MessageCode.RENJIE_ROB_TIMEOUT);
		}
		
		//	检查距离上一次派出小队参加抢劫活动到现在是否已经超过1小时, 如果超过了强制将派出的队伍召回
		long now = System.currentTimeMillis(),
				lastRobTime = getStartRobTime();
		if (lastRobTime > 0) {
			long elapseTime = (long)(((now - lastRobTime) / 1000));
			logger.info("from last send defend attackers to now elapse {}(s)", elapseTime);
			if (elapseTime >= 3600) {
				setStartRobTime(0);
				worldActivity.removeDefendWarPlayer(player.getId());
				player.getMail().addSysMail(MessageCode.MAIL_WAR_TITLE, MessageCode.RENJIE_ROB_TIMEOUT);
			}
		}
	}

	public void addCurrPass() {
		this.currPass++;
		setStatus(0);
		addChange("currPass", currPass);
		defenderHps.clear();
		setDefenderId(null);
	}

	public int getCurrPass() {
		return currPass;
	}

	public void setCurrPass(int currPass) {
		this.currPass = currPass;
		addChange("currPass", currPass);
	}

	public String getDefenderId() {
		return defenderId;
	}

	public void setDefenderId(String defenderId) {
		this.defenderId = defenderId;
	}

	public int getStatus() {
		return status;
	}

	public boolean canFight(int id) {
		if (currPass == id - 1) {
			return true;
		}
		return false;
	}

	public void setStatus(int status) {
		this.status = status;
		addChange("status", status);
	}
	
	public void setStartRobTime(long startRobTime) {
		this.startRobTime = startRobTime;
	}
	
	public long getStartRobTime() {
		return this.startRobTime;
	}
	
	/**
	 * 检测是否需要重置时间
	 */
	private void checkReset() {
		if (todayFinish != 0 && !TimeUtil.isSameDay(finishTime)) {
			todayFinish = 0;
		}
		if (takeNum != 0 && !TimeUtil.isSameDay(takeTime)) {
			takeNum = 0;
		}
		if (currPass == -1) {
			todayFinish = 1;
			finishTime = TimeUtil.getCurrentTime();
			currPass = 0;
		}
	}

	public void addTodayFinish(Player player) {
		todayFinish++;
		finishTime = TimeUtil.getCurrentTime();
		addChange("num", getTotalWarTimes(player) - todayFinish);
	}
	
	public void addTakeNum() {
		takeNum++;
		takeTime = TimeUtil.getCurrentTime();
		addChange("takeNum", MAX_TAKE_NUM - takeNum);
	}
	
	public int getTotalWarTimes(Player player) {
		Vip vip = AllGameConfig.getInstance().activitys.getVip(player.getProperty().getVipLv());
		return vip.getTimes()[VipConstant.WAR_TIMES];
	}
	
	private boolean isMaxFinish(Player player) {
		checkReset();
		boolean isMax = todayFinish >= getTotalWarTimes(player);
		if (!isMax) {
			addTodayFinish(player);
		}
		return isMax;
	}
	
	public boolean isMaxTakeNum(Player player) {
		checkReset();
		boolean isMax = takeNum >= MAX_TAKE_NUM;
		if (!isMax) {
			addTakeNum();
		}
		return isMax;
	}

	public Map<Integer, Float> getDefenderHps() {
		return defenderHps;
	}

	public void setDefenderHps(int hid, Float hpRate) {
		this.defenderHps.put(hid, hpRate);
	}
	
	

}
