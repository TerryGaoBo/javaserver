package com.dol.cdf.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dol.cdf.common.config.AllGameConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ContextConfig {

	public static String REGION_ID = "quyou";
	public static String HOST_ID = "quyou";
	public static String SERVER_ID = "1.172";
	public static Integer SERVER_PORT_BASE = 6000;
	public static Integer WEB_PORT_BASE = 9000;
	public static String SYS_KEY = "Sysx7waxn1HoJG3DEhM0"; // 系统内调的key
	public static String KICK_KEY = "kickxLzUCu63avq3Zoss"; // 玩家踢下线的key
	public static String GM_REWARD_KEY = "gmRewardsxLzUCavq3Zou61"; // gm补偿的key
	public static String Game_OPEN_KEY = "cancalIpkei930idj"; // 取消IP检查key
	public static String ADD_IP_KEY = "AddIp930idjz1"; //
	public static String LOGIN_KEY = ""; // 登陆等接口密钥
	public static String PAYMENT_KEY = ""; // 充值密钥

	public static String START_DATE;

	/********* 全局的值 *********/
	// 温泉中使用高级修炼的玩家名称
	public static String SENIOR_PRATISE_PLAYER_NAME;
	// 温泉中使用高级修炼的结束时间
	public static Integer SENIOR_PRATISE_END_TIME;
	// 固定时间给予体力奖励是否开启
	public static boolean GIVE_ENERGY_FIX_TIME_OPEN = false;

	/********* 系统开关 *********/

	// 游戏开关
	public static boolean GAME_OPEN = false;

	// debug log 开关
	public static boolean DEBUG_LOG_OPEN = false;

	// 防沉迷开关
	public static boolean INDULGE_OPEN = false;

	// 是否开启gmTool
	public static boolean GM_TOOL_OPEN = true;

	// 是否打开客户端与服务器通讯的消息
	public static boolean MESSAGE_OPEN = false;

	// 是否检测版本信息
	public static boolean VERSION_CHECK = true;

	// 是否开启新手卡
	public static boolean NEWER_CARD_OPEN = true;

	//  是否军团展开开启
	public static boolean TEAMS_WAR_OPEN = false; // 测试

	// 赫德首充第二次重置
	// public static int FIRST_PAY_RESET = 0;

	public static Set<String> ipList = new HashSet<String>(); // 测试

	static {
		initIp();
	}

	public static void addIp(String ip) {
		ipList.add(ip);
	}

	public static void rmIp(String ip) {
		if (ipList.contains(ip)) {
			ipList.remove(ip);
		}
	}

	public static boolean checkIp(String requestIp) {
		for (String ip : ipList) {
			if (requestIp.equals(ip)) {
				return true;
			}
		}
		return false;
	}

	/********* 系统参数数值 *********/
	// 最大在线人数
	public static int MAX_ONLINE_PLAYER = 10000;

	// 清理天梯积分,每一百条sleep多长时间，单位为毫秒
	public static int CLEAR_RANK_SLEEP_MILLIS = 3 * 1000;

	// 消息处理时间的阀值
	public static long LONG_MESSAGE_PROCESS_TIME = 10;

	// 是否显示充值排名
	public static boolean SHOW_EXCHANGE_ORDER = false;

	/**
	 * 是否为合服后的服务器
	 * 
	 * @return
	 */
	public static boolean isJointServer() {
		return SERVER_ID.split("\\.").length > 1;
	}

	public static int getFirstServerId() {
		return Integer.parseInt(SERVER_ID.split("\\.")[0]);
	}

	public static List<Integer> getServerIds() {
		String[] sidStr = SERVER_ID.split("\\.");
		int minId = Integer.parseInt(sidStr[0]);
		int maxId = Integer.parseInt(sidStr[1]);
		List<Integer> sids = Lists.newArrayList();
		for (int i = minId; i <= maxId; i++) {
			sids.add(Integer.parseInt(sidStr[i]));
		}
		return sids;
	}

	/**
	 * 获取服务器端口
	 * 
	 * @return
	 */
	public static int getServerPort() {
		RuntimeEnv env = AllGameConfig.getInstance().env;
		int firstServerId = getFirstServerId();
		if (env == RuntimeEnv.STAGE) {
			if (firstServerId == 1) {
				return 80;
			}
		}
		return SERVER_PORT_BASE + firstServerId;
	}

	/**
	 * 获取web端口
	 * 
	 * @return
	 */
	public static int getWebPort() {
		int firstServerId = getFirstServerId();
		if (firstServerId == 1) {
			return 8082;
		}
		return WEB_PORT_BASE + firstServerId;
	}

	/**
	 * 验证sid
	 * 
	 * @param sid
	 * @return
	 */
	public static boolean checkSeverId(String sid) {
		sid = sid.trim();
		if (ContextConfig.isJointServer()) {
			String[] sids = SERVER_ID.split("\\.");
			int minId = Integer.parseInt(sids[0]);
			int maxId = Integer.parseInt(sids[1]);
			int serverId = Integer.parseInt(sid);
			if (serverId >= minId && serverId <= maxId) {
				return true;
			}
		} else {
			return SERVER_ID.equals(sid);
		}
		return false;
	}

	public static boolean isShiningPratise() {
		if (SENIOR_PRATISE_END_TIME != null
				&& TimeUtil.getCurrentTime() < SENIOR_PRATISE_END_TIME) {
			return true;
		}
		return false;

	}

	public static void shinePratise(String name) {
		SENIOR_PRATISE_PLAYER_NAME = name;
		SENIOR_PRATISE_END_TIME = TimeUtil.getCurrentTime();
	}

	public enum RuntimeEnv {
		OTHER("other"),
		// 封闭测试
		TEST("t"),
		// 赫德正式服
		STAGE("s"),
		// 海外版本
		OVERSEAS("o"),
		// 赫德IOS版本
		IOS_STAGE("i");
		private final String name;

		public String getName() {
			return name;
		}

		private RuntimeEnv(String name) {
			this.name = name;
		}

		public static RuntimeEnv getRuntimeEnv(String name) {
			for (RuntimeEnv env : RuntimeEnv.values()) {
				if (env.name.equals(name)) {
					return env;
				}
			}
			return RuntimeEnv.OTHER;
		}

	}

	public static boolean isOfficalEnv() {
		RuntimeEnv env = AllGameConfig.getInstance().env;
		return env == RuntimeEnv.STAGE || env == RuntimeEnv.OVERSEAS
				|| env == RuntimeEnv.TEST || env == RuntimeEnv.IOS_STAGE;
	}

	public static void loadScript() {
		Map<String, Object> context = Maps.newHashMap();
		File globalJs = new File(AllGameConfig.getConfigResource("config.js"));
		if (!globalJs.exists()) {
			System.out.println("globalJs not exists");
		}
		try {
			JsScriptHelper.executeScriptFile(globalJs.getPath(), context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void initIp() {
		ipList.clear();
		;
		InputStream in = ClassLoader.getSystemResourceAsStream("nadron/ip.txt");
		InputStreamReader ir = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(ir);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				ipList.add(line);
			}
		} catch (Exception e) {
			System.out.println("初始化IP失败！！");
			e.printStackTrace();
		}
		System.out.println(ipList.toString());
	}

}
