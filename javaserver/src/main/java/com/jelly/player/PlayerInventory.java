package com.jelly.player;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.BaseItem;
import com.dol.cdf.common.bean.Formula;
import com.dol.cdf.common.bean.Item;
import com.dol.cdf.common.bean.QualityRef;
import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.collect.IntList;
import com.dol.cdf.common.collect.TwoKeyHashMap;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.ItemConstant;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.common.context.GameContext;
import com.dol.cdf.common.gamefunction.IConditionGF;
import com.dol.cdf.common.gamefunction.IEffectGF;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.jelly.activity.ActivityType;
import com.jelly.hero.Hero;
import com.jelly.hero.ItemFactory;
import com.jelly.quest.TaskType;

public class PlayerInventory extends DynamicJsonProperty {
	private static final Logger logger = LoggerFactory.getLogger(PlayerInventory.class);
	/** 背包装备栏 */
	@JsonProperty("eq")
	private List<ItemInstance> equipList = Lists.newArrayList();

	/** 背包道具栏 */
	@JsonProperty("it")
	private List<ItemInstance> itemList = Lists.newArrayList();

	/** 背包材料栏 */
	@JsonProperty("ma")
	private List<ItemInstance> matList = Lists.newArrayList();

	@JsonProperty("cd")
	private int cdTime;
	
	@JsonProperty("ch")
	private Map<Integer, Integer> composeHeroTimes = Maps.newHashMap();
	
	private static final int clearGold = 60;// 金币
	private static final int coolTime = 60;// 分钟
	
	private List<ItemInstance> charList = null;

	private int currentHeroId;

	private TwoKeyHashMap<Integer, Integer, ObjectNode> itemChange = new TwoKeyHashMap<Integer, Integer, ObjectNode>();


	
	
	public void init() {
		for (int i = 0; i < ItemConstant.EQUIP_INIT_SLOT; i++) {
			equipList.add(null);
		}
		// for (int i = 0; i < ItemConstant.EQUIP_INIT_SLOT; i++) {
		// equipList.add(ItemFactory.createItemInstance(3000, 1));
		// }
		for (int i = 0; i < ItemConstant.ITEM_INIT_SLOT; i++) {
			itemList.add(null);
		}

		itemList.set(0, ItemFactory.createItemInstance(5000, 1));
		itemList.set(1, ItemFactory.createItemInstance(5060, 1));
		itemList.set(2, ItemFactory.createItemInstance(5041, 10));
		// itemList.set(2,ItemFactory.createItemInstance(5043, 100));
		// itemList.set(3,ItemFactory.createItemInstance(5044, 100));
		// itemList.set(4,ItemFactory.createItemInstance(5045, 100));
		// itemList.set(5,ItemFactory.createItemInstance(5046, 100));
		// itemList.set(6,ItemFactory.createItemInstance(5047, 100));
		// itemList.set(7,ItemFactory.createItemInstance(5048, 100));
		// itemList.set(8,ItemFactory.createItemInstance(5049, 100));
		for (int i = 0; i < ItemConstant.MAT_INIT_SLOT; i++) {
			matList.add(null);
		}
	}
	
	public void addTestItem() {
		matList.set(4, ItemFactory.createItemInstance(ItemConstant.WASH_STONE_ID, 999));
		addChange("", 1);
	}

	public List<ItemInstance> getContainerById(int cid) {
		switch (cid) {
		case ItemConstant.CON_CHAR:
			return charList;
		case ItemConstant.CON_EQUIP:
			return equipList;
		case ItemConstant.CON_ITEM:
			return itemList;
		case ItemConstant.CON_MAT:
			return matList;
		default:
			return null;
		}
	}

	public void setCurrentHeroId(int id) {
		this.currentHeroId = id;
	}

	public int getCurrentHeroId() {
		return this.currentHeroId;
	}

	/**
	 * 排序道具
	 * 
	 * @param cid
	 * @return
	 */
	public List<ItemInstance> sortItems(int cid) {
		List<ItemInstance> items = getContainerById(cid);
		if (items == null)
			return null;
		Ordering<Comparable<ItemInstance>> order = Ordering.natural().nullsLast();
		boolean ordered = order.isOrdered(items);
		if (ordered) {
			return null;
		} else {
			List<ItemInstance> sortedCopy = order.sortedCopy(items);
			items.clear();
			items.addAll(sortedCopy);
			return sortedCopy;
		}

	}

	@Override
	public void clearChanged() {
		itemChange = new TwoKeyHashMap<Integer, Integer, ObjectNode>();
		super.clearChanged();
	}

	public List<ItemInstance> getCharList() {
		return charList;
	}

	public void setCharList(List<ItemInstance> charList) {
		this.charList = charList;
	}

	/**
	 * 获取道具
	 * 
	 * @param containerId
	 * @param idx
	 * @return
	 */
	public ItemInstance getItemInstance(int containerId, int idx) {
		if (!isExisted(containerId, idx)) {
			return null;
		}
		return getContainerById(containerId).get(idx);
	}

	/**
	 * 移动道具
	 * 
	 * @param containerId
	 * @param srcIdx
	 * @param tarIdx
	 * @return
	 */
	public boolean moveItem(int containerId, int srcIdx, int tarIdx) {
		if (!isExisted(containerId, srcIdx)) {
			logger.error("移动失败，道具不存在");
			return false;
		}
		return switchItem(containerId, srcIdx, containerId, tarIdx);
	}

