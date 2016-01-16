package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.event.Event;
import io.nadron.handlers.netty.BinaryWebsocketDecoder;
import io.nadron.handlers.netty.BinaryWebsocketEncoder;
import io.nadron.handlers.netty.DefaultToServerHandler;
import io.nadron.handlers.netty.LoginProtocol;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This protocol can be used for websocket clients which pass JSon objects as
 * text over the wire. The incoming text will be converted to {@link Event}
 * objects and sent to the {@link Session}. The outgoing messages will be
 * converted from Events to JSon string representation using Jackson library.
 * 
 * @author Abraham Menacherry
 * 
 */
public class WebSocketBinaryProtocol extends AbstractNettyProtocol
{

	private static final Logger LOG = LoggerFactory
			.getLogger(WebSocketBinaryProtocol.class);

	/**
	 * Used to decode incoming JSon string objects to {@link Event} objects.
	 */
	private BinaryWebsocketDecoder binaryWebsocketDecoder;
	/**
	 * Used to encode the outgoing Event objects to JSon string representation.
	 */
	private BinaryWebsocketEncoder binaryWebsocketEncoder;

	public WebSocketBinaryProtocol()
	{
		super("WEB_SOCKET_BINARY_PROTOCOL");
	}

	/**
	 * Specifically overriden so that the pipeline is not cleared. TODO check if
	 * simply adding wsencoder, wsdecoder in the chain will still maintain the
	 * connection.
	 */
	@Override
	public void applyProtocol(PlayerSession playerSession,
			boolean clearExistingProtocolHandlers)
	{
		applyProtocol(playerSession);
	}

	@Override
	public void applyProtocol(PlayerSession playerSession)
	{
		LOG.trace("Going to apply {} on session: {}", getProtocolName(),
				playerSession);

		ChannelPipeline pipeline = NettyUtils
				.getPipeLineOfConnection(playerSession);
		pipeline.addLast("binaryWebsocketDecoder", binaryWebsocketDecoder);
		pipeline.addLast("eventHandler", new DefaultToServerHandler(
				playerSession));

		pipeline.addLast("binaryWebsocketEncoder", binaryWebsocketEncoder);
		// Since the pipeline was not cleared for this protocol do some cleanup
		// manually.
		pipeline.remove(LoginProtocol.LOGIN_HANDLER_NAME);
		pipeline.remove(AbstractNettyProtocol.IDLE_STATE_CHECK_HANDLER);
	}

	public BinaryWebsocketDecoder getBinaryWebsocketDecoder() {
		return binaryWebsocketDecoder;
	}

	public void setBinaryWebsocketDecoder(BinaryWebsocketDecoder binaryWebsocketDecoder) {
		this.binaryWebsocketDecoder = binaryWebsocketDecoder;
	}

	public BinaryWebsocketEncoder getBinaryWebsocketEncoder() {
		return binaryWebsocketEncoder;
	}

	public void setBinaryWebsocketEncoder(BinaryWebsocketEncoder binaryWebsocketEncoder) {
		this.binaryWebsocketEncoder = binaryWebsocketEncoder;
	}


}
