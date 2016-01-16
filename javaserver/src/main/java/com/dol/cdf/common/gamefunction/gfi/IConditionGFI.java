package com.dol.cdf.common.gamefunction.gfi;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.parameter.IGameFunctionParameter;

public interface IConditionGFI extends IGameFunctionInterface{
	
	/**
	 * 条件检验
	 * 
	 * @param paramter
	 * @return
	 */
	public int eval(IGameFunctionParameter parameter, GameContext context);
	
}
