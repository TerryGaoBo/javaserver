package com.dol.cdf.common.gamefunction.gfi.effect;

import io.nadron.app.Player;

import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.BaseGameFunctionInterface;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.dol.cdf.common.gamefunction.gfi.IEffectGFI;
import com.jelly.hero.Hero;

public class GiveExpEffectGFI extends BaseGameFunctionInterface implements IEffectGFI {
	
	public GiveExpEffectGFI()
	{
		super(GFIId.ADD_EXP_ITEM_TYPE);
	}
	
	@Override
	public void cancel(GameContext context) {
		// nothing to do 
	}

	@Override
	public void execute( GameContext context) {
		Integer exp = (Integer) parameter.getParamter("exp");
		String param = context.getItemUseParam();
		String [] kv = param.split(",");
		int heroid = Integer.parseInt(kv[0]);
		int nums = Integer.parseInt(kv[1]);
		Player player = context.getPlayer();
		Hero hero = player.getHeros().getHero(heroid);
		if(hero == null){
			return;
		}
		
		int expValue = exp.intValue() * nums;
		hero.addExp(expValue, player);
		player.getHeros().appendChangeMap("hes", heroid, hero.toJson());
	}
}
