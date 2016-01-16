//package com.dol.cdf.log;
//
//import java.nio.ByteBuffer;
//
//import mojasi.HashedByteWord;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.log4j.Logger;
//
//import com.play.collections.KeyValuePair;
//import com.play.crypto.Guid;
//import com.play.http.HttpUtils;
//import com.play.net.WriteIntf;
//import com.play.service.GenericService;
//import com.play.service.http.HttpCallback;
//import com.play.service.http.HttpHeaderInfo;
//import com.play.service.http.HttpService;
//import com.play.service.http.IsHttpService;
//import com.play.wizards.dialogs.PlayActionPicker;
//import com.yp.ballistic.platform.GameInterface;
//
//@IsHttpService
//public class LogManager extends GenericService {
//
//	public static Logger logger = Logger.getLogger(LogManager.class);
//	
//	public LogManager(WriteIntf sock, Guid session) {
//		super(sock, session);
//	}
//
//	@HttpService(path = "GET/ballistic/log.do")
//	public HttpCallback aindex = new HttpCallback() {
//		public void run(HttpHeaderInfo info) throws Exception {
//			logger.debug("serving log.do");
//			KeyValuePair lid = info.getValue(new HashedByteWord("lid="));
//			KeyValuePair account = info.getValue(new HashedByteWord("account="));
//			KeyValuePair roleId = info.getValue(new HashedByteWord("roleId="));
//			KeyValuePair roleName = info.getValue(new HashedByteWord("roleName="));
//			KeyValuePair para = info.getValue(new HashedByteWord("para="));
//			KeyValuePair sign = info.getValue(new HashedByteWord("sign="));
//			logger.debug(lid + "|" + account + "|" + roleId + "|" + roleName + "|" + para);
//			if (lid == null) {
//				logger.error("==> log.do mising lid");
//				sendHttpReply(info, "mising args ");
//				return ;
//			}
//			String accountId = "";
//			String charId = "";
//			String charName = "";
//			if (account != null) 
//				accountId = account.getValue();
//			if (roleId != null) 
//				charId = roleId.getValue();
//			if (roleName != null) 
//				charName = roleName.getValue();
//			
//			if (lid.getIntValue() == 999) {
//				if (sign == null) {
//					logger.error("==> log.do mising sign");
//					sendHttpReply(info, "mising args ");
//					return ;
//				}
//				String code = account.getValue() + GameInterface.KEY;
//				String mySign = DigestUtils.md5Hex(code);
//				if (!mySign.equals(sign.getValue())) {
//					logger.error("==> log.do sign error  sign=" + sign.getValue() + " mySign=" + mySign + " code=" + code);
//					sendHttpReply(info, "sign error");
//					return;
//				}
//			}
//			LogUtil.doLoadingLog(accountId, charId, charName, lid.getIntValue());
//			sendHttpReply(info, "succ");
//		}
//	};
//
//	
//	public static void sendHttpReply(HttpHeaderInfo info, String result) throws Exception {
//		byte[] bytes = result.toString().getBytes("utf8");
//		ByteBuffer buf = java.nio.ByteBuffer.allocate(bytes.length + 1024);
//		HttpUtils.makeHTTPReply(buf, "200 OK", bytes, null);
//		info.sock.write(buf);
//	}
//	
//	
//	public static void main(String[] args) {
//		PlayActionPicker.start(LogManager.class);
//	}
//}
