package com.jelly.team;

import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;
import io.nadron.context.AppContext;
import io.nadron.event.Events;
import io.nadron.example.lostdecade.LDRoom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.RefuseWordsFilter;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.GuildWar;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.JsonEntity;
import com.dol.cdf.common.constant.GameConstId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.game.command.ChatConstants;
import com.jelly.hero.Hero;
import com.jelly.hero.HeroState;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.player.EmojiFilter;
import com.jelly.rank.GameRankMaster;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 军团
 **/
public class Team extends JsonEntity {

	@JsonProperty("name")
	private String name; // 军团名称

	@JsonProperty("announ")
	private String announce; // 军团公告

	@JsonProperty("intro")
	private String intro;	// 军团介绍

	@JsonProperty("mbrs")
	private Map<String, TeamMember> members; // 军团成员

	@JsonProperty("t2c")
	private Map<TeamMemberTitle, Integer> title2CountMap; // 军团中剩余可使用的军衔数量,-1表示无上限

	@JsonProperty("joinLv")
	private int joinLevel; // 加入军团时要求最低等级

	@JsonProperty("exp")
	private int exp = 0; // 军团经验值

	@JsonProperty("lv")
	private int level = 0; // 军团阶级

	@JsonProperty("wealth")
	private int wealth; // 军团拥有财富

	@JsonProperty("cid")
	private String commanderId; // 军团长的guid

	@JsonProperty("ajp")
	private Map<String, Long> applyJoinPlayers;

	@JsonProperty("ctm")
	private long createTime; // 创建时间(ms)
	
	@JsonProperty("cancelheronew")
	private Map<String, List<Integer>> cancelHerosFromArmyNew = Maps.newHashMap();
	
	@JsonProperty("useSign")
	private int useSign = 0; // 军团状态  0正常 1解散
	
	///------------------------------ 军团战 ----------------------------------///
	
	private boolean warOpened = false; // 标记军团战是否开启
	
	@JsonProperty("tWarName")
	private String teamWarTeamName; // 军团战期间对战方军团名称
	
	@JsonProperty("winNum")
	private int winNum = 0;   /// 军团战胜利次数
	
	@JsonProperty("warScore")
	private int warScore = 0;
	
	@JsonProperty("warStage")
	private int warStage = 0;//0第一圈可以打  1第二圈可以打 2第三圈可以打 
	
	@JsonProperty("warHoldBase")
	private List<WarHoldBase> strongHoldsWar = Lists.newArrayList();// 军团战中的军团据点信息$
	
	//----------------------------------------------------------------//
	
	@JsonProperty("cpinzhi")
	private int cpinzhi = 0;
	
	@JsonProperty("cdengji")
	private int cdengji = 0;
	
	@JsonProperty("newArmyHero")
	private Map<String, List<ArmyHero>> newArmyHero = Maps.newHashMap(); // 新的军团部队存储方式
	
	@JsonProperty("narmyhold")
	private List<ArmyStrongHold> armyStrongHolds = Lists.newArrayList(); //新军团据点信息
	
	@JsonProperty("warmm")
	private Map<String, WarMember> warMemberMap = Maps.newHashMap(); // 参见军团战中的军团成员
	
	// 静态成员变量
	private static LDRoom room = AppContext.getBean(LDRoom.class);
	private static ObjectCacheService objectCacheService = AppContext.getBean(ObjectCacheService.class);
	private static AllPlayersCache allPlayersCache = AppContext.getBean(AllPlayersCache.class);
	
	private static final Logger logger = LoggerFactory.getLogger("teamInfo");
	
	@JsonProperty("log")
	private List<TeamLog> teamLog = Lists.newArrayList();
	
	@JsonProperty("warcount")
	private int warcount = 0;
	
	public Team() {
		this.name = "";
		this.announce = "";
		this.intro = "";
		this.level = 0;
		this.exp = 0;
		this.members = Maps.newHashMap();
		this.joinLevel = 0;
		this.applyJoinPlayers = Maps.newHashMap();
		this.commanderId = "";
		this.createTime = 0;
		this.title2CountMap = Maps.newHashMap();
		this.useSign = 0;
		
		// 设置的默认值 // 默认值
		this.cpinzhi = 1;
		this.cdengji = 5;//30
		this.armyStrongHolds = Lists.newArrayList();
	}

	public Team(String name, String intro, int joinLv, Player commander) {
		this();
		this.name = name;
		this.intro = intro;
		this.joinLevel = joinLv;
		this.level = 1;
		this.exp = 0;
		this.commanderId = commander.getId();
		this.members.put(commander.getId(), new TeamMember(commander.getId(),
				TeamMemberTitle.COMMANDER));
		this.createTime = System.currentTimeMillis(); // 军团创建时间
		
		int title = 1;
		String[] jobNums = AllGameConfig.getInstance().teams.getGuild(this.level).getJobnumber();
		for (String jn : jobNums) {
			this.title2CountMap.put(TeamMemberTitle.parse(title), Integer.parseInt(jn));
			++title;
		}
		
		for(int hd = 0;hd<21;hd++){
			this.armyStrongHolds.add(new ArmyStrongHold(hd));
		}
		
		this.useSign = 0;
		
		allPlayersCache.setPlayerTeamName(commanderId,name);
	}
	
	public TeamMember getTeamMember(String guid){
		return members.get(guid);
	}

