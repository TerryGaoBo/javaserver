package com.jelly.activity;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jelly.node.datastore.mapper.RoleEntity;

public class PayRank extends TopListJsonEntity {
	public ArrayNode toJsonArray(RoleEntity e, long payCount) {
		ArrayNode node = toJsonArray(e);
		node.add(payCount);
		return node;
	}
}
