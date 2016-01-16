package com.jelly.team;

import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.hero.Hero;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;

import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.context.AppContext;
import io.nadron.event.Events;
import io.nadron.example.lostdecade.LDRoom;

/**
 * 通知服务类
 **/
public class TeamNotifyService {
	
	private static final LDRoom room = AppContext.getBean(LDRoom.class);
	private static final ObjectCacheService cacheService = AppContext.getBean(ObjectCacheService.class);
	
	/**
	 * 创建一个PlayerSession事件源对象实例
	 **/
	private static ObjectNode createEventSource(int notifyReason, String... params) {
		ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
		source.put("reason", notifyReason);
		source.put("params", DynamicJsonProperty.convertToArrayNode(params));
		return source;
	}
	
	private static ObjectNode createEventSource(int notifyReason, ArrayNode params) {
		ObjectNode source = DynamicJsonProperty.jackson.createObjectNode();
		source.put("reason", notifyReason);
		source.put("params", params);
		return source;
	}
	
	/**
	 * 通知发送入团申请的玩家已批准加入军团
	 **/
	private void notifyJoinMember(Player player, String targetId, String... params) {
		PlayerSession ps = room.getSessions().get(player.getId());
		if (ps != null) {
			ps.onEvent(Events.event("teamNotify", createEventSource(TeamConstants.TEAM_JOIN_MEMBER, params)));
		} else {
			DefaultPlayer offlinePlayer = cacheService.getCache(targetId, DefaultPlayer.class);
			if (offlinePlayer != null) {
				offlinePlayer.getMail().addSysMail(TeamConstants.MAIL_TITLE_JOIN_MEMBER, TeamConstants.MAIL_CONTENT_JOIN_MEMBER, params);
				offlinePlayer.getTeam().updateMyTeam(params[0]);
				cacheService.putObject(offlinePlayer);
				offlinePlayer = null;
			}
		}
	}
	
	/**
	 * 通知发送入团申请的玩家请求被拒绝
	 **/
	private void notifyRefuseMember(Player player, String targetId, String... params) {
		PlayerSession ps = room.getSessions().get(player.getId());
		if (ps != null) {
			ps.onEvent(Events.event("teamNotify", createEventSource(TeamConstants.TEAM_REFUSE_MEMBER, params)));
		} else {
			DefaultPlayer offlinePlayer = cacheService.getCache(targetId, DefaultPlayer.class);
			if (offlinePlayer != null) {
				offlinePlayer.getTeam().removeApplyTeam(params[0]);
				cacheService.putObject(offlinePlayer);
				offlinePlayer = null;
			}
		}
	}
	
	private void notifyDismissMember(Player player, String targetId, String... params) {
		PlayerSession ps = room.getSessions().get(player.getId());
		if (ps != null) {
			ps.onEvent(Events.event("teamNotify", createEventSource(TeamConstants.TEAM_DISMISS_MEMBER, params)));
		} else {
			DefaultPlayer offlinePlayer = cacheService.getCache(targetId, DefaultPlayer.class);
			if (offlinePlayer != null) {
				offlinePlayer.getTeam().notifyDismiss(offlinePlayer,"teamName");
				cacheService.putObject(offlinePlayer);
				offlinePlayer = null;
			}
		}
	}
	
//	public void notifyCancelRolesFromArmy(Player player, String targetId, List<Hero> cancelHeros) {
//		PlayerSession ps = room.getSessions().get(targetId);
//		if (ps != null) {
//			ps.onEvent(Events.event("teamNotify", createEventSource(
//					TeamConstants.TEAM_CANCEL_ARMY_ROLE, DynamicJsonProperty.convertToArrayNode(cancelHeros))));
//		} else {}
//	}
	
//	public void notifyMembers(Player player, int notifyReason, String targetId, String... params) {
//		switch (notifyReason) {
//		case TeamConstants.TEAM_JOIN_MEMBER:
//			notifyJoinMember(player, targetId, params);
//			break;
//		case TeamConstants.TEAM_REFUSE_MEMBER:
//			notifyRefuseMember(player, targetId, params);
//			break;
//		case TeamConstants.TEAM_DISMISS_MEMBER:
//			notifyDismissMember(player, targetId, params);
//			break;
//		case TeamConstants.TEAM_CANCEL_ARMY_ROLE:
////			notifyCancelRolesFromArmy(player, targetId, cancelHeros);
//			break;
//		}
//	}
}
