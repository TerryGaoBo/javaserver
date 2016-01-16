package com.jelly.game;

import io.nadron.app.GameCommandInterpreter;
import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.app.impl.InvalidCommandException;
import io.nadron.event.Event;
import io.nadron.event.impl.DefaultSessionEventHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.jelly.activity.WorldActivity;
import com.jelly.game.command.BuildingCommandHandler;
import com.jelly.game.command.ItemCommandHandler;
import com.jelly.game.command.PlayerCommandHandler;
import com.jelly.game.command.SkillCommandHandler;
import com.jelly.game.command.SocialCommandHandler;
import com.jelly.game.command.SubJsonCommandHandler;
import com.jelly.game.command.SubJsonCommandHandler.JsonCommandHandler;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;

@SuppressWarnings("rawtypes")
public class SessionCommandHandler extends DefaultSessionEventHandler implements GameCommandInterpreter {
	private static final Logger LOG = LoggerFactory.getLogger(SessionCommandHandler.class);
	volatile int cmdCount;

	Map<String, JsonCommandHandler> commandToHandle = new HashMap<String, JsonCommandHandler>();

	ObjectCacheService objectCache;

	Map<String, PlayerSession> gameRoomSessions;
	AllPlayersCache allPlayersCache;
	
	WorldActivity worldActivity;
	
	

	public SessionCommandHandler(Session session, ObjectCacheService objectCache, Map<String, PlayerSession> gameRoomSessions, AllPlayersCache allPlayersCache,WorldActivity worldActivity) {
		super(session);
		this.objectCache = objectCache;
		this.gameRoomSessions = gameRoomSessions;
		this.allPlayersCache =allPlayersCache;
		this.worldActivity = worldActivity;
		registerHandler(session);

	}

	/**
	 * 注册所有的操作处理器
	 * 
	 * @param playersession
	 */
	private void registerHandler(Session session) {
		addHandler(new PlayerCommandHandler(session));
		addHandler(new ItemCommandHandler(session));
		addHandler(new SkillCommandHandler(session));
		addHandler(new BuildingCommandHandler(session));
		addHandler(new SocialCommandHandler(session, gameRoomSessions, objectCache,allPlayersCache,worldActivity));
	}

	@Override
	public void onDataIn(Event event) {
		Object source = event.getSource();
		DefaultPlayer player = null;
		try {
			if (source == null) {
				if(ContextConfig.DEBUG_LOG_OPEN) {
					LOG.error("userId:{},event type: {},source is null",player!=null?player.getProperty().getUserId():"null", event.getType());
				}
				return;
			}
			player = ((DefaultPlayer) (((PlayerSession) this.getSession()).getPlayer()));
			if(ContextConfig.DEBUG_LOG_OPEN) {
				LOG.info("before interpretCommand ,userId:{},source:{}, change:{}",player!=null?player.getProperty().getUserId():"null", source,player.getPlayerChanged());
			}
			interpretCommand(source);
			if(ContextConfig.DEBUG_LOG_OPEN) {
				LOG.info("after interpretCommand ,userId:{},source:{}, change:{}",player!=null?player.getProperty().getUserId():"null", source,player.getPlayerChanged());
			}
			if (player.hasPlayerChanged()) {
				if(ContextConfig.DEBUG_LOG_OPEN) {
					LOG.info("before sendChangedMessage ,userId:{},source:{}, change:{}",player!=null?player.getProperty().getUserId():"null", source,player.getPlayerChanged());
				}
				player.sendChangedMessage();
				if(ContextConfig.DEBUG_LOG_OPEN) {
					LOG.info("after sendChangedMessage ,userId:{},source:{}, change:{}",player!=null?player.getProperty().getUserId():"null", source,player.getPlayerChanged());
				}
				this.objectCache.putObject(player); //测试新手引导注释掉这行
				if (LOG.isDebugEnabled()) {
					DefaultPlayer cache = this.objectCache.getCache(player.guid, DefaultPlayer.class);
					LOG.debug("adv get from cache {} ",cache.getAdventure().toWholeJson());
					LOG.debug("player entity data saved ");
				}
			}
		} catch (Exception e) {
			LOG.error("userId:{},source:{},Exception:{}",player!=null?player.getProperty().getUserId():"null", source,e);
			LOG.error("", e);
			
		}
	}

	@Override
	public void interpretCommand(Object command) throws InvalidCommandException {
		JsonNode jsonNode = (JsonNode) command;
		Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();
		if (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			commandToHandle.get(key).run(value);
		} else {
			commandToHandle.get(jsonNode.asText()).run(null);
		}
	}

	protected void addHandler(String command, JsonCommandHandler handler) {
		if (commandToHandle.get(command) != null) {
			LOG.error("存在command相同的处理，command=" + command);
		}
		commandToHandle.put(command, handler);
	}

	protected void addHandler(SubJsonCommandHandler handler) {
		for (JsonCommandHandler commandHandler : handler.getJsonCommandHandlers()) {
			commandToHandle.put(commandHandler.getCommand(), commandHandler);
		}
	}

	protected void removeHandler(String command) {
		commandToHandle.remove(command);
	}

}
