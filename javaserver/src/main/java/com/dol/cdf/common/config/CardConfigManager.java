package com.dol.cdf.common.config;

import java.util.List;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.bean.Card;
import com.fasterxml.jackson.core.type.TypeReference;

public class CardConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_FORM = "Card.json";
	private static final String JSON_FILE_IOS_FORM = "Card_ios.json";

	private List<Card> cardList;

	@Override
	public void loadConfig() {
		cardList = readConfigFile(AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE ? 
				JSON_FILE_IOS_FORM : JSON_FILE_FORM, new TypeReference<List<Card>>() {
		});
	}

	public List<Card> getCardList() {
		return cardList;
	}

}
