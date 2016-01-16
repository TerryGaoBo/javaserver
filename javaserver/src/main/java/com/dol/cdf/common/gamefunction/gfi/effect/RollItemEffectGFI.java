package com.dol.cdf.common.gamefunction.gfi.effect;

import io.nadron.app.impl.OperResultType;

import java.util.ArrayList;
import java.util.List;

import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.DropGroupConfigManager;
import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.BaseGameFunctionInterface;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.dol.cdf.common.gamefunction.gfi.IEffectGFI;
import com.dol.cdf.log.LogConst;

public class RollItemEffectGFI extends BaseGameFunctionInterface implements IEffectGFI{

	/**
	 * @param id
	 */
	public RollItemEffectGFI() {
		super(GFIId.GFI_EFFECT_ROLL_ITEM);
	}

	@Override
	public void cancel(GameContext context) {
		// nothing to do 
	}

	@Override
	public void execute(GameContext context) {
		int[] drop_groups = (int[]) parameter.getParamter("drop_groups");
		List<VariousItemEntry> awards = new ArrayList<VariousItemEntry>();
		DropGroupConfigManager drop = AllGameConfig.getInstance().drops;
		for (int i = 0; i < drop_groups.length; i++) {
			VariousItemEntry vItem = drop.getVariousItemByGroupId(drop_groups[i]);
			if (vItem != null) {
				awards.add(vItem);
			}
		}
		if (awards.isEmpty() == false ) {

			VariousItemUtil.doBonus(context.getPlayer(), awards, LogConst.OPER_TYPE_ROLLITEM, true);
			context.getPlayer().sendResult(OperResultType.ROLL_ITEM,VariousItemUtil.itemToJson(awards));

		}
		
	}
	
	

}
