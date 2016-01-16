package com.jelly.buff;

import com.dol.cdf.common.gamefunction.IEffectGF;
import com.jelly.hero.BaseSkill;
import com.jelly.player.IFighter;

public class BuffFacotry {

	public static BaseBuff createIntermittentBuff(IFighter fighter, IEffectGF effect, BaseSkill bs) {
		BaseBuff buff = new IntermittentBuff(effect,bs);
		buff.setOwner(fighter);
		return buff;
	}

}
