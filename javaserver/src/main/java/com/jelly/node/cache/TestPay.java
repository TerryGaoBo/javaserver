package com.jelly.node.cache;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.codec.digest.DigestUtils;

public class TestPay {
	
	public static boolean localTest = true;	//本地测试

	public static void main(String[] args) {
		int sid = 1;	//服务器id
		int channel = 2;//渠道号 2-赫德 3-海外
		String name = "abcde";	//角色名
		
		for (int i=1; i<=10; i++) {
			String itemId = "hy.gold" + i;
			if (channel == 3) {
				itemId = "com.xxrz.rg.en.hy.gold" + i;
			} 
			TestPay.createUrl(sid, channel, name, itemId);
		}
	}
	
	/**
	 * 生成测试充值链接
	 * @param sid 服务器id
	 * @param channel 渠道号
	 * @param roleName 角色名
	 * @param itemId 内购道具id
	 */
	public static void createUrl(int sid, int channel, String roleName, String itemId) {
		try {
			String orderId = "TEST_ORDER_001";
			String code = orderId + roleName + itemId + "Iw!L0Kb51226";
			String sign = DigestUtils.md5Hex(code);
					
			int port = 8082;
			if (channel == 3) {
				port = 9001;
			}
			if (sid > 1) {
				port = 9000+sid;
			}
			String url ="";
			if (channel == 1) {
				url = "http://s" + sid +".rzhd.dreamjelly.com:" + port + "/pay/payCallback?orderId=" + orderId + "&itemId=" + itemId + "&roleName=" + URLEncoder.encode(roleName, "utf-8") + "&sign=" + sign;
			} else if (channel == 2) {
				url = "http://s" + sid +".rzhd.dreamjelly.com:" + port + "/pay/payCallback?orderId=" + orderId + "&itemId=" + itemId + "&roleName=" + URLEncoder.encode(roleName, "utf-8") + "&sign=" + sign;
			} else if (channel == 3) { //LYTO
				url = "http://54.179.175.110:8082/pay/payCallback?orderId=" + orderId + "&itemId=" + itemId + "&roleName=" + URLEncoder.encode(roleName, "utf-8") + "&sign=" + sign;
			}
			
			if (localTest) { //强制生成本地测试链接
				url = "http://localhost:"+port+"/pay/payCallback?orderId=" + orderId + "&itemId=" + itemId + "&roleName=" + URLEncoder.encode(roleName, "utf-8") + "&sign=" + sign;
			}
			System.out.println(url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ;
	}

}
