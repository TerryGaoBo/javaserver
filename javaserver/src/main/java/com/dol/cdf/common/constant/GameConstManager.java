package com.dol.cdf.common.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.bean.Const;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BaseConfigLoadManager;
import com.fasterxml.jackson.core.type.TypeReference;

public class GameConstManager extends BaseConfigLoadManager {

	private Map<Integer, GameConst> constants;
	private Map<String, GameConst> constantsNameIdx;
	private static final String CONFIG_FILE_NAME = "Const.json";
	private static final String CONFIG_FILE_IOS_NAME = "Const_ios.json";

	/**
	 * 初始化常量
	 */
	private void initGameConst() {
		constants = new HashMap<Integer, GameConst>();
		constantsNameIdx = new HashMap<String, GameConst>();

		/**
		 * new
		 */
		registerConstant(GameConstId.CRIT_RATE, "CRIT_RATE", ConstTypeEnum.INT, "10");
		registerConstant(GameConstId.CRIT_MULTIPLE, "CRIT_MULTIPLE", ConstTypeEnum.FLOAT, "1.5");
		registerConstant(GameConstId.FRIENDS_MAX_NUM, "FRIENDS_MAX_NUM", ConstTypeEnum.INT, "100");
		registerConstant(GameConstId.ENERGY_INC, "ENERGY_INC", ConstTypeEnum.INT, "10");
		registerConstant(GameConstId.INIT_ENERGY_NUM, "INIT_ENERGY_NUM", ConstTypeEnum.INT, "200");
		registerConstant(GameConstId.ENERGY_COST, "ENERGY_COST", ConstTypeEnum.INT_LIST, "10|20|30|40|50|60|70|80|90|100|120|140|160|180|200");
		registerConstant(GameConstId.BUY_ENERGY_NUM, "BUY_ENERGY_NUM", ConstTypeEnum.INT, "200");
		registerConstant(GameConstId.SILVER_COST, "SILVER_COST", ConstTypeEnum.INT_LIST, "10|20|30|40|50|60|70|80|90|100|120|140|160|180|200");
		registerConstant(GameConstId.BUY_SILVER_NUM, "BUY_SILVER_NUM", ConstTypeEnum.INT, "50000");
		registerConstant(GameConstId.AID_INC, "AID_INC", ConstTypeEnum.INT_LIST, "10|10|50|10");
		
		registerConstant(GameConstId.BAG_EQP,"BAG_EQP",ConstTypeEnum.INT_LIST,"80|12|10|5");
		registerConstant(GameConstId.BAG_ITEM,"BAG_ITEM",ConstTypeEnum.INT_LIST,"80|12|10|5");
		registerConstant(GameConstId.BAG_MATERIAL,"BAG_MATERIAL",ConstTypeEnum.INT_LIST,"80|12|10|5");
		registerConstant(GameConstId.OPEN_SLOT_PRICE, "OPEN_SLOT_PRICE", ConstTypeEnum.INT, "20");
		registerConstant(GameConstId.MAX_QUALITY_LV,"MAX_QUALITY_LV",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.PER_LV_INC,"PER_LV_INC",ConstTypeEnum.FLOAT,"0.1");
		registerConstant(GameConstId.FORCE_SILVER,"FORCE_SILVER",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.TAKE_NUM,"TAKE_NUM",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.TAKE_SILVER,"TAKE_SILVER",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.RS_COIN,"RS_COIN",ConstTypeEnum.INT,"20");
		registerConstant(GameConstId.RS_ROLE_LEVEL,"RS_ROLE_LEVEL",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.COST_MUTI,"COST_MUTI", ConstTypeEnum.FLOAT_LIST, "1|3|5|10|20|40|80|160|1000000");
		registerConstant(GameConstId.FB_RESET_COST,"FB_RESET_COST", ConstTypeEnum.INT_LIST, "10|10|10|10");
		registerConstant(GameConstId.FIRST_NORMAL_REC,"FIRST_NORMAL_REC",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.FIRST_MID_REC,"FIRST_MID_REC",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.FUND_COST,"FUND_COST",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.SECOND_MID_REC,"SECOND_MID_REC",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.FIRST_MID_REC_CD,"FIRST_MID_REC_CD",ConstTypeEnum.INT,"10");
		registerConstant(GameConstId.PVP_COUNT_COST, "PVP_COUNT_COST", ConstTypeEnum.INT_LIST, "10|20");

		registerConstant(GameConstId.REFRESH_SHOPPING_COST,"REFRESH_SHOPPING_COST",ConstTypeEnum.INT_LIST,"50|50|75|75|100|100|100|150|150|150|200");
		registerConstant(GameConstId.REFRESH_BLACK_COST,"REFRESH_BLACK_COST",ConstTypeEnum.INT_LIST,"50|50|75|75|100|100|100|150|150|150|200");

		registerConstant(GameConstId.PVP_INC, "PVP_INC", ConstTypeEnum.INT, "5");
		registerConstant(GameConstId.OLD_SERVER_MAX, "OLD_SERVER_MAX", ConstTypeEnum.INT, "33");
		registerConstant(GameConstId.SKILL_STAR_UP_RATE, "SKILL_STAR_UP_RATE", ConstTypeEnum.FLOAT, "0.04");

		registerConstant(GameConstId.HIGH_VALUE_GOODS, "HIGH_VALUE_GOODS", ConstTypeEnum.INT, "5");
		registerConstant(GameConstId.SWEEP_COMMON_STAGE, "SWEEP_COMMON_STAGE", ConstTypeEnum.INT, "5");
		registerConstant(GameConstId.SWEEP_ELITE_STAGE, "SWEEP_ELITE_STAGE", ConstTypeEnum.INT, "5");
		registerConstant(GameConstId.ELITE_RESET_COST, "ELITE_RESET_COST", ConstTypeEnum.INT_LIST, "10|10|10|10");
		
		registerConstant(GameConstId.PREFER_IS_OPEN_MONTHCARD, "PREFER_IS_OPEN_MONTHCARD", ConstTypeEnum.INT, "0");
		registerConstant(GameConstId.PREFER_IS_OPEN_GIFTCODE, "PREFER_IS_OPEN_GIFTCODE", ConstTypeEnum.INT, "0");
		
		registerConstant(GameConstId.TEAM_PAGE_IN_MAX_TEAMS, "TEAM_PAGE_IN_MAX_TEAMS", ConstTypeEnum.INT, "8");
		registerConstant(GameConstId.TEAM_CREATE_COSTS, "TEAM_CREATE_COSTS", ConstTypeEnum.INT_LIST, "500|1000000");
		registerConstant(GameConstId.TEAM_CREATE_VILLAGE_LEVEL, "TEAM_CREATE_VILLAGE_LEVEL", ConstTypeEnum.INT, "40");
		registerConstant(GameConstId.TEAM_MAX_APPLY_TEAMS, "TEAM_MAX_APPLY_TEAMS", ConstTypeEnum.INT, "5");
		registerConstant(GameConstId.TEAM_JOIN_LEVEL, "TEAM_JOIN_LEVEL", ConstTypeEnum.INT_LIST, "15|100");
		registerConstant(GameConstId.TEAM_NAME_LEN, "TEAM_NAME_LEN", ConstTypeEnum.INT_LIST, "1|6");
		registerConstant(GameConstId.TEAM_MAX_ANNOUNCE_LEN, "TEAM_MAX_ANNOUNCE_LEN", ConstTypeEnum.INT, "60");
		registerConstant(GameConstId.TEAM_MAX_INTRO_LEN, "TEAM_MAX_INTRO_LEN", ConstTypeEnum.INT, "60");
	
		registerConstant(GameConstId.FIRST_PAY_RESET, "FIRST_PAY_RESET", ConstTypeEnum.INT, "0");
		registerConstant(GameConstId.TEAM_DELATE_PASS_VOTES, "TEAM_DELATE_PASS_VOTES", ConstTypeEnum.INT, "4");
		registerConstant(GameConstId.TEAM_DONATE_GOLD_CONV_WEALTH, "TEAM_DONATE_GOLD_CONV_WEALTH", ConstTypeEnum.INT_LIST, "1|5|5");
		registerConstant(GameConstId.REFRESH_SHOP_COST, "REFRESH_SHOP_COST", ConstTypeEnum.INT_LIST, "50|50|100|100|150");
		registerConstant(GameConstId.REFRESH_SHOP_TIMES, "REFRESH_SHOP_TIMES", ConstTypeEnum.INT, "5");
		
		registerConstant(GameConstId.REFRESH_NINJASHOP_COST, "REFRESH_NINJASHOP_COST", ConstTypeEnum.INT_LIST, "50|50|100|100|150");
		registerConstant(GameConstId.REFRESH_NINJASHOP_TIMES, "REFRESH_NINJASHOP_TIMES", ConstTypeEnum.INT, "5");
		
		registerConstant(GameConstId.REFRESH_ARENASHOP_COST, "REFRESH_ARENASHOP_COST", ConstTypeEnum.INT_LIST, "50|50|100|100|150");
		registerConstant(GameConstId.REFRESH_ARENASHOP_TIMES, "REFRESH_ARENASHOP_TIMES", ConstTypeEnum.INT, "5");
		
		registerConstant(GameConstId.WIN_ARENA_GET_POINT, "WIN_ARENA_GET_POINT", ConstTypeEnum.INT, "10");
		registerConstant(GameConstId.LOSE_ARENA_GET_POINT, "LOSE_ARENA_GET_POINT", ConstTypeEnum.INT, "2");
		registerConstant(GameConstId.SEND_NINJA_REWARD, "SEND_NINJA_REWARD", ConstTypeEnum.INT_LIST, "20|10");
		
	}

