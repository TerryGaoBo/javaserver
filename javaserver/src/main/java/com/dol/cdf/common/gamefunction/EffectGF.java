package com.dol.cdf.common.gamefunction;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.IEffectGFI;

public class EffectGF extends GameFunction implements IEffectGF{

	private final IEffectGFI gfi ;

	/**
	 * @param gfi
	 */
	public EffectGF(IEffectGFI gfi) {
		super();
		this.gfi = gfi;
	}


	@Override
	public void execute(GameContext context) {
		gfi.execute(context);

	}


	@Override
	public void cancel(GameContext context) {
		gfi.cancel( context);

	}


	public IEffectGFI getGfi() {
		return gfi;
	}


}
