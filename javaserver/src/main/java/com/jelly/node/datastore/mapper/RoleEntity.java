package com.jelly.node.datastore.mapper;

import java.sql.Timestamp;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

public class RoleEntity implements Comparable<RoleEntity> {
	String guid;
	int net;
	String channel;
	String userId;
	String name;
	int charId;
	int level;
	Timestamp lastLogin;
	Timestamp firstLogin;
	int vipScore;
	int paid; //1-充值过 0-没充过
	int power;
	int heroPow;
	int gold;
	int silver;
	int coin;
	int exp;
	int examLv;
	int chapter;
	int stage;
	String teamName;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getNet() {
		return net;
	}

	public void setNet(int net) {
		this.net = net;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCharId() {
		return charId;
	}

	public void setCharId(int charId) {
		this.charId = charId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Timestamp getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(Timestamp firstLogin) {
		this.firstLogin = firstLogin;
	}

	public int getVipScore() {
		return vipScore;
	}

	public void setVipScore(int vipScore) {
		this.vipScore = vipScore;
	}

	public int getPaid() {
		return paid;
	}

	public void setPaid(int paid) {
		this.paid = paid;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	
	public int getHeroPow() {
		return heroPow;
	}

	public void setHeroPow(int heroPow) {
		this.heroPow = heroPow;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getSilver() {
		return silver;
	}

	public void setSilver(int silver) {
		this.silver = silver;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + charId;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((lastLogin == null) ? 0 : lastLogin.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		RoleEntity other = (RoleEntity) obj;
		if (charId != other.charId)
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (lastLogin == null) {
			if (other.lastLogin != null)
				return false;
		} else if (!lastLogin.equals(other.lastLogin))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
	
	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getChapter() {
		return chapter;
	}

	public void setChapter(int chapter) {
		this.chapter = chapter;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getExamLv() {
		return examLv;
	}

	public void setExamLv(int examLv) {
		this.examLv = examLv;
	}
	
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	public String getTeamName() {
		return this.teamName;
	}

	@Override
	public int compareTo(RoleEntity other) {
		return ComparisonChain.start().compare(getLastLogin(), other.getLastLogin(), Ordering.natural().nullsLast()).result();
	}

	@Override
	public String toString() {
		return "RoleEntity [userId=" + userId + ", name=" + name + ", charId=" + charId + ", level=" + level + ",channel"+channel+"]";
	}


}