	public int calcPower() {
		int pow = 0;
		for (Map.Entry<String, TeamMember> entry : members.entrySet()) {
			RoleEntity role = allPlayersCache.getPlayerInfo(entry.getValue()
					.getGuid());
			if (role != null) {
				pow += role.getPower();
			}
		}
		return pow;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 发送消息到军团聊天频道
	 **/
	public int talkWords(String words, Player talker, String... insWords) {
		List<String> mbrs = Lists.newArrayList(); // 收到聊天消息的军团成员
		TeamMemberTitle talkerTitle; // 发言人的职务
		synchronized (this) {
			if (!members.containsKey(talker.getId())) {
				return MessageCode.TEAM_NO_TEAM_MEMBER;
			}
			talkerTitle = members.get(talker.getId()).getTitle();
			for (TeamMember mbr : members.values()) {
				mbrs.add(mbr.getGuid());
			}
		}

		// 发送内容
		ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
		node.put("scope", ChatConstants.SCOPE_TEAM);
		node.put("from", talker.getName()); // 发言人名称
		node.put("title", talkerTitle.ordinal()); // 发言人职务
		node.put("text", words); // 发言内容
		if (insWords.length > 0) {
			node.put("params", DynamicJsonProperty.convertToArrayNode(insWords));
		}

		// 发送给每位在线的军团成员
		for (String id : mbrs) {
			PlayerSession ps = room.getSessions().get(id);
			if (ps != null) {
				ps.getPlayer().sendMessage("chat", node);
			}
		}
		return MessageCode.OK;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Long> getApplyJoinPlayers() {
		return this.applyJoinPlayers;
	}

	public long getCreateTime() {
		return this.createTime;
	}
	
	public int getUseSign() {
		return useSign;
	}

	public void setUseSign(int useSign) {
		this.useSign = useSign;
	}
	
	// 胜利，失败，排名奖励，邮件发送
	public void sendMailToGuidAwards(String guid,Integer reason,Integer title,Integer content,VariousItemEntry items,int rankid)
	{
		PlayerSession ps = room.getSessions().get(guid);
		if(ps != null){
			ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
			source.put("reason", reason);
			ObjectNode earr = DynamicJsonProperty.jackson.createObjectNode();
			earr.put("type", items.getType());
			earr.put("count", items.getAmount());
			source.put("items", earr);
			source.put("mailtitle", title);
			source.put("mailcontent", content);
			source.put("rankID", rankid+"");
			ps.onEvent(Events.event("teamNotify", source));
		}else{
			TeamMember mm = this.members.get(guid);
			if(mm != null){
				ItemMail mail = new ItemMail();
				mail.setSysMail(title, content, rankid+"");
				mail.addRewards(items.getType(),items.getAmount());
				mm.addMailItem(mail);
			}
//			DefaultPlayer dp = objectCacheService.getCache(guid, DefaultPlayer.class);
//			if (dp != null) {
//				dp.getMail().addSysItemMail(items.getType(),items.getAmount(),title,content,rankid+"");
//				objectCacheService.putObject(dp);
//				dp = null;
//			}
		}
	}

	
	public void sendMailToAllMemberTeamWar(String teamName,Integer reason,Integer title,Integer content,String... insWords)
	{
		for (TeamMember mbr1 : members.values()) {
			PlayerSession ps = room.getSessions().get(mbr1.getGuid());
			if (ps != null) {
				ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
				source.put("reason", reason);
				source.put("params", DynamicJsonProperty.convertToArrayNode(insWords));
				ps.onEvent(Events.event("teamNotify", source));
			} else { // 不在线
				ItemMail mail = new ItemMail();
				mail.setSysMail(title, content, insWords);
				mbr1.addMailItem(mail);
				
//				DefaultPlayer dp = objectCacheService.getCache(mbr1.getGuid(), DefaultPlayer.class);
//				if (dp != null) {
//					dp.getMail().addSysMail(
//							title, 
//							content, 
//							insWords);
//					objectCacheService.putObject(dp);
//					dp = null;
//				}
			}
		}
	}
	
	public synchronized int dissolve(Player player) {
		if (warOpened) { // 军团战期间不可解散军团
			return MessageCode.TEAM_WAR_CANNOT_DISSOLVE;
		}
		
		TeamMember mbr = members.get(player.getId());
		if (mbr == null || mbr.getTitle() != TeamMemberTitle.COMMANDER) {
			return MessageCode.TEAM_RIGHTS_REFUSED;
		}
		
		for (TeamMember mbr1 : members.values()) {
			
			RoleEntity role = allPlayersCache.getPlayerInfo(mbr1.getGuid());
			if(role == null){
				logger.info("no player ="+mbr1.getGuid());
				continue;
			}
			
			// 移除成员派出到部队中的所有忍者
			cancelMemberRolesFromArmy(player, mbr1.getGuid(), new ArrayList<Integer>());
			mailToPlayerfunc(mbr1.getGuid());
			
			// 将成员从军团中移除
			PlayerSession ps = room.getSessions().get(mbr1.getGuid());
			
			if (ps != null) {
				ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
				source.put("reason", TeamChangeNotify.DISMISS_TEAM.ordinal());
				source.put("params", DynamicJsonProperty.convertToArrayNode(new String[] { player.getName() }));
				ps.onEvent(Events.event("teamNotify", source));
			} else {
				DefaultPlayer dp = objectCacheService.getCache(mbr1.getGuid(), DefaultPlayer.class);
				if (dp != null) {
					dp.getTeam().kickMemberFromTeam();
					// 发送军团解散的邮件给玩家
					dp.getMail().addSysMail(
							MessageCode.MAIL_TITLE_TEAM_DISMISS, 
							MessageCode.MAIL_CONTENT_TEAM_DISMISS, 
							player.getName());
					objectCacheService.putObject(dp);
					dp = null;
				}
			}
			allPlayersCache.setPlayerTeamName(mbr1.getGuid(), "");
		}
		members.clear();
		useSign = 1;
		return MessageCode.OK;
	}
	
	public synchronized int dissolveTwo() {
		
		for (TeamMember mm : members.values()) 
		{
			String guid = mm.getGuid();
			RoleEntity role = allPlayersCache.getPlayerInfo(guid);
			if(role == null){
				logger.info("no player ="+guid);
				continue;
			}
			PlayerSession ps = room.getSessions().get(guid);
			if(ps != null){ // 在线恢复
				DefaultPlayer dp = (DefaultPlayer)ps.getPlayer();
				dp.getTeam().setApplyTeams(new ArrayList<String>());
				dp.getTeam().setLeaveTeam("");
				dp.getTeam().setName("");
				dp.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
			}
			
			logger.info("guid ="+guid);
			
			if(guid == null || guid.equals("")){
				continue;
			}
			DefaultPlayer dp = objectCacheService.getCache(guid, DefaultPlayer.class);
			if(dp != null){
				dp.getTeam().setApplyTeams(new ArrayList<String>());
				dp.getTeam().setLeaveTeam("");
				dp.getTeam().setName("");
				dp.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
				objectCacheService.putObject(dp);
				dp = null;
			}
			
			allPlayersCache.setPlayerTeamName(mm.getGuid(), "");
		}
		
		cancelHerosFromArmyNew.clear();
		applyJoinPlayers.clear();
		members.clear();
		useSign = 1;
		return MessageCode.OK;
	}
	
	public synchronized int clearTeamWealthfunc(int value)
	{
		if(value >this.wealth || value <0){
			this.wealth = 0;
		}else{
			this.wealth = this.wealth - value;
		}
		
		return MessageCode.OK;
	}
	
	public synchronized int leave(Player player) {
		if (warOpened) { // 军团战期间不可退出军团
			return MessageCode.TEAM_WAR_CANNOT_LEAVE;
		}
		if (members.containsKey(player.getId())) {
			TeamMember self = members.get(player.getId());
			
			cancelMemberRolesFromArmy(player, self.getGuid(), new ArrayList<Integer>());
			mailToPlayerfunc(player.getId());
			
			members.remove(player.getId());
			if (title2CountMap.get(self.getTitle()) > -1) {
				title2CountMap.put(self.getTitle(), title2CountMap.get(self.getTitle()) + 1);
			}
			// 发送消息给其它在线的军团成员,通知有一位成员已退出军团
			for (TeamMember mbr : members.values()) {
				PlayerSession ps = room.getSessions().get(mbr.getGuid());
				if (ps != null) {
					ObjectNode source = DynamicJsonProperty.jackson
							.createObjectNode();
					source.put("scope", ChatConstants.SCOPE_TEAM);
					source.put("from", player.getName());
					source.put("text", MessageCode.TEAM_MEMBER_LEAVE);
					source.put("params",
							DynamicJsonProperty
									.convertToArrayNode(new String[] { player
											.getName() }));
					ps.getPlayer().sendMessage("chat", source);
				}
			}
			allPlayersCache.setPlayerTeamName(player.getId(), "");
			return MessageCode.OK;
		} else {
			return MessageCode.TEAM_NO_TEAM_MEMBER;
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized int dismiss(Player player, String mid) {
		if (warOpened) { // 军团战期间不可开除团员
			return MessageCode.TEAM_WAR_CANNOT_DISMISS;
		}
		
		TeamMember mbr1 = members.get(player.getId());
		TeamMember mbr2 = members.get(mid);
		
		if (mbr1 == null || mbr2 == null) {
			return MessageCode.TEAM_NO_TEAM_MEMBER;
		}
		
		if (mbr1.getTitle().ordinal() > TeamMemberTitle.STAFF_SERGEANT.ordinal() 
				|| mbr2.getTitle().ordinal() <= mbr1.getTitle().ordinal()) {
			return MessageCode.TEAM_RIGHTS_REFUSED;
		}
		
		cancelMemberRolesFromArmy(player, mid, new ArrayList<Integer>());
		mailToPlayerfunc(mid);
		
		members.remove(mid);
		// 归还成员使用的职务名额
		if (title2CountMap.get(mbr2.getTitle()) > -1) {
			title2CountMap.put(mbr2.getTitle(), title2CountMap.get(mbr2.getTitle()) + 1);
		}
		
		// 发送消息告知该玩家已被踢出军团
		PlayerSession ps = room.getSessions().get(mid);
		if (ps != null) { // 在线玩家
			ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
			source.put("reason", TeamChangeNotify.KICK_MEMBER.ordinal());
			source.put("params", DynamicJsonProperty.convertToArrayNode(Lists.newArrayList(name)));
			ps.onEvent(Events.event("teamNotify", source));
		} else { // 离线玩家
			DefaultPlayer dp = objectCacheService.getCache(mid, DefaultPlayer.class);
			if (dp != null) {
				dp.getTeam().notifyDismiss(dp,name);
				objectCacheService.putObject(dp);
				dp = null;
			}
		}
		allPlayersCache.setPlayerTeamName(mid, "");
		
		// 发送消息到军团聊天频道,通知其它成员该玩家已被踢出军团
		for (TeamMember mbr : members.values()) {
			ps = room.getSessions().get(mbr.getGuid());
			if (ps != null) {
				ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
				source.put("from", player.getName());
				source.put("scope", ChatConstants.SCOPE_TEAM);
				source.put("text", MessageCode.BROADCAST_KICK_TEAM_MEMBER);
				// {0}—踢人者的职务名,{1}—踢人者忍者村名,{2}—被踢者忍者村名
				source.put("params", DynamicJsonProperty.convertToArrayNode(
						Lists.newArrayList(mbr1.getTitle(), 
								player.getName(), 
								allPlayersCache.getPlayerInfo(mbr2.getGuid()).getName())));
				ps.getPlayer().sendMessage("chat", source);
			}
		}
		
		return MessageCode.OK;
	}
	
	// 提交入团申请
	public synchronized int submitJoinApply(Player player) {
		// 检查玩家是否符合入团等级
		if (player.getProperty().getLevel() < joinLevel) {
			return MessageCode.LEVEL_NOT_ENOUGH;
		}
		if (members.size() >= AllGameConfig.getInstance().teams.getMaxMembers(level)) {
			return MessageCode.TEAM_MEMBER_FULL;
		}
		if (applyJoinPlayers.size() >= 100) {
			this.fixApplyReachData();
			if(applyJoinPlayers.size() >= 100){
				return MessageCode.TEAM_APPLY_JOIN_TOO_MUCH;
			}
		}
		applyJoinPlayers.put(player.getId(), System.currentTimeMillis());
		return MessageCode.OK;
	}
	// 当申请人数》100 时候，清一次三天前申请的数据
	private void fixApplyReachData()
	{
		long ct = System.currentTimeMillis();
		List<String> mlist = Lists.newArrayList();
		for (String guid : applyJoinPlayers.keySet()) {
			long t = applyJoinPlayers.get(guid);
			long day = TimeUtil.diffDays(ct,t);
			if(day>=3){
				mlist.add(guid);
			}
		}

		for (String string : mlist) {
			applyJoinPlayers.remove(string);
			PlayerSession ps = room.getSessions().get(string);
			if (ps != null) {
				DefaultPlayer ply = (DefaultPlayer)ps.getPlayer();
				if(ply != null){
					ply.getTeam().removeApplyTeam(this.name);
				}
			}else{
				DefaultPlayer dp = objectCacheService.getCache(string, DefaultPlayer.class);
				if (dp != null) {
					dp.getTeam().removeApplyTeam(this.name);
					objectCacheService.putObject(dp);
					dp = null;
				}
			}
		}
	}
	
	/**
	 * 批准入团申请
	 * 
	 * @param Player player - 受理入团申请的军团成员Player
	 * @param String playerID - 新入团玩家ID null表示批准所有入团申请, 非null表示处理给定玩家提交的入团申请
	 **/
	public synchronized boolean agreeJoinApply(Player player, String playerID) {
		TeamMember mbr = members.get(player.getId());
		if (mbr == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_TEAM_MEMBER);
			return false;
		}
		if (!(mbr.getTitle().ordinal() <= TeamMemberTitle.STAFF_SERGEANT.ordinal())) {
			// 权限不足, 只有上士或更高职务的军团成员可以执行该操作
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return false;
		}
		
		List<String> failApplys = Lists.newArrayList();
		List<String> playersID = Lists.newArrayList();
		if (playerID == null) {
			playersID.addAll(applyJoinPlayers.keySet());
		} else {
			playersID.add(playerID);
		}
		
		if ((playersID.size() + members.size()) > AllGameConfig.getInstance().teams.getMaxMembers(level)) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_MEMBER_FULL);
			return false;
		}
		
		for (String id : playersID) {
			RoleEntity role = allPlayersCache.getPlayerInfo(id);
			if (role.getTeamName()!=null && !role.getTeamName().equals("")) {
				failApplys.add(id);
			} else {
				members.put(id, new TeamMember(id, TeamMemberTitle.SERGEANT));
				allPlayersCache.setPlayerTeamName(id, name);
				PlayerSession ps = room.getSessions().get(id);
				if (ps != null) {
					ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
					source.put("reason", TeamChangeNotify.JOIN_MEMBER.ordinal());
					source.put("params", DynamicJsonProperty.convertToArrayNode(
							new String[] { name, player.getName() }));
					ps.onEvent(Events.event("teamNotify", source));
				} else {
					DefaultPlayer dp = objectCacheService.getCache(id, DefaultPlayer.class);
					if (dp != null) {
						TeamManager.getSingleton().removeTeamJoinApply(dp); // 移除该玩家提交给其它军团入团申请
						dp.getTeam().updateMyTeam(name);
						dp.getMail().addSysMail(
								MessageCode.MAIL_TITLE_TEAM_AGREE_JOIN, 
								MessageCode.MAIL_CONTENT_TEAM_AGREE_JOIN, 
								name, player.getName());
						objectCacheService.putObject(dp);
						dp = null;
					}
				}
				applyJoinPlayers.remove(id);
			}
		}
		ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
		res.put("failApplys", DynamicJsonProperty.convertToArrayNode(failApplys));
		player.sendResult(OperResultType.TEAM, res);
		return true;
	}
	
	// 拒绝入团申请
	public synchronized boolean refuseJoinApply(Player player, String playerID) {
		TeamMember mbr = members.get(player.getId());
		if (mbr == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_TEAM_MEMBER);
			return false;
		}
		if (!(mbr.getTitle().ordinal() <= TeamMemberTitle.STAFF_SERGEANT.ordinal())) {
			// 权限不足, 只有上士或更高职务的军团成员可以执行该操作
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return false;
		}
		List<String> refusedApplys = Lists.newArrayList();
		if (playerID == null) {
			refusedApplys.addAll(applyJoinPlayers.keySet());
		} else {
			refusedApplys.add(playerID);
		}
		
		List<String> failApplys = Lists.newArrayList();
		// 拒绝所有提交的入团申请
		for (String pid : refusedApplys) {
			RoleEntity role = allPlayersCache.getPlayerInfo(pid);
			if (role.getTeamName()!=null && role.getTeamName().equals("")) {
				PlayerSession ps = room.getSessions().get(pid);
				if (ps != null) {
					// 检查玩家是否已有所属军团
					if (ps.getPlayer().getTeam().getName().equals("")) {
						ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
						source.put("reason", TeamChangeNotify.REFUSE_MEMBER.ordinal());
						source.put("name", name);
						ps.onEvent(Events.event("teamNotify", source));
					}
				} else {
					DefaultPlayer dp = objectCacheService.getCache(pid, DefaultPlayer.class);
					if (dp != null) {
						dp.getTeam().removeApplyTeam(name);
						objectCacheService.putObject(dp);
						dp = null;
					}
				}
			} else {
				failApplys.add(pid);
			}
			applyJoinPlayers.remove(pid);
		}
		ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
		res.put("failApplys", DynamicJsonProperty.convertToArrayNode(failApplys));
		player.sendResult(OperResultType.TEAM, res);
		return true;
	}
	
