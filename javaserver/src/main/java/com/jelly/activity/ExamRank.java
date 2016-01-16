package com.jelly.activity;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jelly.node.datastore.mapper.RoleEntity;

public class ExamRank extends TopListJsonEntity{
	@Override
	public ArrayNode toJsonArray(RoleEntity e) {
		ArrayNode  node =  super.toJsonArray(e);
		node.add(e.getExamLv());
		return node;
	}
}
