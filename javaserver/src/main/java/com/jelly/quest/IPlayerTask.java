package com.jelly.quest;

import io.nadron.app.Player;

public interface IPlayerTask {


	/**
	 * 接受任务
	 * 
	 */
	public void acceptTask(Player player);
	
	/**
	 * 提交任务领取奖励
	 * 
	 * @param taskId
	 */
	public void submitTask(int taskId,Player player);

}
