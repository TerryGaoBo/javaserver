package com.jelly.combat;

import io.nadron.app.Player;
import io.nadron.context.AppContext;
import io.nadron.example.lostdecade.LDRoom;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jelly.player.ICombatGroup;
import com.jelly.player.IFighter;
import com.jelly.team.Team;
import com.jelly.team.TeamManager;

public class WarHerosFightManager extends PVPCombatManager
{
	private static LDRoom ldRoom = AppContext.getBean(LDRoom.class);
	
	private static final Logger logger = LoggerFactory.getLogger(TeamManager.class);
	
	private Integer strongHoldId;
	private Integer warType; // 0 表示是攻打敌方 1 表示驱除敌方
	private Team hteam;
	private Team myTeam;
	Map<String, String> guidmap = null;
	
	public WarHerosFightManager(ICombatGroup attackerGroup,ICombatGroup defenderGroup, Player player,Team myTeam,Team hteam,Integer strongHoldId,Integer warType,Map<String, String> guidmap)
	{
		super(attackerGroup,defenderGroup,player);
		this.myTeam = myTeam;
		this.hteam = hteam;
		this.strongHoldId = strongHoldId;
		this.warType = warType;
		this.guidmap = guidmap;
		cb.setMirrorFight(true);
	}
	
	@Override
	public void afterFight() {
		super.afterFight();
		boolean win = this.getAttackerGroup().isWin();
		
		logger.info("军团战战斗结果：=====>"+win);
		if(win){
			myTeam.addWarAttReward(player);
		}else{
			hteam.addWarDefendRewardWars(guidmap);
		}
		
		int id = 0;
		for (IFighter fighter : getAttackerGroup().getFighters()) {
			int hp = fighter.getHp() < 0 ? 0:fighter.getHp();
			
			Integer index = fighter.getHero().getDefineID();
			
			float hpx = hp*1.0f/fighter.getHpMax();
			if(hpx<0.01){
				hpx = 0;
			}
			myTeam.updateWarBaseHeroDataAttack(player,index,hpx,win,this.strongHoldId,hteam,this.warType,id);
			id = id+1;
		}
		
		for (IFighter fighter : getDefenderGroup().getFighters()) {
			int hp = fighter.getHp() < 0 ? 0:fighter.getHp();
			
			Integer index = fighter.getHero().getDefineID();
			String guid = fighter.getHero().getDefineGuid();
			
			float hpx = hp*1.0f/fighter.getHpMax();
			if(hpx<0.01){
				hpx = 0;
			}
			hteam.updateWarBaseHeroDataDefend(index,hpx,win,this.strongHoldId,guid,myTeam,this.warType);
		}
	}
}
