package com.dol.cdf.indulge;

import io.nadron.app.Player;

import org.apache.log4j.Logger;

import com.dol.cdf.common.ContextConfig;

public class IndulgeUtil {
	public static Logger logger = Logger.getLogger(IndulgeUtil.class);
	
	public static float getIndulgePoint(Player player){
		if(ContextConfig.INDULGE_OPEN){
//			long currentTime = System.currentTimeMillis();
//			long onlineTime = currentTime - player.getProperty().getLastOnlineTime();
//			long totalDuration = onlineTime + player.property.getDuration();
//			if(totalDuration >= TimeUnit.HOURS.toMillis(5)) {
//				// lobby.sendSystemMiddleMessage("您已不健康游戏,收益为零 ");
//				return 0;
//			}
//			if(totalDuration >= TimeUnit.HOURS.toMillis(3)) {
//				// lobby.sendSystemMiddleMessage("您已疲劳游戏,收益为50%");
//				return 0.5f;
//			}
		}
		return 1;
	}
}