	/**
	 * 装备/卸下
	 * 
	 * @param containerId
	 * @param srcIdx
	 * @param tarIdx
	 * @return
	 */
	public boolean moveEquip(int containerId, int srcIdx, int tarIdx, Player player) {
		boolean result = false;
		List<ItemInstance> srcContainer = getContainerById(containerId);
		if (containerId == ItemConstant.CON_EQUIP) { // 装备
			ItemInstance srcItem = srcContainer.get(srcIdx);
			Accessory acc = AllGameConfig.getInstance().items.getAccessoryById(srcItem.getItemId());
			result = switchItem(ItemConstant.CON_EQUIP, srcIdx, ItemConstant.CON_CHAR, acc.getCategory());
			if (result) {
				// player.getTask().dispatchEvent(new
				// ItemUseEvent(srcItem.getItemId(), 1), player);
			}
		} else { // 卸下
			if (tarIdx != -1) {
				ItemInstance tarItem = getContainerById(ItemConstant.CON_EQUIP).get(tarIdx);
				if (tarItem != null) {
					Accessory acc = AllGameConfig.getInstance().items.getAccessoryById(tarItem.getItemId());
					Integer category = acc.getCategory();
					if (category != srcIdx) {
						tarIdx = -1;
					}
				} else {
					tarIdx = -1;
				}
			}
			result = switchEquip(containerId, srcIdx, tarIdx, false);
			if (!result) {
				player.sendMessage(getKey(), jackson.createObjectNode());
				player.sendMiddleMessage(MessageCode.CONTAINER_FULL);
			}
		}
		if (logger.isDebugEnabled()) {
			Hero currentHero = player.getHeros().getCurrentHero();
			currentHero.getAllPower();
		}

		return result;
	}

	/**
	 * 切换装备
	 * 
	 * @param containerId
	 * @param srcIdx
	 * @param tarIdx
	 * @param on
	 *            : true-穿 flase-卸
	 * @return
	 */
	private boolean switchEquip(int containerId, int srcIdx, int tarIdx, boolean on) {
		if (tarIdx == -1) {
			if (on) {
				tarIdx = getEmptyIndex(containerId);
				if (tarIdx == -1) {// 如果没有空位，替换第一个
					tarIdx = 0;
				}
			} else {
				tarIdx = getEmptyIndex(ItemConstant.CON_EQUIP);
				if (tarIdx == -1) {// 如果没有空位，提示不能卸下
					return false;
				}
			}
		}
		if (on) {
			return switchItem(ItemConstant.CON_EQUIP, srcIdx, containerId, tarIdx);
		} else {
			return switchItem(containerId, srcIdx, ItemConstant.CON_EQUIP, tarIdx);
		}
	}

	/**
	 * 道具位置切换
	 * 
	 * @param srcContainer
	 * @param srcIdx
	 * @param tarContainer
	 * @param tarIdx
	 */
	private boolean switchItem(int srcContainerId, int srcIdx, int tarContainerId, int tarIdx) {
		try {
			List<ItemInstance> srcContainer = getContainerById(srcContainerId);
			List<ItemInstance> tarContainer = getContainerById(tarContainerId);
			ItemInstance srcItem = srcContainer.get(srcIdx);
			if (tarIdx < 0 || tarIdx >= tarContainer.size()) {
				return false;
			}
			ItemInstance tarItem = tarContainer.get(tarIdx);
			srcContainer.set(srcIdx, tarItem);
			tarContainer.set(tarIdx, srcItem);
			ArrayNode array = jackson.createArrayNode();
			array.add(toItemJson(tarItem, srcContainerId, srcIdx));
			array.add(toItemJson(srcItem, tarContainerId, tarIdx));
			addChange("itemList", array);
		} catch (Exception e) {
			logger.error("移动道具出错：", e);
			return false;
		}
		return true;
	}

	private ObjectNode toItemJson(ItemInstance item, int containerId, int idx) {
		ObjectNode json = jackson.createObjectNode();
		if (containerId == ItemConstant.CON_CHAR) {
			json.put("hid", currentHeroId);
		}
		json.put("c", containerId);
		json.put("idx", idx);
		if (item != null) {
			item.toJson(json);
		}
		return json;
	}

	/**
	 * 使用道具
	 * 
	 * @return
	 */
	public boolean useItem(int containerId, int idx, Player player, int bat, String param) {
		ItemInstance item = getItemInstance(containerId, idx);
		if (player.getProperty().getLevel() < ((Item) item.getBaseItem()).getLevel()) {
			player.sendMessage(getKey(), jackson.createObjectNode());
			player.sendMiddleMessage(MessageCode.LEVEL_NOT_ENOUGH);
			return false;
		}
		if (bat == 1) { // 批量使用//没有考虑不满足添加条件，直接加
			int useCount = item.getStackCount();
			param = "bat=" + useCount;
//			for (int i = 0; i < item.getStackCount(); i++) {
//				if (!useItemEffectImpl(player, item, param)) {
//					break;
//				}
//				useCount++;
//			}
			if (useCount == 0) {
				return false;
			}
			useItemEffectImpl(player, item, param);
			rmItemReally(containerId, idx, item, useCount, LogConst.ITEM_USE, player);
			// player.getTask().dispatchEvent(new ItemUseEvent(item.getItemId(),
			// useCount), player);
			return true;
		} else {
			if (useItemEffectImpl(player, item, param)) {
				rmItemReally(containerId, idx, item, 1, LogConst.ITEM_USE, player);
				// player.getTask().dispatchEvent(new
				// ItemUseEvent(item.getItemId(), 1), player);
				return true;
			}
			return false;
		}
	}
	
	
	public boolean useItemByNums(int containerId,int idx,Player player,int useCount,String param)
	{
		ItemInstance item = getItemInstance(containerId, idx);
		if (player.getProperty().getLevel() < ((Item) item.getBaseItem()).getLevel()) {
			player.sendMessage(getKey(), jackson.createObjectNode());
			player.sendMiddleMessage(MessageCode.LEVEL_NOT_ENOUGH);
			return false;
		}
		useItemEffectImpl(player, item, param);
		rmItemReally(containerId, idx, item, useCount, LogConst.ITEM_USE, player);
		return true;
	}
	

