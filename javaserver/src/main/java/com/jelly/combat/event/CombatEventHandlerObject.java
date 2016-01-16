package com.jelly.combat.event;

public class CombatEventHandlerObject implements Comparable<CombatEventHandlerObject> {

	private int priority;
	private ICombatEventHandler handler;

	public CombatEventHandlerObject(ICombatEventHandler handler, int priority) {
		this.handler = handler;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public ICombatEventHandler getHandler() {
		return handler;
	}

	@Override
	public int compareTo(CombatEventHandlerObject arg0) {
		return arg0.getPriority() - priority;
	}
}