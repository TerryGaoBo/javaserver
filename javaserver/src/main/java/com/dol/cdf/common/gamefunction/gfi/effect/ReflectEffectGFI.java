package com.dol.cdf.common.gamefunction.gfi.effect;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.jelly.combat.CombatResultType;
import com.jelly.combat.context.CBContext;

public class ReflectEffectGFI extends BaseBuffEffectGFI {

	public ReflectEffectGFI() {
		super(GFIId.GFI_EFFECT_REFLECT);
	}

	@Override
	public void cancel(GameContext context) {
		// nothing to do
	}
	
	@Override
	public void execute(GameContext context) {
		CBContext cb = context.getBuffOwner().getCBContext();
		float damagePoint = cb.getCombatResultValue(CombatResultType.DAMAGE_POINT);
		int ratioInteger = getRadio(context.getI());
		int when = (Integer) parameter.getParamter("when");
		float ratio = ratioInteger / 100f;
		//死亡后就不可以进行反弹
		if(when == context.getE() && !context.getBuffOwner().isDead()){
			damagePoint = damagePoint*ratio;
			cb.putCombatResultBoolean(CombatResultType.IF_REFLECT, true);
			cb.getAttacker().addHp(-(int)damagePoint);
			cb.setChp(-(int)damagePoint);
			cb.setCskill(context.getS().getSkill().getId());
		}
			
	}


}
