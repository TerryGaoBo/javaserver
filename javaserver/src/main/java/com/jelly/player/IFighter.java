package com.jelly.player;

import java.util.List;

import com.jelly.combat.context.CBContext;
import com.jelly.combat.event.ICombatEventManager;
import com.jelly.hero.BaseSkill;
import com.jelly.hero.IHero;

public interface IFighter extends ICombatEventManager{
	
	
	public int getUuid();

	public IHero getHero();

	/**
	 * 表示攻击者还是防守者
	 * 
	 * @return
	 */
	public int getIdx();
	
	public void setIdx(int idx);

	/**
	 * 战斗者的血量
	 * 
	 * @return
	 */
	public int getHp();
	
	/**
	 * 设置血量根据上下文设置掉血的双方
	 * 
	 * @return
	 */
	public void addHpWithContext(int hp);
	
	public void addHp(int hp);
	
	public void setHp(int hp);

	
	public int getStrength();

	public int getDefence();

	public int getHpMax();

	public int getAgility();
	
	
	/**
	 * 检测该玩家是否可以施法/攻击
	 * 
	 */
	public void cast();

	/**
	 * 是否死亡、用于检测是否结束
	 * 
	 * @return
	 */
	public boolean isDead();
	
	/**
	 * 处理是否死亡逻辑
	 * @return
	 */
	public boolean processIfDead();

	public void setEnemy(IFighter enemy);

	public IFighter getEnemy();
	
	public void setCBContext(CBContext context);
	
	public CBContext getCBContext();
	
	public void registerBuffHandler(BaseSkill skill);
	
	public List<BaseSkill> getSKillByWhen(int when);
	
	public void activeFight();
	
	public void activeGroupSkill();
	
	public int getRecoverTimes();
	
	public void setRecoverTimes(int times);
	
}
