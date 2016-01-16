package com.dol.cdf.common.gamefunction;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.IConditionGFI;

public class ConditionGF extends GameFunction implements IConditionGF{

	private IConditionGFI gfi;
	
	/**
	 * @param gfi
	 */
	public ConditionGF(IConditionGFI gfi) {
		this.gfi = gfi;
	}



	@Override
	public int eval(GameContext context) {
		return gfi.eval(parameter,context);
	}

}
