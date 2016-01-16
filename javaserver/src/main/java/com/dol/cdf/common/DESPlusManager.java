package com.dol.cdf.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.dol.cdf.common.bean.Card;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.config.AllGameConfig;
import com.google.common.collect.ArrayListMultimap;


public class DESPlusManager {
	
	public static Logger logger = Logger.getLogger(DESPlusManager.class);
	
	private static DESPlusManager instance;
	
	public static int fromId = 0;
	
	public static int toId = 9999999;
	
	private UsedCDKeyStore keyStore = new UsedCDKeyStore();
	
	private List<CardModel> cardDespList = new ArrayList<CardModel>();
	
	public static final String ALL_CHANNLE_ID = "-1";
	
	/**
	 * channel对应的card
	 */
	ArrayListMultimap<String, CardModel> channelCardMap = ArrayListMultimap.create(); 

	public static DESPlusManager getInstance(){
		if(instance == null){
			synchronized (DESPlusManager.class) {
				if(instance == null){
					instance = new DESPlusManager();
				}
			}
			
		}
		return instance;
	}


	public Collection<CardModel> getChannelCards(String channel){
		return channelCardMap.get(channel);
	}
	
	public CardModel getCardModelByChannel(String channel, String key) {
		Collection<CardModel> channelCards = getChannelCards(channel);
		if(channelCards != null) {
			for (CardModel card : channelCards) {
				try {
					Integer id = Integer.valueOf(card.getDesp().decrypt(key));
					if (id >= fromId && id <= toId) {
						return card;
					}
				} catch (Exception e1) {
					continue;
				}
			}
		}
		return null;
	}
	
	public DESPlusManager() {
		try {
			channelCardMap.clear();
			List<Card> cardList = AllGameConfig.getInstance().cards.getCardList();
			for (Card card : cardList) {
				CardModel cardModel = new CardModel(card);
				cardDespList.add(cardModel);
				if(cardModel.getChannel() == null) {
					channelCardMap.put(ALL_CHANNLE_ID, cardModel);
				}else {
					channelCardMap.put(cardModel.getChannel(), cardModel);
				}
				
			}
			UsedCDKeyStore storeValues = (UsedCDKeyStore)keyStore.readValues();
	        if(storeValues != null) keyStore = storeValues;
		} catch (Exception e) {
			logger.error("DESPlusManager",e);
		}
	}
	
	public CardModel getCardModel(String key) {
		for (CardModel card : cardDespList) {
			try {
				Integer id = Integer.valueOf(card.getDesp().decrypt(key));
				if (id >= fromId && id <= toId) {
					return card;
				}
			} catch (Exception e1) {
				continue;
			}
		}
		return null;
	}

	
	public synchronized boolean isUsed(String channel, String key){
		if(keyStore.containCDKey(channel,key)){
			return true;
		}
		return false;
	}
	
	public synchronized void addUsedKeys(String channel, String key){
		keyStore.addKey(channel,key);
	}
	
	public void writeUsedKeys(){
		keyStore.writevalues();
	}
	
	public static void main(String[] args) throws Exception {
		List<CardModel> cards = DESPlusManager.getInstance().cardDespList;
		ArrayListMultimap<String, String> map = ArrayListMultimap.create();
		ArrayListMultimap<String, String> mType = ArrayListMultimap.create();
		for (CardModel cardModel : cards) {
			List<String> lines = new ArrayList<String>();
			String first = null;
			for (int i = 0; i < 1; i++) {
				String encrypt = cardModel.getDesp().encrypt(i + "");
				//System.out.println(cardModel.getName() + " " + encrypt);
				try {
					if(first == null) {
						first = encrypt;
						map.put(first, cardModel.getName());
						mType.put(first, cardModel.getType());
					}
//					System.out.println(encrypt);
//					CardModel mediaCard = DESPlusManager.getInstance().getCardModel(encrypt);
//					System.out.println(i + "  " + mediaCard);
					lines.add(encrypt);
				} catch (Exception e) {
					logger.error("", e);
				}
			}
//			String ch =cardModel.getCard().getChannel() == null  ? "":cardModel.getCard().getChannel();
//			String name = cardModel.getName() == null ? "" : cardModel.getName();
//			FileUtils.writeLines(new File("cardCDKeys_"+cardModel.getMark()+"_"+ch+"_"+name+"_s_"+first+".txt"), lines);
		}
//		for (Entry<String, String> e : map.entries()) {
//			System.out.println(e.getKey() + " " + e.getValue());
//		}
		
		for (Entry<String, Collection<String>> e : map.asMap().entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
		for (Entry<String, Collection<String>> e : mType.asMap().entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
		

	}

	public static class CardModel {

		public static Logger logger = Logger.getLogger(CardModel.class);

		private Card card;

		DESPlus desp;

		public CardModel(Card card) {
			super();
			this.card = card;
			String runtime_env = System.getProperty("runtime.env", "s");
			if(logger.isDebugEnabled()) {
				//logger.debug("CardModel " + runtime_env);
			}
			
			String[] split = runtime_env.split("-");
			try {
				this.desp = new DESPlus(card.getType() + split[0]);
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		public DESPlus getDesp() {
			return desp;
		}

		public void setDesp(DESPlus desp) {
			this.desp = desp;
		}

		public Card getCard() {
			return card;
		}

		public void setCard(Card card) {
			this.card = card;
		}

		
		public String getType() {
			return card.getType();
		}

		public VariousItemEntry[] getReward() {
			return card.getReward();
		}

		public String getMark() {
			return card.getMark();
		}

		public String getName() {
			return card.getName();
		}

		public String getChannel() {
			return card.getChannel();
		}
	}

}
