package io.nadron.app.impl;

import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.event.Events;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Recharge;
import com.dol.cdf.common.config.ActivityConfigManager;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.crypto.Guid;
import com.dol.cdf.common.entities.Entity;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.activity.GiftType;
import com.jelly.activity.PlayerActivity;
import com.jelly.activity.WorldActivity;
import com.jelly.game.command.ChatConstants;
import com.jelly.hero.PlayerHeros;
import com.jelly.hero.PlayerPractise;
import com.jelly.hero.PlayerRecruit;
import com.jelly.hero.PlayerWars;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.player.PlayerAdventure;
import com.jelly.player.PlayerArena;
import com.jelly.player.PlayerBuilding;
import com.jelly.player.PlayerExam;
import com.jelly.player.PlayerFriends;
import com.jelly.player.PlayerInventory;
import com.jelly.player.PlayerMail;
import com.jelly.player.PlayerOperResult;
import com.jelly.player.PlayerProperty;
import com.jelly.player.PlayerShop;
import com.jelly.player.PlayerTeam;
import com.jelly.player.shop.NewPlayerShop;
import com.jelly.quest.PlayerTask;
import com.jelly.quest.TaskType;

public class DefaultPlayer extends Entity implements Player {

	private static final Logger logger = LoggerFactory.getLogger(DefaultPlayer.class);
	
	/**
	 * One player can be connected to multiple games at the same time. Each
	 * session in this set defines a connection to a game. TODO, each player
	 * should not have multiple sessions to the same game.
	 */
	private PlayerSession playerSession;

	public static final int DEFAULT_ROLE_ID = 17;

	@JsonProperty("pr")
	private PlayerProperty property;

	@JsonProperty("he")
	private PlayerHeros heros;

	@JsonProperty("av")
	private PlayerAdventure adventure;

	@JsonProperty("ma")
	private PlayerMail mail;

	@JsonProperty("in")
	private PlayerInventory invenotry;

	@JsonProperty("fr")
	private PlayerFriends friends;

	@JsonProperty("ta")
	private PlayerTask task;

	@JsonProperty("bu")
	private PlayerBuilding building;

	@JsonProperty("ar")
	private PlayerArena arena;  // 竞技PVP排行榜

	@JsonProperty("ex")
	private PlayerExam exam;

	@JsonProperty("pa")
	private PlayerPractise practise;

	@JsonProperty("re")
	private PlayerRecruit recruit;
	
	@JsonProperty("ac")
	private PlayerActivity activity;
	
	
	@JsonProperty("pw")
	private PlayerWars wars;
	
	@JsonProperty("ps")
	private PlayerShop shop;
	
	@JsonProperty("pt")
	private PlayerTeam team;
	
	private String clientInfo; //客户端信息，不存数据库

	private final PlayerOperResult oper = new PlayerOperResult();

	private RoleEntity role;

	private AllPlayersCache allPlayersCache;
	
	private ObjectCacheService objectCache;
	
	@JsonProperty("nsp")
	private NewPlayerShop newShop;
	
	@JsonProperty("csign")
	private boolean csign = true;

	public DefaultPlayer(String guid, String userId,String ch,String net) {
		super(guid);
		property = new PlayerProperty();
		if (!ContextConfig.checkSeverId(net)) {
			logger.error("how you come here,serverId:{},userNet:{}",ContextConfig.SERVER_ID,net);
		}
		property.setUserId(userId);
		property.setCh(ch);
		property.setNet(net);
		if(!ContextConfig.isOfficalEnv()) {
			property.setGold(9999999);
			property.setVipLv(3);
		}else if(AllGameConfig.getInstance().env == RuntimeEnv.TEST) {
			property.setVipLv(1);
		}
		heros = new PlayerHeros(DEFAULT_ROLE_ID);
		adventure = new PlayerAdventure();
		mail = new PlayerMail();
		invenotry = new PlayerInventory();
		invenotry.init();
		friends = new PlayerFriends();
		task = new PlayerTask();
		building = new PlayerBuilding();
		arena = new PlayerArena();
		exam = new PlayerExam();
		practise = new PlayerPractise();
		recruit = new PlayerRecruit();
		activity = new PlayerActivity();
		wars = new PlayerWars();
		shop = new PlayerShop();
		team = new PlayerTeam();
		newShop = new NewPlayerShop();
	}

