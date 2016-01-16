package com.jelly.activity;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jelly.node.datastore.mapper.RoleEntity;

public class GoldRank extends TopListJsonEntity{
	@Override
	public ArrayNode toJsonArray(RoleEntity e) {
		ArrayNode  node =  super.toJsonArray(e);
		node.add(e.getGold());
		return node;
	}
}
