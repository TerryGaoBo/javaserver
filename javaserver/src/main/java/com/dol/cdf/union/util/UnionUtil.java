//package com.dol.cwdz.union.util;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import com.dol.cdf.common.TimeUtil;
//import com.dol.cdf.data.role.model.RoleEntity;
//import com.dol.cdf.lobby.PlayerLobbys;
//import com.dol.cdf.player.AllPlayersCache;
//import com.dol.cdf.rank.GameRankMaster;
//import com.dol.cwdz.battle.pet.model.PetModel;
//import com.dol.cwdz.battle.pet.util.PetUtil;
//import com.dol.cwdz.player.Player;
//import com.dol.cwdz.player.util.PlayerUtil;
//import com.dol.cwdz.union.UnionManager;
//import com.dol.cwdz.union.constants.UnionConstants.MemberStatus;
//import com.dol.cwdz.union.model.UnionEntity;
//import com.dol.cwdz.union.model.UnionJoinRequestInfo;
//import com.dol.cwdz.union.model.UnionMember;
//import com.dol.cwdz.union.model.UnionPet;
//import com.dol.cwdz.union.model.UnionRequestReinforcement;
//import com.dol.cwdz.union.model.UnionResolveReinforcement;
//import com.play.crypto.Guid;
//import com.yp.ballistic.lobby.LobbySocketServer;
//
//public class UnionUtil {
//	/**查看单个公会信息
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_info(LobbySocketServer lobbySocketServer,UnionEntity unionEntity,boolean isOther){
//		JSONObject object = new JSONObject();
//		object.putOpt("id", unionEntity.guid.toString());
//		object.putOpt("name", unionEntity.getName());
//		object.putOpt("level", unionEntity.getLevel());
//		object.putOpt("num", unionEntity.sise());
//		object.putOpt("owner", AllPlayersCache.getInstance().getNameByPlayerId(unionEntity.getOwnerId()));
//		object.putOpt("honour", unionEntity.getHonour());
//		object.putOpt("exp", unionEntity.getExp());
//		object.putOpt("notice", unionEntity.getNotice());
//		MemberStatus memberStatus=getMemberStatus(lobbySocketServer.player.guid, unionEntity);
//		object.putOpt("members", members2Json(unionEntity, memberStatus,isOther));
//		object.putOpt("rank", GameRankMaster.getInstance().unionRank.getIndex(unionEntity.guid));
//		object.putOpt("status", memberStatus.ordinal());
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		if(memberStatus==MemberStatus.JOINED&&!isOther){
//			jsonObject.putOpt("cmd", "union_info");
//		}else{
//			jsonObject.putOpt("cmd", "union_info_other");
//		}
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**公会申请列表
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_requst_list(LobbySocketServer lobbySocketServer,UnionEntity unionEntity){
//		JSONObject object = new JSONObject();
//		object.putOpt("info", requestInfo2Json(unionEntity));
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_requst_list");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**公会申请列表
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_create(LobbySocketServer lobbySocketServer,UnionEntity unionEntity,String name){
//		JSONObject object = new JSONObject();
//		object.putOpt("name", name);
//		object.putOpt("isOk", unionEntity==null?0:1);
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_create");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**退出公会
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_quit(LobbySocketServer lobbySocketServer,int isOk){
//		JSONObject object = new JSONObject();
//		object.putOpt("isOk",isOk);
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_quit");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**公会状态信息
//	 * @param lobbySocketServer
//	 */
//	public static void send_union_status(LobbySocketServer lobbySocketServer){
//		JSONObject object = new JSONObject();
//		Guid guid=UnionManager.getIntance().getPlayer2union().get(lobbySocketServer.player.guid);
//		UnionEntity unionEntity=null;
//		if(guid!=null){
//			unionEntity=UnionManager.getIntance().getUnions().get(guid);
//		}
//		if(guid==null||unionEntity==null){
//			object.putOpt("player_status", 0);
//		}else{
//			if(unionEntity.isMaster(lobbySocketServer.player.guid)){
//				if(unionEntity.isUnresolveJoinRequest()){
//					object.putOpt("request_status", 1);
//				}else{
//					object.putOpt("request_status", 0);
//				}
//			}
//			object.putOpt("union_name", unionEntity.getName());
//			object.putOpt("player_status", unionEntity.isMaster(lobbySocketServer.player.guid)?1:2);
//			object.putOpt("left_num", lobbySocketServer.player.property.getUnion_left_num());	
//		}
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_status");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**公会状态信息
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_status(LobbySocketServer lobbySocketServer,UnionEntity unionEntity,
//			List<UnionRequestReinforcement> reinforcement,List<UnionResolveReinforcement> resolveReinforcement){
//		JSONObject object = new JSONObject();
//		UnionMember member=unionEntity.getMembers().get(lobbySocketServer.player.guid);
//		if(unionEntity.isMaster(lobbySocketServer.player.guid)){
//			if(unionEntity.isUnresolveJoinRequest()){
//				object.putOpt("request_status", 1);
//			}else{
//				object.putOpt("request_status", 0);
//			}
//		}
//		object.putOpt("player_status", unionEntity.isMaster(lobbySocketServer.player.guid)?1:2);
//		object.putOpt("left_num",lobbySocketServer.player.property.getUnion_left_num());
//		object.putOpt("union_name", unionEntity.getName());
//		if(reinforcement!=null){
//			object.putOpt("request", reinforcementsRequest2Json(reinforcement,member));
//		}
//		if(resolveReinforcement!=null){
//			object.putOpt("info", reinforcementsResolve2Json(resolveReinforcement));
//		}
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_status");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**援兵栏信息
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_reinforcements_mine(LobbySocketServer lobbySocketServer,UnionEntity unionEntity){
//		JSONObject object = new JSONObject();
//		UnionMember member=unionEntity.getMembers().get(lobbySocketServer.player.guid);
//		object.putOpt("mine", mineReinforcements2Json(member));
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_reinforcements_mine");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**收到邀请信息
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_receive_join(LobbySocketServer lobbySocketServer,String union_name,String name){
////		//收到邀请信息
////		"cmd":"union_receive_join",
////		"data":{
////		"union_name":"帮会名字",
////		"name":"玩家名字"
////		}
//		JSONObject object = new JSONObject();
//		object.putOpt("union_name", union_name);
//		object.putOpt("name",name);
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_receive_join");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	/**公会支援信息
//	 * @param lobbySocketServer
//	 * @param unionEntity
//	 */
//	public static void send_union_reinforcements(LobbySocketServer lobbySocketServer,UnionEntity unionEntity){
//		JSONObject object = new JSONObject();
//		UnionMember member=unionEntity.getMembers().get(lobbySocketServer.player.guid);
//		object.putOpt("request", reinforcementsRequest2Json(unionEntity.getRequestReinforcements(),member));
//		object.putOpt("info", reinforcementsResolve2Json(unionEntity.getResolveReinforcements()));
//		object.putOpt("mine", mineReinforcements2Json(member));
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("data", object);
//		jsonObject.putOpt("cmd", "union_reinforcements");
//		lobbySocketServer.putMsg(jsonObject);
//	}
//	public static PetModel getRandomPet(Player player){
//		Map<Integer, PetModel> map=	player.pet.getPets();
//		int index=new Random().nextInt(map.size());
//		int count=0;
//		for(Integer key:map.keySet()){
//			if(index==count){
//				return map.get(key);
//			}
//			count++;
//		}
//		return null;
//	}
//	private static JSONArray reinforcementsRequest2Json(List<UnionRequestReinforcement> reinforcements,UnionMember member){
//		JSONArray jsonArray=new JSONArray();
//		for(UnionRequestReinforcement reinforcement:reinforcements){
//			jsonArray.put(oneRequest2Json(reinforcement, member));
//		}
//		return jsonArray;
//	}
//	private static JSONArray oneRequest2Json(UnionRequestReinforcement reinforcement,UnionMember member){
//		JSONArray array=new JSONArray();
////		//申请人列表 [申请人名字，申请时间，申请处理状态（0:未处理，1：已同意）]
//		array.put(AllPlayersCache.getInstance().getNameByPlayerId(reinforcement.getGuid()));
//		array.put(TimeUtil.getCurrentTime()-reinforcement.getRequst_time());
//		array.put(member.getResolveStatus(reinforcement));
//		return array;
//	}
//	private static JSONArray oneResolve2Json(UnionResolveReinforcement resolve){
//		JSONArray array=new JSONArray();
////		JSONArray array=new JSONArray();
////		援兵信息[时间，被支援人名字,支援人名字,支援宠物id,支援宠物属性]
//		array.put(TimeUtil.getCurrentTime()-resolve.getResolve_time());
//		array.put(resolve.getRequest_name());
//		array.put(resolve.getResolve_name());
//		array.put(resolve.getPet().getPetType());
//		array.put("");
//		array.put(resolve.getPet().getPower());
//		return array;
//	}
//	//reinforcementsResolve2Json
//	private static JSONArray reinforcementsResolve2Json(List<UnionResolveReinforcement> resolveReinforcements){
//		JSONArray jsonArray=new JSONArray();
//		for(UnionResolveReinforcement resolve:resolveReinforcements){
//			jsonArray.put(oneResolve2Json(resolve));
//		}
//		return jsonArray;
//	}
//	
////	//公会支援信息
////	"cmd":"union_reinforcements"
////	"data":{
////	//申请人列表 [申请人名字，申请时间，申请处理状态（0:未处理，1：已同意）]
////	"request":[["申请人名字",23323,0]]
////	//我的援兵[援兵id,援兵主人名字,援兵宠物id,援兵属性（同宠物属性）]
////	"mine":[["dfdfd","援兵主人名字",2222,{"enhance":[1,2,3,3,10],"level":1,"clear_times":2,"gem":[0,0,0,0,0],"exp":1000,"full_point":22}]]
////	//援兵信息[被支援人名字,支援人名字,支援宠物id,支援宠物属性]
////	"info":[["被支援人名字","支援人名字",2222,{"enhance":[1,2,3,3,10],"level":1,"clear_times":2,"gem":[0,0,0,0,0],"exp":1000,"full_point":22}]]
////	}
//	private static JSONArray mineReinforcements2Json(UnionMember member){
//		JSONArray jsonArray=new JSONArray();
//		for(UnionPet pet:member.getUnionPets().values()){
//			JSONArray array=new JSONArray();
////			//我的援兵[援兵id,援兵主人名字,援兵宠物id,援兵属性（同宠物属性）]
//			array.put(pet.getGuid());
//			array.put(pet.getName());
//			array.put(pet.getPetType());
//			array.put("");
//			array.put(pet.getPower());
//			jsonArray.put(array);
//		}
//		return jsonArray;
//	}
//	
//	
//	
//	
//	
//	private static JSONArray requestInfo2Json(UnionEntity unionEntity){
//		JSONArray jsonArray=new JSONArray();
//		for(UnionJoinRequestInfo requestInfo:unionEntity.getJoinRequestInfos()){
//			JSONArray array=new JSONArray();
////			[申请人名字，申请时间，申请处理状态（0:未处理，1：已同意，2：已拒绝）]
//			RoleEntity roleEntity=AllPlayersCache.getInstance().getPlayerInfo(requestInfo.getGuid());
//			array.put(roleEntity.getName());
//			array.put(TimeUtil.getCurrentTime()- requestInfo.getRequest_time());
//			array.put(requestInfo.getRequest_status());
//			array.put(requestInfo.getPower()==0?roleEntity.getPower():requestInfo.getPower());
//			jsonArray.put(array);
//		}
//		return jsonArray;
//	}
//	
//	
//	/**
//	 * @param unionEntity
//	 * @param isMember--是否本帮成员
//	 * @return
//	 */
//	private static JSONArray members2Json(UnionEntity unionEntity,MemberStatus memberStatus,boolean isOther){
//		JSONArray jsonArray=new JSONArray();
//		JSONArray array=new JSONArray();
//		UnionMember owner=unionEntity.getMembers().get(unionEntity.getOwnerId());
//		//成员列表[名字，等级，职务（0:普通，1：帮主）,竞技场排名,今日贡献，总贡献，荣誉值，上次登录时间]
//		RoleEntity roleEntity=AllPlayersCache.getInstance().getPlayerInfo(unionEntity.getOwnerId());
//		array.put(roleEntity.getName());
//		array.put(roleEntity.getLevel());
//		array.put(1);
//		array.put(GameRankMaster.getInstance().arenaRank.getIndex(unionEntity.getOwnerId()));
//		if(memberStatus.equals(MemberStatus.JOINED)&&!isOther){
//			array.put(owner.getToday_contribution());
//			if(!TimeUtil.isSameDay(owner.getLast_refresh_time(), TimeUtil.getCurrentTime())){
//				owner.setLast_refresh_time(TimeUtil.getCurrentTime());
//				owner.setToday_contribution(0);
//			}
//			array.put(owner.getContribution());
//			array.put(owner.getPp());
//			array.put(TimeUtil.getCurrentTime()-(roleEntity.getLastLogin().getTime()/1000));
//			array.put(PlayerLobbys.getInstance().getLobby(owner.getGuid())==null?0:1);
//		}
//		jsonArray.put(array);
//		List<UnionMember> list=sortUnionMember(unionEntity.getMembers().values());
//		for(UnionMember member:list){
//			if(member.getGuid().equals(owner.getGuid())){
//				continue;
//			}
//			JSONArray array2=new JSONArray();
//			roleEntity=AllPlayersCache.getInstance().getPlayerInfo(member.getGuid());
//			array2.put(roleEntity.getName());
//			array2.put(roleEntity.getLevel());
//			array2.put(member.getDuty());
//			array2.put(GameRankMaster.getInstance().arenaRank.getIndex(member.getGuid()));
//			if(memberStatus.equals(MemberStatus.JOINED)&&!isOther){
//				array2.put(owner.getToday_contribution());
//				if(!TimeUtil.isSameDay(member.getLast_refresh_time(), TimeUtil.getCurrentTime())){
//					member.setLast_refresh_time(TimeUtil.getCurrentTime());
//					member.setToday_contribution(0);
//				}
//				array2.put(member.getContribution());
//				array2.put(member.getPp());
//				array2.put(TimeUtil.getCurrentTime()-roleEntity.getLastLogin().getTime()/1000);
//				array2.put(PlayerLobbys.getInstance().getLobby(member.getGuid())==null?0:1);
//			}
//			jsonArray.put(array2);
//		}
//		return jsonArray;
//	}
//	
//	/**查看一个玩家对于一个帮会的状态
//	 * @param guid
//	 * @param unionEntity
//	 * @return
//	 */
//	public static MemberStatus getMemberStatus(Guid guid,UnionEntity unionEntity){
//		if(unionEntity.getMembers().containsKey(guid)){
//			return MemberStatus.JOINED;
//		}
//		if(UnionManager.getIntance().isUnionMember(guid)){
//			return MemberStatus.OTHER;
//		}
//		if(unionEntity.isRequstJson(guid)){
//			return MemberStatus.REQUESTED;
//		}
//		return MemberStatus.NOT_REQUEST;
//	}
//	
//	private static List<UnionMember> sortUnionMember(Collection<UnionMember> members ){
//		List<UnionMember> list=new ArrayList<UnionMember>(members);
//		Collections.sort(list);
//		return list;
//	}
////	//公会排名
////	"rank":12,
////	//成员列表[名字，等级，职务（0:普通，1：帮主）,竞技场排名]
////	"members":[["名字",10,1,10]],
////	//申请入帮的状态（0：普通，1：可以申请,2:已经申请过)
////	"status":1
////	}
//	
//
//}