	@Override
	public String getName() {
		if(role == null) {
			return "";
		}
		String name = role.getName();
		if (name == null) {
			logger.error("name is null");
		}
		return name;
	}

	@Override
	public RoleEntity getRole() {
		return role;
	}

	@Override
	public void setRole(RoleEntity role) {
		this.role = role;
		property.setRole(role);
	}

	@Override
	public PlayerTask getTask() {
		return task;
	}

	@Override
	public void setTask(PlayerTask task) {
		this.task = task;
	}

	@Override
	public PlayerProperty getProperty() {
		return property;
	}

	@Override
	public PlayerActivity getActivity() {
		if (activity == null) {
			activity = new PlayerActivity();
		}
		return activity;
	}

	
	@Override
	public PlayerWars getWars() {
		if (wars == null) {
			wars = new PlayerWars();
		}
		return wars;
	}
	
	
	@Override
	public PlayerArena getArena() {
		return arena;
	}

	@Override
	public void setArena(PlayerArena arena) {
		this.arena = arena;
	}

	@Override
	public void setProperty(PlayerProperty property) {
		this.property = property;
	}
	
	@Override
	public void setShop(PlayerShop shop) {
		this.shop = shop;
	}
	
	@Override
	public PlayerShop getShop() {
		if (this.shop == null) {
			this.shop = new PlayerShop();
		}
		return this.shop;
	}
	
	@Override
	public NewPlayerShop getNewShop(){
		if(this.newShop == null){
			this.newShop = new NewPlayerShop();
		}
		return this.newShop;
	}
	
	@Override
	public PlayerTeam getTeam() {
		if (this.team == null) {
			this.team = new PlayerTeam();
		}
		return this.team;
	}
	
	@Override
	public void setTeam(PlayerTeam team) {
		this.team = team;
	}

	@Override
	public PlayerHeros getHeros() {
		return heros;
	}

	@Override
	public void setHeros(PlayerHeros heros) {
		this.heros = heros;
	}

	@Override
	public PlayerAdventure getAdventure() {
		return adventure;
	}

	@Override
	public void setAdventure(PlayerAdventure adventure) {
		this.adventure = adventure;
	}

	@Override
	public PlayerMail getMail() {
		return mail;
	}

	@Override
	public void setMail(PlayerMail mail) {
		this.mail = mail;
	}

	@Override
	public PlayerInventory getInvenotry() {
		return invenotry;
	}

	@Override
	public void setInvenotry(PlayerInventory invenotry) {
		this.invenotry = invenotry;
	}

	@Override
	public PlayerFriends getFriends() {
		return friends;
	}

	@Override
	public void setFriends(PlayerFriends friends) {
		this.friends = friends;
	}

	public DefaultPlayer() {
	}

	@Override
	public PlayerBuilding getBuilding() {
		return building;
	}

	@Override
	public void setBuilding(PlayerBuilding building) {
		this.building = building;
	}

	@Override
	public PlayerExam getExam() {
		return exam;
	}

	public void setExam(PlayerExam exam) {
		this.exam = exam;
	}

	@Override
	public PlayerPractise getPractise() {
		return practise;
	}

	public void setPractise(PlayerPractise practise) {
		this.practise = practise;
	}

	@Override
	public PlayerRecruit getRecruit() {
		return recruit;
	}

	public void setRecruit(PlayerRecruit recruit) {
		this.recruit = recruit;
	}

	@Override
	public String getClientInfo() {
		return clientInfo;
	}

