package com.jelly.combat.result;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
when---> 0.开场前；1.攻击前；2攻击时（主动技能）；3攻击后；4被攻击前；5被攻击后（反击）；6死亡时;7复活；8buff关闭；9反击；10领悟; -1游戏结束

[
           {
               "idx":1,	//回合的玩家
               "when":1, // 领悟
               "sid":1//使用的技能
           },
           {
               "idx":1,	//回合的玩家
               "when":2, // 释放技能
               "sid":1,//使用的技能
               "ahp":10,//吸血效果
               "bhp":10,//对方掉血
               "status":1//状态 比如说下一次是否有反击 1=是击中2=闪避
           } , 
           {
               "idx":1,	//回合的玩家
               "when":7, // 释放技能
               "sid":1,//使用的技能
               "status":1//状态 比如说下一次是否有反击 1=是击中2=闪避
           } ,
            {
               "idx":1,	//回合的玩家
               "when":7, // 释放技能
               "sid":2,//使用的技能
               "status":1//状态 比如说下一次是否有反击 1=是击中2=闪避
           } ,
           {
               "idx":1,	//回合的玩家
               "when":3, // 持久技能删除
               "skillList":[1,2,3,5]//需要删除的技能
           },
           {
               "idx":1,	//回合的玩家
               "when":3, // 持久技能删除
               "list":[1,2,3,5]//需要删除的技能
           },
           {
               "idx":1,	//回合的玩家
               "when":4, // 回合开始掉血
               "list":[10,200,300]//回合开始掉多少血
           },
           {
               "idx":1,	//回合的玩家
               "when":5, // 反击
               "sid":1,//使用的技能
               "ahp":10,//吸血效果
               "bhp":10,//对方掉血
               "status":1//状态 比如说下一次是否有反击 1=是击中2=闪避
           },
           {
               "idx":1,	//回合的玩家
               "when":6, // 复活
               "sid":1,//使用的技能
               "ahp":10,//复活的时候恢复的血
               "bhp":10,//对方掉血
               "status":1//状态 比如说下一次是否有反击 1=是击中2=闪避
           }
        ]


 * @author zhoulei
 *
 */
public class CombatActionResult {
	
	/**
	 * 攻击者ID
	 */
	private Integer idx = -1;
	
	/**
	 * 触发类型
	 */
	private Integer when;
	
	/**
	 * 技能ID
	 */
	private Integer sid;
	
	/**
	 * 反弹技能ID
	 */
	private Integer cid;
	
	/**
	 * 待删除的技能列表或者减少血量的列表
	 */
	private Set<Integer> list = Sets.newHashSet();
	
	/**
	 * 掉血map
	 */
	private Map<Integer,Integer> hpMap = Maps.newHashMap();
	
	/**
	 * 攻击者血量的变化，例如吸血
	 */
	private Integer ahp;
	
	/**
	 * 防守方掉血
	 */
	private Integer bhp;
	
	
	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	/**
	 * 反弹伤害
	 */
	private Integer chp;
	
	
	/**
	 * 是否击中
	 */
	private Integer status;

	/**
	 * 攻击是关闭的Abuff
	 * 
	 */
	public List<Integer> closeBufA;
	/**
	 * 攻击是关闭的Bbuff
	 * 
	 */
	public List<Integer> closeBufB;
	
	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	
	public Integer getWhen() {
		return when;
	}

	public void setWhen(Integer when) {
		this.when = when;
	}

	public Integer getAhp() {
		return ahp;
	}

	public void setAhp(Integer ahp) {
		this.ahp = ahp;
	}

	public Integer getBhp() {
		return bhp;
	}

	public void setBhp(Integer bhp) {
		this.bhp = bhp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


	public Set<Integer> getList() {
		return list;
	}

	public void setList(Set<Integer> list) {
		this.list = list;
	}

	public Integer getChp() {
		return chp;
	}

	public void setChp(Integer chp) {
		this.chp = chp;
	}

	public List<Integer> getCloseBufA() {
		return closeBufA;
	}

	public void setCloseBufA(List<Integer> closeBufA) {
		this.closeBufA = closeBufA;
	}

	public List<Integer> getCloseBufB() {
		return closeBufB;
	}

	public void setCloseBufB(List<Integer> closeBufB) {
		this.closeBufB = closeBufB;
	}

	public Map<Integer, Integer> getHpMap() {
		return hpMap;
	}

	public void setHpMap(Map<Integer, Integer> hpMap) {
		this.hpMap = hpMap;
	}
	
	
}
