package com.jelly.hero;

import java.util.List;
import java.util.Map;

public interface IHero {

	/** 输出伤害百分比 */
	public static final int HURT_TARGET_RATIO = 1;
	/** 受到伤害百分比 */
	public static final int HURT_SELF_RATIO = 2;
	/** HP上限百分比 */
	public static final int HP_MAX_RATIO = 3;
	/** HP当前百分比 */
	public static final int HP_CURRENT_RATIO = 4;
	/** 攻击 */
	public static final int PROP_STRENGTH = 11;
	/** 防御 */
	public static final int PROP_DEFENCE = 12;
	/** 速度 */
	public static final int PROP_SPEED = 13;
	/** 敏捷 */
	public static final int PROP_AGILITY = 14;
	/** 暴击率 */
	public static final int PROP_CRIT_RATE = 15;

	public int getUuid();
	
	public int getRoleId();

	public int getStrength();

	public int getDefence();

	public int getHpMax();
	
	public int getHp();

	public int getAgility();

	public int getLevel();

	public void setLevel(int level);

	public List<BaseSkill> getSkills();

	public int getTop();

	public int getLeft();

	public int getRight();

	public int getBottom();

	public int getQuality();

	public int getPower();
	
	public int[] getEskill();
	
	public String getName();
	
	public int[] getChakra();

	public Map<Integer, Integer> getEquipRefineProps();
	
	public int getDefineID();
	public void setDefineID(int id);
	public String getDefineGuid();
	public void setDefineGuid(String guid);
}