	@Override
	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	public static String genPlayerGuid(String id,String ch, String net) {
		Guid createHash = null;
		if(AllGameConfig.getInstance().env == RuntimeEnv.OVERSEAS) {
			createHash = Guid.createHash(id + net + "PlayercSdfJR8ubXwewsdfeaX");
		}else {
			createHash = Guid.createHash(id + ch + net + "PlayercSdfJR8ubXwewsdfeaX");
		}
		return createHash.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultPlayer other = (DefaultPlayer) obj;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		return true;
	}

	@Override
	public String getId() {
		return guid;
	}

	@Override
	public void setId(Object id) {
		this.guid = id.toString();
	}

	@Override
	public synchronized boolean addSession(PlayerSession session) {
		this.playerSession = session;
		return true;
	}

	@Override
	public synchronized boolean removeSession(PlayerSession session) {
		if (playerSession != null) {
			playerSession = null;
			return true;
		}
		return false;
	}

	@Override
	public synchronized void logout(PlayerSession session) {
		session.close();
		playerSession = null;
	}

	@Override
	public PlayerSession getPlayerSession() {
		return playerSession;
	}

	public void setPlayerSession(PlayerSession playerSession) {
		this.playerSession = playerSession;
	}

	List<DynamicJsonProperty> djpList = Lists.newArrayList();

	/**
	 * 注册可能更改的属性列表
	 */
	public void registerMayChangedProperty() {
		djpList.add(property);
		djpList.add(heros);
		djpList.add(adventure);
		djpList.add(friends);
		djpList.add(building);
		djpList.add(mail);
		djpList.add(invenotry);
		djpList.add(task);
		if(arena == null){
			arena = new PlayerArena();
			djpList.add(arena);
		}
		djpList.add(exam);
		djpList.add(practise);
		djpList.add(recruit);
		djpList.add(wars);
		djpList.add(shop);
		djpList.add(team);
//		djpList.add(newShop);
		
		// operResult必须放最后
		djpList.add(oper);
	}

	public void initPlayer(AllPlayersCache allPlayersCache) {
		this.allPlayersCache = allPlayersCache;
		role = allPlayersCache.getPlayerInfo(this.getId());
		property.setAllPlayersCache(this.allPlayersCache);
		property.setRole(role);
		registerMayChangedProperty();
		if(ContextConfig.isJointServer()){
			if(role != null && role.getTeamName() != null && !role.getTeamName().equals(this.getTeam().getName())){
				this.getTeam().setName(role.getTeamName());
//				this.getTeam().setApplyTeams(new ArrayList<String>());
//				this.getTeam().setLeaveTeam("");
			}
		}
	}

	@Override
	public AllPlayersCache getAllPlayersCache() {
		return allPlayersCache;
	}

	public void setAllPlayersCache(AllPlayersCache allPlayersCache) {
		this.allPlayersCache = allPlayersCache;
	}

	public void clearChangedJson() {
		for (DynamicJsonProperty djp : djpList) {
			if(djp != null) {
				djp.clearChanged();
			}
		}
	}

	public ObjectNode getPlayerChanged() {

		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		for (DynamicJsonProperty djp : djpList) {
			if (djp != null && djp.hasChanged()) {
				obj.put(djp.getKey(), DynamicJsonProperty.jackson.createObjectNode().setAll(djp.getJsonValue()));
			}
		}
		return obj;
	}

	@Override
	public void sendPlayerWholeJSON() {
		heros.sendShardHeros(this);
		for (DynamicJsonProperty djp : djpList) {
			if(djp == null) continue;
			JsonNode jsonValue;
			if (djp instanceof PlayerProperty) {
				ObjectNode pp = (ObjectNode) djp.toWholeJson();
				pp.put("id", guid);// .put("name", role.getName());
				if (role != null) {
					pp.put("name", role.getName());
				}
				
				pp.put("power", heros.getPlayerPower());
				jsonValue = pp;
			} else if (djp instanceof PlayerBuilding) {
				jsonValue = building.toAllJson(this);
			}else {
				jsonValue = djp.toWholeJson();
			}
			if (jsonValue != null) {
				sendMessage(djp.getKey(), jsonValue);
			}
		}
		invenotry.sendItemList(this);
		//发送登陆成功
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("code", 0);
		sendMessage("enterGame", obj);
		
	}

