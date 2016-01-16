package com.dol.cdf.log.msg;

import io.nadron.app.Player;

import com.dol.cdf.common.ContextConfig;

public class BaseLogMessage {
	public int log_type;
	public long log_time; // 日志时间
	public String region_id; // 服务器区ID
	public String host_id; // 服务器组ID
	public String server_id; // 服务器ID
	public String account_id;
	public String account_name;
	public String char_id;
	public String char_name;
	public int level;
	public int reason;
	public String param;


	public BaseLogMessage() {
		this.log_time = System.currentTimeMillis();
		this.region_id = ContextConfig.REGION_ID;
		this.server_id = ContextConfig.SERVER_ID;
		
	}
	public BaseLogMessage(Player player) {
		this.log_time = System.currentTimeMillis();
		this.region_id = ContextConfig.REGION_ID;
		this.server_id = ContextConfig.SERVER_ID;
		this.account_id = player.getProperty().getUserId();
		this.char_id = player.getId().toString();
		this.char_name =player.getRole() == null? "" : player.getRole().getName();
	}
	
	public int getLog_type() {
		return log_type;
	}

	public void setLog_type(int logType) {
		log_type = logType;
	}

	public long getLog_time() {
		return log_time;
	}

	public void setLog_time(long logTime) {
		log_time = logTime;
	}

	public String getHost_id() {
		return host_id;
	}

	public void setHost_id(String hostId) {
		host_id = hostId;
	}
	
	public String getRegion_id() {
		return region_id;
	}

	public void setRegion_id(String regionId) {
		region_id = regionId;
	}

	public String getServer_id() {
		return server_id;
	}

	public void setServer_id(String serverId) {
		server_id = serverId;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String accountId) {
		account_id = accountId;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String accountName) {
		account_name = accountName;
	}

	public String getChar_id() {
		return char_id;
	}

	public void setChar_id(String charId) {
		char_id = charId;
	}

	public String getChar_name() {
		return char_name;
	}

	public void setChar_name(String charName) {
		char_name = charName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getReason() {
		return reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	@Override
	public String toString() {
		return "" ;
	}

}
