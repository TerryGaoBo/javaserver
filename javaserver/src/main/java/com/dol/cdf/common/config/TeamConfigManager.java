package com.dol.cdf.common.config;

import java.util.List;
import java.util.Map;

import com.dol.cdf.common.bean.Guild;
import com.dol.cdf.common.bean.GuildWar;
import com.dol.cdf.common.constant.GameConstId;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fasterxml.jackson.core.type.TypeReference;

public class TeamConfigManager extends BaseConfigLoadManager {

	private Map<Integer, Guild> allGuilds;
	private Map<Integer, GuildWar> allGuildWars;
	
	public TeamConfigManager() {
		allGuilds = Maps.newHashMap();
		allGuildWars = Maps.newHashMap();
	}
	
	@Override
	public void loadConfig() {
		allGuilds = Maps.newHashMap();
		allGuildWars = Maps.newHashMap();
		
		List<Guild> guilds = readConfigFile(
				"Guild.json", 
				new TypeReference<List<Guild>>() {}
				);
		for (Guild guild : guilds) {			
			allGuilds.put(guild.getLv(), guild);
		}
		
		List<GuildWar> guildWars = readConfigFile(
				"GuildWar.json", 
				new TypeReference<List<GuildWar>>() {}
				);
		for (GuildWar guildWar : guildWars) {			
			allGuildWars.put(guildWar.getLv(), guildWar);
		}
	}
	
	public Guild getGuild(int lv) {
		if (!allGuilds.containsKey(lv)) {
			logger.error("guild#Err illegal level " + lv);
			return null;
		}
		return allGuilds.get(lv);
	}
	
	public int getMaxMembers(int lv) {
		if (!allGuilds.containsKey(lv)) {
			logger.error("guild#Err illegal level " + lv);
			return 0;
		}
		return allGuilds.get(lv).getMaxnumber();
	}
	
	public int getMaxSubmitJoinTeamApply() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_MAX_APPLY_TEAMS);
	}
	
	public int getMaxAnnounWords() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_MAX_ANNOUNCE_LEN);
	}
	
	public int getMaxIntroWords() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_MAX_INTRO_LEN);
	}
	
	public int[] getJoinTeamLevelConds() {
		return (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_JOIN_LEVEL);
	}
	
	public int[] getMaxNameWords() {
//		int[] nameConds = (int[])gconst.getConstant(GameConstId.TEAM_NAME_LEN);
		return (int[]) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_NAME_LEN);
	}
	
	public int getCreateTeamLevel() {
		return (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_CREATE_VILLAGE_LEVEL);
	}
	
	public int[] getCreateTeamCosts() {
		return (int[]) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_CREATE_COSTS);
	}
	
	/**
	 * 获取军团升级所需经验
	 * 
	 * @param lv - 军团等级
	 * @return 返回升至下一级需要的经验, 返回0表示已经达到最高等级
	 **/
	public int getUpgradeExp(int lv) {
		Guild guild = allGuilds.get(lv);
		if (guild == null) {
			return 0;
		}		
		return guild.getExp();
	}

	public int turnWealth(int gold, int silver) {
		int[] rates = (int[])AllGameConfig.getInstance().gconst.getConstant(GameConstId.TEAM_DONATE_GOLD_CONV_WEALTH);
		return gold * rates[1];
	}
	
	public int getMaxWealth(int lv) {
		return allGuilds.get(lv).getMaxmoney();
	}
	
	public int getMaxArmy(int lv) {
		return allGuilds.get(lv).getGuildarmy();
	}
	
	public int getDonateWealthTimes(int lv) {
		return 0;
	}
	
	public int[] getUpgradeCosts(int lv) {
		if (!allGuilds.containsKey(lv)) { // Safety check!!!
			return null;
		}
		return allGuilds.get(lv).getUpgrade();
	}
	
	/**
	 * 返回对应据点的奖励信息（进攻计分，防守计分，进攻贡献，防守贡献，进攻经验，防守经验）
	 * @param lv
	 * @param strongHoldId
	 * @return
	 */
//	public List<Integer> getGuildWar(int lv,int strongHoldId) {
//		if (!allGuildWars.containsKey(lv)) {
//			logger.error("guildWar#Err illegal level " + lv);
//			return null;
//		}
//		List<Integer> list = Lists.newArrayList();
//		list.add(allGuildWars.get(lv).getAttackpoint());
//		list.add(allGuildWars.get(lv).getDefendpoint());
//		list.add(allGuildWars.get(lv).getOncewincontribution());
//		list.add(allGuildWars.get(lv).getOncelosecontribution());
//		
//		list.add(allGuildWars.get(lv).getOverwincontribution());
//		list.add(allGuildWars.get(lv).getOverlosecontribution());
//		
//		list.add(allGuildWars.get(lv).getWinguildexp());
//		list.add(allGuildWars.get(lv).getWinmoney());
//		
//		return list;
//	}
	public GuildWar getGuildWarInfo(int lv){
		return allGuildWars.get(lv);
	}
	public Integer getGuildWarIdx(int lv,int strongHoldId) {
		if (!allGuildWars.containsKey(lv)) {
			logger.error("guildWar#Err illegal level " + lv);
			return -1;
		}
		int []ap = allGuilds.get(lv).getArmyposition();
		for(int i=0;i<ap.length;i++){
			if(ap[i]==strongHoldId){
				return i;
			}
		}
		return -1;
	}
}