	// 撤销入团申请
	public synchronized int undoJoinApply(String playerID) {
		if (applyJoinPlayers.containsKey(playerID)) {
			applyJoinPlayers.remove(playerID);
		}
		return MessageCode.OK;
	}
	
	/**
	 * 任命新军团长
	 **/
	private void appointCommander(Player player, String newCommanderMbrId) {
		PlayerSession ps = room.getSessions().get(newCommanderMbrId);
		int newCommanderLv = 0;
		if (ps != null) {
			newCommanderLv = ps.getPlayer().getProperty().getLevel();
		} else {
			newCommanderLv = allPlayersCache.getPlayerInfo(newCommanderMbrId).getLevel();
		}
		
		// 新军团长等级不能低于TEAM_CREATE_VILLAGE_LEVEL要求的等级
		int commanderRequireLv = (Integer) AllGameConfig.getInstance().gconst
				.getConstant(GameConstId.TEAM_CREATE_VILLAGE_LEVEL);
		if (newCommanderLv < commanderRequireLv) {
			player.sendResult(OperResultType.TEAM, MessageCode.LEVEL_NOT_ENOUGH);
			return;
		}
		
		// 原军团长角色昵称
		String oldCommanderName = allPlayersCache.getPlayerInfo(player.getId()).getName();
		// 新军团角色昵称
		String newCommanderName = allPlayersCache.getPlayerInfo(newCommanderMbrId).getName();
		
		members.get(player.getId()).setTitle(TeamMemberTitle.SERGEANT);
		members.get(newCommanderMbrId).setTitle(TeamMemberTitle.COMMANDER);
		title2CountMap.put(TeamMemberTitle.DEPUTY_COMMANDER, 
				title2CountMap.get(TeamMemberTitle.DEPUTY_COMMANDER) + 1);
		commanderId = newCommanderMbrId;
		player.sendResult(OperResultType.TEAM);
		
		// 委任新军团长,将这个消息通知给所有在线成员
		for (TeamMember mbr : members.values()) {
			ps = room.getSessions().get(mbr.getGuid());
			if (ps != null) {
				ObjectNode source = DynamicJsonProperty.jackson
						.createObjectNode();
				source.put("scope", ChatConstants.SCOPE_TEAM);
				source.put("text",
						MessageCode.BROADCAST_APPOINT_NEW_TEAM_COMMANDER);
				source.put("params",
						DynamicJsonProperty.convertToArrayNode(new String[] {
								oldCommanderName, newCommanderName }));
				ps.getPlayer().sendMessage("chat", source);
			}
		}
	}

	/**
	 * 提升军团成员职务
	 **/
	public synchronized void promoteMemberTitle(Player player, String memberId) {
		if (!(existMember(player.getId()) && existMember(memberId))) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_TEAM_MEMBER);
			return;
		}

		TeamMember destMember = members.get(memberId);
		TeamMember srcMember = members.get(player.getId());

		// 操作者需确保自己的职务高于被升职成员升职前的职务
		if (srcMember.getTitle().ordinal() >= destMember.getTitle().ordinal()) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return;
		}

		TeamMemberTitle newTitle = destMember.getTitle().getPromoteAfterTitle(
				title2CountMap);
		if (newTitle == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NOT_ENOUGH_TITLE);
			return;
		}
		
		if (newTitle == TeamMemberTitle.COMMANDER) {
			// 委任新团长
			appointCommander(player, memberId);
		} else {
			if (title2CountMap.get(destMember.getTitle()) > -1) {
				title2CountMap.put(destMember.getTitle(),
						title2CountMap.get(destMember.getTitle()) + 1);
			}
			if (title2CountMap.get(newTitle) > -1) {
				title2CountMap.put(newTitle, title2CountMap.get(newTitle) - 1);
			}
			destMember.setTitle(newTitle);
			
			ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
			res.put("id", memberId);
			res.put("title", newTitle.ordinal());
			player.sendResult(OperResultType.TEAM, res);
		}
	}

	/**
	 * 降低军团成员职务
	 **/
	public synchronized void demotionMemberTitle(Player player, String memberId) {
		
		if (!(existMember(player.getId()) && existMember(memberId))) {
			player.sendResult(OperResultType.TEAM,
					MessageCode.TEAM_NO_TEAM_MEMBER);
			return;
		}

		TeamMember destMember = members.get(memberId);
		TeamMember srcMember = members.get(player.getId());
		
		// 操作者需确保自己的职务高于被升职成员升职前的职务
		if (srcMember.getTitle().ordinal() >= destMember.getTitle().ordinal()) {
			player.sendResult(OperResultType.TEAM,
					MessageCode.TEAM_RIGHTS_REFUSED);
			return;
		}
		
		TeamMemberTitle newTitle = destMember.getTitle().getDemoteAfterTitle(title2CountMap);
		if (newTitle == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NOT_ENOUGH_TITLE);
			return;
		}
		
		if (title2CountMap.get(destMember.getTitle()) > -1) {
			title2CountMap.put(destMember.getTitle(), title2CountMap.get(destMember.getTitle()) + 1);
		}
		if (title2CountMap.get(newTitle) > -1) {
			title2CountMap.put(newTitle, title2CountMap.get(newTitle) - 1);
		}
		destMember.setTitle(newTitle);
		
		ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
		res.put("id", memberId);
		res.put("title", newTitle.ordinal());
		player.sendResult(OperResultType.TEAM, res);
	}
	

	/**
	 * 增加军团经验
	 * 
	 * @param exp
	 *            - 增加经验值
	 **/
	public int addExp(int exp) {		
//		int lvMaxExp = AllGameConfig.getInstance().teams.getUpgradeExp(this.level);
//		if (lvMaxExp > 0) {
//			if ((this.exp += exp) > lvMaxExp) {
//				this.exp = lvMaxExp;
//			}
//		}
		return this.exp;
	}

	/**
	 * 提升军团阶级
	 **/
	public synchronized boolean upgrade(Player player) {
		TeamMember myMbr = members.get(player.getId());
		// 检查是否具有升级军团等级的权限
		if (myMbr.getTitle().ordinal() > TeamMemberTitle.STAFF_SERGEANT.ordinal()) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return false;
		}
		// 检查军团是否满足升级要求的所有条件
		int[] costs = AllGameConfig.getInstance().teams.getUpgradeCosts(level);
		if (costs == null) { // 已达到最高阶级
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_CANNOT_UPGRADE);
			return false;
		}
		if (wealth < costs[1]) { // 财富不足
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_CANNOT_UPGRADE);
			return false;
		}
		wealth -= costs[1]; // 扣除升级消耗的财富
		++level; // 更新军团等级
		exp = 0;
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		info.put("level", level);
		info.put("wealth", wealth);
//		player.sendResult(OperResultType.TEAM, info);
		player.sendResult(OperResultType.TEAM, MessageCode.CHAT_TEAM_UPGRADE_PROMPT,info);
		// 在军团聊天频道发送消息通知在线成员升级信息
		talkWords(String.valueOf(MessageCode.CHAT_TEAM_UPGRADE_PROMPT)
				, player, new String[] { String.valueOf(level) });
		
		logger.info("version="+AllGameConfig.getInstance().maxVersion+","+"modules_func=upgrade"+","+"guid="+player.getId()+","+"roleName="+player.getName()+","+"level="+player.getRole().getLevel()+","+"wealth=-"+costs[1]);
		
//		TeamLog log = new TeamLog();
//		log.type = 3;
//		log.lv = level;
//		addTeamLog(log);
		
		return true;
	}
	
	public synchronized boolean donateWealth(Player player, int silver, int gold) {
		if (gold <= 0) {
			player.sendResult(OperResultType.TEAM, MessageCode.GOLD_NOT_ENUGH);
			return false;
		}
		if (silver < 0) {
			player.sendResult(OperResultType.TEAM, MessageCode.SILVER_NOT_ENUGH);
			return false;
		}
		if (!player.getProperty().hasEnoughMoney("silver", silver)) {
			player.sendResult(OperResultType.TEAM, MessageCode.SILVER_NOT_ENUGH);
			return false;
		}
		
		if (!player.getProperty().hasEnoughMoney("gold", gold)) {
			player.sendResult(OperResultType.TEAM, MessageCode.GOLD_NOT_ENUGH);
			return false;
		}
			
		// 军团财富不能超过当前等级能够容纳的财富上限
		int[] rates = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_DONATE_GOLD_CONV_WEALTH);
//		int maxWealth = AllGameConfig.getInstance().teams.getMaxWealth(level);
		int addOfWealth = gold * rates[1];	
