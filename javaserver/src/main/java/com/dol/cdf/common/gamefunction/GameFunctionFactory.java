package com.dol.cdf.common.gamefunction;

import java.util.ArrayList;
import java.util.List;

import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.gamefunction.gfi.BaseGameFunctionInterface;
import com.dol.cdf.common.gamefunction.gfi.GFIFactory;
import com.dol.cdf.common.gamefunction.gfi.GFIModel;
import com.dol.cdf.common.gamefunction.gfi.GFIType;
import com.dol.cdf.common.gamefunction.gfi.IConditionGFI;
import com.dol.cdf.common.gamefunction.gfi.IEffectGFI;
import com.dol.cdf.common.gamefunction.parameter.GameFunctionParamter;

public class GameFunctionFactory {
	
	private static GameFunctionFactory instance;

	public static GameFunctionFactory getInstance() {
		if(instance == null)
		{
			synchronized (GameFunctionFactory.class) {
				if(instance == null){
					instance = new GameFunctionFactory();
				}
			}
		}
		return instance;
	}
	
	public IGameFunction createGF(int type,BaseGameFunctionInterface gfi){
		switch (type) {
		case GFIType.GFI_TYPE_CONDITION:
			
			return new ConditionGF((IConditionGFI)gfi);
			
		case GFIType.GFI_TYPE_EFFECT:
			
			return new EffectGF((IEffectGFI)gfi);

		default:
			throw new RuntimeException("GFI类型使用错误，未定义Type：" + type);
		}
	}
	
	public List<IGameFunction> create(String desc, int type){
		List<IGameFunction> gfList = new ArrayList<IGameFunction>();
		// 解析字符串
		if (desc == null || desc.trim().length() == 0) {
			return gfList;
		}
		String[] params = desc.split("\\|");
		
		// 每个param对应一个GameFunction
		for(String param : params){
			IGameFunction gf = createFunction(param.trim(), type);
			gfList.add(gf);
		}
		return gfList;
	}

	/**
	 * @param trim
	 * @param type
	 * @return
	 */
	private IGameFunction createFunction(String desc, int type) {
		String symbol = desc.substring(0,desc.indexOf("("));
		GFIModel gfi = AllGameConfig.getInstance().gfcm.getGFIBySymbol(symbol);
		if (gfi.isValidType(type) == false) {
			throw new RuntimeException("GFI类型使用错误，不允许使用Type：" + type);
		}
		BaseGameFunctionInterface create = GFIFactory.getInstance().create(gfi.getId());
		GameFunctionParamter param = new GameFunctionParamter(StringHelper.substringBetween(desc, "(", ")"),gfi. getParamTable());
		create.setParameter(param);
		IGameFunction gf = createGF(type, create);
		((GameFunction)gf).setParameter(param);
		
		return gf;
	}
}
