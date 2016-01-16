package com.jelly.game.command;

import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;
import io.nadron.context.AppContext;
import io.nadron.event.Events;
import io.nadron.example.lostdecade.LDRoom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.RefuseWordsFilter;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Beast;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.bean.War;
import com.dol.cdf.common.config.ActivityConfigManager.ActivityWrapper;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.common.config.PayUtil;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.common.entities.GlobalProps;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.activity.ActivityType;
import com.jelly.activity.GiftType;
import com.jelly.activity.WorldActivity;
import com.jelly.combat.PVBeastCombatManager;
import com.jelly.combat.WarsCombatManager;
import com.jelly.game.command.SubJsonCommandHandler.JsonCommandHandler;
import com.jelly.hero.Hero;
import com.jelly.hero.HeroState;
import com.jelly.hero.IHero;
import com.jelly.hero.Monster;
import com.jelly.hero.PlayerPractise;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.player.AttackerGroup;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.player.EmojiFilter;
import com.jelly.player.IFighter;
import com.jelly.player.PlayerMail;
import com.jelly.quest.TaskType;
import com.jelly.rank.GameRankMaster;
import com.jelly.rank.RankModel;
import com.jelly.rank.RankType;
import com.jelly.team.TeamConstants;
import com.jelly.team.TeamManager;