//		if ((addOfWealth + wealth) > maxWealth) {
//			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_CANNOT_DONATE_MORE_WEALTH);
//			return false;
//		}
		
		wealth += addOfWealth;
		int contribute = members.get(player.getId()).addContribute(gold * rates[2]); // 增加该成员对军团贡献值
		player.getProperty().changeMoney(-gold, -silver); // 扣除捐赠花费的金币与银币
		ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
		res.put("wealth", wealth);
		res.put("contribute", contribute);
		player.sendResult(OperResultType.TEAM, MessageCode.TEAM_DONATE_WAEALTH, res);
		talkWords(String.valueOf(MessageCode.CHAT_TEAM_DONATE_PROMPT)
				, player, new String[] { player.getName(), String.valueOf(addOfWealth) });
		
		logger.info("version="+AllGameConfig.getInstance().maxVersion+","+"modules_func=donateWealth"+","+"guid="+player.getId()+","+"roleName="+player.getName()+","+"level="+player.getRole().getLevel()+","+"gold="+gold+","+"silver="+silver+","+"addOfWealth="+addOfWealth);
		
		return true;
	}
	
	/**
	 * 军团基本信息
	 **/
	public synchronized ObjectNode getBasicInfo() {
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		info.put("name", name); // 名称
		info.put("level", level); // 等级
		info.put("intro", intro); // 军团介绍
		info.put("announ", announce); // 军团公告
		info.put("joinLevel", joinLevel); // 入团等级
		info.put("wealth", wealth); // 军团财富
		info.put("power", calcPower()); // 军团战斗力	
		RoleEntity role = allPlayersCache.getPlayerInfo(commanderId);
		info.put("commander", role != null ? role.getName() : "???"); // 军团长角色昵称
		info.put("curMembers", members.size()); // 当前军团成员人数
		info.put("maxMembers", AllGameConfig.getInstance().teams
				.getGuild(level).getMaxnumber()); // 军团成员人数上限
		return info;
	}
	
	/**
	 * 军团详细信息
	 **/
	public ObjectNode getDetailInfo(Player player) {
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		info.put("name", name); // 名称
		info.put("exp", exp); // 经验
		info.put("level", level); // 等级
		info.put("intro", intro); // 军团介绍
		info.put("announ", announce); // 军团公告
		info.put("joinLevel", joinLevel); // 入团等级
//		info.put("wealth", wealth); // 军团财富
		info.put("power", calcPower()); // 军团战斗力
		info.put("commanderName", allPlayersCache.getPlayerInfo(commanderId)
				.getName()); // 军团长角色昵称
		
		info.put("pinzhi", this.cpinzhi);
		info.put("dengji", this.cdengji);
		info.put("warcount", this.warcount);

		// 军团成员信息
		long nowMillis = System.currentTimeMillis();
		ArrayNode mbrs = DynamicJsonProperty.jackson.createArrayNode();
		for (TeamMember mbr : members.values()) {
			ObjectNode mbrInfo = DynamicJsonProperty.jackson.createObjectNode();
			mbrInfo.put("id", mbr.getGuid()); // 角色的guid
			RoleEntity role = allPlayersCache.getPlayerInfo(mbr.getGuid());
			mbrInfo.put("name", role.getName()); // 角色昵称
			mbrInfo.put("level", role.getLevel()); // 角色等级
			mbrInfo.put("title", mbr.getTitle().ordinal()); // 军团职务
			mbrInfo.put("charId", role.getCharId()); // 角色头像ID
			mbrInfo.put("arenaRanking", GameRankMaster.getInstance()
					.getArenaRank().getIndex(mbr.getGuid())); // 竞技场排名
//			mbrInfo.put("contribute", mbr.getContribute()); // 角色军团内的贡献
			int realPow = role.getPower();
			PlayerSession ps = room.getSessions().get(mbr.getGuid());
			if (ps != null) {
				realPow = ps.getPlayer().getHeros().getPlayerPower();
			}
			mbrInfo.put("power", realPow); // 角色战斗力
			int leaveSecs = ps != null ? 0 : TimeUtil
					.convertMilisecondsToSeconds(nowMillis
							- role.getLastLogin().getTime());
			mbrInfo.put("lastOfflineTime", leaveSecs); // 离线时间,0表示该成员在线
			
			List<ArmyHero> mlist = newArmyHero.get(mbr.getGuid());
			int zhanli = 0;
			int cishu = mbr.getPci();
			int addCount = 0;
			if(mlist != null && !mlist.isEmpty()){
				for(int km = 0;km<mlist.size();km++)
				{
					ArmyHero mhero = mlist.get(km);
					zhanli += mhero.getHero().getAllPower();
					
//					cishu += mhero.getHeroCiShu();
					addCount += mhero.getHeroCiShu();
				}
			}
			
			int[] reaward = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.SEND_NINJA_REWARD);
			int addCifu = reaward[0];
			int addGongXian =reaward[1];
			
			this.wealth += addCifu*addCount;
			mbr.addContribute(addGongXian*addCount);
			
			mbrInfo.put("contribute", mbr.getContribute()); // 角色军团内的贡献
			
			cishu += addCount;
			mbr.setPci(cishu);
			mbrInfo.put("paiqiancishu", cishu); // 派遣次数
			mbrInfo.put("paiqianzhanli", zhanli);// 派遣战力
			
			mbrs.add(mbrInfo);
		}
		
		info.put("wealth", wealth); // 军团财富
		
		info.put("members", mbrs);
		if (members.get(player.getId()) == null) {	//修复之前的错误数据
			return null;
		}
		// 未处理的入团申请
		if (members.get(player.getId()).getTitle().ordinal() <= TeamMemberTitle.STAFF_SERGEANT
				.ordinal()) {
			ArrayNode applyItems = DynamicJsonProperty.jackson
					.createArrayNode();
			for (Map.Entry<String, Long> e : applyJoinPlayers.entrySet()) {
				RoleEntity role = allPlayersCache.getPlayerInfo(e.getKey());
				int level = role.getLevel(), pow = role.getPower();
				PlayerSession ps = room.getSessions().get(e.getKey());
				if (ps != null) {
					Player p = ps.getPlayer();
					level = p.getProperty().getLevel();
					pow = p.getHeros().getPlayerPower();
				}
				ObjectNode applyInfo = DynamicJsonProperty.jackson
						.createObjectNode();
				applyInfo.put("id", e.getKey());
				applyInfo.put("name", role.getName());
				applyInfo.put("level", level);
				applyInfo.put("power", pow);
				int leaveSecs = TimeUtil.convertMilisecondsToSeconds(nowMillis
						- e.getValue());
				applyInfo.put("time", leaveSecs);
				applyItems.add(applyInfo);
			}
			info.put("applyJoin", applyItems);
		}

		return info;
	}
	
	public synchronized boolean modifyProfile(Player player, String intro, String announ, Integer joinLv,Integer pinzhi,Integer dengji) {
		TeamMember mbr = members.get(player.getId());
		if (mbr == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_TEAM_MEMBER);
			return false;
		}
		if (mbr.getTitle().ordinal() > TeamMemberTitle.DEPUTY_COMMANDER.ordinal()) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return false;
		}
		
		ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
		
		intro = EmojiFilter.filterEmoji(intro);
		
		if (intro != null) {
			if (intro.length() > AllGameConfig.getInstance().teams.getMaxIntroWords()) {
				player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
				return false;
			}
			if (RefuseWordsFilter.getInstance().contain(intro)) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_INTRO_REFUSE_WORDS);
				return false;
			}
			this.intro = intro;
		}
		
		if (announ != null) {
			if (announ.length() > AllGameConfig.getInstance().teams.getMaxAnnounWords()) {
				player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
				return false;
			}
			if (RefuseWordsFilter.getInstance().contain(announ)) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_ANNOUNCEMENT_REFUSE_WORDS);
				return false;
			}
			this.announce = announ;
			
		}
		
		if (joinLv != null) {
			int[] joinLvConds = AllGameConfig.getInstance().teams.getJoinTeamLevelConds();
			if (!(joinLv >= joinLvConds[0] && joinLv <= joinLvConds[1])) {
				player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
				return false;
			}
			this.joinLevel = joinLv;
		}
		
		this.cpinzhi =pinzhi;
		this.cdengji = dengji;
		
		res.put("intro", this.intro);
		res.put("announ", this.announce);
		res.put("joinLevel", this.joinLevel);
		res.put("pinzhi", this.cpinzhi);
		res.put("dengji", this.cdengji);
		player.sendResult(OperResultType.TEAM, res);
		return true;
	}
	
	public List<Integer> popCancelRolesFromArmy(String mid) {
		List<Integer> hes = cancelHerosFromArmyNew.get(mid);
		if (hes != null) {
			cancelHerosFromArmyNew.remove(mid);
		}else{
			hes = Lists.newArrayList();
		}
		return hes;
	}
	public int getNewArmyHeroCount(String guid){
		int n = 0;
		List<ArmyHero> hes = newArmyHero.get(guid);
		if(hes != null){
			n = hes.size();
		}
		return n;
	}
	public List<ArmyHero> getNewArmyHeroList(String guid)
	{
		List<ArmyHero> hes = newArmyHero.get(guid);
		return hes;
	}
	public void clearNewArmyHero(String guid){
		if(newArmyHero.containsKey(guid)){
			newArmyHero.remove(guid);
		}
	}
	
	public synchronized ObjectNode getGMInfo() {
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		info.put("name", name);
		info.put("announce", announce);
		info.put("intro", intro);
		info.put("level", level);
		info.put("exp", exp);
		info.put("wealth", wealth);
		info.put("createTime", createTime);
		info.put("commanderName", allPlayersCache.getPlayerInfo(commanderId).getName());
		ArrayNode mbrs = DynamicJsonProperty.jackson.createArrayNode();
		for (TeamMember mbr : members.values()) {
			ObjectNode mbrInfo = DynamicJsonProperty.jackson.createObjectNode();
			mbrInfo.put("name", allPlayersCache.getPlayerInfo(mbr.getGuid()).getName());
			mbrInfo.put("title", mbr.getTitle().ordinal());
			mbrInfo.put("contribute", mbr.getContribute());
			mbrs.add(mbrInfo);
		}
		info.put("members", mbrs);
		return info;
	}
	
	private boolean existMember(String id) {
		return members.containsKey(id);
	}
	
	/**
	 * 强制设置军团长
	 * @param name
	 * @param guid
	 */
	public synchronized boolean setTeamCommander(String guid) {
		TeamMember member = members.get(guid);
		if(member==null){
			return false;
		}
		title2CountMap.put(member.getTitle(), title2CountMap.get(member.getTitle()) + 1);
		member.setTitle(TeamMemberTitle.COMMANDER);
		return true;
	}
	
	public Map<String, TeamMember> getMembers() {
		return members;
	}
	
	public int getLevel() {
		return level;
	}
	
	private boolean shidisOpen(Integer strongHoldid){
		for(int ap:AllGameConfig.getInstance().teams.getGuild(this.level).getArmyposition()){
			if(ap==strongHoldid){
				return true;
			}
		}
		return false;
	}
	
	
	public List<Integer> getStrongHoldWinAndLoseNum(){
		int[] armyposition = AllGameConfig.getInstance().teams.getGuild(this.level).getArmyposition();
		List<Integer> list = Lists.newArrayList();
		int winNum = 0;
		for(Integer id:armyposition){
			if(strongHoldsWar.get(id-1).getSign() == 0){
				winNum ++;
			}
		}
		list.add(winNum);
		list.add(armyposition.length-winNum);
		return list;
	}
	/**
	 * @return
	 * 查询军团战相关信息
	 */
	public synchronized ObjectNode getTeamWarInfo(){
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		info.put("teamName", name);
		info.put("warScore", warScore);
		
		return info;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////    //////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	public int getWinNum() {
		return winNum;
	}

	public void setWinNum(Integer winNum) {
		this.winNum = winNum;
	}

	public int getWarScore() {
		return warScore;
	}

	public void setWarScore(Integer warScore) {
		this.warScore = warScore;
	}

	public Integer getWarStage() {
		return warStage;
	}

	public void setWarStage(Integer warStage) {
		this.warStage = warStage;
	}

	public boolean isWarOpened() {
		return warOpened;
	}

	public void setWarOpened(boolean isOpened) {
		this.warOpened = isOpened;
	}
	
	public String getTeamWarTeamName() {
		return teamWarTeamName;
	}
	
	// 初始化军团战数据
	public void initWarData()
	{
		try {
			this.warOpened = false;
			this.teamWarTeamName = "";
			this.winNum = 0;
			this.warScore = 0;
			this.warStage = 0;
			this.strongHoldsWar.clear();
			this.warMemberMap.clear();
			
			for (Map.Entry<String, List<ArmyHero>> idhes : newArmyHero.entrySet()) 
			{
				List<ArmyHero> mlist = idhes.getValue();
				for(int i=0;i<mlist.size();i++)
				{
					ArmyHero amyhero = mlist.get(i);
					amyhero.setStrongID(-1);
					amyhero.setheroWhere(-1);
					amyhero.setHps(1);
				}
			}
			
			for(int i=0;i<armyStrongHolds.size();i++)
			{
				ArmyStrongHold hold = armyStrongHolds.get(i);
				List<ArmyHoldHeroIdx> idxs = hold.getStrongHoldRoles();
				
				List<ArmyHoldHeroIdx> tidx = Lists.newArrayList();
				for(int k=0;k<idxs.size();k++)
				{
					ArmyHoldHeroIdx idx = idxs.get(k);
					String guid = idx.getGuid();
					Integer id = idx.getIdx();
					List<ArmyHero> listArmyHeros = newArmyHero.get(guid);
					if(listArmyHeros != null && listArmyHeros.size() > id){
						ArmyHero mhero = listArmyHeros.get(id);
						if(mhero != null){
							mhero.setStrongID(i);
							tidx.add(idx);
						}
					}else{
//						logger.error("[Team {} initWarData] StrongHoldRole guid={},id={},newArmyHero maxId={}",name,guid,id,listArmyHeros.size());
					}
				}
				hold.setStrongHoldRoles(tidx);
			}
		} catch (Exception e) {
			logger.error("{}",e);
		}
	}
	
	public synchronized void addWarWinReward(){  // 战斗结束，战斗胜利
		
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		
		int overwincontribution = guildWar.getOverwincontribution().intValue();
		int winmoney = guildWar.getWinmoney().intValue();
		
		for(String name:members.keySet())
		{
			if(members.get(name) != null){
				members.get(name).addContribute(overwincontribution);
			}
		}
		
		this.sendMailToAllMemberTeamWar(name, 
				TeamConstants.TEAM_WAR_WIN, 
				MessageCode.TEAM_WAR_WIN_TITLE, 
				MessageCode.TEAM_WAR_WIN_CONTENT, 
				new String[]{teamWarTeamName,overwincontribution +"",winmoney + ""});
		
		int maxWealth = AllGameConfig.getInstance().teams.getMaxWealth(level);
		int xkk = wealth + winmoney;
		if(xkk>maxWealth){
			xkk = maxWealth;
		}
		wealth = xkk;
		
		this.teamWinAwardRankFunc();
		
		this.setWarcount();
//		
		logger.info("version="+AllGameConfig.getInstance().maxVersion+","+"modules_func=teamReward"+","+"wealth="+winmoney);
	}
	
	public synchronized void warLoseRewards() // 军团战结束，战斗失败
	{
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		int overlosecontribution = guildWar.getOverlosecontribution().intValue();
		VariousItemEntry[] loserewarditem = guildWar.getLoserewarditem();
		
		for(String name:members.keySet())
		{
			if(members.get(name) != null){
				members.get(name).addContribute(overlosecontribution);
			}
		}
		
		this.sendMailToAllMemberTeamWar(name, 
				TeamConstants.TEAM_WAR_LOSE, 
				MessageCode.TEAM_WAR_LOSE_TITLE, 
				MessageCode.TEAM_WAR_LOSE_CONTENT, 
				new String[]{teamWarTeamName,overlosecontribution+""});
		
		this.teamLostAwardRankFunc();
		
//		List<ObjectNode> milist = Lists.newArrayList();
//		
//		for (Map.Entry<String, WarMember> mmm : warMemberMap.entrySet()) 
//		{
//			WarMember mk = mmm.getValue();
//			if(mk.getScore() == 0){
//				continue;
//			}
//			ObjectNode ml = DynamicJsonProperty.jackson.createObjectNode();
//			ml.put("guid", mmm.getKey());
//			ml.put("nvalue", mk.getScore());
//			ml.put("time", mk.getTimes());
//			milist.add(ml);
//		}
//		
//		Collections.sort(milist, new Comparator<ObjectNode>() {
//			@Override
//			public int compare(ObjectNode lhs,ObjectNode rhs){
//				int powDiff = lhs.get("nvalue").asInt() - rhs.get("nvalue").asInt();
//				if (powDiff < 0) {
//					return 1;
//				} else if (powDiff > 0) {
//					return -1;
//				}
//				return 0;
//			}
//		});
//		
//		List<ObjectNode> mi = Lists.newArrayList();
//		List<ObjectNode> mki = Lists.newArrayList();
//		if(milist.size()<=3){
//			mi = milist;
//		}else{
//			ObjectNode oo = milist.get(2);
//			int kvalue = oo.get("nvalue").asInt();
//			for(int km =milist.size()-1;km>=0;km--)
//			{
//				ObjectNode obj = milist.get(km);
//				int mvalue = obj.get("nvalue").asInt();
//				if(mvalue > kvalue){
//					mi.add(obj);
//				}else if(mvalue == kvalue){
//					mki.add(obj);
//				}
//			}
//		}
//		
//		/////
//		Collections.sort(mki, new Comparator<ObjectNode>() {
//			@Override
//			public int compare(ObjectNode lhs,ObjectNode rhs){
//				int powDiff = lhs.get("nvalue").asInt() - rhs.get("nvalue").asInt();
//				if (powDiff < 0) {
//					return 1;
//				} else if (powDiff > 0) {
//					return -1;
//				}
//				return 0;
//			}
//		});
//		
//		if(mki.size() > 0){
//			mi.add(mki.get(mki.size()-1));
//		}
//		
//		Collections.sort(mi, new Comparator<ObjectNode>() {
//			@Override
//			public int compare(ObjectNode lhs,ObjectNode rhs){
//				int powDiff = lhs.get("time").asInt() - rhs.get("time").asInt();
//				if (powDiff < 0) {
//					return 1;
//				} else if (powDiff > 0) {
//					return -1;
//				}
//				return 0;
//			}
//		});
//		
//		for(int n = 0;n<mi.size();n++)
//		{
//			ObjectNode objectNode = mi.get(n);
//			String guid = objectNode.get("guid").asText();
//			if(n >= loserewarditem.length-1){
//				sendMailToGuidAwards(guid,TeamConstants.TEAM_WAR_END_RANK_MAIL,MessageCode.WAR_TIPS_6271,MessageCode.WAR_TIPS_6272,loserewarditem[loserewarditem.length-1],n+1);
//			}else{
//				sendMailToGuidAwards(guid,TeamConstants.TEAM_WAR_END_RANK_MAIL,MessageCode.WAR_TIPS_6271,MessageCode.WAR_TIPS_6272,loserewarditem[n],n+1);
//			}
//		}
	}
	
	// 胜利排名奖励
		public void teamWinAwardRankFunc()
		{
			GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
			VariousItemEntry[] winrewarditem = guildWar.getWinrewarditem();
			
			if(winrewarditem.length==0){
				return;
			}
			
			List<WarMember> mlist =  this.getRankWarMemberScore();
			for(int n=0;n<mlist.size();n++){
				WarMember mm = mlist.get(n);
				String guid = mm.getGuid();
				if(n>=winrewarditem.length-1){
					sendMailToGuidAwards(guid,TeamConstants.TEAM_WAR_END_RANK_MAIL,MessageCode.WAR_TIPS_6271,MessageCode.WAR_TIPS_6272,winrewarditem[winrewarditem.length-1],winrewarditem.length);
				}else{
					sendMailToGuidAwards(guid,TeamConstants.TEAM_WAR_END_RANK_MAIL,MessageCode.WAR_TIPS_6271,MessageCode.WAR_TIPS_6272,winrewarditem[n],n+1);
				}
			}
		}
		
		// 失败排名奖励
		public void teamLostAwardRankFunc()
		{
			GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
			VariousItemEntry[] loserewarditem = guildWar.getLoserewarditem();
			
			if(loserewarditem.length==0){
				return;
			}
			
			List<WarMember> mlist =  this.getRankWarMemberScore();
			for(int n=0;n<mlist.size();n++){
				WarMember mm = mlist.get(n);
				String guid = mm.getGuid();
				if(n>=loserewarditem.length-1){
					sendMailToGuidAwards(guid,TeamConstants.TEAM_WAR_END_RANK_MAIL,MessageCode.WAR_TIPS_6271,MessageCode.WAR_TIPS_6272,loserewarditem[loserewarditem.length-1],loserewarditem.length);
				}else{
					sendMailToGuidAwards(guid,TeamConstants.TEAM_WAR_END_RANK_MAIL,MessageCode.WAR_TIPS_6271,MessageCode.WAR_TIPS_6272,loserewarditem[n],n+1);
				}
			}
		}
	
	
	/// 和平握手
	public void teamPeaceFunc()
	{
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		VariousItemEntry[] drawrewarditem = guildWar.getDrawrewarditem();
		
		if(drawrewarditem.length==0){
			return;
		}
		
		List<WarMember> mlist =  this.getRankWarMemberScore();
		for(int n=0;n<mlist.size();n++){
			WarMember mm = mlist.get(n);
			String guid = mm.getGuid();
			if(n>=drawrewarditem.length-1){
				sendMailToGuidAwards(guid,
						TeamConstants.TEAM_WAR_END_RANK_MAIL,
						MessageCode.MESSAGE_7095,
						MessageCode.MESSAGE_7096,
						drawrewarditem[drawrewarditem.length-1],drawrewarditem.length);
			}else{
				sendMailToGuidAwards(guid,
						TeamConstants.TEAM_WAR_END_RANK_MAIL,
						MessageCode.MESSAGE_7095,
						MessageCode.MESSAGE_7096,
						drawrewarditem[n],n+1);
			}
		}
	}

	public synchronized void setTeamWarTeamName(String teamName) {
		if(teamName.equals("")){
			return;
		}
		this.teamWarTeamName = teamName;
		this.warOpened = true;
	}
	
	/// 从军团战中撤出军团成员
	public synchronized void cancelMemberRolesFromWar() {
		warOpened = false;
		
		this.initWarData();
	}
	
	public ArrayNode getTeamWarHoldsInfo()
	{
		ArrayNode arrayNode = DynamicJsonProperty.jackson.createArrayNode();
		
		for(int i=0;i<strongHoldsWar.size();i++){
			WarHoldBase oe = strongHoldsWar.get(i);
			arrayNode.add(oe.toJson());
		}
		
		return arrayNode;
	}
	
