package io.nadron;

import io.nadron.app.GameRoom;
import io.nadron.service.LookupService;
import io.nadron.service.impl.SimpleLookupService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.jelly.node.cache.ObjectCacheService;

@Configuration
@ImportResource("classpath:/nadron/beans/server-beans.xml")
public class NadronSpringConfig
{

	public @Bean(name="lookupService") LookupService lookupService()
	{
		Map<String,GameRoom> refKeyGameRoomMap = new HashMap<String, GameRoom>();
		
		ObjectCacheService objectCache = new ObjectCacheService();
		LookupService service = new SimpleLookupService(refKeyGameRoomMap,objectCache);
		return service;
	}
}
