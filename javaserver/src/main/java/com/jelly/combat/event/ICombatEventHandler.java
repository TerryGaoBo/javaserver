package com.jelly.combat.event;


public interface ICombatEventHandler{
	
	public boolean checkEvent();

	public void onEvent(int eventType);

}
