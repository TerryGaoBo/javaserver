package com.jelly.team;

import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;
import io.nadron.context.AppContext;
import io.nadron.event.Events;
import io.nadron.example.lostdecade.LDRoom;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.RefuseWordsFilter;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.common.constant.GameConstManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.activity.ActivityType;
import com.jelly.activity.WorldActivity;
import com.jelly.combat.WarHerosFightManager;
import com.jelly.game.command.ChatConstants;
import com.jelly.hero.Hero;
import com.jelly.hero.HeroState;
import com.jelly.hero.MirrorHero;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.player.AttackerGroup;
import com.jelly.player.BaseFighter;
import com.jelly.player.DefenderGroup;
import com.jelly.player.EmojiFilter;
import com.jelly.player.IFighter;

import edu.emory.mathcs.backport.java.util.Collections;

public class TeamManager {
	
	private static final Logger logger = LoggerFactory.getLogger(TeamManager.class);
	
	private static TeamManager instance;
	
	private Map<String, Team> name2TeamMap;
	
	private List<String> teamFromNameMap;
	
	private Map<String, Team> deleteNameTeamMap;
	
	private TeamMapperWrapper teamMapper;
	
	///---------------------------------军团战---------------------------------------------------
	private List<String> warTeamName;
	
	private long warActvityEndTime = 0;// 军团战结束时间
	
	private static LDRoom room = AppContext.getBean(LDRoom.class);
	
	private final int WAR_WAR_TEAM_COUNTS = 1; // 参加军团战中必须满足大于5个人
	
	private static AllPlayersCache allPlayersCache = AppContext.getBean(AllPlayersCache.class);
	
	private static ObjectCacheService objectCacheService = AppContext.getBean(ObjectCacheService.class);
	
	///-----------------------------------------------------------------------------------
	private TeamManager() {
		this.teamFromNameMap = Lists.newArrayList();
		this.name2TeamMap = Maps.newHashMap();
		this.deleteNameTeamMap = Maps.newHashMap();
	}
	
	public void init() {
		try {
			teamMapper = new TeamMapperWrapper();
			for (Team team : teamMapper.loadAllTeams()) {
				if(team.getUseSign()==0){
					name2TeamMap.put(team.getName(), team);
					teamFromNameMap.add(team.getName());
				}else{
					deleteNameTeamMap.put(team.getName(), team);
				}
			}
//			logger.error("---------name2TeamMap is init:--------------"+name2TeamMap.size());
		} catch (JsonParseException ex) {
			logger.error(ex.getMessage());
		} catch (JsonMappingException ex) {
			logger.error(ex.getMessage());
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}
		calcTeamRank();
	}
	
	public List<String> getAllTeamNames() {
		return teamFromNameMap;
	}
	
	public Team getTeamByName(String name){
		return name2TeamMap.get(name);
	}
	
	public Map<String, Team> getAllTeams(){
		return name2TeamMap;
	}
	
	public Map<String,Team> getDeleteAllTeams(){
		return deleteNameTeamMap;
	}
	
	/**
	 * 对服务器内所有军团按照战斗力高低进行排序
	 * 
	 * <p>军团战力为所有成员战斗力的总和,战力越高名次越前
	 **/
	public synchronized void calcTeamRank() {
		Collections.sort(teamFromNameMap, new TeamSortComparator(name2TeamMap));
	}

	public synchronized void applyTeams(int page, Player player) {
		GameConstManager gconst = AllGameConfig.getInstance().gconst;
		final int pageInMaxTeams = (Integer) gconst
				.getConstant(GameConstId.TEAM_PAGE_IN_MAX_TEAMS);
		final int to = page * pageInMaxTeams, from = to - pageInMaxTeams;
		ArrayNode teams = DynamicJsonProperty.jackson.createArrayNode();
		for (int i = from; i < to && i < teamFromNameMap.size(); ++i) {
			Team team = name2TeamMap.get(teamFromNameMap.get(i));
			ObjectNode info = team.getBasicInfo();
			info.put("ranking", teamFromNameMap.indexOf(team.getName()) + 1); // 军团排名
			teams.add(info);
		}
		ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
		res.put("teams", teams);
		res.put("maxTeams", name2TeamMap.size());
		player.sendMessage("team", res);
	}
	
	public synchronized void getTeamInfo(String name, Player player) {
		Team team = name2TeamMap.get(player.getTeam().getName());
		if (team == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		ObjectNode info = team.getDetailInfo(player);
		if (info == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		info.put("ranking", teamFromNameMap.indexOf(team.getName()) + 1);
		player.sendMessage("team", info);
	}
	
	/**
	 * 创建军团
	 * 
	 * @param name - 军团名称
	 * @param intro - 军团介绍
	 * @param lv - 入团最低等级
	 * @param player
	 **/
	public synchronized void createTeam(String name, String intro, int lv, Player player) {
		name = name.trim();
		name = name.replaceAll("'","");
		name = name.replaceAll(" ","");
		name = name.replaceAll("　", "");
		
		// 判断用户名是否包含非法字符
		if (!StringHelper.containsNone(name, " ~`!^?<>/=\"\'%,.(){}[]+*~&|;#$:-　") 
				|| RefuseWordsFilter.getInstance().contain(name)) {
			player.sendResult(OperResultType.CREATE_NAME, MessageCode.INVALID_NAME);
			return;
		}
		
		GameConstManager gconst = AllGameConfig.getInstance().gconst;		
		
		//	检查玩家是否满足创建军团条件
		int requireVillageLv = (Integer)gconst.getConstant(GameConstId.TEAM_CREATE_VILLAGE_LEVEL);
		if (player.getProperty().getLevel() < requireVillageLv) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.FAIL
					);
			return;
		}
		
		//	检查军团名称是否符合要求
		int[] nameConds = (int[])gconst.getConstant(GameConstId.TEAM_NAME_LEN);
		if (!(name.length() >= nameConds[0] && 
				name.length() <= nameConds[1])) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.FAIL
					);
			return;
		}
		if (RefuseWordsFilter.getInstance().contain(name)) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.TEAM_NAME_REFUSE_WORDS
					);
			return;
		}
		
		//	检查公告介绍是否包含违禁字
		int maxIntroWords = (Integer)gconst.getConstant(GameConstId.TEAM_MAX_INTRO_LEN);
		if (intro.length() > maxIntroWords) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.FAIL
					);
			return;
		}
		if (RefuseWordsFilter.getInstance().contain(intro)) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.TEAM_INTRO_REFUSE_WORDS
					);
			return;
		}
		
		name = EmojiFilter.filterEmoji(name);
		intro = EmojiFilter.filterEmoji(intro);
		
		if(name.equals("")){
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.FAIL
					);
			return;
		}
		
		//	检查玩家是否有足够的金币与银币
		int[] costs = (int[])gconst.getConstant(GameConstId.TEAM_CREATE_COSTS);
		if (!player.getProperty().hasEnoughMoney(
				costs[0], 
				costs[1], 
				null)
				) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.GOLD_NOT_ENUGH
					);
			return;
		}
		
		//	检查帮会入团等级
		int[] lvConds = (int[])gconst.getConstant(GameConstId.TEAM_JOIN_LEVEL);
		if (!(lv >= lvConds[0] && lv <= lvConds[1])) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.FAIL
					);
			return;
		}
		
		if (name2TeamMap.containsKey(name)) {
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.TEAM_NAME_EXIST
					);
			return;
		}
		boolean exist = false;
		for (String existName : name2TeamMap.keySet()) {
			if (existName.equalsIgnoreCase(name)) {
				exist = true;
				break;
			}
		}
		if (exist) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NAME_EXIST);
			return;
		}
		if(!player.getTeam().getName().equals(""))
		{
			player.sendResult(
					OperResultType.TEAM, 
					MessageCode.TEAM_JOIN_OEHTERTEAM
					);
			return;
		}
		
		Team team = new Team(name, intro, lv, player);
		name2TeamMap.put(name, team);
		teamFromNameMap.add(team.getName());
		player.getProperty().changeMoney(-costs[0], -costs[1]);
		player.getTeam().setName(name);