//	public void setOneWarState(boolean s)
//	{
//		isOneWar = true;
//	}
	
	// 初始化，军团战开启后，初始化数据使用军团部队的数据
	public void initTeamWarInfoBase()
	{
		this.strongHoldsWar = Lists.newArrayList();
		
		for(int j = 0;j<21;j++){
			WarHoldBase o = new WarHoldBase();
			o.setID(j);
			this.strongHoldsWar.add(o);
		}
		
		// 复制军团部队中的据点信息
		if(armyStrongHolds.isEmpty()){
			return;
		}
		
		for(int i=0;i<armyStrongHolds.size();i++)
		{
			ArmyStrongHold hold =  armyStrongHolds.get(i);
			WarHoldBase oe = strongHoldsWar.get(i);
			oe.getBaseHero().clear();
			
			List<ArmyHoldHeroIdx> strongHoldRoles = hold.getStrongHoldRoles();
			
			for(int k =0;k<strongHoldRoles.size();k++)
			{
				ArmyHoldHeroIdx ko = strongHoldRoles.get(k);
				WarBaseHeroOne one = new WarBaseHeroOne();
				RoleEntity roles = allPlayersCache.getPlayerInfo(ko.getGuid());
				String name = roles.getName();
				String teamName =  roles.getTeamName();
				
				List<ArmyHero> lo = newArmyHero.get(ko.getGuid());
				if(lo == null || lo.size()<=ko.getIdx()){
					continue;
				}
				ArmyHero mhero = lo.get(ko.getIdx());
				mhero.setheroWhere(TeamConstants.WAR_HOLD_STATE_0);
				Hero hero = mhero.getHero();
				
				String modelID = hero.getRoleId() + "";
				one.setDatas(ko.getGuid(), name, teamName, modelID,mhero.getHps(),ko.getIdx());
				oe.addBaseHero(one);
			}
			
			oe.setSign(TeamConstants.WAR_BASE_STATE_A);
		}
	}
	
	public ArmyHero getArmyHeroByIndex(String guid,Integer index)
	{
		if(newArmyHero.get(guid) == null){
			return null;
		}
		if(newArmyHero.get(guid).get(index) == null){
			return null;
		}
		return newArmyHero.get(guid).get(index);
	}
	
	public WarHoldBase getWarHoldBases(Integer index)
	{
		return this.strongHoldsWar.get(index);
	}
	
	public List<WarHoldBase> getWarHoldBaseList()
	{
		return this.strongHoldsWar;
	}
	
	//0是忍者列表，1是索引列表  战斗结束之后更新血量和索引状态
	public void updateWarBaseHeroDataAttack(Player player,Integer index,float hps,boolean win,Integer strongHoldId,Team warTeam,Integer warType,int idx)
	{
		ArmyHero mhero = newArmyHero.get(player.getId()).get(index);
		mhero.setHps(hps);
		if(win){
			WarHoldBase base = null;
			if(warType == 0){
				base = warTeam.getWarHoldBases(strongHoldId);
			}else{
				base = this.getWarHoldBases(strongHoldId);
			}
			
			if(idx == 0){
				base.clearBaseList(warTeam);
				base.getBaseHero().clear();
			}
			if(hps>0){
				mhero.setStrongID(strongHoldId);
				if(warType == 0){
					base.setSign(1);
				}
				WarBaseHeroOne one = new WarBaseHeroOne();
				one.setDatas(player.getId(), player.getName(), this.getName(), mhero.getHero().getRoleId()+"",hps,index);
				base.addBaseHero(one);
				if(warType == 0){
					mhero.setheroWhere(1);
				}else{
					mhero.setheroWhere(0);
				}
			}else{
				mhero.setStrongID(-1);
				mhero.setheroWhere(-1);
			}
		}else{
			mhero.setStrongID(-1);
			mhero.setheroWhere(-1);
		}
	}
	
	public void updateWarBaseHeroDataDefend(Integer index,float hps,boolean win,Integer strongHoldId,String guid,Team warTeam,Integer warType)
	{
		ArmyHero mhero = newArmyHero.get(guid).get(index);
		mhero.setHps(hps);
		
		if(warType == 0){
			if(!win){
				WarHoldBase base = strongHoldsWar.get(strongHoldId);
				List<WarBaseHeroOne> list = base.getBaseHero();
				if(hps>0){
					for (WarBaseHeroOne warBaseHeroOne : list) {
						if(warBaseHeroOne.getWarIndex() == index){
							warBaseHeroOne.setHps(hps);
							break;
						}
					}
				}else{
					for(int i=0;i<list.size();i++)
					{
						WarBaseHeroOne warBaseHeroOne = list.get(i);
						if(warBaseHeroOne.getWarIndex() == index){
							list.remove(i);
							mhero.setStrongID(-1);
							mhero.setheroWhere(-1);
							break;
						}
					}
				}
			}else{
				mhero.setStrongID(-1);
				mhero.setheroWhere(-1);
			}
			
		}else{
			if(!win){
				WarHoldBase base = warTeam.getWarHoldBaseList().get(strongHoldId);
				List<WarBaseHeroOne> list = base.getBaseHero();
				if(hps>0){
					for (WarBaseHeroOne warBaseHeroOne : list) {
						if(warBaseHeroOne.getWarIndex() == index){
							warBaseHeroOne.setHps(hps);
							break;
						}
					}
				}else{
					for(int i=0;i<list.size();i++)
					{
						WarBaseHeroOne warBaseHeroOne = list.get(i);
						if(warBaseHeroOne.getWarIndex() == index){
							list.remove(i);
							mhero.setStrongID(-1);
							mhero.setheroWhere(-1);
							break;
						}
					}
				}
				
			}else{
				mhero.setStrongID(-1);
				mhero.setheroWhere(-1);
			}
		}
	}
	
	public void updateDefendPlayerState(Player player,String guid,Integer index)
	{
		List<ArmyHero>  mlist = newArmyHero.get(guid);
		if(mlist != null){
			ArmyHero ho = mlist.get(index);
			if(ho != null){
				ho.setStrongID(-1);
			}
		}
	}
	
	/**
	 * 增加据点进攻胜利奖励
	 * @param player
	 * @param rewardList
	 */
	public void addWarAttReward(Player player){
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		
		int attackpoint = guildWar.getAttackpoint().intValue();
		int oncewincontribution = guildWar.getOncewincontribution().intValue();
		
		winNum += 1;
		warScore += attackpoint;
		TeamMember mm = members.get(player.getId());
		if(mm != null){
			mm.addContribute(oncewincontribution);
		}
		
		setPlayerWarScores(player.getId(),attackpoint);
	}
	
	public int getWinAddwardScore()
	{
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		int attackpoint = guildWar.getAttackpoint().intValue();
		return attackpoint;
	}
	public int getLostWardScore()
	{
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		int point = guildWar.getDefendpoint().intValue();
		return point;
	}
	// 占领空据点得分
	public int addDefendEmptySlotRewards(Player player)
	{
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		int emptypoint = guildWar.getStandpoint().intValue();
		warScore += emptypoint;
		
		setPlayerWarScores(player.getId(),emptypoint);
		return emptypoint;
	}
	
	private void setPlayerWarScores(String guid,int score)
	{
		WarMember m = warMemberMap.get(guid);
		if(m == null){
			m = new WarMember();
			m.setGuid(guid);
			m.setScore(score);
		}else{
			m.setScore(score);
		}
		warMemberMap.put(guid, m);
	}
	
	//增加据点防御胜利奖励
	public void addWarDefendRewardWars(Map<String, String> guidmap){
		
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		
		int defendpoint = guildWar.getDefendpoint().intValue();
		int oncelosecontribution = guildWar.getOncelosecontribution().intValue();
		
		List<String> slist = Lists.newArrayList();
		for (String s :guidmap.values()) {
			slist.add(s);
		}
		
		for (String ms : slist) {
			TeamMember mm = members.get(ms);
			if(mm != null){
				mm.addContribute(oncelosecontribution);
				setPlayerWarScores(ms,defendpoint/slist.size());
			}
		}
		
		warScore += defendpoint;
	}
	
	// 我方空据点防守胜利
	public void addWarEmptyBaseDefendRewards(Player player){
		
		GuildWar guildWar = AllGameConfig.getInstance().teams.getGuildWarInfo(this.level);
		
		int defendpoint = guildWar.getDefendpoint().intValue();
		int oncewincontribution = guildWar.getOncewincontribution().intValue();
		
		warScore += defendpoint;
		members.get(player.getId()).addContribute(oncewincontribution);
	}
	
	// 判断当前据点是否是占领状态
	public Integer theBaseIsZhanLing(Integer strongHoldId)
	{
		Integer sign = strongHoldsWar.get(strongHoldId-1).getSign();
		return sign;
	}
	
	
	/**
	 * 判断前置据点是否通过
	 * @param strongHoldId
	 * @return
	 */
	public boolean theBaseCanFight(Integer strongHoldId){
		
		strongHoldId = strongHoldId + 1;
		
		if(strongHoldId>20){//判断13-20是否被击败过
			if(warStage>=2){
				return true;
			}else if(warStage==1){
				int[] armyposition = AllGameConfig.getInstance().teams.getGuild(this.level).getArmyposition();
				for(int id:armyposition){
					if(id>13 && id<=20 && strongHoldsWar.get(id-1).getSign()!=1){
						return false;
					}
				}
				warStage = 2;
				return true;
			}else{
				return false;
			}
		}else if(strongHoldId>12){//判断1-12是否被击败过
			if(warStage>=1){
				return true;
			}else{
				int[] armyposition = AllGameConfig.getInstance().teams.getGuild(this.level).getArmyposition();
				for(int id:armyposition){
					if(id<=12 && strongHoldsWar.get(id-1).getSign()!=1){
						return false;
					}
				}
				warStage = 1;
				return true;
			}
		}else{
			return true;
		}
	}
	
	////------------------------------  end 军团战 ------------------------------------------------
	
	
	///////////////////////// 军团部队 新 -----------------------------------------
	
	// 每周日晚上12点清军团中成员的派遣次数
	public void clearTeamMemberPaiCishu()
	{
		for(TeamMember mm : members.values()){
			mm.setPci(0);
		}
		
		for (Map.Entry<String, List<ArmyHero>> idhes : newArmyHero.entrySet()) 
		{
			List<ArmyHero> mlist = idhes.getValue();
			for(int i=0;i<mlist.size();i++)
			{
				ArmyHero amyhero = mlist.get(i);
				amyhero.setCurrentTime();
			}
		}
	}
	
	/// 修复线上的，军团成员的权限
