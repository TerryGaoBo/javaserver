package com.dol.cdf.common.gamefunction.gfi.effect;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.jelly.combat.CombatResultType;
import com.jelly.combat.context.CBContext;

public class BanActionEffectGFI extends BaseBuffEffectGFI {

	public BanActionEffectGFI() {
		super(GFIId.GFI_EFFECT_BAN_ACTION);
	}

	@Override
	public void cancel(GameContext context) {
		// nothing to do
	}
	
	@Override
	public int getTurnNumber(){
		return (Integer)parameter.getParamter("turn_num");
	}

	@Override
	public void execute(GameContext context) {
		CBContext cb = context.getCBContextParam();
		int when = (Integer) parameter.getParamter("when");
		if(when == context.getE()){
			cb.putCombatResultBoolean(CombatResultType.BAN_ACTION, true);
		}

		
	}


}
