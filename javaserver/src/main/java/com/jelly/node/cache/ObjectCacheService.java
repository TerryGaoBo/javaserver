package com.jelly.node.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.dol.cdf.common.entities.Entity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.jelly.node.datastore.MySqlDataStore;

public class ObjectCacheService {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectCacheService.class);

	MySqlDataStore dataStore;

	ObjectMapper jackson;

	LoadingCache<String, byte[]> caches = CacheBuilder.newBuilder().expireAfterAccess(6, TimeUnit.HOURS).recordStats().build(new CacheLoader<String, byte[]>() {
		@Override
		public byte[] load(String key) throws Exception {
			byte[] bytes = dataStore.get(key);
			return bytes == null ? ArrayUtils.EMPTY_BYTE_ARRAY : bytes;
		}
	});

	/**
	 * 从object cache中获取实体对象，如果缓存里没有则向数据库里请求
	 * 
	 * @param guid
	 * @param z
	 * @return 如果返回为null，则表示没有这条数据
	 */
	public <T extends Entity> T getObject(final T entity, Class<T> z) {
		try {
			byte[] bytes = caches.get(entity.guid, new Callable<byte[]>() {
				@Override
				public byte[] call() throws Exception {
					byte[] bs = dataStore.get(entity.guid);
					if (bs == null) {
						bs = jackson.writeValueAsBytes(entity);
						dataStore.create(entity.guid, bs);
					} else {
						if (LOG.isDebugEnabled()) {
							//LOG.debug("从数据库里取{}", StringUtils.toAsciiString(bs));
						}
					}
					return bs;
				}
			});
			if (LOG.isDebugEnabled()) {
				//LOG.debug("取出来的数据 {}", StringUtils.toAsciiString(bytes));
			}
			return jackson.readValue(bytes, z);
		} catch (Exception e) {
			LOG.error("{}", e);
		}
		return null;

	}

	/**
	 * 直接从cache里面去，取不到直接返回空
	 * 
	 * @param guid
	 * @param z
	 * @return
	 */
	public <T extends Entity> T getCache(String guid, Class<T> z) {
		try {
			byte[] bytes = caches.getIfPresent(guid);
			if (bytes == null) {
				bytes = dataStore.get(guid);
				if (bytes != null) {
					return jackson.readValue(bytes, z);
				}
			}
			return jackson.readValue(bytes, z);
		} catch (Exception e) {
			LOG.error("{}", e);
		}
		return null;

	}

	public String getByteString(String guid) {
		byte[] bytes = caches.getUnchecked(guid);
		try {
			return jackson.writeValueAsString(bytes);
		} catch (Exception e) {
			LOG.error("{}", e);
		}
		return null;
	}

	/**
	 * 向Object cache中放入实体对象
	 * 
	 * @param e
	 * @param isCreate
	 */
	public <T extends Entity> void putObject(T e) {
		try {
			byte[] byptes = jackson.writeValueAsBytes(e);
			caches.put(e.guid, byptes);
			dataStore.put(e.guid, byptes);
		} catch (Exception e1) {
			LOG.error("{}", e1);
		}
	}

	//半个小时清理一次
	@Scheduled(fixedDelay = 1800000)
	public void clearUpExpireCache() {
		LOG.info("ClearUp Expire Cache Task is running");
		caches.cleanUp();
		CacheStats stats = caches.stats();
		LOG.info("hit rate:{},miss rate:{},load Exception Rate:{},avg load Penalty:{}", stats.hitRate(), stats.missRate(), stats.loadExceptionRate(), stats.averageLoadPenalty());
		CacheStats delta = caches.stats().minus(stats);
		LOG.info("hitCount:{},missCount:{},loadSuccessCount:{},loadExceptionCount:{},totalLoadTime:{}", delta.hitCount(), delta.missCount(), delta.loadSuccessCount(), delta.loadExceptionCount(), delta.totalLoadTime());

	}

	// public synchronized ClearUpExpireCacheTask getClearUpExpireCacheTask(){
	// if(clearExpireTask == null){
	// clearExpireTask = new ClearUpExpireCacheTask(caches);
	// }
	// return clearExpireTask;
	// }
	//
	// protected static class ClearUpExpireCacheTask implements Task
	// {
	//
	// LoadingCache<String, byte[]> caches;
	//
	// public ClearUpExpireCacheTask(LoadingCache<String, byte[]> caches) {
	// this.caches = caches;
	// }
	//
	// @Override
	// public void run() {
	// LOG.debug("ClearUp Expire Cache Task is running");
	// caches.cleanUp();
	// }
	//
	// @Override
	// public Object getId() {
	// return null;
	// }
	//
	// @Override
	// public void setId(Object id) {
	//
	// }
	//
	// }

	public MySqlDataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(MySqlDataStore dataStore) {
		this.dataStore = dataStore;
	}

	public ObjectMapper getJackson() {
		return jackson;
	}

	public void setJackson(ObjectMapper jackson) {
		this.jackson = jackson;
	}

}
