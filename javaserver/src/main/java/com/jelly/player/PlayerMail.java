package com.jelly.player;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.OperResultType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.RefuseWordsFilter;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.ItemGroupEnum;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.log.LogConst;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PlayerMail extends DynamicJsonProperty {

	private static final Logger logger = LoggerFactory.getLogger(PlayerMail.class);
	public static final int TAB_SYS = 1;
	public static final int TAB_PRIVATE = 2;
	public static final int TAB_GM = 3;
	
	public static final int MAIL_NEW = 1;
	public static final int MAIL_READED = 2;
	public static final int MAIL_USED = 3;

//	public static final ImmutableMap<Integer, Integer> MAIL_MAX = ImmutableMap.of(TAB_SYS, 50, TAB_PRIVATE, 30, TAB_GM, 30);
	public static final ImmutableMap<Integer, Integer> MAIL_MAX = ImmutableMap.of(TAB_SYS, 50, TAB_PRIVATE, 30);

	@JsonProperty("sm")
	private List<MailInstance> sysMail;
	
	private HashMap<Integer, MailInstance> sysMailMap;

	@JsonProperty("prm")
	private List<MailInstance> privateMail;
	
	private HashMap<Integer, MailInstance> privateMailMap;

	@JsonProperty("gm")
	private List<MailInstance> gmMail;
	
	private HashMap<Integer, MailInstance> gmMailMap;

	public List<MailInstance> getMailByTab(int tab) {
		if (TAB_SYS == tab) {
			if (sysMail == null) {
				sysMail = Lists.newArrayList();
			}
			return sysMail;
		} else if (TAB_PRIVATE == tab) {
			if (privateMail == null) {
				privateMail = Lists.newArrayList();
			}
			return privateMail;
		} else if (TAB_GM == tab) {
			if (gmMail == null) {
				gmMail = Lists.newArrayList();
			}
			return gmMail;
		}
		return Collections.emptyList();
	}
	
	public Map<Integer, MailInstance> getMailMapByTab(int tab) {
		Map<Integer, MailInstance> map = null;
		List<MailInstance> list = null;
		if (TAB_SYS == tab) {
			if (sysMailMap == null) {
				sysMailMap = Maps.newHashMap();
				list = sysMail;
				map = sysMailMap;
			}else{
				map = sysMailMap;
			}
		} else if (TAB_PRIVATE == tab) {
			if (privateMailMap == null) {
				privateMailMap = Maps.newHashMap();
				list = privateMail;
				map = privateMailMap;
			}else{
				map = privateMailMap;
			}
		} else if (TAB_GM == tab) {
			if (gmMailMap == null) {
				gmMailMap = Maps.newHashMap();
				list = gmMail;
				map = gmMailMap;
			}else{
				map = gmMailMap;
			}
		}
		if(list!=null){
			for(int i=0; i<list.size(); i++){
				MailInstance mi = list.get(i);
				map.put(mi.getId(), mi);
			}
		}
		
		if(map != null){
			return map;
		}
		return Collections.emptyMap();
	}

	private void putMail(int tab, MailInstance instance) {
		List<MailInstance> mails = getMailByTab(tab);
		Map<Integer, MailInstance> mailmaps =  getMailMapByTab(tab);
		if (mails.size() >= MAIL_MAX.get(tab)) {
			MailInstance mi = mails.remove(0);
			mailmaps.remove(mi.getId());
		}
		mails.add(instance);
		mailmaps.put(instance.getId(), instance);
		appendChange(tab, instance.toJson());
		addChange("newMail", 1);
	}

	public void removeMail(Player player, int tab, int id) {
		List<MailInstance> mails = getMailByTab(tab);
		Map<Integer, MailInstance> mailmaps =  getMailMapByTab(tab);
		
		MailInstance mi = mailmaps.remove(id);
		if(mi != null){
			mails.remove(mi);
			player.sendResult(OperResultType.MAIL,MessageCode.OK,0);
		}else{
			player.sendResult(OperResultType.MAIL,MessageCode.FAIL);
		}
			
	}
	
	public void rewardMail(Player player, int tab, int id) {
		Map<Integer, MailInstance> mails = getMailMapByTab(tab);
		MailInstance mailInstance = mails.get(id);
		if(mailInstance.getReward().isEmpty() || mailInstance.getStatus() == MAIL_USED) {
			logger.error("rewardMail error :  {}",mailInstance.toJson());
			player.sendResult(OperResultType.MAIL,MessageCode.FAIL);
			return;
		}
		List<VariousItemEntry> rewards = VariousItemUtil.mapToVariousItem(mailInstance.getReward());
		int code = VariousItemUtil.checkBonus(player, rewards, true);
		if (code != MessageCode.OK) {
			player.sendResult(OperResultType.MAIL,code);
			return;
		}
		mailInstance.setStatus(MAIL_USED);
		VariousItemUtil.doBonus(player, rewards, LogConst.GET_FROM_MAIL, true);
		addChange(tab, mailInstance.toJson());
		player.sendResult(OperResultType.MAIL,VariousItemUtil.itemToJson(rewards));
		if (mailInstance.getHead() != null && mailInstance.getHead().indexOf("GM") >= 0) {
			logger.info(player.getName() + " 已领取GM奖励 " + mailInstance.getReward());
		}
		for(VariousItemEntry entry : rewards) {
			if (entry.getGroup() == ItemGroupEnum.hero) {
				LogUtil.doGetNinjaLog((DefaultPlayer)player, Integer.parseInt(entry.getType()), 1, LogConst.GET_FROM_MAIL);
			}
		}
	}
	
	public void readMail(int tab, int id) {
		Map<Integer, MailInstance> mails = getMailMapByTab(tab);
		
		MailInstance mailInstance = mails.get(id);
		if(mailInstance == null) {
			return;
		}
		if (mailInstance.getStatus() == MAIL_NEW) {
			mailInstance.setStatus(MAIL_READED);
		}
		addChange(tab, mailInstance.toJson());
	}

	public void addSysMail(int hid, int tid) {
		MailInstance mi = new MailInstance( calcNextMailId(TAB_SYS) );
		mi.setHid(hid);
		mi.setTid(tid);
		mi.setTime(TimeUtil.getCurrentTime());
		putMail(TAB_SYS, mi);
	}
	
	public void addSysMail(int hid, int tid, String... textParam) {
		MailInstance mi = createMail(TAB_SYS, hid, tid, textParam);
		putMail(TAB_SYS, mi);
	}
	
	/**
	 * 系统道具未入包，放到邮件中 //来自哪里,是根据奖励类型来的
	 * @param itemId
	 * @param count
	 */
	public void addSysItemMail(int itemId, int count,int headId) {
		addSysItemMail(""+itemId, count, headId, headId);
	}
	
	public void addSysItemMail(String type, int count,int headId, int tid, String... textParam) {
		MailInstance mi = createMail(TAB_SYS,headId, tid, textParam);
		mi.addReward(type, count);
		putMail(TAB_SYS, mi);
	}
	public void addSysItemMail(VariousItemEntry[] items,int headId, int tid, String... textParam) {
		MailInstance mi = createMail(TAB_SYS,headId, tid, textParam);
		mi.addRewards(items);
		putMail(TAB_SYS, mi);
	}
	
	public void addSysItemMail(String reward,String head, String text) {
		MailInstance mi = createTextMail(TAB_SYS, head, text);
		mi.addRewards(VariousItemUtil.parse1(reward));
		putMail(TAB_SYS, mi);
	}

	public MailInstance createMail(int tab, int headId, int tid, String... textParam) {
		MailInstance mi = new MailInstance(calcNextMailId(tab));
		mi.setHid(headId);
		mi.setTid(tid);
		if(textParam.length > 0) {
			mi.setText(StringUtils.join(textParam, "|"));
		}
		mi.setTime(TimeUtil.getCurrentTime());
		return mi;
	}

	public void addPriveteMail(String head, String text) {
		text = RefuseWordsFilter.getInstance().filt(text);
		text = EmojiFilter.filterEmoji(text);
		MailInstance mi = createTextMail(TAB_PRIVATE, head, text);
		putMail(TAB_PRIVATE, mi);
	}

	public MailInstance createTextMail(int tab, String head, String text) {
		MailInstance mi = new MailInstance(calcNextMailId(tab));
		mi.setHead(head);
		mi.setText(text);
		mi.setTime(TimeUtil.getCurrentTime());
		return mi;
	}

	public void addGMMail(String head, String text) {
		MailInstance mi = createTextMail(TAB_GM, head, text);
		putMail(TAB_GM, mi);
	}
	
	public int calcNextMailId(int tab){
		List<MailInstance> list =  this.getMailByTab(tab);
		if(list.size() == 0){
			return 1;
		}
		int maxIdx = list.size() -1;
		Integer id = list.get(maxIdx).getId();
		if(id == null) {
			compatOldMailData();
		}
		return  list.get(maxIdx).getId() + 1;
	}

	@Override
	public String getKey() {
		return "mails";
	}

	@Override
	public JsonNode toWholeJson() {
		compatOldMailData();
		
		ObjectNode obj = jackson.createObjectNode();
		for (Integer key : MAIL_MAX.keySet()) {
			List<MailInstance> mailByTab = getMailByTab(key);
			ArrayNode array = jackson.createArrayNode();
			for (MailInstance m : mailByTab) {
				array.add(m.toShortJson());
			}
			obj.put(key+"", array);
		}
		return obj;
	}

	public void compatOldMailData() {
		for (Integer key : MAIL_MAX.keySet()) {
			List<MailInstance> mailByTab = getMailByTab(key);
			
			if(mailByTab.size() == 0 )continue;
			int i=1;
			for (MailInstance m : mailByTab) {
				if(m.getId() == null){
					m.setId(i);
					i++;
				}
			}
			
			//初始化hashmap
			getMailMapByTab(key);
		}
	}

}
