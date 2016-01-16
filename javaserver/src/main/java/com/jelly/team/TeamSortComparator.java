package com.jelly.team;

import java.util.Comparator;
import java.util.Map;

/**
 * 军团排名比较器类
 **/
public class TeamSortComparator implements Comparator<String> {

	private Map<String, Team> names2Teams;
	
	public TeamSortComparator(Map<String, Team> names2Teams) {
		this.names2Teams = names2Teams;
	}
	
//	@Override
//	public int compare(String lhs, String rhs) {
//		Team team1 = this.names2Teams.get(lhs);
//		Team team2 = this.names2Teams.get(rhs);
//		// 根据战斗力高低比较两个军团先后顺序
//		int powDiff = team1.calcPower() - team2.calcPower();
//		if (powDiff > 0) {
//			return -1;
//		} else if (powDiff < 0) {
//			return 1;
//		}
//		// 当战斗力相同时根据创建时间比较两个军团先后顺序, 先创建的军团排在后创建军团之前
//		long timeDiff = team1.getCreateTime() - team2.getCreateTime();
//		if (timeDiff > 0) {
//			return 1;
//		} else if (timeDiff < 0) {
//			return -1;
//		}
//		return 0;
//	}
	
	
	@Override
	public int compare(String lhs,String rhs)
	{
		Team team1 = this.names2Teams.get(lhs);
		Team team2 = this.names2Teams.get(rhs);
		
		int level = team1.getLevel() - team2.getLevel();
		if(level > 0){
			return -1;
		}else if(level < 0 ){
			return 1;
		}
		
		// 当等级相同则根据战斗力排序
		int power = team1.calcPower() - team2.calcPower();
		if(power > 0){
			return -1;
		}else if(power < 0){
			return 1;
		}
		
		// 当等级和战斗力都相等的情况下，根据军团创建时间排序
		long timeDiff = team1.getCreateTime() - team2.getCreateTime();
		if (timeDiff > 0) {
			return 1;
		}else if (timeDiff < 0) {
			return -1;
		}
		
		return 0;
	}

}
