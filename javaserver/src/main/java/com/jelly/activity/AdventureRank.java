package com.jelly.activity;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jelly.node.datastore.mapper.RoleEntity;

public class AdventureRank extends TopListJsonEntity{
	
	@Override
	public ArrayNode toJsonArray(RoleEntity e) {
		ArrayNode  node =  super.toJsonArray(e);
		StringBuffer sBuffer  = new StringBuffer();
		sBuffer.append(e.getChapter()).append("-").append(e.getStage());
		node.add(sBuffer.toString());
		return node;
	}
}
