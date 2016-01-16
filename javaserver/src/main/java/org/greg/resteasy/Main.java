package org.greg.resteasy;

import io.nadron.example.SpringConfig;

import org.greg.resteasy.controller.HomeController;
import org.greg.resteasy.server.NettyHttpServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.Assert;

public class Main {

	public static void main(String[] args)
			throws Exception {

		AbstractApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfig.class);
		Assert.notNull(ac);
		Assert.notNull(ac.getBean(HomeController.class));

		NettyHttpServer netty = ac.getBean(NettyHttpServer.class);

		netty.start();

	}
}
