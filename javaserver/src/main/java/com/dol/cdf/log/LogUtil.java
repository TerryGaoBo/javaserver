package com.dol.cdf.log;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.context.AppContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.bean.Recharge;
import com.dol.cdf.common.bean.Role;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.log.msg.CurrencyChangeLogMessage;
import com.dol.cdf.log.msg.ItemChangeLogMessage;
import com.dol.cdf.log.msg.ItemTradeLogMessage;
import com.dol.cdf.log.msghd.HdAcquire;
import com.dol.cdf.log.msghd.HdActivate;
import com.dol.cdf.log.msghd.HdActivity;
import com.dol.cdf.log.msghd.HdBaseLog;
import com.dol.cdf.log.msghd.HdBeast;
import com.dol.cdf.log.msghd.HdCatch;
import com.dol.cdf.log.msghd.HdChange;
import com.dol.cdf.log.msghd.HdChurchChallenge;
import com.dol.cdf.log.msghd.HdCreateRole;
import com.dol.cdf.log.msghd.HdEquipLel;
import com.dol.cdf.log.msghd.HdFinishTask;
import com.dol.cdf.log.msghd.HdGetItem;
import com.dol.cdf.log.msghd.HdLevelup;
import com.dol.cdf.log.msghd.HdLogin;
import com.dol.cdf.log.msghd.HdLogout;
import com.dol.cdf.log.msghd.HdMoneyCost;
import com.dol.cdf.log.msghd.HdNinja;
import com.dol.cdf.log.msghd.HdNinjaTest;
import com.dol.cdf.log.msghd.HdPoint;
import com.dol.cdf.log.msghd.HdPractice;
import com.dol.cdf.log.msghd.HdPveFlight;
import com.dol.cdf.log.msghd.HdRecharge;
import com.dol.cdf.log.msghd.HdRefining;
import com.dol.cdf.log.msghd.HdRegister;
import com.dol.cdf.log.msghd.HdRemoveItem;
import com.dol.cdf.log.msghd.HdScholopractice;
import com.dol.cdf.log.msghd.HdSpa;
import com.dol.cdf.log.msghd.HdSwallow;
import com.jelly.node.datastore.mapper.Pay;
import com.jelly.node.datastore.mapper.PayMapper;
import com.jelly.player.ItemInstance;
import com.jelly.player.PlayerProperty;

public class LogUtil {

	/**
	 * 日志是否开启
	 */
	public static boolean isOpen = true;

	public static boolean logFilter = false;
	
	private static final Logger logger = LoggerFactory.getLogger("mhLog");

	public static List<String> filterList = new ArrayList<String>();
	static {
		filterList.add("acquire");
		filterList.add("moneycost");
		filterList.add("getitem");
		filterList.add("PVEfight");
		try {
			InputStream is = ClassLoader
					.getSystemResourceAsStream("nadron/props/filterBuyPointLog.txt");
			byte[] data = new byte[is.available()];
			is.read(data);
			for (String line : new String(data, "utf8").split("\r\n")) {
				filterList.add(line.trim());
			}
		} catch (IOException ex) {
		}
	}

	/**
	 * 日志消息队列
	 */
	public static Queue<HdBaseLog> queue = new ConcurrentLinkedQueue<HdBaseLog>();

	/**
	 * 在线人数 5分钟记录一次
	 */
	public static List<Integer> onlineData = new ArrayList<Integer>();

	/**
	 * 发送日志
	 * 
	 * @param log
	 */
	public synchronized static void sendLog(HdBaseLog log) {
		if (!isOpen)
			return;
		if (AllGameConfig.getInstance().env == RuntimeEnv.TEST
				|| AllGameConfig.getInstance().env == RuntimeEnv.STAGE
				|| AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
//			queue.add(log);
			logger.info(log.toString());
		}
	}

	public static String getAppKey(String channel) {
		return "1";// 赫德固定为1
	}

	public static long lastOnlineLog = 0;

	public static BASE64Decoder de = new BASE64Decoder();