	private boolean useItemEffectImpl(Player player, ItemInstance itemInstance, String param) {
		Item item = ((Item) itemInstance.getBaseItem());
		int code = useItemEffectImpl(player, item, param);
		if(code != MessageCode.OK) {
			player.sendMessage(getKey(), jackson.createObjectNode());
			player.sendMiddleMessage(code);
			return false;
		};
		return true;
	}
	
	private int useItemEffectImpl(Player player, Item item, String param) {
		GameContext context = new GameContext();
		context.setItemUseParam(param);
		context.setPlayer(player);
		IConditionGF[] conditions = item.getUseConditons();
		if (conditions != null && conditions.length > 0) {
			for (IConditionGF gf : conditions) {
				int result = gf.eval(context);
				if (result != MessageCode.OK) {
					return result;
				}
			}
		}
		IEffectGF[] useEffects = item.getUseEffects();
		if (useEffects != null && useEffects.length > 0) {
			for (IEffectGF gf : useEffects) {
				gf.execute(context);
			}
		}
		return MessageCode.OK;
	}

	/**
	 * 出售
	 * 
	 * @param containerId
	 * @param idx
	 * @param item
	 * @param count
	 */
	public void sellItem(int containerId, int idx, ItemInstance item, int count, Player player, int price, String currency) {
		rmItemReally(containerId, idx, item, count, LogConst.ITEM_SELL, player);
		
	}

	/**
	 * 强化
	 * 
	 * @param item
	 * @param containerId
	 * @param idx
	 */
	public void enhanceItem(int containerId, int idx, Player player, int count) {
		ItemInstance item = getItemInstance(containerId, idx);
		if (item == null) {
			player.sendResult(OperResultType.ENHANCE, MessageCode.FAIL);
			return;
		}

		if (item.getLv() >= 99 || item.getLv() >= player.getProperty().getLevel()) {
			player.sendResult(OperResultType.ENHANCE, MessageCode.FAIL);
			logger.error("装备已经到了{}级别了 itemId:{},playerLv:{}", item.getLv(), item.getItemId(), player.getProperty().getLevel());
			return;
		}
		// 强化cd是1小时
		if (isForbid(coolTime)) {
			player.sendResult(OperResultType.ENHANCE, MessageCode.FAIL);
			logger.error("cd ...");
			return;
		}
		
		if((item.getLv() + count) > player.getProperty().getLevel()){
			count = player.getProperty().getLevel() - item.getLv();
//			logger.info("强化{}级",count);
		}

		Accessory accessory = (Accessory) item.getBaseItem();
		int currlv = item.getLv();
		int tarLv = item.getLv() + count;
		QualityRef qref = config.qref.getQualityRef(accessory.getQuality());
		int price = getNeedEnhanceSilver(item.getLv(),tarLv, qref);
//		logger.info("needSilver{}",price);
		int code = VariousItemUtil.doBonus(player, new VariousItemEntry("silver", price), LogConst.ITEM_ENCHANGE, false);
		if (code == MessageCode.OK) {
			item.setLv(tarLv);
			putItem(item, containerId, idx, false);
			// 道具等级就是cd的分钟数
			if (!player.getProperty().getVipFun(VipConstant.ENHANGE_NO_CD)) {
				String value = ActivityType.ENHANCE_CD.getValue();
				if (!value.equals("1")) {
					int cd = getEnchanceCDTime(currlv, tarLv);
					addCd(cd);
				}
			}
			player.sendResult(OperResultType.ENHANCE, MessageCode.OK, tarLv);

			player.getTask().dispatchMutiEvent(player, TaskType.ENHANCE, count);
		}else {
			player.sendResult(OperResultType.ENHANCE, code);
		}

	}

	private int getEnchanceCDTime(int currLv , int tarLv) {
		int totelCD = 0;
		for(int i = currLv + 1; i <= tarLv; i++){
			int cd = (i - 1) / 8 + 1;
			totelCD += cd;
		}
		
		return totelCD;
	}

	/**
	 * 清理CD
	 * 
	 * @param player
	 */
	public void clearCd(Player player) {
		int needGold = getClearCdGold();
//		logger.info("needGold : {}", needGold);
		if (needGold <= 0) {
			logger.error("消耗金币小于 0");
			return;
		}

		int code = VariousItemUtil.doBonus(player, new VariousItemEntry("gold", needGold), LogConst.CLEAR_CD_ENHANCE, false);
		if (code == MessageCode.OK) {
			setCdTime(0);
		}
		player.sendResult(OperResultType.ENHANCE, MessageCode.OK, "cd");
	}

