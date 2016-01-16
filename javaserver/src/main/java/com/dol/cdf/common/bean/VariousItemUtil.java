package com.dol.cdf.common.bean;

import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.MessageCode;
import com.dol.cdf.common.SBUtil;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.constant.ConstTypeEnum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class VariousItemUtil {

	/**
	 * 将<gold|1><ore|2>转换为VariousItemEntry[]
	 * 
	 * @param str
	 * @return
	 */
	public static VariousItemEntry[] parse(String str) {
		if (str == null || str.equals("")) {
			return VariousItemEntry.EMPTY_ARRAY;
		}
		return (VariousItemEntry[]) ConstTypeEnum.VIENTRY_LIST.getValue(str);
	}
	
	/**
	 * 将<gold;1><ore;2>转换为VariousItemEntry[]
	 * 
	 * @param str
	 * @return
	 */
	public static VariousItemEntry[] parse1(String value) {
		if (value == null || value.equals("")) {
			return VariousItemEntry.EMPTY_ARRAY;
		}
		value = value.substring(1, value.length() - 1);
		String[] stringArray = value.split("><");
		VariousItemEntry[] itemArray = new VariousItemEntry[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			String[] realItem = stringArray[i].split(";");
			String key = realItem[0];
			int count = Integer.parseInt(realItem[1]);
			itemArray[i] = new VariousItemEntry(key, count);
		}
		return itemArray;
	}

	/**
	 * 执行奖励， 外部调用此方法，需要自行sendchangedMessage，并且保存player，目的是减少player对象的序列化
	 * 
	 * @param player
	 * @param awards
	 * @param bonusType
	 */
	public static void doBonus(Player player, VariousItemEntry[] awards, int bonusType, boolean isBonus) {
		if (player == null) {
			return;
		}
		for (VariousItemEntry item : awards) {
			doBonus(player, item, bonusType, isBonus);
		}
	}

	public static ObjectNode itemToJson(VariousItemEntry[] awards) {
		ObjectNode objectNode = DynamicJsonProperty.jackson.createObjectNode();
		for (VariousItemEntry variousItemEntry : awards) {
			itemToJson(objectNode, variousItemEntry);
		}
		return objectNode;
	}

	private static void itemToJson(ObjectNode objectNode, VariousItemEntry variousItemEntry) {
		if (variousItemEntry.getGroup() == ItemGroupEnum.NULL) {
			return;
		}
		if (variousItemEntry.getGroup() == ItemGroupEnum.rxp) {
			objectNode.put("rxp", variousItemEntry.getAmount());
		}else {
			objectNode.put(variousItemEntry.getType(), variousItemEntry.getAmount());
		}
	}
	
	public static ObjectNode itemToJson( VariousItemEntry variousItemEntry) {
		ObjectNode objectNode = DynamicJsonProperty.jackson.createObjectNode();
		if (variousItemEntry.getGroup() == ItemGroupEnum.NULL) {
			return objectNode;
		}
		if (variousItemEntry.getGroup() == ItemGroupEnum.rxp) {
			objectNode.put("rxp", variousItemEntry.getAmount());
		}else {
			objectNode.put(variousItemEntry.getType(), variousItemEntry.getAmount());
		}
		return objectNode;
	}

	public static ObjectNode itemToJson(List<VariousItemEntry> awards) {
		ObjectNode objectNode = DynamicJsonProperty.jackson.createObjectNode();
		for (VariousItemEntry variousItemEntry : awards) {
			itemToJson(objectNode, variousItemEntry);
		}
		return objectNode;
	}

	/**
	 * 执行vip奖励， 外部调用此方法，需要自行sendchangedMessage，并且保存player，目的是减少player对象的序列化
	 * 
	 * @param player
	 * @param awards
	 * @param bonusType
	 */
	public static void doVipBonus(Player player, VariousItemEntry[] awards, int bonusType, boolean isBonus) {
		if (player == null) {
			return;
		}
		for (VariousItemEntry item : awards) {
			doVipBonus(player, item, bonusType, isBonus);
		}
	}

	public static void doVipBonus(Player player, VariousItemEntry item, int bonusType, boolean isBonus) {
		// if ("exp".equals(item.getType())) {
		// int vipXp = (int) (item.getAmount() *
		// VipUtil.getVipExpScale(player.getProperty()));
		// VariousItemEntry tempItem = new VariousItemEntry(item.getType(),
		// vipXp);
		// doBonus(player, tempItem, bonusType);
		// } else if ("gold".equals(item.getType())) {
		// int vipGold = (int) (item.getAmount() *
		// VipUtil.getVipGoldScale(player.getProperty()));
		// VariousItemEntry tempItem = new VariousItemEntry(item.getType(),
		// vipGold);
		// doBonus(player, tempItem, bonusType);
		// } else {
		// doBonus(player, item, bonusType);
		// }
	}

	/**
	 * 执行奖励， 外部调用此方法，需要自行sendchangedMessage，并且保存player,目的是减少player对象的序列化
	 * 
	 * @param player
	 * @param awards
	 * @param bonusType
	 */
	public static void doBonus(Player player, List<VariousItemEntry> awards, int bonusType, boolean isBonus) {
		if (player == null) {
			return;
		}
		for (VariousItemEntry item : awards) {
			doBonus(player, item, bonusType, isBonus);
		}
	}

	/**
	 * 执行奖励， 外部调用此方法，需要自行sendchangedMessage，并且保存player,目的是减少player对象的序列化
	 * 
	 * @param player
	 * @param awards
	 * @param bonusType
	 */
	public static int doBonus(Player player, VariousItemEntry item, int bonusType, boolean isBonus) {
		if (player == null) {
			return MessageCode.FAIL;
		}
		return item.getGroup().doBonus(player, item.getType(), item.getAmount(), bonusType, isBonus);

	}

	/**
	 * 获得奖励描述
	 * 
	 * @param player
	 * @param item
	 * @return
	 */
	public static String getDesc(DefaultPlayer player, VariousItemEntry item, boolean isBonus) {
		if (player == null) {
			return StringHelper.EMPTY;
		}
		return item.getGroup().getDesc(player, item.getType(), item.getAmount(), isBonus);
	}

	/**
	 * 获得奖励描述
	 * 
	 * @param player
	 * @param awards
	 * @return
	 */
	public static String getDesc(DefaultPlayer player, VariousItemEntry[] awards, boolean isBonus) {
		if (player == null) {
			return StringHelper.EMPTY;
		}
		StringBuilder sb = SBUtil.startAppend(awards.length + 10);
		for (VariousItemEntry item : awards) {
			sb.append(getDesc(player, item, isBonus));
		}
		return sb.toString();
	}

	/**
	 * 获得奖励描述
	 * 
	 * @param player
	 * @param awards
	 * @return
	 */
	public static String getDesc(DefaultPlayer player, List<VariousItemEntry> awards, boolean isBonus) {
		if (player == null) {
			return StringHelper.EMPTY;
		}
		StringBuilder sb = SBUtil.startAppend(awards.size() + 10);
		for (VariousItemEntry item : awards) {
			sb.append(getDesc(player, item, isBonus));
		}
		return sb.toString();
	}

	/**
	 * 检验是否可以进行奖励，例如背包是否满等. 如果不在乎不满足给与奖励的条件，则可以直接执行doBonus
	 * 
	 * @param lobby
	 * @param awards
	 * @return 三
	 */
	public static int checkBonus(Player player, VariousItemEntry[] awards, boolean isBonus) {
		for (VariousItemEntry item : awards) {
			int code = checkBonus(player, item, isBonus);
			if (code != MessageCode.OK) {
				return code;
			}
		}
		return MessageCode.OK;
	}

	/**
	 * 检验是否可以进行奖励，例如背包是否满等. 如果不在乎不满足给与奖励的条件，则可以直接执行doBonus
	 * 
	 * @param lobby
	 * @param awards
	 * @return 三
	 */
	public static int checkBonus(Player player, List<VariousItemEntry> awards, boolean isBonus) {
		for (VariousItemEntry item : awards) {
			int code = checkBonus(player, item, isBonus);
			if (code != MessageCode.OK) {
				return code;
			}
		}
		return MessageCode.OK;
	}

	/**
	 * 检验是否可以进行奖励，例如背包是否满等. 如果不在乎不满足给与奖励的条件，则可以直接执行doBonus
	 * 
	 * @param lobby
	 * @param awards
	 * @return 三
	 */
	public static int checkBonus(Player player, VariousItemEntry item, boolean isBonus) {
		int code = item.getGroup().check(player, item.getType(), item.getAmount(), isBonus);
		if (code != MessageCode.OK) {
			return code;
		}
		return MessageCode.OK;
	}
	
	public static List<VariousItemEntry> mapToVariousItem(Map<String, Integer> map){
		List<VariousItemEntry> list = Lists.newArrayList();
		for (Entry<String, Integer> entry : map.entrySet()) {
			list.add(new VariousItemEntry(entry.getKey(), entry.getValue()));
		}
		return list;
		
	}
}
