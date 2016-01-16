package com.dol.cdf.union.model;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.entities.Entity;
import com.dol.cdf.union.constants.UnionConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.UnionCacheService;

public class UnionEntity extends Entity {
	public UnionEntity() {
	}

	@JsonProperty("na")
	private String name;

	@JsonProperty("lv")
	private int level = 1;
	/** 帮主id */
	@JsonProperty("ow")
	private String ownerId;

	/** 公会荣誉值 */
	@JsonProperty("ho")
	private int honour;

	/** 公会经验值 */
	@JsonProperty("ex")
	private int exp;
	/** 公会公告 */
	@JsonProperty("no")
	private String notice;
	/** 公会战斗力 */
	public long power;

	@JsonProperty("de")
	private boolean isDeleted = false;

	private List<String> receiveIds = Lists.newCopyOnWriteArrayList();

	// 成员列表[名字，等级，职务（0:普通，1：帮主）,竞技场排名,今日贡献，总贡献，上次登录时间]
	private Set<String> members = new HashSet<String>();
	// private ArrayList<UnionJoinRequestInfo> joinRequestInfos=new
	// ArrayList<UnionJoinRequestInfo>(UnionConstants.MAX_REQUEST_JOIN_NUM);
	private LinkedHashMap<String, UnionJoinRequestInfo> joinRequestInfos = Maps
			.newLinkedHashMap();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level <= 0 ? 1 : level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getOwnerId() {
		return ownerId;
	}

	// 移交帮主
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		// 查看是否有该玩家的入帮申请
		UnionJoinRequestInfo unionJoinRequestInfo = joinRequestInfos.get(guid);
		unionJoinRequestInfo.setRequest_resolve_time(TimeUtil.getCurrentTime());
		unionJoinRequestInfo.setRequest_status(1);
	}

	public int getHonour() {
		return honour;
	}

	public void setHonour(int honour) {
		this.honour = honour;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public List<String> getReceiveIds() {
		return receiveIds;
	}

	public void setReceiveIds(List<String> receiveIds) {
		this.receiveIds = receiveIds;
	}

	public Set<String> getMembers() {
		return members;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setMembers(Set<String> members) {
		this.members = members;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	@Autowired
	private static AllPlayersCache allPlayersCache;

	/**
	 * 增加帮会成员
	 * 
	 * @param guid
	 * @return
	 */
	public synchronized boolean addMemeber(String guid) {
		if (UnionCacheService.getInstance().isUnionMember(guid)) {
			return false;
		}
		this.members.add(guid);
		UnionCacheService.getInstance().getPlayer2union().put(guid, this.guid);
		// 查看是否有该玩家的入帮申请
		for (UnionEntity entity : UnionCacheService.getInstance().getUnions()
				.values()) {
			if (entity.guid.equals(this.guid)) {
				UnionJoinRequestInfo joinRequestInfo = joinRequestInfos
						.get(guid);
				joinRequestInfo.setRequest_resolve_time(TimeUtil
						.getCurrentTime());
				joinRequestInfo.setRequest_status(1);
			} else {
				entity.getJoinRequestInfos().remove(guid);
			}
		}
		if (!this.isMaster(guid)) {
			// this.sendChat(MessageCode.UNION_JOIN,
			// allPlayersCache.getNameByPlayerId(guid), this.name);
		}

		return true;
	}

	public void sendChat(int code, String... strings) {

	}

	/**
	 * 拒绝入帮
	 * 
	 * @param guid
	 */
	public void refuseMember(String guid) {
		// 查看是否有该玩家的入帮申请
		UnionJoinRequestInfo unionJoinRequestInfo = this.joinRequestInfos
				.get(guid);
		if (unionJoinRequestInfo != null) {
			unionJoinRequestInfo.setRequest_resolve_time(TimeUtil
					.getCurrentTime());
			unionJoinRequestInfo.setRequest_status(2);
		}
	}

	/**
	 * 是否本帮成员
	 * 
	 * @param guid
	 * @return
	 */
	public boolean isOurMember(String guid) {
		if (this.getMembers().contains(guid)) {
			return true;
		}
		return false;
	}

	public void removeRequest(String guid) {
		this.joinRequestInfos.remove(guid);
	}

	/**
	 * 删除帮会成员
	 * 
	 * @param guid
	 * @return
	 */
	public synchronized boolean removeMember(String guid) {
		if (this.members.remove(guid)) {
			UnionCacheService.getInstance().getPlayer2union().remove(guid);
			// 查看是否有该玩家的入帮申请
			joinRequestInfos.remove(guid);

			// TODO 通知玩家帮会名称没有了
			return true;
		}
		return false;
	}

	/**
	 * 玩家是否有申请加入
	 * 
	 * @param guid
	 * @return
	 */
	public boolean isRequstJson(String guid) {
		return joinRequestInfos.containsKey(guid);
	}

	/**
	 * 是否有未处理的 加入请求
	 * 
	 * @return
	 */
	public boolean isUnresolveJoinRequest() {
		for (UnionJoinRequestInfo joinRequestInfo : this.joinRequestInfos
				.values()) {
			if (joinRequestInfo.getRequest_status() == 0) {
				return true;
			}
		}
		return false;
	}

	public synchronized void addJoinRequest(String guid,
			UnionJoinRequestInfo joinRequestInfo) {
		if (joinRequestInfos.size() >= UnionConstants.MAX_REQUEST_JOIN_NUM) {
			// 删除第一个单元
			Object key = joinRequestInfos.keySet().iterator().next();
			joinRequestInfos.remove(key);
		}
		joinRequestInfos.put(guid, joinRequestInfo);
	}

	public int sise() {
		return this.members.size();
	}

	/**
	 * 是否是帮主
	 * 
	 * @param guid
	 * @return
	 */
	public boolean isMaster(String guid) {
		if (guid.equals(this.ownerId)) {
			return true;
		}
		return false;
	}

	public long getPower() {
		return power;
	}

	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public int compareTo(Object o) {
		UnionEntity entity = (UnionEntity) o;
		if (this.power > entity.power) {
			return -1;
		} else if (this.power == entity.power) {
			if (this.equals(o)) {
				return 0;
			}
		}
		return 1;
	}

	public LinkedHashMap<String, UnionJoinRequestInfo> getJoinRequestInfos() {
		return joinRequestInfos;
	}

	public void setJoinRequestInfos(
			LinkedHashMap<String, UnionJoinRequestInfo> joinRequestInfos) {
		this.joinRequestInfos = joinRequestInfos;
	}

}