//	public void fixTeamMemberTitles()
//	{
//		for (TeamMember mm : members.values()) {
//			if(mm.getTitle().ordinal() > TeamMemberTitle.SERGEANT.ordinal())
//			{
//				mm.setTitle(TeamMemberTitle.SERGEANT);
//			}
//		}
//	}
	
	private ObjectNode  getAllArmyHeros(Player player,int cp,int type)
	{
		ObjectNode armyInfo = DynamicJsonProperty.jackson.createObjectNode();
		ArrayNode heros = DynamicJsonProperty.jackson.createArrayNode();
		
		int playerSendHeroNums = 0;
		int totalHerosNums = 0;
		for (Map.Entry<String, List<ArmyHero>> idhes : newArmyHero.entrySet()) 
		{
			String id = idhes.getKey();
			String playername = allPlayersCache.getPlayerInfo(idhes.getKey()).getName();
			List<ArmyHero> mlist = idhes.getValue();
			
			if(id.equals(player.getId())){
				playerSendHeroNums = mlist.size();
			}
			
			for(int i=0;i<mlist.size();i++)
			{
				ArmyHero amyhero = mlist.get(i);
				Hero hero = amyhero.getHero();
				ObjectNode obj = hero.toJson();
				obj.put("heroArmyID", i);
				obj.put("strongHoldIDX", amyhero.getStrongID());
				obj.put("nid", id);
				obj.put("playername", playername);
				heros.add(obj);
			}
		}
		
		totalHerosNums = heros.size();
		
		int maxlen = 8;
		
		int maxpage = heros.size()%maxlen == 0? heros.size()/maxlen : (int)(heros.size()/maxlen) + 1;
		if(maxpage == 0){
			maxpage = 1;
		}
		
		if(cp>maxpage){
			cp = maxpage;
		}
		
		ArrayNode hl = DynamicJsonProperty.jackson.createArrayNode();
		
		int k = (cp-1)*maxlen;
		int m ;
		if(maxpage== cp){
			m = heros.size();
		}else {
			m = maxlen;
		}
		
		for(int i =k ;i<k+m;i++)
		{
			hl.add(heros.get(i));
		}
		
		armyInfo.put("armyRoles", hl); // 部队
		armyInfo.put("maxPage", maxpage);
		armyInfo.put("playerSendHeroNums", playerSendHeroNums);
		armyInfo.put("currentPage", cp);
		armyInfo.put("totalHerosNums", totalHerosNums);
		return armyInfo;
	}
	
	private ObjectNode getAllMemberArmy(Player player,int cp,int type)
	{
		ObjectNode armyInfo = DynamicJsonProperty.jackson.createObjectNode();
		ArrayNode heros = DynamicJsonProperty.jackson.createArrayNode();
		
		for (Map.Entry<String, List<ArmyHero>> idhes : newArmyHero.entrySet()) 
		{
			String id = idhes.getKey();
			String playername = allPlayersCache.getPlayerInfo(idhes.getKey()).getName();
			List<ArmyHero> mlist = idhes.getValue();
			
			for(int i=0;i<mlist.size();i++)
			{
				ArmyHero amyhero = mlist.get(i);
				Hero hero = amyhero.getHero();
				ObjectNode obj = hero.toJson();
				obj.put("heroArmyID", i);
				obj.put("strongHoldIDX", amyhero.getStrongID());
				obj.put("nid", id);
				obj.put("playername", playername);
				heros.add(obj);
			}
		}
		
		armyInfo.put("armyRoles", heros); // 部队
		return armyInfo;
	}
	
	private ObjectNode  getPlayerArmyHeros(Player player,int cp,int type) 
	{
		ObjectNode armyInfo = DynamicJsonProperty.jackson.createObjectNode();
		ArrayNode heros = DynamicJsonProperty.jackson.createArrayNode();
		
		List<ArmyHero> mlist = newArmyHero.get(player.getId());
		if(mlist != null){
			for(int i=0;i<mlist.size();i++)
			{
				ArmyHero amyhero = mlist.get(i);
				Hero hero = amyhero.getHero();
				ObjectNode obj = hero.toJson();
				obj.put("heroArmyID", i);
				obj.put("strongHoldIDX", amyhero.getStrongID());
				obj.put("nid", player.getId());
				obj.put("playername", player.getName());
				heros.add(obj);
			}
		}
		
		armyInfo.put("myArmyHeros", heros);
		return armyInfo;
	}
	
	public synchronized ObjectNode getArmy(Player player,int cp,int type) {
		ObjectNode armyInfo = DynamicJsonProperty.jackson.createObjectNode();
		if(type == 1){
			armyInfo =  getPlayerArmyHeros(player,cp,type);
		}else if(type == 0){
			armyInfo =  getAllArmyHeros(player,cp,type);
		}else{
			armyInfo = getAllMemberArmy(player,cp,type);
		}
		return armyInfo;
	}
	
	/**
	 * 派出忍者到部队中
	 * @param heros 派出忍者背包ID
	 **/
	public synchronized boolean sendRoleToArmy(Player player, List<Integer> hids) {
		
		List<MyHero> heros = player.getHeros().getHeros(hids);
		// 检查玩家派出忍者数是否已达上限
		if (!newArmyHero.containsKey(player.getId())) {
			newArmyHero.put(player.getId(), new ArrayList<ArmyHero>());
		}

		List<ArmyHero> list = newArmyHero.get(player.getId());

		int maxArmyRoles = AllGameConfig.getInstance().teams.getMaxArmy(this.level);

		if(list.size() >= maxArmyRoles){
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_CANNOT_MORE_ARMY);
			return false;
		}
		
		if(list.size()+heros.size() > maxArmyRoles){
			// 当部队无法容纳派遣的全部忍者时,优先选取战斗力高的忍者
			int ignoreRoles = heros.size() - (list.size()+heros.size()-maxArmyRoles);
			Collections.sort(heros, new Comparator<MyHero>() {
				@Override
				public int compare(MyHero lhs,MyHero rhs){
					int powDiff = lhs.hero.getAllPower() - rhs.hero.getAllPower();
					if (powDiff < 0) {
						return 1;
					} else if (powDiff > 0) {
						return -1;
					}
					return 0;
				}
			});

			for(int i=heros.size()-1;i>=ignoreRoles;--i){
				heros.remove(i);
			}
		}
		
		// 检查如果有派遣忍者在温泉中,那么强制将该忍者从温泉中移除并且放弃所有温泉收益