	private static PayMapper payMapper = (PayMapper) AppContext
			.getBean("payMapper");

	public static void doPayLog(DefaultPlayer player, Recharge recharge,
			String channel, boolean firstPay, String orderId, int gold) {
		// 记录充值日志
		Pay pay = new Pay();
		pay.setGuid(player.guid);
		pay.setUserId(player.getProperty().getUserId());
		pay.setStatus(1);
		pay.setItemId(recharge.getId());
		pay.setOrderId(orderId);
		pay.setRmb(recharge.getRmb());
		pay.setExchange(recharge.getGold());
		if (firstPay) {
			pay.setBouns(recharge.getFistGive());
		} else {
			pay.setBouns(recharge.getGive());
		}
		pay.setChannel(channel);
		pay.setParam("");
		payMapper.insert(pay);
		if (gold > 0) {
			doAcquireLog(player, LogConst.PAY_ADD_GOLD, gold, player
					.getProperty().getGold(), "gold");
		}
		doRechargeLog(player, String.valueOf(recharge.getRmb()));

		/*
		 * 
		 * { "original-purchase-date-pst" =
		 * "2014-05-16 02:50:42 America/Los_Angeles"; "unique-identifier" =
		 * "2bd40455209b9392250465344dc793ec63dae196"; "original-transaction-id"
		 * = "1000000111010175"; "bvrs" = "1.0"; "transaction-id" =
		 * "1000000111010175"; "quantity" = "1"; "original-purchase-date-ms" =
		 * "1400233842892"; "unique-vendor-identifier" =
		 * "55082D61-4B39-439B-8800-D19B200C2637"; "product-id" =
		 * "com.jelly.hy.gold1"; "item-id" = "877433215"; "bid" =
		 * "com.disney.game.xyy"; "purchase-date-ms" = "1400233842892";
		 * "purchase-date" = "2014-05-16 09:50:42 Etc/GMT"; "purchase-date-pst"
		 * = "2014-05-16 02:50:42 America/Los_Angeles"; "original-purchase-date"
		 * = "2014-05-16 09:50:42 Etc/GMT"; }
		 */
	}

	/**
	 * 1.激活日志
	 */
	public static void doActivateLog(String deviceId, String channelId) {
		if (!isOpen || (logFilter && filterList.contains("activate"))) {
			return;
		}
		HdActivate log = new HdActivate(deviceId, channelId);
		sendLog(log);
	}

