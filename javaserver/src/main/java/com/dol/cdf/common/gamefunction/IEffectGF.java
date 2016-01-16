package com.dol.cdf.common.gamefunction;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.IEffectGFI;


public interface IEffectGF extends IGameFunction{
	
	/**
	 * 执行效果
	 */
	public void execute(GameContext player);
	
	/**
	 * 取消
	 */
	public void cancel(GameContext player);
	
	public IEffectGFI getGfi();

}