//		新军团创建成功,撤销军团长之前发给其它军团的入团申请
		for (String teamName : player.getTeam().getApplyTeams()) {
			removeTeamJoinApply(teamName, player.getId());
		}
		player.getTeam().clearApplyTeams();
		
		player.sendResult(
				OperResultType.TEAM, 
				MessageCode.OK, 
				team.getDetailInfo(player)
				);
		//	发送广播消息,通知服务器内所有玩家
		player.broadcastChatMessage(
				MessageCode.BROADCAST_CREATE_TEAM, 
				player.getName(), 
				name);
		
		try {
			teamMapper.insertTeams(team);
		} catch (JsonProcessingException e) {
		}
	}
	
	/**
	 * 更改军团成员职务
	 **/
	public synchronized void modifyTeamMemberTitle(String memberId, Player player, boolean isPromotion) {		
		Team myTeam = name2TeamMap.get(player.getTeam().getName());
		if (myTeam == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
		} else {
			if (isPromotion) {	//	升职
				myTeam.promoteMemberTitle(player, memberId);
			} else {	//	降职
				myTeam.demotionMemberTitle(player, memberId);
			}
			teamMapper.markUpdate(myTeam);
		}
	}
	
	/**
	 * 搜索名称与给定关键字匹配的军团信息
	 * 
	 * @param condition - 搜索关键字
	 **/
	public synchronized void searchTeams(String condition, Player player) {
		ArrayNode teams = DynamicJsonProperty.jackson.createArrayNode();
		for (Team team : name2TeamMap.values()) {
			// 检索军团名时忽略大小写
			if (team.getName().toLowerCase().indexOf(condition.toLowerCase()) > -1) {
				ObjectNode info = team.getBasicInfo();
				info.put("ranking", teamFromNameMap.indexOf(team.getName()) + 1);
				teams.add(info);
			}
		}
		ObjectNode res = DynamicJsonProperty.jackson.createObjectNode();
		res.put("searchTeams", teams);
		player.sendMessage("team", res);	
	}
	
	public synchronized int talkWords(String text, Player player) {
		Team team = name2TeamMap.get(player.getTeam().getName());
		if (team == null) {
			return MessageCode.TEAM_NO_EXIST;
		}
		return team.talkWords(text, player);
	}
	
	public static TeamManager getSingleton() {
		if (instance == null) {
			instance = new TeamManager();
		}
		return instance;
	}
	
	public synchronized void saveAllTeams(boolean isShutdown) {
		try {
			logger.info("save all teams");
			long l = System.currentTimeMillis();
			teamMapper.update(isShutdown);
			if(isShutdown){
				teamMapper.updateAllTeams();
			}
			logger.info("save all teams complete elapse - " + (System.currentTimeMillis() - l));
		} catch (JsonProcessingException ex) {
			logger.error("TeamManager.saveAllTeams#Err " + ex.getMessage());
		}
	}
	
	public synchronized void removeTeamJoinApply(Player player) {
		for (String teamName : player.getTeam().getApplyTeams()) {
			if (name2TeamMap.containsKey(teamName)) {
				name2TeamMap.get(teamName).getApplyJoinPlayers().remove(player.getId());
				teamMapper.markUpdate(name2TeamMap.get(teamName));
			}
		}
	}
	
	public synchronized void removeTeamJoinApply(String name, String guid) {
		if (name2TeamMap.containsKey(name)) {
			name2TeamMap.get(name).getApplyJoinPlayers().remove(guid);
			teamMapper.markUpdate(name2TeamMap.get(name));
		}
	}
	
	public synchronized void removeTeamJoinApply(List<String> teamNames, String playerId) {
		for (String teamName : teamNames) {
			Team team = name2TeamMap.get(teamName);
			if (team != null) {
				team.getApplyJoinPlayers().remove(playerId);
				teamMapper.markUpdate(team);
			}
		}
	}
	
	////////////////////////////////////////////////
	/**
	 * 退出军团
	 **/
	public synchronized int leaveTeam(Player player) {
		Team team = name2TeamMap.get(player.getTeam().getName());
		if (team == null) {
			return MessageCode.TEAM_NO_EXIST;
		}
		int res = team.leave(player);
		teamMapper.markUpdate(team);
		return res;
	}
	
	/**
	 * 将给定玩家从军团中移除
	 **/
	public synchronized int dismissTeamMember(Player player, String mid) {
		Team team = name2TeamMap.get(player.getTeam().getName());
		if (team == null) {
			return MessageCode.TEAM_NO_EXIST;
		}
		int res = team.dismiss(player, mid);
		teamMapper.markUpdate(team);
		return res;
	}
	
	/**
	 * 批准给定的玩家加入军团的申请
	 **/
	public synchronized void agreeJoinTeamApply(Player player, String playerID) {
		Team team = name2TeamMap.get(player.getTeam().getName());
		if (team == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		if (team.agreeJoinApply(player, playerID)) {		
			teamMapper.markUpdate(team);
		}
	}
	
	/**
	 * 拒绝给定的玩家加入军团的申请
	 **/
	public synchronized void refuseJoinTeamApply(Player player, String playerID) {
		Team team = name2TeamMap.get(player.getTeam().getName());
		if (team == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		if (team.refuseJoinApply(player, playerID)) {
			teamMapper.markUpdate(team);
		}
	}
	
	/**
	 * 撤回玩家提交的军团申请
	 **/
	public synchronized int undoJoinTeamApply(Player player, String teamName) {
		Team team = name2TeamMap.get(teamName);
		if (team == null) {
			return MessageCode.TEAM_NO_EXIST;
		}
		int res = team.undoJoinApply(player.getId());
		teamMapper.markUpdate(team);
		return res;
	}
	
	/**
	 * 提交入团申请
	 **/
	public synchronized int submitJoinTeamApply(Player player, String teamName) {
		Team team = name2TeamMap.get(teamName);
		if (team == null) {
			return MessageCode.TEAM_NO_EXIST;
		}
		int res = team.submitJoinApply(player);
		teamMapper.markUpdate(team);
		return res;
	}
	
	/**
	 * 解散军团
	 **/
	public synchronized int dissolveTeam(Player player, String teamName) {
		Team team = name2TeamMap.get(teamName);
		if (team == null) {
			return MessageCode.TEAM_NO_EXIST;
		}
		int res = team.dissolve(player);
		if (res == MessageCode.OK) {
			name2TeamMap.remove(teamName);
			teamFromNameMap.remove(teamFromNameMap.indexOf(teamName));
			deleteNameTeamMap.put(team.getName(), team);
			teamMapper.markUpdate(team);
//			teamMapper.deleteTeams(teamName);
		}
		return res;
	}
	
	public void dissoveTeamByPlayerGuidTwo(String teamName)
	{
		Team team = name2TeamMap.get(teamName);
		if(team == null){
			return;
		}
		
		int res = team.dissolveTwo();
		if (res == MessageCode.OK) {
			name2TeamMap.remove(teamName);
			teamFromNameMap.remove(teamFromNameMap.indexOf(teamName));
			deleteNameTeamMap.put(team.getName(), team);
			teamMapper.markUpdate(team);
		}
	}
	public void clearTeamWealthfunc(String teamName,int value)
	{
		Team team = name2TeamMap.get(teamName);
		if(team == null){
			return;
		}
		int res = team.clearTeamWealthfunc(value);
		if (res == MessageCode.OK) {
			teamMapper.markUpdate(team);
		}
	}
	
	public void dissoveTeamByPlayerGuid(String name,String teamName)
	{
		String guid = allPlayersCache.getPlayerIdByName(name);
		if(guid == null){
			return;
		}
		
		PlayerSession ps = room.getSessions().get(guid);
		if(ps != null){ // 在线恢复
			DefaultPlayer dp = (DefaultPlayer)ps.getPlayer();
			if (dp != null) {
				this.dissoveTeamByGuid(dp, teamName);
			}
		}else{ // 离线恢复
			DefaultPlayer dp = objectCacheService.getCache(guid, DefaultPlayer.class);
			if (dp != null) {
				this.dissoveTeamByGuid(dp, teamName);
				objectCacheService.putObject(dp);
				dp = null;
			}
		}
	}
	private void dissoveTeamByGuid( Player player, String teamName)
	{
		Team team = name2TeamMap.get(teamName);
		if(team == null){
			return;
		}
		int res = team.dissolve(player);
		if (res == MessageCode.OK) {
			name2TeamMap.remove(teamName);
			teamFromNameMap.remove(teamFromNameMap.indexOf(teamName));
			deleteNameTeamMap.put(team.getName(), team);
			teamMapper.markUpdate(team);
		}
	}
	
	public synchronized void modifyTeamProfile(Player player, String intro, String announ, Integer joinLv,Integer pinzhi,Integer dengji) {
		Team team = name2TeamMap.get(player.getTeam().getName());
		if (team == null) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		if (team.modifyProfile(player, intro, announ, joinLv,pinzhi,dengji)) {
			teamMapper.markUpdate(team);
		}
	}
	
	public synchronized void upgradeTeam(Player player) {
		String teamName = player.getTeam().getName();
		if (!name2TeamMap.containsKey(teamName)) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		Team team = name2TeamMap.get(teamName);
		if (team.upgrade(player)) {
			teamMapper.markUpdate(team);
		}
	}
	
	public synchronized void donateTeamWealth(Player player, int silver, int gold) {
		String teamName = player.getTeam().getName();
		if (!name2TeamMap.containsKey(teamName)) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		Team team = name2TeamMap.get(teamName);
		if (team.donateWealth(player, silver, gold)) {
			teamMapper.markUpdate(team);
		}
	}
	
	public synchronized void getTeamArmy(Player player,int cp,int type) {
		if (!name2TeamMap.containsKey(player.getTeam().getName())) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		
		ObjectNode node = name2TeamMap.get(player.getTeam().getName()).getArmy(player,cp,type);
		player.sendMessage("team", node);
	};
	
	/**
	 * 派出给定忍者到军团部队
	 **/
	public synchronized boolean sendRoleToTeamArmy(Player player, List<Integer> hids) {
		if (!name2TeamMap.containsKey(player.getTeam().getName())) {
			sendPlayerFail(player, MessageCode.TEAM_NO_EXIST);
			return false;
		}
		boolean result =  name2TeamMap.get(player.getTeam().getName()).sendRoleToArmy(player, hids);
		if(result){
			teamMapper.markUpdate(name2TeamMap.get(player.getTeam().getName()));
		}
		return result;
	}
	
	/**
	 * 撤回派出到部队的忍者
	 **/
	public synchronized void cancelRoleFromTeamArmy(Player player, Map<String, List<Integer>> cancelRolesMemberID2Index) {
		String teamName = player.getTeam().getName();
		if (!name2TeamMap.containsKey(teamName)) {
			sendPlayerFail(player, MessageCode.TEAM_NO_EXIST);
			return;
		}
		Team myTeam = name2TeamMap.get(teamName);
		if (myTeam.cancelRolesFromArmy(player, cancelRolesMemberID2Index)) {
			teamMapper.markUpdate(myTeam);
		}
	}
	
	public synchronized List<Integer> popCancelRolesFromTeamArmy(Player player) {
		
		List<Integer> list = Lists.newArrayList();

		String teamName = player.getTeam().getName();
		String levelTeamName = player.getTeam().getLeaveTeam();
		Team team = null;
		if(teamName == null || teamName.equals("")){
			if(levelTeamName != null && !levelTeamName.equals("")){
				team = name2TeamMap.get(levelTeamName);
				player.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
			}
		}else{
			team = name2TeamMap.get(teamName);
		}

		if(team != null){
			list.addAll(team.popCancelRolesFromArmy(player.getId()));
		}

		player.getTeam().setLeaveTeam("");
		
		String name = player.getTeam().getName();
		if(!name.equals("")){
			Team xteam = name2TeamMap.get(name);
			if(xteam == null){
				player.getTeam().setName("");
				RoleEntity role = allPlayersCache.getPlayerInfo(player.getId());
				if(role != null){
					allPlayersCache.setPlayerTeamName(player.getId(), "");
				}
				
				player.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
			}else{
				int count = xteam.getNewArmyHeroCount(player.getId());
				if(count==0){
					player.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
				}
				
				List<ArmyHero> mherolist = xteam.getNewArmyHeroList(player.getId());
				if(mherolist != null && mherolist.size()>0){
					player.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
					for(ArmyHero armyhero : mherolist){
						int hid = armyhero.getHid();
						player.getHeros().addHeroState(hid, HeroState.TEAM_ARMY_STATE);
					}
				}
				
				if(xteam.getTeamMember(player.getId()) == null){
					player.getTeam().setName("");
					RoleEntity role = allPlayersCache.getPlayerInfo(player.getId());
					if(role != null){
						allPlayersCache.setPlayerTeamName(player.getId(), "");
					}
					player.getHeros().clearHerosAllState(HeroState.TEAM_ARMY_STATE);
					
					xteam.clearNewArmyHero(player.getId());
				}else{
					xteam.playerLoginHandleMails(player);
				}
			}
		}

		return list;
	}
	
	private static void sendPlayerFail(Player player, int code) {
		player.sendResult(OperResultType.TEAM, code);
	}
	/**
	 * 强制设置军团长
	 * @param name
	 * @param guid
	 */
	public synchronized boolean setTeamCommander(String name, String guid) {
		Team team = name2TeamMap.get(name);
		if (team == null) {
			return false;
		}
		boolean result = team.setTeamCommander(guid);
		if(result){
			teamMapper.markUpdate(team);
		}
		return result;
	}
	
	
	
	/**
	 * 获得军团据点信息
	 * @param player
	 * @param strongHoldid
	 * @param sign
	 */
	public synchronized void getStrongHoldInfo(Player player){
		String teamName = player.getTeam().getName();
		if (!name2TeamMap.containsKey(teamName)) {
			sendPlayerFail(player, MessageCode.TEAM_NO_EXIST);
			return;
		}
		ObjectNode armyInfo = DynamicJsonProperty.jackson.createObjectNode();
		ArrayNode armyBases = DynamicJsonProperty.jackson.createArrayNode();
		Team myTeam = name2TeamMap.get(teamName);
		armyBases = myTeam.getStrongHoldsInfo(player);
		
		armyInfo.put("armyBases", armyBases); // 据点
		player.sendMessage("team", armyInfo);
	}
	public synchronized void sendRolesToStrongHold(Player player,Integer strongHoldId,Integer shIdx,String guid,Integer heroIdx){
		String teamName = player.getTeam().getName();
		if (!name2TeamMap.containsKey(teamName)) {
			sendPlayerFail(player, MessageCode.TEAM_NO_EXIST);
			return;
		}
		Team myTeam = name2TeamMap.get(teamName);
		if(myTeam.isWarOpened()){//军团战期间不能操作
			player.sendResult(OperResultType.TEAMSTRONGHOLD, MessageCode.TEAM_WAR_CANNOT_SENDROLES_TO_SH);
			return;
		}
		
		if (myTeam.sendRoleToStrongHold(player,strongHoldId,shIdx,guid,heroIdx)) {
			teamMapper.markUpdate(myTeam);
		}
	}
	
	public synchronized void cancelRolesFromStrongHold(Player player,Integer strongHoldid,Integer idx){
		String teamName = player.getTeam().getName();
		if (!name2TeamMap.containsKey(teamName)) {
			sendPlayerFail(player, MessageCode.TEAM_NO_EXIST);
			return;
		}
		Team myTeam = name2TeamMap.get(teamName);
		if(myTeam.isWarOpened()){//军团战期间不能操作
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		if (myTeam.cancelRolesFromStrongHold(player,strongHoldid,idx)) {
			teamMapper.markUpdate(myTeam);
		}
	}
	
	public void addTeamExp(String teamName, int exp) {
		Team team = name2TeamMap.get(teamName);
		if (team != null) {
			team.addExp(exp);
		}
	}

	
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////  GM Interface /////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	public synchronized ArrayNode getTeams(List<String> names) {		
		ArrayNode teamsInfo = DynamicJsonProperty.jackson.createArrayNode();
		if (names.isEmpty()) {	// 所有军团信息
			for (Team team : name2TeamMap.values()) {
				teamsInfo.add(team.getGMInfo());
			}
		} else {
			for (String name : names) {
				Team team = name2TeamMap.get(name);
				if (team != null) {
					teamsInfo.add(team.getGMInfo());
				}
			}
		}
		return teamsInfo;
	}
	
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////  军团战  /////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	// -------------------------------------- 军团战 -----------------------------------------------

	public synchronized void saveTeamWarDataDB(Player player)
	{
		String teamName = player.getTeam().getName();
		if (teamName.equals("")) {
			return;
		}
		
		Team myteam = name2TeamMap.get(teamName);
		Team hteam = null;
		if(myteam == null){
			return;
		}
		teamName = "";
		teamName = myteam.getTeamWarTeamName();
		if (teamName.equals("")) {
			return;
		}
		hteam = name2TeamMap.get(teamName);
		if(hteam == null){
			return;
		}
		
		teamMapper.markUpdate(myteam);
		teamMapper.markUpdate(hteam);
	}
	
	//军团战结束 清理
	public synchronized void teamWarEndFunc()
	{
		logger.info("军团战结束!!!!");
		
		if(warTeamName == null || warTeamName.size() == 0){
			return;
		}
		
		for (String t : warTeamName) {
			Team myTeam = name2TeamMap.get(t);
			Team hTeam = name2TeamMap.get(myTeam.getTeamWarTeamName());
			teamMapper.markUpdate(myTeam);
			if(hTeam != null){
				teamMapper.markUpdate(hTeam);	
			}
		}
	
		saveTeamWarResultReward();
		
		broadcastChatMessage(MessageCode.WAR_TIPS_6269);
		
		sendAllOnlinePlayerWarState(MessageCode.WAR_TIPS_6269);
		
		warTeamName.clear();
		
		warTeamName = null;
		warActvityEndTime = 0;
	}

	/**
	 *  军团战开始
	 */
	public synchronized void teamWarStartFunc()
	{
		logger.info("军团战开始!!!!");
		for (Team t : name2TeamMap.values()) {
			t.initWarData();
			t.clearTeamLog();
		}
		
		saveTeamWar();
		
		broadcastChatMessage(MessageCode.WAR_TIPS_6270);
		
		sendAllOnlinePlayerWarState(MessageCode.WAR_TIPS_6270);
	}

	/**
	 * 军团匹配配对
	 */
	public  void saveTeamWar() {
		warTeamName = Lists.newArrayList();
		List<String> list = Lists.newArrayList();
		for(int i=0;i<teamFromNameMap.size();i++){
			Team team = name2TeamMap.get(teamFromNameMap.get(i));
			/// 
			if(team.getMembers().size() >= WAR_WAR_TEAM_COUNTS){
				list.add(teamFromNameMap.get(i));
			}
		}

		int listsize = list.size();
		if(listsize % 2 != 0 && listsize >0){
			listsize = listsize - 1;
		}
		for(int i=0;i<listsize;i++){
			Team team = name2TeamMap.get(list.get(i));
			Team team1 = name2TeamMap.get(list.get(i+1));
			team.setTeamWarTeamName(team1.getName());
			team1.setTeamWarTeamName(team.getName());
			i++;
			warTeamName.add(team.getName());
		}

		for (String t : warTeamName) {
			Team myTeam = name2TeamMap.get(t);
			Team hTeam = name2TeamMap.get(myTeam.getTeamWarTeamName());
			myTeam.initTeamWarInfoBase();
			hTeam.initTeamWarInfoBase();
		}
	}

	// 军团战结束之后奖励结算
	public synchronized void saveTeamWarResultReward() {
		if(warTeamName==null || warTeamName.size()==0){
			logger.error("no team war");
			return;
		}
		
		for(String teamName:warTeamName){
			Team myTeam = name2TeamMap.get(teamName);
			Team hTeam = name2TeamMap.get(myTeam.getTeamWarTeamName());
			if(hTeam == null){
				logger.info("Team End Reward={}"+myTeam.getName());
				continue;
			}
			int km = compareWinFunc(myTeam,hTeam);
			if(km != -2){
				if(km == 1){
					myTeam.addWarWinReward();
					hTeam.warLoseRewards();
				}else if(km == 0){
					
					myTeam.sendMailToAllMemberTeamWar(myTeam.getName(), TeamConstants.TEAM_WAR_DRAW, 
							MessageCode.TEAM_WAR_DRAW_TITLE, 
							MessageCode.TEAM_WAR_DRAW_CONTENT, 
							new String[]{hTeam.getName()});
					hTeam.sendMailToAllMemberTeamWar(hTeam.getName(), TeamConstants.TEAM_WAR_DRAW, 
							MessageCode.TEAM_WAR_DRAW_TITLE, 
							MessageCode.TEAM_WAR_DRAW_CONTENT, 
							new String[]{myTeam.getName()});
					
					myTeam.teamPeaceFunc();
					hTeam.teamPeaceFunc();
					
				}else if(km == -1){
					hTeam.addWarWinReward();
					myTeam.warLoseRewards();
				}
			}else{
				myTeam.sendMailToAllMemberTeamWar(myTeam.getName(), TeamConstants.TEAM_WAR_DRAW, 
						MessageCode.TEAM_WAR_DRAW_TITLE, 
						MessageCode.TEAM_WAR_DRAW_CONTENT, 
						new String[]{hTeam.getName()});
				hTeam.sendMailToAllMemberTeamWar(hTeam.getName(), TeamConstants.TEAM_WAR_DRAW, 
						MessageCode.TEAM_WAR_DRAW_TITLE, 
						MessageCode.TEAM_WAR_DRAW_CONTENT, 
						new String[]{myTeam.getName()});
			}
			
			myTeam.cancelMemberRolesFromWar();
			hTeam.cancelMemberRolesFromWar();
		}
	}
//	private int compareWin(Team myTeam,Team hTeam){
//		if(myTeam.getWarScore() > hTeam.getWarScore()){
//			return 1;
//		}else if(myTeam.getWarScore() == hTeam.getWarScore()){
//			if(myTeam.getStrongHoldWinAndLoseNum().get(0)+hTeam.getStrongHoldWinAndLoseNum().get(1) >
//			myTeam.getStrongHoldWinAndLoseNum().get(1)+hTeam.getStrongHoldWinAndLoseNum().get(0)){
//				return 1;
//			}else if(myTeam.getStrongHoldWinAndLoseNum().get(0)+hTeam.getStrongHoldWinAndLoseNum().get(1).intValue() ==
//					myTeam.getStrongHoldWinAndLoseNum().get(1)+hTeam.getStrongHoldWinAndLoseNum().get(0)){
//				if(myTeam.getWinNum() > hTeam.getWinNum()){
//					return 1;
//				}else if(myTeam.getWinNum() == hTeam.getWinNum()){
//					if(myTeam.theBaseIsZhanLing(21)==0 && hTeam.theBaseIsZhanLing(21)==1){
//						return 1;
//					}else if(myTeam.theBaseIsZhanLing(21)==1 && hTeam.theBaseIsZhanLing(21)==0){
//						return 1;
//					}else{
//						return -1;
//					}
//				}else{
//					return 0;
//				}
//			}else{
//				return 0;
//			}
//		}else{
//			return 0;
//		}
//	}
	
	private int compareWinFunc(Team myTeam,Team hTeam){
		int mscore = myTeam.getWarScore();
		int hscore = hTeam.getWarScore();
		if(mscore == 0 && hscore == 0){
			return -2;
		}
		if(mscore>hscore){
			return 1;
		}else if(mscore == hscore){
			return 0;
		}else{
			return -1;
		}
	}

	public synchronized void getTeamWarInfo(Player player,JsonNode object,WorldActivity worldActivity)
	{
		String teamName = player.getTeam().getName();
		if (!name2TeamMap.containsKey(teamName)) {
			sendPlayerFail(player, MessageCode.TEAM_NO_EXIST);
			return;
		}
		
		Integer strongHoldid = -1;
		if(object.get("baseIdState") != null)
			strongHoldid = object.get("baseIdState").asInt();

		Team myteam = name2TeamMap.get(teamName);
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		boolean state = myteam.isWarOpened();

		info.put("isWarOpened",state);
		
		info.put("warlefttime", 0);
		
		if(state){
			info.put("playerscore",myteam.getWarMember(player.getId()));
			Team hTeam = name2TeamMap.get(myteam.getTeamWarTeamName());
			
			info.put("teamWarTeamName",myteam.getTeamWarTeamName());
			info.put("enemyLevel", hTeam.getLevel());
			info.put("enemyScore", hTeam.getWarScore());
			info.put("warScore", myteam.getWarScore());
			info.put("myteamLevel", myteam.getLevel());

			ArrayNode armyBases = DynamicJsonProperty.jackson.createArrayNode();
			armyBases = myteam.getTeamWarHoldsInfo();
			info.put("myArmyBases",armyBases);
			
			armyBases = hTeam.getTeamWarHoldsInfo();
			info.put("enemyBases", armyBases);
			if(strongHoldid == -1 || strongHoldid>20 ){
				info.put("baseIdState",-1);
			}else{
				boolean t = hTeam.theBaseCanFight(strongHoldid);
				if(t){
					info.put("baseIdState",1);
				}else{
					info.put("baseIdState",0);
				}
			}
			
			if(warActvityEndTime == 0){
				warActvityEndTime = worldActivity.getActivityEndTimeByID(ActivityType.TEAMS_WAR.getActId());
			}
			long cd = warActvityEndTime-System.currentTimeMillis(); //1447301036 1447301100000
			cd  = (int)(cd /1000);
			if(cd<0){
				cd = 0;
			}
			info.put("warlefttime", cd);
		}
		player.sendMessage("teamWar", info);
	}

	// 得到当前玩家参加军团战的英雄
	public synchronized void getWarHeroIndexList(Player player)
	{
		Team myteam = name2TeamMap.get(player.getTeam().getName());
		
		if(!myteam.isWarOpened()){
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			info.put("teamWarClosed", true);
			player.sendMessage("teamWar", info);
			player.sendResult(OperResultType.TEAM, MessageCode.WAR_TIPS_6268);
			return;
		}
		
		ArrayNode arr = myteam.getWarHeroIndexList(player);
		
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		info.put("warHerosList", arr);
		player.sendMessage("teamWar", info);
	}

	// 取消防守据点中的忍者,在防守据点中删除忍者
	public synchronized void updateWarBaseHeroState(Player player,Integer index)
	{
		Team myteam = name2TeamMap.get(player.getTeam().getName());
		
		if(!myteam.isWarOpened()){
			
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			info.put("teamWarClosed", true);
			player.sendMessage("teamWar", info);
			player.sendResult(OperResultType.TEAM, MessageCode.WAR_TIPS_6268);
			return;
		}
		
		ArmyHero mhero = myteam.getArmyHeroByIndex(player.getId(), index);
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		if(mhero.getHps()<=0){
			info.put("theHeroIsDie", 0);
			player.sendMessage("teamWarHeroState",info);
			sendPlayerFail(player, MessageCode.TEAM_WAR_CANNOT_HEROISDEAD);
			return;
		}
		
		info.put("theHeroIsDie", 1);
		player.sendMessage("teamWarHeroState",info);
		
		int strongid = mhero.getStrongID();
		if(strongid ==-1){
			return;
		}
		
		Team hteam = name2TeamMap.get(myteam.getTeamWarTeamName());
		int herowhere = mhero.getheroWhere();
		WarHoldBase base = null;
		if(herowhere == 1){
			base = hteam.getWarHoldBases(strongid);
		}else{
			base = myteam.getWarHoldBases(strongid);
		}
		
		List<WarBaseHeroOne> onelist = base.getBaseHero();
		for(int i = 0;i<onelist.size();i++)
		{
			WarBaseHeroOne one = onelist.get(i);
			if(one.getWarIndex() == index){
				onelist.remove(i);
				break;
			}
		}
		
		mhero.setStrongID(-1);
		mhero.setheroWhere(-1);
		
		saveTeamWarDataDB(player);
	}

	// baseIndex 据点id
	// warType 0 攻打敌方，1 攻打占领我方据点的敌人
	// type :(0 是忍者列表，1 是索引列表)
	// index: 忍者id/索引列表id
	// 开始战斗  object {"baseIndex":0,"hes":[{"type":0,"index":0},{},{}]
	public synchronized void startTeamWarFight(Player player,JsonNode object)
	{
		Team myteam = name2TeamMap.get(player.getTeam().getName());
		if(!myteam.isWarOpened()){
			
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			info.put("teamWarClosed", true);
			player.sendMessage("teamWar", info);
			player.sendResult(OperResultType.TEAM, MessageCode.WAR_TIPS_6268);
			return;
		}
		
		ArrayNode arr = (ArrayNode)object.get("hes");
		Integer strongHoldid = object.get("baseIndex").asInt();
		Integer warType = object.get("warType").asInt();

		if(arr.size()==0){
			logger.info("the hero is empty,how fight?");
			sendPlayerFail(player, MessageCode.WAR_TIPS_6275);
			return;
		}

		Team hteam = name2TeamMap.get(myteam.getTeamWarTeamName());

		if(warType == 0){
			if(!hteam.theBaseCanFight(strongHoldid)){
				sendPlayerFail(player, MessageCode.TEAM_WAR_CANNOT_FIGHT);
				return;
			}
		}

		// -- 攻击列表 --
		List<MirrorHero> mirrorHeroAttackers = Lists.newArrayList();
		for(int i=0;i<arr.size();i++)
		{
			ObjectNode o = (ObjectNode)arr.get(i);
			int index = o.get("index").asInt();
			float hpRate = 0;
			
			ArmyHero mhero = myteam.getArmyHeroByIndex(player.getId(), index);
			hpRate = mhero.getHps();
			if(hpRate <=0){
				continue;
			}
			Hero hero1 = mhero.getHero();
			Hero hero = player.getHeros().getHero(mhero.getHid().intValue());
			if(hero == null){
				if(hero1 == null){
					continue;
				}
				hero = hero1;
			}
			
			MirrorHero mirrorHero = new MirrorHero(0,hero,(int) (hpRate*hero.getHpMax()),hero.getHpMax(), 1);
			mirrorHero.setDefineID(index);
			mirrorHeroAttackers.add(mirrorHero);
		}

		List<IFighter> fighterAList = Lists.newArrayList();
		for (MirrorHero hero : mirrorHeroAttackers) {
			IFighter fighterA = new BaseFighter(hero);
			fighterAList.add(fighterA);
		}

		AttackerGroup warAttackerGroup = new AttackerGroup(fighterAList);
		if(warAttackerGroup.isEmptyGroup()) {
			logger.error("doTeamWarFight empty attackerGroup,player name:{}",player.getName());
			return ;
		}

		/// --- 防守列表
		List<MirrorHero> defendHeros = Lists.newArrayList();

		WarHoldBase base = null;
		if(warType == 0){
			base = hteam.getWarHoldBases(strongHoldid);
		}else{
			base = myteam.getWarHoldBases(strongHoldid);
		}

		List<WarBaseHeroOne> baseHList = base.getBaseHero();
		float hpRate = 0;
		if(baseHList.size() ==0){
			logger.info("防守为空，如果此据点是我方据点则直接防守，如果是地方据点则直接占领。。。。。。");
			defendOrAttackFunc(player,arr,strongHoldid,warType,mirrorHeroAttackers);
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			info.put("warFightState", 1); // 0 是此举点已经被我方占领 1，此据点是空据点
			player.sendMessage("teamWar", info);
			return;
		}
		
		Map<String, String> guidmap = Maps.newHashMap();
		
		for(int i=0;i<baseHList.size();i++)
		{
			WarBaseHeroOne one = baseHList.get(i);
			String km = one.getTeamName();
			String kn = myteam.getName();
			if(km != null && kn != null && km.equals(kn)){
				ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
				info.put("warFightState", 0); // 0 是此举点已经被我方占领 1，此据点是空据点
				player.sendMessage("teamWar", info);
				player.sendResult(OperResultType.TEAMSTRONGHOLD, MessageCode.TEAM_STRONGHOLD_STILLHOLD);
				return;
			}
			Integer warIndex = one.getWarIndex();
			ArmyHero who = hteam.getArmyHeroByIndex(one.getGuid(), warIndex);
			hpRate = who.getHps();
			if(hpRate <= 0){
				continue;
			}
			Hero hero = who.getHero();
			MirrorHero mirrorHero = new MirrorHero(0,hero,(int) (hpRate*hero.getHpMax()),hero.getHpMax(), 1);
			mirrorHero.setDefineID(warIndex);
			mirrorHero.setDefineGuid(one.getGuid());
			defendHeros.add(mirrorHero);
			
			if(guidmap.get(one.getGuid()) == null){
				guidmap.put(one.getGuid(), one.getGuid());
			}
		}
		List<IFighter> defendList = Lists.newArrayList();
		for (MirrorHero hero : defendHeros) {
			IFighter fighterA = new BaseFighter(hero);
			defendList.add(fighterA);
		}
		
		DefenderGroup wardefendGroup = new DefenderGroup(defendList);
		WarHerosFightManager warManager = new WarHerosFightManager(warAttackerGroup, wardefendGroup, player, myteam,hteam, strongHoldid,warType,guidmap);
		warManager.start();
		
		/// 添加日志：
		boolean iswin = warManager.getAttackerGroup().isWin();
		TeamLog log = new TeamLog();
		log.type = 1;
		log.myName = player.getName();
		log.idx = strongHoldid;
		log.warType = warType;
		log.lv = myteam.getLevel();
		for(int i = 0;i<mirrorHeroAttackers.size();i++){
			MirrorHero mo = mirrorHeroAttackers.get(i);
			log.mlist.add(mo.getRoleId());
		}
		
		if(iswin){
			log.win = 1;
			log.myscore = myteam.getWinAddwardScore();
			
			log.escore = hteam.getLostWardScore();
		}else{
			log.win = 0;
			log.myscore = myteam.getLostWardScore();
			log.escore = hteam.getWinAddwardScore();
		}

		String dguid = "";
		if(guidmap.size() == 1){
			for(String dkguid : guidmap.values()){
				dguid = dkguid;
			}

			RoleEntity drole = allPlayersCache.getPlayerInfo(dguid);
			if(drole != null){
				log.eName = drole.getName();
			}
		}else{
			log.eName = "";
		}
//		log.escore = hteam.getLostWardScore();
		for(int i = 0;i<defendHeros.size();i++){
			MirrorHero mo = defendHeros.get(i);
			log.elist.add(mo.getRoleId());
		}
		myteam.addTeamLog(log);
		
//		if(iswin){
//			TeamLog log = new TeamLog();
//			log.type = 1;
//			log.myName = player.getName();
//			log.myscore = myteam.getWinAddwardScore();
//			log.idx = strongHoldid;
//			log.warType = warType;
//			for(int i = 0;i<mirrorHeroAttackers.size();i++){
//				MirrorHero mo = mirrorHeroAttackers.get(i);
//				log.mlist.add(mo.getRoleId());
//			}
//
//			log.win = 1;
//
//			String dguid = "";
//			if(guidmap.size() == 1){
//				for(String dkguid : guidmap.values()){
//					dguid = dkguid;
//				}
//
//				RoleEntity drole = allPlayersCache.getPlayerInfo(dguid);
//				if(drole != null){
//					log.eName = drole.getName();
//				}
//			}else{
//				log.eName = "";
//			}
//
//			log.escore = hteam.getLostWardScore();
//			for(int i = 0;i<defendHeros.size();i++){
//				MirrorHero mo = mirrorHeroAttackers.get(i);
//				log.elist.add(mo.getRoleId());
//			}
//			log.lv = myteam.getLevel();
//
//			myteam.addTeamLog(log);
//		}else{
//			TeamLog log = new TeamLog();
//			log.type = 1;
//			log.eName = player.getName();
//			log.escore = myteam.getWinAddwardScore();
//			log.idx = strongHoldid;
//			for(int i = 0;i<mirrorHeroAttackers.size();i++){
//				MirrorHero mo = mirrorHeroAttackers.get(i);
//				log.elist.add(mo.getRoleId());
//			}
//
//			log.win = 0;
//
//			String dguid = "";
//			if(guidmap.size() == 1){
//				for(String dkguid : guidmap.values()){
//					dguid = dkguid;
//				}
//
//				RoleEntity drole = allPlayersCache.getPlayerInfo(dguid);
//				if(drole != null){
//					log.myName = drole.getName();
//				}
//			}else{
//				log.myName = "";
//			}
//
//			log.myscore = hteam.getLostWardScore();
//			for(int i = 0;i<defendHeros.size();i++){
//				MirrorHero mo = mirrorHeroAttackers.get(i);
//				log.mlist.add(mo.getRoleId());
//			}
//			log.lv = hteam.getLevel();
//
//			hteam.addTeamLog(log);
//		}
	}
	// 直接 攻打，或者防守
	private void defendOrAttackFunc(Player player,ArrayNode arr,Integer strongHoldid,Integer warType,List<MirrorHero> mirrorHeroAttackers)
	{
		Team team = null;
		if(warType == 0){
			Team myteam = name2TeamMap.get(player.getTeam().getName());
			team = name2TeamMap.get(myteam.getTeamWarTeamName());
		}else{
			team =  name2TeamMap.get(player.getTeam().getName());
		}

		WarHoldBase base = team.getWarHoldBases(strongHoldid);
		if(base.getBaseHero().size() > 0){
			logger.info("此据点不是空据点,不可占领或防守");
			return;
		}
		int myscore = 0;

		if(warType == 0){
			base.setSign(1);
			if(base.getKoState()<=0){
				Team kmem = name2TeamMap.get(player.getTeam().getName());
				kmem.addDefendEmptySlotRewards(player);
				myscore = kmem.addDefendEmptySlotRewards(player);
				base.setKoState(1);
			}
		}

		List<WarBaseHeroOne> baseHList = base.getBaseHero();

		Team myteam = name2TeamMap.get(player.getTeam().getName());

		for(int i=0;i<arr.size();i++)
		{
			ObjectNode o = (ObjectNode)arr.get(i);
			int index = o.get("index").asInt();

			ArmyHero armyHero = myteam.getArmyHeroByIndex(player.getId(),index);
			armyHero.setStrongID(strongHoldid);
			if(warType == 0){
				armyHero.setheroWhere(1);
			}else{
				armyHero.setheroWhere(0);
			}
			WarBaseHeroOne one = new WarBaseHeroOne();

			one.setDatas(player.getId(), player.getName(), myteam.getName(),armyHero.getHero().getRoleId()+"",armyHero.getHps(),index);
			baseHList.add(one);
		}

		if(warType == 0 && myscore>0){
			TeamLog log = new TeamLog();
			log.type = 2;
			log.warType = warType;
			log.myName = player.getName();
			log.myscore = myscore;
			log.idx = strongHoldid;
			for(int i = 0;i<mirrorHeroAttackers.size();i++){
				MirrorHero mo = mirrorHeroAttackers.get(i);
				log.mlist.add(mo.getRoleId());
			}
			log.win = 1;
			myteam.addTeamLog(log);
		}
	}

	public synchronized void updateDefendPlayerState(Player player,String guid,Integer index)
	{
		Team myteam = name2TeamMap.get(player.getTeam().getName());
		myteam.updateDefendPlayerState(player, guid, index);
	}
	
	private void broadcastChatMessage(int id, String... strs) {
		ObjectNode cmd = DynamicJsonProperty.jackson.createObjectNode();
		cmd.put("tid", id);
		if (strs != null && strs.length > 0) {
			cmd.put("param", DynamicJsonProperty.convertToArrayNode(strs));
		}
		cmd.put("scope", ChatConstants.SCOPE_MIDDLE);
		ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
		node.put("chat", cmd);
		room.sendBroadcast(Events.networkEvent(node));
	}
	
	private void sendAllOnlinePlayerWarState(int id)
	{
		boolean state = false;
		if(id == MessageCode.WAR_TIPS_6270){
			state = true;
		}
		ObjectNode cmd = DynamicJsonProperty.jackson.createObjectNode();
		cmd.put("tid", id);
		cmd.put("state", state);
		ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
		node.put("teamWarOnline", cmd);
		room.sendBroadcast(Events.networkEvent(node));
	}
	
	////////////////////////////////////////////// 军团部队 /////////////////////////////////////////////////////
	
	// 每周日晚上12点清军团中成员的派遣次数
	public void clearTeamMemberPaiCishu()
	{
		for (Team team : name2TeamMap.values()) {
			team.clearTeamMemberPaiCishu();
		}
	}
	
	public void teamWarStatusFunc(Player player)
	{
		Team myteam = name2TeamMap.get(player.getTeam().getName());
		boolean isopen = myteam.isWarOpened();
		ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
		node.put("juntuanstatus", isopen);
		player.sendMessage("teamWarStatus", node);
	}
	
	public void teamWarStateFuncS2C(Player player)
	{
		boolean isopen = false;
		String teamName = player.getTeam().getName();
		if (teamName.equals("")) {
			ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
			node.put("state", isopen);
			player.sendMessage("teamWarst", node);
			return;
		}
		Team myteam = name2TeamMap.get(player.getTeam().getName());
		isopen = myteam.isWarOpened();
		ObjectNode node = DynamicJsonProperty.jackson.createObjectNode();
		node.put("state", isopen);
		player.sendMessage("teamWarst", node);
	}
	
	public void loadPlayerHeroConfig(String guid ,Integer id)
	{
		if(id == 0){
			return;
		}
		RoleEntity role = allPlayersCache.getPlayerInfo(guid);
		if(role == null){
			return;
		}
		PlayerSession ps = room.getSessions().get(guid);
		if(ps != null){ // 在线恢复
			DefaultPlayer dp = (DefaultPlayer)ps.getPlayer();
			if (dp != null) {
				Hero mhero = dp.getHeros().getHero(id.intValue());
				logger.info("loadPlayerHeroConfig==guid="+guid+" " + "id= "+id.intValue()+" hero="+mhero);
				if(mhero == null){
					return;
				}
				if(mhero.isHeroHaveState(HeroState.WENQUAN_STATE)){
					return;
				}
				
				dp.getHeros().removeHerosByID(id);
			}
		}else{ // 离线恢复
			DefaultPlayer dp = objectCacheService.getCache(guid, DefaultPlayer.class);
			if (dp != null) {
				Hero mhero = dp.getHeros().getHero(id.intValue());
				logger.info("loadPlayerHeroConfig==guid="+guid+" " + "id= "+id.intValue()+" hero="+mhero);
				if(mhero == null){
					return;
				}
				if(mhero.isHeroHaveState(HeroState.WENQUAN_STATE)){
					return;
				}
				dp.getHeros().removeHerosByID(id);
				
				objectCacheService.putObject(dp);
				dp = null;
			}
		}
	}
	
	// 军团日志
	public void getTeamLogInfo(Player player)
	{
		Team myteam = name2TeamMap.get(player.getTeam().getName());

		ArrayNode teamLogs = DynamicJsonProperty.jackson.createArrayNode();
		List<TeamLog> log = myteam.getTeamLogs();
		for(int i = 0;i<log.size();i++){
			TeamLog tlog = log.get(i);
			teamLogs.add(tlog.getLog());
		}

		player.sendMessage("getTeamLogInfo", teamLogs);
	}
	
	public void fixRoleTeamNameData()
	{
		long l = System.currentTimeMillis();
		for(Team team : name2TeamMap.values()){
			Map<String, TeamMember> s = team.getMembers();
			for(String guid : s.keySet()){
				RoleEntity role = allPlayersCache.getPlayerInfo(guid);
				if(role != null){
					allPlayersCache.setPlayerTeamName(guid, team.getName());
				}
			}
			team.fixRoleTeamNameData();
//			teamMapper.markUpdate(team);
		}
		logger.info("fixRoleTeamNameData elapse " + (System.currentTimeMillis() - l) + "(ms)");
	}
	
}