	/**
	 * 2.注册日志
	 */
	public static void doRegisterLog(DefaultPlayer player, String clientInfo) {
		if (!isOpen || (logFilter && filterList.contains("register"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		String[] info = clientInfo.split("_");
		if (info.length >= 4) {
			HdRegister log = new HdRegister(pp.getCh(), info[0],
					pp.getUserId(), "", info[3], info[2], info[1]);
			sendLog(log);
		}
	}

	/**
	 * 3.创建角色日志
	 */
	public static void doCreateRoleLog(DefaultPlayer player, String idfa,
			String dev) {
		if (!isOpen || (logFilter && filterList.contains("rolebuild"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		String name = "";
		if (player.getRole() != null) {
			name = player.getName();
		}
		HdCreateRole log = new HdCreateRole(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), name, idfa, dev);
		sendLog(log);
	}

	/**
	 * 4.登录日志
	 */
	public static void doLoginLog(DefaultPlayer player,String remoteAddres) {
		if (!isOpen || (logFilter && filterList.contains("login"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		String name = "";
		if (player.getRole() != null) {
			name = player.getName();
		}
		sendLog(new HdLogin(pp.getLogNet(), pp.getCh(), pp.getUserId(), "",
				player.getId(), name, pp.getLevel(),remoteAddres));
	}

	/**
	 * 5.退出日志
	 */
	public static void doLogoutLog(DefaultPlayer player) {
		if (!isOpen || (logFilter && filterList.contains("logout"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		String name = "";
		if (player.getRole() != null) {
			name = player.getName();
		}
		sendLog(new HdLogout(pp.getLogNet(), pp.getCh(), pp.getUserId(), "",
				player.getId(), name, pp.getLevel()));
	}

	/**
	 * 6.充值日志
	 * 
	 * @param player
	 * @param entity
	 */
	public static void doRechargeLog(DefaultPlayer player, String rmb) {
		if (!isOpen || (logFilter && filterList.contains("recharge"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdRecharge log = new HdRecharge(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), rmb, "", "");
		sendLog(log);
	}

	/**
	 * 7.升级日志
	 */
	public static void doLevelupLog(DefaultPlayer player) {
		if (!isOpen || (logFilter && filterList.contains("levelup"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdLevelup log = new HdLevelup(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), player.getName(),
				pp.getLevel(), pp.getLevel() - 1, 0);
		sendLog(log);
	}

	/**
	 * 8.货币或经验获得
	 */
	public static void doAcquireLog(DefaultPlayer player, int cause, int count,
			int total, String moneyType) {
		PlayerProperty pp = player.getProperty();
		HdAcquire log = new HdAcquire(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), cause, count,
				total, getMoneyType(moneyType));
		if (!isOpen) {
			return;
		}
		if (logFilter && filterList.contains("acquire")) {
			if (!log.getType().equals("1")) {
				return;
			}
		}
		sendLog(log);
	}

	private static String getMoneyType(String moneyType) {
		if (moneyType.equals("gold")) {
			return "1";
		} else if (moneyType.equals("silver")) {
			return "2";
		} else if (moneyType.equals("coin")) {
			return "3";
		} else if (moneyType.equals("exp")) {
			return "4";
		}
		return "-1";
	}

	private static String getItemName(int itemId) {
		String itemName = AllGameConfig.getInstance().items.getItemName(itemId);
		if (itemName == null || itemName == "") {
			return String.valueOf(itemId);
		}
		return itemName;
	}

	private static String getNinjaName(int ninjaId) {
		Role role = AllGameConfig.getInstance().characterManager
				.getRoleById(ninjaId);
		if (role == null) {
			return String.valueOf(ninjaId);
		}
		return role.getAlt();
	}

	/**
	 * 9.货币消耗
	 */
	public static void doMoneyCostLog(DefaultPlayer player, int cause,
			int count, int total, String moneyType) {
		PlayerProperty pp = player.getProperty();
		HdMoneyCost log = new HdMoneyCost(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), cause, count,
				total, getMoneyType(moneyType));
		if (!isOpen) {
			return;
		}
		if (logFilter && filterList.contains("moneycost")) {
			if (!log.getType().equals("1")) {
				return;
			}
		}
		sendLog(log);
	}

	/**
	 * 10.获得物品
	 */
	public static void doGetItemLog(DefaultPlayer player, int cause,
			int itemId, int count) {
		if (!isOpen || (logFilter && filterList.contains("getitem"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdGetItem log = new HdGetItem(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(),
				player.getProperty().getLevel(), getItemName(itemId), cause,
				count);
		sendLog(log);
	}

	public static void doGetEnergyLog(DefaultPlayer player, int cause, int count) {
		if (!isOpen || (logFilter && filterList.contains("getitem"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdGetItem log = new HdGetItem(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(),
				player.getProperty().getLevel(), "体力", cause, count);
		sendLog(log);
	}

	public static void doGetNinjaLog(DefaultPlayer player, int ninjaId,
			int count, int cause) {
		if (!isOpen || (logFilter && filterList.contains("getitem"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdGetItem log = new HdGetItem(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(),
				player.getProperty().getLevel(), getNinjaName(ninjaId), cause,
				count);
		sendLog(log);
	}

	/**
	 * 11.失去物品
	 */
	public static void doRemoveItemLog(DefaultPlayer player, int cause,
			int itemId, int count) {
		if (!isOpen || (logFilter && filterList.contains("removeitem"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdRemoveItem log = new HdRemoveItem(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(),
				player.getProperty().getLevel(), getItemName(itemId), cause,
				count);
		sendLog(log);
	}

	/**
	 * 12.任务日志
	 * 
	 * @param taskId
	 *            - 任务id
	 */
	public static void doTaskLog(DefaultPlayer player, int taskId) {
		if (!isOpen || (logFilter && filterList.contains("finishtask"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdFinishTask log = new HdFinishTask(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), taskId + "", 1);
		sendLog(log);
	}

	/**
	 * 13.关卡日志
	 * 
	 * @param player
	 * @param guanqiaid
	 *            关卡id
	 * @param flightType
	 *            战斗类型 1-扫荡 2-挑战 3-精英扫荡 4-精英挑战
	 * @param result
	 *            0-失败 1-成功
	 */
	public static void doGuanQiaLog(DefaultPlayer player, String guanqiaid,
			int flightType, int result) {
		if (!isOpen || (logFilter && filterList.contains("PVEfight"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdPveFlight log = new HdPveFlight(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), guanqiaid,
				flightType, "", result);
		sendLog(log);
	}

	/**
	 * 15.活动日志
	 * 
	 * @param taskId
	 *            - 任务id
	 */
	public static void doActivityLog(DefaultPlayer player, String activityId) {
		if (!isOpen || (logFilter && filterList.contains("activity"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdActivity log = new HdActivity(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), activityId);
		sendLog(log);
	}

	/**
	 * 玩法埋点
	 */

	/**
	 * 1 忍术学校修炼
	 * 
	 * @param player
	 * @param ninjaId
	 * @param pType
	 *            修炼方式（1、初级修炼；2.高级修炼；3、卷轴修炼；）
	 * @param isMoney
	 *            是否消耗金币（0：否；1：是）
	 * @param ninjutsuId修炼到的忍术id
	 */
	public static void doPractiseLog(DefaultPlayer player, int ninjaId,
			int pType, int isMoney,int ninjutsuId) {
		if (!isOpen || (logFilter && filterList.contains("practice"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdPractice log = new HdPractice(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), ninjaId, pType,
				isMoney,ninjutsuId);
		sendLog(log);
	}

	/**
	 * 2 抓忍者 ok
	 * 
	 * @param player
	 * @param ninjaId
	 * @param pType
	 *            抓取方式（1、初级抓取；2、高级抓取；3.特技抓取）
	 * @param isMoney
	 *            是否消耗金币（0：否；1：是）
	 */
	public static void doCatchLog(DefaultPlayer player, int ninjaId, int pType,
			int isMoney) {
		if (!isOpen || (logFilter && filterList.contains("catch"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdCatch log = new HdCatch(pp.getLogNet(), pp.getCh(), pp.getUserId(),
				player.getId(), pp.getLevel(), ninjaId, pType, isMoney);
		sendLog(log);
	}

	/**
	 * 3 通灵学院修炼 ok
	 * 
	 * @param player
	 * @param ninjaId
	 * @param pType
	 *            修炼方式（1、初级修炼；2、高级修炼）
	 * @param isMoney
	 * @param scholopracticeId  学习到的忍术id
	 */
	public static void doScholopracticeLog(DefaultPlayer player, int ninjaId,
			int pType, int isMoney,int scholopracticeId) {
		if (!isOpen || (logFilter && filterList.contains("Scholopractice"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdScholopractice log = new HdScholopractice(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), ninjaId, pType,
				isMoney,scholopracticeId);
		sendLog(log);
	}

	/**
	 * 4 隐忍堂挑战 ok
	 * 
	 * @param result
	 *            挑战结果(0:失败，1：成功）
	 */
	public static void doChurchChallengeLog(DefaultPlayer player, int result) {
		if (!isOpen || (logFilter && filterList.contains("churchchallenge"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdChurchChallenge log = new HdChurchChallenge(pp.getLogNet(),
				pp.getCh(), pp.getUserId(), player.getId(), pp.getLevel(),
				result);
		sendLog(log);
	}

	/**
	 * 5 泡温泉 ok
	 * 
	 * @param player
	 * @param ninjaId
	 * @param pType
	 *            温泉类型（1、初级温泉；2、高级温泉）
	 */
	public static void doSpaLog(DefaultPlayer player, int ninjaId, int pType) {
		if (!isOpen || (logFilter && filterList.contains("spa"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdSpa log = new HdSpa(pp.getLogNet(), pp.getCh(), pp.getUserId(),
				player.getId(), pp.getLevel(), ninjaId, pType);
		sendLog(log);
	}

	/**
	 * 实验室忍者吞噬日志
	 * 
	 * @param ninjiaID
	 *            - 忍者ID
	 * @param beNinjiaIDs
	 *            - 被吞噬忍者ID
	 * @param type
	 *            - 0=忍者吞噬,1=忍者进阶,2=忍术转移
	 **/
	public static void doSwallowLog(Player player, int pType, int ninjiaID,
			int beNinjiaID) {
		if (!isOpen || (logFilter && filterList.contains("swallow"))) {
			return;
		}
		int type = 0;
		switch (pType) {
		case 0:
			type = 1;
			break;
		case 1:
			type = 2;
			break;
		case 2:
			type = 3;
			break;
		default:
			break;
		}
		Role role = AllGameConfig.getInstance().characterManager
				.getRoleById(beNinjiaID);
		if (role != null && role.getQuality() >= 4) {
			PlayerProperty pp = player.getProperty();
			HdSwallow log = new HdSwallow(pp.getLogNet(), pp.getCh(),
					pp.getUserId(), player.getId(), pp.getLevel(), type,
					ninjiaID, beNinjiaID);
			sendLog(log);
		}
	}

	/**
	 * 7 上忍考试 ok
	 * 
	 * @param player
	 * @param floor
	 *            达到层数
	 * @param isMoney
	 *            是否消耗金币（0、否；1、是）
	 */
	public static void doNinjaTestLog(DefaultPlayer player, int floor,
			int isMoney) {
		if (!isOpen || (logFilter && filterList.contains("test"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdNinjaTest log = new HdNinjaTest(pp.getLogNet(), pp.getCh(),
				pp.getUserId(), player.getId(), pp.getLevel(), floor, isMoney);
		sendLog(log);
	}

	/**
	 * 8 注入尾兽之力 ok
	 * 
	 * @param player
	 * @param ninjaId
	 * @param count
	 *            尾数（一尾、二尾、….九尾）
	 */
	public static void doBeastLog(DefaultPlayer player, int ninjaId, int count,int beastId) {
		if (!isOpen || (logFilter && filterList.contains("beast"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdBeast log = new HdBeast(pp.getLogNet(), pp.getCh(), pp.getUserId(),
				player.getId(), pp.getLevel(), ninjaId, count,beastId);
		sendLog(log);
	}

	/**
	 * 玩家金钱日志
	 * 
	 * @param player
	 * @param currency
	 *            - 金钱类型
	 * @param amount
	 *            - 改变数量
	 * @param reason
	 *            - 改变原因
	 * @return
	 */
	public static void doMoneyLog(Player player, String currency, int amount,
			int reason) {
		CurrencyChangeLogMessage msg = new CurrencyChangeLogMessage(player);
		msg.setBeforeAmount(player.getProperty().getMoney(currency) - amount);
		msg.setReason(getCurrency(currency));
		msg.setAfterAmount(player.getProperty().getMoney(currency));
		msg.setAmount(amount);
		msg.setParam(String.valueOf(reason));
		// sendLog(msg);
	}

	/**
	 * 玩家道具日志
	 * 
	 * @param player
	 * @param containerId
	 *            - 容器id
	 * @param idx
	 *            - 格子索引
	 * @param item
	 *            - 道具实例
	 * @param amount
	 *            - 改变数量
	 * @param reason
	 *            - 改变原因
	 * @return
	 */
	public static void doItemLog(Player player, int containerId, int idx,
			ItemInstance item, int amount, int reason) {
		ItemChangeLogMessage msg = new ItemChangeLogMessage(player);
		msg.setItem_index(idx);
		msg.setItem_inst_id(String.valueOf(item.getItemId()));
		msg.setItem_tmpl_id(containerId); // 容器id
		msg.setCount_delta(amount); // 变化数量
		msg.setCount_stack(item.getStackCount()); // 改变后的数量
		msg.setReason(reason);
		// sendLog(msg);
	}

	/**
	 * 商城日志
	 * 
	 * @param player
	 * @param itemId
	 *            - 道具id
	 * @param amount
	 *            - 交易数量
	 * @param oper
	 *            - 交易类型 1-购买 2-续费 3-出售
	 * @param price
	 *            - 单价
	 * @param day
	 *            - 道具有效期
	 * @param costType
	 *            - 花费类型 1-金币 2-骨币
	 */
	public static void doShopLog(Player player, int itemId, int amount,
			int oper, int day, int price, int costType) {
		ItemTradeLogMessage msg = new ItemTradeLogMessage(player);
		msg.setItem_inst_id(String.valueOf(itemId));
		msg.setCount(amount);
		msg.setOper(oper);
		msg.setPrice(price);
		msg.setDay(day);
		msg.setCost(price * amount);
		msg.setCostType(costType);
		// sendLog(msg);
	}

	public static int getCurrency(String currency) {
		return currency.equals("silver") ? LogConst.MONEY_SILVER
				: LogConst.MONEY_GOLD;
	}
	
	/**
	 * 装备精练log
	 * @param player
	 * @param equipId  装备id
	 * @param ninjutsuId  忍术id
	 */
	public static void doRefiningLog(Player player, int equipId,
			int ninjutsuId) {
		if (!isOpen || (logFilter && filterList.contains("refining"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdRefining log = new HdRefining(pp.getLogNet(), pp.getUserId(), 
				player.getId(), pp.getLevel(), equipId, ninjutsuId);
		
		sendLog(log);
		
	}
	
	/**
	 * 装备洗练log
	 * @param player
	 * @param equipId  装备id
	 * @param equiplel  第几次洗练
	 */
	public static void doEquipLelLog(Player player, int equipId,
			int equiplel) {
		if (!isOpen || (logFilter && filterList.contains("equiplel"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdEquipLel log = new HdEquipLel(pp.getLogNet(), pp.getUserId(), 
				player.getId(), pp.getLevel(), equipId, equiplel);
		
		sendLog(log);
		
	}
	/**
	 * 技能替换log
	 * @param player
	 * @param ninjaId 替换的忍者id
	 * @param ninjutsuID1  替换的忍术id
	 * @param ninjutsuID2  被替换的忍术id
	 */
	public static void doChangeLog(Player player, int ninjaId,
			int ninjutsuID1,int ninjutsuID2){
		if (!isOpen || (logFilter && filterList.contains("change"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdChange log = new HdChange(pp.getLogNet(), pp.getUserId(), 
				player.getId(), pp.getLevel(), ninjaId, ninjutsuID1,ninjutsuID2);
		sendLog(log);
		
	}

	/**
	 * 专精点获取
	 * @param player
	 * @param gettype
	 * @param ninjutsuId
	 */
	public static void doPointLog(Player player, int gettype,
			int ninjutsuId){
		if (!isOpen || (logFilter && filterList.contains("point"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		HdPoint log = new HdPoint(pp.getLogNet(), pp.getUserId(), 
				player.getId(), pp.getLevel(), gettype, ninjutsuId);
		
		sendLog(log);
		
	}
	
	/**
	 * 忍者获取log
	 * @param player
	 * @param gettype （）
	 * @param ninjutsuId
	 */
	public static void doNinjaLog(Player player, int gettype,
			int ninjutsuId){
		if (!isOpen || (logFilter && filterList.contains("ninja"))) {
			return;
		}
		PlayerProperty pp = player.getProperty();
		if(gettype!=LogConst.RAFFLE_HERO)
		{
			HdNinja log = new HdNinja(pp.getLogNet(), pp.getUserId(), 
					player.getId(), pp.getLevel(), gettype, ninjutsuId);
			
			sendLog(log);
		}
		
	}
	
	
	
	

}
