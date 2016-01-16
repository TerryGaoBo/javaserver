package org.greg.resteasy.controller;

import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.context.AppContext;
import io.nadron.event.Events;
import io.nadron.example.lostdecade.LDRoom;
import io.netty.channel.ChannelHandlerContext;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.greg.resteasy.controller.request.Article;
import org.greg.resteasy.pojo.response.Helloworld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.DESPlusManager;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Recharge;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.ActivityConfigManager;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingConfigManager;
import com.dol.cdf.common.config.PayUtil;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.common.entities.GlobalProps;
import com.dol.cdf.common.entities.NoticeProps;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.activity.WorldActivity;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.CombineServer;
import com.jelly.node.cache.FixTeamDBData;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.Naru;
import com.jelly.node.datastore.mapper.NaruMapper;
import com.jelly.node.datastore.mapper.Pay;
import com.jelly.node.datastore.mapper.PayMapper;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.node.datastore.mapper.RoleMapper;
import com.jelly.node.datastore.mapper.TeamMapper;
import com.jelly.player.BuildInstance;
import com.jelly.player.PlayerProperty;
import com.jelly.rank.GameRankMaster;
import com.jelly.team.Team;
import com.jelly.team.TeamManager;
import com.jelly.team.TeamMember;

import edu.emory.mathcs.backport.java.util.Collections;

@Controller
@Path("/")
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	private static final String PAY_CALLBACK_KEY = "Iw!L0Kb51226";
	private static final String GM_REWARD_KEY = "Pq9!j7Di";
	
	LDRoom ldRoom;
	
	private static List<Integer> ignoreCheckUpdateChannels; //IOS专属,用于针对某些特殊版本客户端禁止其从服务器上更新补丁包
	
	static {
		ignoreCheckUpdateChannels = Lists.newArrayList();
//		ignoreCheckUpdateChannels.add(50);
//		ignoreCheckUpdateChannels.add(51); // 开启博人传iOS增量更新
		ignoreCheckUpdateChannels.add(52);
		ignoreCheckUpdateChannels.add(53);
	}
	
	////////////////////////////////////////////新加//////////////////////////////////////////////////////////////////////////////
	
	@GET
	@Path("/gm/deleteTeamDataDB")
	@Produces("application/json;charset=UTF-8")
	public String deleteTeamDataDB()
	{
		logger.info("=============deleteTeamDataDB =============");
		TeamMapper mapper = AppContext.getBean(TeamMapper.class);
		FixTeamDBData.deleteTeamDataDB(mapper);
		return "succ";
	}
	
	@GET
	@Path("/gm/fixTeamDBData")
	@Produces("application/json;charset=UTF-8")
	public String fixTeamDBData(@QueryParam("sid") int sid)
	{
		logger.info("=============fixTeamDBData =============sid="+sid);
		TeamMapper mapper = AppContext.getBean(TeamMapper.class);
		if(sid<=0){
			sid = 1;
		}
		FixTeamDBData.fixTeamData(sid,mapper);
		return "succ";
	}
	
	// 先修改 fixTeamDBData 然后重启服务器在修改这个方法
	@GET
	@Path("/gm/fixRoleTeamNameData")
	@Produces("application/json;charset=UTF-8")
	public String fixRoleTeamNameData()
	{
		logger.info("=============fixRoleTeamNameData =============");
		TeamManager.getSingleton().fixRoleTeamNameData();
		return "succ";
	}
	
	@GET
	@Path("/game/dissoveTeamByPlayerGuid")
	@Produces("application/json;charset=UTF-8")
	public String dissoveTeamByPlayerGuid(@QueryParam("name") String name,@QueryParam("teamName") String teamName)
	{
		logger.info("=============dissoveTeamByPlayerGuid =============");
		if(name == null || teamName == null){
			return "succ";
		}
		
		TeamManager.getSingleton().dissoveTeamByPlayerGuid(name,teamName);
		return "succ";
	}
	
	@GET
	@Path("/game/dissoveTeamByPlayerGuidTwo")
	@Produces("application/json;charset=UTF-8")
	public String dissoveTeamByPlayerGuidTwo(@QueryParam("teamName") String teamName)
	{
		logger.info("=============dissoveTeamByPlayerGuid =============");
		if(teamName == null){
			return "succ";
		}
		
		TeamManager.getSingleton().dissoveTeamByPlayerGuidTwo(teamName);
		return "succ";
	}
	
	@GET
	@Path("/game/clearTeamWealthfunc")
	@Produces("application/json;charset=UTF-8")
	public String clearTeamWealthfunc(@QueryParam("teamName") String teamName,@QueryParam("value") int value)
	{
		logger.info("=============clearTeamWealthfunc =============");
		if(teamName == null){
			return "succ";
		}
		
		TeamManager.getSingleton().clearTeamWealthfunc(teamName,value);
		return "succ";
	}
	
	
	@GET
	@Path("/game/banRoleByGuid")
	@Produces("application/json;charset=UTF-8")
	public String banRoleByGuid(@QueryParam("guid") String guid)
	{
		logger.info("=============banRoleByGuid ============="+guid);
		if(guid == null || guid.equals("")){
			return "succ";
		}
		
		guid = java.net.URLDecoder.decode(guid);
		
		GlobalProps globalProps = getLDroom().getGlobalProps();
		if(globalProps.isBanPlayer(guid)){
		}else{
			globalProps.addBanPlayer(guid);
			logger.info("=============guid ============="+guid);
		}
		
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		RoleEntity role = allPlayersCache.getPlayerInfo(guid);
		if(role == null){
			return "succ:role=null";
		}
		
		return role.getName();
	}
	
	////////////////// ----- 热更新 配置 ---------------///////////////////////
	
	// 1 GameFunction.json
	// 2 Const.json
	// 3 Beast.json
	// 4 Skill.json
	// 5 Role.json RoleGroup.json Training.json 
	// 6 Accessory.json Item.json Material.json ItemGroup.json  ShopItemGroup.json Shop.json
	// 7 DropGroup.json Dig.json
	// 8 Adventure.json
	// 9 Level.json
	// 10 QualityRate.json 
	// 11 QualityRef.json 
	// 12 Building.json BuildingRef.json
	// 13 Arena.json Arenapoint.json
	// 14 Formula.json
	// 15 Quest.json QuestRef.json
	// 16 Recruit.json
	// 17 Exam.json
	// 18 DayReward.json Vip.json Vip_ios.json Recharge.json  Recharge_ios.json Activity.json Catchninja.json NewActivity.json Plan.json
	// 19 Gift.json NewGift.json Plan.json 
	// 20 Text.json
	// 21 NpcTalk.json 
	// 22 War.json 
	// 23 Card.json Card_ios.json 
	// 24 Guild.json GuildWar.json
	// http://localhost:8082/gm/reloadModulesConfigFunc?key=AddIp930idjz1&moduletype=1
	@GET
	@POST
	@Path("/gm/reloadModulesConfigFunc")
	@Produces("application/json;charset=UTF-8")
	public String reloadModulesConfigFunc(@QueryParam("key") String key,@QueryParam("moduletype") int moduletype) {
		// 只支持商店,关卡掉落以及忍者屋抓忍者权重配置表热更新
		
		// 检查操作源是否合法
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "未授权的用户无法执行该操作");
		}
		
		logger.info("======reloadModulesConfigFunc====key="+key+" moduletype="+moduletype);
		
		if(moduletype == 1){
			AllGameConfig.getInstance().gconst.loadConfig();
		}else if(moduletype == 2){
			AllGameConfig.getInstance().beast.loadConfig();
		}else if(moduletype == 3){
			AllGameConfig.getInstance().skills.loadConfig();
		}else if(moduletype == 4){
			AllGameConfig.getInstance().characterManager.loadConfig();
		}else if(moduletype == 5){
			AllGameConfig.getInstance().items.loadReloadConfig();
		}else if(moduletype == 6){
			AllGameConfig.getInstance().drops.loadConfig();
		}else if(moduletype == 7){
			AllGameConfig.getInstance().adventures.loadConfig();
		}else if(moduletype == 8){
			AllGameConfig.getInstance().levels.loadConfig();
		}else if(moduletype == 9){
			AllGameConfig.getInstance().rate.loadConfig();
		}else if(moduletype == 10){
			AllGameConfig.getInstance().qref.loadConfig();
		}else if(moduletype == 11){
			AllGameConfig.getInstance().buildings.loadConfig();
		}else if(moduletype == 12){
			AllGameConfig.getInstance().arena.loadConfig();
		}else if(moduletype == 13){
			AllGameConfig.getInstance().formula.loadConfig();
		}else if(moduletype == 14){
			AllGameConfig.getInstance().quests.loadConfig();
		}else if(moduletype == 15){
			AllGameConfig.getInstance().recruits.loadConfig();
		}else if(moduletype == 16){
			AllGameConfig.getInstance().exams.loadConfig();
		}else if(moduletype == 17){
			AllGameConfig.getInstance().activitys.loadConfig();
			getLDroom().getWorldActivity().resetActivitys();
		}else if(moduletype == 18){
			AllGameConfig.getInstance().gifts.loadConfig();
			getLDroom().getWorldActivity().resetActivitys();
		}else if(moduletype == 19){
			AllGameConfig.getInstance().text.loadConfig();
		}else if(moduletype == 20){
			AllGameConfig.getInstance().wars.loadConfig();
		}else if(moduletype == 21){
			AllGameConfig.getInstance().cards.loadConfig();
		}else if(moduletype == 22){
			AllGameConfig.getInstance().teams.loadConfig();
		}else if(moduletype == 23){
			AllGameConfig.getInstance().teams.loadConfig();
		}else if(moduletype == 9999){
		}
		
		return toJSON(0, "操作完成");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//http://localhost:8082/gm/resetClanTeamData 本地测试
	@GET
	@Path("/gm/resetClanTeamData")
	@Produces("application/json")
	public String resetClanTeamData() throws Exception { 
		AllPlayersCache allPlayersCache = (AllPlayersCache) AppContext.getBean("allPlayersCache");
		
		//-------------------------------------------------
		
		Map<String, Team> mapteam = TeamManager.getSingleton().getAllTeams();
		for (Team team : mapteam.values()) {
			Map<String, TeamMember> members = team.getMembers();
			for(TeamMember m : members.values())
			{
				String guid = m.getGuid();
				if(guid == "" || guid == null){
					continue;
				}
				RoleEntity role = allPlayersCache.getPlayerInfo(guid);
				if(role != null){
					allPlayersCache.setPlayerTeamName(guid, team.getName());
				}
			}
		}
		
		logger.error("-------------重置 resetClanTeamData  RoleEntity 中 军团名字为null 成功！！！！");
		logger.error("-------------重置成功！");
		logger.error("-------------重置成功！");
		
		return "succ";
	}
	
	/**
	 *  屏蔽支付平台，根据渠道号屏蔽支付方式
	 *  channelID : 渠道id
	 *  payType : 需要屏蔽的支付类型 （ 1 google play , 2 ios pay ,3 信用卡 ）  需要追加新的，以此在往后追加
	 *  isShield : 开启和屏蔽 (0 屏蔽，1 开启)
	 *  // game/gameShedldPayPlatformQuDao?channelID=999051&payType=1&isShield=0
	 */
	@GET
	@Path("/game/gameShedldPayPlatformQuDao")
	@Produces("application/json;charset=UTF-8")
	public String gameShedldPayPlatformQuDao(@QueryParam("channelID") String channelID,@QueryParam("payType") int payType,@QueryParam("isShield") int isShield) throws Exception
	{
		logger.info("=============shieldpayplatform =============");
		logger.info("需要屏蔽某一个渠道的支付方式");
		logger.info("渠道ID==" + channelID);
		logger.info("支付类型=" + payType);
		logger.info("是否屏蔽=" + isShield);
		logger.info("==========================");
		
		if(channelID == null || payType == 0){
			return "succ";
		}
		
		
		GlobalProps globalProps = getLDroom().getGlobalProps();
		globalProps.shieldPayFunc(channelID,payType,isShield);
		
		String desc = "<u>需要屏蔽某一个渠道的支付方式</u>" + "\n" + "<li>渠道ID==" + channelID + "</li>" + "\n" + "<li>支付类型=" + payType + "</li>"+ "\n"+ "<li>是否屏蔽="+isShield +"</li>" ;
		
		return desc;
	}
	
	// 修改军团部队中的数据 
	// 转移军团部队中以前的数据存储方式为新的存储方式
