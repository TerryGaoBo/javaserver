package io.nadron.example;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.impl.GameRoomSession.GameRoomSessionBuilder;
import io.nadron.app.impl.SimpleGame;
import io.nadron.example.lostdecade.LDRoom;
import io.nadron.handlers.netty.TextWebsocketEncoder;
import io.nadron.protocols.Protocol;
import io.nadron.protocols.impl.NettyObjectProtocol;
import io.nadron.service.LookupService;
import io.nadron.service.impl.SimpleLookupService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.jelly.activity.WorldActivity;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.ObjectCacheService;

/**
 * This class contains the spring configuration for the nadron library user.
 * The only bean that is compulsory to be declared is lookupService bean.
 * Otherwise the program will terminate with a context load error from spring
 * framework. The other beans declared can also be created using **new**
 * operator as per programmer convenience.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Configuration
@ImportResource("classpath:/nadron/beans/server-beans.xml")
public class SpringConfig
{
	@Autowired
	@Qualifier("messageBufferProtocol")
	private Protocol messageBufferProtocol;

	@Autowired
	@Qualifier("webSocketProtocol")
	private Protocol webSocketProtocol;
	
	@Autowired
	@Qualifier("webSocketBinaryProtocol")
	private Protocol webSocketBinaryProtocol;

	@Autowired
	@Qualifier("textWebsocketEncoder")
	private TextWebsocketEncoder textWebsocketEncoder;

	@Autowired
	@Qualifier("nettyObjectProtocol")
	private NettyObjectProtocol nettyObjectProtocol;
	
	@Autowired
	@Qualifier("objectCacheService")
	private ObjectCacheService objectCacheService;
	
	@Autowired
	@Qualifier("allPlayersCache")
	private AllPlayersCache allPlayersCache;
	
	@Autowired
	@Qualifier("worldActivity")
	private WorldActivity worldActivity;
	
	
	public @Bean
	Game zombieGame()
	{
		Game game = new SimpleGame(1, "Zombie");
		return game;
	}


	public @Bean(name = "LDGame")
	Game ldGame()
	{
		return new SimpleGame(2, "LDGame");
	}

	public @Bean(name = "LDGameRoom", destroyMethod="shutdown")
	GameRoom ldGameRoom()
	{
		GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
		sessionBuilder.parentGame(ldGame()).gameRoomName("LDGameRoom")
				.protocol(webSocketProtocol);
		LDRoom room = new LDRoom(sessionBuilder,objectCacheService,allPlayersCache,worldActivity);
		return room;
	}
	public @Bean(name = "lookupService")
	LookupService lookupService()
	{
		Map<String, GameRoom> refKeyGameRoomMap = new HashMap<String, GameRoom>();
		refKeyGameRoomMap.put("LDGameRoom", ldGameRoom());
		LookupService service = new SimpleLookupService(refKeyGameRoomMap,objectCacheService);
		return service;
	}
}
