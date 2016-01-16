package com.jelly.player;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.BaseItem;
import com.dol.cdf.common.bean.Item;
import com.dol.cdf.common.bean.Skill;
import com.dol.cdf.common.collect.Pair;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.ItemConstant;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.jelly.hero.Hero;

public class ItemInstance implements Comparable<ItemInstance> {
	
	private static final Logger logger = LoggerFactory.getLogger(ItemInstance.class);
	
	/**
	 * 道具id
	 */
	@JsonProperty("i")
	private int itemId;

	/**
	 * 堆叠数
	 */
	@JsonProperty("s")
	private int stackCount = 1;

	/**
	 * 强化等级
	 */
	@JsonProperty("l")
	private int lv = 0;

	// 洗练属性
	@JsonProperty("p")
	private Set<Integer> props = Sets.newHashSet();

	// 精炼属性Pair<key(技能ID),value(触发效果)>
	@JsonProperty("rp")
	private List<RefinePropModel> refineProps = Lists.newArrayList();

	// 临时缓存数据
	private BaseItem item;

	public Integer getAtt() {
		return getBaseAccBaseValue(0);
	}

	public Integer getDef() {
		return getBaseAccBaseValue(1);
	}

	public Integer getHp() {
		return getBaseAccBaseValue(2);
	}

	public Integer getAgi() {
		return getBaseAccBaseValue(3);
	}

	private int getBaseAccBaseValue(int tarProp) {
		if (getBaseItem() instanceof Accessory) {
			Accessory acc = (Accessory) getBaseItem();
			Integer category = acc.getCategory();
			int bValue = 0;
			switch (category) {
			case 0:
				bValue = acc.getAtt();
				break;
			case 1:
				bValue = acc.getDef();
				break;
			case 2:
				bValue = acc.getHp()/10;
				break;
			case 3:
				bValue = acc.getAgi();
				break;
			default:
				break;
			}
			if (category == tarProp || props.contains(tarProp)) {
				return bValue;
			}
		}
		return 0;
	}

	/**
	 * 洗练属性，增加数值
	 * 
	 */
	public void washProp(int pos) {
		int[] is = ItemConstant.ACCESSORY_PROPS[getBaseItem().getCategory()];
		int tarProp = is[pos];
		props.add(tarProp);
	}
	
	public Set<Integer> getProps() {
		return props;
	}
	
	

	/**
	 * 是否可以洗练
	 * @return 可洗练的位置
	 */
	public int canWashPos() {
		int srcSize = props.size();
		int[] is = ItemConstant.ACCESSORY_PROPS[getBaseItem().getCategory()];
		if (srcSize >= is.length - 1) {
			return -1;
		} else {
			return props.size() + 1;
		}
	}
	
	public int refineProp(int pos, int itemId) {
		int tarSkillId = 0;
		Item skillItemCfg = AllGameConfig.getInstance().items.getItemById(itemId);
		if (skillItemCfg.getCategory() == 12) {
			Skill skill = AllGameConfig.getInstance().skills.getRndAttackSkill(skillItemCfg.getQuality());
			tarSkillId = skill.getId();
		} else {
			tarSkillId = skillItemCfg.getSid();
		}		
		int effectVal = AllGameConfig.getInstance().skills.getSkill(tarSkillId).getRefineplus(); // 洗练获得的技能效果
		logger.info("refineProp={},effect={}", tarSkillId, effectVal);		
		RefinePropModel newProp = new RefinePropModel(tarSkillId, effectVal);
		if (refineProps.size() > pos) {
			// 使用新属性替换旧属性
			refineProps.set(pos, newProp);
		} else {
			refineProps.add(newProp);
		}
		return tarSkillId;
	}
	
	public Map<Integer, Integer> getRefineProps() {
		Map<Integer, Integer> rprops = Maps.newHashMap();
		for (RefinePropModel p : refineProps) {
			rprops.put(p.getSkillId(), p.getEffectVal());
		}
		return rprops;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getStackCount() {
		return stackCount;
	}

	public void setStackCount(int stackCount) {
		this.stackCount = stackCount;
	}

	public BaseItem getBaseItem() {
		if (item == null) {
			item = AllGameConfig.getInstance().items.getBaseItem(itemId);
		}
		return item;
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public void toJson(ObjectNode json) {
		json.put("id", itemId);
		json.put("s", stackCount);
		if (lv != 0)
			json.put("lv", getLv());
		if (!props.isEmpty()) {
			json.put("props", DynamicJsonProperty.convertToJsonNode(props));
		}
//		ArrayNode rprops = DynamicJsonProperty.jackson.createArrayNode();
//		for (RefinePropModel e : refineProps) {
//			ObjectNode p = DynamicJsonProperty.jackson.createObjectNode();
//			p.put("id", e.getSkillId());
//			p.put("effect", e.getEffectVal());
//			
//			rprops.add(p);
//		}
//		json.put("refineProps", rprops);
		if (!refineProps.isEmpty()) {
			json.put("refineProps", DynamicJsonProperty.convertToJsonNode(refineProps));
		}
	}

	public ObjectNode toJson() {
		ObjectNode json = DynamicJsonProperty.jackson.createObjectNode();
		json.put("id", itemId);
		if (lv != 0)
			json.put("lv", getLv());
		if (!props.isEmpty()) {
			json.put("props", DynamicJsonProperty.convertToJsonNode(props));
		}
		if (!refineProps.isEmpty()) {
			json.put("refineProps", DynamicJsonProperty.convertToJsonNode(refineProps));
		}
		
//		ArrayNode rprops = DynamicJsonProperty.jackson.createArrayNode();
//		for (RefinePropModel e : refineProps) {
//			ObjectNode p = DynamicJsonProperty.jackson.createObjectNode();
//			p.put("id", e.getSkillId());
//			p.put("effect", e.getEffectVal());
//			
//			rprops.add(p);
//		}
//		json.put("refineProps", rprops);
		return json;
	}

	@Override
	public int compareTo(ItemInstance o) {
		BaseItem me = getBaseItem();
		BaseItem other = o.getBaseItem();
		return ComparisonChain.start().compare(me.getCategory(), other.getCategory(), Ordering.natural().nullsLast()).compare(other.getQuality(), me.getQuality(), Ordering.natural().nullsLast()).result();
	}
}