	/**
	 * 清理CD四个小时花费60金币 4分钟1金币
	 * 
	 * @return
	 */
	private int getClearCdGold() {
		float gf = (clearGold / coolTime) * ((cdTime - TimeUtil.getCurrentTime()) / 60f);
		return (int) Math.ceil(gf);
	}

//	private int getNeedEnhanceSilver(int lv, QualityRef qref) {
//		double ecsilver = qref.getEcsilver() * Math.pow(qref.getEratio(), lv);
//		int price = (int) ecsilver;
//		return price;
//	}
	
	private int getNeedEnhanceSilver(int currLv, int lv, QualityRef qref) {
		int totelPrice = 0;
		for(int i= currLv + 1; i <= lv; i++){
			double ecsilver = qref.getEcsilver() * (1+i*qref.getEratio());
			int price = (int) ecsilver;
			totelPrice+=price;
		}
		
		return totelPrice;
	}


	/**
	 * 是否处于禁止操作的时间段中
	 * 
	 * @param time
	 *            单位分钟
	 * @return
	 */
	public boolean isForbid(int time) {
		int duration = cdTime - TimeUtil.getCurrentTime();
		return duration >= TimeUnit.MINUTES.toSeconds(time);
	}

	/**
	 * 添加CD
	 * 
	 * @param duration
	 *            单位分钟
	 */
	public void addCd(int duration) {
		int currTime = TimeUtil.getCurrentTime();
		int targetTime = 0;
		if (this.cdTime < currTime) {
			targetTime = (int) (currTime + TimeUnit.MINUTES.toSeconds(duration));
		} else {
			targetTime = (int) (this.cdTime + TimeUnit.MINUTES.toSeconds(duration));
		}
		setCdTime(targetTime);
	}

	public void setCdTime(int cdTime) {
		this.cdTime = cdTime;
		int cd = cdTime - TimeUtil.getCurrentTime();
		if (cd <= 0) {
			cd = 0;
		}
		addChange("cd", cd);
	}
	
	/**
	 * 分解道具
	 **/
	public void decomposeItems(int containerId, ArrayNode idxs, Player player) {
		Map<Integer, Integer> items = Maps.newHashMap();
		int silver = 0;
		for (int i = 0; i < idxs.size(); ++i) {
			int idx = idxs.get(i).asInt();	//	Item of index
			ItemInstance item = getItemInstance(containerId, idx);
			if (item != null) {
				QualityRef qref = config.qref.getQualityRef(item.getBaseItem().getQuality());
				silver += getDecomposeSilver(item.getLv(), qref);
				for (Integer id : qref.getItem()) {
					items.put(id, items.containsKey(id) ? items.get(id) + 1 : 1);
				}
			}
		}
		
//		logger.info("fenjiefyinbi--->"+silver);
		List<VariousItemEntry> itemEntries = Lists.newArrayList();
		for (Map.Entry<Integer, Integer> e : items.entrySet()) {
			itemEntries.add(new VariousItemEntry(e.getKey(), e.getValue()));
		}
		itemEntries.add(new VariousItemEntry("silver", silver));
		int res = VariousItemUtil.checkBonus(player, itemEntries, true);
		if (res != MessageCode.OK) {
			player.sendMiddleMessage(res);
			return;
		}
		
		//	从背包中移除被分解的道具
		for (int i = 0; i < idxs.size(); ++i) {
			rmItemByIdx(containerId, idxs.get(i).asInt(), 1, LogConst.ITEM_DECOMPOSE, player);
		}
		VariousItemUtil.doBonus(player, itemEntries, LogConst.ITEM_DECOMPOSE, true);
		player.sendMiddleMessage(MessageCode.DECOMPOSE_SUCCESS);
	}
	
	/**
	 * 洗练装备
	 * 
	 * @param containerId
	 * @param idx
	 * @param player
	 */
	public void washAccItem(int containerId, int idx, Player player) {
		ItemInstance item = getItemInstance(containerId, idx);
		if (item == null) {
			logger.error("item is null");
			return;
		}
		int canWashPos = item.canWashPos();
		if(canWashPos > -1) {
			QualityRef qref = config.qref.getQualityRef(item.getBaseItem().getQuality());
			int needNum = 0;
			if(canWashPos == 1) {
				needNum = qref.getWash1();
			}else if(canWashPos == 2) {
				needNum = qref.getWash2();
			}else if(canWashPos == 3) {
				needNum = qref.getWash3();
			}else {
				logger.error("wrong wash stone number");
				return;
			}
			int code = VariousItemUtil.doBonus(player, new VariousItemEntry(ItemConstant.WASH_STONE_ID, needNum), LogConst.WASH_ACCESSORY, false);
			if (code == MessageCode.OK) {
				item.washProp(canWashPos);
				addChange(item, containerId, idx, false);
				player.sendResult(OperResultType.WASH,MessageCode.OK,DynamicJsonProperty.convertToArrayNode(item.getProps()));
				player.sendMiddleMessage(MessageCode.WASH_SUCCESS);
			}else {
				player.sendMiddleMessage(code);
			}
			
			//洗练埋点
			LogUtil.doEquipLelLog(player, item.getItemId(), canWashPos);
			
		}
	}
	
	
	
