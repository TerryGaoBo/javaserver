package com.jelly.node.cache;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.jelly.node.datastore.MySqlDataStore;

public class DelaySaveObjCacheService {
	
	MySqlDataStore dataStore;

	ObjectMapper jackson;
	
	LoadingCache<String, byte[]> caches = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).removalListener(new RemovalListener<String, byte[]>(){
        @Override
        public void onRemoval(RemovalNotification<String, byte[]> rn) {
            System.out.println(rn.getKey()+"被移除");  
            
        }}).recordStats().build(new CacheLoader<String, byte[]>() {
		@Override
		public byte[] load(String key) throws Exception {
			byte[] bytes = dataStore.get(key);
			return bytes == null ? ArrayUtils.EMPTY_BYTE_ARRAY : bytes;
		}
	});
	
	
}
