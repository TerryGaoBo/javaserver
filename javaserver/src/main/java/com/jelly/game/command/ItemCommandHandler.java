package com.jelly.game.command;

import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.DefaultPlayer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.BaseItem;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.ItemConstant;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jelly.hero.Hero;
import com.jelly.player.ItemInstance;

public class ItemCommandHandler extends SubJsonCommandHandler {

	private final PlayerSession playerSession;
	private final DefaultPlayer player;
	private static final Logger LOG = LoggerFactory.getLogger(ItemCommandHandler.class);

	public ItemCommandHandler(Session session) {
		this.playerSession = (PlayerSession) session;
		player = (DefaultPlayer) this.playerSession.getPlayer();
		addHandler(openSlot);
		addHandler(sellItem);
		addHandler(moveItem);
		addHandler(useItem);
		addHandler(sortBag);
		addHandler(enhanceItem);
		addHandler(decomposeItem);
		addHandler(composeItem);
		addHandler(clearEnhanceCd);
		addHandler(refineAccessory);
		addHandler(washAccessory);
		addHandler(useExpToHeroFunc);
	}

	/**
	 * 栏位开格子
	 */
	JsonCommandHandler openSlot = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "openSlot";
		}
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt(-1);
			int tarIdx = object.get("idx").asInt(-1);
			List<ItemInstance> container = player.getInvenotry().getContainerById(containerId);
			int curMaxIdx = container.size() - 1;
			if (tarIdx <= curMaxIdx || tarIdx > ItemConstant.getMaxSlot(containerId)-1) {
				return;
			} 
			
			int openCount = tarIdx - curMaxIdx;
			int cost = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.OPEN_SLOT_PRICE);
			
			VariousItemEntry needGoldEntry = new VariousItemEntry("gold", cost * openCount);
			int code = VariousItemUtil.doBonus(player, needGoldEntry, LogConst.PACKAGE_OPEN_SLOT, false);
			if (code != MessageCode.OK) {
				player.sendMiddleMessage(code);
				return;
			}
			player.getInvenotry().openContainerSlot(containerId, openCount);
		}
	};
	
	/**
	 * 出售道具
	 */
	JsonCommandHandler sellItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "sellItem";
		}
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt(-1);
			int idx = object.get("idx").asInt(-1);
			int count = object.get("count").asInt(-1);
			ItemInstance item = player.getInvenotry().getItemInstance(containerId, idx);
			if (item == null) {
				return;
			}
			if(item.getStackCount() <= 0) {
				List<ItemInstance> container = player.getInvenotry().getContainerById(containerId);
				container.set(idx, null);
				player.getInvenotry().addChange(null, containerId, idx, false);
				return;
			}
			if(item.getStackCount() < count) {
				return;
			}
			BaseItem baseItem = item.getBaseItem();
			
//			if (containerId == ItemConstant.CON_EQUIP) {
//				QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(baseItem.getQuality());
//				int decomposeSilver = player.getInvenotry().getDecomposeSilver(item.getLv(), qualityRef);
//				player.getInvenotry().sellItem(containerId, idx, item, count, player, decomposeSilver, "silver");
//				VariousItemEntry itemEntry = new VariousItemEntry("silver", decomposeSilver);
//				VariousItemUtil.doBonus(player, itemEntry, LogConst.ITEM_SELL, true);
//				player.sendMiddleMessage(MessageCode.EARN_SILVER, decomposeSilver+"");
//			}else {
				VariousItemEntry[] price = baseItem.getSell();
				for (VariousItemEntry p : price) {
					String currency = p.getType();
					player.getInvenotry().sellItem(containerId, idx, item, count, player, p.getAmount() * count, currency);
					VariousItemUtil.doBonus(player, new VariousItemEntry(currency, p.getAmount() * count), LogConst.ITEM_SELL, true);
					player.sendMiddleMessage(MessageCode.EARN_SILVER, p.getAmount() * count+"");
				}
