package com.dol.cdf.common.gamefunction.gfi.effect;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.jelly.combat.context.CBContext;

public class DebuffEffectGFI extends BaseBuffEffectGFI {

	public DebuffEffectGFI() {
		super(GFIId.GFI_EFFECT_DEBUFF);
	}

	@Override
	public void cancel(GameContext context) {
		// nothing to do
	}
	
	@Override
	public void execute(GameContext context) {
		CBContext cb = context.getBuffOwner().getCBContext();
		int when = (Integer) parameter.getParamter("when");
		//-1表示清楚所有的buff
		if(when == context.getE()){
			context.getBuffOwner().unregigsterAll();
			if(cb.getAttacker() == context.getBuffOwner()){
				cb.addCloseBufA(-1);
			}else{
				cb.addCloseBufB(-1);
			}
		}
		
		
			
	}


}