	private static final int REFINE_ACC_COST_SKILL_ITEM = 10;//精练消耗的技能卷轴数量
	/**
	 * 装备精炼
	 * @param skillFromConIdx
	 * @param accFromConIdx
	 * @param player
	 */
	public void refineAccItem(int containerId,int skillFromConIdx, int accFromConIdx, Player player) {
		ItemInstance equipItem = getItemInstance(containerId, accFromConIdx);
		ItemInstance skillItem = getItemInstance(ItemConstant.CON_ITEM, skillFromConIdx);
		if (equipItem == null || skillItem == null) {	// Safety check!
			logger.error("item is null");
			return;
		}
		
		if (skillItem.getStackCount() < REFINE_ACC_COST_SKILL_ITEM) {	// Safety check!
			logger.error("skill stack not enough");
			return;
		}
		
		int skillid = equipItem.refineProp(0, skillItem.getItemId());
		
//		Item item = AllGameConfig.getInstance().items.getItemById(skillItem.getItemId());
		Skill skillConfig = AllGameConfig.getInstance().skills.getSkill(skillid);
		if (skillConfig.getRefine() == 0) {	// Safety check!
			logger.error("skill cannot refine!");
			player.sendResult(OperResultType.REFINE_EQUIP, MessageCode.SILVER_NOT_ENUGH);
			return;
		}
		
		int costSilver = skillConfig.getRefinecost();
		if (!player.getProperty().hasEnoughMoney(null, costSilver, null)) {
			player.sendResult(OperResultType.REFINE_EQUIP, MessageCode.SILVER_NOT_ENUGH);
			return;
		}
		
		VariousItemEntry costSkillItem = new VariousItemEntry(skillItem.getItemId(), REFINE_ACC_COST_SKILL_ITEM);
		int code = VariousItemUtil.doBonus(player, costSkillItem, LogConst.REFINE_ACC, false);
		if (code != MessageCode.OK) {
			player.sendMiddleMessage(code);
			return;
		}
		
		
		putItem(equipItem, containerId, accFromConIdx, false);
		player.getProperty().changeMoney(0, -costSilver);
		player.sendResult(OperResultType.REFINE_EQUIP);
		//精练埋点
		LogUtil.doRefiningLog(player, equipItem.getItemId(), skillItem.getItemId());
	}

	/**
	 * 装备分解出的金币
	 * 
	 * @param lv
	 * @param qref
	 * @return
	 */
	public int getDecomposeSilver(int lv, QualityRef qref) {
//		int price = getNeedEnhanceSilver(lv,lv+1, qref) * (lv+1)/4; // 修改，分解的银币全部返回给玩家
		int price = getNeedEnhanceSilver(lv,lv+1, qref); 
		return price;
	}
	
	private int getComposeTimes(int hid) {
		Integer times = composeHeroTimes.get(hid);
		return times == null ? 0 : times;
	}
	private void addComposeTimes(int hid) {
		int composeTimes = getComposeTimes(hid);
		composeHeroTimes.put(hid, composeTimes + 1);
		addChange("cmpoh", convertToJsonNode(composeHeroTimes));
	}

	public void composeItem(int id, Player player) {
		Formula formula = config.formula.getFormula(id);
		List<VariousItemEntry> cost = Lists.newArrayList();
		if (formula.getShow() == 2) {
			int composeTimes = getComposeTimes(formula.getProduct());
			float muti = getValueLimit(composeTimes);
//			logger.info("muti =" +muti);
			for (VariousItemEntry cItemEntry : formula.getCost()) {
				int needCount = (int)Math.ceil(cItemEntry.getAmount() * muti);
//				logger.info("needCount =" +needCount);
				cost.add(new VariousItemEntry(cItemEntry.getType(), needCount));
			}
		}
		if (cost.isEmpty()) {
			for (VariousItemEntry variousItemEntry : formula.getCost()) {
				cost.add(variousItemEntry);
			}
		}
		int code = VariousItemUtil.checkBonus(player, cost, false);
		if (code == MessageCode.OK) {
			// 检测是否有任务完成
			Item item = config.items.getItemById(formula.getProduct());
			if (item != null) {
				code = useItemEffectImpl(player, item, "");
				if (code != MessageCode.OK) {
					player.sendResult(OperResultType.COMPOSE, code);
				}else {
					VariousItemUtil.doBonus(player, cost, LogConst.ITEM_COMPOSE, false);
					player.getTask().dispatchEvent(player, TaskType.COMPOSE, item.getQuality());
				}
			}else {
				VariousItemEntry variousItemEntry = new VariousItemEntry(formula.getProduct(), 1);
				code = VariousItemUtil.checkBonus(player, variousItemEntry, true);
				if (code != MessageCode.OK) {
					player.sendResult(OperResultType.COMPOSE, code);
				}else {
					VariousItemUtil.doBonus(player, variousItemEntry, LogConst.ITEM_COMPOSE, true);
					player.sendResult(OperResultType.COMPOSE, VariousItemUtil.itemToJson(variousItemEntry));
					if (formula.getShow() == 2) {
						addComposeTimes(formula.getProduct());
						//如果是第一次合成雏田
						if(formula.getProduct() == 24 && getComposeTimes(formula.getProduct()) == 1) {
							List<Hero> mars = player.getHeros().getMarHeros();
							if(mars.size() < 3) {
								player.getHeros().addHero(10);
							}else {
								float chuTianqualification = player.getHeros().getHeroByRoleId(formula.getProduct()).getQualification();
								int num = 0;//满足条件的数量
								for (Hero hero : mars) {
									if(hero.getQualification() < chuTianqualification) {
										num++;
									}
								}
								if(num == 0) {
									player.getHeros().addHero(10);
								}
							}
						}
					}
					VariousItemUtil.doBonus(player, cost, LogConst.ITEM_COMPOSE, false);
				}
			}
			
		} else {
			player.sendResult(OperResultType.COMPOSE, code);
		}
	}

