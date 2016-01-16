package com.dol.cdf.common.gamefunction.gfi.effect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.jelly.combat.context.CBContext;
import com.jelly.combat.result.CombatActionResultFactory;

public class RecoverEffectGFI extends BaseBuffEffectGFI {
	private static final Logger logger = LoggerFactory.getLogger(RecoverEffectGFI.class);
	public RecoverEffectGFI() {
		super(GFIId.GFI_EFFECT_RECOVER);
	}

	@Override
	public void cancel(GameContext context) {
		// nothing to do
	}
	
	@Override
	public void execute(GameContext context) {
		CBContext cb = context.getCBContextParam();
		int when = getEvent();
		//达到次数闪现则不触发
		Integer limit = context.getS().getSkill().getLimit();
		int recoverTimes = context.getBuffOwner().getRecoverTimes();
		if(context.getBuffOwner().getRecoverTimes() >= limit) {
			return;
		}
		if(when == context.getE()){
			int ratioInteger = getRadio(context.getI());
			float ratio = ratioInteger / 100f;
			if(Rnd.getRandomPercent(ratio)){
				context.getBuffOwner().setRecoverTimes(++recoverTimes);
				int hpMax = context.getBuffOwner().getHpMax();
				float hpPercent = (Integer)parameter.getParamter("hp")/100f;
				int recoverHp = (int)(hpMax * hpPercent);
				if(logger.isDebugEnabled()) {
					logger.debug("recoverHp:{},hpMax:{}",recoverHp,hpMax);
				}
				context.getBuffOwner().setHp(recoverHp);
				CombatActionResultFactory.createRecoverResult(cb, context.getBuffOwner(), context.getS().getSkill().getId(),recoverHp);
			}
			
		}

		
	}


}
