package io.nadron.example;

import io.nadron.example.lostdecade.LDRoom;
import io.nadron.server.ServerManager;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.config.AllGameConfig;
import com.jelly.activity.WorldActivity;
import com.jelly.node.cache.AllPlayersCache;
import com.jelly.node.cache.UnionCacheService;
import com.jelly.rank.GameRankMaster;
import com.jelly.team.TeamManager;

public class GameServer {
	private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure(System
				.getProperty("log4j.configuration"));
		AllGameConfig.getInstance();
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
				SpringConfig.class);
		// For the destroy method to work.
		ctx.registerShutdownHook();
		// serverManager.startServers(18090,843,8081);
		GameRankMaster.getInstance();
		LOG.info("Started servers");
		startGames(ctx);
		
		// Start the main game server
		ServerManager serverManager = ctx.getBean(ServerManager.class);
		
		try {
			serverManager.startServers();
			LOG.info("RuntimeEnv={}", AllGameConfig.getInstance().env.getName());
			if (AllGameConfig.getInstance().env == RuntimeEnv.TEST
					|| AllGameConfig.getInstance().env == RuntimeEnv.STAGE
					|| AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
//				if (ContextConfig.getFirstServerId() < 990) {
//					TaskManagerService taskManager = ctx
//							.getBean(TaskManagerService.class);
//					taskManager.scheduleAtFixedRate(new HdLogThread(), 1000,
//							1000, TimeUnit.SECONDS);
//					taskManager.scheduleAtFixedRate(new HdOnlineTask(), 1, 1,
//							TimeUnit.MINUTES);
//				}
			}

		} catch (Exception e) {
			LOG.error("Unable to start servers cleanly: {}", e);
		}
	}

	public static void startGames(AbstractApplicationContext ctx) {
		AllPlayersCache bean = ctx.getBean(AllPlayersCache.class);
		bean.init();
		UnionCacheService bean1 = ctx.getBean(UnionCacheService.class);
		bean1.init();
		LDRoom bean2 = ctx.getBean(LDRoom.class);
		bean2.init();
		WorldActivity bean3 = ctx.getBean(WorldActivity.class);
		bean3.resetActivitys();

		TeamManager.getSingleton().init();
		
		// World world = ctx.getBean(World.class);
		// GameRoom room1 = (GameRoom)ctx.getBean("Zombie_ROOM_1");
		// GameRoom room2 = (GameRoom)ctx.getBean("Zombie_ROOM_2");
		// Task monitor1 = new WorldMonitor(world,room1);
		// Task monitor2 = new WorldMonitor(world,room2);
		// TaskManagerService taskManager =
		// ctx.getBean(TaskManagerService.class);
		// ObjectCacheService objService =
		// ctx.getBean(ObjectCacheService.class);
		// taskManager.scheduleWithFixedDelay(monitor1, 1000, 5000,
		// TimeUnit.MILLISECONDS);
		// // taskManager.scheduleWithFixedDelay(monitor2, 2000, 5000,
		// TimeUnit.MILLISECONDS);

		// taskManager.scheduleWithFixedDelay(objService.getClearUpExpireCacheTask(),
		// 1, 5, TimeUnit.MINUTES);
	}

}