//		for (int id : hids) {
//			if (player.getPractise().inPractise(id)) {
//				player.getPractise().setPractiseHeroId(-1);
//				break;
//			}
//		}
		
		List<ArmyHero> mlist = newArmyHero.get(player.getId());
		for(int mk = 0;mk<heros.size();mk++){
			Hero hero = heros.get(mk).hero;
			ArmyHero mhero = new ArmyHero(hero);
			mhero.setHid(heros.get(mk).id);
			mlist.add(mhero);
			logger.info("version="+AllGameConfig.getInstance().maxVersion+","+"modules_func=sendRoleToArmy"+","+"guid="+player.getId()+","+"roleName="+player.getName()+","+"level="+player.getRole().getLevel()+","+"heroId="+hero.getRoleId()+","+"heroLv="+hero.getLevel());
			
			int idx = heros.get(mk).id;
			player.getHeros().addHeroState(idx, HeroState.TEAM_ARMY_STATE);
		}
		
		player.sendResult(OperResultType.TEAM, MessageCode.TEAM_SEND_ROLES_TO_ARMY_COMPLETE);
		
		return true;
	}
	
	/**
	 * 从部队中撤回派出的忍者
	 * @param membersID - 
	 * @param cancelRolesIndex - 被撤回忍者在部队中的索引
	 **/
	public synchronized boolean cancelRolesFromArmy(Player player, Map<String, List<Integer>> cancelRolesMemberID2Index) {
		if (warOpened) { // 军团战期间不可从部队撤回忍者
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_WAR_CANNOT_CANCELROLES_FROM_ARMY);
			return false;
		}

		if (!existMember(player.getId())) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_TEAM_MEMBER);
			return false;
		}

		// 检查权限, 只有军团长与军团长可以撤回除自己以外其它团员派出到部队的忍者
		TeamMember mbr = members.get(player.getId());
		if (mbr.getTitle().ordinal() > TeamMemberTitle.DEPUTY_COMMANDER.ordinal()) {
			if (cancelRolesMemberID2Index.size() > 1 || 
					!cancelRolesMemberID2Index.containsKey(player.getId())) {
				player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
				return false;
			}
		}


		for (Map.Entry<String, List<Integer>> e : cancelRolesMemberID2Index.entrySet()) {
			cancelMemberRolesFromArmyFunc(player, e.getKey(), e.getValue());
		}
		player.sendResult(OperResultType.TEAM, MessageCode.TEAM_CANCEL_ROLES_FROM_ARMY_COMPLETE);
		return true;
	}
	
	/// 据点，忍者列表
	/// 撤回军团部队中的忍者
	private void cancelMemberRolesFromArmyFunc(Player player, String memberId, List<Integer> cancelRolesIdx) 
	{
		List<ArmyHero> sendOfAllHeros = newArmyHero.get(memberId);
		
		if (sendOfAllHeros == null || sendOfAllHeros.isEmpty()) {
			return;
		}

		List<Integer> needCancelHeros = Lists.newArrayList();

		List<ArmyHero> remainHeros = Lists.newArrayList();
		
		int kc = 0;
		for(int i = 0;i<sendOfAllHeros.size();i++)
		{
			ArmyHero mhero = sendOfAllHeros.get(i);
//			Hero hero = mhero.getHero();
			int strongID = mhero.getStrongID().intValue();
			if(cancelRolesIdx.contains(i)){
				// 清空在据点中的英雄据点信息
				if(strongID > -1){
					if(armyStrongHolds.size()>strongID){
						armyStrongHolds.get(strongID).removeStrongHoldRoles(memberId,i);
					}
					mhero.setStrongID(-1);
				}
				needCancelHeros.add(mhero.getHid());
				
			}else{
				if(strongID > -1){
					if(armyStrongHolds.size()>strongID){
						armyStrongHolds.get(strongID).setStrongHoldIDX(memberId, i, kc);
					}
				}
				remainHeros.add(mhero);
				kc = kc+1;
			}
		}
		
		newArmyHero.put(memberId, remainHeros);
		
		// 将撤回的忍者存入玩家忍者背包
		backHeroToPackage(player,memberId,needCancelHeros);
		
//		for (Hero hero : needCancelHeros) {
//			logger.info("version="+AllGameConfig.getInstance().maxVersion+","+"modules_func=cancelMemberRolesFromArmyFunc"+","+"guid="+player.getId()+","+"roleName="+player.getName()+","+"level="+player.getRole().getLevel()+","+"heroId="+hero.getRoleId()+","+"heroLv="+hero.getLevel());
//		}
	}
	
	private void backHeroToPackage(Player player,String guid,List<Integer> heros)
	{
		if(heros.size() == 0){
			return;
		}
		PlayerSession ps = room.getSessions().get(guid);
		if(ps != null){ // 在线恢复
			ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
			source.put("reason", TeamConstants.TEAM_CANCEL_ARMY_ROLE);
			source.put("roles", DynamicJsonProperty.convertToArrayNode(heros));
			if(!player.getId().equals(guid)){
				source.put("name", player.getName());
			}
			ps.onEvent(Events.event("teamNotify", source));
		}else{ // 离线恢复
			// @WARN 如果玩家离线,先将撤回的忍者临时保存等待玩家上线后归还
			// ObjectCacheService的机制无法正确的处理离线玩家,因为无法保证操作中间这名玩家是否会进入游戏, 
			// 如果进入游戏那么此时对ObjectCacheService操作很可能导致撤回的忍者无法回到玩家忍者背包内
			if (cancelHerosFromArmyNew.containsKey(guid)) {
				cancelHerosFromArmyNew.get(guid).addAll(heros);
			} else {
				cancelHerosFromArmyNew.put(guid, heros);
			}
			DefaultPlayer dp = objectCacheService.getCache(guid, DefaultPlayer.class);
			if (dp != null) {
				dp.getMail().addSysMail(
						MessageCode.TEAM_ARMY_CANCELHERO_TITLE, 
						MessageCode.TEAM_ARMY_CANCELHERO_CONTENT,
						player.getName());
				objectCacheService.putObject(dp);
				dp = null;
			}
		}
	}
	
	////------------------------------------------------
	/**
	 * 撤回给定玩家派出到军团部队内的忍者
	 * 
	 * @param memberId - 玩家GUID
	 * @param cancelRolesIdx - 撤回忍者在部队索引
	 **/
	private void cancelMemberRolesFromArmy(Player player, String memberId, List<Integer> cancelRolesIdx) {
		List<ArmyHero> sendOfAllHeros = newArmyHero.get(memberId);
		
		if (sendOfAllHeros == null || sendOfAllHeros.isEmpty()) {
			return;
		}
		
		List<Integer> needCancelHeros = Lists.newArrayList();
		for(int i = 0;i<sendOfAllHeros.size();i++)
		{
			ArmyHero mhero = sendOfAllHeros.get(i);
			int strongID = mhero.getStrongID().intValue();
			if(strongID > -1){
				armyStrongHolds.get(strongID).removeStrongHoldRoles(memberId,i);
				mhero.setStrongID(-1);
			}
			needCancelHeros.add(mhero.getHid());
		}
		
		newArmyHero.remove(memberId);
		
		backHeroToPackage(player,memberId,needCancelHeros);
	}
	
	public ArrayNode getWarHeroIndexList(Player player)
	{
		ArrayNode arr = DynamicJsonProperty.jackson.createArrayNode();
		List<ArmyHero> mlist = newArmyHero.get(player.getId());
		if(mlist != null && !mlist.isEmpty()){
			for(int i = 0;i<mlist.size();i++){
				ArmyHero mhero = mlist.get(i);
				Hero hero = mhero.getHero();
				ObjectNode obj = hero.toJson();
				obj.put("heroArmyID", i);
				obj.put("strongHoldIDX", mhero.getStrongID());
				obj.put("hps", mhero.getHps());
				arr.add(obj);
			}
		}
		return arr;
	}
	
	///////////////////////////////------------------------- 军团据点 ---------------------------------------------------------------------
	
	/**
	 * 获取军团部队据点信息
	 * @param player
	 * @return
	 */
	public ArrayNode getStrongHoldsInfo(Player player){
		ArrayNode arrayNode = DynamicJsonProperty.jackson.createArrayNode();
		
		if(armyStrongHolds.isEmpty()){
			for(int hd = 0;hd<21;hd++){
				this.armyStrongHolds.add(new ArmyStrongHold(hd));
			}
		}
		
		for(int i = 0;i<armyStrongHolds.size();i++){
			ArmyStrongHold hold = armyStrongHolds.get(i);
			ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
			node.put("strongHold", hold.toJson(this));
			arrayNode.add(node);
		}
		
		return arrayNode;
	}
	
	public Hero getAmryHeroIDByidx(String guid,int id)
	{
		Hero hero = newArmyHero.get(guid).get(id).getHero();
		return hero;
	}
	
	/**
	 * 派出部队忍者到据点中
	 * @param heros 派出忍者部队ID
	 **/
	public synchronized boolean sendRoleToStrongHold(Player player,Integer strongHoldid,Integer shIdx,String guid,Integer heroIdx) {
		//判断权限
		if(members.get(player.getId()).getTitle().ordinal() > TeamMemberTitle.STAFF_SERGEANT.ordinal()){
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return false;
		}
		//判断据点是否开放
		if(!shidisOpen(strongHoldid+1)){//据点未开放
			player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
			return false;
		}
		
		ArmyStrongHold armyhold = armyStrongHolds.get(strongHoldid); 
		List<ArmyHoldHeroIdx> mlist = armyhold.getStrongHoldRoles();
		
		if(shIdx > mlist.size()){
			player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
			return false;
		}
		
		// 容错处理
		List<ArmyHero> armylist = newArmyHero.get(guid);
		if(armylist == null || armylist.size() == 0){
			player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
			return false;
		}
		ArmyHero mheros = armylist.get(heroIdx);
		if(mheros == null){
			player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
			return false;
		}
		
		
		if(shIdx>=mlist.size()){
			armyhold.addStrongHoldRoles(new ArmyHoldHeroIdx(guid, heroIdx));
		}else{
			ArmyHoldHeroIdx idx = mlist.get(shIdx);
			newArmyHero.get(idx.getGuid()).get(idx.getIdx()).setStrongID(-1);
			armyhold.setStrongHoldRolesAndAdd(shIdx,new ArmyHoldHeroIdx(guid, heroIdx));
		}
		
		if(newArmyHero.get(guid).get(heroIdx).getStrongID() > -1){
			int strongID = newArmyHero.get(guid).get(heroIdx).getStrongID().intValue();
			ArmyStrongHold armyStrongHold = armyStrongHolds.get(strongID);
			List<ArmyHoldHeroIdx> ml = armyStrongHold.getStrongHoldRoles();
			for (ArmyHoldHeroIdx armyHoldHeroIdx : ml) {
				if(armyHoldHeroIdx.getGuid().equals(guid) && armyHoldHeroIdx.getIdx().intValue() == heroIdx){
					armyStrongHold.removeStrongHoldRolesByArmyHero(armyHoldHeroIdx);
					break;
				}
			}
		}
		
		newArmyHero.get(guid).get(heroIdx).setStrongID(strongHoldid);
		
		player.sendResult(OperResultType.TEAMSTRONGHOLD);
		return true;
	}
	
	/**
	 * 撤回据点中的部队忍者
	 * @param heroidx 据点忍者索引  
	 **/
	public synchronized boolean cancelRolesFromStrongHold(Player player,Integer strongHoldid,Integer idx) {
		//判断权限
		if(members.get(player.getId()).getTitle().ordinal() > TeamMemberTitle.STAFF_SERGEANT.ordinal()){
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return false;
		}
		//判断据点是否开放
		if(!shidisOpen(strongHoldid+1)){//据点未开放
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_RIGHTS_REFUSED);
			return false;
		}
		ArmyStrongHold armyhold = armyStrongHolds.get(strongHoldid); 
		List<ArmyHoldHeroIdx> mlist = armyhold.getStrongHoldRoles();
		if(mlist.size()<=idx){
			player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
			return false;
		}
		
		ArmyHoldHeroIdx hold = mlist.get(idx);
		newArmyHero.get(hold.getGuid()).get(hold.getIdx()).setStrongID(-1);
		armyhold.removeStrongHoldRolesByID(idx);
		
		player.sendResult(OperResultType.TEAMSTRONGHOLD);
		return true;
	}
	
	public int getWarMember(String guid)
	{
		WarMember war = warMemberMap.get(guid);
		if(war == null){
			return 0;
		}
		return war.getScore();
	}
	
	private List<WarMember> getRankWarMemberScore()
	{
		List<WarMember> milist = Lists.newArrayList();
		for(WarMember mm : warMemberMap.values())
		{
			if(mm.getScore()>0){
				milist.add(mm);
			}
		}
		
		Collections.sort(milist,new Comparator<WarMember>(){
			@Override
			public int compare(WarMember lhs,WarMember rhs){
				int powDiff = lhs.getScore() - rhs.getScore();
				if (powDiff < 0) {
					return 1;
				} else if (powDiff > 0) {
					return -1;
				}
				
				long pt = lhs.getTimes() - rhs.getTimes();
				if (pt < 0) {
					return 1;
				} else if (pt > 0) {
					return -1;
				}
				return 0;
			}
		});
		
		if(milist.size()<=3){
			return milist;
		}
		
		List<WarMember> milist2 = Lists.newArrayList();
		
		int threescore = milist.get(2).getScore();
		for(int i = 0;i<milist.size();i++){
			WarMember m = milist.get(i);
			if(m.getScore()>=threescore){
				milist2.add(m);
			}
		}
		
		return milist2;
	}
	
	//////////////  当成员被踢出，解散，处理
	private void mailToPlayerfunc(String guid)
	{
		TeamMember member = this.members.get(guid);
		if(member == null){
			return;
		}
		List<ItemMail> mailList = member.getMailItem();
		if(mailList  == null || mailList.size()<=0){
			return;
		}
		
		PlayerSession ps = room.getSessions().get(guid);
		
		if(ps != null){
			Player player = ps.getPlayer();
			if(player != null){
				for(int i=0;i<mailList.size();i++){
					ItemMail mail = mailList.get(i);
					if(mail.isAttachMent()){
						mail.addPlayerSysItemsMail(player);
					}else{
						mail.addPlayerSysMail(player);
					}
				}
			}
		}else{
			DefaultPlayer dp = objectCacheService.getCache(guid, DefaultPlayer.class);
			if (dp != null) {
				for(int i=0;i<mailList.size();i++){
					ItemMail mail = mailList.get(i);
					if(mail.isAttachMent()){
						mail.addPlayerSysItemsMail(dp);
					}else{
						mail.addPlayerSysMail(dp);
					}
				}
				objectCacheService.putObject(dp);
				dp = null;
			}
		}
		
		member.clearMailItem();
	}
	public void playerLoginHandleMails(Player player)
	{
		String guid = player.getId();
		TeamMember member = this.members.get(guid);
		if(member == null){
			return;
		}
		List<ItemMail> mailList = member.getMailItem();
		if(mailList  == null || mailList.size()<=0){
			return;
		}
		for(int i=0;i<mailList.size();i++){
			ItemMail mail = mailList.get(i);
			if(mail.isAttachMent()){
				mail.addPlayerSysItemsMail(player);
			}else{
				mail.addPlayerSysMail(player);
			}
		}
		member.clearMailItem();
	}
	////军团日志
	public void addTeamLog(TeamLog log){
		if(teamLog.size()>100){
			teamLog.remove(0);
		}
		teamLog.add(log);
	}

	public List<TeamLog> getTeamLogs(){
		return this.teamLog;
	}
	public void clearTeamLog()
	{
		teamLog.clear();
	}
	public void setWarcount(){
		this.warcount += 1;
	}
	public int getWarCount(){
		return this.warcount;
	}
	
	
	// 清数据
	public void fixRoleTeamNameData()
	{
		this.applyJoinPlayers.clear();
		this.cancelHerosFromArmyNew.clear();
		this.newArmyHero.clear();
		this.armyStrongHolds.clear();
	}
}
