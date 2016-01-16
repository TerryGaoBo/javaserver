package com.jelly.player;

import java.util.List;

public interface ICombatGroup {

	/**
	 * 获得所有的战斗人员
	 * 
	 * @return
	 */
	public List<IFighter> getFighters();

	public boolean doFail();
	
	public boolean doWin();
	
	public boolean isWin();

	/**
	 * 做战斗前准备
	 */
	public void active();
	
	public void unActive();
	
	public IFighter getCurrentFighter();
	
	public IFighter getNextFighter();

	public IFighter getFirstFighter();

	public boolean isEmptyGroup();
}
