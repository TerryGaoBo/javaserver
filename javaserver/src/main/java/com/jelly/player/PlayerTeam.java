package com.jelly.player;

import io.nadron.app.Player;
import io.nadron.app.impl.OperResultType;

import java.util.ArrayList;
import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.config.AllGameConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.team.TeamManager;

public class PlayerTeam extends DynamicJsonProperty {

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("at")
	private List<String> applyTeams;
	
	@JsonProperty("lt")
	private String leaveTeam;
	
//	private static Logger logger = LoggerFactory.getLogger(PlayerTeam.class);
	
//	private List<Integer> warAttackers = Lists.newArrayList();
	
	public PlayerTeam() {
		this.name = "";
		this.applyTeams = Lists.newArrayList();
		this.leaveTeam = "";
	}
	
	// home controler
	public void resetClan(String name) {
		this.name = "";
		if (applyTeams.contains(name)) {
			applyTeams.remove(name);
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
		addChange("name", name);
	}
	
	public List<String> getApplyTeams() {
		return this.applyTeams;
	}
	
	public String getLeaveTeam() {
		return leaveTeam;
	}

	public void setLeaveTeam(String leaveTeam) {
		this.leaveTeam = leaveTeam;
		addChange("leaveTeam", this.leaveTeam);
	}

	public void setApplyTeams(List<String> applyTeams) {
		this.applyTeams = applyTeams;
		ArrayNode teams = jackson.createArrayNode();
		for (String team : this.applyTeams) {
			teams.add(team);
		}
		this.addChange("applyTeams", teams);
	}
	
	public void clearApplyTeams() {
		this.applyTeams.clear();
		this.addChange("applyTeams", jackson.createArrayNode());
	}
	
	public void updateMyTeam(String name) {
		setName(name);
		setApplyTeams(new ArrayList<String>());
	};
	
	public void removeApplyTeam(String name) {
		int here = applyTeams.indexOf(name);
		if (here > -1) {
			applyTeams.remove(here);
			setApplyTeams(applyTeams);
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public void leaveTeam(Player player) {
		int res = TeamManager.getSingleton().leaveTeam(player);
		if (res == MessageCode.OK) {
			setLeaveTeam(name);
			setName("");
		}
		player.sendResult(OperResultType.TEAM, res);
	}
	
	public void submitJoinTeamApply(Player player, String teamName) {
		if (!name.equals("")) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_DUPLICATE);
			return;
		}
		
		if (applyTeams.contains(teamName)) { // 处理可能的重复提交入团申请
			player.sendResult(OperResultType.TEAM, MessageCode.OK);
			return;
		}
		
		// 玩家同一时刻能够向多少个军团发出入团申请
		int canSubmitMax = AllGameConfig.getInstance().teams.getMaxSubmitJoinTeamApply();
		if (applyTeams.size() >= canSubmitMax) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_APPLY_JOIN_TOO_MUCH);
			return;
		}
		
		int res = TeamManager.getSingleton().submitJoinTeamApply(player, teamName);
		if (res == MessageCode.OK) {
			applyTeams.add(teamName);
			addChange("applyTeams", convertToArrayNode(applyTeams));
		}
		player.sendResult(OperResultType.TEAM, res);
	}
	
	public void undoJoinTeamApply(Player player, String teamName) {
		int here = applyTeams.indexOf(teamName);
		if (here == -1) {
			player.sendResult(OperResultType.TEAM, MessageCode.FAIL);
			return;
		}
		int res = TeamManager.getSingleton().undoJoinTeamApply(player, teamName);
		if (res == MessageCode.OK) {
			applyTeams.remove(here);
			addChange("applyTeams", convertToArrayNode(applyTeams));
		}
		player.sendResult(OperResultType.TEAM, res);
	}
	
	public void refuseJoinTeamApply(Player player, String refusePlayerID) {
		if (isNoTeam()) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_MYTEAM);
			return;
		}
		TeamManager.getSingleton().refuseJoinTeamApply(player, refusePlayerID);
	}
	
	public void dissolveTeam(Player player) {
		if (name.equals("")) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		int res = TeamManager.getSingleton().dissolveTeam(player, name);
		if (res == MessageCode.OK) {
			setLeaveTeam(name);
			setName("");
		}
		player.sendResult(OperResultType.TEAM, res);
	}
	
	public void modifyTeamProfile(Player player, String intro, String announ, Integer joinLv,Integer pinzhi,Integer dengji) {
		if (name.equals("")) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		TeamManager.getSingleton().modifyTeamProfile(player, intro, announ, joinLv,pinzhi,dengji);
	}
	
	public void sendRoleToArmy(Player player, List<Integer> hids) {
		if (name.equals("")) {
			player.sendResult(OperResultType.TEAM, MessageCode.TEAM_NO_EXIST);
			return;
		}
		
		TeamManager.getSingleton().sendRoleToTeamArmy(player, hids);
		
//		if (TeamManager.getSingleton().sendRoleToTeamArmy(player, hids)) {
//			player.getHeros().removeHeros(player, hids);
//			int newMax = player.getHeros().changeMaxCount(-hids.size());
//			logger.info("忍者背包最大容量=" + newMax);
//		}
	}
	
	public void addExp(int exp) {
		if (name.equals("")) {
			return;
		}
		TeamManager.getSingleton().addTeamExp(name, exp);
	}
	
	public void notifyDismiss(Player player,String teamName) {
		player.getMail().addSysMail(
				MessageCode.MAIL_TITLE_TEAM_KICK_MEMBER, 
				MessageCode.MAIL_CONTENT_TEAM_KICK_MEMBER, 
				name);
//		name = "";
//		leaveTeam = teamName;
//		addChange("name", name);
		kickMemberFromTeam();
	}
	
	public void kickMemberFromTeam()
	{
		setLeaveTeam(name);
		setName("");
	}
	
	@Override
	public String getKey() {
		return "team";
	}

	@Override
	public JsonNode toWholeJson() {
		ObjectNode info = jackson.createObjectNode();
		info.put("name", name);
		info.put("applyTeams", convertToArrayNode(applyTeams));
		return info;
	}
	
	private boolean isNoTeam() {
		return name.equals("");
	}

//	public static void main(String[] args) {
//		System.out.println(DynamicJsonProperty.convertToArrayNode(Lists.newArrayList("archer", "saber", "caster")));
//	}
}
