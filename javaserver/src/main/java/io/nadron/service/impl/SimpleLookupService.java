package io.nadron.service.impl;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.Player;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.service.LookupService;

import java.util.HashMap;
import java.util.Map;

import com.dol.cdf.log.LogUtil;
import com.jelly.node.cache.ObjectCacheService;
import com.jelly.player.PlayerProperty;



/**
 * The lookup service abstracts away the implementation detail on getting the
 * game objects from the reference key provided by the client. This lookup is
 * now done from a hashmap but can be done from database or any other manner.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SimpleLookupService implements LookupService
{
	private final Map<String, GameRoom> refKeyGameRoomMap;
	
	private ObjectCacheService objectCache;

	public SimpleLookupService()
	{
		refKeyGameRoomMap = new HashMap<String, GameRoom>();
	}
	
	public SimpleLookupService(Map<String, GameRoom> refKeyGameRoomMap, ObjectCacheService objectCache)
	{
		super();
		this.refKeyGameRoomMap = refKeyGameRoomMap;
		this.objectCache = objectCache;
	}

	@Override
	public Game gameLookup(Object gameContextKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameRoom gameRoomLookup(Object gameContextKey)
	{
		return refKeyGameRoomMap.get(gameContextKey);
	}

	@Override
	public Player playerLookup(Player player)
	{
		DefaultPlayer object = objectCache.getObject((DefaultPlayer)player, DefaultPlayer.class);
		if (!object.getProperty().containStatus(PlayerProperty.REGISTER)) {
			object.getProperty().addStatus(PlayerProperty.REGISTER);
			LogUtil.doRegisterLog(object, player.getClientInfo());
		}
		
		if(object != null) {
			object.setObjectCache(objectCache);
		}
		return object;
	}
	

	public Map<String, GameRoom> getRefKeyGameRoomMap()
	{
		return refKeyGameRoomMap;
	}


}
