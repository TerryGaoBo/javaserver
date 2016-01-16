package com.dol.cdf.common.gamefunction.gfi.effect;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.BaseGameFunctionInterface;
import com.dol.cdf.common.gamefunction.gfi.IEffectGFI;

public abstract class BaseBuffEffectGFI extends BaseGameFunctionInterface implements IEffectGFI{

	public static final int UN_LIMIT_RATIO = 999;
	
	public BaseBuffEffectGFI(int id) {
		super(id);
	}

	
	public int getTurnNumber(){
		int[] ratios = (int[]) parameter.getParamter("ratios");
		if (ratios == null) {
			return 1;
		}else if(ratios[0] == UN_LIMIT_RATIO) {
			return UN_LIMIT_RATIO;
		}else {
			return ratios.length;
		}
	}
	
	
	public int getEvent(){
		return (Integer)parameter.getParamter("when");
	}
	
	
	@Override
	public void execute(GameContext context) {
		Integer act = (Integer)parameter.getParamter("act");
		if(act != null && act != 0){
//			context.getBuffOwner().unregigsterEventHandler(context.getBuffOwner(), getEvent());
		}
		
	}
	
	/**
	 * 获得当前回合的百分比例
	 * @param idx
	 * @return
	 */
	protected  int getRadio(int idx) {
		int[] ratios = (int[]) parameter.getParamter("ratios");
		if(ratios[0] == UN_LIMIT_RATIO) {
			return ratios[1];
		}
		return ratios[idx];
	}
	
	
	
	
}
