package com.jelly.game.command;

import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.DESPlusManager;
import com.dol.cdf.common.DESPlusManager.CardModel;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.Adventure;
import com.dol.cdf.common.bean.Building;
import com.dol.cdf.common.bean.DayReward;
import com.dol.cdf.common.bean.Dig;
import com.dol.cdf.common.bean.Gift;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.config.AdventureConfigManager;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.BuildingType;
import com.dol.cdf.common.constant.GameConstId;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jelly.activity.ActivityType;
import com.jelly.activity.GiftType;
import com.jelly.combat.PVECombatManager;
import com.jelly.hero.PlayerHeros;
import com.jelly.player.AttackerGroup;
import com.jelly.player.DefenderGroup;
import com.jelly.player.PlayerProperty;
import com.jelly.player.ShopType;
import com.jelly.player.VipConstant;
import com.jelly.quest.TaskType;

public class PlayerCommandHandler extends SubJsonCommandHandler {

	private final PlayerSession playerSession;
	private final DefaultPlayer player;
	private static final Logger logger = LoggerFactory.getLogger(PlayerCommandHandler.class);

	public PlayerCommandHandler(Session session) {
		this.playerSession = (PlayerSession) session;
		player = (DefaultPlayer) this.playerSession.getPlayer();
		player.getBuilding().checkOpenBuilding(player.getProperty().getLevel(), player);
		addHandler(setFighter);
		addHandler(setCurrentHero);
		addHandler(fightPVE);
		addHandler(aids);
		addHandler(openAids);

		addHandler(submitQuest);
		addHandler(questReward);
		addHandler(refreshQuest);
		addHandler(raffle);
		addHandler(giveEnergy);
		addHandler(sysAddEnergy);
		addHandler(sysGiveEnergy);
		addHandler(sysGiveItem);
		addHandler(buyEnergy);
		addHandler(buySilver);
		addHandler(practise);
		addHandler(endPractise);
		addHandler(exchange);
		addHandler(beastInject);
		addHandler(recruit);
		addHandler(takeForce);
		addHandler(takeFace);
		addHandler(dayReward);
		// 上忍考试
		addHandler(doExamOption);
		addHandler(examFight);
		addHandler(endExam);
		addHandler(quickExam);
		// 挖宝
		addHandler(dig);
		addHandler(endDig);
		
		
		addHandler(buyWarItem);
		addHandler(refreshWarItem);
		addHandler(resetFuben);
		addHandler(checkFuben);
		addHandler(newerGift);
//		addHandler(requestGift);
		addHandler(getGift);
		addHandler(buyFund);
		addHandler(buyPvpTimes);
		
		//商店购买 刷新
		addHandler(refreshTypeShop);
		addHandler(buyShopItem);
		addHandler(normalRefreshShop);
		//忍着训练
		addHandler(trainHero);
		addHandler(trainHeroCommit);
		
		// 新商店
		addHandler(getShopInfo);
		addHandler(buyShopItemFunc);
		addHandler(refreshShopFunc);
		
		// 忍者大战商店
		addHandler(getWarShopInfo);
		addHandler(buyWarShopFunc);
		addHandler(refreshWarShopFunc);
		
		// 竞技商店
		addHandler(getJingjiInfo);
		addHandler(buyJingjiShopFunc);
		addHandler(refreshJingjiShopFunc);
	}

