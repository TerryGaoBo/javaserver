package io.nadron.server.netty;

import io.nadron.context.AppContext;
import io.nadron.server.ServerManager;

import java.util.HashSet;
import java.util.Set;

import org.greg.resteasy.server.NettyHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerManagerImpl implements ServerManager
{
	private Set<AbstractNettyServer> servers;
	private static final Logger LOG = LoggerFactory.getLogger(ServerManagerImpl.class);
	
	public ServerManagerImpl()
	{
		servers = new HashSet<AbstractNettyServer>();
	}
	
	@Override
	public void startServers(int tcpPort, int flashPort, int udpPort) throws Exception
	{
		
		if(tcpPort > 0)
		{
			AbstractNettyServer tcpServer = (AbstractNettyServer)AppContext.getBean(AppContext.TCP_SERVER);
			tcpServer.startServer(tcpPort);
			servers.add(tcpServer);
		}
		
		if(flashPort > 0)
		{
			AbstractNettyServer flashServer = (AbstractNettyServer)AppContext.getBean(AppContext.FLASH_POLICY_SERVER);
			flashServer.startServer(flashPort);
			servers.add(flashServer);
		}
		
		if(udpPort > 0)
		{
			AbstractNettyServer udpServer = (AbstractNettyServer)AppContext.getBean(AppContext.UDP_SERVER);
			udpServer.startServer(udpPort);
			servers.add(udpServer);
		}
		
	}

	@Override
	public void startServers() throws Exception 
	{
		AbstractNettyServer tcpServer = (AbstractNettyServer)AppContext.getBean(AppContext.TCP_SERVER);
		tcpServer.startServer();
		servers.add(tcpServer);
		LOG.info("start tcp server :{}",tcpServer.toString());
//		AbstractNettyServer flashServer = (AbstractNettyServer)AppContext.getBean(AppContext.FLASH_POLICY_SERVER);
//		flashServer.startServer();
//		servers.add(flashServer);
//		AbstractNettyServer udpServer = (AbstractNettyServer)AppContext.getBean(AppContext.UDP_SERVER);
//		udpServer.startServer();
//		servers.add(udpServer);

		NettyHttpServer netty = (NettyHttpServer) AppContext.getBean(NettyHttpServer.class);

		netty.start();
		LOG.info(netty.toString());
	}
	
	@Override
	public void stopServers() throws Exception
	{
		for(AbstractNettyServer nettyServer: servers){
			try
			{
				nettyServer.stopServer();
			}
			catch (Exception e)
			{
				LOG.error("Unable to stop server {} due to error {}", nettyServer,e);
				throw e;
			}
		}
	}

}