//	@GET
//	@Path("/game/teamArmyDataChangeFunc")
//	@Produces("application/json;charset=UTF-8")
//	public String teamArmyDataChangeFunc()
//	{
//		logger.info("=============teamArmyDataChangeFunc =============");
//		TeamManager.getSingleton().teamArmyDataChangeFunc();
//		return "succ";
//	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 如果军团数据丢失，清除玩家军团信息
	 */
	@GET
	@Path("/gm/resetClan")
	@Produces("application/json")
	public String resetClan() throws Exception { 
		AllPlayersCache allPlayersCache = (AllPlayersCache) AppContext.getBean("allPlayersCache");
		ObjectCacheService objectCacheService = (ObjectCacheService) AppContext.getBean("objectCacheService");
		Collection<RoleEntity> allRoles =  allPlayersCache.getAllPlayerInfo();
		List<String> allTeamName = TeamManager.getSingleton().getAllTeamNames();
		Set<String> lostTeam = new HashSet<String>();
		List<String> ppName = new ArrayList<String>();
		for (RoleEntity role : allRoles) {
			String playerTeamName = role.getTeamName();
			if (playerTeamName == null || playerTeamName.equals("")) {
				continue;
			}
			if (!allTeamName.contains(playerTeamName)) {
				logger.error("玩家名称={},丢失的军团={}",role.getName() , playerTeamName);
				setPlayerTeamInfoEmpty(allPlayersCache, objectCacheService,
						lostTeam, ppName, role, playerTeamName);
			}
		}
		logger.error("-------------重置成功！");
		logger.error("-------------重置成功！");
		logger.error("-------------重置成功！");
		for(String t : lostTeam) {
			logger.error("lost clan name:" + t);
		}
		for(String t : ppName) {
			logger.error("player name:" + t);
		}
		return "succ";
	}
	
	/**
	 * 玩家军团相关数据清除
	 * @param allPlayersCache
	 * @param objectCacheService
	 * @param lostTeam
	 * @param ppName
	 * @param role
	 * @param playerTeamName
	 */
	private void setPlayerTeamInfoEmpty(AllPlayersCache allPlayersCache,
			ObjectCacheService objectCacheService, Set<String> lostTeam,
			List<String> ppName, RoleEntity role, String playerTeamName) {
		lostTeam.add(playerTeamName);
		ppName.add(role.getName());
		DefaultPlayer dp = new DefaultPlayer();
		dp.setId(role.getGuid());
		DefaultPlayer player = objectCacheService.getObject(dp, DefaultPlayer.class);
		player.getTeam().resetClan(playerTeamName);
		objectCacheService.putObject(player);
		role.setTeamName("");
		allPlayersCache.updateRolePropAndSave(player, role);
	}
	
	/**
	 * 如果军团数据丢失，清除玩家军团信息
	 */
	@GET
	@Path("/gm/resetPlayDataNotInClan")
	@Produces("application/json")
	public String resetPlayDataNotInClan() throws Exception { 
		AllPlayersCache allPlayersCache = (AllPlayersCache) AppContext.getBean("allPlayersCache");
		ObjectCacheService objectCacheService = (ObjectCacheService) AppContext.getBean("objectCacheService");
		Collection<RoleEntity> allRoles =  allPlayersCache.getAllPlayerInfo();
		Set<String> lostTeam = new HashSet<String>();
		List<String> ppName = new ArrayList<String>();
		for (RoleEntity role : allRoles) {
			String playerTeamName = role.getTeamName();
			if (playerTeamName == null || playerTeamName.equals("")) {
				continue;
			}
			Team team = TeamManager.getSingleton().getTeamByName(playerTeamName);
			//军团存在
			if(team != null){
				//军团成员却没有我
				if(team.getTeamMember(role.getGuid()) == null){
					logger.error("玩家名称={},没有我的军团={}",role.getName() , playerTeamName);
					setPlayerTeamInfoEmpty(allPlayersCache, objectCacheService,
							lostTeam, ppName, role, playerTeamName);
				}
			}
		}
		logger.error("-------------resetPlayDataNotInClan重置成功！");
		for(String t : lostTeam) {
			logger.error("resetPlayDataNotInClan lost in clan name:" + t);
		}
		for(String t : ppName) {
			logger.error("resetPlayDataNotInClan player name:" + t);
		}
		return "succ";
	}
	
	
	/**
	 * 合服时处理玩家名字
	 */
	@GET
	@POST
	@Path("/gm/resetPlayerName")
	@Produces("application/json")
	public String resetPlayerName() throws Exception { 
		AllPlayersCache allPlayersCache = (AllPlayersCache) AppContext.getBean("allPlayersCache");
		Map<String, String> map = allPlayersCache.getExistedName();
		for (String guid : map.keySet()) {
			RoleEntity role = allPlayersCache.getPlayerInfo(guid);
			role.setName(role.getName() + "-" + role.getNet());
			allPlayersCache.getMapper().update(role);
			logger.info(role.getName());
		}
		logger.info("finished! all size={}", map.size());
		return "succ";
	}
	
	/**
	 * 合服时处理军团名字
	 */
	@GET
	@POST
	@Path("/gm/resetClanName")
	@Produces("application/json")
	public String resetClanName(@QueryParam("maxSid") int maxSid) throws Exception { 
		TeamMapper mapper = AppContext.getBean(TeamMapper.class);
		CombineServer.combinTeam(maxSid, mapper);
		return "succ";
	}
	
	/**
	 * 合服前统计所有表数据总数
	 */
	@GET
	@POST
	@Path("/gm/getAllTableCount")
	@Produces("application/json")
	public String getAllTableCount(@QueryParam("maxSid") int maxSid) throws Exception { 
		CombineServer.getAllTableCount(maxSid);
		return "succ";
	}
	
	/**
	 * 游戏版本号
	 */
	@GET
	@Path("/game/version")
	@Produces("application/json")
	public String gameVersion(@QueryParam("v") String v,
			@QueryParam("ch") String ch, @Context ChannelHandlerContext ctx)
			throws Exception {
		logger.info("v=" + v);
		if (ContextConfig.isOfficalEnv()) {
			if (!ContextConfig.GAME_OPEN) {
				String ip = StringUtils.substringBetween(ctx.channel()
						.remoteAddress().toString(), "/", ":");
				if (!ContextConfig.checkIp(ip)) {
					logger.error("非法IP " + ip);
					return v + "#0";
				}
			}
		}
		
		if (AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE
				&& ch != null
				&& ignoreCheckUpdateChannels.contains(Integer.parseInt(ch))) {
			return v + "#" + 0;
		} else {
			Map<String, String> versions = AllGameConfig.getInstance().versionMap;
			String key = v + "_" + AllGameConfig.getInstance().maxVersion;
			String size = versions.get(key);
			if (size == null) {
				size = "0";
			}
			String ret = AllGameConfig.getInstance().maxVersion + "#" + size;
			logger.info(ret);
			return ret;
		}
	}
	
	
	
	@GET
	@POST
	@Path("/gm/getOnlineUsers")
	@Produces("application/json;charset=UTF-8")
	public String getOnlineUsers(@QueryParam("key") String key) {
		Map<String, PlayerSession> pss = getLDroom().getSessions();
		ArrayNode arr = DynamicJsonProperty.jackson.createArrayNode();
		for (PlayerSession ps : pss.values()) {
			Player player = ps.getPlayer();
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			info.put("id", player.getId());
			info.put("name", player.getName());
			info.put("level", player.getProperty().getLevel());
			info.put("vipLevel", player.getProperty().getVipLv());
			info.put("vipSr", player.getProperty().getVipScore());
			info.put("ch", player.getProperty().getCh());
			info.put("clientInfo", player.getClientInfo());
			info.put("net", player.getProperty().getNet());
			arr.add(info);
		}
		return toJSON(0, arr);
	}
	
	@GET
	@POST
	@Path("/gm/getOnlineUserCount")
	@Produces("application/json;charset=UTF-8")
	public String getOnlineUserCount(@QueryParam("key") String key) {
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
		info.put("count", getLDroom().getSessions().size());
		return toJSON(0, info);
	}
	
	/**
	 * 切换日志开发
	 */
	@GET
	@Path("/game/switchLogStatus")
	@Produces("application/json;charset=UTF-8")
	public String closeLog() throws Exception {
		logger.info("当前open=" +LogUtil.isOpen);
		LogUtil.isOpen = !LogUtil.isOpen;
		return "open="+LogUtil.isOpen;
	}
	
	/**
	 * 重新加载version.txt
	 */
	@GET
	@Path("/game/updateVersion")
	@Produces("application/json;charset=UTF-8")
	public String updateVersion() throws Exception {
		AllGameConfig.getInstance().initVersions();
		return "succ";
	}
	
	/**
	 * 各渠道充值回调 统一入口
	 * @param orderId
	 * @param roleName
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@POST 
	@GET
	@Path("/pay/payCallback")
	@Produces("application/json")
	public String payCallback(@QueryParam(value = "orderId") String orderId, @QueryParam(value = "roleName") String roleName, 
			@QueryParam(value = "itemId") String itemId, @QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(orderId) || isEmpty(roleName) || isEmpty(itemId) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = orderId + roleName + itemId + PAY_CALLBACK_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		ActivityConfigManager config = AllGameConfig.getInstance().activitys;
		Recharge recharge = config.getRecharge(itemId);
		if (recharge == null) {
			return toJSON(0, "itemId error");
		}
		
		if (roleName == null || roleName.equals("")) {
			return toJSON(0, "roleName is null");
		}
		
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if(guid == null) {
			logger.info("player not exist! roleName=" + roleName);
			return toJSON(0, "player not exist");
		}
		
		//给玩家加金币
		PlayerSession playerSession = getLDroom().getSessions().get(guid);
		if (playerSession != null) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("itemId", itemId);
			msg.put("orderId", orderId);
			obj.put("payAddGold", msg);
			playerSession.onEvent(Events.event(obj));
			logger.info("玩家在线，发送添加金币消息");
		} else {
			DefaultPlayer player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
			player.addPayment(itemId, orderId, getLDroom().getWorldActivity());
			getLDroom().getObjectCacheService().putObject(player);
			logger.info("玩家不在线，添加金币成功");
		}
		
		PayUtil.getInstance().verifyOrder(orderId);
		return "OK";
	}

	private LDRoom getLDroom() {
		 if(ldRoom == null ) {
			 ldRoom = AppContext.getBean(LDRoom.class);
		 }
		 return ldRoom;
	}
	
	/**
	 * 快用充值回调
	 */
	@POST 
	@GET
	@Path("/pay/payKYCallback")
	@Produces("application/json")
	public String payKYCallback(@QueryParam(value = "orderId") String orderId, @QueryParam(value = "userId") String userId, 
			@QueryParam(value = "itemId") String itemId,@QueryParam(value = "fee") String fee,@QueryParam(value = "sid") String sid, @QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(orderId) || isEmpty(userId) || isEmpty(itemId) || isEmpty(fee) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = orderId + userId + itemId + fee + sid + PAY_CALLBACK_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		ActivityConfigManager config = AllGameConfig.getInstance().activitys;
		Recharge recharge = config.getRecharge(itemId);
		if (recharge == null) {
			return toJSON(0, "itemId error");
		}
		float realFee = Float.parseFloat(fee); //快用玩家实际花费的钱,可能和道具价格不一致
		int exchangeGold = 0;					
		if (realFee != recharge.getRmb()) {
			exchangeGold = (int)(realFee*10);
		}
		String guid = DefaultPlayer.genPlayerGuid(userId, "99906", sid);
		//给玩家加金币
		PlayerSession playerSession = getLDroom().getSessions().get(guid);
		if (playerSession != null) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("itemId", itemId);
			msg.put("orderId", orderId);
			if (exchangeGold > 0) {
				msg.put("exchangeGold", exchangeGold);
			}
			obj.put("payAddGold", msg);
			playerSession.onEvent(Events.event(obj));
			PayUtil.getInstance().verifyOrder(orderId);
			logger.info("玩家在线，发送添加金币消息");
			return "OK";
		} else {
			DefaultPlayer player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
			if (player == null) {
				return "player not exists";
			}
			if (exchangeGold > 0) {
				player.addKYPayment(orderId, getLDroom().getWorldActivity(), exchangeGold);
			} else {
				player.addPayment(itemId, orderId, getLDroom().getWorldActivity());
			}
			
			getLDroom().getObjectCacheService().putObject(player);
			PayUtil.getInstance().verifyOrder(orderId);
			logger.info("玩家不在线，添加金币成功");
			return "OK";
		}
		
		
	}
	
	/**
	 * 设置gameopen
	 */
	@GET
	@POST
	@Path("/game/server/switchGameOpen")
	@Produces("application/json;charset=UTF-8")
	public String switchGameOpen(@QueryParam("key") String key, @QueryParam("s") int s,@Context ChannelHandlerContext ctx) throws Exception {
		if (!ContextConfig.ADD_IP_KEY.equals(key)) {
			logger.error("gameopen wrong key:" + key);
			return toJSON(0, "wrong key");
		}
		
		String ip = StringUtils.substringBetween(ctx.channel().remoteAddress().toString(), "/", ":");
		if (!ContextConfig.checkIp(ip)) {
			logger.error("gameopen wrong ip:" + ip);
			return toJSON(0, "wrong ip");
		}
		if (s == 1) {
			ContextConfig.GAME_OPEN = true;
		} else if (s == 2) {
			ContextConfig.GAME_OPEN = false;
		}
		return toJSON(1, String.valueOf(ContextConfig.GAME_OPEN));
	}
	
	/**
	 * 新增IP
	 */
	@GET
	@POST
	@Path("/game/server/addip")
	@Produces("application/json;charset=UTF-8")
	public String addIP(@QueryParam("key") String key, @Context ChannelHandlerContext ctx) throws Exception {
		if (!ContextConfig.ADD_IP_KEY.equals(key)) {
			logger.error("changeServer wrong key:" + key);
			return toJSON(0, "key error");
		}
		
		String ip = StringUtils.substringBetween(ctx.channel().remoteAddress().toString(), "/", ":");
		ContextConfig.addIp(ip);
		return toJSON(1, "succ");
	}
	
	/**
	 * 手动输入或删除IP
	 */
	@GET
	@POST
	@Path("/game/server/inputIP")
	@Produces("application/json;charset=UTF-8")
	public String inputIP(@QueryParam("key") String key, @QueryParam("ip") String ip, @QueryParam("rm") int rm, @Context ChannelHandlerContext ctx) throws Exception {
		if (!ContextConfig.ADD_IP_KEY.equals(key)) {
			logger.error("changeServer wrong key:" + key);
			return toJSON(0, "error");
		}
		
		if (rm == 1) {
			ContextConfig.rmIp(ip);
		} else {
			ContextConfig.addIp(ip);
		}
		return toJSON(1, ContextConfig.ipList.toString());
	}
	
	/**
	 * GM直接发奖
	 */
	@POST 
	@GET
	@Path("/gm/reward")
	@Produces("application/json")
	public String gmReward(@QueryParam(value = "data") String data,  @QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(data) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = data + GM_REWARD_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		
		JsonNode json = null;
		try {
			//data = URLDecoder.decode(data, "utf-8");
			json = DynamicJsonProperty.jackson.readTree(data);
		} catch (Exception e) {
			logger.error("数据解析错误", e);
			return toJSON(0, "param error");
		}
		String roleName = json.get("roleName").asText();
		roleName = URLDecoder.decode(roleName, "utf-8");
 
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if(guid == null) {
			return toJSON(0, "player not exist");
		}
		
		//给玩家加金币
		PlayerSession playerSession = getLDroom().getSessions().get(guid);
		if (playerSession != null) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("items", json.get("list"));
			obj.put("gmReward", msg);
			playerSession.onEvent(Events.event(obj));
			logger.info("玩家在线，发送奖励消息");
		} else {
			DefaultPlayer player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
			ArrayNode rewardArray = (ArrayNode)json.get("list");
			VariousItemEntry[] awards = new VariousItemEntry[rewardArray.size()];
			for (int i=0; i<rewardArray.size(); i++) {
				ArrayNode reward = (ArrayNode)rewardArray.get(i);
				VariousItemEntry item = new VariousItemEntry(reward.get(0).asText(), reward.get(1).asInt());
				awards[i] = item;
			}
			VariousItemUtil.doBonus(player, awards, LogConst.GM_GIVE, true);
			getLDroom().getObjectCacheService().putObject(player);
			logger.info("玩家不在线，添加奖励成功");
		}
		
		return toJSON(1, "");
	}
	
	/**
	 * GM通过邮件发奖
	 */
	@POST 
	@GET
	@Path("/gm/rewardByMail")
	@Produces("application/json")
	public String gmRewardByMail(@QueryParam(value = "data") String data,  @QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(data) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = data + GM_REWARD_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		
		JsonNode json = null;
		try {
			//data = URLDecoder.decode(data, "utf-8");
			json = DynamicJsonProperty.jackson.readTree(data);
		} catch (Exception e) {
			logger.error("数据解析错误", e);
			return toJSON(0, "param error");
		}
		String roleName = json.get("roleName").asText();
		roleName = URLDecoder.decode(roleName, "utf-8");
 
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if(guid == null) {
			return toJSON(0, "player not exist");
		}
		
		//给玩家加金币
		PlayerSession playerSession = getLDroom().getSessions().get(guid);
		if (playerSession != null) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("roleName", json.get("roleName"));
			msg.put("rewards", json.get("rewards"));
			msg.put("title", json.get("title"));
			msg.put("content", json.get("content"));
			obj.put("gmRewardByMail", msg);
			playerSession.onEvent(Events.event(obj));
			logger.info("玩家在线，发送奖励消息 - " + roleName);
		} else {
			DefaultPlayer player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
			player.getMail().addSysItemMail(json.get("rewards").asText(), json.get("title").asText(), json.get("content").asText());
			getLDroom().getObjectCacheService().putObject(player);
			logger.info("玩家不在线，添加奖励成功 - " + roleName);
		}
		
		return toJSON(1, "");
	}
	
	
	/**
	 * 维护补偿邮件
	 * @param orderId
	 * @param roleName
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@POST 
	@GET
	@Path("/gm/rewardMail")
	@Produces("application/json")
	public String gmRewardMail(@QueryParam(value = "data") String data,  @QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(data) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = data + GM_REWARD_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		
		JsonNode json = null;
		try {
			json = DynamicJsonProperty.jackson.readTree(data);
		} catch (Exception e) {
			logger.error("数据解析错误", e);
			return toJSON(0, "param error");
		}
		String titleId = json.get("titleId").asText();
		String contentId = json.get("contentId").asText();
		JsonNode rewardNode = json.get("reward");
		String reward = "";
		if (rewardNode != null) {
			reward = rewardNode.asText();
		}
		
		logger.info("titleId={}, contentId={}, reward={}", titleId, contentId, reward);
		
		//给玩家发邮件
		GlobalProps globalProps = getLDroom().getGlobalProps();
		globalProps.addMail(titleId, contentId, reward);
		return toJSON(1, "");
	}

	String title = "停机维护补偿";
	String content = "尊敬的村长大人，为了改善游戏环境，我们已经将忍者世界进行了全面升级。为了对您的耐心等待表示敬意，五影大会特向您赠送100金币，期待您在忍者世界中最拉轰的表现！";
	String reward = "<gold;100>";
	/**
	 * auto Script GM补偿邮件
	 */
	@POST 
	@GET
	@Path("/gm/rewardMailScript/{key}")
	@Produces("application/json")
	public String gmRewardMailScript(@PathParam(value = "key") String key, @QueryParam(value = "isReward") String isReward, @Context ChannelHandlerContext ctx) throws Exception { 
		if (isEmpty(key) || !ContextConfig.GM_REWARD_KEY.equals(key)) {
			return toJSON(0, "param error");
		}
		if("1".equals(isReward)) {
			GlobalProps globalProps = getLDroom().getGlobalProps();
			globalProps.addMail(title, content, reward);
		}
		ContextConfig.GAME_OPEN = true;
		String ip = StringUtils.substringBetween(ctx.channel().remoteAddress().toString(), "/", ":");
		ContextConfig.addIp(ip);
		return "OK";
	}
	
	/**
	 * 取消IP检查
	 */
	@POST 
	@GET
	@Path("/gm/open/{key}")
	@Produces("application/json")
	public String cancalCheckIp(@PathParam(value = "key") String key) throws Exception { 
		if (isEmpty(key) || !ContextConfig.Game_OPEN_KEY.equals(key)) {
			return toJSON(0, "param error");
		}
		
		ContextConfig.GAME_OPEN = true;
		return "OK";
	}
	
	/**
	 * 更改玩家vip积分
	 */
	@POST 
	@GET
	@Path("/gm/player/vipScore")
	@Produces("application/json")
	public String addVipScore(@QueryParam(value = "data") String data,  @QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(data) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = data + GM_REWARD_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		
		JsonNode json = null;
		try {
			json = DynamicJsonProperty.jackson.readTree(data);
		} catch (Exception e) {
			logger.error("数据解析错误", e);
			return toJSON(0, "param error");
		}
		String roleName = json.get("roleName").asText();
		roleName = URLDecoder.decode(roleName, "utf-8");
 
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if(guid == null) {
			return toJSON(0, "player not exist");
		}
		
		PlayerSession playerSession = getLDroom().getSessions().get(guid);
		if (playerSession != null) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("vipScore", json.get("vipScore").asInt());
			obj.put("gmAddVipScore", msg);
			playerSession.onEvent(Events.event(obj));
			logger.info("玩家在线，添加vip积分成功");
		} else {
			DefaultPlayer player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
			player.getProperty().addVipScore(player, json.get("vipScore").asInt());
			getLDroom().getObjectCacheService().putObject(player);
			logger.info("玩家不在线，添加vip积分成功");
		}
		return toJSON(1, "");
	}

	/**
	 * 更改玩家等级
	 */
	@POST 
	@GET
	@Path("/gm/player/lv")
	@Produces("application/json")
	public String setPlayerLevel(@QueryParam(value = "data") String data,  @QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(data) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = data + GM_REWARD_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		
		JsonNode json = null;
		try {
			json = DynamicJsonProperty.jackson.readTree(data);
		} catch (Exception e) {
			logger.error("数据解析错误", e);
			return toJSON(0, "param error");
		}
		String roleName = json.get("roleName").asText();
		roleName = URLDecoder.decode(roleName, "utf-8");
 
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if(guid == null) {
			return toJSON(0, "player not exist");
		}
		
		PlayerSession playerSession = getLDroom().getSessions().get(guid);
		if (playerSession != null) {
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			ObjectNode msg = DynamicJsonProperty.jackson.createObjectNode();
			msg.put("lv", json.get("lv").asInt());
			obj.put("gmSetLevel", msg);
			playerSession.onEvent(Events.event(obj));
			logger.info("玩家在线，设置等级成功");
		} else {
			DefaultPlayer player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
			player.getProperty().setLevel(json.get("lv").asInt());
			player.getProperty().setExp(0);
			logger.info("玩家不在线，设置等级成功");
		}
		return toJSON(1, "");
	}
	
	@GET
	@Path("/kick/{key}")
	@Produces("application/json")
	public String kickPlayers(@PathParam(value = "key") String key) throws Exception { 
		if(getLDroom() != null && ContextConfig.KICK_KEY.equals(key)) {
			getLDroom().kickRoomAllPlayers();
			getLDroom().getWorldActivity().shutdown();
			GameRankMaster.getInstance().saveRank();
			DESPlusManager.getInstance().writeUsedKeys();
			TeamManager.getSingleton().saveAllTeams(true);
			return "OK";
		}
		return "FAIL";
	}
	
	@GET
	@Path("/gm/calcRank")
	@Produces("application/json")
	public String calcRank(@QueryParam("key") String key) throws Exception { 
		if(getLDroom() != null && ContextConfig.GM_REWARD_KEY.equals(key)) {
			GameRankMaster.getInstance().caculateTopList();
			return "OK";
		}
		return "FAIL";
	}
	
	@GET
	@Path("/gm/debug/{key}")
	@Produces("application/json")
	public String openDebugLog(@PathParam(value = "key") String key,@QueryParam(value = "open") int open) throws Exception { 
		if(getLDroom() != null && ContextConfig.GM_REWARD_KEY.equals(key)) {
			if(open == 1) {
				ContextConfig.DEBUG_LOG_OPEN = true;
			}else {
				ContextConfig.DEBUG_LOG_OPEN = false;
			}
			return "OK";
		}
		return "FAIL";
	}
	
	@GET
	@Path("/give")
	@Produces("application/json")
	public String giveItem(@QueryParam(value = "key") String key, @QueryParam(value = "name") String name,@QueryParam(value = "type") String type, @QueryParam(value = "count") int count) throws Exception { 
		if(getLDroom() != null && "dre@m@j$lly".equals(key)) {
			getLDroom().gmGiveItem(type, count, name);
			logger.info("*****GM give Item: name:{},type{},count:{}********",name,type,count);
			return "OK";
		}
		return "FAIL";
	}
	
	@GET
	@Path("/act/payReward")
	@Produces("application/json")
	public String payReward(@QueryParam(value = "key") String key) throws Exception { 
		if(getLDroom() != null && "dre@m@j$lly".equals(key)) {
			WorldActivity.startActivity(18);
			PayMapper payMapper = AppContext.getBean(PayMapper.class);
			List<Pay> loadAll = payMapper.loadAll();
			for (Pay pay : loadAll) {
				getLDroom().getWorldActivity().addPayValue(pay.getGuid(), pay.getRmb()*10);
			}
			WorldActivity.stopActivity(18);
			return "OK";
		}
		return "FAIL";
	}
	
	@GET
	@POST
	@Path("/gm/deleteHero")
	@Produces("application/json")
	public String deleteHero(@QueryParam(value = "key") String key, @QueryParam(value = "name")String roleName, @QueryParam(value = "rid")String rid ) throws Exception { 

		if(!"dre@m@j$lly".equals(key)) {
			return toJSON(0, "key error");
		}
		roleName = URLDecoder.decode(roleName, "utf-8");
 
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if(guid == null) {
			return toJSON(0, "player not exist");
		}
		
		PlayerSession playerSession = getLDroom().getSessions().get(guid);
		if (playerSession != null) {
			playerSession.getPlayer().getHeros().removeHeroByRid( playerSession.getPlayer(),Integer.parseInt(rid));
			logger.info("删除英雄成功");
		} else {
			DefaultPlayer player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
			player.getHeros().removeHeroByRid(player,Integer.parseInt(rid));
			getLDroom().getObjectCacheService().putObject(player);
			logger.info("删除英雄成功");
		}
		return toJSON(1, "");
	}
	
	@GET
	@POST
	@Path("/log/filter")
	@Produces("application/json")
	public String payReward(@QueryParam(value = "open") int open,@QueryParam(value = "filter") int filter) throws Exception { 
		if (open == 1) {
			LogUtil.isOpen = true;
		} else {
			LogUtil.isOpen = false;
		}
		if (filter == 1) {
			LogUtil.logFilter = true;
		} else {
			LogUtil.logFilter = false;
		}
		return "FAIL";
	}
	
	@GET
	@POST
	@Path("/gm/banPlayer")
	@Produces("application/json")
	public String banPlayer(@QueryParam(value = "data") String data,@QueryParam(value = "sign") String sign) throws Exception { 
		if (isEmpty(data) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}
		
		String code = data + GM_REWARD_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (! mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}
		
		JsonNode json = null;
		try {
			json = DynamicJsonProperty.jackson.readTree(data);
		} catch (Exception e) {
			logger.error("数据解析错误", e);
			return toJSON(0, "param error");
		}
		String roleName = json.get("roleName").asText();
		roleName = URLDecoder.decode(roleName, "utf-8");
		int oper = json.get("oper").asInt();//1-封号 2-禁言 3-踢下线
		int shift = json.get("shift").asInt();//1-封 0-解封
		
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if (guid == null) {
			return toJSON(0, "player not exist");
		}
		
		if (oper == 1) {
			if(shift == 1) { //1-封号
				boolean result =  getLDroom().banPlayer(roleName);
				return toJSON(1, String.valueOf(result));
			}else if(shift == 0){
				boolean result =  getLDroom().liftPlayer(roleName);
				return toJSON(1, String.valueOf(result));
			}
		} else if (oper == 2) { // 2-禁言
			
		} else if (oper == 3) { //3-踢下线
			
		}
		
		return toJSON(0, "unknown error");
		
	}
	
	@GET
	@POST
	@Path("/gm/getPlayerInfo")
	@Produces("application/json")
	public String getPlayerInfo(@QueryParam(value = "data") String data,
			@QueryParam(value = "sign") String sign) throws Exception {
		if (isEmpty(data) || isEmpty(sign)) {
			return toJSON(0, "param error");
		}

		String code = data + GM_REWARD_KEY;
		String mySign = DigestUtils.md5Hex(code);
		if (!mySign.equals(sign)) {
			logger.error("sign=" + sign + " mySign=" + mySign + " code=" + code);
			return toJSON(0, "sign error");
		}

		JsonNode json = null;
		try {
			json = DynamicJsonProperty.jackson.readTree(data);
		} catch (Exception e) {
			logger.error("数据解析错误", e);
			return toJSON(0, "param error");
		}
		String roleName = json.get("roleName").asText();
		roleName = URLDecoder.decode(roleName, "utf-8");
		
		AllPlayersCache allPlayersCache = getLDroom().getAllPlayersCache();
		String guid = allPlayersCache.getPlayerIdByName(roleName);
		if (guid == null) {
			return toJSON(0, "player not exist");
		}
		
		Player player = null;
		PlayerSession ps = getLDroom().getSessions().get(guid);
		if (ps != null) {
			player = ps.getPlayer();
		} else {
			player = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
		}
		if (player != null) {
			ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();
			PlayerProperty pp = player.getProperty();
			info.put("gold", pp.getGold());
			info.put("silver", pp.getSilver());
			info.put("coin", pp.getCoin());
			info.put("lv", pp.getLevel());
			info.put("exp", pp.getExp());
			info.put("userId", pp.getUserId());
			info.put("vipSr", pp.getVipScore());
			info.put("vipLv", pp.getVipLv());
			info.put("spoint", pp.getSpoint());
			info.put("ch", pp.getCh());
			info.put("ft", pp.getFirstOnlineTime());
			info.put("lt", pp.getLastOnlineTime());
			info.put("ban", pp.getBanPlayerLv() > 0 ? 1 : 0);
			info.put("pow", player.getHeros().getPlayerPower());
			return toJSON(1, info.toString());
		}
		return toJSON(0, "unknown error");
	}
	
	@POST
	@GET
	@Path("/gm/getPlayerInfo2")
	@Produces("application/json")
	public String getPlayerInfo2(
			@QueryParam(value = "roleName") String roleName,
			@QueryParam(value = "key") String key) throws Exception {

		if ((AllGameConfig.getInstance().env != RuntimeEnv.OTHER && AllGameConfig
				.getInstance().env != RuntimeEnv.TEST)
				&& (isEmpty(key) || !ContextConfig.GM_REWARD_KEY.equals(key))) {
			return toJSON(0, "fatal error!");
		}

		roleName = URLDecoder.decode(roleName, "utf8");

		LDRoom room = getLDroom();
		String guid = room.getAllPlayersCache().getPlayerIdByName(roleName);
		if (guid == null) {
			return toJSON(1, "not exist player!");
		}

		Player player = null;
		PlayerSession ps = room.getSessions().get(guid);
		if (ps != null) {
			player = ps.getPlayer();
		} else {
			player = room.getObjectCacheService().getCache(guid,
					DefaultPlayer.class);
			if (player == null) {
				return toJSON(1, "not exist player!");
			}
		}

		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();

		info.put("id", guid);
		info.put("name", player.getName());
		info.put("level", player.getProperty().getLevel());
		info.put("exp", player.getProperty().getExp());
		info.put("gold", player.getProperty().getGold());
		info.put("silver", player.getProperty().getSilver());
		info.put("coin", player.getProperty().getCoin());
		info.put("userId", player.getProperty().getUserId());
		info.put("clientInfo", player.getClientInfo());
		info.put("vipLv", player.getProperty().getVipLv());
		info.put("vipSr", player.getProperty().getVipScore());
		info.put("spoint", player.getProperty().getSpoint());
		info.put("ch", player.getProperty().getCh());
		info.put("pow", player.getHeros().getPlayerPower());
		info.put("lt", player.getProperty().getLastOnlineTime());
		info.put("ft", player.getProperty().getFirstOnlineTime());
		info.put("ban", player.getProperty().getBanPlayerLv() > 0 ? 1 : 0);

		// 忍村建筑物信息
		ArrayNode builds = DynamicJsonProperty.jackson.createArrayNode();
		BuildingConfigManager buildingConfig = AllGameConfig.getInstance().buildings;
		for (Map.Entry<Integer, BuildInstance> e : player.getBuilding()
				.getBuilds().entrySet()) {
			ObjectNode b = DynamicJsonProperty.jackson.createObjectNode();
			b.put("name", buildingConfig.getBuilding(e.getKey()).getName()); // 建筑物名称
			b.put("lv", e.getValue().getLevel()); // 建筑物等级
			builds.add(b);
		}
		info.put("builds", builds);

		return toJSON(1, info.toString());
	}
	
	@POST 
	@GET
	@Path("/gm/addNotice")
	@Produces("application/json")
	public String addNotice(@QueryParam(value = "title") String title,@QueryParam(value = "content") String content,  @QueryParam(value = "key") String key) throws Exception { 
		
		if (isEmpty(key) || !ContextConfig.GM_REWARD_KEY.equals(key)) {
			return toJSON(0, "param error");
		}
		GlobalProps globalProps = getLDroom().getGlobalProps();
		if (isEmpty(title) ) {
			globalProps.setNoticeProps(null);
			return toJSON(1, "");
		}
		
		logger.info("titleId={}, contentId={}", title, content);
		
		//给玩家发邮件
		globalProps.addNotice(title, content);
		return toJSON(1, "");
	}

	
	@GET
	@POST
	@Path("/notice")
	@Produces("application/json;charset=UTF-8")
	public String getNotice() throws Exception { 
		NoticeProps notice = getLDroom().getGlobalProps().getNoticeProps();
		if(notice == null) {
			return "FAIL";
		}else {
			ObjectNode json = notice.toJson();
			return json.toString();
		}
	}
	
	
	@GET
	@Path("/world")
	@Produces("application/json")
	public Helloworld helloworld() throws Exception { 
		return new Helloworld("Welcome, HelloWorld");
	}

	@GET
	@Path("/favicon.ico")
	@Produces("application/json")
	public Helloworld favicon() throws Exception {
		return new Helloworld("Welcome, HelloWorld");
	}
	
	@GET
	@Path("/auth")
	@Produces("application/json")
	public Helloworld auth(@Context SecurityContext context) {
		return new Helloworld(context.getUserPrincipal().getName());
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Article save(Article article) {
		return article;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Article> save(
			@QueryParam("multi") boolean isMulti,
			List<Article> articles) {
		return articles;
	}
	
	@GET
	@Path(value = "/echo/{message}")  
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Helloworld doPath(@PathParam(value = "message") String message) {
		//ObjectCacheService cache = new ObjectCacheService();
		return new Helloworld(message);
	}
	
	private String toJSON(int code, String msg) {
		ObjectNode json = new ObjectMapper().createObjectNode();
		json.put("code", code);
		if (!msg.isEmpty()) {
			json.put("msg", msg);
		}
		return json.toString();
	}
	
	private String toJSON(int code, JsonNode node) {
		return toJSON(code, node.toString());
	}
	
	private boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}
	
	
	@GET
	@POST
	@Path("/gm/setServerPrefer")
	@Produces("application/json;charset=UTF-8")
	public String setServerPrefer(@QueryParam("key") String key, @QueryParam("name") String name, @QueryParam("value") int value, @Context ChannelHandlerContext ctx) throws Exception {
		//	检查用户身份
		if (!ContextConfig.ADD_IP_KEY.equals(key)) {
			logger.error("illegal user wrong key:" + key);
			return toJSON(0, "shit fuck!");
		}
		//	检查参数
		if (key.isEmpty()) {
			return toJSON(0, "param error!");
		}	
		if (name.equals("openMonthCard")) {
			AllGameConfig.getInstance().gconst.setConstant(GameConstId.PREFER_IS_OPEN_MONTHCARD, value);
		} else if (name.equals("openGiftCode")) {
			AllGameConfig.getInstance().gconst.setConstant(GameConstId.PREFER_IS_OPEN_GIFTCODE, value);
		} else {
			return toJSON(0, "no prefer!");
		}
		return toJSON(0, "OK");
	}
	
	/**
	 * 重置竞技场排名
	 **/
	@GET
	@POST
	@Path("/gm/cleanRanking")
	@Produces("application/json;charset=UTF-8")
	public String cleanArenaRanking(@QueryParam("key") String key) {		
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "key wrong!");
		}
		GameRankMaster.getInstance().saveRank();
		GameRankMaster.getInstance().arenaRank.clear();
		return toJSON(0, "OK");
	}
	
	@GET
	@POST
	@Path("/gm/getTeams")
	@Produces("application/json;charset=UTF-8")
	public String getTeams(@QueryParam("key") String key, @QueryParam("name") String data) {
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "key wrong!");
		}
		List<String> names = Lists.newArrayList();
		if (data != null) {
			Collections.addAll(names, data.split(","));
		}
		return toJSON(0, TeamManager.getSingleton().getTeams(names).toString());
	}
	
	@GET
	@POST
	@Path("/gm/getPlayerTeam")
	@Produces("application/json;charset=UTF-8")
	public String getPlayerTeam(@QueryParam("key") String key, @QueryParam("name") String roleName) {
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "key wrong!");
		}
		if (roleName == null || roleName.equals("")) {
			return toJSON(1, "invalid parameters!");
		}
		
		LDRoom room = getLDroom();
		String guid = room.getAllPlayersCache().getPlayerIdByName(roleName);
		if (guid == null) {
			return toJSON(1, "no exist role!");
		}
		Player player = room.getObjectCacheService().getCache(guid, DefaultPlayer.class);
		return toJSON(0, player.getTeam().toWholeJson().toString());
	}
	
	/**
	 * 修改玩家军团信息
	 * 如果teamName为空，修改roleName为当前所在军团的军团长
	 * 如果teamName不为空，修改roleName的军团为teamName
	 * @param key
	 * @param roleName
	 * @param teamName
	 * @return
	 */
	@GET
	@POST
	@Path("/gm/repairPlayerBadTeam")
	@Produces("application/json;charset=UTF-8")
	public String repairPlayerBadTeam(@QueryParam("key") String key, @QueryParam("name") String roleName, @QueryParam("teamName") String teamName) {
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "key wrong!");
		}
		if (roleName == null || roleName.equals("")) {
			return toJSON(1, "invalid parameters!");
		}
		String guid = getLDroom().getAllPlayersCache().getPlayerIdByName(roleName);
		if (guid == null) {
			return toJSON(1, "no exist player!");
		}
		DefaultPlayer dp = getLDroom().getObjectCacheService().getCache(guid, DefaultPlayer.class);
		if (dp == null) {
			return toJSON(1, "unknown error!");
		}
		if (teamName == null || teamName.equals("")) {
			TeamManager.getSingleton().setTeamCommander(dp.getTeam().getName(), guid);
		}else{
			dp.getTeam().setName(teamName);
			getLDroom().getObjectCacheService().putObject(dp);
		}
		dp = null;
		return toJSON(0, "OK");
	}
	
	@GET
	@POST
	@Path("/gm/getRaffleRoleScoreRank")
	@Produces("application/json;charset=UTF-8")
	public String getRaffleRoleScoreRank(@QueryParam("key") String key, 
			@QueryParam("from") int from, @QueryParam("to") int to) {
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "key wrong!");
		}
		return toJSON(0, GameRankMaster.getInstance().scoreRank.getRangeRankGM(from, to).toString());
	}
	
	@GET
	@POST
	@Path("/gm/getPlayerRaffleRoleScoreRank")
	@Produces("application/json;charset=UTF-8")
	public String getPlayerRaffleRoleScoreRank(@QueryParam("key") String key, @QueryParam("name") String name) {
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "key wrong!");
		}
		String guid = getLDroom().getAllPlayersCache().getPlayerIdByName(name);
		if (guid == null) {
			return toJSON(1, "no exist player!");
		}
		return toJSON(0, GameRankMaster.getInstance().scoreRank.getRankInfo(guid).toString());
	}
	
	@GET
	@POST
	@Path("/gm/reloadConfig")
	@Produces("application/json;charset=UTF-8")
	public String reloadConfig(@QueryParam("key") String key) {
		// 只支持商店,关卡掉落以及忍者屋抓忍者权重配置表热更新
		
		// 检查操作源是否合法
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "未授权的用户无法执行该操作");
		}
		
		// 检查当前是否有玩家在线或服务器处于对外开放状态
