package com.jelly.player;

import java.util.List;

import com.jelly.combat.context.CBConst.GroupDefine;

public class AttackerGroup extends BaseCombatGroup {

	public AttackerGroup(List<IFighter> fighters) {
		super(GroupDefine.attacker.ordinal(),fighters);
	}
	
}
