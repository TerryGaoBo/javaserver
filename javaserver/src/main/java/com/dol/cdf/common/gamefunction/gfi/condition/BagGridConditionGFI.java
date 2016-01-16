package com.dol.cdf.common.gamefunction.gfi.condition;

import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.ItemConstant;
import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.gfi.BaseGameFunctionInterface;
import com.dol.cdf.common.gamefunction.gfi.GFIId;
import com.dol.cdf.common.gamefunction.gfi.IConditionGFI;
import com.dol.cdf.common.gamefunction.parameter.IGameFunctionParameter;

public class BagGridConditionGFI extends BaseGameFunctionInterface implements IConditionGFI {

	public BagGridConditionGFI() {
		super(GFIId.GFI_CONDITION_CHECK_BAG_GRID);
	}

	@Override
	public int eval(IGameFunctionParameter paramter, GameContext context) {
		String[] type = (String[]) paramter.getParamter("type");
		int[] count = (int[]) paramter.getParamter("count");
		for (int i = 0; i < type.length; i++) {
			int id = Integer.parseInt(type[i]);
			Short containerId = AllGameConfig.getInstance().items.getItemContainerId(id);
			Integer stackmax = AllGameConfig.getInstance().items.getBaseItem(id).getStackmax();
			if (stackmax == null) {
				stackmax = 1;
			}
			int j = count[i] % stackmax != 0 ? 1 : 0;
			int needCount = count[i] / stackmax + j;
			boolean isOk = context.getPlayer().getInvenotry().needEmptyCount(containerId, needCount);
			if (!isOk) {
				return ItemConstant.BAG_FULL_CODE[containerId];
			}
		}
		return MessageCode.OK;

	}

}
