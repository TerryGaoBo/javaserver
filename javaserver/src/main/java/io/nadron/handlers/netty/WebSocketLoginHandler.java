package io.nadron.handlers.netty;

import io.nadron.app.GameRoom;
import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.DefaultPlayer;
import io.nadron.communication.NettyTCPMessageSender;
import io.nadron.context.AppContext;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.event.impl.DefaultEvent;
import io.nadron.event.impl.ReconnetEvent;
import io.nadron.example.lostdecade.LDRoom;
import io.nadron.service.LookupService;
import io.nadron.service.UniqueIDGeneratorService;
import io.nadron.service.impl.ReconnectSessionRegistry;
import io.nadron.util.Credentials;
import io.nadron.util.NadronConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.entities.GlobalProps;
import com.dol.cdf.log.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jelly.node.cache.AllPlayersCache;

/**
 * This login handler will parse incoming login events to get the
 * {@link Credentials} and lookup {@link Player} and {@link GameRoom} objects.
 * It kicks of the session creation process and will then send the
 * {@link Events#START} event object to websocket client.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class WebSocketLoginHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	private static final Logger LOG = LoggerFactory.getLogger(WebSocketLoginHandler.class);

	private LookupService lookupService;
	protected ReconnectSessionRegistry reconnectRegistry;
	protected UniqueIDGeneratorService idGeneratorService;

	private ObjectMapper jackson;

	private AllPlayersCache allPlayersCache;
	
	private GlobalProps globalProps;
	
	/**
	 * 在游戏关闭的时候也可以登录的玩家
	 */
	private String gmUserIds = "";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
		Channel channel = ctx.channel();
		String data = frame.text();
		LOG.trace("From websocket: " + data);
		Event event = jackson.readValue(data, DefaultEvent.class);
		int type = event.getType();
		if (Events.LOG_IN == type) {
			String remoteAddres = StringUtils.substringBetween(channel.remoteAddress().toString(), "/", ":");
			LOG.trace("Login attempt from {}", remoteAddres);
			List<String> credList = null;
			credList = (List) event.getSource();
			String userId = credList.get(0);
			if(ContextConfig.isOfficalEnv()) {
				if (!ContextConfig.GAME_OPEN && !ContextConfig.checkIp(remoteAddres)) {
					LOG.error("user: {} try login but game close，ip={}",userId,remoteAddres);
					denyPlayer(DENY_TYPE_GAME_CLOSE, channel);
					return;
				}
				String version = credList.get(3);
				if(ContextConfig.VERSION_CHECK && !AllGameConfig.getInstance().maxVersion.equals(version)) {
					denyPlayer(DENY_TYPE_VERSION_DIFF,channel);
					LOG.error("version diff ,server version :{}, client version:{}",AllGameConfig.getInstance().maxVersion,version);
					return;
				}
			}
			
			if (userId.isEmpty()) {
				LOG.error("Invalid userId, event {} sent from remote address {}. " + "Going to close channel {}", new Object[] { event.getType(), channel.remoteAddress(), channel });
				closeChannelWithLoginFailure(channel);
				return;
			}
			String net = credList.get(1);
			String ch = credList.get(4);//渠道
			String clientInfo = credList.get(5);
//			if (credList.size() >= 5) {//兼容老数据
//				ch = credList.get(4);
//				LOG.info("player from channel {}", ch);
//			}
			String playerGuid = DefaultPlayer.genPlayerGuid(userId,ch, net);
			disconnectOnlineMe(playerGuid);
			Player player = lookupPlayer(playerGuid,userId,ch,net, clientInfo);
			if(getGlobalProps().isBanPlayer(playerGuid)) {
				LOG.error("ban player try login server. name={},guid={}",player.getName(),playerGuid);
				closeChannelWithLoginFailure(channel);
				return;
			}
			
//			player.getProperty().setLevel(60);
//			player.getProperty().addGold(100000);
//			player.getProperty().addCoin(100000);
//			player.getProperty().changeMoney(null, -(player.getProperty().getCoin() - 60));
//			player.getProperty().setVipLv(3);
//			player.getInvenotry().addItem(5995, 5, 0, player);
//			player.getInvenotry().addItem(5996, 5, 0, player);
//			player.getInvenotry().addItem(5997, 2, 0, player);
//			player.getInvenotry().addItem(5998, 3, 0, player);
//			player.getInvenotry().addItem(5999, 3, 0, player);
//			player.getInvenotry().addItem(5093, 20, 0, player);
//			player.getInvenotry().addItem(4125, 10, 0, player);
//			player.getInvenotry().addItem(4087, 1, 0, player);
			
			handleLogin(player, channel);
			handleGameRoomJoin(player, channel, credList.get(2));
			LogUtil.doLoginLog((DefaultPlayer)player,remoteAddres);
			
		} else if (type == Events.RECONNECT) {
			LOG.debug("Reconnect attempt from {}", channel.remoteAddress());
			if (!ContextConfig.GAME_OPEN) {
				closeChannelWithLoginFailure(channel);
				return;
			}
			if (event.getSource() == null) {
				closeChannelWithLoginFailure(channel);
				return;
			}
			PlayerSession playerSession = lookupSession((String) event.getSource());
			handleReconnect(playerSession, channel);
		} else {
			LOG.error("Invalid event {} sent from remote address {}. " + "Going to close channel {}", new Object[] { event.getType(), channel.remoteAddress(), channel });
			closeChannelWithLoginFailure(channel);
		}
	}

	private void disconnectOnlineMe(String guid) {
		
		// reconnectId change to userId by zhl 2014-5-19
		if (guid != null && guid.isEmpty() == false) {
			String userKey = reconnectRegistry.getUserKey(guid);
			if (userKey != null) {
				Session removedSession = reconnectRegistry.removeSession(userKey);
				if (removedSession != null) {
					reconnectRegistry.removeUser(guid);
					LOG.info("remove old session userKey:{},userId:{}", userKey, guid);
					removedSession.close();
				}
			};
		}
	}

	public PlayerSession lookupSession(final String reconnectKey) {
		PlayerSession playerSession = (PlayerSession) reconnectRegistry.getSession(reconnectKey);
		if (null != playerSession) {
			synchronized (playerSession) {
				// if its an already active session then do not allow a
				// reconnect. So the only state in which a client is allowed to
				// reconnect is if it is "NOT_CONNECTED"
				if (playerSession.getStatus() == Session.Status.NOT_CONNECTED) {
					playerSession.setStatus(Session.Status.CONNECTING);
				} else {
					playerSession = null;
				}
				LOG.info("player session status == {}", playerSession == null ? "null " : playerSession.getStatus());
			}
		}
		return playerSession;
	}

	protected void handleReconnect(PlayerSession playerSession, Channel channel) throws Exception {
		if (null != playerSession) {
			
			disconnectOnlineMe(playerSession.getPlayer().getId());
			
			channel.writeAndFlush(eventToFrame(Events.LOG_IN_SUCCESS, null));
			GameRoom gameRoom = playerSession.getGameRoom();
			String guid = playerSession.getPlayer().getId();
			PlayerSession playerSession2 = gameRoom.getSessions().get(guid);
			if (playerSession2 != null) {
				LOG.info("playerSession2 is replace by reconnect");
				playerSession2.close();
			}
			gameRoom.disconnectSession(playerSession);
			if (null != playerSession.getTcpSender())
				playerSession.getTcpSender().close();

			handleReJoin(playerSession, gameRoom, channel);
		} else {
			// Write future and close channel
			closeChannelWithLoginFailure(channel);
		}
	}

	protected void handleReJoin(PlayerSession playerSession, GameRoom gameRoom, Channel channel) throws Exception {
		// Set the tcp channel on the session.
		NettyTCPMessageSender sender = new NettyTCPMessageSender(channel);
		playerSession.setTcpSender(sender);
		// Connect the pipeline to the game room.
		gameRoom.connectSession(playerSession);
		channel.writeAndFlush(eventToFrame(Events.GAME_ROOM_JOIN_SUCCESS, null));// assumes
																					// that
																					// the
																					// protocol
																					// applied
																					// will
																					// take
																					// care
																					// of
																					// event
																					// objects.
		playerSession.setWriteable(true);// TODO remove if unnecessary. It
											// should be done in start event
		// Send the re-connect event so that it will in turn send the START
		// event.
		playerSession.onEvent(new ReconnetEvent(sender));
		// add by zhl 2014-5-19
		sender.sendMessage(Events.event(null, Events.START));
	}

	public Player lookupPlayer(String playerGuid,String username, String ch, String net, String clientInfo) throws Exception {
		Player player = null;
		player = new DefaultPlayer(playerGuid,username,ch,net);
		player.setClientInfo(clientInfo);
		player = lookupService.playerLookup(player);
		if (null == player) {
			LOG.error("Invalid credentials provided by user: {}", player);
			return null;
		}
		player.getProperty().setOnline();
		((DefaultPlayer) player).initPlayer(allPlayersCache);
		return player;
	}

	public GlobalProps getGlobalProps() {
		if(globalProps == null) {
			globalProps = AppContext.getBean(LDRoom.class).getGlobalProps();
		}
		return globalProps;
	}
	
	public void handleLogin(Player player, Channel channel) throws Exception {
		if (null != player) {
			channel.writeAndFlush(eventToFrame(Events.LOG_IN_SUCCESS, null));
		} else {
			// Write future and close channel
			closeChannelWithLoginFailure(channel);
		}
	}

	protected void closeChannelWithLoginFailure(Channel channel) throws Exception {
		// Close the connection as soon as the error message is sent.
		channel.writeAndFlush(eventToFrame(Events.LOG_IN_FAILURE, null)).addListener(ChannelFutureListener.CLOSE);
	}

	public void handleGameRoomJoin(Player player, Channel channel, String refKey) throws Exception {
		GameRoom gameRoom = lookupService.gameRoomLookup(refKey);
		if (null != gameRoom) {
			String guid = player.getId();
			PlayerSession playerSession2 = gameRoom.getSessions().get(guid);
			if (playerSession2 != null) {
				LOG.info("playerSession2 is replace");
				playerSession2.close();
			}
			PlayerSession playerSession = gameRoom.createPlayerSession(player);
			String reconnectKey = (String) idGeneratorService.generateFor(playerSession.getClass());
			playerSession.setAttribute(NadronConfig.RECONNECT_KEY, reconnectKey);
			playerSession.setAttribute(NadronConfig.RECONNECT_ID, guid);
			playerSession.setAttribute(NadronConfig.RECONNECT_REGISTRY, reconnectRegistry);
			LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {}, RECONNECT_KEY {}, RECONNECT_ID player guid {}", channel, reconnectKey, guid);
			ChannelFuture future = channel.writeAndFlush(eventToFrame(Events.GAME_ROOM_JOIN_SUCCESS, reconnectKey));
			connectToGameRoom(gameRoom, playerSession, future);
		} else {
			// Write failure and close channel.
			ChannelFuture future = channel.writeAndFlush(eventToFrame(Events.GAME_ROOM_JOIN_FAILURE, null));
			future.addListener(ChannelFutureListener.CLOSE);
			LOG.error("Invalid ref key provided by client: {}. Channel {} will be closed", refKey, channel);
		}
	}

	public void connectToGameRoom(final GameRoom gameRoom, final PlayerSession playerSession, ChannelFuture future) {
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				Channel channel = future.channel();
				LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {} completed", channel);
				if (future.isSuccess()) {
					// Set the tcp channel on the session.
					NettyTCPMessageSender tcpSender = new NettyTCPMessageSender(channel);
					playerSession.setTcpSender(tcpSender);
					// Connect the pipeline to the game room.
					gameRoom.connectSession(playerSession);
					// send the start event to remote client.
					tcpSender.sendMessage(Events.event(null, Events.START));
					gameRoom.onLogin(playerSession);
				} else {
					LOG.error("Sending GAME_ROOM_JOIN_SUCCESS message to client was failure, channel will be closed");
					channel.close();
				}
			}
		});
	}

	protected TextWebSocketFrame eventToFrame(byte opcode, Object payload) throws Exception {
		Event event = Events.event(payload, opcode);
		return new TextWebSocketFrame(jackson.writeValueAsString(event));
	}
	
	
	private static final int DENY_TYPE_VERSION_DIFF = 0;
	private static final int DENY_TYPE_GAME_CLOSE = 1;
	
	protected void denyPlayer(Integer type, Channel channel) throws Exception {
		Event event = Events.event(type,Events.GAME_OVER);
		TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(jackson.writeValueAsString(event));
		channel.writeAndFlush(textWebSocketFrame).addListener(ChannelFutureListener.CLOSE);
	}
	
	

	public LookupService getLookupService() {
		return lookupService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public ReconnectSessionRegistry getReconnectRegistry() {
		return reconnectRegistry;
	}

	public void setReconnectRegistry(ReconnectSessionRegistry reconnectRegistry) {
		this.reconnectRegistry = reconnectRegistry;
	}

	public UniqueIDGeneratorService getIdGeneratorService() {
		return idGeneratorService;
	}

	public void setIdGeneratorService(UniqueIDGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public ObjectMapper getJackson() {
		return jackson;
	}

	public void setJackson(ObjectMapper jackson) {
		this.jackson = jackson;
	}

	public String getGmUserIds() {
		return gmUserIds;
	}
	
	public void setGmUserIds(String gmUserIds) {
		this.gmUserIds = gmUserIds;
	}
	
	public AllPlayersCache getAllPlayersCache() {
		return allPlayersCache;
	}

	public void setAllPlayersCache(AllPlayersCache allPlayersCache) {
		this.allPlayersCache = allPlayersCache;
	}

}
