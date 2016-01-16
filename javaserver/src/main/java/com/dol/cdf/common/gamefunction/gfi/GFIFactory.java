package com.dol.cdf.common.gamefunction.gfi;

import com.dol.cdf.common.gamefunction.gfi.condition.BagGridConditionGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.AlterEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.BanActionEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.BanSkillEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.DebuffEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.GiveExpEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.GiveItemEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.RecoverEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.ReflectEffectGFI;
import com.dol.cdf.common.gamefunction.gfi.effect.RollItemEffectGFI;

/**
 *
 * @author zhoulei
 *
 */
public class GFIFactory {

	private static GFIFactory instance;

	public static GFIFactory getInstance() {
		if (instance == null) {
			instance = new GFIFactory();
		}
		return instance;
	}

	public BaseGameFunctionInterface create(int id) {
		BaseGameFunctionInterface gfi = null;
		switch (id) {
		case GFIId.GFI_EFFECT_ALTER:
			gfi = new AlterEffectGFI();
			break;
		case GFIId.GFI_EFFECT_BAN_SKILL:
			gfi = new BanSkillEffectGFI();
			break;
		case GFIId.GFI_EFFECT_BAN_ACTION:
			gfi = new BanActionEffectGFI();
			break;
		case GFIId.GFI_EFFECT_RECOVER:
			gfi = new RecoverEffectGFI();
			break;
		case GFIId.GFI_EFFECT_REFLECT:
			gfi = new ReflectEffectGFI();
			break;
		case GFIId.GFI_EFFECT_DEBUFF:
			gfi = new DebuffEffectGFI();
			break;
		case GFIId.GFI_CONDITION_CHECK_BAG_GRID:
			gfi = new BagGridConditionGFI();
			break;
		case GFIId.GFI_EFFECT_ROLL_ITEM:
			gfi = new RollItemEffectGFI();
			break;
		case GFIId.GFI_EFFECT_GIVE_ITEM:
			gfi = new GiveItemEffectGFI();
			break;
		case GFIId.ADD_EXP_ITEM_TYPE:
			gfi = new GiveExpEffectGFI();
			break;
		default:
			throw new RuntimeException("未注册的GFIID：" + id);
		}
		return gfi;
	}
}
