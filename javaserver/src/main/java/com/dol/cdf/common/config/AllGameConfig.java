/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.dol.cdf.common.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.constant.GameConstManager;
import com.dol.cdf.common.gamefunction.GameFunctionConfigManager;

public class AllGameConfig {

	private static final Logger logger = LoggerFactory.getLogger(AllGameConfig.class);

	public static final String ConfigurationPath = "../resource/json/";
	public static final String ImagePath = "../JS/HY/Resources/image/";

	static private AllGameConfig instance = null;

	public boolean inited = false;

	public String version = "";

	public GameFunctionConfigManager gfcm;

	public GameConstManager gconst;

	public BeastConfigManager beast;
	
	public SkillConfigManager skills;
	
	public CharacterManager characterManager;
	
	public ItemConfigManager items;
	
	public DropGroupConfigManager drops;
	
	public AdventureConfigManager adventures;

	public LevelConfigManager levels;

	public QualityRateConfigManager rate;

	public QualityRefConfigManager qref;

	public BuildingConfigManager buildings;

	public ArenaConfigManager arena;

	public FormulaConfigManager formula;
	
	public QuestConfigManager quests;

	public RecruitConfigManager recruits;

	public ExamConfigManager exams;
	
	public ActivityConfigManager activitys;
	
	public GiftConfigManager gifts;
	
	public TextConfigManager text;
	
	public NpcTalkConfigManager talks;
	
	public WarConfigManager wars;
	
	public CardConfigManager cards;
	
	public TeamConfigManager teams;
	
	public Map<String, String> versionMap = new HashMap<String, String>();
	
	public String maxVersion = "1.0.0";

	public static String getConfigResource(String fileName) {
		return ConfigurationPath + fileName;
	}

	public RuntimeEnv env = RuntimeEnv.OTHER;

	public static AllGameConfig getInstance() {
		if (instance == null) {
			synchronized (AllGameConfig.class) {
				if (instance == null) {
					instance = new AllGameConfig();
					if (!instance.inited)
						instance.init();
				}
			}

		}
		return instance;
	}

	synchronized void clear() {
		inited = false;
	}

	private void init() {

		if (inited)
			return;
		logger.info("re-initing config files");
		initRuntimeEnv();
		inited = true;
		initConfigLoaderManager();
		initVersions();
	}

	private void initRuntimeEnv() {
		String runtime_env = System.getProperty("runtime.env", "other");
		String[] serverInfo = runtime_env.split("-");
		env = RuntimeEnv.getRuntimeEnv(serverInfo[0]);
		ContextConfig.START_DATE = System.getProperty("app.startDate", ContextConfig.START_DATE);
		if (serverInfo.length > 1) {
			ContextConfig.SERVER_ID = serverInfo[1];
		}
		if (ContextConfig.isOfficalEnv()) {
			ContextConfig.VERSION_CHECK = true; 
		}
		logger.info("runtime env:{}, server start date:{}, serverId:{}", runtime_env, ContextConfig.START_DATE,ContextConfig.SERVER_ID);
	}

	private void initConfigLoaderManager() {
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			try {
				if (field.getType().getSuperclass() == BaseConfigLoadManager.class) {
					IConfigLoadManager configLoadMaanger = ((IConfigLoadManager) (field.getType().newInstance()));
					FieldUtils.writeField(field, this, configLoadMaanger);
					configLoadMaanger.loadConfig();
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
	
	public void initVersions() {
		versionMap.clear();
		InputStream in = ClassLoader.getSystemResourceAsStream(
				AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE ? "nadron/version_ios.txt" : "nadron/version.txt");
		InputStreamReader ir = new InputStreamReader(in);
		BufferedReader br= new BufferedReader(ir);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] v = line.split(" ");
				String key = v[1];
				key = key.substring(0, key.length()-4);
				versionMap.put(key, v[0]);
				maxVersion = key.split("_")[1];
			}
			logger.info("version check :{}，server maxVersion：{}", ContextConfig.VERSION_CHECK,maxVersion);
		} catch (Exception e) {
			logger.error("init version fail！！", e);
			e.printStackTrace();
		}
	}

}
