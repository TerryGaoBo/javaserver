package com.dol.cdf.common.gamefunction;

import com.dol.cdf.common.context.GameContext;


public interface IConditionGF extends IGameFunction{
	
	/**
	 * 条件检查
	 * 
	 * @return
	 */
	public int eval(GameContext context);
	
}
