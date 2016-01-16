package test.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloNettyHttpTest {

	private static final Logger LOG = LoggerFactory.getLogger(HelloNettyHttpTest.class);
	
	public void start(int port) throws Exception{
		
		System.out.println("http listener port : "+port);
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workderGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workderGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                    ch.pipeline().addLast(new HttpResponseEncoder());
                    // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                    ch.pipeline().addLast(new HttpRequestDecoder());
                    ch.pipeline().addLast(new HttpServerInboundHandler());
				}
			}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		}finally{
			workderGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
		}
		LOG.info("http listener port : "+port);
	}
	
	public static void main(String[] args) throws Exception {
		String config = System.getProperty("log4j.configuration");
		PropertyConfigurator.configure(config);
		HelloNettyHttpTest server = new HelloNettyHttpTest();
        server.start(8844);
	}

}
