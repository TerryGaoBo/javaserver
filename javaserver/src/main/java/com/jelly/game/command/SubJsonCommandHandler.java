package com.jelly.game.command;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class SubJsonCommandHandler {
	private static final Logger logger = LoggerFactory.getLogger(SubJsonCommandHandler.class);
	
	public static abstract class JsonCommandHandler {
		
		public abstract String getCommand();
		
		public JsonCommandHandler() {
			init();
		}

		public void init() {
			
		}

		// return false if you can't handle this command now
		public abstract void run(JsonNode object);
	}

	protected Set<JsonCommandHandler> handlers = new HashSet<JsonCommandHandler>();

	public Set<JsonCommandHandler> getJsonCommandHandlers() {
		return this.handlers;
	}

	protected void addHandler(JsonCommandHandler handler) {
//		if (logger.isDebugEnabled()) {
//			logger.debug(handler.getCommand());
//		}
		this.handlers.add(handler);
	}

	/**
	 * 移除该handler
	 * 
	 * @param handler
	 */
	protected void removeHandler(JsonCommandHandler handler) {
		this.handlers.remove(handler);
	}

}
