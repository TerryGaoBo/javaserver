package com.jelly.combat;

import io.nadron.app.Player;
import io.nadron.context.AppContext;
import io.nadron.example.lostdecade.LDRoom;

import com.dol.cdf.common.MessageCode;
import com.jelly.activity.WorldActivity;
import com.jelly.player.ICombatGroup;
import com.jelly.player.IFighter;

public class WarsCombatManager extends PVPCombatManager {

	private WorldActivity worldActivity;
	
	public static final String WIN_COIN = "10";
	public static final String FAIL_COIN = "5";
	
	public static final int WIN_COIN_INT = 10;
	
	private static LDRoom ldRoom = AppContext.getBean(LDRoom.class);

	public WarsCombatManager(ICombatGroup attackerGroup, ICombatGroup defenderGroup, Player player, int coin, WorldActivity worldActivity) {
		super(attackerGroup, defenderGroup, player);
		this.worldActivity = worldActivity;
		cb.setMirrorFight(true);
	}

	@Override
	public void afterFight() {
		
		super.afterFight();
		boolean win = this.getAttackerGroup().isWin();
		for (IFighter fighter : getAttackerGroup().getFighters()) {
			int hp = fighter.getHp() < 0 ? 0:fighter.getHp();
			player.getWars().setWarHeroHps(fighter.getUuid(), hp, hp*1.0f/fighter.getHpMax());
		}
		
		for (IFighter fighter : getDefenderGroup().getFighters()) {
			int hp = fighter.getHp() < 0 ? 0:fighter.getHp();
			player.getWars().setDefenderHps(fighter.getUuid(),hp*1.0f/fighter.getHpMax());
		}
		String defenderId = player.getWars().getDefenderId();
		int contentId;
		String rewardCoin;
		if (win) {
			player.getWars().addCurrPass();
			contentId = MessageCode.MAIL_WAR_CONTENT_FAIL;
			rewardCoin = FAIL_COIN;
		} else {
			player.getWars().addChange("currPass", player.getWars().getCurrPass());
			contentId = MessageCode.MAIL_WAR_CONTENT_WIN;
			rewardCoin = WIN_COIN;
		}
		if (worldActivity.isInWar(defenderId)) {
			String rewardItemString = "<coin;"+rewardCoin+">";
			ldRoom.sysGiveItem(rewardItemString, defenderId, MessageCode.MAIL_WAR_TITLE, contentId,0, player.getName(),rewardCoin);
			worldActivity.removeDefendWarPlayer(defenderId);
			player.getWars().addChange("defendStatus", 1);
		}
	}

}
