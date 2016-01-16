package org.greg.resteasy.server;

import io.nadron.server.netty.AbstractNettyServer;
import io.nadron.server.netty.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

public class HttpNettyServer extends AbstractNettyServer{

	private static final Logger LOG = LoggerFactory
			.getLogger(HttpNettyServer.class);
	
	private ServerBootstrap serverBootstrap;
	
	private NettyJaxrsServer netty;
	 
	@Autowired
	ApplicationContext				ac;

	String							rootResourcePath	= "/resteasy";
	int								port				= 8082;
	
	public HttpNettyServer(NettyConfig nettyConfig,
			ChannelInitializer<? extends Channel> channelInitializer) {
		super(nettyConfig, channelInitializer);
	}

	@Override
	public void setChannelInitializer(
			ChannelInitializer<? extends Channel> initializer) {
		this.channelInitializer = initializer;
		serverBootstrap.childHandler(initializer);
		
	}

	@Override
	public TransmissionProtocol getTransmissionProtocol() {
		return TRANSMISSION_PROTOCOL.HTTP;
	}

	@Override
	public void startServer() throws Exception {
		try {
			serverBootstrap = new ServerBootstrap();
			Map<ChannelOption<?>, Object> channelOptions = nettyConfig.getChannelOptions();
			if(null != channelOptions){
				Set<ChannelOption<?>> keySet = channelOptions.keySet();
				for(@SuppressWarnings("rawtypes") ChannelOption option : keySet)
				{
					serverBootstrap.option(option, channelOptions.get(option));
				}
			}
			serverBootstrap.group(getBossGroup(),getWorkerGroup())
					.channel(NioServerSocketChannel.class)
					.childHandler(getChannelInitializer());
			Channel serverChannel = serverBootstrap.bind(nettyConfig.getSocketAddress()).sync()
					.channel();
			

			ResteasyDeployment dp = new ResteasyDeployment();

			Collection<Object> providers = ac.getBeansWithAnnotation(Provider.class).values();
			Collection<Object> controllers = ac.getBeansWithAnnotation(Controller.class).values();

			Assert.notEmpty(controllers);

			// extract providers
			if (providers != null) {
				dp.getProviders().addAll(providers);
			}
			// extract only controller annotated beans
			dp.getResources().addAll(controllers);
			
			netty = new NettyJaxrsServer();
//			netty.initBootstrap().setOption("reuseAddress", true);
			netty.setDeployment(dp);
			netty.setPort(port);
			netty.setRootResourcePath(rootResourcePath);
			netty.setSecurityDomain(null);
			netty.start();
			
			ALL_CHANNELS.add(serverChannel);
		} catch(Exception e) {
			LOG.error("HTTP Server start error {}, going to shut down", e);
			super.stopServer();
			throw e;
		}
		
	}

	@Override
	public String toString() {
		return "HttpNettyServer [socketAddress=" + nettyConfig.getSocketAddress()
				+ ", portNumber=" + nettyConfig.getPortNumber() + "]";
	}
}
