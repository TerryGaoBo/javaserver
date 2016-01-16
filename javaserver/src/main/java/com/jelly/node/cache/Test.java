package com.jelly.node.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class Test {
	public static void main(String[] args) {

		final CacheLoader<String, Integer> loader = new CacheLoader<String, Integer>() {
			public Integer load(String key) throws Exception {
				return 1;
			}
		};
		final RemovalListener<String, Integer> removalListener = new RemovalListener<String, Integer>() {
			public void onRemoval(RemovalNotification<String, Integer> removal) {
				Integer conn = removal.getValue();
				System.out.println(conn);
			}
		};

		LoadingCache<String, Integer> build = CacheBuilder.newBuilder()
//				.maximumSize(3)
				.refreshAfterWrite(1, TimeUnit.SECONDS)
				.removalListener(removalListener).build(loader);
		build.put("a", 10000000);
		build.put("b", 20000000);
		build.put("c", 30000000);
		build.put("d", 40000000);
		build.put("e", 50000000);

			try {
				Thread.sleep(2000);
				build.put("f", 600000);
				build.cleanUp();
				try {
//					System.out.println(build.get("a"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// build.invalidateAll();
		// build.invalidate("a");
	}
}
