package com.dol.cdf.common.entities;

import java.util.List;
import java.util.Set;
import java.util.Map;

import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.crypto.Guid;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jelly.node.cache.ObjectCacheService;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.dol.cdf.common.DynamicJsonProperty;

import io.nadron.app.Player;

public class GlobalProps extends Entity{
	
	public static Guid theGuid = Guid.SHA1("ictWIr7/+h6q/3GYEsM3GMyABWoServerGlobalProps");
	
	private ObjectCacheService objectCache;
	
	/**
	 * 停服补偿的奖励
	 */
	@JsonProperty("ms")
	private List<MailGiveProps> mails = Lists.newArrayList();
	
	/**
	 * 系统公告
	 */
	@JsonProperty("ns")
	private NoticeProps noticeProps;
	
	/**
	 * 被封号的玩家guid
	 */
	@JsonProperty("bp")
	private Set<String> banPlayers = Sets.newHashSet();
	
	@JsonProperty("shieldMap")
	private Map<String, List<Map<String, Integer>>> shieldMap = Maps.newHashMap();
	
	private static final int maxMailNumber = 10;
	
	public GlobalProps() {
		super(theGuid.toString());
	}

	public void addBanPlayer(String guid){
		banPlayers.add(guid);
		objectCache.putObject(this);
	}
	
	public boolean isBanPlayer(String guid) {
		return banPlayers.contains(guid);
	}
	
	public boolean unBanPlayer(String guid) {
		boolean isRemove =  banPlayers.remove(guid);
		objectCache.putObject(this);
		return isRemove;
	}
	
	public Set<String> getBanPlayers() {
		return banPlayers;
	}

	public void addMail(String title, String content, String reward ) {
		if(mails.size() >= maxMailNumber) {
			mails.remove(0);
		}
		MailGiveProps instance = new MailGiveProps();
		instance.setContent(content);
		instance.setTitle(title);
		instance.setReward(reward);
		instance.setSendTime(TimeUtil.getCurrentTime());
		mails.add(instance);
		objectCache.putObject(this);
	}
	
	public void setObjectCache(ObjectCacheService objectCache) {
		this.objectCache = objectCache;
	}
	
	public List<MailGiveProps> getMails() {
		return mails;
	}
	public void addNotice(String title, String content) {
		noticeProps = new NoticeProps();
		noticeProps.setContent(content);
		noticeProps.setTitle(title);
		objectCache.putObject(this);
	}
	
	public NoticeProps getNoticeProps() {
		return noticeProps;
	}
	
	public void setNoticeProps(NoticeProps noticeProps) {
		this.noticeProps = noticeProps;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *  <u>屏蔽支付平台，根据渠道号屏蔽支付方式</u>
	 *  <li>channelID : 渠道id</li>
	 *  <li>payType : 需要屏蔽的支付类型 （ 1 google play , 2 ios pay ,3 信用卡 ）  需要追加新的，以此在往后追加</li>
	 *  <li>isShield : 开启和屏蔽 (0 屏蔽，1 开启)</li>
	 */
	public synchronized void shieldPayFunc(String channelID,Integer payType,Integer isShield )
	{
		List<Map<String, Integer>> list = shieldMap.get(channelID);

		if(list == null){
			list = Lists.newArrayList();
			Map<String, Integer> map = Maps.newHashMap(); 
			map.put("payType", payType);
			map.put("isShield", isShield);
			list.add(map);
			shieldMap.put(channelID, list);
		}else{
			boolean s = false;
			for (Map<String, Integer> m : list) {
				Integer t = m.get("payType");
				if(t.intValue() == payType.intValue()){
					m.put("isShield", isShield);
					s = true;
					break;
				}
			}
			if(!s){
				Map<String, Integer> map = Maps.newHashMap(); 
				map.put("payType", payType);
				map.put("isShield", isShield);
				list.add(map);
			}
		}

		objectCache.putObject(this);
	}

	//{"isHaveShield":true,"info":[{"payType":1,"isShield":0}]}
	//{"isHaveShield":false};
	public synchronized void sendS2CPayMessageFunc(String channelID,Player player)
	{
		ObjectNode info = DynamicJsonProperty.jackson.createObjectNode();

		boolean isHaveShield = false;

		List<Map<String, Integer>> list = shieldMap.get(channelID);
		if(list != null){
			isHaveShield = true;
		}

		info.put("isHaveShield", isHaveShield);

		if(isHaveShield){
			ArrayNode f = DynamicJsonProperty.jackson.createArrayNode();
			for (Map<String, Integer> m : list) {
				ObjectNode fo = DynamicJsonProperty.jackson.createObjectNode();
				Integer p = m.get("payType");
				Integer d = m.get("isShield");

				fo.put("channelID", channelID);
				fo.put("payType", p.intValue());
				fo.put("isShield", d.intValue());
				f.add(fo);
			}
			info.put("info",f);
		}

		player.sendMessage("payPlatformInfo", info);
	}
}
