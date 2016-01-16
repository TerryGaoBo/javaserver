package com.dol.cdf.log.msghd;

import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.log.LogUtil;

public class HdBaseLog {
	
	public long time; //日志时间
	
	public String version; //游戏版本
	
	public String type; //日志类型
	
	public String appkey; //游戏标示
	
	public String channelId;//渠道id
	
	public HdBaseLog() {
		this.time = System.currentTimeMillis();
		this.version = AllGameConfig.getInstance().maxVersion;
	}

	public String getTime() {
		return TimeUtil.formatDateLongWithSec(time);
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAppkey() {
		return LogUtil.getAppKey(channelId);
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getChannelId() {
		int channelId = 0;
		 try {
			 channelId = Integer.parseInt(this.channelId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		 return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Override
	public String toString() {
		return "";
	}
	
	

}