	public boolean hasPlayerChanged() {
		for (DynamicJsonProperty djp : djpList) {
			if (djp != null && djp.hasChanged()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void sendChangedMessage() {
		sendMessage(getPlayerChanged());
		clearChangedJson();
	}

	@Override
	public void sendMessage(ObjectNode object) {
		playerSession.getTcpSender().sendMessage(Events.event(object));
	}

	@Override
	public void sendMessage(String key, JsonNode node) {
		playerSession.getTcpSender().sendMessage(Events.event(key, node));
	}

	@Override
	public void sendMiddleMessage(int id, String... strs) {
		sendChatMessage(id, ChatConstants.SCOPE_MIDDLE, strs);
	}

	public void sendChatMessage(int id, int scope, String... strs) {
		sendChatMessageWithTip(id, scope, -1, strs);
	}

	public void sendChatMessageWithTip(int id, int scope, int type, String... strs) {
		ObjectNode cmd = DynamicJsonProperty.jackson.createObjectNode();
		cmd.put("tid", id);
		if (strs != null && strs.length > 0) {
			cmd.put("param", DynamicJsonProperty.convertToArrayNode(strs));
		}
		cmd.put("scope", scope);
		if (type > 0) {
			cmd.put("type", type);
		}
		if(playerSession != null && playerSession.getTcpSender() != null) {
			playerSession.getTcpSender().sendMessage(Events.event("chat", cmd));
		}
	}
	
	@Override
	public void broadcastChatMessage(int id, String... strs) {
		ObjectNode cmd = DynamicJsonProperty.jackson.createObjectNode();
		cmd.put("tid", id);
		if (strs != null && strs.length > 0) {
			cmd.put("param", DynamicJsonProperty.convertToArrayNode(strs));
		}
		cmd.put("scope", ChatConstants.SCOPE_MIDDLE);
		ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
		node.put("chat", cmd);
		playerSession.getGameRoom().sendBroadcast(Events.networkEvent(node));
	}

	/**
	 * 发送给客户端的操作结果
	 * 
	 * @param type
	 *            {@link OperResultType}
	 * @param res
	 *            {@link MessageCode}
	 * @param param
	 *            任意值，例如返回的文字描述或者具体的数值
	 */
	@Override
	public void sendResult(OperResultType type, int res, Object param) {
		oper.setType(type.getId());
		oper.setRes(res);
		if (param != null) {
			oper.setParam(param);
		}else {
			oper.setParam(null);
		}
	}

	@Override
	public void sendResult(OperResultType type, int res) {
		sendResult(type, res, null);
	}

	@Override
	public void sendResult(OperResultType type, Object param) {
		sendResult(type, MessageCode.OK, param);
	}

	@Override
	public void sendResult(OperResultType type) {
		sendResult(type, MessageCode.OK);
	}
	
	@Override
	public void logoff()
	{
		if (role != null) {
			allPlayersCache.updateRolePropAndSave(this, role);
			property.setOffline();
		}
		logger.info("player logoff guid={},userId={},name={}",this.getId(),property.getUserId(),role != null?role.getName():"");
	}

	@Override
	public ObjectCacheService getObjectCache() {
		return objectCache;
	}

	public void setObjectCache(ObjectCacheService objectCache) {
		this.objectCache = objectCache;
	}
	
	
	public void addKYPayment(String orderId,  WorldActivity worldActivity, int exchangeGold) {
		if (exchangeGold <= 0) {
			return;
		}
		
		String reward = "<gold;" + exchangeGold + ">";
		mail.addSysItemMail(reward, "特殊充值兑换金币", "特殊充值兑换金币");
		property.addVipScore(this, exchangeGold);
		sendResult(OperResultType.PAY_VALID, MessageCode.OK, null);
		
	}
	
	/**
	 * 将由appStore服务器发送过来的充值订单道具ID转换为服务端内部使用的道具ID
     * com.xxrz.hd.hy.gold1 -->hy.gold1
	 **/
	public String convertAppStoreItemIDToItemID(String itemId) {
		if (AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
//			//	解决IOS月卡内购BUG,购买hy.gold2时强制改为购买月卡
			itemId = itemId.trim();
			if (itemId.equals("com.xxrz.hd2.hy.gold2")) {
				itemId = "com.xxrz.hd2.hy.gold10";
			}
		} else {
			String[] array = itemId.split("\\.");
	        if (array.length == 5) {
	            itemId = array[3] + array[4];
	        }
		}
//		logger.info("rechargeItemId="+itemId);
        return itemId;
	}
    
	public void addPayment(String itemId, String orderId, WorldActivity worldActivity) {
		itemId = convertAppStoreItemIDToItemID(itemId);
		ActivityConfigManager config = AllGameConfig.getInstance().activitys;
		Recharge recharge = config.getRecharge(itemId);
		if (recharge == null) {
			logger.error("充值异常 itemId=" + itemId + " orderId=" + orderId);
			return;
		}
		int gold = recharge.getGold();
		boolean firstPay = false;
		if (property.getPayStatus(itemId) != 1) {
			if (recharge.getDays() != null && recharge.getDays().intValue() > 0) {
				//月卡 不记首充
			} else {
				firstPay = true;
				property.setPaid(itemId);
			}
			gold += recharge.getFistGive();
		} else {
			gold += recharge.getGive();
		}
		if (gold > 0) {
			property.addGold(gold);
		}
		int payGold =  recharge.getRmb()*10; 
		property.addVipScore(this, payGold);
		sendResult(OperResultType.PAY_VALID, MessageCode.OK, null);
		
		if (recharge.getDays() != null && recharge.getDays().intValue() > 0) {//月卡
			if (property.getMonthlyPayDays() > 0) { //续月卡
				property.addMonthlyPayment(recharge.getDays());
				gold = 0;
				mail.addSysMail(MessageCode.MONTHLY_TIP_TITLE, MessageCode.MONTHLY_TIP, String.valueOf(property.getMonthlyPayDays()));
			} else { //第一次购买或过期
				gold = recharge.getDayGive();
				property.addGold(gold);
				property.addMonthlyPayment(recharge.getDays() - 1);
				mail.addSysItemMail("gold", recharge.getDayGive(), MessageCode.MONTHLY_PAY_TID, MessageCode.MONTHLY_PAY_REWARD, String.valueOf(recharge.getDayGive()), String.valueOf(recharge.getDays()-1));
				property.updateMPTime();
			}
			getTask().dispatchEvent(this, TaskType.BUY_CARD);
		} 
		
		worldActivity.addPayValue(guid, recharge.getRmb()*10);
		
		
		//活动数值设置
		GiftType.RECHARGE_1DAY.addValue(this, payGold);
		GiftType.RECHARGE_MAX.addValue(this, payGold);
		GiftType.RECHARGE_DAY_BY_DAY.addValue(this, payGold);
		GiftType.RECHARGE_FIRST.addValue(this, payGold);
		GiftType.RECHARGE_N_DAYS.addValue(this, payGold);
		
		
		//记录日志
		LogUtil.doPayLog(this, recharge, property.getCh(), firstPay, orderId, gold);
	}
	
	/**
	 * 封账号
	 */
	public void banPlayer() {
		getProperty().banProperty();
		getHeros().banHero();
	}
	public void unBanPlayer() {
		getProperty().unBanProperty();
	}
	
	
	/**
	 * 玩家登陆清理数据的标志
	 */
	public void setClearDataSign(boolean s){
		this.csign = s;
	}
	public boolean getClearDataSign(){
		return this.csign;
	}
}