//			}
		}
	}; 
	
	
	
	/**
	 * 移动道具
	 */
	JsonCommandHandler moveItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "moveItem";
		}
		@Override
		public void run(JsonNode object) {
			int container = object.get("c").asInt(-1);
			int srcIdx = object.get("srcIdx").asInt(-1);
			int tarIdx = object.get("tarIdx").asInt(-1);
			player.getInvenotry().moveItem(container, srcIdx, tarIdx);
		}
	}; 
	
	/**
	 * 使用道具
	 */
	JsonCommandHandler useItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "useItem";
		}
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt(-1);
			int srcIdx = object.get("srcIdx").asInt(-1);
			int tarIdx = -1;
			if(object.has("tarIdx")) {
				tarIdx = object.get("tarIdx").asInt(-1);
			}
			int bat = -1;
			if (object.has("bat")) {
				bat = object.get("bat").asInt(-1);
			}
			String param = "";
			if (object.has("param")) {
				param = object.get("param").asText();
			}
			if(object.has("hid")) {
				player.getHeros().setCurrentHero(object.get("hid").asInt(),player);
			}
			
			ItemInstance ii = player.getInvenotry().getItemInstance(containerId, srcIdx);
			if (ii == null) {
				LOG.error("使用道具，道具不存在");
				return ;
			}
			if(ii.getStackCount() <= 0) {
				List<ItemInstance> container = player.getInvenotry().getContainerById(containerId);
				container.set(srcIdx, null);
				player.getInvenotry().addChange(null, containerId, srcIdx, false);
				return;
			}
			boolean result = false, 
					isNeedUpdatePow = false;	//	是否需要刷新RoleEntity缓存的玩家战斗力
			switch (containerId) {
			case ItemConstant.CON_ITEM:
				result = player.getInvenotry().useItem(containerId, srcIdx, player, bat,param);
				break;
			case ItemConstant.CON_EQUIP:
				if (player.getHeros().getCurrentHero().getLevel() < ((Accessory)ii.getBaseItem()).getLevel()) {
					player.sendMessage(player.getInvenotry().getKey(), DynamicJsonProperty.jackson.createObjectNode());
					player.sendMiddleMessage(MessageCode.LEVEL_NOT_ENOUGH);
					return;
				}
				isNeedUpdatePow = (result = player.getInvenotry().moveEquip(containerId, srcIdx, tarIdx, player));				
				break;
			case ItemConstant.CON_CHAR:
				isNeedUpdatePow = (result = player.getInvenotry().moveEquip(containerId, srcIdx, tarIdx, player));
				break;
			default:
				LOG.error("使用道具出错:containerId= " + containerId);
			}
			
			if (isNeedUpdatePow) {
				player.getAllPlayersCache().updatePlayerPower(player);
			}
		}
	}; 
	
	/**
	 * 整理背包
	 */
	JsonCommandHandler sortBag = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "sortBag";
		}
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt(-1);
			List<ItemInstance> sortItems = player.getInvenotry().sortItems(containerId);
			if(sortItems == null) return;
			ObjectNode result = DynamicJsonProperty.jackson.createObjectNode();
			result.put("c", containerId);
			ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
			for (int i=0; i<sortItems.size(); i++) {
				ItemInstance item = sortItems.get(i);
				if (item != null) {
					ObjectNode json =  DynamicJsonProperty.jackson.createObjectNode();
					json.put("c", containerId);
					json.put("idx", i);
					item.toJson(json);
					array.add(json);
				}
			}
			result.put("itemList", array);
			ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
			obj.put("sortBagResult", result);
			player.sendMessage(obj);
		}
	};
	
	/**
	 * 强化装备
	 */
	JsonCommandHandler enhanceItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "enhanceItem";
		}
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt(-1);
			int idx = object.get("idx").asInt(-1);
			if(object.has("hid")) {
				player.getHeros().setCurrentHero(object.get("hid").asInt(),player);
			}
			int count = 1;
			if(object.has("count")) {
				count = object.get("count").asInt();
			}
			player.getInvenotry().enhanceItem(containerId, idx,player,count);
		}
	};
	
	/**
	 * 合成道具
	 */
	JsonCommandHandler composeItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "composeItem";
		}
		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt(-1);
			player.getInvenotry().composeItem(id, player);
		}
	};
	
	/**
	 * 分解道具
	 */
	JsonCommandHandler decomposeItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "decomposeItem";
		}
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt(-1);
			if (object.has("hid")) {
				player.getHeros().setCurrentHero(object.get("hid").asInt(),player);
			}
//			ArrayNode arr = DynamicJsonProperty.jackson.createArrayNode();
//			arr.add(object.get("idx").asInt());
//			player.getInvenotry().decomposeItems(containerId, arr, player);
			player.getInvenotry().decomposeItems(containerId, (ArrayNode) object.get("idx"), player);
		}
	};
	/**
	 * 清理强化CD
	 * 
	 */
	JsonCommandHandler clearEnhanceCd = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "clearEnhanceCd";
		}
		@Override
		public void run(JsonNode object) {
			player.getInvenotry().clearCd(player);
		}
	};
	/**
	 * 洗练装备
	 * 
	 */
	JsonCommandHandler washAccessory = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "washAccessory";
		}
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt(-1);
			int idx = object.get("idx").asInt(-1);
			if(object.has("hid")) {
				player.getHeros().setCurrentHero(object.get("hid").asInt(),player);
			}
			player.getInvenotry().washAccItem(containerId, idx, player);
		}
	};
	
	/**
	 * 精炼装备
	 **/
	JsonCommandHandler refineAccessory = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refineAccessory";
		}

		@Override
		public void run(JsonNode object) {
			if(object.has("hid")) {
				player.getHeros().setCurrentHero(object.get("hid").asInt(),player);
			}
			player.getInvenotry().refineAccItem(
					object.get("c").asInt(-1),
					object.get("skillBook").asInt(),
					object.get("itemId").asInt(), player);
		}
	};
	
	/**
	 *  给英雄使用经验丹
	 */
	JsonCommandHandler useExpToHeroFunc = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "useExpToHeroFunc";
		}
		
		@Override
		public void run(JsonNode object) {
			int containerId = object.get("c").asInt();
			int srcIdx = object.get("srcIdx").asInt(); // 索引
			int heroid = object.get("heroid").asInt();
			int num = object.get("num").asInt(); // 消耗数量，默认是1
			
			ItemInstance ii = player.getInvenotry().getItemInstance(containerId, srcIdx);
			if (ii == null) {
				LOG.error("使用道具，道具不存在");
				return ;
			}
			if(num <=0 ){
				LOG.error("发送消息不正确。。。。num === 0");
				return;
			}
			if(ii.getStackCount() <= 0) {
				List<ItemInstance> container = player.getInvenotry().getContainerById(containerId);
				container.set(srcIdx, null);
				player.getInvenotry().addChange(null, containerId, srcIdx, false);
				return;
			}
			
			if(ii.getStackCount() < num){
				num = ii.getStackCount();
			}
			
			Hero hero = player.getHeros().getHero(heroid);
			if(hero == null){
				return;
			}
			
			String param = heroid + ","+num;
			
			if(containerId == ItemConstant.CON_ITEM){
				player.getInvenotry().useItemByNums(containerId, srcIdx, player, num, param);
			}
		}
	};
}
