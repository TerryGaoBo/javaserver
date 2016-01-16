package com.dol.cdf.common.gamefunction.gfi;

import com.dol.cdf.common.gamefunction.parameter.GFIParameterTable;

public interface IGFIModel{
	
	public int getId();

	public String getSymbol();

	public GFIParameterTable getParamTable();
	
	public boolean isValidType(int type);
	

}