	private float getValueLimit(int composeTimes) {
		float[] costMuti = (float[])config.gconst.getConstant(GameConstId.COST_MUTI);
		return IntList.getFloatValueLimit(composeTimes, costMuti);
	}

	//
	// /**
	// * 升级道具
	// *
	// * @param item
	// * @param itemId
	// * @param containerId
	// * @param idx
	// */
	// public void upgradeItem(ItemInstance item, int itemId, int containerId,
	// int idx) {
	// item.setItemId(itemId);
	// putItem(item, containerId, idx, false);
	// }
	//
	private void putItem(ItemInstance item, int containerId, int idx, boolean newIdx) {
		getContainerById(containerId).set(idx, item);
		addChange(item, containerId, idx, newIdx);
	}

	public void addItemReally(ItemInstance item, int count, int containerId, int idx, int reason, Player player, boolean newIdx) {
		putItem(item, containerId, idx, newIdx);
		LogUtil.doItemLog(player, containerId, idx, item, count, reason);
	}

	/**
	 * 删除道具(通过索引)
	 * 
	 * @param containerId
	 * @param idx
	 *            - 索引
	 * @param count
	 *            - 删除数量
	 * @param resaon
	 *            - 删除原因
	 * @param player
	 * @return
	 */
	public boolean rmItemByIdx(int containerId, int idx, int count, int resaon, Player player) {
		if (!isExisted(containerId, idx)) {
			logger.error("rmItemByIdx isExisted = false. containerId:{},idx{},count:{},resean:{}  ",containerId,idx,count,resaon);
			return false;
		}
		ItemInstance item = getItemInstance(containerId, idx);
		if (item.getStackCount() < count) {
			logger.error("getStackCount enough = false. containerId:{},idx{},count:{},resean:{}  ",containerId,idx,count,resaon);
			return false;
		}
		rmItemReally(containerId, idx, item, count, resaon, player);
		return true;
	}

	/**
	 * 删除道具(通过道具id)
	 * 
	 * @param itemId
	 *            - 道具id
	 * @param count
	 *            - 删除数量
	 * @param reason
	 *            - 删除原因
	 * @param player
	 * @return
	 */
	public boolean rmItemByItemId(int itemId, int count, int reason, Player player) {
		short containerId = AllGameConfig.getInstance().items.getItemContainerId(itemId);
		return rmItemByItemId(containerId, itemId, count, reason, player);
	}

	private boolean rmItemByItemId(int containerId, int itemId, int count, int reason, Player player) {
		int idx = getItemIdx(containerId, itemId);
		if (idx < 0) {
			logger.error("删除失败！背包里没有该道具  " + itemId);
			return false;
		}
		ItemInstance item = getItemInstance(containerId, idx);
		if (item.getStackCount() >= count) { // 最常出现的情况,优先处理
			rmItemByIdx(containerId, idx, count, reason, player);
			return true;
		} else {
			if (getItemCount(containerId, itemId) < count) {
				logger.error("删除失败！背包里道具数量不足  " + itemId + "|" + count + "|" + containerId);
				return false;
			}
			BaseItem baseItem = AllGameConfig.getInstance().items.getBaseItem(itemId);
			List<ItemInstance> container = getContainerById(containerId);
			Integer stackMax = baseItem.getStackmax();
			if (stackMax == null || stackMax.intValue() == 1) {
				// 不可叠加的道具
				for (int i = 0; i < count; i++) {
					int tmpIdx = -1;
					for (int j = 0; j < container.size(); j++) {
						ItemInstance it = container.get(j);
						if (it != null && it.getItemId() == itemId) {
							tmpIdx = j;
						}
					}
					rmItemReally(containerId, tmpIdx, container.get(tmpIdx), 1, reason, player);
				}
				return true;
			} else {
				// 可叠加的道具
				if (count > stackMax) {
					logger.error("单次删除道具数量过大 " + count + ">" + stackMax);
					return false;
				}
				int leftCount = item.getStackCount() - count;
				if (leftCount >= 0) {
					rmItemReally(containerId, idx, item, count, reason, player);
					return true;
				} else {
					int left = count - item.getStackCount();
					rmItemReally(containerId, idx, item, item.getStackCount(), reason, player);
					int anotherIdx = getItemIdx(containerId, itemId);
					ItemInstance anotherItem = container.get(anotherIdx);
					rmItemReally(containerId, anotherIdx, anotherItem, left, reason, player);
					return true;
				}
			}
		}
	}

	private void rmItemReally(int containerId, int idx, ItemInstance item, int count, int reason, Player player) {
		item.setStackCount(item.getStackCount() - count);
		if (item.getStackCount() == 0) {
			List<ItemInstance> container = getContainerById(containerId);
			container.set(idx, null);
			addChange(null, containerId, idx, false);
		} else {
			addChange(item, containerId, idx, false);
		}
		LogUtil.doRemoveItemLog((DefaultPlayer)player, reason, item.getItemId(), count);
	}

	/**
	 * 增加道具 用于各种奖励
	 * 
	 * @param itemId
	 *            - 道具id
	 * @param count
	 *            - 数量
	 * @param reason
	 *            增加原因
	 * @param player
	 * @return
	 */
	public boolean addItem(int itemId, int count, int reason, Player player) {
		boolean isAdded = addItem(itemId, count, -1, reason, player);
		if (isAdded == false) {
			player.getMail().addSysItemMail(itemId, count, reason);
			player.sendMiddleMessage(MessageCode.ITEM_TO_MAIL);
		}
		return true;
	}

