package com.dol.cdf.common.config;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Gift;
import com.dol.cdf.common.bean.NewGift;
import com.dol.cdf.common.bean.Plan;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GiftConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_FORM = "Gift.json";
	
//	private ArrayListMultimap<Integer, Gift> giftMap;
	
	private Map<Integer, Gift> giftMap;
	
	private Map<Integer, GiftInfo> giftInfoMap;
	
	private static final String JSON_NEW_GIFT_JSON="NewGift.json";
	private static final String JSON_PLAN_JSON="Plan.json";
	
	@Override
	public void loadConfig() {
//		giftMap = ArrayListMultimap.create();
		giftMap = Maps.newHashMap();
		giftInfoMap = Maps.newHashMap();
//		List<Gift> list = readConfigFile(JSON_FILE_FORM, new TypeReference<List<Gift>>() {});
//		for (Gift gift : list) {
//			//关闭的不在列表中
//			if(gift.getClose() == null) {
//				giftMap.put(gift.getId(), gift);
//			}
//		}
		
		Map<Integer, NewGift> actMap = Maps.newHashMap();
		List<NewGift> listAct = readConfigFile(JSON_NEW_GIFT_JSON, new TypeReference<List<NewGift>>() {});
		for(NewGift act : listAct){
			actMap.put(act.getId(), act);
		}
		
		List<Plan> listPlan = readConfigFile(JSON_PLAN_JSON, new TypeReference<List<Plan>>() {});
		
		List<Plan> planKaifu = Lists.newArrayList();
		for (Plan plan : listPlan){
			
			if(plan.getType() != 2 || plan.getClose() != null){
				continue;
			}
			
			if(plan.getActivity_gift_id() == null || plan.getActivity_gift_id().length ==0){
				continue;
			}
			
			if(plan.getStartDay() != null && plan.getEndDay() != null){
				planKaifu.add(plan);
				continue;
			}
			
			for(int ts = 0;ts<plan.getActivity_gift_id().length;ts++){
				int id = plan.getActivity_gift_id()[ts];
				NewGift nact = actMap.get(id);
				
				Gift gift = new Gift();
				gift.setId(nact.getId());
				gift.setClose(plan.getClose());
				gift.setStartDay(plan.getStartDay());
				gift.setEndDay(plan.getEndDay());
				gift.setStartTime(stringRevertFunc(plan.getStartTime()));
				gift.setEndTime(stringRevertFunc(plan.getEndTime()));
				
				gift.setType(nact.getType());
				gift.setIdx(nact.getIdx());
				gift.setImg(nact.getImg());
				gift.setDesc1(nact.getDesc1());
				gift.setDesc2(nact.getDesc2());
				gift.setItems(nact.getItems());
				gift.setValue(nact.getValue());
				gift.setTip(nact.getTip());
				gift.setOldOpen(nact.getOldOpen());
				
				giftMap.put(gift.getId(), gift);
			}
		}
		
		for (Plan plan : planKaifu){
			for(int ts = 0;ts<plan.getActivity_gift_id().length;ts++){
				int id = plan.getActivity_gift_id()[ts];
				NewGift nact = actMap.get(id);
				Gift gift = giftMap.get(nact.getId());
				if(gift == null){
					gift = new Gift();
				}
				gift.setId(nact.getId());
				gift.setStartDay(plan.getStartDay());
				gift.setEndDay(plan.getEndDay());
				giftMap.put(gift.getId(), gift);
			}
		}
		
		
//		for(Entry<Integer,Collection<Gift>> entry : getAllGift().entrySet()){
//			int giftId = entry.getKey();
//			List<Gift> gifts = Lists.newArrayList(entry.getValue());
//			GiftInfo giftInfo = new GiftInfo(gifts.get(0));
//			giftInfoMap.put(giftId, giftInfo);
//		}
		
		for(Entry<Integer, Gift> entry : giftMap.entrySet())
		{
			int giftId = entry.getKey();
			Gift gift = entry.getValue();
			GiftInfo giftInfo = new GiftInfo(gift);
			giftInfoMap.put(giftId, giftInfo);
		}
		
//		logger.info("size == "+giftInfoMap.size()+" ="+giftMap.size());
	}
	
	private String stringRevertFunc(String value)
	{
		if(value == null){
			return null;
		}
		String kk = "";
		if(value.equals("")){
			return kk;
		}
		
		String [] sk = value.split(" ");
		for(int i = sk.length-1;i>=0;i--){
			if(sk[i].equals("")){
			}else{
				kk = kk+sk[i] + " ";
			}
		}
		
		kk = kk.trim();
		return kk;
	}
	
	public GiftInfo getGiftInfo(int id) {
		return giftInfoMap.get(id);
	}
	
	public Collection<GiftInfo> getAllGiftInfo(){
		return giftInfoMap.values();
	}
	
//	public Map<Integer, Collection<Gift>> getAllGift() {
//		return giftMap.asMap();
//	}
	
	public Map<Integer, Gift> getAllGift() {
		return giftMap;
	}

	public Gift getGift(int id, int idx) {
//		return getGiftList(id).get(idx);
		return giftMap.get(id);
	}
	
//	public List<Gift> getGiftList(int id){
//		return giftMap.get(id);
//	}
	
//	public int getGiftlength(int id) {
//		return getGiftList(id).size();
//	}
	
	public static class GiftInfo{
		
		Gift gift;
		
		String startDayString;
		
		String endDayString;
		
		public GiftInfo(Gift gift) {
			super();
			this.gift = gift;
		}
		
		public String getStartDayString() {
			return startDayString;
		}
 
		public void setStartDayString(Date startDay) {
			this.startDayString = TimeUtil.formatChineseTime(startDay);
		}
 
		public String getEndDayString() {
			return endDayString;
		}
 
		public void setEndDayString(Date endDay) {
			this.endDayString = TimeUtil.formatChineseTime(endDay);;
		}
 

		public Integer getId() {
			return gift.getId();
		}

		public Integer getStartDay() {
			return gift.getStartDay();
		}

		public Integer getEndDay() {
			return gift.getEndDay();
		}

		public String getStartTime() {
			return gift.getStartTime();
		}

		public String getEndTime() {
			return gift.getEndTime();
		}

		public Integer getClose() {
			return gift.getClose();
		}

		public Integer getType() {
			return gift.getType();
		}
		
		public Integer getOldOpen(){
			 return gift.getOldOpen();
		}
		
	}
	
}