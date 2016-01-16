package com.dol.cdf.common.entities;

import com.dol.cdf.common.DynamicJsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NoticeProps {
	@JsonProperty("t")
	private String title;
	
	@JsonProperty("c")
	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public ObjectNode toJson() {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("title", title);
		obj.put("content", content);
		return obj;
		
	}
	
}
