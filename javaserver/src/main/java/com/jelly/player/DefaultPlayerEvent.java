package com.jelly.player;

import io.nadron.event.impl.DefaultEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class DefaultPlayerEvent extends DefaultEvent
{
	private static final long serialVersionUID = 1L;
	
	private JsonNode  source;

	@Override
	public JsonNode getSource()
	{
		return source;
	}

	public void setSource(JsonNode source)
	{
		this.source = source;
	}
	
	@Override
	public String toString() {
		return "Event [type=" + type + ", source=" + source + ", timeStamp="
				+ timeStamp + "]";
	}
}