	public Object getConstant(int id) {
		GameConst constant = constants.get(id);
		if (constant != null) {
			return constant.getValue();
		}
		return null;
	}
	
	public boolean setConstant(int id, int value) {
		GameConst constant = constants.get(id);
		if (constant != null) {
			constant.setValue(String.valueOf(value));
			return true;
		}
		return false;
	}

	public Object getConstantByName(String name) {
		GameConst constant = constantsNameIdx.get(name);
		if (constant != null) {
			return constant.getValue();
		}
		return null;
	}

	/**
	 * 注册常量
	 * 
	 * @param id
	 * @param name
	 * @param type
	 * @param value
	 */
	private void registerConstant(int id, String name, ConstTypeEnum type, String value) {
		GameConst constant = new GameConst();
		constant.setId(id);
		constant.setType(type);
		constant.setValue(value);

		constants.put(id, constant);
		constantsNameIdx.put(name, constant);
	}

	public static void main(String[] args) {
		GameConstManager gcm = new GameConstManager();
		gcm.initGameConst();
	}

	@Override
	public void loadConfig() {
		initGameConst();
		List<Const> loadSingleConfig = readConfigFile(AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE 
				? CONFIG_FILE_IOS_NAME : CONFIG_FILE_NAME, new TypeReference<List<Const>>() {});
		for (Const cst : loadSingleConfig) {
			GameConst constant = constantsNameIdx.get(cst.getName());
			if (constant != null) {
				constant.setValue(cst.getValue());
			}
		}
	}
}