	JsonCommandHandler newerGift = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "newerGift";
		}

		@Override
		public void run(JsonNode object) {
			try {
				String key = object.asText();
				if (key.isEmpty() || key.length() != 16) {
					return;
				}
				if (!ContextConfig.NEWER_CARD_OPEN) {
					return;
				}


//				CardModel mediaCard = DESPlusManager.getInstance().getCardModel(key);
//				
//				if (mediaCard == null) {
//					player.sendResult(OperResultType.DAILY_REWARD, MessageCode.CARD_IS_WRONG);
//					return;
//				}
				
//				if(mediaCard.getChannel() != null && !mediaCard.getChannel().equals(player.getProperty().getCh())) {
//					player.sendResult(OperResultType.DAILY_REWARD, MessageCode.BAN_OTHER_CHANNEL);
//					return;
//				}
				
				String channel = player.getProperty().getCh();
				CardModel mediaCard = DESPlusManager.getInstance().getCardModelByChannel(channel, key);
				if(mediaCard == null) {
					channel = DESPlusManager.ALL_CHANNLE_ID;
					mediaCard = DESPlusManager.getInstance().getCardModelByChannel(channel, key);
					if(mediaCard == null) {
						player.sendResult(OperResultType.DAILY_REWARD, MessageCode.CARD_IS_WRONG);
						return;
					}
				}
				
				if (DESPlusManager.getInstance().isUsed(channel,key)) {
					player.sendResult(OperResultType.DAILY_REWARD, MessageCode.CARD_IS_USED);
					return;
				}
				

				VariousItemEntry[] rewards = mediaCard.getCard().getReward();
				int checkCode = VariousItemUtil.checkBonus(player, rewards, true);
				if (checkCode != MessageCode.OK) {
					player.sendResult(OperResultType.DAILY_REWARD, checkCode);
					return;
				}

				if (player.getProperty().containCardMark(mediaCard.getCard().getMark())) {
					player.sendResult(OperResultType.DAILY_REWARD, MessageCode.CARD_CAN_NOT_USED);
					return;
				}

				player.getProperty().addCardMark(mediaCard.getCard().getMark());

				VariousItemUtil.doBonus(player, mediaCard.getCard().getReward(), LogConst.CARD_REWARD, true);
				
				DESPlusManager.getInstance().addUsedKeys(channel,key);
				
				player.sendResult(OperResultType.DAILY_REWARD, VariousItemUtil.itemToJson(mediaCard.getCard().getReward()));

			} catch (Exception e) {
				logger.error("", e);
			}

		}

	};
	JsonCommandHandler setFighter = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "setFighter";
		}
		
		@Override
		public void run(JsonNode object) {
			ArrayNode hids = (ArrayNode) object.get("hids");
			player.getHeros().setToFighterHero(hids);
			player.getAllPlayersCache().updatePlayerPower(player);
		}
	};
	JsonCommandHandler buyFund = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buyFund";
		}
		
		@Override
		public void run(JsonNode object) {
			if (!player.getProperty().getVipFun(VipConstant.BUG_FUND)) {
				logger.error("没有权限购买基金 vipLv:",player.getProperty().getVipLv());
				return;
			}
			if(player.getProperty().containStatus(PlayerProperty.BUY_FUND)) {
				logger.error("已经购买过基金");
				return;
			}
			int needGold =(Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.FUND_COST);
			VariousItemEntry entry = new VariousItemEntry("gold", needGold);
			int code = VariousItemUtil.doBonus(player, entry, LogConst.BUY_FUND, false);
			
			if(code == MessageCode.OK) {
				player.getProperty().addStatus(PlayerProperty.BUY_FUND);
				player.getProperty().addChange("fund", 1);
			}else {
				player.sendMiddleMessage(code);
			}
			
		}
		
	};
	
