package com.jelly.rank;

import io.nadron.app.Player;

import java.util.List;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.jelly.node.datastore.mapper.RoleEntity;

public class RankModel  {
	/** 玩家id */
	private String guid;
	private List<RankRecord> records;


	public RankModel() {
	}

	public RankModel(String guid) {
		this.guid = guid;
	}

	public RankModel(Player player) {
		super();
		this.guid = player.getId().toString();
	}

	public RankModel(RoleEntity player) {
		super();
		this.guid = player.getGuid();
	}



	public List<RankRecord> getRecords() {
		return records;
	}

	
	public void setRecords(List<RankRecord> records) {
		this.records = records;
	}


	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
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
		RankModel other = (RankModel) obj;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		return true;
	}

	public ArrayNode toJsonArray(RoleEntity e) {
		ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
		array.add(e.getName()).add(e.getLevel()).add(e.getCharId()).add(e.getPower());
		return array;
	}

	public ArrayNode toUnionJsonArray() {
		ArrayNode array = DynamicJsonProperty.jackson.createArrayNode();
		array.add(this.getGuid());
		array.add("");
		return array;
	}

	public void addRecord(RankRecord rankRecord) {
		if (this.records == null) {
			this.records = Lists.newArrayList();
		}
		if (this.records.size() > 15) {
			this.records.remove(0);
		}
		this.records.add(rankRecord);
	}

}
