package com.jelly.buff;

import com.jelly.combat.event.ICombatEventHandler;
import com.jelly.player.IFighter;

public interface IBuff extends ICombatEventHandler {

	public void onActive();

	public void onCancel();

	public IFighter getOwner();

	public void setOwner(IFighter hero);
	
	

}
