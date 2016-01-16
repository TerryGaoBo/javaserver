package com.jelly.team;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public enum TeamMemberTitle {
	NONE(0) {

		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return null;
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return null;
		}
	},// 团长
	COMMANDER(1) {
		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getPromoteAfterTitle(this, title2RemainCountMap);
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getDemoteAfterTitle(this, title2RemainCountMap);
		}
	}, // 副团长
	DEPUTY_COMMANDER(2) {
		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getPromoteAfterTitle(this, title2RemainCountMap);
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getDemoteAfterTitle(this, title2RemainCountMap);
		}
	}, // 上士
	STAFF_SERGEANT(3) {
		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getPromoteAfterTitle(this, title2RemainCountMap);
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getDemoteAfterTitle(this, title2RemainCountMap);
		}
	}, // 中士
	SERGEANT(4) {
		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getPromoteAfterTitle(this, title2RemainCountMap);
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getDemoteAfterTitle(this, title2RemainCountMap);
		}
	}, // 下士
	COPRPORA(5) {
		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getPromoteAfterTitle(this, title2RemainCountMap);
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getDemoteAfterTitle(this, title2RemainCountMap);
		}
	}, // 团员
	TRAINEE(6) {
		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getPromoteAfterTitle(this, title2RemainCountMap);
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getDemoteAfterTitle(this, title2RemainCountMap);
		}
	}, // 见习
	INTERN(7) {

		@Override
		public TeamMemberTitle getPromoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getPromoteAfterTitle(this, title2RemainCountMap);
		}

		@Override
		public TeamMemberTitle getDemoteAfterTitle(
				Map<TeamMemberTitle, Integer> title2RemainCountMap) {
			return TeamMemberTitle.getDemoteAfterTitle(this, title2RemainCountMap);
		}
	} 
	;

	int type;

	private TeamMemberTitle(int type) {
		this.type = type;
	}

	/**
	 * 升职
	 **/
	public abstract TeamMemberTitle getPromoteAfterTitle(
			Map<TeamMemberTitle, Integer> title2RemainCountMap);

	/**
	 * 降职
	 **/
	public abstract TeamMemberTitle getDemoteAfterTitle(
			Map<TeamMemberTitle, Integer> title2RemainCountMap);
	
	static final Logger logger = LoggerFactory.getLogger(TeamMemberTitle.class);
	static Map<TeamMemberTitle, TeamMemberTitle> promoteTitlesChain, demoteTitlesChain;
	static {
		// 升职关系
		promoteTitlesChain = Maps.newHashMap();
//		promoteTitlesChain.put(TeamMemberTitle.INTERN, TeamMemberTitle.TRAINEE);
//		promoteTitlesChain.put(TeamMemberTitle.TRAINEE,
//				TeamMemberTitle.COPRPORA);
//		promoteTitlesChain.put(TeamMemberTitle.COPRPORA,
//				TeamMemberTitle.SERGEANT);
		
		promoteTitlesChain.put(TeamMemberTitle.SERGEANT,
				TeamMemberTitle.STAFF_SERGEANT);
		promoteTitlesChain.put(TeamMemberTitle.STAFF_SERGEANT,
				TeamMemberTitle.DEPUTY_COMMANDER);
		promoteTitlesChain.put(TeamMemberTitle.DEPUTY_COMMANDER,
				TeamMemberTitle.COMMANDER);

		// 降职关系
		demoteTitlesChain = Maps.newHashMap();
		demoteTitlesChain.put(TeamMemberTitle.COMMANDER,
				TeamMemberTitle.DEPUTY_COMMANDER);
		demoteTitlesChain.put(TeamMemberTitle.DEPUTY_COMMANDER,
				TeamMemberTitle.STAFF_SERGEANT);
		demoteTitlesChain.put(TeamMemberTitle.STAFF_SERGEANT,
				TeamMemberTitle.SERGEANT);
//		demoteTitlesChain.put(TeamMemberTitle.SERGEANT,
//				TeamMemberTitle.COPRPORA);
//		demoteTitlesChain
//				.put(TeamMemberTitle.COPRPORA, TeamMemberTitle.TRAINEE);
//		demoteTitlesChain.put(TeamMemberTitle.TRAINEE, TeamMemberTitle.INTERN);
	}
	
	/**
	 * 获取提升后的下一级新职务
	 **/
	private static TeamMemberTitle getPromoteAfterTitle(
			TeamMemberTitle currentTitle,
			Map<TeamMemberTitle, Integer> title2RemainCountMap) {
		if (!promoteTitlesChain.containsKey(currentTitle)) {
			return null;
		}
		if (currentTitle == TeamMemberTitle.DEPUTY_COMMANDER) {
			return TeamMemberTitle.COMMANDER;
		} else {
			TeamMemberTitle newTitle = promoteTitlesChain.get(currentTitle);
			int remainCount = title2RemainCountMap.get(newTitle);
			return remainCount == -1 || remainCount > 0 ? newTitle : null;
		}
	}

	/**
	 * 获取降低后的下一级职务
	 **/
	private static TeamMemberTitle getDemoteAfterTitle(
			TeamMemberTitle currentTitle,
			Map<TeamMemberTitle, Integer> title2RemainCountMap) {
		if (!demoteTitlesChain.containsKey(currentTitle)) {
			return null;
		}
		TeamMemberTitle newTitle = demoteTitlesChain.get(currentTitle);
		int remainCount = title2RemainCountMap.get(newTitle);
		return remainCount == -1 || remainCount > 0 ? newTitle : null;
	}
	
	public static TeamMemberTitle parse(int type) {
		for (TeamMemberTitle title : TeamMemberTitle.values()) {
			if (title.ordinal() == type) {
				return title;
			}
		}
		return null;
	}

	public static TeamMemberTitle parse(String type) {
		return TeamMemberTitle.parse(Integer.parseInt(type));
	}

//	public static boolean check(int type) {
//		return TeamMemberTitle.parse(type) != null;
//	}

//	public static void main(String[] args) {
//		Map<TeamMemberTitle, Integer> titles = Maps.newHashMap();
//		titles.put(TeamMemberTitle.INTERN, -1);
//		titles.put(TeamMemberTitle.TRAINEE, -1);
//		titles.put(TeamMemberTitle.COPRPORA, 0);
//		titles.put(TeamMemberTitle.SERGEANT, 0);
//		titles.put(TeamMemberTitle.STAFF_SERGEANT, 0);
//		titles.put(TeamMemberTitle.DEPUTY_COMMANDER, 0);
//
//		TeamMemberTitle testTitle = TeamMemberTitle.TRAINEE;
//		System.out.println("NewTitle="
//				+ testTitle.demote(titles, TeamMemberTitle.COMMANDER));
//	}
}