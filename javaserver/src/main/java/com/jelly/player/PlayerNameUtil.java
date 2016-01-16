package com.jelly.player;

import com.dol.cdf.common.KeyWordsFilter;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.RefuseWordsFilter;
import com.dol.cdf.common.StringHelper;
import com.jelly.node.cache.AllPlayersCache;

public final class PlayerNameUtil {
	
	public static int validateName(String displayName) {
		// 判断名称长度
		int minlengh = 2;
		int maxLengh = 12;
		int nameLength = StringHelper.asciiLength(displayName);
		if(nameLength < minlengh || nameLength > maxLengh){
			return MessageCode.INVALID_NAME_LENGTH;
		}
		// 判断用户名是否包含非法字符
		if (!StringHelper.containsNone(displayName, " ~`!^?<>/=\"\'\\\n\r\t\f\b%,.(){}[]+*~&|;#$:-　")) {
			return MessageCode.INVALID_NAME;
		}
		
		if(RefuseWordsFilter.getInstance().contain(displayName)){
			return MessageCode.INVALID_NAME;
		}
		
		return MessageCode.OK;
	}
	
	public static int validatePlayerName(AllPlayersCache allPlayersCache, String displayName) {
		int validateRet = MessageCode.OK;
		int minlengh = 2;
		int maxLengh = 12;
		int nameLength = StringHelper.asciiLength(displayName);
		if(nameLength < minlengh || nameLength > maxLengh){
			validateRet = MessageCode.INVALID_NAME_LENGTH;
		}
		// 判断用户名是否包含非法字符
		if (!StringHelper.containsNone(displayName, " ~`!^?<>/=\"\'%,.(){}[]+*~&|;#$:-　")) {
			return MessageCode.INVALID_NAME;
		}
		
		if(RefuseWordsFilter.getInstance().contain(displayName)){
			return MessageCode.INVALID_NAME;
		}
		if (validateRet != MessageCode.OK) {
			return validateRet;
		}
		if (allPlayersCache.hasThisName(displayName)) {
			validateRet = MessageCode.DUPLICATE_NAME;
		}
		
		return validateRet;
		
	}
}
