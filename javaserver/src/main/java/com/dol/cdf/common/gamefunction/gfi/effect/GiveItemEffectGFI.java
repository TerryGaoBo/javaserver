package com.dol.cdf.common.gamefunction.gfi.effect;

import io.nadron.app.impl.OperResultType;

import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.BaseGameFunctionInterface;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.dol.cdf.common.gamefunction.gfi.IEffectGFI;
import com.dol.cdf.log.LogConst;

public class GiveItemEffectGFI extends BaseGameFunctionInterface implements IEffectGFI{

	/**
	 * @param id
	 */
	public GiveItemEffectGFI() {
		super(GFIId.GFI_EFFECT_GIVE_ITEM);
	}

	@Override
	public void cancel(GameContext context) {
		// nothing to do 
	}

	@Override
	public void execute( GameContext context) {
		VariousItemEntry[] items =	(VariousItemEntry[])parameter.getParamter("items");
		if(context.getItemUseParam() != null && context.getItemUseParam().startsWith("bat")) {
			int count = Integer.parseInt(context.getItemUseParam().split("=")[1]);
			VariousItemEntry[] itemList = new VariousItemEntry[items.length];
			for (int i = 0; i < items.length; i++) {
				itemList[i] = new VariousItemEntry(items[i].getType(), items[i].getAmount() * count);
			}
			items = itemList;
		}
		VariousItemUtil.doBonus(context.getPlayer(), items, LogConst.GIVE_ITEM, true);
		context.getPlayer().sendResult(OperResultType.ROLL_ITEM,VariousItemUtil.itemToJson(items));
	}
	
	

}
