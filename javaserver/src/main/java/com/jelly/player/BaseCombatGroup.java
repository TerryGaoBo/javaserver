package com.jelly.player;

import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class BaseCombatGroup implements ICombatGroup {

	List<IFighter> fighters;

	IFighter currentFighter;
	
	protected boolean isWin = false;
	
	
	public BaseCombatGroup(int idx ,List<IFighter> fighters) {
		this.fighters = fighters;
		for (IFighter fighter : this.fighters) {
			fighter.setIdx(idx);
		}
	}

	@Override
	public List<IFighter> getFighters() {
		return fighters;
	}
	
	@Override
	public boolean isWin() {
		return isWin;
	}

	@Override
	public boolean doFail() {
		isWin = false;
		return true;
	}
	
	@Override
	public boolean doWin() {
		isWin = true;
		return true;
	}

	@Override
	public void active() {
		IFighter iFighter = getCurrentFighter();
		iFighter.activeFight();
		if (iFighter.getEnemy() != null) {
			iFighter.getEnemy().setEnemy(iFighter);
			iFighter.getEnemy().activeFight();
			
		}
	}

	
	@Override
	public void unActive(){
		IFighter iFighter = getCurrentFighter();
//		iFighter.cancelBuffs();
	}

	@Override
	public IFighter getNextFighter(){
		if(currentFighter == null){
			currentFighter = getFirstFighter();
//			newFighterCome();
		}
		if(!currentFighter.isDead()){
			return currentFighter;
		}
		int indexOfCF = fighters.indexOf(currentFighter);
		if(indexOfCF >= fighters.size() - 1){
			return null;
		}else{
			//找到下一个玩家并保存设置敌人
			IFighter tempEnemy = currentFighter.getEnemy();
			currentFighter = fighters.get(indexOfCF + 1);
			currentFighter.setEnemy(tempEnemy);
			newFighterCome();
			return currentFighter;
		}
	}
	
	public void newFighterCome(){
		active();
		
	}
	
	@Override
	public IFighter getCurrentFighter(){
		return currentFighter;
	}
	
	@Override
	public IFighter getFirstFighter(){ 
		return fighters.get(0);
	}
	
	public ObjectNode toJson(){
		ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
		for (IFighter fighter : fighters) {
			ObjectNode json = ((BaseFighter)fighter).toJson();
			array.add(json);
		}
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("roleList", array);
		return obj;
	}
	
	@Override
	public boolean isEmptyGroup() {
		return fighters == null || fighters.isEmpty();
	}
}
