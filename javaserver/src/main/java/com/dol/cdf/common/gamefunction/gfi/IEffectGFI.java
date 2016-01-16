package com.dol.cdf.common.gamefunction.gfi;

import com.dol.cdf.common.context.GameContext;

public interface IEffectGFI extends IGameFunctionInterface{
	
	/**
	 * 效果执行
	 */
	public void execute(GameContext context);
	
	/**
	 * 取消
	 */
	public void cancel(GameContext context);
	

}
