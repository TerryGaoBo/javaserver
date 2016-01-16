package com.dol.cdf.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.bean.Formula;
import com.fasterxml.jackson.core.type.TypeReference;

public class FormulaConfigManager extends BaseConfigLoadManager {
	private final Map<Integer, Formula> formulas=new HashMap<Integer, Formula>();
	
	private static final String FORMULA_JSON = "Formula.json";
	
	@Override
	public void loadConfig() {
		List<Formula> list = readConfigFile(FORMULA_JSON, new TypeReference<List<Formula>>() {
		});
		for (Formula formula : list) {
			formulas.put(formula.getId(),formula);
		}
	}
	public Map<Integer, Formula> getFormulas() {
		return formulas;
	}
	
	public Formula getFormula(int id) {
		return formulas.get(id);
	}

}