	/**
	 * 增加道具
	 * 
	 * @param itemId
	 *            - 道具id
	 * @param count
	 *            - 数量
	 * @param containerId
	 * @param reason
	 *            增加原因
	 * @param player
	 * @return
	 */
	private boolean addItem(int itemId, int count, int containerId, int reason, Player player) {
		if (containerId == -1) {
			containerId = AllGameConfig.getInstance().items.getItemContainerId(itemId);
		}
		BaseItem baseItem = AllGameConfig.getInstance().items.getBaseItem(itemId);
		Integer stackMax = baseItem.getStackmax();
		if (stackMax == null || stackMax.intValue() == 1) {
			// 不可叠加的道具
			int emptyCount = getEmptyIdxCount(containerId);
			if (emptyCount < count) {
				logger.error("增加道具失败！背包空间不足  需要" + count + "，剩余" + emptyCount + "|" + itemId + "|" + containerId);
				return false;
			}
			for (int i = 0; i < count; i++) {
				ItemInstance item = ItemFactory.createItemInstance(itemId, 1);
				int idx = getEmptyIndex(containerId);
				addItemReally(item, 1, containerId, idx, reason, player, true);
			}
			return true;
		} else {
			// 可叠加的道具
			if (stackMax < count) {
				logger.error("增加道具失败！单次添加道具数量过大 " + count + ">" + stackMax);
			}
			int existedIdx = getItemIdx(containerId, itemId);
			if (existedIdx < 0) {// 背包里没有这种道具,创建实例
				int emptyIdx = getEmptyIndex(containerId);
				if (emptyIdx < 0) {
					logger.error("增加道具失败！背包已满  " + itemId + "|" + count + "|" + containerId);
					return false;
				}
				ItemInstance item = ItemFactory.createItemInstance(itemId, count);
				addItemReally(item, count, containerId, emptyIdx, reason, player, true);
			} else { // 背包里有这种道具
				ItemInstance item = getItemInstance(containerId, existedIdx);
				int all = item.getStackCount() + count;
				if (all < stackMax) {// 没超过上限，直接叠加
					item.setStackCount(all);
					addItemReally(item, count, containerId, existedIdx, reason, player, false);
				} else {// 超过的部分，创建新实例
					int emptyIdx = getEmptyIndex(containerId);
					if (emptyIdx < 0) {
						logger.error("增加道具失败！背包已满  " + itemId + "|" + count + "|" + containerId);
						return false;
					}
					int addCount = stackMax - item.getStackCount();
					item.setStackCount(stackMax);
					addItemReally(item, addCount, containerId, existedIdx, reason, player, false);
					ItemInstance newItem = ItemFactory.createItemInstance(itemId, all - stackMax);
					addItemReally(newItem, all - stackMax, containerId, emptyIdx, reason, player, true);
				}
			}
			return true;
		}
	}

	/**
	 * 检查道具的数量
	 * 
	 * @param itemId
	 * @param count
	 *            >0检查背包空间 count<0检查道具数量
	 * @return
	 */
	public boolean checkItem(int itemId, int count) {
		int containerId = AllGameConfig.getInstance().items.getItemContainerId(itemId);
		if (count > 0) { // 检查背包空间是否足够
			BaseItem baseItem = AllGameConfig.getInstance().items.getBaseItem(itemId);
			Integer stackMax = baseItem.getStackmax();
			if (stackMax == null || stackMax.intValue() == 1) {
				return getEmptyIdxCount(containerId) >= count;
			} else { // 可叠加的
				if (stackMax < count) {
					return false;
				}
				int existedIdx = getItemIdx(containerId, itemId);
				if (existedIdx < 0) {// 背包里没有这种道具
					return getEmptyIndex(containerId) >= 0;
				} else {
					ItemInstance item = getItemInstance(containerId, existedIdx);
					int all = item.getStackCount() + count;
					if (all < stackMax) {// 没超过上限
						return true;
					} else {
						return getEmptyIndex(containerId) >= 0;
					}
				}
			}
		} else { // 检查道具数量是否足够
			return getItemCount(containerId, itemId) >= (-count);
		}
	}

	/**
	 * 检查背包或身上是否有此道具
	 * 
	 * @param itemId
	 * @return
	 */
	public boolean hasItem(int itemId) {
		int containerId = AllGameConfig.getInstance().items.getItemContainerId(itemId);
		// 检查背包
		int existedIdx = getItemIdx(containerId, itemId);
		if (existedIdx >= 0) {
			return true;
		} else { // 检查英雄身上
			BaseItem baseItem = AllGameConfig.getInstance().items.getBaseItem(itemId);
			return getContainerById(containerId).get(baseItem.getCategory()) != null;
		}

	}

	/**
	 * 计算开格花费
	 * 
	 * @param curMaxIdx
	 * @param count
	 * @param containerId
	 * @return
	 */
	public int getOpenCost(int curMaxIdx, int count, int containerId) {
		int price = 0;
		int initPrice = ItemConstant.getInitPrice(containerId);
		int addPrice = ItemConstant.getAddPrice(containerId);
		int initCount = ItemConstant.getInitSlot(containerId);
		for (int i = 1; i <= count; i++) {
			price += (initPrice + addPrice * (1 + curMaxIdx + i - initCount));
		}
		return price;
	}

