package com.jelly.player;

import java.util.List;

import com.jelly.combat.context.CBConst.GroupDefine;

public class DefenderGroup extends BaseCombatGroup {

	public DefenderGroup(List<IFighter> fighters) {
		super(GroupDefine.defender.ordinal(), fighters);
	}

}
