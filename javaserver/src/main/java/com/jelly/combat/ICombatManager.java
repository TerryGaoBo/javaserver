package com.jelly.combat;

import com.jelly.player.ICombatGroup;

public interface ICombatManager {

	public ICombatGroup getAttackerGroup();

	public ICombatGroup getDefenderGroup();

}
