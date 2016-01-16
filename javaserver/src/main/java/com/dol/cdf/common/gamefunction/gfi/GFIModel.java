package com.dol.cdf.common.gamefunction.gfi;

import java.util.HashSet;
import java.util.Set;

import com.dol.cdf.common.bean.GameFunc;
import com.dol.cdf.common.gamefunction.parameter.GFIParameterTable;

public class GFIModel implements IGFIModel {
	private int id;
	private String symbol;
	private GFIParameterTable paramTable;
	private Set<Integer> validType = new HashSet<Integer>();

	
	public GFIModel(GameFunc gf) {
		id = gf.getId();
		symbol = gf.getSymbol();
		String paramTableStr = gf.getParamTable();
		if (paramTableStr != null) {
			setParamTable(paramTableStr);
		}
		String validTypeStr = gf.getValidType();
		setValidType(validTypeStr);
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public GFIParameterTable getParamTable() {
		return paramTable;
	}

	public void setParamTable(String paramTable) {
		this.paramTable = new GFIParameterTable(paramTable);
	}

	@Override
	public boolean isValidType(int type) {
		return validType.contains(type);
	}

	public void setValidType(String validType) {
		String[] types = validType.split("\\|");

		for (String type : types) {
			this.validType.add(Integer.parseInt(type));
		}

	}

}