//	JsonCommandHandler requestGift = new JsonCommandHandler() {
//		@Override
//		public String getCommand() {
//			return "requestGift";
//		}
//		
//		@Override
//		public void run(JsonNode object) {
//			JsonNode giftJson = player.getProperty().toGiftJson();
//			
//			player.sendMessage(player.getProperty().getKey(),giftJson);
//		}
//	};
	
	JsonCommandHandler getGift = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getGift";
		}
		
		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt();
			int idx = object.get("idx").asInt();
			Gift gift = AllGameConfig.getInstance().gifts.getGift(id, idx);
			int value = gift.getValue();
			if(player.getProperty().containGiftStatus(id, idx)) {
				player.sendResult(OperResultType.GIFT_GAIN,MessageCode.CARD_CAN_NOT_USED);
				return;
			}
			GiftType giftType = GiftType.getGiftType(gift.getType());
			int check = giftType.check(player,id, value);
			if(check == MessageCode.OK) {
				VariousItemEntry[] items = gift.getItems();
				int code = VariousItemUtil.checkBonus(player, items, true);
				if(code != MessageCode.OK) {
					player.sendResult(OperResultType.GIFT_GAIN,code);
				} else {
					player.getProperty().addGiftStatus(id,idx);
					VariousItemUtil.doBonus(player, items, LogConst.GIFT_GAIN, true);
					player.sendResult(OperResultType.GIFT_GAIN,VariousItemUtil.itemToJson(items));
				}
			}else {
				player.sendResult(OperResultType.GIFT_GAIN,check);
			}
		}
	};
	
	JsonCommandHandler buyWarItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buyWarItem";
		}
		
		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt();
			int idx = object.get("idx").asInt();
			int itemNeedCoin = AllGameConfig.getInstance().items.getItemNeedCoin(id, idx);
			VariousItemEntry needCoin = new VariousItemEntry("coin", itemNeedCoin);
			int code = VariousItemUtil.checkBonus(player, needCoin, false);
			if (code != MessageCode.OK) {
				player.sendResult(OperResultType.BUY_ITEM, code);
				return;
			}
			VariousItemEntry groupItem = AllGameConfig.getInstance().items.getGroupItem(id, idx);
			code = VariousItemUtil.checkBonus(player, groupItem, true);
			if (code == MessageCode.OK) {
				VariousItemUtil.doBonus(player, needCoin, MessageCode.WAR_EXCHANGE, false);
				VariousItemUtil.doBonus(player, groupItem, MessageCode.WAR_EXCHANGE, true);
				player.sendResult(OperResultType.BUY_ITEM, MessageCode.OK,idx);
			}else {
				player.sendResult(OperResultType.BUY_ITEM, code);
			}
		}
		
	};
	
	
	JsonCommandHandler refreshWarItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refreshWarItem";
		}
		@Override
		public void run(JsonNode object) {
			int coin = (Integer)AllGameConfig.getInstance().gconst.getConstant(GameConstId.RS_COIN);
			if(player.getProperty().hasEnoughMoney(null, null, coin)) {
				player.getProperty().addCoin(-coin);
				player.sendResult(OperResultType.REFRESH_ITEM);
				LogUtil.doAcquireLog(player, LogConst.REFRESH_WARITEM, coin, player.getProperty().getCoin(), "coin");
			}else {
				player.sendResult(OperResultType.REFRESH_ITEM,MessageCode.FAIL);
			}
		}
		
	};

	JsonCommandHandler buyShopItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buyShopItem";
		}
		
		@Override
		public void run(JsonNode object) {
			ShopType type = ShopType.parse(object.get("type").asInt());
			if(type != null && type.checkBuy(player)){
				type.buyItem(player, type, object.get("idx").asInt());
			}
		}
	};
	
	
	JsonCommandHandler refreshTypeShop = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refreshTypeShop";
		}
		
		@Override
		public void run(JsonNode object) {
			ShopType type = ShopType.parse(object.get("type").asInt());
			if (!type.checkRefresh(player)){
				player.sendResult(OperResultType.REFRESH_SHOP, MessageCode.VIPLEVEL_NOT_ENUGH);
				return;
			}
			type.buyRefresh(player);
		}
	};
	
	JsonCommandHandler normalRefreshShop = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "normalRefreshShop";
		}
		
		@Override
		public void run(JsonNode object) {
			for (ShopType type : shopTypes) {
				type.normalRefresh(player);
			}
			player.sendResult(OperResultType.REFRESH_SHOP);
		}

		ShopType[] shopTypes = new ShopType[] { ShopType.SHOP, ShopType.BLACK };
	};
	
	JsonCommandHandler setCurrentHero = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "setCurrentHero";
		}

		@Override
		public void run(JsonNode object) {
			int hid = object.get("hid").asInt();
			player.getHeros().setCurrentHero(hid, player);
			if (logger.isDebugEnabled()) {
				player.getHeros().getCurrentHero().getAllPower();
				player.getAllPlayersCache().updatePlayerPower(player);
			}

		}

	};

	JsonCommandHandler aids = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "aids";
		}

		@Override
		public void run(JsonNode object) {
			List<Integer> aids = Lists.newArrayList();
			for (JsonNode jsonNode : object) {
				aids.add(jsonNode.asInt());
			}
			if (aids.isEmpty()) {
				logger.error("set empty aids");
				return;
			}
			if (aids.size() < PlayerHeros.MAX_FIT_TEAM) {
				List<Integer> tarAids = Lists.newArrayList();
				for (int i = 0; i < PlayerHeros.MAX_FIT_TEAM; i++) {
					if (i < aids.size()) {
						Integer val = aids.get(i);
						tarAids.add(val == null ? 0 : val);
					} else {
						tarAids.add(0);
					}
				}
				player.getHeros().setAidList(tarAids);
			} else {
				player.getHeros().setAidList(aids);
			}
			player.getTask().dispatchEvent(player, TaskType.LINEUP);
		}

	};

	JsonCommandHandler openAids = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "openAids";
		}

		@Override
		public void run(JsonNode object) {
			if (player.getHeros().getAidList().isEmpty()) {
				if (!player.getHeros().hasThisHero(9)) {
					player.getHeros().openHero(9);
				}
				if (!player.getHeros().hasThisHero(10)) {
					player.getHeros().openHero(10);
				}
				player.sendResult(OperResultType.OPEN_ADIS);
			} else {
				player.sendResult(OperResultType.OPEN_ADIS, MessageCode.FAIL);
			}

		}

	};
	
	/**
	 * 重置副本限制次数
	 */
	JsonCommandHandler resetFuben = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "resetFuben";
		}

		@Override
		public void run(JsonNode object) {
			int chapterId = object.get("chapter").asInt();	//	章节ID
			int stageId = object.get("stage").asInt();	//	关卡ID
			int advType = object.get("type").asInt();	//	副本类型
			Adventure adv = AllGameConfig.getInstance().adventures.getAdventure(advType, chapterId, stageId);
			if (adv == null) {
				logger.error("当前关卡未开启 cid=" + chapterId + " sid=" + stageId);
				return;
			}
			
			PlayerProperty property = player.getProperty();
			//	判断是否有足够的金币重置副本挑战次数
			int curResetCount = property.getFubenReset(adv.getType(), chapterId, stageId);
			int costGold = AllGameConfig.getInstance().adventures.getResetFubenGold(adv.getType(), curResetCount);			
			if (property.getGold() < costGold) {
				logger.error("重置副本金币不足 gold=" + property.getGold() + " need=" + costGold);
				player.sendResult(OperResultType.RESET_FUBEN, MessageCode.GOLD_NOT_ENUGH);
				return;
			}
			
			//判断重置次数
			if (curResetCount >= AllGameConfig.getInstance().adventures.getResetFubenTimes(adv.getType(), property.getVipLv())) {
				logger.error("重置次数已用完 times=" + curResetCount);
				player.sendResult(OperResultType.RESET_FUBEN, MessageCode.FAIL);
				return;
			}
			
			property.resetFubenUse(adv.getType(), chapterId, stageId);
			property.addFubenReset(adv.getType(), chapterId, stageId, 1);
			VariousItemEntry entry = new VariousItemEntry("gold", costGold);
			VariousItemUtil.doBonus(player, entry, LogConst.RESET_FUBEN, false);
			player.sendResult(OperResultType.RESET_FUBEN, MessageCode.OK);
		}
	};
	
	/**
	 * 检查重置副本
	 */
	JsonCommandHandler checkFuben = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "checkFuben";
		}

		@Override
		public void run(JsonNode object) {
			PlayerProperty property = player.getProperty();
			//处理副本重置
			if (!TimeUtil.isSameDay(property.getLastFubenReset())) {
				property.clearFubenReset(1);
				property.clearFubenReset(2);
			}
			if (!TimeUtil.isSameDay(property.getLastFubenUse())) {
				property.clearFubenUse(1);
				property.clearFubenUse(2);
			}
		}
	};


	JsonCommandHandler fightPVE = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "PVE";
		}

		@Override
		public void run(JsonNode object) {
			int cid = object.get("chapter").asInt();
			int sid = object.get("stage").asInt();
			int sweep = object.get("sweep").asInt();
			int advType = object.get("type").asInt();
			
			AdventureConfigManager advm = AllGameConfig.getInstance().adventures;
			Adventure adv = AllGameConfig.getInstance().adventures.getAdventure(advType, cid, sid);
			if (adv == null)
				return;
			if (adv.getPreStage() != null && !player.getAdventure().containStage(adv.getType(), cid, adv.getPreStage())) {
				logger.error("前置关卡未开启");
				return;
			}
			int pveTime = (sweep == 0 ? 1 : sweep);
			int leftTime = (advType == 1 ? 3 : 2) - player.getProperty().getFubenUse(advType, cid, sid);
			if (pveTime > leftTime) {
				logger.error("副本次数已用完, cid={},sid={},{}>{}",cid,sid,pveTime,leftTime);
				return;
			}
			if (sweep == 0) {
				VariousItemEntry needEnergyEntry = new VariousItemEntry("energy", advm.getAdventure(advType, cid, sid).getEnergy());
				int code = VariousItemUtil.checkBonus(player, needEnergyEntry, false);
				if (code != MessageCode.OK) {
					player.sendMiddleMessage(code);
					return;
				}
				AttackerGroup attackerGroup = player.getHeros().getAttackerGroup();
				DefenderGroup defenderGroup = AllGameConfig.getInstance().adventures.getDefenderGroup(advType, cid, sid);
				PVECombatManager pveCombatManager = new PVECombatManager(attackerGroup, defenderGroup, advType, cid, sid, player);
				pveCombatManager.start();
			} else {
				// 扫荡改为不扣金币 
//				Vip vip = AllGameConfig.getInstance().activitys.getVip(player.getProperty().getVipLv());
//				int cost = vip.getDiscount()[VipConstant.DISCOUNT_SWEEP] * sweep;
//				if (cost > 0) {
//					if (!player.getProperty().hasEnoughMoney("gold", cost)) {
//						player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
//						return;
//					} else {
//						player.getProperty().changeMoney("gold", -cost, LogConst.SWEEP_COST, player);
//					}
//				}
				List<VariousItemEntry> items = player.getAdventure().sweepAdv(player, adv.getType(), cid, sid, sweep);
				if (items.size() != 0) {
					ObjectNode rewards = VariousItemUtil.itemToJson(items);
					player.sendResult(OperResultType.SWEEP, rewards);
					// 军团经验
				}
				player.getTeam().addExp(adv.getGuildEXP() * sweep);
				player.getProperty().addFubenUse(advType, cid, sid, sweep);
				if (adv.getType() == 1) {
					LogUtil.doGuanQiaLog(player, cid + "-" + sid, 1, 1);
				} else {
					LogUtil.doGuanQiaLog(player, cid + "-" + sid, 3, 1);
				}
			}

		}

	};

	JsonCommandHandler submitQuest = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "submitQuest";
		}

		@Override
		public void run(JsonNode object) {
			int id = object.get("id").asInt();
			if (id <= 0) {
				return;
			}
			player.getTask().submitTask(id, player);
		}
	};
	JsonCommandHandler questReward = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "questReward";
		}

		@Override
		public void run(JsonNode object) {
			int idx = object.get("idx").asInt();
			player.getTask().gainReward(player, idx);
		}
	};

	JsonCommandHandler refreshQuest = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refreshQuest";
		}

		@Override
		public void run(JsonNode object) {
			//player.getTask().refreshQuest(player);
		}
	};

	/**
	 * 抽取忍者
	 */
	JsonCommandHandler raffle = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "raffle";
		}

		@Override
		public void run(JsonNode object) {
			player.getHeros().raffleHero(object.get("idx").asInt(), player);
		}
	};

	/**
	 * 系统自动添加体力
	 */
	JsonCommandHandler sysAddEnergy = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "sysAddEnergy";
		}

		@Override
		public void run(JsonNode object) {
			String text = object.asText();
			if (logger.isDebugEnabled()) {
				logger.debug("rev ... sysAddEnergy,userId:{}", player.getProperty().getUserId());
			}
			if (text.equals(ContextConfig.SYS_KEY)) {
				player.getProperty().giveEnergyByTimer();
			}
		}
	};
	/**
	 * 系统自动添加体力
	 */
	JsonCommandHandler sysGiveEnergy = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "sysGiveEnergy";
		}

		@Override
		public void run(JsonNode object) {
			String text = object.asText();
			if (logger.isDebugEnabled()) {
				logger.debug("rev ... sysGiveEnergy,userId:{}", player.getProperty().getUserId());
			}
			if (text.equals(ContextConfig.SYS_KEY)) {
				player.getProperty().checkRewardEnergy(player);
			}
		}
	};
	/**
	 * 系统奖励给予的道具
	 */
	JsonCommandHandler sysGiveItem = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "sysGiveItem";
		}
		
		@Override
		public void run(JsonNode object) {
			String itemString = object.get("item").asText();
			int titleId = object.get("titleId").asInt();
			int contentId = object.get("contentId").asInt();
			int isTip = object.get("isTip").asInt();
			String contentText = object.get("contentText").asText();
			VariousItemEntry[] items = VariousItemUtil.parse1(itemString);
			player.getMail().addSysItemMail(items, titleId, contentId,contentText);
			if (isTip == 1) {
				player.sendMiddleMessage(MessageCode.MAIL_REWARD_TIP);
			}
		}
	};

	/**
	 * 玩家购买体力
	 */
	JsonCommandHandler buyEnergy = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buyEnergy";
		}

		@Override
		public void run(JsonNode object) {
			player.getProperty().buyEnergy(player);
		}
	};
	
	/**
	 * 玩家购买体力
	 */
	JsonCommandHandler buyPvpTimes = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buyPvpTimes";
		}

		@Override
		public void run(JsonNode object) {
			player.getProperty().buyPVPTimes(player);
		}
	};

	/**
	 * 玩家购买体力
	 */
	JsonCommandHandler buySilver = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buySilver";
		}

		@Override
		public void run(JsonNode object) {
			player.getProperty().buySilver(player);
		}
	};

	/**
	 * 拉面馆给东西
	 */
	JsonCommandHandler giveEnergy = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "giveEnergy";
		}

		@Override
		public void run(JsonNode object) {
			player.getProperty().giveEnergy(player);

		}
	};

	/**
	 * 忍者修炼
	 */
	JsonCommandHandler practise = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "practise";
		}

		@Override
		public void run(JsonNode object) {
			int practiseType = object.get("ptype").asInt();
			Integer hid = object.get("hid").asInt();
			player.getPractise().practise(player, hid, practiseType);
		}
	};

	/**
	 * 结束修炼和领取奖励
	 */
	JsonCommandHandler endPractise = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "endPractise";
		}

		@Override
		public void run(JsonNode object) {
			player.getPractise().endPractise(player, object.get("cd").asInt());
		}
	};

	/**
	 * 实验室的0经验转移1血继转移
	 */
	JsonCommandHandler exchange = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "exchange";
		}

		@Override
		public void run(JsonNode object) {
			int type = object.get("type").asInt();
			int tarId = object.get("tarId").asInt();
			if (type == PlayerHeros.EXCHANGE_EXP) {
				player.getHeros().exchangeExp(player, type, (ArrayNode) object.get("srcList"), tarId);
			} else {
				int srcId = object.get("srcId").asInt();
				int srcIdx = object.get("srcIdx").asInt();
				int tarIdx = object.get("tarIdx").asInt();
				player.getHeros().exchange(player, type, srcId, tarId, srcIdx, tarIdx);
				player.getAllPlayersCache().updatePlayerPower(player);
			}
		}
	};
	/**
	 * 九尾技能注入
	 */
	JsonCommandHandler beastInject = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "beastInject";
		}

		@Override
		public void run(JsonNode object) {
			Integer hid = object.get("hid").asInt();
			Integer bid = object.get("bid").asInt();
			player.getHeros().skillInject(player, hid, bid);
		}
	};
	/**
	 * 招募
	 */
	JsonCommandHandler recruit = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "recruit";
		}

		@Override
		public void run(JsonNode object) {
			player.getRecruit().recruit(player);
		}
	};

	/**
	 * 打劫
	 */
	JsonCommandHandler takeForce = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "takeForce";
		}

		@Override
		public void run(JsonNode object) {
			boolean maxFinish = player.getBuilding().isMaxFinish(BuildingType.LMG.getId(), player);
			if (!maxFinish) {
				int value = (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.FORCE_SILVER);
				float muti = Float.parseFloat(ActivityType.TAKE_MUTI.getValue());
				int real = (int) (value * muti);
				player.getProperty().changeMoney(0, real);
				player.sendResult(OperResultType.LMG,MessageCode.OK,real);
				player.getTask().dispatchEvent(player, TaskType.TAKE_FORCE);
				player.getActivity().dispatchEvent(ActivityType.TAKE_0, player);
				LogUtil.doAcquireLog(player, LogConst.LAMIAN_TAKEFORCE, real, player.getProperty().getSilver(), "silver");
			}
		}
	};

	/**
	 * 打劫
	 */
	JsonCommandHandler takeFace = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "takeFace";
		}

		@Override
		public void run(JsonNode object) {
//			player.getProperty().takeSilver();
			if (player.getProperty().getTakeSilverTimes() >= (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TAKE_NUM) ) {
				return;
			}
			int value = (Integer) AllGameConfig.getInstance().gconst.getConstant(GameConstId.TAKE_SILVER);
			player.getProperty().setTakeSilverTimes(player.getProperty().getTakeSilverTimes() + 1);
			player.getProperty().changeMoney(0, value);
			LogUtil.doAcquireLog(player, LogConst.MAINSCENE_TAKEFORCE, value, player.getProperty().getSilver(), "silver");
		}
	};

	/**
	 * 累计登录每日奖励 //TODO 背包满了，放到邮箱里
	 */
	JsonCommandHandler dayReward = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "dayReward";
		}

		@Override
		public void run(JsonNode object) {
			if (player.getProperty().canDailyReward() != 1) {
				logger.error("已领过每日奖励！");
				player.sendResult(OperResultType.DAILY_REWARD, MessageCode.FAIL, null);
				return;
			}
			int days = player.getProperty().getRewardDay();
			DayReward conf = AllGameConfig.getInstance().activitys.getDayRewards(days);
			VariousItemEntry tmp = conf.getItem()[0];
			VariousItemEntry item = new VariousItemEntry(tmp.getType(), tmp.getAmount());

			if (conf.getV2() != null && conf.getV2() == 1 && player.getProperty().getVipLv() >= 2) {
				item.setAmount(item.getAmount() * 2);
			}
			VariousItemUtil.doBonus(player, item, LogConst.ACTIVE_REWARD, true);
			player.getProperty().getDayReward();
			player.sendResult(OperResultType.DAILY_REWARD, VariousItemUtil.itemToJson(item));
		}
	};

	JsonCommandHandler doExamOption = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "doExamOption";
		}

		@Override
		public void run(JsonNode object) {
			ArrayNode node = DynamicJsonProperty.jackson.createArrayNode();
			player.getExam().doExamOption(player, object.get("idx").asInt(), node);
			if (node.size() != 0) {
				player.sendResult(OperResultType.SRKSXZ, node);
			}
		}
	};

	JsonCommandHandler examFight = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "examFight";
		}

		@Override
		public void run(JsonNode object) {
			player.getExam().examFight(player);
			LogUtil.doNinjaTestLog(player, player.getExam().getCurrPass()+1, 0);
		}
	};

	JsonCommandHandler endExam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "endExam";
		}

		@Override
		public void run(JsonNode object) {
			player.getExam().endExam(player);
		}
	};

	JsonCommandHandler quickExam = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "quickExam";
		}

		@Override
		public void run(JsonNode object) {
			ArrayNode node = DynamicJsonProperty.jackson.createArrayNode();
			player.getExam().quickExam(player, node);
			player.sendResult(OperResultType.SRKSKSZD, node);
		}
	};



	List<Integer> rndPreciousIds;
	JsonCommandHandler dig = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "dig";
		}

		@Override
		public void run(JsonNode object) {
			int buildingType = BuildingType.DB.getId();
			Building building = AllGameConfig.getInstance().buildings.getBuilding(buildingType);
			if (player.getBuilding().isCding(buildingType)) {
				int code = VariousItemUtil.doBonus(player, building.getFuncCost()[0], LogConst.DIG_ITEM, false);
				if (code != MessageCode.OK) {
					player.sendMiddleMessage(MessageCode.GOLD_NOT_ENUGH);
					return;
				}
			} else {
				if (!player.getBuilding().checkMaxFinishAndAddFunCd(buildingType, building, player, LogConst.DIG_ITEM, 0)) {
					return;
				}
			}
			rndPreciousIds = AllGameConfig.getInstance().drops.getRndPreciousIds();
			player.sendResult(OperResultType.DIG, DynamicJsonProperty.convertToArrayNode(rndPreciousIds));
			player.getTask().dispatchEvent(player, TaskType.DIG);
			player.getActivity().dispatchEvent(ActivityType.DIG_0, player);
		}

	};

	JsonCommandHandler endDig = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "endDig";
		}

		@Override
		public void run(JsonNode object) {
			if (object.isArray()) {
				ArrayNode arrayNode = (ArrayNode) object;
				int size = arrayNode.size();
				if (size > 6) {
					player.sendMiddleMessage(MessageCode.FAIL);
					return;
				}
				if (rndPreciousIds == null) {
					logger.error("rndPreciousIds is null");
					return;
				}
				Iterator<JsonNode> iterator = arrayNode.iterator();
				List<VariousItemEntry> digItems = Lists.newArrayList();
				while (iterator.hasNext()) {
					int id = iterator.next().asInt();
					Dig digItem = AllGameConfig.getInstance().drops.getDigItemById(id);
					if (rndPreciousIds.contains(id)) {
						rndPreciousIds.remove(rndPreciousIds.indexOf(id));
					} else {
						if (digItem.getPrecious() == 1) {
							logger.error("作弊？playername:{}",player.getName());
							return;
						}
					}
					VariousItemEntry itemEntry = new VariousItemEntry(digItem.getType(), digItem.getAmount());
					digItems.add(itemEntry);
				}
				rndPreciousIds = null;
				if (digItems.isEmpty() == false) {
					VariousItemUtil.doBonus(player, digItems, LogConst.DIG_ITEM, true);
				}
			}
		}
	};
	
	//忍着训练
	JsonCommandHandler trainHero = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "trainHero";
		}

		@Override
		public void run(JsonNode object) {
			int hid = object.get("hid").asInt(-1);
			int idx = object.get("idx").asInt(-1);
			player.getHeros().trainHero(player, hid, idx);
		}
		
	};
	
	//忍着训练结果确认
	JsonCommandHandler trainHeroCommit = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "trainHeroCommit";
		}

		@Override
		public void run(JsonNode object) {
			int hid = object.get("hid").asInt(-1);
			int type = object.get("type").asInt(0);
			player.getHeros().commitTrainProp(player, hid, type);
		}
			
	};
	
	/// 新商店
	JsonCommandHandler getShopInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getShopInfo";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().getShopInfo(player);
		}
	};
	
	JsonCommandHandler buyShopItemFunc = new JsonCommandHandler() {
		
		@Override
		public String getCommand() {
			return "buyShopItemFunc";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().buyShopItemFunc(player,object);
		}
	};
	
	JsonCommandHandler refreshShopFunc = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refreshShopFunc";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().refreshShopFunc(player);
		}
	};
	
	///// 忍者大战商店
	JsonCommandHandler getWarShopInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getWarShopInfo";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().getWarShopInfo(player);
		}
	};
	
	JsonCommandHandler buyWarShopFunc = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buyWarShopFunc";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().buyWarShopFunc(player,object);
		}
	};
	
	JsonCommandHandler refreshWarShopFunc = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refreshWarShopFunc";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().refreshWarShopFunc(player);
		}
	};
	
	///////////////////////////////////////// 竞技商店 /////////////////////////////////////////
	
	JsonCommandHandler getJingjiInfo = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "getJingjiInfo";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().getJingjiInfo(player);
		}
	};
	
	JsonCommandHandler buyJingjiShopFunc = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "buyJingjiShopFunc";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().buyJingjiShopFunc(player,object);
		}
	};
	
	JsonCommandHandler refreshJingjiShopFunc = new JsonCommandHandler() {
		@Override
		public String getCommand() {
			return "refreshJingjiShopFunc";
		}
		
		@Override
		public void run(JsonNode object) {
			player.getNewShop().refreshJingjiShopFunc(player);
		}
	};
	
}