//		if (ContextConfig.GAME_OPEN || getLDroom().getSessions().size() > 0) {
//			return toJSON(1, "服务器处于对外开放状态或仍有玩家在线时不允许执行该操作");
//		}
		
		AllGameConfig.getInstance().rate.loadConfig(); // 重新初始化忍者权重配置数据
		AllGameConfig.getInstance().items.loadShopItemGroup(); // 重新初始化商城配置数据
		AllGameConfig.getInstance().drops.loadConfig(); // 重新初始化关卡道具掉率数据
		
		return toJSON(0, "操作完成");
	};
	
	@GET
	@POST
	@Path("/gm/refreshAllRoleEntity")
	@Produces("application/json;charset=UTF-8")
	public String refreshAllRoleEntity(@QueryParam("key") String key) {
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "未授权的用户无法执行该操作");
		}
		int count = 0;
		AllPlayersCache allPlayerCache = getLDroom().getAllPlayersCache();
		for (RoleEntity role : allPlayerCache.getAllPlayerInfo()) {
			DefaultPlayer dp = getLDroom().getObjectCacheService().getCache(role.getGuid(), DefaultPlayer.class);
			if (dp != null) {
				allPlayerCache.updateRolePropAndSave(dp, role);
				++count;
			}
		}
		return toJSON(0, "处理角色数量=" + count);
	}
	
	/****************************************************************************************
	 * 军团GM命令
	 ****************************************************************************************/
	
	/**
	 * 开启军团战争
	 **/
	@GET
	@POST
	@Path("/gm/switchTeamWarOpen")
	@Produces("application/json;charset=UTF-8")
	public String switchTeamWarOpen(@QueryParam("key") String key) {
		// 检查操作源是否合法
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "未授权的用户无法执行该操作");
		}
		return toJSON(0, "OK");
	}
	
	/**
	 * 增加军团经验
	 **/
	public String addTeamExp(@QueryParam("key") String key, @QueryParam("isAutoUpgrade") boolean isAutoUpgrade) {
		return toJSON(0, "OK");
	}
	
	/**
	 * 删除小号
	 **/
	@GET
	@POST
	@Path("/gm/removeOldData")
	@Produces("application/json;charset=UTF-8")
	public String removeOldData(@QueryParam("key") String key) {
		// 检查操作源是否合法
		if (key == null || !ContextConfig.ADD_IP_KEY.equals(key)) {
			return toJSON(1, "fail auth");
		}
		NaruMapper naruMapper = AppContext.getBean(NaruMapper.class);
		RoleMapper roleMapper = AppContext.getBean(RoleMapper.class);
		int count = 0;
		List<String> all = naruMapper.loadAll();
		for (String guid : all) {
			Naru naru = naruMapper.getVal(guid);
			byte[] val = naru.getVal();
			try {
				DefaultPlayer player = DynamicJsonProperty.jackson.readValue(val, DefaultPlayer.class);
				PlayerProperty property = player.getProperty();
				if(property == null) {
					if(guid.equals(GlobalProps.theGuid.toString())) {
						naruMapper.delete(guid);
					}else {
						logger.info("what is this : guid : {}",guid);
					}
					
				}else {
					if(property.getLevel() < 15 && property.getVipScore() <= 0) {
						long intervalDay = TimeUtil.diffDays(TimeUtil.getCurrentTime() * 1000L, 
								property.getLastOnlineTime() * 1000L);
						if(intervalDay >= 30) {
							roleMapper.delete(guid);
							naruMapper.delete(guid);
							count ++;
						}
					}
				}
				
			} catch (Exception e) {
				logger.error("{}",e);
			} 
		}
		logger.info("removeOldData all : {}, removed:{}",all.size(), count);
		return toJSON(0, "OK");
	}
	
}
