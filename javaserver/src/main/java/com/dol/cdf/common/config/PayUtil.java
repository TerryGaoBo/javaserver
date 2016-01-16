package com.dol.cdf.common.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PayUtil.class);
	
	/**
	 * 客户端传来的订单
	 * 充值回调传来的订单和此列表对比，如果一致，从此列表中删除。
	 * 一直都没被删除的订单为无效订单，停服时保存到日志中，以备查询。 
	 */
	private Map<String, List<String>> orderList = new ConcurrentHashMap<String, List<String>>();
	
	private static PayUtil instance = null;
	
	public static PayUtil getInstance() {
		if (instance == null) {
			synchronized (PayUtil.class) {
				if (instance == null) {
					instance = new PayUtil();
				}
			}

		}
		return instance;
	}
	
	/**
	 * 保存客户端传来的订单
	 * @param orderId - 订单id
	 * @param info - [itemId, guid, name]
	 */
	public void saveOrder(String orderId, List<String> info) {
		orderList.put(orderId, info);
	}
	
	/**
	 * 充值回调传回的订单
	 * 如果orderList存在此订单，说明客户端传来的订单是有效的
	 * 把有效订单删除，则orderList剩余订单都是无效的
	 * @param orderId
	 */
	public boolean verifyOrder(String orderId) {
		boolean result = false;
		Iterator<String> it = orderList.keySet().iterator();
		 while (it.hasNext()) {
		        String tmp = it.next();
		        if (tmp.equals(orderId)) {
		        	it.remove();
		        	result = true;
		        }
		    }
		 logger.info("未验证的订单数量：" + orderList.size());
		 return result;
	}
	
	public void dumpOrderList() {
		for (String key : orderList.keySet()) {
			List<String> list = orderList.get(key);
			logger.info("无效订单 orderId={}, itemId={}, guid={}, name={}", key, list.get(0), list.get(1), list.get(2));
		}
	}
}