public class SocialCommandHandler extends SubJsonCommandHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SocialCommandHandler.class);
	private static final Logger payLogger = LoggerFactory.getLogger("payment");

	private final PlayerSession playerSession;

	private final DefaultPlayer player;

	// TODO 是否key改为名称
	private final Map<String, PlayerSession> gameRoomSessions;

	private final ObjectCacheService objectCache;

	private final AllPlayersCache allPlayersCache;
	
	private final WorldActivity worldActivity;
	
	private LDRoom ldRoom;

	public SocialCommandHandler(Session session, Map<String, PlayerSession> gameRoomSessions, ObjectCacheService objectCache, AllPlayersCache allPlayersCache,WorldActivity worldActivity) {
		this.playerSession = (PlayerSession) session;
		this.gameRoomSessions = gameRoomSessions;
		this.objectCache = objectCache;
		this.allPlayersCache = allPlayersCache;
		this.worldActivity = worldActivity;
		player = (DefaultPlayer) this.playerSession.getPlayer();
		
		//新手引导相关
		addHandler(setPlayerName);
		addHandler(setPlayerLevel);
		addHandler(setAdvStagePass);
		
		addHandler(chatHandler);
		addHandler(challege);
		addHandler(revenge);
		// addHandler(arenaReward);
		addHandler(washingGuys);
		addHandler(friendInfo);
		addHandler(operFriend);
		addHandler(initFriends);
		addHandler(verifyReceipt);
		addHandler(readMail);
		addHandler(rewardMail);
		addHandler(sendMail);
		addHandler(removeMail);
		addHandler(revMail);
		addHandler(refreshTeam);
		addHandler(recFight);
		addHandler(requestRank);
		addHandler(requestRankRecord);
		addHandler(showPlayer);
		addHandler(openBuild);
		addHandler(beastFight);
		addHandler(requestBeastOrder);
		addHandler(requestActivities);
		
		//	忍界大战
		addHandler(requestWarInfo);
		addHandler(setWarDefenders);	//	忍者大战的防守小队
		addHandler(doWarDefend);		//	派出防守小队
		addHandler(setWarAttackers);	//	忍者大战的进攻小队
		addHandler(doWarFight);			//	派出进攻小队
		addHandler(restartWar);
		addHandler(getWarReward);
		
		addHandler(payAddGold);
		addHandler(gmReward);
		addHandler(gmRewardByMail);
		addHandler(teamNotify);
		addHandler(gmAddVipScore);
		addHandler(gmSetLevel);
		addHandler(enterBackground);
		
		addHandler(getTopList);
		addHandler(enterScene);
		
		//	军团
		addHandler(applyTeams);
		addHandler(getTeamInfo);
		addHandler(searchTeams);
		addHandler(createTeam);
		addHandler(dismissTeam);		
		addHandler(applyJoinTeam);
		addHandler(agreeJoinTeam);
		addHandler(refuseJoinTeam);
		addHandler(undoJoinTeam);		
		addHandler(modifyTeamProfile);
		addHandler(applyDelateTeamBoss);
		addHandler(agreeDelateTeamBoss);
		addHandler(modifyTeamMemberTitle);
		addHandler(kickTeamMember);		
		addHandler(upgradeTeam);// 升级军团
		addHandler(donateTeamWealth);	
		// 军团科技
		addHandler(getTeamTechs);
		addHandler(studyTeamTech);
		addHandler(buyTeamTech);
		
		// 军团部队
		addHandler(getTeamArmy);
		addHandler(sendRolesToTeamArmy);
		addHandler(cancelRolesFromTeamArmy);
		
		//军团据点
		addHandler(getStrongHoldInfo); 
		addHandler(sendRolesToStrongHold);
		addHandler(cancelRolesFromStrongHold);
		
		//军团战
		addHandler(getTeamWarInfo); //
		addHandler(getWarHeroIndexList);
		addHandler(updateWarBaseHeroState);
		addHandler(startTeamWarFight);
		addHandler(attackDefendFight);
		
		addHandler(teamWarStatus);
		addHandler(teamWarst);
		// 渠道号
		addHandler(getQuDaoPayInfo);
		
		addHandler(getTeamLogInfo);
	}
	
	JsonCommandHandler enterScene = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "enterScene";
		}

		@Override
		public void run(JsonNode object) {
			player.getProperty().checkRewardEnergy(player);
			player.getTask().checkRefreshQuest(player);
			Set<GiftType> agt = worldActivity.getActiveGiftTypes();
			for(GiftType giftType : agt){
				giftType.reset(player);
			}
		}
	};
	
	/**
	 * 打开建筑
	 */
	JsonCommandHandler openBuild = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "openBuild";
		}

		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt(-1);
			BuildingType buildType = BuildingType.getBuildType(id);
			buildType.open(player);
			if (buildType == BuildingType.WSZN) {
				float val = Float.parseFloat(ActivityType.BEAST_ORDER.getValue());
				ArrayNode all = grandOnlinePlayer(WorldActivity.MAX_BEAST_ROOM_PLAYERS);
				ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
				msg.put("roleList", all);
				msg.put("list", worldActivity.getBeastActTopFiveList());
				msg.put("myPos", worldActivity.getPostionNode(player.getName()));
				if(val > 0) {
					msg.put("beastId", val);
				}
				msg.put("hpRate", PVBeastCombatManager.getBasetCurrentHpRate());
				player.sendMessage("beastOrder",msg);
				msg = DynamicJsonProperty.jackson.createObjectNode();
				msg.put("beastOpen", ActivityType.BEAST_ORDER.isActive() ? 1 : 0 );
				player.sendMessage(player.getProperty().getKey(),msg);
			}
		}
	};
	/**
	 * 打开建筑
	 */
	JsonCommandHandler requestBeastOrder = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "requestBeastOrder";
		}
		
		@Override
		public void run(JsonNode object) {
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("list", worldActivity.getBeastActTopFiveList());
			msg.put("myPos", worldActivity.getPostionNode(player.getName()));
			msg.put("hpRate", PVBeastCombatManager.getBasetCurrentHpRate());
			player.sendMessage("beastOrder",msg);
		}
		
		
	};
	
	
	JsonCommandHandler requestWarInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "requestWarInfo";
		}
		
		@Override
		public void run(JsonNode object) {
			ObjectNode objectNode = DynamicJsonProperty.jackson.createObjectNode();
			ObjectNode jsonNode = player.getWars().toWarInfo(player,worldActivity,allPlayersCache);
			objectNode.put(player.getWars().getKey(), jsonNode);
			player.sendMessage(objectNode);
		}
	};
	
	JsonCommandHandler setWarDefenders = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "setWarDefenders";
		}
		
		@Override
		public void run(JsonNode object) {
			ArrayNode hids = (ArrayNode)object.get("hids");
			player.getWars().setWarDefenders(hids,player);
		}
	};
	
	JsonCommandHandler doWarDefend = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "doWarDefend";
		}
		
		@Override
		public void run(JsonNode object) {
			ArrayNode hids = (ArrayNode)object.get("hids");
			player.getWars().setWarDefenders(hids,player);
			if(worldActivity.isInWar(player.getId())) {
				return;
			}
			if(player.getWars().isMaxTakeNum(player)) {
				return;
			}
			worldActivity.addDefendWarPlayer(player.getProperty().getLevel(), player.getId());
			player.getWars().addChange("defendStatus", 1);
		}
	};
	
	JsonCommandHandler doWarFight = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "doWarFight";
		}
		
		@Override
		public void run(JsonNode object) {
			War warConfig = player.getWars().getWarConfig();
			AttackerGroup warAttackerGroup = player.getWars().getWarAttackerGroup(player);
			if(warAttackerGroup.isEmptyGroup()) {
				LOG.error("doWarFight empty attackerGroup,player name:{}",player.getName());
				return ;
			}
			DefenderGroup warDefenderGroup = player.getWars().getWarDefenderGroup(warConfig);
			if (warDefenderGroup .isEmptyGroup()) {
				LOG.error("doWarFight empty defenderGroup,player name:{}",player.getName());
				return;
			}			
			WarsCombatManager warsCombatManager = new WarsCombatManager(warAttackerGroup, warDefenderGroup, player, warConfig.getCoin(),worldActivity);
			warsCombatManager.start();
			
			player.getTask().dispatchEvent(player, TaskType.RENZHE_DAZHAN);
			
			if(warsCombatManager.getAttackerGroup().isWin()){
				LogUtil.doAcquireLog((DefaultPlayer)player, LogConst.RENJIE_LOG_1,WarsCombatManager.WIN_COIN_INT, player.getProperty().getCoin(), "coin");
			}
		}
	};
	
	
	JsonCommandHandler restartWar = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "restartWar";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getWars().restartWar(player,worldActivity,allPlayersCache);
		}
	};
	
	
	JsonCommandHandler getWarReward = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getWarReward";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getWars().earnReward(player);
		}
	};
	
	JsonCommandHandler setWarAttackers = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "setWarAttackers";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getWars().setWarAttackers((ArrayNode)object.get("hids"));
		}
	};
	

	JsonCommandHandler setPlayerName = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "setPlayerName";
		}

		@Override
		public void run(JsonNode object) {
			String displayName = object.get("name").asText();
			String channel = object.get("channel").asText();
			RoleEntity role = player.getRole();
			if (role == null) {
				int minlengh = 2;
				int maxLengh = 12;
				int nameLength = StringHelper.asciiLength(displayName);
				if(nameLength < minlengh || nameLength > maxLengh){
					player.sendResult(OperResultType.CREATE_NAME, MessageCode.INVALID_NAME_LENGTH);
					return;
				}
				// 判断用户名是否包含非法字符
				if (!StringHelper.containsNone(displayName, " ~`!^?<>/=\"\'%,.(){}[]+*~&|;#$:-　") 
						|| RefuseWordsFilter.getInstance().contain(displayName)) {
					player.sendResult(OperResultType.CREATE_NAME, MessageCode.INVALID_NAME);
					return;
				}
				
				displayName = EmojiFilter.filterEmoji(displayName);
				nameLength = StringHelper.asciiLength(displayName);
				if(nameLength < minlengh || nameLength > maxLengh){
					player.sendResult(OperResultType.CREATE_NAME, MessageCode.INVALID_NAME);
					return;
				}
				if (allPlayersCache.hasThisName(displayName)) {
					player.sendResult(OperResultType.CREATE_NAME, MessageCode.DUPLICATE_NAME);
					return;
				}
				allPlayersCache.putPlayerInfo(player, displayName, channel);
				player.getProperty().addChange("name", displayName);
				player.sendResult(OperResultType.CREATE_NAME);
				
				//	idfa与dev是用于标识与渠道合作的广告商导量数据, idfa,dev如果为null表示忽略
				String idfa = null, //	AppStore idfa
						dev = null;	//	客户端设备号
				if (object.has("idfa")) {
					idfa = object.get("idfa").asText();
				}
				if (object.has("dev")) {
					dev = object.get("dev").asText();
				}
				LogUtil.doCreateRoleLog(player, idfa, dev);
			} else {
				player.sendResult(OperResultType.CREATE_NAME, MessageCode.FAIL);
			}
		}
	};

	JsonCommandHandler setPlayerLevel = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "setPlayerLevel";
		}

		@Override
		public void run(JsonNode object) {
			int tarLv = object.asInt();
			int level = player.getProperty().getLevel();
			if (level < tarLv && tarLv <= 3) {
				player.getProperty().setLevel(tarLv);
				player.getBuilding().checkOpenBuilding(tarLv,player);
			}
		}
	};
	
	JsonCommandHandler setAdvStagePass = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "setAdvStagePass";
		}

		@Override
		public void run(JsonNode object) {
			int leftTime = 3 - player.getProperty().getFubenUse(1, 1, object.asInt());
			if (1 > leftTime) {
				LOG.error("副本次数已用完, cid={},sid={},{}>{}",1,object.asInt(),1,leftTime);
				return;
			}
			player.getAdventure().passStage(player, 1, 1, object.asInt());
			player.getProperty().addFubenUse(1, 1, object.asInt(), 1);
		}
	};

	JsonCommandHandler chatHandler = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "chat";
		}

		@Override
		public void run(JsonNode object) {
			//	方便测试,暂时关闭
			int curTime = TimeUtil.getCurrentTime();
			if (AllGameConfig.getInstance().env != RuntimeEnv.OTHER) {
				if(!((curTime - player.getProperty().getLastSpeakInChatTime()) >= ChatConstants.CHAT_TIME_INTERVAL)) {
					return;
				}
			}

			
			int scope = object.get("scope").asInt(1);
			String text = object.get("text").asText();
			if (text.isEmpty() || text.length() > 100) {
				return;
			}
			
			text = RefuseWordsFilter.getInstance().filt(text);
			((ObjectNode) object).put("text", text);			
			switch (scope) {
			// case ChatConstants.SCOPE_PRIVATE:
			// String to = object.get("to").asText();
			// PlayerSession ps = gameRoomSessions.get(to);
			// if (ps != null) {
			// ObjectNode obj = ((ObjectNode) object);
			// obj.put("from", (String) ps.getPlayer().getId());
			// // 给玩家自己发送聊天消息
			// // playerSession.onEvent(Events.event(obj));
			// ps.onEvent(Events.event(obj));
			// }
			// break;
			case ChatConstants.SCOPE_WORLD:
//			case ChatConstants.SCOPE_SYSTEM:// test
				ObjectNode obj = ((ObjectNode) object);
				obj.put("from", player.getName());
				ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
				node.put("chat", obj);
				playerSession.getGameRoom().sendBroadcast(Events.networkEvent(node));
				player.getProperty().setLastSpeakInChatTime(curTime);
				break;
			case ChatConstants.SCOPE_TEAM:
				player.sendResult(OperResultType.TEAM, 
						TeamManager.getSingleton().talkWords(object.get("text").asText(), player));
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 好友相关的操作 添加、删除、搜索
	 */
	JsonCommandHandler operFriend = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "operFriend";
		}

		@Override
		public void run(JsonNode object) {
			int type = object.get("type").asInt(1);
			int oper = object.get("oper").asInt(1);
			JsonNode nameNode = object.get("name");
			if (nameNode == null && type != 3) {
				LOG.info("param error");
				return;
			}
			String id = null;
			if (nameNode != null) {
				String name = object.get("name").asText();
				id = allPlayersCache.getPlayerIdByName(name);
				if (id == null && type != 3) {
					LOG.info("不能添加自己为好友");
					return;
				}
			}

			if (type == 1) { // 好友
				if (oper == 1) { // 增加
					int friendMaxCount = (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.FRIENDS_MAX_NUM);
					if (player.getFriends().myFriends.size() >= friendMaxCount) {
						LOG.info("player friend reach maximum");
						player.sendMiddleMessage(MessageCode.FRIEND_LIMIT);
						return;
					}
					if (player.guid.equals(id)) {
						player.sendMiddleMessage(MessageCode.FRIEND_LIMIT);
						return;
					}
					if (player.getFriends().myFriends.contains(id)) {
						player.sendMiddleMessage(MessageCode.FRIEND_EXISTS);
						return;
					}
					player.getFriends().addFriend(id);
					player.sendMiddleMessage(MessageCode.ADD_FRIEND_SUCCESS);
				} else { // 删除
					if (!player.getFriends().rmFriend(id)) {
						player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
						return;
					}
					player.sendMiddleMessage(MessageCode.REMOVE_FRIEND_SUCCESS);
				}
			} else if (type == 2) { // 黑名单
				if (oper == 1) {
					int friendMaxCount = (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.FRIENDS_MAX_NUM);
					if (player.getFriends().blackList.size() >= friendMaxCount) {
						LOG.info("player blackList reach maximum");
						player.sendMiddleMessage(MessageCode.BLACKLIST_LIMIT);
						return;
					}
					player.getFriends().addBlackList(id);
				} else {
					if (!player.getFriends().rmBlackList(id)) {
						player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
						return;
					}
					player.getFriends().addFriend(id);
				}
			} else { // 搜索
				ArrayNode searchList = DynamicJsonProperty.jackson.createArrayNode();
				if (id != null) {
					RoleEntity p = allPlayersCache.getPlayerInfo(id);
					if (p == null) {
						LOG.info("player is not exists");
						player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
						return;
					}
					searchList.add(player.getFriends().getPlayerInfo(id));
				} else { // 随机取近似等级的玩家
					List<String> existRoles = new ArrayList<String>();
					existRoles.addAll(player.getFriends().myFriends);
					existRoles.addAll(player.getFriends().blackList);
					existRoles.add(player.getRole().getGuid());
					List<RoleEntity> rndRoles = player.getAllPlayersCache().getRndFriends(existRoles, player.getProperty().getLevel(), 15, 5);
					if (rndRoles.size() == 0) {
						rndRoles = player.getAllPlayersCache().getRndFriends(existRoles, player.getProperty().getLevel(), 15, 10);
					}
					for (RoleEntity role : rndRoles) {
						searchList.add(player.getFriends().getPlayerInfo(role));
					}
				}

				ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
				obj.put("searchList", searchList);
				player.sendMessage(player.getFriends().getKey(), obj);
			}
		}

	};
	
	
	/**
	 * 初始化好友
	 */
	JsonCommandHandler initFriends = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "initFriends";
		}

		@Override
		public void run(JsonNode object) {
			Set<String> set = new HashSet<String>();
			List<RoleEntity> list = allPlayersCache.getRndFriends(1, 10, 2);
			for (RoleEntity role : list) {
				set.add(role.getGuid());
			}
			list = allPlayersCache.getRndFriends(11, 20, 2);
			for (RoleEntity role : list) {
				set.add(role.getGuid());
			}
			list = allPlayersCache.getRndFriends(21, 30, 2);
			for (RoleEntity role : list) {
				set.add(role.getGuid());
			}
			list = allPlayersCache.getRndFriends(31, 40, 2);
			for (RoleEntity role : list) {
				set.add(role.getGuid());
			}
			list = allPlayersCache.getRndFriends(41, 50, 2);
			for (RoleEntity role : list) {
				set.add(role.getGuid());
			}
			
			player.getFriends().setFriends(set);
			
		}
	};
	
	/**
	 * 查看好友的属性
	 */
	JsonCommandHandler friendInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "friendInfo";
		}

		@Override
		public void run(JsonNode object) {
			String name = object.get("name").asText();
			String id = allPlayersCache.getPlayerIdByName(name);
			if(id == null) {
				LOG.info("player is not exists");
				player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
				return;
			}
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			DefaultPlayer cache = objectCache.getCache(id, DefaultPlayer.class);
			if (cache == null) {
				LOG.error("player cache is null {}", id);
				player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
				return;
			}
			ObjectNode node = (ObjectNode)cache.getHeros().friendInfo();
			node.put("name", name);
			obj.put("friendInfo", node);
			player.sendMessage(player.getFriends().getKey(), obj);
		}
	};
	
	/**
	 * 查看忍界大战的防守者信息
	 */
	JsonCommandHandler warsInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "warsInfo";
		}
		
		@Override
		public void run(JsonNode object) {
			String name = object.get("name").asText();
			String id = allPlayersCache.getPlayerIdByName(name);
			if(id == null) {
				LOG.info("player is not exists");
				player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
				return;
			}
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			DefaultPlayer cache = objectCache.getCache(id, DefaultPlayer.class);
			if (cache == null) {
				LOG.error("player cache is null {}", id);
				player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
				return;
			}
			ObjectNode node = (ObjectNode)cache.getHeros().friendInfo();
			node.put("name", name);
			obj.put("warsInfo", node);
			player.sendMessage(player.getFriends().getKey(), obj);
		}
	};
	
