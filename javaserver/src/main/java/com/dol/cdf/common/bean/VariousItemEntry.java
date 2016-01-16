package com.dol.cdf.common.bean;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.config.AllGameConfig;
import com.google.common.collect.Sets;

public class VariousItemEntry {

	private static final Logger logger = LoggerFactory.getLogger(VariousItemEntry.class);

	public static final VariousItemEntry[] EMPTY_ARRAY = new VariousItemEntry[0];

	public static final Set<String> NULL_ITEM_TYPES = Sets.newHashSet("");
	
	public static final VariousItemEntry EMPTY_ITEM_ENTRY = new VariousItemEntry("",0,ItemGroupEnum.NULL) ;

	/**
	 * 道具的类型
	 */
	private String type;

	/**
	 * 属于那个组
	 */
	private ItemGroupEnum group;

	/**
	 * 奖励的数量
	 */
	private int amount;

	public VariousItemEntry(String type, int amount) {
		this.type = type;
		this.amount = amount;
		initItemGroup(type);
	}
	
	private VariousItemEntry(String type, int amount,ItemGroupEnum group) {
		this.type = type;
		this.amount = amount;
		this.group = group;
	}

	public VariousItemEntry(int id, int amount) {
		this.type = id + "";
		this.amount = amount;
		initItemGroup(type);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ItemGroupEnum getGroup() {
		return group;
	}

	public void setGroup(ItemGroupEnum group) {
		this.group = group;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	private void initItemGroup(String type) {
		if (type.equals("gold") || type.equals("silver") || type.equals("coin") || type.equals("arenapoint")) {
			group = ItemGroupEnum.currency;
		} else if (type.equals("exp")) {
			group = ItemGroupEnum.exp;
		} else if (type.equals("energy")) {
			group = ItemGroupEnum.energy;
		}else if (type.equals("spoint")) {
			group = ItemGroupEnum.spoint;
		} 
		else if (NULL_ITEM_TYPES.contains(type)) {
			group = ItemGroupEnum.NULL;
		} else {
			int id = Integer.parseInt(type);
			if (config().items.getBaseItem(id) != null) {
				group = ItemGroupEnum.bag;
			}else if (config().characterManager.getRoleById(id) != null) {
				//大于1表示给忍者加经验，小于1表示增加忍者
				if (amount <= 1) {
					group = ItemGroupEnum.hero;
				}else {
					group = ItemGroupEnum.rxp;
				}
				
			}else {
				//logger.error("various Item no group type: {}",type);
			}


		}
	}

	private AllGameConfig config() {
		return AllGameConfig.getInstance();
	}

	@Override
	public String toString() {
		return "<" + type + "|" + amount + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariousItemEntry other = (VariousItemEntry) obj;
		if (amount != other.amount)
			return false;
		if (group != other.group)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
