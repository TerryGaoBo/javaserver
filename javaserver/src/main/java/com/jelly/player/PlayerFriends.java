package com.jelly.player;

import io.nadron.app.PlayerSession;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.context.AppContext;
import io.nadron.example.lostdecade.LDRoom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.rank.GameRankMaster;

public class PlayerFriends extends DynamicJsonProperty{

	@JsonProperty("mf")
	public Set<String> myFriends = new HashSet<String>();
	
	@JsonProperty("bl")
	public Set<String> blackList = new HashSet<String>();
	
	@JsonProperty("in")
	public int init = 0; //为玩家初始化好友
	
	private AllPlayersCache allPlayersCache = (AllPlayersCache) AppContext.getBean("allPlayersCache");
	private LDRoom room = AppContext.getBean(LDRoom.class);
	private ObjectCacheService objectCacheService = AppContext.getBean(ObjectCacheService.class);

	public void setFriends(Set<String> set) {
		myFriends = set;
		init = 1;
		ArrayNode friendList = jackson.createArrayNode();
		for (String guid : myFriends) {
			ArrayNode node = getPlayerInfo(guid);
			if (node != null)
				friendList.add(node);
		}
		addChange("friendList", friendList);
		addChange("init", init);
	}
	
	public void addFriend(String guid) {
		myFriends.add(guid);
		addChange("friendList", resultList(guid));
		if (this.blackList.contains(guid)) {
			this.rmBlackList(guid);
		}
	}
	
	public boolean rmFriend(String guid) {
		if (myFriends.remove(guid)) {
			addChange("friendList", resultList(guid));
			return true;
		}
		return false;
	}
	
	public void addBlackList(String guid){
		blackList.add(guid);
		addChange("blacklist", resultList(guid));
		if (this.myFriends.contains(guid)) {
			this.rmFriend(guid);
		}
	}
	
	public boolean rmBlackList(String guid){
		if (blackList.remove(guid)) {
			addChange("blacklist", resultList(guid));
			return true;
		}
		return false;
	}

	@Override
	public String getKey() {
		return "friends";
	}
	

	
	@Override
	public JsonNode toWholeJson() {
		ArrayNode friendList = jackson.createArrayNode();
		for (String guid : myFriends) {
			ArrayNode node = getPlayerInfo(guid);
			if (node != null)
				friendList.add(node);
		}
		ArrayNode blacklist = jackson.createArrayNode();
		for (String guid : blackList) {
			ArrayNode node = getPlayerInfo(guid);
			if (node != null)
				blacklist.add(node);
		}
		ObjectNode obj = jackson.createObjectNode();
		obj.put("friendList", friendList);
		obj.put("blacklist", blacklist);
		obj.put("ALL", 1);
		obj.put("init", init);
		return obj;

	}
	
	public ArrayNode getPlayerInfo(String guid) {
		RoleEntity p = allPlayersCache.getPlayerInfo(guid);
		return getPlayerInfo(p);
	}
	
	public ArrayNode getPlayerInfo(RoleEntity p) {
		if (p == null) {
			return null;
		}
		ArrayNode node = DynamicJsonProperty.jackson.createArrayNode().add(
				p.getName()).add(p.getLevel()).add(p.getCharId()).add(p.getPower());
		node.add(GameRankMaster.getInstance().getArenaRank().getIndex(p.getGuid()));//排名
		node.add(p.getTeamName()); // 优化
//		// @NOTE 需要优化
//		PlayerSession session = room.getSessions().get(p.getGuid());
//		if (session != null) {
//			node.add(session.getPlayer().getTeam().getName());
//		} else {
//			DefaultPlayer player2 = objectCacheService.getCache(p.getGuid(), DefaultPlayer.class);
//			if (player2 != null) {
//				node.add(player2.getTeam().getName()); //TODO 添加帮会
//				objectCacheService.putObject(player2);
//				player2 = null;
//			}
//		}
		return node;
	}
	
	
	private JsonNode resultList(String guid) {
		ArrayNode list = jackson.createArrayNode();
		ArrayNode node = getPlayerInfo(guid);
		list.add(node);
		return list;
	}
	
}
