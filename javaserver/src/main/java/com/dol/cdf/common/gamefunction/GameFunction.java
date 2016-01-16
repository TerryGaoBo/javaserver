package com.dol.cdf.common.gamefunction;

import com.dol.cdf.common.gamefunction.parameter.GameFunctionParamter;

public class GameFunction implements IGameFunction{

	protected GameFunctionParamter parameter;

	public GameFunctionParamter getParameter() {
		return parameter;
	}

	public void setParameter(GameFunctionParamter paramter) {
		this.parameter = paramter;
	}


}