//	static Map<String, Map<String, String>> channelFromItemIDsMap;
//	static {
//		channelFromItemIDsMap = Maps.newHashMap();
//		
//		Map<String, String> channel51 = Maps.newHashMap();
//		channel51.put("com.xxrz.hd3.hy.gold1", "com.xxrz.hd2.hy.gold1");
//		channel51.put("com.xxrz.hd3.hy.gold3", "com.xxrz.hd2.hy.gold3");
//		channel51.put("com.xxrz.hd3.hy.gold4", "com.xxrz.hd2.hy.gold4");
//		channel51.put("com.xxrz.hd3.hy.gold5", "com.xxrz.hd2.hy.gold5");
//		channel51.put("com.xxrz.hd3.hy.gold6", "com.xxrz.hd2.hy.gold6");
//		channel51.put("com.xxrz.hd3.hy.gold7", "com.xxrz.hd2.hy.gold7");
//		channel51.put("com.xxrz.hd3.hy.gold10", "com.xxrz.hd2.hy.gold10");
//		channelFromItemIDsMap.put("51", channel51);
//		channelFromItemIDsMap.put("52", channel51);
//		channelFromItemIDsMap.put("53", channel51);
//	}
//	public static void main(String[] args) {
//		System.out.println(transferItemId("com.xxrz.hd3.hy.gold1"));
//		System.out.println(transferItemId("com.xxrz.hd4.hy.gold2"));
//	}
	
	//这几个渠道内购道具id需要转换
	static List<String> specialChannel = new ArrayList<String>();
	{
		specialChannel.add("51");
		specialChannel.add("52");
		specialChannel.add("53");
	}
	
	private String transferItemId(String itemId) {
		String[] array = itemId.split("\\.");
		itemId = "";
		for (int i=0; i<array.length; i++) {
			if (i == 2) {
				itemId += "hd2";
			} else {
				itemId += array[i];
			}
			if (i < array.length -1) {
				itemId += ".";
			}
		}
		return itemId;
	}
	
	/**
	 * 验证ios充值订单
	 */
	JsonCommandHandler verifyReceipt = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "verifyReceipt";
		}

		@Override
		public void run(JsonNode object) {
			String receipt = object.get("receipt").asText();
			String isSandbox = object.get("isSandbox").asText();
			String channel = object.get("channel").asText();
			if (isEmpty(receipt) || isEmpty(channel)) {
				payLogger.error("缺少参数 receipt=" + receipt + " channel="
						+ channel);
				player.sendResult(OperResultType.PAY_VALID, MessageCode.FAIL,
						null);
				return;
			}
			if (channel.equals("1")
					|| channel.equals("7")
					|| channel.equals("50")
					|| channel.equals("51")
					|| channel.equals("52")
					|| channel.equals("53")
					|| channel.equals("54")
					|| channel.equals("55")
					|| channel.equals("56")
					|| channel.equals("57")
					|| channel.equals("58")
					|| channel.equals("59")) { // iOS
				// APPSTORE
				handAppStore(receipt, isSandbox, object.get("orderId").asText(), channel);
			} else if (channel.equals("2")) { // android anysdk
				saveOrder(receipt, object.get("orderId").asText());
			} else if (channel.equals("3") || channel.equals("4")) { // iOS
																		// 91和同步推
				saveOrder(receipt, object.get("orderId").asText());
			} else if (channel.equals("8")) {
				if (object.get("orderId") == null) {
					payLogger.error("GooglePlay 订单号为空 receipt=" + receipt);
					player.sendResult(OperResultType.PAY_VALID,
							MessageCode.FAIL, null);
					return;
				}
				player.addPayment(receipt, object.get("orderId").asText(),
						worldActivity);
				player.sendResult(OperResultType.PAY_VALID, MessageCode.OK,
						null);
			} else {
				payLogger.error("未知渠道 channel=" + channel);
				player.sendResult(OperResultType.PAY_VALID, MessageCode.FAIL,
						null);
				return;
			}

		}
	};
	
	private void handAppStore(String receipt, String isSandbox, String orderId, String channel) {
		JsonNode json = null;
		String sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";
		String appstore = "https://buy.itunes.apple.com/verifyReceipt";
		if ("1".equals(isSandbox)) { // 测试时去沙箱环境验证
			json = requestAppStore(sandbox, receipt);
		} else {
			json = requestAppStore(appstore, receipt);
		}
		if (json == null) {
			//再试一次
			payLogger.error("充值验证没有返回结果 重试一次 userId=" + player.getId() + " guid=" + player.getId() + " name=" + player.getName());
			if ("1".equals(isSandbox)) {
				json = requestAppStore(sandbox, receipt);
			} else {
				json = requestAppStore(appstore, receipt);
			}
			if (json == null) {
				payLogger.error("充值验证没有返回结果 userId=" + player.getId() + " guid=" + player.getId() + " name=" + player.getName()
						+ "isSandbox=" + isSandbox + " receipt=" + receipt);
				player.sendResult(OperResultType.PAY_VALID, MessageCode.FAIL, null);
				return;
			}
		}
		String status = json.get("status").asText();
		LOG.info("Payment sandbox status " + status);
		if ("21007".equals(status)) {// 苹果审核时用的是sandbox
			json = requestAppStore(sandbox, receipt);
			status = json.get("status").asText();
		}
		
		LOG.info("Payment status " + status);
		if ("0".equals(status)) { // 验证成功
			LOG.debug("验证成功 " + json.toString());
			JsonNode receiptNode = json.get("receipt");
			
			//	特殊处理appstore提审包充值
			String itemId = receiptNode.get("product_id").asText();
			if (specialChannel.contains(channel)) {
				LOG.info("Convert before itemId=" + itemId);
				itemId = transferItemId(itemId);
				LOG.info("Convert after itemId=" + itemId);
			}
			
			player.addPayment(itemId, orderId, worldActivity);
		} else {
			payLogger.error("验证失败，错误订单！ 把此人加入黑名单 userId=" + player.getId() + " guid=" + player.getId() + " name=" + player.getName()
						+ "isSandbox=" + isSandbox + " receipt=" + receipt);
			player.sendResult(OperResultType.PAY_VALID, MessageCode.FAIL, null);
		}
	}
	
	private void saveOrder(String itemId, String orderId) {
		RoleEntity p = allPlayersCache.getPlayerInfo(player.getId());
		List<String> list = new ArrayList<String>();
		list.add(itemId);
		list.add(p.getGuid());
		list.add(p.getName());
		PayUtil.getInstance().saveOrder(orderId, list);
	}
	
	private boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 去苹果服务器验证
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	private JsonNode requestAppStore(String urlStr, String receiptStr) {
		OutputStreamWriter out = null;
		HttpURLConnection con = null;
		InputStream is = null;
		try {
			URL dataUrl = new URL(urlStr);
			con = (HttpURLConnection) dataUrl.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("content-type", "text/json");
			con.setRequestProperty("Proxy-Connection", "Keep-Alive");
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			// con.setRequestProperty("receipt-data", receipt);
			con.setDoOutput(true);
			con.setDoInput(true);
			out = new OutputStreamWriter(con.getOutputStream());
			out.write(String.format(Locale.CHINA, "{\"receipt-data\":\"" + receiptStr + "\"}"));
			out.flush();
			out.close();
			is = con.getInputStream();
			JsonNode node = DynamicJsonProperty.jackson.readTree(is);
			is.close();
			con.disconnect();
			out = null;
			is = null;
			con = null;
			return node;
		} catch (MalformedURLException e) {
			LOG.error("MalformedURLException", e);
			e.printStackTrace();
		} catch (ProtocolException e) {
			LOG.error("ProtocolException", e);
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("IOException", e);
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public ArrayNode grandOnlinePlayer(int maxCount) {
		Iterator<PlayerSession> it = gameRoomSessions.values().iterator();
		int i = 0;
		ArrayNode all = DynamicJsonProperty.jackson.createArrayNode();
		while (it.hasNext()) {
			PlayerSession playerSession = it.next();
			if (i >= PlayerPractise.WQ_PLAYER_COUNT) {
				break;
			}
			if (playerSession.getPlayer() == null) {
				continue;
			}
			if (playerSession.getPlayer().getRole() == null) {
				continue;
			}
			String name = playerSession.getPlayer().getRole().getName();
			if(player.getRole() == null) {
				continue;
			}
			// 去掉自己
			if (name == null || name.equals(player.getRole().getName())) {
				continue;
			}
			ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
			int charId = playerSession.getPlayer().getRole().getCharId();
			array.add(charId).add(name);
			all.add(array);
			i++;
		}
		
		int size = all.size();
		if(size < PlayerPractise.WQ_PLAYER_COUNT) {
			List<RoleEntity> highestRoleEntities = allPlayersCache.getHighestRoleEntities(maxCount - size);
			for (RoleEntity roleEntity : highestRoleEntities) {
				ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
				array.add(roleEntity.getCharId()).add(roleEntity.getName());
				all.add(array);
			}
		}
		return all;
	}
	
	JsonCommandHandler payAddGold = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "payAddGold";
		}

		@Override
		public void run(JsonNode object) {
			String itemId = object.get("itemId").asText();
			String orderId = object.get("orderId").asText();
			JsonNode exchangeGold = object.get("exchangeGold");
			if (exchangeGold != null) {
				player.addKYPayment(orderId, worldActivity, exchangeGold.asInt());
			} else {
				player.addPayment(itemId, orderId, worldActivity);
			}
			
		}

	};
	
	JsonCommandHandler gmReward = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "gmReward";
		}

		@Override
		public void run(JsonNode object) {
			ArrayNode rewardArray = (ArrayNode)object.get("items");
			VariousItemEntry[] awards = new VariousItemEntry[rewardArray.size()];
			for (int i=0; i<rewardArray.size(); i++) {
				ArrayNode reward = (ArrayNode)rewardArray.get(i);
				VariousItemEntry item = new VariousItemEntry(reward.get(0).asText(), reward.get(1).asInt());
				awards[i] = item;
			}
			VariousItemUtil.doBonus(player, awards, LogConst.GM_GIVE, true);
		}

	};
	
	JsonCommandHandler gmRewardByMail = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "gmRewardByMail";
		}

		@Override
		public void run(JsonNode json) {
			player.getMail().addSysItemMail(json.get("rewards").asText(), json.get("title").asText(), json.get("content").asText());
		}
	};
	
	JsonCommandHandler gmAddVipScore = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "gmAddVipScore";
		}

		@Override
		public void run(JsonNode json) {
			player.getProperty().addVipScore(player, json.get("vipScore").asInt());
		}

	};
	JsonCommandHandler gmSetLevel = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "gmSetLevel";
		}

		@Override
		public void run(JsonNode json) {
			player.getProperty().setLevel(json.get("lv").asInt());
			player.getProperty().setExp(0);
		}

	};
	

	JsonCommandHandler readMail = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "readMail";
		}

		@Override
		public void run(JsonNode object) {
			int tab = object.get("tab").asInt();
			int id = object.get("id").asInt();
			PlayerMail mail = player.getMail();
			mail.readMail(tab, id);
		}

	};
	
	JsonCommandHandler rewardMail = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "rewardMail";
		}
		
		@Override
		public void run(JsonNode object) {
			int tab = object.get("tab").asInt();
			int id = object.get("id").asInt();
			PlayerMail mail = player.getMail();
			mail.rewardMail(player, tab, id);
		}
		
	};

	JsonCommandHandler removeMail = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "removeMail";
		}

		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt();
			int tab = object.get("tab").asInt();
			PlayerMail mail = player.getMail();
			mail.removeMail(player,tab, id);
		}

	};

	JsonCommandHandler sendMail = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "sendMail";
		}

		@Override
		public void run(JsonNode object) {
			String name = object.get("name").asText();
			String text = object.get("text").asText();
			if (text.length() < 1 || text.length() > 50) {
				LOG.info("text length error: {}", text.length());
				return;
			}
			String id = allPlayersCache.getPlayerIdByName(name);
			if (id == null) {
				LOG.info("player is not exists");
				player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
				return;
			}
			PlayerSession ps = gameRoomSessions.get(id);
			if (ps != null) {
				Set<String> blacklist = ps.getPlayer().getFriends().blackList;
				if (blacklist.contains(id)) {
					LOG.info("已被对方拉黑，不能发送邮件");
					return;
				}
				ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
				obj.put("from", player.getName());
				obj.put("text", text);
				ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
				source.put("revMail", obj);
				ps.onEvent(Events.event(source));
				
			} else {
				DefaultPlayer cache = objectCache.getCache(id, DefaultPlayer.class);
				if (cache == null) {
					LOG.error("player cache is null {}", id);
				} else {
					Set<String> blacklist = cache.getFriends().blackList;
					if (blacklist.contains(id)) {
						LOG.info("已被对方拉黑，不能发送邮件");
						return;
					}
					// is not thread safe
					cache.getMail().addPriveteMail(player.getName(), text);
				}
			}

			player.sendMiddleMessage(MessageCode.SEND_MAIL_SUCCESS);
		}

	};

	JsonCommandHandler revMail = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "revMail";
		}

		@Override
		public void run(JsonNode object) {
			player.getMail().addPriveteMail(object.get("from").asText(), object.get("text").asText());
		}

	};

	/**
	 * 竞技场挑战
	 */
	JsonCommandHandler challege = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "challege";
		}

		@Override
		public void run(JsonNode object) {
			player.getArena().challege(player, object.get("idx").asInt(), objectCache);
		}

	};
	/**
	 * 竞技场挑战
	 */
	JsonCommandHandler beastFight = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "beastFight";
		}
		
		@Override
		public void run(JsonNode object) {
			float val = Float.parseFloat(ActivityType.BEAST_ORDER.getValue());
			if(val > 0) {
				Building building = AllGameConfig.getInstance().buildings.getBuilding(BuildingType.WSZN.getId());
				if (player.getBuilding().isFunCding(BuildingType.WSZN.getId())) {
					int code = VariousItemUtil.doBonus(player, building.getFuncCost()[0], LogConst.WSZN_FLIGHT, false);
					if (code != MessageCode.OK) {
						player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
						return;
					} else {
						startFightBeast(val);
					}
				} else {
					startFightBeast(val);
					player.getBuilding().addFunCd(building);
				}
				
			}
			
		}

		public void startFightBeast(float val) {
			AttackerGroup attackerGroup = player.getHeros().getAttackerGroup();
			Beast beast = AllGameConfig.getInstance().beast.getBeast((int)val);
			int roleId =beast.getRole();
			List<IFighter> fighters = Lists.newArrayList();
			IHero enemy = new Monster(roleId);
			enemy.setLevel(beast.getLevel());
			fighters.add(new BaseFighter(enemy));
			PVBeastCombatManager pvBeastCombatManager = new PVBeastCombatManager(attackerGroup, new DefenderGroup(fighters), player,worldActivity);
			pvBeastCombatManager.start();
		}
		
	};

	/**
	 * 竞技场复仇
	 */
	JsonCommandHandler revenge = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "revenge";
		}

		@Override
		public void run(JsonNode object) {
			String guid = allPlayersCache.getPlayerIdByName(object.get("name").asText());
			if (guid != null) {
				player.getArena().revenge(player, guid, objectCache);
			}

		}

	};
	// /**
	// * 竞技场获得每日奖励
	// */
	// JsonCommandHandler arenaReward = new JsonCommandHandler() {
	// @Override
	// public String getCommand() {
	// return "arenaReward";
	// }
	//
	// @Override
	// public void run(JsonNode object) {
	// player.getArena().arenaReward(player);
	// }
	//
	// };
	/**
	 * 温泉人物信息
	 */
	JsonCommandHandler washingGuys = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "washingGuys";
		}

		@Override
		public void run(JsonNode object) {
			ArrayNode all = grandOnlinePlayer(PlayerPractise.WQ_PLAYER_COUNT);
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("guys", all);
			player.sendMessage(msg);
		}

	};
	/**
	 * 请求活动
	 */
	JsonCommandHandler requestActivities = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "requestActivities";
		}

		@Override
		public void run(JsonNode object) {
			worldActivity.sendAllActivityMsg(player,0);
		}

	};
	
	JsonCommandHandler beastGuys = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "beastGuys";
		}

		@Override
		public void run(JsonNode object) {
//			Set<String> beastActHurtPlayers = wa.getBeastActHurtPlayers();
//			int i=0;
//			for (String string : beastActHurtPlayers) {
//				
//				i++;
//			}
			ArrayNode all = grandOnlinePlayer(WorldActivity.MAX_BEAST_ROOM_PLAYERS);
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("guys", all);
			player.sendMessage(msg);
		}

	};

	/**
	 * 刷新队伍
	 */
	JsonCommandHandler refreshTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refreshTeam";
		}

		@Override
		public void run(JsonNode object) {
			player.getRecruit().refreshTeam(player);
		}
	};
	/**
	 * 刷新队伍
	 */
	JsonCommandHandler recFight = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "recFight";
		}

		@Override
		public void run(JsonNode object) {
			player.getRecruit().recFight(player,objectCache);
		}
	};
	/**
	 * 请求排行榜
	 */
	JsonCommandHandler requestRank = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "requestRank";
		}

		@Override
		public void run(JsonNode object) {			
			List<RankModel> models = GameRankMaster.getInstance().arenaRank.getModels(1, 50);
			ArrayNode arrayNode = DynamicJsonProperty.jackson.createArrayNode();
			int i = 1;
			for (RankModel rankModel : models) {
				RoleEntity playerInfo = allPlayersCache.getPlayerInfo(rankModel.getGuid());
				if (playerInfo != null) {
					ArrayNode arr = rankModel.toJsonArray(playerInfo).add(i++);
//					DefaultPlayer dp = objectCache.getCache(playerInfo.getGuid(), DefaultPlayer.class);
//					String teamName = dp.getTeam().getName();// 玩家所属的军团名称
					String teamName = playerInfo.getTeamName(); //  优化
					arr.add(teamName);
					arrayNode.add(arr);
				}
			}
			ObjectNode objectNode = DynamicJsonProperty.jackson.createObjectNode();
			objectNode.put("myOrder", GameRankMaster.getInstance().arenaRank.getIndex(player.getId()));
			player.sendMessage("player", objectNode);
			player.sendMessage("rankInfo", arrayNode);
		}
	};
	/**
	 * 请求排行榜
	 */
	JsonCommandHandler getTopList = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getTopList";
		}

		@Override
		public void run(JsonNode object) {
			int type = object.get("type").asInt();
			RankType rankType = RankType.getActById(type);
			ObjectNode objectNode = DynamicJsonProperty.jackson.createObjectNode();
			objectNode.put("type", type);
			if (rankType == RankType.gold) { // 充值排行
				List<ArrayNode> rankList = Lists.newArrayList();
				boolean active = ActivityType.EXCHANGE_ORDER.isActive();
				if (active) {
					ActivityWrapper activity = AllGameConfig.getInstance().activitys
							.getActivity(ActivityType.EXCHANGE_ORDER.getActId());
					objectNode.put("open", 1);
					objectNode.put("start", activity.getStartDayString());
					objectNode.put("end", activity.getEndDayString());
					wapperGoldJsonNode(objectNode, rankList);
				} else {
					objectNode.put("open", 0);
					if (ContextConfig.SHOW_EXCHANGE_ORDER) {
						wapperGoldJsonNode(objectNode, rankList);
					} else {
						objectNode.put("list", DynamicJsonProperty.convertToArrayNode(rankList));
					}
				}
			} else if (rankType == RankType.score) { // 积分排行榜
				if (ActivityType.RAFFLE_SCORE.isActive()) {					
					ArrayNode ranklist = DynamicJsonProperty.jackson.createArrayNode();
					for (ArrayNode node : rankType.getRankList()) {
						ranklist.add(node);
					}
					ArrayNode myRank = DynamicJsonProperty.jackson.createArrayNode();
					ObjectNode node = GameRankMaster.getInstance().scoreRank.getRankInfo(player.getId());
					myRank.add(node.get("ranking"));
					myRank.add(node.get("score"));
					ranklist.add(myRank);
					objectNode.put("list", ranklist);
				}
			} else {
				List<ArrayNode> rankList = rankType.getRankList();
				objectNode.put("list",
						DynamicJsonProperty.convertToArrayNode(rankList));
				if (rankType == RankType.power) {
					Integer powerIndx = GameRankMaster.getInstance().powerRank
							.getPowerIndex(player.getName());
					if (powerIndx != null) {
						objectNode.put("myOrder", powerIndx);
					}
				}
			}
			player.sendMessage("topListInfo", objectNode);
		}

		public void wapperGoldJsonNode(ObjectNode objectNode, List<ArrayNode> rankList) {
			Map<String, Long> payMap = worldActivity.getSortedPayMap();
			if(payMap.isEmpty()) {
				rankList = GameRankMaster.getInstance().payRank.getTopList();
			}else {
				for (Entry<String, Long> entry : payMap.entrySet()) {
					String guid = entry.getKey();
					Long value = entry.getValue();
					RoleEntity e = allPlayersCache.getPlayerInfo(guid);
					ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
					array.add(e.getName()).add(e.getLevel()).add(e.getCharId()).add(e.getPower()).add(value/10);
					rankList.add(array);
				}
			}
			
			objectNode.put("list", DynamicJsonProperty.convertToArrayNode(rankList));
		}
	};
	/**
	 * 请求挑战记录
	 */
	JsonCommandHandler requestRankRecord = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "requestRankRecord";
		}

		@Override
		public void run(JsonNode object) {
			player.getArena().requestRankRecord(player);
		}
	};
	/**
	 * 查看玩家基础信息
	 */
	JsonCommandHandler showPlayer = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "showPlayer";
		}
		
		@Override
		public void run(JsonNode object) {
			String name = object.get("name").asText();
			String id = allPlayersCache.getPlayerIdByName(name);
			if (id == null) {
				LOG.info("player is not exists");
				player.sendMiddleMessage(MessageCode.PLAYER_NOT_EXIST);
				return;
			}
			ArrayNode playerInfo = player.getFriends().getPlayerInfo(id);
			player.sendMessage("playerShortInfo", playerInfo);
		}
	};
	
	/**
	 * 游戏退到后台
	 */
	JsonCommandHandler enterBackground = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "enterBackground";
		}
		
		@Override
		public void run(JsonNode object) {
//			LOG.info("收到退出消息");
			LogUtil.doLogoutLog(player);
		}
	};
	
	/**
	 * 获取军团信息
	 **/
	JsonCommandHandler applyTeams = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "applyTeams";
		}
		
		@Override
		public void run(JsonNode object) {
			if (!object.has("page")) {
				LOG.error("applyTeamErr arguments!");
				player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
				return;
			}
			
			int page = object.get("page").asInt();
			TeamManager.getSingleton().applyTeams(page <= 0 ? 1 : page, player);
		}
	};
	
	/**
	 * 查询军团
	 **/
	JsonCommandHandler searchTeams = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "searchTeams";
		}
		
		@Override
		public void run(JsonNode object) {
			if (!object.has("condition")) {
				LOG.error("searchTeams#Err arguments!");
				player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
				return;
			}
			TeamManager.getSingleton().searchTeams(object.get("condition").asText(), player);
		}
	};
	
	/**
	 * 获取军团详细信息
	 **/
	JsonCommandHandler getTeamInfo = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "getTeamInfo";
		}

		@Override
		public void run(JsonNode object) {
//			object.get("name").asText()
			String name = "";
			TeamManager.getSingleton().getTeamInfo(name, player);
		}
	};

	/**
	 * 创建军团
	 **/
	JsonCommandHandler createTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "createTeam";
		}
		
		@Override
		public void run(JsonNode object) {
			String name = object.get("name").asText();
			int lv = object.get("lv").asInt();
			String intro = object.get("intro").asText();
			TeamManager.getSingleton().createTeam(name, intro, lv, player);
//			player.getTeam().createTeam(player, name, intro, lv);
		}
	};
	
	/**
	 * 解散军团
	 **/
	JsonCommandHandler dismissTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "dismissTeam";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getTeam().dissolveTeam(player);
		}
	};
	
	/**
	 * 发出申请入团
	 **/
	JsonCommandHandler applyJoinTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "applyJoinTeam";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getTeam().submitJoinTeamApply(player, object.get("name").asText());
		}
	};
	
	/**
	 * 同意入团申请
	 **/
	JsonCommandHandler agreeJoinTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "agreeJoinTeam";
		}
		
		@Override
		public void run(JsonNode object) {
			String agreePlayerID = null;
			if (object.has("id")) {
				agreePlayerID = object.get("id").asText();
			}
			TeamManager.getSingleton().agreeJoinTeamApply(player, agreePlayerID);
		}
	};
	
	/**
	 * 拒绝入团申请
	 **/
	JsonCommandHandler refuseJoinTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refuseJoinTeam";
		}

		@Override
		public void run(JsonNode object) {
			String refusePlayerID = null;
			if (object.has("id")) {
				refusePlayerID = object.get("id").asText();
			}
//			player.sendResult(OperResultType.TEAM, 
//					TeamManager.getSingleton().refuseJoinTeamApply(player, refusePlayerID));
			player.getTeam().refuseJoinTeamApply(player, refusePlayerID);
		}
	};
	
	/**
	 * 撤销入团申请
	 **/
	JsonCommandHandler undoJoinTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "undoJoinTeam";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getTeam().undoJoinTeamApply(player, object.get("name").asText());
		}
	};
	
	/**
	 * 弹劾军团长
	 **/
	JsonCommandHandler applyDelateTeamBoss = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "applyDelateTeamBoss";
		}
		
		@Override
		public void run(JsonNode object) {}
	};
	
	/**
	 * 同意弹劾请求
	 **/
	JsonCommandHandler agreeDelateTeamBoss = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "agreeDelateTeamBoss";
		}
		
		@Override
		public void run(JsonNode object) {}
	};
	
	/**
	 * 更改军团轮廓信息
	 **/
	JsonCommandHandler modifyTeamProfile = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "modifyTeamProfile";
		}
		
		@Override
		public void run(JsonNode object) {
			Integer joinLv = null;
			String announ = null, intro = null;
			
			if (object.has("announ")) {
				announ = object.get("announ").asText();
			}
			if (object.has("intro")) {
				intro = object.get("intro").asText();
			}
			if (object.has("joinLv")) {
				joinLv = object.get("joinLv").asInt();
			}
			
			if (announ == null && 
				intro == null && 
				joinLv == null) {
				player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
				return;
			}
			
			Integer pinzhi = object.get("pinzhi").asInt();
			Integer dengji = object.get("dengji").asInt();
			
			player.getTeam().modifyTeamProfile(player, intro, announ, joinLv,pinzhi,dengji);
		}
	};
	
	/**
	 * 开除军团成员
	 **/
	JsonCommandHandler kickTeamMember = new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "kickTeamMember";
		}

		@Override
		public void run(JsonNode object) {
			if (object.has("memberId")) {	// 开除军团成员
				player.sendResult(OperResultType.TEAM, 
						TeamManager.getSingleton().dismissTeamMember(
								player, object.get("memberId").asText()));
			} else {	// 退出军团
				player.getTeam().leaveTeam(player);
			}
		}
	};
	
	/**
	 * 更改军团成员职务
	 **/
	JsonCommandHandler modifyTeamMemberTitle = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "modifyTeamMemberTitle";
		}

		@Override
		public void run(JsonNode object) {
			if (!(object.has("memberId") && 
					object.has("isPromotion"))) {
				player.sendResult(
						OperResultType.TEAM, 
						MessageCode.FAIL
						);
				return;
			}
			
			String memberId = object.get("memberId").asText();
			boolean isPromotion = object.get("isPromotion").asBoolean();
			TeamManager.getSingleton().modifyTeamMemberTitle(
					memberId, 
					player,
					isPromotion
					);
		}
		
	};
	
	// 提升军团阶级
	JsonCommandHandler upgradeTeam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "upgradeTeam";
		}

		@Override
		public void run(JsonNode object) {
			TeamManager.getSingleton().upgradeTeam(player);
		}
	};
	
	// 捐赠财富
	JsonCommandHandler donateTeamWealth = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "donateTeamWealth";
		}

		@Override
		public void run(JsonNode object) {
			int silver = 0, gold = 0;
			if (object.has("silver")) {
				silver = object.get("silver").asInt();
			}
			if (object.has("gold")) {
				gold = object.get("gold").asInt();
			}
			TeamManager.getSingleton().donateTeamWealth(player, silver, gold);
		}
	};
	
	// 获取军团部队信息
	JsonCommandHandler getTeamArmy = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getTeamArmy";
		}

		@Override
		public void run(JsonNode object) {
			int cp = 1;// 默认 1	
			if (object.has("cpage")) {
				cp = object.get("cpage").asInt(); 
			}
			int type = 0; // 0 表示获取军团部队 1 获取自己派遣的忍者 2 获取全部军团部队
			if (object.has("type")) {
				type = object.get("type").asInt(); 
			}
			TeamManager.getSingleton().getTeamArmy(player,cp,type);
		}
	};
	
	// 派出忍者到军团部队
	JsonCommandHandler sendRolesToTeamArmy = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "sendRolesToTeamArmy";
		}

		@Override
		public void run(JsonNode object) {
			List<Integer> hids = Lists.newArrayList();
			ArrayNode arr = (ArrayNode)object.get("hids");
			for (int i = 0; i < arr.size(); ++i) {
				hids.add(arr.get(i).asInt());
			}
			player.getTeam().sendRoleToArmy(player, hids);
		}
		
	};
	
	// 撤回在军团部队中的忍者
	JsonCommandHandler cancelRolesFromTeamArmy = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "cancelRolesFromTeamArmy";
		}

		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			
			Map<String, List<Integer>> cancelRolesMemberID2Index = Maps.newHashMap();
			ArrayNode arr = (ArrayNode)object.get("cancelRoles");
			for (int i = 0; i < arr.size(); ++i) {
				ObjectNode o = (ObjectNode)arr.get(i);
				ArrayNode arr2 = (ArrayNode)o.get("roles");
				List<Integer> rolesIdx = Lists.newArrayList();
				for (int j = 0; j < arr2.size(); ++j) {
					rolesIdx.add(arr2.get(j).asInt());
				}
				cancelRolesMemberID2Index.put(o.get("id").asText(), rolesIdx);
			}
			TeamManager.getSingleton().cancelRoleFromTeamArmy(player, cancelRolesMemberID2Index);
		}
		
	};
	
	// 购买军团科技
	JsonCommandHandler buyTeamTech = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "buyTeamTech";
		}

		@Override
		public void run(JsonNode object) {
		}
		
	};
	
	// 研发军团科技
	JsonCommandHandler studyTeamTech = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "studyTeamTech";
		}

		@Override
		public void run(JsonNode object) {
		}
		
	};
	
	// 拉取军团科技信息
	JsonCommandHandler getTeamTechs = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "getTeamTechs";
		}

		@Override
		public void run(JsonNode object) {
		}
		
	};
	
	JsonCommandHandler teamNotify = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "teamNotify";
		}
		@Override
		public void run(JsonNode object) {
			String[] strs = null;
			if (object.has("params")) {
				ArrayNode params = (ArrayNode) object.get("params");
				strs = new String[params.size()];
				for (int i = 0; i < params.size(); ++i) {
					strs[i] = params.get(i).asText();
				}
			}
			switch (object.get("reason").asInt()) {
			case 0:		//	JOIN_MEMBER	
				player.getMail().addSysMail(
						MessageCode.MAIL_TITLE_TEAM_AGREE_JOIN, 
						MessageCode.MAIL_CONTENT_TEAM_AGREE_JOIN, 
						strs);
				player.getTeam().updateMyTeam(strs[0]);
				TeamManager.getSingleton().removeTeamJoinApply(player);
				break;
			case 1:		//	REFUSE_MEMBER
				player.getTeam().removeApplyTeam(object.get("name").asText());
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_REFUSE_JOIN_TEAM, object);
				break;
			case 2:		//	KICK_MEMBER
				player.getTeam().kickMemberFromTeam();
				player.getMail().addSysMail(
						MessageCode.MAIL_TITLE_TEAM_KICK_MEMBER, 
						MessageCode.MAIL_CONTENT_TEAM_KICK_MEMBER, 
						strs[0]);
				break;
			case 5:		//	DISMISS_TEAM
				player.getMail().addSysMail(
						MessageCode.MAIL_TITLE_TEAM_DISMISS, 
						MessageCode.MAIL_CONTENT_TEAM_DISMISS, 
						strs[0]);
				player.getTeam().kickMemberFromTeam();
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_DISMISS);
				break;
			case 7: // MODIFY_TEAM_PROFILE
				ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
				info.put("intro", object.get("intro").asText());
				info.put("announ", object.get("announ").asText());
				info.put("joinLevel", object.get("joinLevel").asInt());
				player.sendResult(OperResultType.TEAM, info);
				break;
			case TeamConstants.TEAM_CANCEL_ARMY_ROLE:
				ArrayNode arr = (ArrayNode) object.get("roles");
				List<Integer> roles = Lists.newArrayList();
				for (int i = 0; i < arr.size(); ++i) {
					roles.add(DynamicJsonProperty.jackson.convertValue(arr.get(i), Integer.class));
				}
				for(int h=0;h<roles.size();h++){
					Integer hid = roles.get(h);
					player.getHeros().removeHeroState(hid, HeroState.TEAM_ARMY_STATE);
				}
				if(object.has("name")){
					player.getMail().addSysMail(
							MessageCode.TEAM_ARMY_CANCELHERO_TITLE, 
							MessageCode.TEAM_ARMY_CANCELHERO_CONTENT,
							object.get("name").asText());
				}
				player.sendMessage("heros", DynamicJsonProperty.jackson.createObjectNode());
				player.sendResult(OperResultType.TEAM, MessageCode.OK);
				break;
			case TeamConstants.TEAM_WAR_OPEN:
				player.getMail().addSysMail(
						MessageCode.TEAM_WAR_OPEN_TITLE, 
						MessageCode.TEAM_WAR_OPEN_CONTENT, 
						strs[0]);
				break;
			case TeamConstants.TEAM_WAR_WIN:
				player.getMail().addSysMail(
						MessageCode.TEAM_WAR_WIN_TITLE, 
						MessageCode.TEAM_WAR_WIN_CONTENT, 
						strs[0],strs[1],strs[2]);
				break;
			case TeamConstants.TEAM_WAR_LOSE:
				player.getMail().addSysMail(
						MessageCode.TEAM_WAR_LOSE_TITLE, 
						MessageCode.TEAM_WAR_LOSE_CONTENT, 
						strs[0],strs[1]);
				break;
			case TeamConstants.TEAM_WAR_DRAW:
				player.getMail().addSysMail(
						MessageCode.TEAM_WAR_DRAW_TITLE, 
						MessageCode.TEAM_WAR_DRAW_CONTENT, 
						strs[0]);
				break;
			case TeamConstants.TEAM_WAR_ON_PLAYER_HERO_DIE:
				 Integer roleIndex = object.get("roleIndex").asInt();
				 String guidID = object.get("guidID").asText();
				 TeamManager.getSingleton().updateDefendPlayerState(player, guidID, roleIndex);
				break;
			case TeamConstants.TEAM_WAR_END_RANK_MAIL:
				Integer title = object.get("mailtitle").asInt();
				Integer content = object.get("mailcontent").asInt();
				ObjectNode earr = (ObjectNode) object.get("items");
				String type = earr.get("type").asText();
				int count = earr.get("count").asInt();
				String rankID = object.get("rankID").asText();
				player.getMail().addSysItemMail(type,count, title, content,rankID);
				
				break;
			}
		}
	};
	
	// 查询据点信息
	JsonCommandHandler getStrongHoldInfo = new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "getStrongHoldInfo";
		}
		
		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			TeamManager.getSingleton().getStrongHoldInfo(player);
		}
		
	};	
	// 派出部队忍者到据点 socket上传:-->key:sendRolesToStrongHold--数据:{"strongHoldId":21,"shIdx":2,"guid":"4Pb8sslbUe0XxDqJ/O4M25VCc54","heroIdx":2}
	JsonCommandHandler sendRolesToStrongHold = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "sendRolesToStrongHold";
		}

		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM,
						MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			Integer strongHoldId = object.get("strongHoldId").asInt(); //0-20
			Integer shIdx = object.get("shIdx").asInt();
			Integer heroIdx = object.get("heroIdx").asInt();
			String guid = object.get("guid").asText();
			
			TeamManager.getSingleton().sendRolesToStrongHold(player,strongHoldId, shIdx,guid,heroIdx);
		}
	};

	// 从据点撤回部队忍者
	JsonCommandHandler cancelRolesFromStrongHold = new JsonCommandHandler() {

		@Override
		public String getCommand() {
			return "cancelRolesFromStrongHold";
		}

		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM,
						MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			Integer strongHoldid = object.get("strongHoldid").asInt(); // 0-20
			Integer idx = object.get("idx").asInt();
			TeamManager.getSingleton().cancelRolesFromStrongHold(player,strongHoldid, idx);
		}

	};

	//##########################################################################

	//查询军团战信息 得到交战双方的据点防守，攻占信息
	JsonCommandHandler getTeamWarInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getTeamWarInfo";
		}
		
		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			
			TeamManager.getSingleton().getTeamWarInfo(player,object,worldActivity);
		}
	};
	
	JsonCommandHandler getWarHeroIndexList = new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "getWarHeroIndexList";
		}
		
		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			
			TeamManager.getSingleton().getWarHeroIndexList(player);
		}
	};
	
	// index 部队索引id
	JsonCommandHandler updateWarBaseHeroState = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "updateWarBaseHeroState";
		}
		
		@Override
		public void run(JsonNode object) {
			Integer index = object.get("index").asInt();
			TeamManager.getSingleton().updateWarBaseHeroState(player,index);
		}
	};
	
	// 战斗 
	JsonCommandHandler startTeamWarFight  =  new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "startTeamWarFight";
		}
		
		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			
			TeamManager.getSingleton().startTeamWarFight(player,object);
			
			TeamManager.getSingleton().saveTeamWarDataDB(player);
		}
	};
	
	//// 防守我方的空据点，攻击敌方的空据点
	//  warType: 0 占领敌方空据点，1 防守我方空据点
	// baseIndex:0 据点id
	// type :(0 是忍者列表，1 是索引列表)
	// index: 忍者id/索引列表id
	// {"warType":0,"baseIndex":0,"hes":[{"type":0,"index":0},{},{}]}
	// 不返回数据
	JsonCommandHandler attackDefendFight =  new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "attackDefendFight";
		}
		
		@Override
		public void run(JsonNode object) {
			TeamManager.getSingleton().startTeamWarFight(player,object);
			
			TeamManager.getSingleton().saveTeamWarDataDB(player);
		}
	};
	
	JsonCommandHandler teamWarStatus = new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "teamWarStatus";
		}
		
		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			
			TeamManager.getSingleton().teamWarStatusFunc(player);
		}
	};
	
	JsonCommandHandler teamWarst = new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "teamWarst";
		}
		
		@Override
		public void run(JsonNode object) {
			TeamManager.getSingleton().teamWarStateFuncS2C(player);
		}
	};
	
	// {"channelID":"99990"}
	JsonCommandHandler getQuDaoPayInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getQuDaoPayInfo";
		}

		@Override
		public void run(JsonNode object) {
			String channelid = object.get("channelID").asText();
			if(channelid == null || channelid.equals("")){
				return;
			}
			GlobalProps globalProps = getLDroom().getGlobalProps();
			globalProps.sendS2CPayMessageFunc(channelid, player);
		}
	};
	
	JsonCommandHandler getTeamLogInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getTeamLogInfo";
		}

		@Override
		public void run(JsonNode object) {
			String teamName = player.getTeam().getName();
			if (teamName.equals("")) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
				return;
			}
			
			TeamManager.getSingleton().getTeamLogInfo(player);
		}
	};

	private LDRoom getLDroom() {
		if(ldRoom == null ) {
			ldRoom = AppContext.getBean(LDRoom.class);
		}
		return ldRoom;
	}
}