	/**
	 * 获取某个道具的数量
	 * 
	 * @param itemId
	 * @param containerId
	 * @return
	 */
	public int getItemCount(int containerId, int itemId) {
		if (containerId == -1) {
			containerId = AllGameConfig.getInstance().items.getItemContainerId(itemId);
		}
		List<ItemInstance> container = getContainerById(containerId);
		if (container == null) {
			return 0;
		}
		int count = 0;
		for (ItemInstance item : container) {
			if (item != null && item.getItemId() == itemId) {
				count += item.getStackCount();
			}
		}
		return count;
	}

	/**
	 * 找一个空位
	 * 
	 * @param containerId
	 * @return
	 */
	public int getEmptyIndex(int containerId) {
		List<ItemInstance> container = getContainerById(containerId);
		for (int i = 0; i < container.size(); i++) {
			if (container.get(i) == null) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean needEmptyCount(int containerId,int count) {
		int num = 0;
		List<ItemInstance> container = getContainerById(containerId);
		for (int i = 0; i < container.size(); i++) {
			if (container.get(i) == null) {
				num++;
				if(num >= count) {
					return true;
				}
			}
		}
		return false;
	}

	public int getEmptyIdxCount(int containerId) {
		int count = 0;
		List<ItemInstance> container = getContainerById(containerId);
		for (int i = 0; i < container.size(); i++) {
			if (container.get(i) == null) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 查询道具的索引（优先返回叠加没满的）
	 * 
	 * @param containerId
	 * @param itemId
	 * @return
	 */
	private int getItemIdx(int containerId, int itemId) {
		List<ItemInstance> container = getContainerById(containerId);
		if (container == null) {
			return -1;
		}
		int idx = -1;
		for (int i = 0; i < container.size(); i++) {
			ItemInstance item = container.get(i);
			if (item != null && item.getItemId() == itemId) {
				if (item.getStackCount() < item.getBaseItem().getStackmax()) {
					return i;
				} else {
					idx = i;
				}
			}
		}
		return idx;
	}

	/**
	 * 栏位开格
	 * 
	 * @param containerId
	 * @param count
	 */
	public void openContainerSlot(int containerId, int count) {
		List<ItemInstance> container = getContainerById(containerId);
		for (int i = 0; i < count; i++) {
			container.add(null);
		}
		addChange("slot" + containerId, container.size());
	}

	/**
	 * index位置上的道具是否存在
	 * 
	 * @param containerId
	 * @param idx
	 * @return
	 */
	public boolean isExisted(int containerId, int idx) {
		List<ItemInstance> container = getContainerById(containerId);
		if (idx < 0 || container == null) {
			return false;
		}
		if (container.size() < idx || container.get(idx) == null) {
			return false;
		}
		return true;
	}

	/**
	 * 检查索引
	 * 
	 * @param containerId
	 * @param idx
	 * @return
	 */
	public boolean checkIndex(int containerId, int idx) {
		List<ItemInstance> container = getContainerById(containerId);
		if (idx < 0 || container == null || container.size() < idx) {
			return false;
		}
		return true;
	}

	public void addChange(ItemInstance item, int containerId, int idx, boolean newIdx) {
		ObjectNode json = jackson.createObjectNode();
		if (itemChange.containsKey(containerId, idx)) {
			json = itemChange.get(containerId, idx);
			// newIdx = json.optInt("nw") == 1;
		}
		if (containerId == ItemConstant.CON_CHAR) {
			json.put("hid", currentHeroId);
		}
		json.put("c", containerId);
		json.put("idx", idx);
		if (item != null) {
			item.toJson(json);
		}
		if (newIdx) {
			// json.putOpt("nw", 1);
		}

		itemChange.put(containerId, idx, json);
	}

	@Override
	public boolean hasChanged() {
		return !itemChange.isEmpty() || super.hasChanged();
	}

	@Override
	public ObjectNode getJsonValue() {
		if (!itemChange.isEmpty()) {
			Collection<ObjectNode> values = itemChange.values();
			json.put("itemList", convertToArrayNode(values));
		}
		return json;
	}

	@Override
	public String getKey() {
		return "inventory";
	}

	@Override
	public JsonNode toWholeJson() {
		ObjectNode json = jackson.createObjectNode();
		json.put("slot1", equipList.size());
		json.put("slot2", itemList.size());
		json.put("slot3", matList.size());
		int cd = cdTime - TimeUtil.getCurrentTime();
		if (cd > 0) {
			json.put("cd", cdTime - TimeUtil.getCurrentTime());
		}
			
		json.put("cmpoh", convertToJsonNode(composeHeroTimes));
		return json;
	}

	public void sendItemList(Player player) {
		String key = "itemList";
		ArrayNode node = jackson.createArrayNode();
		ArrayNode all = list2Object(equipList, ItemConstant.CON_EQUIP);
		all.addAll(list2Object(itemList, ItemConstant.CON_ITEM));
		all.addAll(list2Object(matList, ItemConstant.CON_MAT));
		int i = 0;
		for (JsonNode jsonNode : all) {
			node.add(jsonNode);
			if ((i + 1) % 10 == 0) {
				player.sendMessage(key, node);
				node.removeAll();
			}
			i++;
		}
		player.sendMessage(key, node);
	}

	private ArrayNode list2Object(List<ItemInstance> container, int containerId) {
		ArrayNode list = jackson.createArrayNode();
		for (int i = 0; i < container.size(); i++) {
			ItemInstance item = container.get(i);
			if (item != null) {
				ObjectNode json = jackson.createObjectNode();
				json.put("c", containerId);
				json.put("idx", i);
				item.toJson(json);
				list.add(json);
			}
		}
		return list;
	}

}
