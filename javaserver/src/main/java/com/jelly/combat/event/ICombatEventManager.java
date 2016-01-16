package com.jelly.combat.event;

public interface ICombatEventManager {
	
	public void dispatchEvent(int eventType);
	
	public void registerEventHandler(ICombatEventHandler handler, int eventType, int priority);
	
	public void unregigsterEventHandler(ICombatEventHandler handler, int eventType);
	
	public void unregigsterAll();
	
	public void removeTargetEventHanler(int eventType);
	
	
}
